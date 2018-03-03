package com.app.jonathan.willimissbart.Persistence;

import com.app.jonathan.willimissbart.API.Models.Station.Station;
import com.app.jonathan.willimissbart.Misc.NotGuava;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.subjects.ReplaySubject;

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
