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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.jonathan.willimissbart.API.Callbacks.EtdCallback;
import com.app.jonathan.willimissbart.API.Models.DeparturesFeedModels.FlattenedEstimate;
import com.app.jonathan.willimissbart.API.Models.EtdModels.EtdFailure;
import com.app.jonathan.willimissbart.API.Models.EtdModels.EtdRespBundle;
import com.app.jonathan.willimissbart.API.Models.EtdModels.EtdStation;
import com.app.jonathan.willimissbart.Adapters.DeparturesAdapter;
import com.app.jonathan.willimissbart.Enums.RefreshStateEnum;
import com.app.jonathan.willimissbart.Listeners.SwipeRefresh.EtdRefreshListener;
import com.app.jonathan.willimissbart.Misc.SharedEtdDataBundle;
import com.app.jonathan.willimissbart.Misc.Utils;
import com.app.jonathan.willimissbart.Persistence.Models.UserBartData;
import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.ViewHolders.DeparturesViewHolder;
import com.google.common.collect.Lists;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    private EtdRefreshListener etdRefreshListener;
    private DeparturesAdapter departuresAdapter;

    private List<UserBartData> userBartData = Lists.newArrayList();
    private List<UserBartData> filteredUserBartData = Lists.newArrayList();
    private List<DeparturesViewHolder> mainElemViewHolders = Lists.newArrayList();

    // Data for setting the the feed of ETD's
    private EtdStation[] stationArr = new EtdStation[5];
    // Need the user data to make retry calls. TODO: use this
    private UserBartData[] associatedData = new UserBartData[5];
    private boolean[] successArr = new boolean[5]; // keeps track of if API calls are successful
    // keeps track of when each call is received (in epoch seconds)
    // this is so that better timer intervals can be generated
    private long[] timeOfResponse = new long[5];
    DateFormat format = new SimpleDateFormat("h:mm a", Locale.US);

    private SharedEtdDataBundle sharedEtdDataBundle = new SharedEtdDataBundle();

    private int day = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_departures, container, false);
        ButterKnife.bind(this, v);
        EventBus.getDefault().register(this);

        mainSWL.setEnabled(false);
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

        Bundle bundle = getArguments();
        deserializeAndFilter(Utils.getUserBartData(bundle, getActivity().getApplicationContext()));
        etdRefreshListener.setUserBartData(filteredUserBartData);

        if (!filteredUserBartData.isEmpty()) {
            Utils.fetchEtds(filteredUserBartData);
        } else {
            handleNothingToFetch();
        }
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
        if (sharedEtdDataBundle.stationCntr == filteredUserBartData.size()) {
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
                // only need the user data for failure since I want to user to be able to refreshOnNewData
                // each item individually
                associatedData[failure.index] = failure.data;
                successArr[failure.index] = false;
                ++sharedEtdDataBundle.stationCntr;
            }

            if (sharedEtdDataBundle.stationCntr == filteredUserBartData.size()) {
                loadFeed(stationArr);
            }
        }
    }

    @Subscribe
    public void onUpdatedUserData(List<UserBartData> newUserData) {
        mainSWL.setVisibility(View.VISIBLE);
        mainSWL.setRefreshing(true);
        userBartData = newUserData;
        filteredUserBartData = filterUserBartData(newUserData);
        nothingToDisplayTV.setVisibility(View.GONE);
        sharedEtdDataBundle.stationCntr = 0;

        if (!filteredUserBartData.isEmpty()) {
            Utils.fetchEtds(userBartData);
        } else {
            mainSWL.setVisibility(View.INVISIBLE);
            mainSWL.setRefreshing(false);
            handleNothingToFetch();
        }
    }

    private void deserializeAndFilter(String serializedUserData) {
        userBartData = Utils.convertToList(serializedUserData);
        filteredUserBartData = filterUserBartData(userBartData);
    }

    private List<UserBartData> filterUserBartData(List<UserBartData> userBartData) {
        day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
        List<UserBartData> filteredByToday = new ArrayList<>();
        for (UserBartData data : userBartData) {
            if (data.getDays()[day]) {
                filteredByToday.add(data);
            }
        }

        return filteredByToday;
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
            stations, associatedData, timeOfResponse, successArr, filteredUserBartData.size());
        departuresAdapter.refresh(flattenedEstimates);

        mainSWL.setVisibility(View.VISIBLE);
        mainFeedLayout.setVisibility(View.VISIBLE);

        etdRefreshListener.setRefreshState(RefreshStateEnum.INACTIVE);
        mainSWL.setEnabled(true);
    }

    private void setUpRetrievalTimeText() {
        departuresAsOf.setText(
            String.format(
                getString(R.string.departures_as_of),
                format.format(Calendar.getInstance().getTime()))
        );
    }

    public void refreshOnNewData(List<UserBartData> freshData) {
        int oldFilteredDataSize = filteredUserBartData.size();
        if (freshData != null) {
            userBartData = freshData;
            filteredUserBartData = filterUserBartData(freshData);
            etdRefreshListener.setUserBartData(filteredUserBartData);
            if (filteredUserBartData.size() > 0) {
                if (oldFilteredDataSize == 0) {
                    progressBar.setVisibility(View.VISIBLE);
                    nothingToDisplayTV.setVisibility(View.INVISIBLE);
                    etdRefreshListener.forceRefresh();
                } else {
                    // Lazy loading (request to refresh is affected by the cooldown)
                    mainSWL.setRefreshing(true);
                    etdRefreshListener.onRefresh();
                }
            } else {
                handleNothingToFetch();
            }
        }
    }
}
