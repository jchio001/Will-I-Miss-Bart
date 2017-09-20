package com.app.jonathan.willimissbart.Adapters;

import android.view.View;
import android.view.ViewGroup;

import com.app.jonathan.willimissbart.API.Models.StationModels.Station;
import com.app.jonathan.willimissbart.ViewHolders.StationsCardViewHolder;

import java.util.List;

public class SingleElemStationsAdapter extends AbstractStationsAdapter {
    private boolean selectingOrigin = true;

    public SingleElemStationsAdapter(List<Station> stations, boolean selectingOrigin) {
        super(stations);
        this.selectingOrigin = selectingOrigin;
    }

    public SingleElemStationsAdapter setSelectingOrigin(boolean selectingOrigin) {
        this.selectingOrigin = selectingOrigin;
        return this;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = super.getView(position, convertView, parent);
        StationsCardViewHolder viewHolder = (StationsCardViewHolder) convertView.getTag();

        viewHolder.abbr.setText(stations.get(position).getAbbr());
        return convertView;
    }
}
