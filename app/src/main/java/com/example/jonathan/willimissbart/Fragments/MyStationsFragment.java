package com.example.jonathan.willimissbart.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.jonathan.willimissbart.API.Models.StationModels.Station;
import com.example.jonathan.willimissbart.Adapters.SimpleLargeTextListAdapter;
import com.example.jonathan.willimissbart.Adapters.StringAdapter;
import com.example.jonathan.willimissbart.Dialogs.DeleteAlertDialog;
import com.example.jonathan.willimissbart.Enums.StyleEnum;
import com.example.jonathan.willimissbart.Misc.Utils;
import com.example.jonathan.willimissbart.Persistence.Models.UserBartData;
import com.example.jonathan.willimissbart.Persistence.SPSingleton;
import com.example.jonathan.willimissbart.Persistence.StationsSingleton;
import com.example.jonathan.willimissbart.R;
import com.example.jonathan.willimissbart.ViewHolders.BartDataElemViewHolder;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyStationsFragment extends Fragment
        implements DeleteAlertDialog.DeleteDataElemListener {
    OnDeleteViewListener deleteCallback;

    public interface OnDeleteViewListener {
        public void onDeleteView(int index);
    }

    @Bind(R.id.my_stations_parent) LinearLayout parent;
    @Bind(R.id.save) Button saveButton;

    private List<UserBartData> userData;
    private List<BartDataElemViewHolder> bartDataElemViewHolders = new ArrayList<>();
    private LayoutInflater vi;
    private SimpleLargeTextListAdapter<Station> stationsAdapter;
    private StringAdapter directionsAdapter;

    private long lastSaveTime;

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

        for (int i = 0; i < userData.size(); ++i) {
            View bartDataView = vi.inflate(R.layout.bart_data_elem, null);
            bartDataElemViewHolders.add(
                    new BartDataElemViewHolder(bartDataView, getActivity(), this, i)
                            .setBartSpinnerAdapter(stationsAdapter)
                            .setDirectionSpinnerAdapter(directionsAdapter)
                            .setStyle(StyleEnum.NO_STYLE)
                            .build(userData.get(i))
            );
            parent.addView(bartDataView, i);
        }

        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    //Let the fragment handle deletion since the order of the view holders needs to be maintained
    @Override
    public void deleteDataElem(int index) {
        Log.i("MyStationsFragment", String.format("Deleting %d", index));
        bartDataElemViewHolders.remove(index);
        for (int i = index + 1; i < bartDataElemViewHolders.size(); ++i) {
            bartDataElemViewHolders.get(i).decrementIndex();
        }
        parent.removeViewAt(index);
    }

    //TODO: Check user data to see that they didn't do something stupid
    @OnClick(R.id.save)
    public void onSave() {
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
