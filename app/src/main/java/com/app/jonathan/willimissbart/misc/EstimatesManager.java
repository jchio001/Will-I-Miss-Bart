package com.app.jonathan.willimissbart.misc;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.app.jonathan.willimissbart.api.Models.Etd.Estimate;
import com.app.jonathan.willimissbart.api.Models.Etd.EtdRespWrapper;
import com.app.jonathan.willimissbart.api.RetrofitClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Note: the onError() method in an observer should never be called. If it is, something is messed
 * up.
 *
 * EstimatesManager should probably be broken up into 3 components:
 * - A feed manager
 * - A estimates client
 * - A estimates manager that the above 2 items pool their results in
 */
public class EstimatesManager {

    public interface EstimateConsumer {
        void onPendingEstimates();
        void consumeEstimates(List<Estimate> estimates);
    }

    public enum EstimatesEvent {
        UPDATE,
    }

    private static EstimatesManager instance = null;

    protected Map<String, List<Estimate>> origDestToEstimates = NotGuava.newHashMap();

    protected BehaviorSubject<EstimatesEvent> estimatesSubject = BehaviorSubject.create();

    protected CompositeDisposable compositeDisposable = new CompositeDisposable();

    private RetrofitClient retrofitClient = RetrofitClient.get();

    public synchronized static EstimatesManager get() {
        if (instance == null) {
            synchronized (EstimatesManager.class) {
                if (instance == null) {
                    instance = new EstimatesManager();
                }
            }
        }

        return instance;
    }

    public void subscribe(Observer<EstimatesEvent> estimatesEventObserver) {
        estimatesSubject
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(estimatesEventObserver);
    }

    public Map<String, List<Estimate>> getOrigDestToEstimates() {
      return origDestToEstimates;
    }

    /**
     * Makes a request to fetch estimates specific to a origin station. The consumer is wrapped in
     * {@link DisposableConsumer} to deal with race conditions.
     * @param routeBundle
     * @param disposableConsumer
     */
    public void requestEstimates(@NonNull RouteBundle routeBundle,
                                 @NonNull DisposableConsumer disposableConsumer) {
        if (consumeEstimatesIfPresent(routeBundle, disposableConsumer, origDestToEstimates)) {
            return;
        }

        disposableConsumer.onPendingEstimates();
        retrofitClient
            .getRealTimeEstimates(routeBundle.getOrigin(), routeBundle.getTrainHeadStations())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new SingleObserver<EtdRespWrapper>() {
                @Override
                public void onSubscribe(Disposable d) {
                    compositeDisposable.add(d);
                }

                @Override
                public void onSuccess(EtdRespWrapper etdRespWrapper) {
                    synchronized (EstimatesManager.class) {
                        origDestToEstimates.putAll(etdRespWrapper.getOrigDestToEstimates());
                    }

                    consumeEstimatesIfPresent(routeBundle, disposableConsumer,
                        etdRespWrapper.getOrigDestToEstimates());
                }

                @Override
                public void onError(Throwable e) {
                    Log.w("EstimatesManager",
                        String.format("Failed to get estimates for %s", routeBundle.getOrigin()));
                }
            });
    }

    private static boolean consumeEstimatesIfPresent(
        @NonNull RouteBundle routeBundle,
        @NonNull DisposableConsumer disposableConsumer,
        @NonNull Map<String, List<Estimate>> origDestToEstimates) {
        String origin = routeBundle.getOrigin();
        for (String trainHeadStation : routeBundle.getTrainHeadStations()) {
            String originDest = origin + trainHeadStation;
            if (origDestToEstimates.containsKey(originDest)) {
                disposableConsumer.consumeEstimates(origDestToEstimates.get(originDest));
                return true;
            }
        }

        return false;
    }

    public void invalidate() {
        compositeDisposable.dispose();
        compositeDisposable = new CompositeDisposable();
        origDestToEstimates.clear();
    }

    public void populateWithFeedEstimates(@NonNull RouteBundle routeBundle,
                                          @Nullable RouteBundle returnRouteBundle) {
        invalidate();

        SingleObserver<EtdRespWrapper> etdObserver =
            new SingleObserver<EtdRespWrapper>() {
                @Override
                public void onSubscribe(Disposable d) {
                    compositeDisposable.add(d);
                }

                @Override
                public void onSuccess(EtdRespWrapper etdRespWrapper) {
                    synchronized (EstimatesManager.class) {
                        boolean wasEmpty = origDestToEstimates.isEmpty();

                        origDestToEstimates.putAll(etdRespWrapper.getOrigDestToEstimates());

                        if (wasEmpty) {
                            beginMinutelyUpdateJob();
                        }
                    }

                    estimatesSubject.onNext(EstimatesEvent.UPDATE);
                }

                @Override
                public void onError(Throwable e) {
                    Log.w("EstimatesManager",
                        String.format("Failed to get estimates for %s", routeBundle.getOrigin()));
                }
            };

        retrofitClient
            .getRealTimeEstimates(routeBundle.getOrigin(), routeBundle.getTrainHeadStations())
            .subscribe(etdObserver);

        if (returnRouteBundle != null) {
            retrofitClient
                .getRealTimeEstimates(returnRouteBundle.getOrigin(),
                    returnRouteBundle.getTrainHeadStations())
                .subscribe(etdObserver);
        }
    }

    // There's still a lot of work that needs to be done on this to resolve the ace conditions!
    // TODO: Handle the app being paused/killed by the OS
    protected void beginMinutelyUpdateJob() {
        Observable.interval(1, TimeUnit.MINUTES)
            .subscribeOn(Schedulers.io())
            .subscribe(new Observer<Long>() {
                @Override
                public void onSubscribe(Disposable d) {
                    compositeDisposable.add(d);
                }

                @Override
                public void onNext(Long interval) {
                    HashMap<String, List<Estimate>> updatedEstimatesMap = new HashMap<>();


                    for (Map.Entry<String, List<Estimate>> entry : origDestToEstimates.entrySet()) {
                        List<Estimate> estimates = entry.getValue();
                        ArrayList<Estimate> updatedEstimates = new ArrayList<>(estimates.size());
                        for (Estimate estimate : estimates) {
                            String minutes = estimate.getMinutes();

                            if (!minutes.equals("0") && !minutes.equals("Leaving")) {
                                String updatedMinutes =
                                    String.valueOf(Integer.valueOf(minutes) - 1);
                                updatedEstimates.add(estimate.updateMinutes(updatedMinutes));
                            }
                        }

                        if (!updatedEstimates.isEmpty()) {
                            updatedEstimatesMap.put(entry.getKey(), updatedEstimates);
                        }
                    }

                    Log.i("EstimatesManager", "Updated estimates!");
                    synchronized (EstimatesManager.class) {
                        origDestToEstimates.clear();
                        origDestToEstimates.putAll(updatedEstimatesMap);
                    }

                    estimatesSubject.onNext(EstimatesEvent.UPDATE);
                }

                @Override
                public void onError(Throwable e) {
                    Log.e("EstimatesManager", "rip");
                }

                @Override
                public void onComplete() {
                }
            });
    }
 }
