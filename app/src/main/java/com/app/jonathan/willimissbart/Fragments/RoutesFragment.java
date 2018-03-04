package com.app.jonathan.willimissbart.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.jonathan.willimissbart.API.Models.Etd.EtdRespWrapper;
import com.app.jonathan.willimissbart.API.Models.Routes.Trip;
import com.app.jonathan.willimissbart.API.RetrofitClient;
import com.app.jonathan.willimissbart.Adapters.TripsAdapter;
import com.app.jonathan.willimissbart.Listeners.SwipeRefresh.TripRefreshListener;
import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.Misc.EstimatesManager;
import com.app.jonathan.willimissbart.Misc.NotGuava;
import com.app.jonathan.willimissbart.Misc.Utils;
import com.app.jonathan.willimissbart.Persistence.Models.UserStationData;
import com.app.jonathan.willimissbart.Persistence.SPManager;
import com.app.jonathan.willimissbart.Persistence.StationsManager;
import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.ViewHolders.UserRouteFooterViewHolder;

import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class RoutesFragment extends Fragment {
    @Bind(R.id.route_swipe_refresh) SwipeRefreshLayout routeSwipeRefresh;
    @Bind(R.id.route_recycler) RecyclerView recyclerView;
    @Bind(R.id.progress_bar) ProgressBar progressBar;
    @Bind(R.id.footer_wrapper) LinearLayout footerLayout;
    @Bind(R.id.failure_text) TextView failureText;

    private TripRefreshListener routeRefreshListener;
    private UserRouteFooterViewHolder footer;
    private List<UserStationData> userData;
    private List<UserStationData> updatedUserData;
    private TripsAdapter adapter = new TripsAdapter();

    private String routeFirstLegHead = null;
    private String returnFirstLegHead = null;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

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
                String.format("Wah wah %s", e.getMessage()), Toast.LENGTH_SHORT)
                .show();
        }
    };

    private final SingleObserver<EtdRespWrapper> etdObserver =
        new SingleObserver<EtdRespWrapper>() {
            @Override
            public void onSubscribe(Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(EtdRespWrapper etdRespWrapper) {
                EstimatesManager.persistThenPost(etdRespWrapper);
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(RoutesFragment.this.getContext(),
                    String.format("Wah wah %s", e.getMessage()), Toast.LENGTH_SHORT)
                    .show();
            }
        };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_trip, container, false);
        ButterKnife.bind(this, v);
        EstimatesManager.register(adapter);

        routeSwipeRefresh.setEnabled(false);
        routeRefreshListener = new TripRefreshListener(routeSwipeRefresh, adapter);
        routeSwipeRefresh.setOnRefreshListener(routeRefreshListener);

        if (getArguments() != null) {
            userData = getArguments().getParcelableArrayList(Constants.USER_DATA);
        } else {
            userData = SPManager.getUserData(getActivity());
        }
        updatedUserData = NotGuava.newArrayList(userData);

        renderFooter();

        recyclerView.setAdapter(adapter);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        boolean includeReturnRoute = SPManager.getIncludeReturnRoute(getContext());
        getTrips(userData.get(0), userData.get(1), includeReturnRoute);
    }

    @Override
    public void onDestroyView() {
        compositeDisposable.dispose();
        super.onDestroyView();
        ButterKnife.unbind(this);
        EstimatesManager.unregister(adapter);
    }

    private void renderFooter() {
        footer = new UserRouteFooterViewHolder(footerLayout, this, updatedUserData);
        footerLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(0, 0, 0, footerLayout.getMeasuredHeight());
        routeSwipeRefresh.setLayoutParams(params);
    }

    @UiThread
    public void loadUserRoutes(List<Trip> mergedTrips) {
        progressBar.setVisibility(View.GONE);

        routeRefreshListener.setRefreshState(Constants.REFRESH_STATE_INACTIVE);
        routeSwipeRefresh.setRefreshing(false);
        routeSwipeRefresh.setEnabled(true);

        failureText.setVisibility(View.GONE);

        boolean includeReturnRoute = SPManager.getIncludeReturnRoute(this.getActivity());

        adapter.refresh(mergedTrips, userData);

        // TODO: WRITE A BETTER FREAKING COMMENT HERE. DO I NEED A MAP OR NOT?
        Map<String, Set<String>> origToDestsMapping = NotGuava.newHashMap();
        for (Trip trip : adapter.getTrips()) {
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

        if (routeFirstLegHead != null) {
            String originAbbr = userData.get(0).getAbbr();
            RetrofitClient.getRealTimeEstimates(originAbbr, origToDestsMapping.get(originAbbr))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(etdObserver);
        }

        if (includeReturnRoute && returnFirstLegHead != null) {
            String destAbbr = userData.get(1).getAbbr();
            RetrofitClient.getRealTimeEstimates(destAbbr, origToDestsMapping.get(destAbbr))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(etdObserver);
        }
    }

    public void updateUserStations(int resultCode, int stationIndex) {
        updatedUserData.set(resultCode - 1, UserStationData.fromStationIndex(stationIndex));
        footer.updateStations(resultCode, StationsManager.getStations().get(stationIndex).getAbbr());
    }

    public void persistUpdatesAndRefresh() {
        compositeDisposable.dispose();
        compositeDisposable = new CompositeDisposable();

        // At this point, userData & updatedUserData need to be identical (in terms of what elements
        // are contained within each list, not do they point to the same list)
        SPManager.persistUserData(getActivity(), updatedUserData);
        userData = NotGuava.newArrayList(updatedUserData);

        boolean isChecked = footer.includeReturn.isChecked();
        SPManager.persistIncludeReturnRoute(getActivity(), isChecked);

        getTrips(updatedUserData.get(0), updatedUserData.get(1), isChecked);

        Utils.showSnackbar(getActivity(), footerLayout, R.color.bartBlue, R.string.updated_data);
    }

    /** Does RxJava things to get a list of trips */
    private void getTrips(UserStationData origin,
                          UserStationData destination,
                          boolean includeReturnRoute) {
        Single<List<Trip>> departuresSingle = RetrofitClient.getTrips(
            origin.getAbbr(),
            destination.getAbbr());

        Single<List<Trip>> returnDeparturesSingle = null;
        if (includeReturnRoute) {
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
