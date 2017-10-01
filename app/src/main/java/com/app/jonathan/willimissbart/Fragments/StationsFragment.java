package com.app.jonathan.willimissbart.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.app.jonathan.willimissbart.Activities.AppActivities.MainActivity;
import com.app.jonathan.willimissbart.Adapters.StationsAdapter;
import com.app.jonathan.willimissbart.Misc.Utils;
import com.app.jonathan.willimissbart.Persistence.StationsSingleton;
import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.ViewHolders.StationGridViewHolder;
import com.app.jonathan.willimissbart.ViewHolders.StationInfoViewHolder;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class StationsFragment extends Fragment {
    @Bind(R.id.stn_grid_layout) LinearLayout stationGridLayout;
    @Bind(R.id.stn_info_parent) ScrollView stationInfoLayout;

    private StationsAdapter adapter;
    private StationGridViewHolder stationGridViewHolder;
    private WeakReference<StationInfoViewHolder> stationInfoViewHolder;

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.i("StationsFragment", "OnCreateView");
        View v = inflater.inflate(R.layout.fragment_stations, container, false);
        ButterKnife.bind(this, v);

        stationInfoViewHolder = new WeakReference<>(
            ((MainActivity) getActivity()).getStationInfoViewHolder());
        adapter = new StationsAdapter(StationsSingleton.getStations(),
            stationInfoViewHolder.get());
        stationGridViewHolder = new StationGridViewHolder(stationGridLayout, adapter, false);
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnItemClick(R.id.stn_grid)
    public void onStationSelected(AdapterView<?> parent, int position) {
        if (stationInfoViewHolder != null && stationInfoViewHolder.get() != null) {
            Utils.hideKeyboard(getActivity());
            stationInfoViewHolder.get().show(adapter.getItem(position).getAbbr());
        }
    }
}
