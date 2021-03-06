package com.app.jonathan.willimissbart.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.app.jonathan.willimissbart.api.Models.Station.Station;
import com.app.jonathan.willimissbart.viewholder.StationInfoViewHolder;
import com.app.jonathan.willimissbart.viewholder.StationsCardViewHolder;

import java.util.List;

public class SingleElemStationsAdapter extends AbstractStationsAdapter {

    private boolean selectingOrigin = true;

    public SingleElemStationsAdapter(List<Station> stations,
                                     StationInfoViewHolder stationInfoViewHolder,
                                     boolean selectingOrigin) {
        super(stations, stationInfoViewHolder);
        this.selectingOrigin = selectingOrigin;
    }

    public boolean isSelectingOrigin() {
        return selectingOrigin;
    }

    public SingleElemStationsAdapter setSelectingOrigin(boolean selectingOrigin) {
        this.selectingOrigin = selectingOrigin;
        return this;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = super.getView(position, convertView, parent);
        StationsCardViewHolder viewHolder = (StationsCardViewHolder) convertView.getTag();

        viewHolder.abbr.setText(filteredStations.get(position).getAbbr());
        return convertView;
    }
}
