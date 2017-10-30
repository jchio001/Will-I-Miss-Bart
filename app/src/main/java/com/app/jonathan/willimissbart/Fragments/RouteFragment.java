package com.app.jonathan.willimissbart.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.jonathan.willimissbart.API.Callbacks.DeparturesCallback;
import com.app.jonathan.willimissbart.API.Models.Generic.FailureEvent;
import com.app.jonathan.willimissbart.API.Models.Routes.RoutesFailure;
import com.app.jonathan.willimissbart.API.Models.Routes.Trip;
import com.app.jonathan.willimissbart.API.Models.Routes.TripsWrapper;
import com.app.jonathan.willimissbart.API.RetrofitClient;
import com.app.jonathan.willimissbart.Adapters.RoutesAdapter;
import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.Misc.Utils;
import com.app.jonathan.willimissbart.Persistence.Models.UserStationData;
import com.app.jonathan.willimissbart.Persistence.SPSingleton;
import com.app.jonathan.willimissbart.Persistence.StationsSingleton;
import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.ViewHolders.UserRouteFooterViewHolder;
import com.google.common.collect.Lists;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RouteFragment extends Fragment {
    @Bind(R.id.progress_bar) ProgressBar progressBar;
    @Bind(R.id.route_parent) RelativeLayout parent;
    @Bind(R.id.footer_wrapper) LinearLayout footerLayout;
    @Bind(R.id.failure_text) TextView failureText;
    @Bind(R.id.route_recycler) RecyclerView recyclerView;

    private UserRouteFooterViewHolder footer;
    private List<UserStationData> userData;
    private List<UserStationData> updatedUserData;
    private RoutesAdapter adapter = new RoutesAdapter();
    private List<Trip>[] trips = new List[2];

    private int respCnt = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_route, container, false);
        ButterKnife.bind(this, v);
        EventBus.getDefault().register(this);

        if (getArguments() != null) {
            userData = getArguments().getParcelableArrayList(Constants.USER_DATA);
        } else {
            userData = SPSingleton.getUserData(getActivity());
        }
        updatedUserData = Lists.newArrayList(userData);

        footer = new UserRouteFooterViewHolder(footerLayout, this, updatedUserData);
        footerLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(0, 0, 0, footerLayout.getMeasuredHeight());
        parent.setLayoutParams(params);

        recyclerView.setAdapter(adapter);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        boolean includeReturnRoute = SPSingleton.getIncludeReturnRoute(getContext());
        if (!includeReturnRoute) {
            respCnt = 1;
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
    }

    @Subscribe
    public synchronized void departuresResponse(TripsWrapper wrapper) {
        trips[wrapper.isReturnRoute() ? 1 : 0] = wrapper.getTrips();
        ++respCnt;
        loadUserRoutes();
    }

    @Subscribe
    public synchronized void departuresFailure(RoutesFailure event) {
        trips[event.isReturnRoute() ? 1 : 0] = null;
        ++respCnt;
        loadUserRoutes();
    }

    public void loadUserRoutes() {
        if (respCnt == 2) {
            progressBar.setVisibility(View.GONE);
            failureText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            List<Trip> merged = trips[0] != null ? trips[0] : Lists.<Trip>newArrayList();
            if (SPSingleton.getIncludeReturnRoute(this.getActivity()) && trips[1] != null) {
                merged.addAll(trips[1]);
            }

            adapter.addAll(merged);
        }
    }

    public void updateUserStations(int resultCode, int stationIndex) {
        updatedUserData.set(resultCode - 1, UserStationData.fromStationIndex(stationIndex));
        footer.updateStations(resultCode, StationsSingleton.getStations()
            .get(stationIndex).getAbbr());
    }

    public void persistUpdatesAndRefresh() {
        SPSingleton.persistUserData(getActivity(), updatedUserData);

        boolean isChecked = footer.includeReturn.isChecked();
        SPSingleton.persistIncludeReturnRoute(getActivity(), isChecked);
        RetrofitClient.getCurrentDepartures(
            updatedUserData.get(0).getAbbr(),
            updatedUserData.get(1).getAbbr(),
            false);
        respCnt = 1;

        if (isChecked) {
            RetrofitClient.getCurrentDepartures(
                updatedUserData.get(1).getAbbr(),
                updatedUserData.get(0).getAbbr(),
                true);
            respCnt = 0;
        }

        Utils.showSnackbar(getActivity(), footerLayout, R.color.bartBlue, R.string.updated_data);
    }
}
