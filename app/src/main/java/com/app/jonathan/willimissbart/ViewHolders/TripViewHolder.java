package com.app.jonathan.willimissbart.ViewHolders;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.jonathan.willimissbart.API.Models.Etd.Estimate;
import com.app.jonathan.willimissbart.API.Models.Routes.Trip;
import com.app.jonathan.willimissbart.Activities.AppActivities.TripActivity;
import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TripViewHolder extends ViewHolder {
    @Bind(R.id.departures_layout) RelativeLayout departuresLayout;
    @Bind(R.id.trip_info_layout) LinearLayout tripInfoLayout;
    @Bind(R.id.trip_orig_to_dest) TextView origToDest;
    @Bind(R.id.trip_depart_time) TextView departureTime;
    @Bind(R.id.trip_arrive_time) TextView arrivalTime;
    @Bind(R.id.trip_fare_info) TextView fairInfo;

    private EstimateViewHolder estimateViewHolder;
    private Trip trip;

    private long timeOfResp = 0;

    public TripViewHolder(View v) {
        super(v);
        ButterKnife.bind(this, v);
        estimateViewHolder = new EstimateViewHolder(departuresLayout);
    }

    @OnClick(R.id.route_card)
    public void onExpandRoute() {
        if (trip != null) {
            Intent intent = new Intent(itemView.getContext(), TripActivity.class)
                .putExtra(Constants.TIME_OF_RESP, timeOfResp)
                .putExtra(Constants.TRIP, (Parcelable) trip);
            itemView.getContext().startActivity(intent);
        }
    }

    public void renderTrip(Trip trip, List<Estimate> estimates, long timeOfResp) {
        this.trip = trip;
        this.timeOfResp = timeOfResp;

        if (trip != null) {
            tripInfoLayout.setVisibility(View.VISIBLE);
            origToDest.setText(itemView.getContext()
                .getString(R.string.orig_to_dest, trip.getOrigin(), trip.getDestination()));
            departureTime.setText(itemView.getContext()
                .getString(R.string.departing, trip.getOrigTimeMin()));
            arrivalTime.setText(itemView.getContext()
                .getString(R.string.arriving, trip.getDestTimeMin()));
            fairInfo.setText(itemView.getContext()
                .getString(R.string.fare_info, trip.getFare(), trip.getClipper()));

            renderEstimate(estimates);
        }
    }

    public void renderEstimate(List<Estimate> estimates) {
        if (estimates == null) {
            estimateViewHolder.renderWithEstimateList(null, estimates, timeOfResp);
        } else {
            estimateViewHolder.renderWithEstimateList(trip.getLegList().get(0), estimates, timeOfResp);
        }
    }

    public void renderFailedTrip(String origin, String dest) {
        tripInfoLayout.setVisibility(View.GONE);
        origToDest.setText(itemView.getContext()
            .getString(R.string.failed_to_load_trip, origin, dest));
    }
}