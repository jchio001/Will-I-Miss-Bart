package com.app.jonathan.willimissbart.fragment;

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

import com.app.jonathan.willimissbart.activity.core.MainActivity;
import com.app.jonathan.willimissbart.adapter.SelectStationsAdapter;
import com.app.jonathan.willimissbart.misc.Utils;
import com.app.jonathan.willimissbart.persistence.StationsManager;
import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.viewholder.StationGridViewHolder;
import com.app.jonathan.willimissbart.viewholder.StationInfoViewHolder;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class StationsFragment extends Fragment {

    @Bind(R.id.stn_grid_layout) LinearLayout stationGridLayout;
    @Bind(R.id.stn_info_parent) ScrollView stationInfoLayout;

    private SelectStationsAdapter adapter;
    private StationGridViewHolder stationGridViewHolder;
    private WeakReference<StationInfoViewHolder> stationInfoViewHolder;

    public static final int PERMISSIONS_CODE = 1;

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
        adapter = new SelectStationsAdapter(StationsManager.getStations(),
            stationInfoViewHolder.get());
        stationGridViewHolder = new StationGridViewHolder(
            stationGridLayout, adapter, PERMISSIONS_CODE, false);
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

    public void loadStation () {
        stationGridViewHolder.fetchLocationAndLoadClosestStation();
    }
}
