package com.app.jonathan.willimissbart.Adapters;

import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.jonathan.willimissbart.API.Models.Etd.Estimate;
import com.app.jonathan.willimissbart.API.Models.Etd.EtdRespWrapper;
import com.app.jonathan.willimissbart.API.Models.Routes.Trip;
import com.app.jonathan.willimissbart.Misc.EstimatesManager;
import com.app.jonathan.willimissbart.Misc.NotGuava;
import com.app.jonathan.willimissbart.Persistence.Models.UserStationData;
import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.ViewHolders.TripViewHolder;

import java.util.List;

public class TripsAdapter extends Adapter<TripViewHolder> implements EstimatesManager.EstimatesListener {
    private static int RENDER_TRIP = 0;
    private static int RENDER_FAILED_TRIPS = 1;
    private static int RENDER_FAILED_RETURN_TRIPS = 2;

    private List<Trip> trips = NotGuava.newArrayList();
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
        int renderingState = getRenderingState(trip, position);

        if (renderingState == RENDER_TRIP) {
            holder.renderTrip(trip, getEstimatesForTripsFirstLeg(trip),
                EstimatesManager.getEstimatesRespTime(origAbbr));
        } else if (renderingState == RENDER_FAILED_TRIPS) {
            holder.renderFailedTrip(origAbbr, destAbbr);
        } else if (renderingState == RENDER_FAILED_RETURN_TRIPS) {
            holder.renderFailedTrip(destAbbr, origAbbr);
        } else {
            throw new IllegalArgumentException("Invalid rendering state.");
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

    private int getRenderingState(Trip trip, int position) {
        if (trip != null) {
            return RENDER_TRIP;
        } else {
            return position == 0 ? RENDER_FAILED_TRIPS : RENDER_FAILED_RETURN_TRIPS;
        }
    }

    public void addAll(List<Trip> trips, List<UserStationData> userData) {
        this.origAbbr = userData.get(0).getAbbr();
        this.destAbbr = userData.get(1).getAbbr();
        this.trips.clear();
        this.trips.addAll(trips);
        notifyDataSetChanged();
    }

    public List<Trip> getTrips() {
        return trips;
    }
}
