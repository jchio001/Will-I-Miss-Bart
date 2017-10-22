package com.app.jonathan.willimissbart.Persistence;


import com.app.jonathan.willimissbart.API.Models.Station.Station;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class StationsSingleton {
    private static StationsSingleton instance;
    private List<Station> stations = Lists.newArrayList();

    private StationsSingleton() {
        stations = new ArrayList<>();
    }

    public static StationsSingleton getInstance() {
        if (instance == null) {
            instance = new StationsSingleton();
        }

        return instance;
    }

    public static List<Station> getStations() {
        return getInstance().stations;
    }

    public void setStations(List<Station> stations) {
        this.stations.addAll(stations);
    }
}
