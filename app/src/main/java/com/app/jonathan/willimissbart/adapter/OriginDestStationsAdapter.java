package com.app.jonathan.willimissbart.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.api.Models.Station.Station;
import com.app.jonathan.willimissbart.fragment.UserDataManager;
import com.app.jonathan.willimissbart.fragment.UserDataManager.UserDataSubscriber;
import com.app.jonathan.willimissbart.misc.NotGuava;
import com.app.jonathan.willimissbart.persistence.models.UserStationData;
import com.app.jonathan.willimissbart.viewholder.StationInfoViewHolder;
import com.app.jonathan.willimissbart.viewholder.StationsCardViewHolder;

import java.util.ArrayList;
import java.util.List;

public class OriginDestStationsAdapter extends AbstractStationsAdapter {

    private UserDataManager userDataManager;

    private boolean pickingOrigin = true;

    private UserDataSubscriber userDataSubscriber = this::notifyDataSetChanged;

    public OriginDestStationsAdapter(List<Station> stations,
                                     StationInfoViewHolder stationInfoViewHolder,
                                     UserDataManager userDataManager) {
        super(stations, stationInfoViewHolder);
        this.userDataManager = userDataManager;
        userDataManager.subscribe(userDataSubscriber);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = super.getView(position, convertView, parent);
        StationsCardViewHolder viewHolder = (StationsCardViewHolder) convertView.getTag();

        viewHolder.infoIcon.setVisibility(View.GONE);
        viewHolder.abbr.setText(getItem(position).getAbbr());
        int originalIndex = getItem(position).getIndex();
        if (originalIndex == userDataManager.getOriginStationData().getStationIndex()) {
            viewHolder.originOrDest.setText(R.string.stn_origin);
        } else if (originalIndex == userDataManager.getDestinationStationData().getStationIndex()) {
            viewHolder.originOrDest.setText(R.string.stn_dest);
        } else {
            viewHolder.originOrDest.setText(" ");
        }

        return convertView;
    }

    public void setOriginOrDest(int position) {
        ArrayList<UserStationData> userData = userDataManager.getUserDataCopy();

        int originIndex = userData.get(0).getStationIndex();
        int destIndex = userData.get(1).getStationIndex();

        if (pickingOrigin) {
            if (position != destIndex) {
                Station originStn = stations.get(position);
                userData.set(0, new UserStationData(
                    originStn.getName(),
                    originStn.getAbbr(),
                    position));
                pickingOrigin = false;
            } else {
                userData.set(1, UserStationData.UNSELECTED_DATA);
            }userDataManager.update(userData, false);
        } else {
            if (position == originIndex) {
                userData.set(0, UserStationData.UNSELECTED_DATA);
                pickingOrigin = true;
            } else if (position != destIndex) {
                Station destStation = stations.get(position);
                userData.set(1, new UserStationData(
                    destStation.getName(),
                    destStation.getAbbr(),
                    position));
            } else {
                userData.set(1, UserStationData.UNSELECTED_DATA);
            }
        }

        userDataManager.ignoreNextBroadcast(userDataSubscriber);
        userDataManager.update(userData, false);
        notifyDataSetChanged();
    }

    public ArrayList<UserStationData> getUserBartData() {
        int originIndex = userDataManager.getOriginStationData().getStationIndex();
        int destIndex = userDataManager.getDestinationStationData().getStationIndex();

        if (originIndex == -1 || destIndex == -1) {
            return null;
        }

        Station originStn = stations.get(originIndex);
        Station destStn = stations.get(destIndex);
        return NotGuava.newArrayList(
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
