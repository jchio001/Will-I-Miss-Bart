package com.example.jonathan.willimissbart.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.jonathan.willimissbart.API.APIConstants;
import com.example.jonathan.willimissbart.API.Callbacks.EtdCallback;
import com.example.jonathan.willimissbart.API.Models.EtdModels.EtdFailure;
import com.example.jonathan.willimissbart.API.Models.EtdModels.EtdRespBundle;
import com.example.jonathan.willimissbart.API.Models.EtdModels.EtdStation;
import com.example.jonathan.willimissbart.API.RetrofitClient;
import com.example.jonathan.willimissbart.Misc.Utils;
import com.example.jonathan.willimissbart.Persistence.Models.UserBartData;
import com.example.jonathan.willimissbart.R;
import com.example.jonathan.willimissbart.ViewHolders.MainFeedElemViewHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

//Fragments kind of suck...
@SuppressWarnings("unchecked")
public class MyStationsFragment extends Fragment {
    @Bind(R.id.main_feed_layout) LinearLayout mainFeedLayout;
    @Bind(R.id.progressBar) ProgressBar progressBar;

    private List<UserBartData> userBartData;
    private List<UserBartData> filteredUserBartData;

    private List<MainFeedElemViewHolder> mainElemViewHolders;
    private EtdStation[] stationArr = new EtdStation[5];

    private int day = -1;
    private int stationCntr = 0;

    public MyStationsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_my_stations, container, false);
        ButterKnife.bind(this, v);
        EventBus.getDefault().register(this);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        deserializeAndFilter(Utils.getUserBartData(bundle, getActivity().getApplicationContext()));
        fetchBartData(filteredUserBartData);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public synchronized void onEtdResponse(EtdRespBundle etdRespBundle) {
        stationArr[etdRespBundle.getIndex()] =
                etdRespBundle.getEtdResp().getRoot().getStation().get(0);

        ++stationCntr;
        if (stationCntr == filteredUserBartData.size()) {
            loadFeed(stationArr);
        }
    }

    @Subscribe
    public synchronized void onEtdFailure(EtdFailure failure) {
        if (failure.tag.equals("EtdCallback")) {
            ++stationCntr;

            if (stationCntr == filteredUserBartData.size()) {
                loadFeed(stationArr);
            }
        }
    }

    private void deserializeAndFilter(String serializedUserData) {
        userBartData = convertToList(serializedUserData);
        filteredUserBartData = filterUserBartData(userBartData);
    }

    private List<UserBartData> convertToList(String serializedUserData) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<UserBartData>>(){}.getType();
        return gson.fromJson(serializedUserData, listType);
    }

    private List<UserBartData> filterUserBartData(List<UserBartData> userBartData) {
        int curDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
        if (curDay != day) {
            List<UserBartData> filteredByToday = new ArrayList<>();
            day = curDay;
            for (UserBartData data : userBartData) {
                if (data.getDays()[curDay]) {
                    filteredByToday.add(data);
                }
            }
            return filteredByToday;
        }
        return filteredUserBartData;
    }

    //should usually be using this on main bart
    private void loadFeed(EtdStation[] stations) {
        progressBar.setVisibility(View.GONE);
        List<MainFeedElemViewHolder> viewHolders = new ArrayList<>();
        LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE
        );

        for (EtdStation s : stations) {
            if (s != null) {
                LinearLayout mainBartDataElem = (LinearLayout) vi.inflate(
                        R.layout.main_bart_data_layout, null
                );
                MainFeedElemViewHolder viewHolder = new MainFeedElemViewHolder(mainBartDataElem, s);
                viewHolders.add(viewHolder);
                mainFeedLayout.addView(mainBartDataElem);
            } else {
                Log.e("MyStationsFragment", "EtdStation is null");
            }
        }

        mainElemViewHolders = viewHolders;
    }

    //also usually filtered
    private void fetchBartData(List<UserBartData> userBartData) {
        for (int i = 0; i < userBartData.size(); ++i) {
            UserBartData data = userBartData.get(i);
            RetrofitClient.getInstance()
                    .getMatchingService()
                    .getEtd("etd", APIConstants.API_KEY, 'y', data.getAbbr(),
                            Utils.directionToUrlParam(data.getDirection())
                    )
                    .clone()
                    .enqueue(new EtdCallback().setStationName(data.getStation()).setIndex(i));
        }
    }
}
