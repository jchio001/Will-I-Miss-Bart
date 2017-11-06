package com.app.jonathan.willimissbart.Adapters;

import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.jonathan.willimissbart.API.Models.Etd.EtdRoot;
import com.app.jonathan.willimissbart.API.Models.Etd.EtdStation;
import com.app.jonathan.willimissbart.API.Models.Routes.Trip;
import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.ViewHolders.RouteViewHolder;
import com.google.common.collect.Lists;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class RoutesAdapter extends Adapter<RouteViewHolder> {
    public static final SimpleDateFormat format =
        new SimpleDateFormat("MM/dd/yyyy h:mm aa z", Locale.ENGLISH);

    private List<Trip> trips = Lists.newArrayList();
    private EtdStation routeEtdStation;
    private EtdStation returnRouteEtdStation;

    private long routeEtdStationTime = 0;
    private long returnRouteEtdStationTime = 0;

    public RoutesAdapter() {
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
            .inflate(R.layout.route_card_elem, parent, false);
        return new RouteViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RouteViewHolder holder, int position) {
        if (routeEtdStation != null
            && trips.get(position).getOrigin().equals(routeEtdStation.getAbbr())) {
            holder.setUp(trips.get(position), routeEtdStation, routeEtdStationTime);
        } else if (returnRouteEtdStation != null
            && trips.get(position).getOrigin().equals(returnRouteEtdStation.getAbbr())) {
            holder.setUp(trips.get(position), returnRouteEtdStation, returnRouteEtdStationTime);
        } else {
            holder.setUp(trips.get(position), null, 0);
        }
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

    public RoutesAdapter setRouteEtdStation(EtdRoot etdRoot) {
        this.routeEtdStation = etdRoot.getStations().get(0);
        this.routeEtdStationTime = etdRoot.getTimeAsEpochMs() / 1000;
        notifyDataSetChanged();
        return this;
    }

    public RoutesAdapter setReturnRouteEtdStation(EtdRoot etdRoot) {
        this.returnRouteEtdStation = etdRoot.getStations().get(0);
        this.returnRouteEtdStationTime = etdRoot.getTimeAsEpochMs() / 1000;
        notifyDataSetChanged();
        return this;
    }
}
