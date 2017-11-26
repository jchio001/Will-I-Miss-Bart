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
import com.app.jonathan.willimissbart.ViewHolders.TripViewHolder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.List;

public class TripsAdapter extends Adapter<TripViewHolder> implements EstimatesListener {
    private List<Trip> trips = Lists.newArrayList();
    private String origAbbr = "";
    private String destAbbr = "";

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    @Override
    public TripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.trip_card_cell, parent, false);
        return new TripViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TripViewHolder holder, int position) {
        Trip trip = trips.get(position);
        if (trip == null) {
            if (position == 0) {
                holder.renderFailedTrip(origAbbr, destAbbr);
            } else {
                holder.renderFailedTrip(destAbbr, origAbbr);
            }
        } else {
            holder.renderTrip(trip, getEstimatesForTripsFirstLeg(trip),
                EstimatesManager.getEstimatesRespTime(origAbbr));
        }
    }

    @Override
    public void onReceiveEstimates(EtdRespWrapper etdRespWrap) {
        notifyDataSetChanged();
    }

    @Override
    public void onEstimatesUpdated() {
        notifyDataSetChanged();
    }

    // For the feed of trips, we also want to provide a real time estimate for the first train
    // of the first leg of a trip. This method will grab us that estimate or return null if it's
    // not found.
    private List<Estimate> getEstimatesForTripsFirstLeg(Trip trip) {
        List<Estimate> estimates = EstimatesManager
            .getEstimates(trip.getOrigin() + trip.getLegList().get(0).getTrainHeadStation());

        return estimates == null ? null : estimates;
    }

    public void addAll(List<Trip> trips, List<UserStationData> userData) {
        this.origAbbr = userData.get(0).getAbbr();
        this.destAbbr = userData.get(1).getAbbr();
        this.trips.clear();

        long now = System.currentTimeMillis();
        List<Trip> filtered = Lists.newArrayList();
        for (Trip trip : trips) {
            if (trip != null) {
                long tripTime = trip.getEpochTime();
                if (now < tripTime) {
                    filtered.add(trip);
                }
            } else {
                filtered.add(trip);
            }
        }

        this.trips.addAll(filtered);
        notifyDataSetChanged();
    }

    public List<Trip> getTrips() {
        return trips;
    }
}
