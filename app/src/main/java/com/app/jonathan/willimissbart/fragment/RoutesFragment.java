package com.app.jonathan.willimissbart.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.adapter.TripsAdapter;
import com.app.jonathan.willimissbart.api.Models.Routes.Trip;
import com.app.jonathan.willimissbart.api.RetrofitClient;
import com.app.jonathan.willimissbart.misc.EstimatesManager;
import com.app.jonathan.willimissbart.misc.EstimatesManager.EstimatesEvent;
import com.app.jonathan.willimissbart.misc.NotGuava;
import com.app.jonathan.willimissbart.misc.RouteBundle;
import com.app.jonathan.willimissbart.misc.Utils;
import com.app.jonathan.willimissbart.persistence.SPManager;
import com.app.jonathan.willimissbart.persistence.StationsManager;
import com.app.jonathan.willimissbart.persistence.models.UserStationData;
import com.app.jonathan.willimissbart.viewholder.UserRouteFooter;

import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class RoutesFragment extends Fragment {

    @Bind(R.id.route_recycler) RecyclerView recyclerView;
    @Bind(R.id.progress_bar) ProgressBar progressBar;
    @Bind(R.id.user_route_footer) UserRouteFooter userRouteFooter;
    @Bind(R.id.failure_text) TextView failureText;
    
    private TripsAdapter adapter;

    private UserDataManager userDataManager;

    protected EstimatesManager estimatesManager = EstimatesManager.get();

    protected String routeFirstLegHead = null;
    protected String returnFirstLegHead = null;

    protected CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final SingleObserver<List<Trip>> tripSubscriber = new SingleObserver<List<Trip>>() {
        @Override
        public void onSubscribe(Disposable d) {
            compositeDisposable.add(d);
        }

        @Override
        public void onSuccess(List<Trip> mergedTrips) {
            loadUserRoutes(mergedTrips);
        }

        @Override
        public void onError(Throwable e) {
            Toast.makeText(RoutesFragment.this.getContext(),
                String.format("Trips Wah wah %s", e.getMessage()), Toast.LENGTH_SHORT)
                .show();
        }
    };

    private final Observer<EstimatesEvent> estimatesMangerObserver =
        new Observer<EstimatesEvent>() {
            @Override
            public void onSubscribe(Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onNext(EstimatesEvent estimatesEvent) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trip, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        userDataManager = new UserDataManager(new SPManager(getContext()), getArguments());
        userDataManager.subscribe((updatedUserData, includeReturnRoute) -> {
            compositeDisposable.dispose();
            compositeDisposable = new CompositeDisposable();
            getTrips();
            Utils.showSnackbar(getActivity(), userRouteFooter,
                R.color.bartBlue, R.string.updated_data);
        });

        userRouteFooter.withUserDataManager(userDataManager);

        adapter = new TripsAdapter(userDataManager);

        renderFooter();

        recyclerView.setAdapter(adapter);

        getTrips();
        estimatesManager.subscribe(estimatesMangerObserver);
    }

    @Override
    public void onDestroyView() {
        compositeDisposable.dispose();
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void renderFooter() {
        userRouteFooter.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(0, 0, 0, userRouteFooter.getMeasuredHeight());
        recyclerView.setLayoutParams(params);
    }

    @UiThread
    public void loadUserRoutes(List<Trip> mergedTrips) {
        progressBar.setVisibility(View.GONE);

        failureText.setVisibility(View.GONE);

        boolean includeReturnRoute = userDataManager.includeReturnRoute();

        adapter.refresh(mergedTrips);

        Map<String, Set<String>> origToDestsMapping = NotGuava.newHashMap();
        for (Trip trip : mergedTrips) {
            if (trip != null) {
                if (!origToDestsMapping.containsKey(trip.getOrigin())) {
                    origToDestsMapping.put(trip.getOrigin(),
                        NotGuava.newHashSet(trip.getLegList().get(0).getTrainHeadStation()));
                } else {
                    Set<String> destSet = origToDestsMapping.get(trip.getOrigin());
                    destSet.add(trip.getLegList().get(0).getTrainHeadStation());
                }
            }
        }

        RouteBundle routeBundle = null;
        if (routeFirstLegHead != null) {
            String originAbbr = userDataManager.getOriginStationData().getAbbr();
            routeBundle = new RouteBundle(originAbbr, origToDestsMapping.get(originAbbr));
        }

        RouteBundle returnRouteBundle = null;
        if (includeReturnRoute && returnFirstLegHead != null) {
            String destAbbr = userDataManager.getDestinationStationData().getAbbr();
            returnRouteBundle = new RouteBundle(destAbbr, origToDestsMapping.get(destAbbr));
        }

        estimatesManager.populateWithFeedEstimates(routeBundle, returnRouteBundle);
    }

    public void updateUserStations(int resultCode, int stationIndex) {
        userRouteFooter.updateStations(resultCode,
            StationsManager.getStations().get(stationIndex).getAbbr());
    }

    // Does RxJava things to get a list of trips
    private void getTrips() {
        UserStationData origin = userDataManager.getOriginStationData();
        UserStationData destination = userDataManager.getDestinationStationData();


        Single<List<Trip>> departuresSingle = RetrofitClient.getTrips(
            origin.getAbbr(),
            destination.getAbbr());

        Single<List<Trip>> returnDeparturesSingle = null;
        if (userDataManager.includeReturnRoute()) {
            returnDeparturesSingle = RetrofitClient.getTrips(
                destination.getAbbr(),
                origin.getAbbr());
        }

        mergeTrips(departuresSingle, returnDeparturesSingle)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(tripSubscriber);
    }

    /**
     * Merges trips and return trips together into a Single of List<Trip>
     * @param tripsSingle Single<List<Trip>> for the user specified route
     * @param returnTripsSingle Single<List<Trip>> for the return route
     * @return A single containing all the trips merged together
     */
    private Single<List<Trip>> mergeTrips(Single<List<Trip>> tripsSingle,
                                          Single<List<Trip>> returnTripsSingle) {
        if (returnTripsSingle != null) {
            return Single.zip(tripsSingle, returnTripsSingle,
                (trips, returnTrips) -> {
                    List<Trip> mergedTrips = NotGuava.newArrayList();

                    mergedTrips.addAll(trips);
                    if (trips.get(0) != null) {
                        routeFirstLegHead = trips.get(0).getLegList().get(0).getTrainHeadStation();
                    }

                    mergedTrips.addAll(returnTrips);
                    if (returnTrips.get(0) != null) {
                        returnFirstLegHead = trips.get(0).getLegList().get(0).getTrainHeadStation();
                    }

                    return mergedTrips;
                });
        } else {
            return tripsSingle.doOnSuccess(trips -> {
                if (trips.get(0) != null) {
                    routeFirstLegHead = trips.get(0).getLegList().get(0).getTrainHeadStation();
                }
            });
        }
    }
}
