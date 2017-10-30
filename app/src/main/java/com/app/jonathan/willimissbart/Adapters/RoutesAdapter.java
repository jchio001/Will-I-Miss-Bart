package com.app.jonathan.willimissbart.Adapters;

import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.jonathan.willimissbart.API.Models.Routes.Trip;
import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.ViewHolders.RouteViewHolder;
import com.google.common.collect.Lists;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class RoutesAdapter extends Adapter<RouteViewHolder> {
    private List<Trip> trips = Lists.newArrayList();

    public RoutesAdapter() {
    }

    public void addAll(List<Trip> trips) {
        this.trips.clear();
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
            .inflate(R.layout.route_list_elem, parent, false);
        return new RouteViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RouteViewHolder holder, int position) {
        holder.setUp(trips.get(position));
    }
}
