package com.app.jonathan.willimissbart.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.app.jonathan.willimissbart.API.Models.Routes.DeparturesResp;
import com.app.jonathan.willimissbart.API.Models.Routes.Trip;
import com.app.jonathan.willimissbart.API.RetrofitClient;
import com.app.jonathan.willimissbart.Adapters.TripsAdapter;
import com.app.jonathan.willimissbart.Listeners.SwipeRefresh.TripRefreshListener;
import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.Misc.EstimatesManager;
import com.app.jonathan.willimissbart.Misc.Utils;
import com.app.jonathan.willimissbart.Persistence.Models.UserStationData;
import com.app.jonathan.willimissbart.Persistence.SPManager;
import com.app.jonathan.willimissbart.Persistence.StationsManager;
import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.ViewHolders.UserRouteFooterViewHolder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

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
        updatedUserData = Lists.newArrayList(userData);

        renderFooter();

        recyclerView.setAdapter(adapter);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        boolean includeReturnRoute = SPManager.getIncludeReturnRoute(getContext());

        Single<Response<DeparturesResp>> departuresSingle = RetrofitClient.getCurrentDepartures(
            userData.get(0).getAbbr(),
            userData.get(1).getAbbr());

        Single<Response<DeparturesResp>> returnDeparturesSingle = null;
        if (includeReturnRoute) {
            returnDeparturesSingle = RetrofitClient.getCurrentDepartures(
                userData.get(1).getAbbr(),
                userData.get(0).getAbbr());
        }

        departuresToTripList(departuresSingle, returnDeparturesSingle)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new SingleObserver<List<Trip>>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onSuccess(List<Trip> mergedTrips) {
                    loadUserRoutes(mergedTrips);
                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(RoutesFragment.this.getContext(),
                        "Wah wah", Toast.LENGTH_SHORT).show();
                }
            });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
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

    public void loadUserRoutes(List<Trip> mergedTrips) {
        progressBar.setVisibility(View.GONE);

        routeRefreshListener.setRefreshState(Constants.REFRESH_STATE_INACTIVE);
        routeSwipeRefresh.setRefreshing(false);
        routeSwipeRefresh.setEnabled(true);

        failureText.setVisibility(View.GONE);
        // recyclerView.setVisibility(View.VISIBLE);

        boolean includeReturnRoute = SPManager.getIncludeReturnRoute(this.getActivity());

        adapter.addAll(mergedTrips, userData);

        // TODO: probably don't need a map. Also probably should explain what's going on
        Map<String, Set<String>> origToDestsMapping = Maps.newHashMap();
        for (Trip trip : adapter.getTrips()) {
            if (!origToDestsMapping.containsKey(trip.getOrigin())) {
                origToDestsMapping.put(trip.getOrigin(),
                    Sets.newHashSet(trip.getLegList().get(0).getTrainHeadStation()));
            } else {
                Set<String> destSet = origToDestsMapping.get(trip.getOrigin());
                destSet.add(trip.getLegList().get(0).getTrainHeadStation());
            }
        }

        if (routeFirstLegHead != null) {
            String originAbbr = userData.get(0).getAbbr();
            RetrofitClient.getRealTimeEstimates(originAbbr, origToDestsMapping.get(originAbbr));
        }

        if (includeReturnRoute && returnFirstLegHead != null) {
            String destAbbr = userData.get(1).getAbbr();
            RetrofitClient.getRealTimeEstimates(destAbbr, origToDestsMapping.get(destAbbr));
        }
    }

    public void updateUserStations(int resultCode, int stationIndex) {
        updatedUserData.set(resultCode - 1, UserStationData.fromStationIndex(stationIndex));
        footer.updateStations(resultCode, StationsManager.getStations()
            .get(stationIndex).getAbbr());
    }

    public void persistUpdatesAndRefresh() {
        // At this point, userData & updatedUserData need to be identical (in terms of what elements
        // are contained within each list, not do they point to the same list)
        SPManager.persistUserData(getActivity(), updatedUserData);
        userData = Lists.newArrayList(updatedUserData);

        boolean isChecked = footer.includeReturn.isChecked();
        SPManager.persistIncludeReturnRoute(getActivity(), isChecked);
        RetrofitClient.getCurrentDepartures(
            updatedUserData.get(0).getAbbr(),
            updatedUserData.get(1).getAbbr());

        if (isChecked) {
            RetrofitClient.getCurrentDepartures(
                updatedUserData.get(1).getAbbr(),
                updatedUserData.get(0).getAbbr());
        }

        Utils.showSnackbar(getActivity(), footerLayout, R.color.bartBlue, R.string.updated_data);
    }

    /**
     * Merges departures and return route departures together into a Single of List<Trip>
     * @param departuresRespSingle Single<DeparturesResp> for the user specified route
     * @param returnDeparturesRespSingle Single<DeparturesResp> for the return route
     * @return A single containing all the trips merged together
     */
    private Single<List<Trip>> departuresToTripList(
        Single<Response<DeparturesResp>> departuresRespSingle,
        Single<Response<DeparturesResp>> returnDeparturesRespSingle) {
        if (returnDeparturesRespSingle != null) {
            return Single.zip(departuresRespSingle, returnDeparturesRespSingle,
                (departuresResp, returnDeparturesResp) -> {
                    List<Trip> mergedTrips = Lists.newArrayList();

                    if (departuresResp.body() != null) {
                        List<Trip> trips = departuresResp.body()
                            .getRoot().getSchedule().getRequest().getTrips();
                        mergedTrips.addAll(trips);
                        routeFirstLegHead = trips.get(0).getLegList().get(0).getTrainHeadStation();

                    } else {
                        mergedTrips.add(null);
                    }

                    if (returnDeparturesResp.body() != null) {
                        List<Trip> trips = returnDeparturesResp.body()
                            .getRoot().getSchedule().getRequest().getTrips();
                        mergedTrips.addAll(trips);
                        returnFirstLegHead = trips.get(0).getLegList().get(0).getTrainHeadStation();
                    } else {
                        mergedTrips.add(null);
                    }

                    return mergedTrips;
                });
        } else {
            return departuresRespSingle.flatMap(departuresResponse -> {
                if (departuresResponse.body() != null) {
                    return Single.just(departuresResponse.body()
                        .getRoot().getSchedule().getRequest().getTrips());
                } else {
                    return Single.just((Lists.newArrayList((Trip) null)));
                }
            });
        }
    }
}
