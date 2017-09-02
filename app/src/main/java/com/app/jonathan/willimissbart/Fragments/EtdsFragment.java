package com.app.jonathan.willimissbart.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.jonathan.willimissbart.API.Callbacks.EtdCallback;
import com.app.jonathan.willimissbart.API.Models.EtdModels.EtdFailure;
import com.app.jonathan.willimissbart.API.Models.EtdModels.EtdRespBundle;
import com.app.jonathan.willimissbart.API.Models.EtdModels.EtdStation;
import com.app.jonathan.willimissbart.Listeners.SwipeRefresh.EtdRefreshListener;
import com.app.jonathan.willimissbart.Enums.RefreshStateEnum;
import com.app.jonathan.willimissbart.Misc.SharedEtdDataBundle;
import com.app.jonathan.willimissbart.Misc.Utils;
import com.app.jonathan.willimissbart.Persistence.Models.UserBartData;
import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.ViewHolders.MainFeedElemViewHolder;
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

//Fragments kind of suck...
@SuppressWarnings("unchecked")
public class EtdsFragment extends Fragment {
    @Bind(R.id.main_swl) SwipeRefreshLayout mainSWL;
    @Bind(R.id.main_feed_layout) LinearLayout mainFeedLayout;
    @Bind(R.id.progressBar) ProgressBar progressBar;
    @Bind(R.id.no_etds_to_display) TextView nothingToDisplayTV;
    @Bind(R.id.departures_as_of) TextView departuresAsOf;

    private EtdRefreshListener etdRefreshListener;

    private List<UserBartData> userBartData;
    private List<UserBartData> filteredUserBartData;
    private List<MainFeedElemViewHolder> mainElemViewHolders = Lists.newArrayList();

    // Data for setting the the feed of ETD's
    private EtdStation[] stationArr = new EtdStation[5];
    // Need the user data to make retry calls
    private UserBartData[] associatedData = new UserBartData[5];
    private boolean[] successArr = new boolean[5]; // keeps track of if API calls are successful

    private int day = -1;
    private SharedEtdDataBundle sharedEtdDataBundle = new SharedEtdDataBundle();
    DateFormat format = new SimpleDateFormat("h:mm a", Locale.US);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_etds, container, false);
        ButterKnife.bind(this, v);
        mainSWL.setEnabled(false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        deserializeAndFilter(Utils.getUserBartData(bundle, getActivity().getApplicationContext()));

        if (!filteredUserBartData.isEmpty()) {
            Utils.fetchEtds(filteredUserBartData);
        } else {
            handleNothingToFetch();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEtdResponse(EtdRespBundle etdRespBundle) {
        synchronized (this) {
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
        //No idea why I need to tag the failure, should remove
        if (failure.tag.equals(EtdCallback.tag)) {
            synchronized (this) {
                stationArr[failure.index] = new EtdStation(failure.data);
                // only need the user data for failure since I want to user to be able to refresh
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
        nothingToDisplayTV.setVisibility(View.VISIBLE);
    }

    //use once all API calls have been made
    private void loadFeed(EtdStation[] stations) {
        progressBar.setVisibility(View.GONE);
        mainSWL.setRefreshing(false);
        mainFeedLayout.setVisibility(View.INVISIBLE);
        int curChildCnt = mainFeedLayout.getChildCount();

        for (int i = 0; i < curChildCnt - 1; ++i) {
            mainFeedLayout.removeViewAt(1);
        }
        setUpRetrievalTimeText();

        mainElemViewHolders.clear();
        LayoutInflater vi = LayoutInflater.from(getActivity());

        for (int i = 0; i < filteredUserBartData.size(); ++i) {
            EtdStation s = stations[i];
            View mainBartDataElem = vi.inflate(R.layout.main_bart_data_layout, null);
            if (s != null) {
                MainFeedElemViewHolder viewHolder = new MainFeedElemViewHolder(
                        mainBartDataElem,
                        getActivity(),
                        s,
                        associatedData[i],
                        successArr[i]
                );
                mainElemViewHolders.add(viewHolder);
                mainFeedLayout.addView(mainBartDataElem);
            } else {
                Log.e("EtdsFragment", "EtdStation is null");
            }
        }

        mainSWL.setVisibility(View.VISIBLE);
        mainFeedLayout.setVisibility(View.VISIBLE);

        if (etdRefreshListener == null) {
            etdRefreshListener = new EtdRefreshListener(mainSWL)
                    .setUserBartData(filteredUserBartData)
                    .setSharedEtdDataBundle(sharedEtdDataBundle);
            mainSWL.setOnRefreshListener(etdRefreshListener);
        }

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
}
