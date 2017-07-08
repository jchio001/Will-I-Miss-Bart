package com.example.jonathan.willimissbart.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.jonathan.willimissbart.Misc.Constants;
import com.example.jonathan.willimissbart.Misc.Utils;
import com.example.jonathan.willimissbart.Persistence.Models.UserBartData;
import com.example.jonathan.willimissbart.Persistence.SPSingleton;
import com.example.jonathan.willimissbart.R;
import com.example.jonathan.willimissbart.ViewHolders.MainFeedElemViewHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

//Fragments kind of suck...
public class MyStationsFragment extends Fragment {
    @Bind(R.id.main_feed_layout) LinearLayout mainFeedLayout;

    private List<UserBartData> userBartData;
    private List<UserBartData> filterUserBartData;

    private List<MainFeedElemViewHolder> mainElemViewHolders;

    private int day = -1;

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
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        deserializeAndFilter(Utils.getUserBartData(bundle, getActivity().getApplicationContext()));
        loadFeed(filterUserBartData);
    }

    private void deserializeAndFilter(String serializedUserData) {
        userBartData = convertToList(serializedUserData);
        filterUserBartData = filterUserBartData(userBartData);
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
        return filterUserBartData;
    }

    //should usually be using this on main bart
    private void loadFeed(List<UserBartData> userBartData) {
        List<MainFeedElemViewHolder> viewHolders = new ArrayList<>();
        LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE
        );

        for (UserBartData data : userBartData) {
            LinearLayout mainBartDataElem = (LinearLayout) vi.inflate(
                    R.layout.main_bart_data_layout, null
            );
            MainFeedElemViewHolder viewHolder = new MainFeedElemViewHolder(mainBartDataElem, data);
            viewHolders.add(viewHolder);
            mainFeedLayout.addView(mainBartDataElem);
        }

        mainElemViewHolders = viewHolders;
    }
}
