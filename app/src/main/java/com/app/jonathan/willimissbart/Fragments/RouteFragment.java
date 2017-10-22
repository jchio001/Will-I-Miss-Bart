package com.app.jonathan.willimissbart.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.jonathan.willimissbart.API.Models.Routes.DeparturesResp;
import com.app.jonathan.willimissbart.API.RetrofitClient;
import com.app.jonathan.willimissbart.Adapters.RoutesAdapter;
import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.Persistence.Models.UserStationData;
import com.app.jonathan.willimissbart.Persistence.SPSingleton;
import com.app.jonathan.willimissbart.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RouteFragment extends Fragment {
    @Bind(R.id.route_recycler) RecyclerView recyclerView;

    private List<UserStationData> userData;

    private RoutesAdapter adapter = new RoutesAdapter();

    boolean fetch = true;

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

        recyclerView.setAdapter(adapter);
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // Make sure that we are currently visible
        if (this.isVisible() && isVisibleToUser && fetch) {
            fetch = false;
            RetrofitClient.getCurrentDepartures(
                userData.get(0).getAbbr(),
                userData.get(1).getAbbr());
        }
    }

    @Subscribe
    public void departuresResponse(DeparturesResp resp) {
        adapter.addAll(resp.getRoot().getSchedule().getRequest().getTrips());
    }
}
