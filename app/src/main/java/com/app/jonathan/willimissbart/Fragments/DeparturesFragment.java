package com.app.jonathan.willimissbart.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.jonathan.willimissbart.API.Callbacks.EtdCallback;
import com.app.jonathan.willimissbart.API.Models.DeparturesFeedModels.FlattenedEstimate;
import com.app.jonathan.willimissbart.API.Models.EtdModels.EtdFailure;
import com.app.jonathan.willimissbart.API.Models.EtdModels.EtdRespBundle;
import com.app.jonathan.willimissbart.API.Models.EtdModels.EtdStation;
import com.app.jonathan.willimissbart.Adapters.DeparturesAdapter;
import com.app.jonathan.willimissbart.Listeners.SwipeRefresh.EtdRefreshListener;
import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.Misc.SharedEtdDataBundle;
import com.app.jonathan.willimissbart.Misc.Utils;
import com.app.jonathan.willimissbart.Persistence.Models.UserStationData;
import com.app.jonathan.willimissbart.Persistence.SPSingleton;
import com.app.jonathan.willimissbart.Persistence.StationsSingleton;
import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.ViewHolders.UserRouteFooterViewHolder;
import com.google.common.collect.Lists;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

// Fragments kind of suck...
@SuppressWarnings("unchecked")
public class DeparturesFragment extends Fragment {
    @Bind(R.id.departures_parent) RelativeLayout parent;
    @Bind(R.id.main_swl) SwipeRefreshLayout mainSWL;
    @Bind(R.id.main_feed_layout) RecyclerView mainFeedLayout;
    @Bind(R.id.progressBar) ProgressBar progressBar;
    @Bind(R.id.no_etds_to_display) TextView nothingToDisplayTV;
    @Bind(R.id.departures_as_of) TextView departuresAsOf;
    @Bind(R.id.footer_wrapper) LinearLayout footerLayout;

    private UserRouteFooterViewHolder footer;

    private EtdRefreshListener etdRefreshListener;
    private DeparturesAdapter departuresAdapter;

    List<UserStationData> userData;
    List<UserStationData> updatedUserData; // Clone for updating purposes

    // Data for setting the the feed of ETD's
    private EtdStation[] stationArr = new EtdStation[5];
    private boolean[] successArr = new boolean[5]; // keeps track of if API calls are successful
    // keeps track of when each call is received (in epoch seconds)
    // this is so that better timer intervals can be generated
    private long[] timeOfResponse = new long[5];
    DateFormat format = new SimpleDateFormat("h:mm a", Locale.US);

    private SharedEtdDataBundle sharedEtdDataBundle = new SharedEtdDataBundle();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_departures, container, false);
        ButterKnife.bind(this, v);
        if (getArguments() != null) {
            userData = getArguments().getParcelableArrayList(Constants.USER_DATA);
        } else {
            userData = SPSingleton.getUserData(getActivity());
        }
        updatedUserData = Lists.newArrayList(userData);
        footer = new UserRouteFooterViewHolder(footerLayout, this, updatedUserData);
        EventBus.getDefault().register(this);

        mainSWL.setEnabled(false);
        footerLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)
            mainSWL.getLayoutParams();
        params.setMargins(0, 0, 0, footerLayout.getMeasuredHeight());
        mainSWL.setLayoutParams(params);

        departuresAdapter = new DeparturesAdapter(Lists.<FlattenedEstimate>newArrayList());
        departuresAdapter.setHasStableIds(true);
        etdRefreshListener = new EtdRefreshListener(mainSWL)
            .setSharedEtdDataBundle(sharedEtdDataBundle);

        LinearLayoutManager layoutManager = new LinearLayoutManager(
            getActivity(), LinearLayoutManager.VERTICAL, false);
        mainFeedLayout.setLayoutManager(layoutManager);
        mainFeedLayout.setAdapter(departuresAdapter);
        mainSWL.setOnRefreshListener(etdRefreshListener);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etdRefreshListener.setUserBartData(updatedUserData);
        Utils.fetchEtds(userData);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEtdResponse(EtdRespBundle etdRespBundle) {
        synchronized (this) {
            timeOfResponse[etdRespBundle.getIndex()] = System.currentTimeMillis() / 1000;
            stationArr[etdRespBundle.getIndex()] =
                etdRespBundle.getEtdResp().getRoot().getStation().get(0);
            successArr[etdRespBundle.getIndex()] = true;
            ++sharedEtdDataBundle.stationCntr;
        }
        if (sharedEtdDataBundle.stationCntr == 2) {
            loadFeed(stationArr);
        }
    }

    @Subscribe
    public synchronized void onEtdFailure(EtdFailure failure) {
        //No idea why I need to tag the failure, should remove. TODO: think about this later
        if (failure.tag.equals(EtdCallback.tag)) {
            synchronized (this) {
                timeOfResponse[failure.index] = System.currentTimeMillis() / 1000;
                stationArr[failure.index] = new EtdStation(failure.data);
                successArr[failure.index] = false;
                ++sharedEtdDataBundle.stationCntr;
            }

            if (sharedEtdDataBundle.stationCntr == 2) {
                loadFeed(stationArr);
            }
        }
    }

    public void updateUserStations(int resultCode, int stationIndex) {
        updatedUserData.set(resultCode - 1, UserStationData.fromStationIndex(stationIndex));
        footer.updateStations(resultCode, StationsSingleton.getStations()
            .get(stationIndex).getAbbr());
    }

    public void persistUpdatesAndRefresh() {
        SPSingleton.persistUserData(getActivity(), updatedUserData);
        etdRefreshListener.forceRefresh();
        Utils.showSnackbar(getActivity(), parent, R.color.bartBlue, R.string.updated_data);
    }

    private void handleNothingToFetch() {
        progressBar.setVisibility(View.INVISIBLE);
        mainSWL.setVisibility(View.INVISIBLE);
        nothingToDisplayTV.setVisibility(View.VISIBLE);
    }

    // load feed all API calls have been made
    private void loadFeed(EtdStation[] stations) {
        progressBar.setVisibility(View.GONE);
        mainSWL.setRefreshing(false);
        mainFeedLayout.setVisibility(View.INVISIBLE);

        setUpRetrievalTimeText();
        List<FlattenedEstimate> flattenedEstimates = Utils.flattenEstimates(
            stations, userData, timeOfResponse, successArr, 2);
        departuresAdapter.refresh(flattenedEstimates);

        mainSWL.setVisibility(View.VISIBLE);
        mainFeedLayout.setVisibility(View.VISIBLE);

        etdRefreshListener.setRefreshState(Constants.REFRESH_STATE_INACTIVE);
        mainSWL.setEnabled(true);
    }

    private void setUpRetrievalTimeText() {
        departuresAsOf.setText(
            String.format(
                getString(R.string.departures_as_of),
                format.format(Calendar.getInstance().getTime()))
        );
    }
}
