package com.app.jonathan.willimissbart.ViewHolders;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.TextView;

import com.app.jonathan.willimissbart.API.Models.Routes.Trip;
import com.app.jonathan.willimissbart.Activities.AppActivities.TripActivity;
import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RouteViewHolder extends ViewHolder {
    @Bind(R.id.route_origin) TextView origin;
    @Bind(R.id.route_dest) TextView dest;
    @Bind(R.id.route_depart_time) TextView departureTime;
    @Bind(R.id.route_arrive_time) TextView arrivalTime;
    @Bind(R.id.route_fare_info) TextView fairInfo;

    private Trip trip;

    public RouteViewHolder(View v) {
        super(v);
        ButterKnife.bind(this, v);
    }

    public void setUp(Trip trip) {
        this.trip = trip;
        origin.setText(trip.getOrigin());
        dest.setText(trip.getDestination());
        departureTime.setText(itemView.getContext()
            .getString(R.string.departing, trip.getOrigTimeMin()));
        arrivalTime.setText(itemView.getContext()
            .getString(R.string.arriving, trip.getDestTimeMin()));
        fairInfo.setText(itemView.getContext()
            .getString(R.string.fare_info, trip.getFare(), trip.getClipper()));
    }

    @OnClick(R.id.route_card)
    public void onExpandRoute() {
        Intent intent = new Intent(itemView.getContext(), TripActivity.class);
        intent.putExtra(Constants.TRIP, (Parcelable) trip);
        itemView.getContext().startActivity(intent);
    }
}