package com.app.jonathan.willimissbart.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.app.jonathan.willimissbart.API.Models.StationModels.Station;
import com.app.jonathan.willimissbart.Activities.AppActivities.MainActivity;
import com.app.jonathan.willimissbart.Adapters.SimpleLargeTextListAdapter;
import com.app.jonathan.willimissbart.Adapters.StringAdapter;
import com.app.jonathan.willimissbart.Dialogs.DeleteAlertDialog;
import com.app.jonathan.willimissbart.Enums.StyleEnum;
import com.app.jonathan.willimissbart.Misc.MyApplication;
import com.app.jonathan.willimissbart.Misc.Utils;
import com.app.jonathan.willimissbart.Persistence.Models.UserBartData;
import com.app.jonathan.willimissbart.Persistence.SPSingleton;
import com.app.jonathan.willimissbart.Persistence.StationsSingleton;
import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.ViewHolders.BartDataElemViewHolder;
import com.app.jonathan.willimissbart.ViewHolders.OptionsElemViewHolder;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyStationsFragment extends Fragment
        implements DeleteAlertDialog.DeleteDataElemListener {
    @Bind(R.id.my_stations_parent) LinearLayout parent;
    @Bind(R.id.add) FloatingActionButton add;

    private List<UserBartData> userData = Lists.newArrayList();
    private List<BartDataElemViewHolder> bartDataElemViewHolders = Lists.newArrayList();
    private LayoutInflater vi;
    private SimpleLargeTextListAdapter<Station> stationsAdapter;
    private StringAdapter directionsAdapter;

    private long lastSaveTime = 0;

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.i("MyStationsFragment", "OnCreateView");
        View v = inflater.inflate(R.layout.fragment_my_stations, container, false);
        ButterKnife.bind(this, v);
        add.setImageDrawable(new IconDrawable(getActivity(), FontAwesomeIcons.fa_plus)
            .colorRes(R.color.white));

        vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        stationsAdapter = new SimpleLargeTextListAdapter(
                getActivity(),
                StationsSingleton.getInstance().getStationElems())
                .setStyle(StyleEnum.NO_STYLE);
        directionsAdapter = new StringAdapter(
                getActivity(),
                Arrays.asList(getResources().getStringArray(R.array.directions)))
                .setStyle(StyleEnum.NO_STYLE);

        Bundle bundle = getArguments();
        userData = Utils.convertToList(
                Utils.getUserBartData(bundle, getActivity().getApplicationContext()));
        loadFeed(userData);
        return v;
    }

    @Override
    public void onStop() {
        super.onStop();
        List<UserBartData> freshData = extractNewData();
        if (freshData != null) {
            SPSingleton.getInstance(MyApplication.getContext()).persistUserData(
                new Gson().toJson(freshData));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    // Let the fragment handle deletion since the order of the view holders needs to be maintained
    @Override
    public void deleteDataElem(int index) {
        Log.i("MyStationsFragment", String.format("Deleting %d", index));
        for (int i = index + 1; i < bartDataElemViewHolders.size(); ++i) {
            bartDataElemViewHolders.get(i).decrementIndex();
        }
        bartDataElemViewHolders.remove(index);
        parent.removeViewAt(index);
    }

    @OnClick(R.id.add)
    public synchronized void onAddDataElem() {
        if (bartDataElemViewHolders.size() < 5) {
            View bartDataView = vi.inflate(R.layout.bart_data_elem, null);
            bartDataElemViewHolders.add(
                new BartDataElemViewHolder(
                    bartDataView, getActivity(), this, bartDataElemViewHolders.size())
                    .setBartSpinnerAdapter(stationsAdapter)
                    .setDirectionSpinnerAdapter(directionsAdapter)
                    .setStyle(StyleEnum.NO_STYLE)
                    .build(null)
            );
            parent.addView(bartDataView, bartDataElemViewHolders.size() - 1);
        }
    }

    public void loadFeed(List<UserBartData> data) {
        for (int i = 0; i < data.size(); ++i) {
            View bartDataView = vi.inflate(R.layout.bart_data_elem, null);
            bartDataElemViewHolders.add(
                    new BartDataElemViewHolder(bartDataView, getActivity(), this, i)
                            .setBartSpinnerAdapter(stationsAdapter)
                            .setDirectionSpinnerAdapter(directionsAdapter)
                            .setStyle(StyleEnum.NO_STYLE)
                            .build(data.get(i))
            );
            parent.addView(bartDataView, i);
        }
    }

    //TODO: Check user data to see that they didn't do something stupid
    public List<UserBartData> extractNewData() {
        List<UserBartData> newUserData = new ArrayList<>();
        for (BartDataElemViewHolder viewHolder : bartDataElemViewHolders) {
            newUserData.add(viewHolder.getDataFromView());
        }

        try {
            newUserData = Utils.filterBadData(newUserData);
        } catch (IllegalArgumentException e) {
            Toast.makeText(getActivity(), "Error: duplicate station.", Toast.LENGTH_SHORT).show();
            return null;
        }

        if (Utils.didDataChange(userData, newUserData)) {
            userData = newUserData;
            Toast.makeText(getActivity(), "Changes saved!", Toast.LENGTH_SHORT).show();
            SPSingleton.getInstance(getActivity()).persistUserData(new Gson().toJson(newUserData));
            return userData;
        } else {
            Log.i("MyStationsFragment", "No changes made.");
            return null;
        }
    }
}
