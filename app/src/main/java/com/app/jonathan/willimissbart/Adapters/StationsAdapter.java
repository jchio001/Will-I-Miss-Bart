package com.app.jonathan.willimissbart.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.app.jonathan.willimissbart.API.Models.StationModels.Station;
import com.app.jonathan.willimissbart.Persistence.Models.UserStationData;
import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.ViewHolders.StationsCardViewHolder;
import com.app.jonathan.willimissbart.ViewHolders.StationsFooterViewHolder;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class StationsAdapter extends BaseAdapter {
    private List<Station> stations;
    private StationsFooterViewHolder footer;

    private boolean pickingOrigin = true;
    private int originIndex = -1;
    private int destIndex = -1;

    public StationsAdapter(List<Station> stations, StationsFooterViewHolder footer) {
        this.stations = stations;
        this.footer = footer.setAdapter(this);
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
        StationsCardViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.station_grid_elem, parent, false);
            viewHolder = new StationsCardViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (StationsCardViewHolder) convertView.getTag();
        }

        viewHolder.abbr.setText(stations.get(position).getAbbr());

        if (position == originIndex) {
            viewHolder.originOrDest.setText(R.string.stn_origin);
        } else if (position == destIndex) {
            viewHolder.originOrDest.setText(R.string.stn_dest);
        } else {
            viewHolder.originOrDest.setText(" ");
        }

        return convertView;
    }

    public void setOriginOrDest(int position) {
        if (pickingOrigin && position != destIndex) {
            originIndex = position;
            pickingOrigin = false;
        } else if (pickingOrigin && position == destIndex) {
            destIndex = -1;
        } else if (!pickingOrigin && position == originIndex){
            originIndex = -1;
            pickingOrigin = true;
        } else if (!pickingOrigin && position != destIndex){
            destIndex = position;
        } else {
            destIndex = -1;
        }

        notifyDataSetChanged();
        footer.updateFooterText(
            originIndex >= 0 ? stations.get(originIndex).getAbbr() : "",
            destIndex >= 0 ? stations.get(destIndex).getAbbr() : "");
    }

    public void swap() {
        int tmp = originIndex;
        originIndex = destIndex;
        destIndex = tmp;

        notifyDataSetChanged();
        footer.updateFooterText(
            originIndex >= 0 ? stations.get(originIndex).getAbbr() : "",
            destIndex >= 0 ? stations.get(destIndex).getAbbr() : "");
    }

    public ArrayList<UserStationData> getUserBartData() {
        if (originIndex == -1 || destIndex == -1) {
            return null;
        }

        Station originStn = stations.get(originIndex);
        Station destStn = stations.get(destIndex);
        return Lists.newArrayList(
            new UserStationData()
                .setStation(originStn.getName())
                .setAbbr(originStn.getAbbr())
                .setStationIndex(originIndex),
            new UserStationData()
                .setStation(destStn.getName())
                .setAbbr(destStn.getAbbr())
                .setStationIndex(destIndex));
    }
}
