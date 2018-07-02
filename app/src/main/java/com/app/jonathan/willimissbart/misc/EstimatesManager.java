package com.app.jonathan.willimissbart.misc;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.app.jonathan.willimissbart.api.Models.Etd.Estimate;
import com.app.jonathan.willimissbart.api.Models.Etd.EtdResp;
import com.app.jonathan.willimissbart.api.Models.Etd.EtdRespWrapper;
import com.app.jonathan.willimissbart.api.RetrofitClient;
import com.app.jonathan.willimissbart.fragment.RoutesFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

// Singleton class for different components of the app to pull estimates from
public class EstimatesManager {

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

    public void persistThenPost(EtdRespWrapper etdRespWrapper) {
        origDestToEstimates.putAll(etdRespWrapper.getOrigDestToEstimates());
        estimatesSubject.onNext(EstimatesEvent.UPDATE);
    }

    public Map<String, List<Estimate>> getEstimates() {
      return origDestToEstimates;
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
                    origDestToEstimates.putAll(etdRespWrapper.getOrigDestToEstimates());
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

    public synchronized void beginMinutelyUpdateJob() {
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
                        ArrayList<Estimate> updatedEstimates = new ArrayList<>(estimates);
                        for (Estimate estimate : updatedEstimates) {
                            String minutes = estimate.getMinutes();

                            if (!minutes.equals("0") && !minutes.equals("Leaving")) {
                                String updatedMinutes =
                                    String.valueOf(Integer.valueOf(minutes) - 1);
                                estimate.setMinutes(updatedMinutes);
                                updatedEstimates.add(estimate);
                            }
                        }

                        if (!updatedEstimates.isEmpty()) {
                            updatedEstimatesMap.put(entry.getKey(), updatedEstimates);
                        }
                    }

                    Log.i("EstimatesManager", "Updated estimates!");
                    origDestToEstimates.clear();
                    origDestToEstimates.putAll(updatedEstimatesMap);
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
