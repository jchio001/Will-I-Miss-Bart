package com.app.jonathan.willimissbart.Adapters;

import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.jonathan.willimissbart.API.Models.Etd.Estimate;
import com.app.jonathan.willimissbart.API.Models.Etd.EtdRespWrapper;
import com.app.jonathan.willimissbart.API.Models.Routes.Trip;
import com.app.jonathan.willimissbart.Misc.EstimatesListener;
import com.app.jonathan.willimissbart.Misc.EstimatesManager;
import com.app.jonathan.willimissbart.Persistence.Models.UserStationData;
import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.ViewHolders.RouteViewHolder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.List;

public class RoutesAdapter extends Adapter<RouteViewHolder> implements EstimatesListener {
    private List<Trip> trips = Lists.newArrayList();
    private String origAbbr;

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    @Override
    public RouteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.route_card_elem, parent, false);
        return new RouteViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RouteViewHolder holder, int position) {
        Trip trip = trips.get(position);
        Estimate estimate;
        if (!EstimatesManager.containsKey(trip.getOrigin()
            + trip.getLegList().get(0).getTrainHeadStation())) {
            estimate = null;
        } else {
            estimate = Iterables.getFirst(EstimatesManager.getEstimates(trip.getOrigin()
                + trip.getLegList().get(0).getTrainHeadStation()), null);
        }

        holder.setUp(trip, estimate, EstimatesManager.getEstimatesRespTime(origAbbr));
    }

    @Override
    public void onReceiveEstimates(EtdRespWrapper etdRespWrap) {
        notifyDataSetChanged();
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void addAll(List<Trip> trips, UserStationData originData) {
        this.origAbbr = originData.getAbbr();
        this.trips.clear();
        // EstimatesManager.clear();

        long now = System.currentTimeMillis();
        List<Trip> filtered = Lists.newArrayList();
        for (Trip trip : trips) {
            long tripTime = trip.getEpochTime();
            if (now < tripTime) {
                filtered.add(trip);
            }
        }

        this.trips.addAll(filtered);
        notifyDataSetChanged();
    }
}
