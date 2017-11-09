package com.app.jonathan.willimissbart.Adapters;

import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.jonathan.willimissbart.API.Models.Etd.Estimate;
import com.app.jonathan.willimissbart.API.Models.Etd.EtdRespWrapper;
import com.app.jonathan.willimissbart.API.Models.Routes.Trip;
import com.app.jonathan.willimissbart.Persistence.Models.UserStationData;
import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.ViewHolders.RouteViewHolder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RoutesAdapter extends Adapter<RouteViewHolder> {
    public static final SimpleDateFormat format =
        new SimpleDateFormat("MM/dd/yyyy h:mm aa z", Locale.ENGLISH);

    private List<Trip> trips = Lists.newArrayList();
    private Map<String, List<Estimate>> origDestToEstimates = Maps.newHashMap();
    private String origAbbr;

    private long routeEtdStationTime = 0;
    private long returnRouteEtdStationTime = 0;

    public RoutesAdapter(UserStationData originData) {
        this.origAbbr = originData.getAbbr();
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
        Trip trip = trips.get(position);
        Estimate estimate;
        if (!origDestToEstimates.containsKey(trip.getOrigin()
            + trip.getLegList().get(0).getTrainHeadStation())) {
            estimate = null;
        } else {
            estimate = Iterables.getFirst(origDestToEstimates.get(trip.getOrigin()
                + trip.getLegList().get(0).getTrainHeadStation()), null);
        }

        holder.setUp(trip, estimate,
            trip.getOrigin().equals(origAbbr) ? routeEtdStationTime : returnRouteEtdStationTime);
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void addAll(List<Trip> trips) {
        this.routeEtdStationTime = 0;
        this.returnRouteEtdStationTime = 0;
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

    public RoutesAdapter populateOrigDestMappings(EtdRespWrapper etdRespWrap) {
        if (etdRespWrap.isReturnRoute()) {
            this.routeEtdStationTime = etdRespWrap.getRespTime();
        } else {
            this.returnRouteEtdStationTime = etdRespWrap.getRespTime();
        }

        this.origDestToEstimates.putAll(etdRespWrap.getOrigDestToEstimates());
        notifyDataSetChanged();
        return this;
    }
}
