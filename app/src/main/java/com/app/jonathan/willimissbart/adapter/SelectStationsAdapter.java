package com.app.jonathan.willimissbart.adapter;


import android.view.View;
import android.view.ViewGroup;

import com.app.jonathan.willimissbart.api.Models.Station.Station;
import com.app.jonathan.willimissbart.viewholder.StationInfoViewHolder;
import com.app.jonathan.willimissbart.viewholder.StationsCardViewHolder;

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
