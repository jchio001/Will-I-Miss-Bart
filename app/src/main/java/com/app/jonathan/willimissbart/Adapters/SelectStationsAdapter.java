package com.app.jonathan.willimissbart.Adapters;


import android.view.View;
import android.view.ViewGroup;

import com.app.jonathan.willimissbart.API.Models.Station.Station;
import com.app.jonathan.willimissbart.ViewHolders.StationInfoViewHolder;
import com.app.jonathan.willimissbart.ViewHolders.StationsCardViewHolder;

import java.util.List;

public class SelectStationsAdapter extends AbstractStationsAdapter {
    public SelectStationsAdapter(List<Station> stations, StationInfoViewHolder stationInfoViewHolder) {
        super(stations, stationInfoViewHolder);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = super.getView(position, convertView, parent);
        StationsCardViewHolder viewHolder = (StationsCardViewHolder) convertView.getTag();
        viewHolder.originOrDest.setVisibility(View.GONE);
        viewHolder.abbr.setText(getItem(position).getAbbr());
        return convertView;
    }
}
