package com.app.jonathan.willimissbart.persistence;

import com.app.jonathan.willimissbart.api.Models.Station.Station;
import com.app.jonathan.willimissbart.misc.NotGuava;

import java.util.List;

public class StationsManager {

    private static StationsManager instance;
    private List<Station> stations = NotGuava.newArrayList();

    public static StationsManager getInstance() {
        if (instance == null) {
            instance = new StationsManager();
        }

        return instance;
    }

    public static List<Station> getStations() {
        return getInstance().stations;
    }

    public static void setStations(List<Station> stations) {
        getInstance().stations.addAll(stations);
    }
}
