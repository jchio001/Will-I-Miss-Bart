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

import com.app.jonathan.willimissbart.API.Models.StationModels.Station;
import com.app.jonathan.willimissbart.Adapters.SimpleLargeTextListAdapter;
import com.app.jonathan.willimissbart.Adapters.StringAdapter;
import com.app.jonathan.willimissbart.Dialogs.DeleteAlertDialog;
import com.app.jonathan.willimissbart.Enums.StyleEnum;
import com.app.jonathan.willimissbart.Persistence.Models.UserStationData;
import com.app.jonathan.willimissbart.Persistence.StationsSingleton;
import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.ViewHolders.BartDataElemViewHolder;
import com.google.common.collect.Lists;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyStationsFragment extends Fragment
        implements DeleteAlertDialog.DeleteDataElemListener {
    @Bind(R.id.my_stations_parent) LinearLayout parent;
    @Bind(R.id.add) FloatingActionButton add;

    private List<UserStationData> userData = Lists.newArrayList();
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
        add.setImageDrawable(new IconDrawable(getActivity(), IoniconsIcons.ion_plus_round)
            .colorRes(R.color.white));

        vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        stationsAdapter = new SimpleLargeTextListAdapter(
                getActivity(),
                StationsSingleton.getInstance().getStations())
                .setStyle(StyleEnum.NO_STYLE);
        directionsAdapter = new StringAdapter(
                getActivity(),
                Arrays.asList(getResources().getStringArray(R.array.directions)))
                .setStyle(StyleEnum.NO_STYLE);

        Bundle bundle = getArguments();
        userData = Lists.newArrayList();
        loadFeed(userData);
        return v;
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

    public void loadFeed(List<UserStationData> data) {
        /*for (int i = 0; i < data.size(); ++i) {
            View bartDataView = vi.inflate(R.layout.bart_data_elem, null);
            bartDataElemViewHolders.add(
                    new BartDataElemViewHolder(bartDataView, getActivity(), this, i)
                            .setBartSpinnerAdapter(stationsAdapter)
                            .setDirectionSpinnerAdapter(directionsAdapter)
                            .setStyle(StyleEnum.NO_STYLE)
                            .build(data.get(i))
            );
            parent.addView(bartDataView, i);
        }*/
    }
}
