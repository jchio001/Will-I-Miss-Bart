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

import com.app.jonathan.willimissbart.API.Models.Routes.RoutesFailure;
import com.app.jonathan.willimissbart.API.Models.Routes.Trip;
import com.app.jonathan.willimissbart.API.Models.Routes.TripsWrapper;
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
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

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
    private List<Trip>[] trips = new List[2];

    private String routeFirstLegHead = null;
    private String returnFirstLegHead = null;

    private int routesCnt = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_trip, container, false);
        ButterKnife.bind(this, v);
        EventBus.getDefault().register(this);
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
        if (!includeReturnRoute) {
            routesCnt = 1;
        }

        RetrofitClient.getCurrentDepartures(
            userData.get(0).getAbbr(),
            userData.get(1).getAbbr(),
            false);

        if (includeReturnRoute) {
            RetrofitClient.getCurrentDepartures(
                userData.get(1).getAbbr(),
                userData.get(0).getAbbr(),
                true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
        EstimatesManager.unregister(adapter);
    }

    @Subscribe
    public void departuresResponse(TripsWrapper wrapper) {
        boolean loadFeed = false;
        synchronized (this) {
            trips[wrapper.isReturnRoute() ? 1 : 0] = wrapper.getTrips();
            ++routesCnt;

            if (routesCnt == 2) {
                loadFeed = true;
            }
        }

        if (!wrapper.isReturnRoute()) {
            routeFirstLegHead = wrapper.getTrips().get(0)
                .getLegList().get(0)
                .getTrainHeadStation();
        } else {
            returnFirstLegHead = wrapper.getTrips().get(0)
                .getLegList().get(0)
                .getTrainHeadStation();
        }

        if (loadFeed) {
            loadUserRoutes();
        }
    }

    @Subscribe
    public void departuresFailure(RoutesFailure event) {
        boolean loadFeed = false;
        synchronized (this) {
            trips[event.isReturnRoute() ? 1 : 0] = null;
            ++routesCnt;

            if (routesCnt == 2) {
                loadFeed = true;
            }
        }

        if (loadFeed) {
            loadUserRoutes();
        }
    }

    private List<Trip> mergeFetchedTrips(boolean includeReturnRoute) {
        List<Trip> merged = trips[0] != null ? trips[0] : Lists.newArrayList((Trip) null);
        if (includeReturnRoute) {
            merged.addAll(trips[1] != null ? trips[1] : Lists.newArrayList((Trip) null));
        }
        return merged;
    }

    private void renderFooter() {
        footer = new UserRouteFooterViewHolder(footerLayout, this, updatedUserData);
        footerLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(0, 0, 0, footerLayout.getMeasuredHeight());
        routeSwipeRefresh.setLayoutParams(params);
    }

    public void loadUserRoutes() {
        progressBar.setVisibility(View.GONE);

        routeRefreshListener.setRefreshState(Constants.REFRESH_STATE_INACTIVE);
        routeSwipeRefresh.setRefreshing(false);
        routeSwipeRefresh.setEnabled(true);

        failureText.setVisibility(View.GONE);
        // recyclerView.setVisibility(View.VISIBLE);

        boolean includeReturnRoute = SPManager.getIncludeReturnRoute(this.getActivity());
        List<Trip> merged = mergeFetchedTrips(includeReturnRoute);

        adapter.addAll(merged, userData);

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
            updatedUserData.get(1).getAbbr(),
            false);
        routesCnt = 1;

        if (isChecked) {
            RetrofitClient.getCurrentDepartures(
                updatedUserData.get(1).getAbbr(),
                updatedUserData.get(0).getAbbr(),
                true);
            routesCnt = 0;
        }

        Utils.showSnackbar(getActivity(), footerLayout, R.color.bartBlue, R.string.updated_data);
    }
}
