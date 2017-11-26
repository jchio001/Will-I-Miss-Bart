package com.app.jonathan.willimissbart.Adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.app.jonathan.willimissbart.API.Models.Station.Station;
import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.ViewHolders.StationInfoViewHolder;
import com.app.jonathan.willimissbart.ViewHolders.StationsCardViewHolder;
import com.google.common.collect.Lists;

import java.util.List;

public class AbstractStationsAdapter extends BaseAdapter {
    protected List<Station> filteredStations;
    protected List<Station> stations;
    private StationInfoViewHolder stationInfoViewHolder;

    public AbstractStationsAdapter(List<Station> stations,
                                   StationInfoViewHolder stationInfoViewHolder) {
        this.filteredStations = Lists.newArrayList(stations);
        this.stations = stations;
        this.stationInfoViewHolder = stationInfoViewHolder;
    }

    @Override
    public final int getCount() {
        return filteredStations.size();
    }

    @Override
    public final Station getItem(int position) {
        return filteredStations.get(position);
    }

    @Override
    public final long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.station_grid_cell, parent, false);
            convertView.setTag(new StationsCardViewHolder(convertView, stationInfoViewHolder));
        }

        return convertView;
    }

    public final void filter(String text) {
        filteredStations.clear();
        for (Station station : stations) {
            if (station.getAbbr().startsWith(text)
                || station.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredStations.add(station);
            }
        }
        notifyDataSetChanged();
    }
}
