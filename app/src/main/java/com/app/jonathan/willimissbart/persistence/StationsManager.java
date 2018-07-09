package com.app.jonathan.willimissbart.persistence;

import com.app.jonathan.willimissbart.api.Models.Station.Station;
import com.app.jonathan.willimissbart.misc.NotGuava;

import java.util.List;

public class StationsManager {

    private static StationsManager instance;
    private List<Station> stations = NotGuava.newArrayList();

    public static StationsManager get() {
        if (instance == null) {
            instance = new StationsManager();
        }

        return instance;
    }

    public List<Station> getStations() {
        return get().stations;
    }

    public void setStations(List<Station> stations) {
        get().stations.addAll(stations);
    }
}
