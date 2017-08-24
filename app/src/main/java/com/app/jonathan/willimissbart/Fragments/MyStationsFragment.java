package com.app.jonathan.willimissbart.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.app.jonathan.willimissbart.API.Models.StationModels.Station;
import com.app.jonathan.willimissbart.Adapters.SimpleLargeTextListAdapter;
import com.app.jonathan.willimissbart.Adapters.StringAdapter;
import com.app.jonathan.willimissbart.Dialogs.DeleteAlertDialog;
import com.app.jonathan.willimissbart.Enums.StyleEnum;
import com.app.jonathan.willimissbart.Misc.Utils;
import com.app.jonathan.willimissbart.Persistence.Models.UserBartData;
import com.app.jonathan.willimissbart.Persistence.SPSingleton;
import com.app.jonathan.willimissbart.Persistence.StationsSingleton;
import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.ViewHolders.BartDataElemViewHolder;
import com.app.jonathan.willimissbart.ViewHolders.OptionsElemViewHolder;
import com.google.common.collect.Lists;
import com.google.gson.Gson;

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
    @Bind(R.id.options) LinearLayout optionsLayout;
    @Bind(R.id.add_widget) LinearLayout addWidget;
    @Bind(R.id.undo_widget) LinearLayout undoWidget;
    @Bind(R.id.save_widget) LinearLayout saveWidget;

    private List<UserBartData> userData;
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
                Utils.getUserBartData(bundle, getActivity().getApplicationContext())
        );

        loadFeed(userData);

        new OptionsElemViewHolder(addWidget, R.string.fa_plus, "Add");
        new OptionsElemViewHolder(undoWidget, R.string.fa_undo, "Reset");
        new OptionsElemViewHolder(saveWidget, R.string.fa_save, "Save");

        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    // Let the fragment handle deletion since the order of the view holders needs to be maintained
    @Override
    public void deleteDataElem(int index) {
        Log.i("MyStationsFragment", String.format("Deleting %d", index));
        for (int i = index + 1; i < bartDataElemViewHolders.size(); ++i) {
            bartDataElemViewHolders.get(i).decrementIndex();
        }
        bartDataElemViewHolders.remove(index);
        parent.removeViewAt(index + 1);
    }

    // Associating all 3 items with the same onclick listener so that they can all be debounced
    // together
    @OnClick({R.id.add_widget, R.id.undo_widget, R.id.save_widget})
    public void onOptionChosen(View v) {
        if (v.getId() == R.id.add_widget) {
            addDataElem();
        } else if (v.getId() == R.id.undo_widget) {
            loadFeed(userData);
        } else {
            save();
        }
    }

    // Loading the feed is now smart enough to reuse views! We did it reddit!
    public void loadFeed(List<UserBartData> data) {
        // Loop 1: Reuse existing bartDataElemViewHolders
        int i = 0;
        for (; i < Math.min(data.size(), bartDataElemViewHolders.size()); ++i) {
            bartDataElemViewHolders.get(i).build(data.get(i));
        }

        // Loop 2: Create new bartDataElemViewHolders when there's not enough
        for (; i < data.size(); ++i) {
            View bartDataView = vi.inflate(R.layout.bart_data_elem, null);
            bartDataElemViewHolders.add(
                    new BartDataElemViewHolder(bartDataView, getActivity(), this, i)
                            .setBartSpinnerAdapter(stationsAdapter)
                            .setDirectionSpinnerAdapter(directionsAdapter)
                            .setStyle(StyleEnum.NO_STYLE)
                            .build(data.get(i))
            );
            parent.addView(bartDataView, i + 1);
        }

        // Loop 3: Delete excess bartDataElemViewHolders. It could be argued that I can just keep
        // them in memory, but it'll make it more difficult maintaining the information
        // since I need to utilize more indexes in order to do so
        int numViewsToRemove = bartDataElemViewHolders.size() - data.size();
        for (int j = 0; j < numViewsToRemove; ++j) {
            bartDataElemViewHolders.remove(data.size());
            parent.removeViewAt(data.size() + 1);
        }
    }

    public void addDataElem() {
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
            parent.addView(bartDataView, bartDataElemViewHolders.size());
        }
    }

    //TODO: Check user data to see that they didn't do something stupid
    public void save() {
        long now = System.currentTimeMillis() / 1000;
        if (now - lastSaveTime < 60) {
            Log.i("MyStationsFragment", "Stop spamming idiot.");
            Toast.makeText(getActivity(), "Please try again later.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<UserBartData> newUserData = new ArrayList<>();
        for (BartDataElemViewHolder viewHolder : bartDataElemViewHolders) {
            newUserData.add(viewHolder.getDataFromView());
        }

        try {
            newUserData = Utils.filterBadData(newUserData);
        } catch (IllegalArgumentException e) {
            Toast.makeText(getActivity(), "Error: duplicate station.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Utils.didDataChange(userData, newUserData)) {
            userData = newUserData;
            SPSingleton.getInstance(getActivity()).persistUserData(new Gson().toJson(newUserData));
            EventBus.getDefault().post(userData);
            Toast.makeText(getActivity(), "Updated your data.", Toast.LENGTH_SHORT).show();
            lastSaveTime = now;
        } else {
            Toast.makeText(getActivity(), "No changes made.", Toast.LENGTH_SHORT).show();
        }
    }
}
