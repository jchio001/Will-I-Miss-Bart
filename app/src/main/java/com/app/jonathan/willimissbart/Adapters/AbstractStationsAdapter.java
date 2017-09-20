package com.app.jonathan.willimissbart.Adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.app.jonathan.willimissbart.API.Models.StationModels.Station;
import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.ViewHolders.StationsCardViewHolder;
import com.app.jonathan.willimissbart.ViewHolders.StationsFooterViewHolder;

import java.util.List;

public abstract class AbstractStationsAdapter extends BaseAdapter {
    protected List<Station> stations;
    protected boolean pickingOrigin = true;

    protected AbstractStationsAdapter(List<Station> stations) {
        this.stations = stations;
    }

    @Override
    public int getCount() {
        return stations.size();
    }

    @Override
    public Station getItem(int position) {
        return stations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.station_grid_elem, parent, false);
            convertView.setTag(new StationsCardViewHolder(convertView));
        }
        return convertView;
    }
}
