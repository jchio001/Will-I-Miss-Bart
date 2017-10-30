package com.app.jonathan.willimissbart.API.Models.Routes;


import java.util.List;

public class TripsWrapper {
    private boolean isReturnRoute;
    private List<Trip> trips;

    public boolean isReturnRoute() {
        return isReturnRoute;
    }

    public TripsWrapper setIsReturnRoute(boolean isReturnRoute) {
        this.isReturnRoute = isReturnRoute;
        return this;
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public TripsWrapper setTrips(List<Trip> trips) {
        this.trips = trips;
        return this;
    }
}
