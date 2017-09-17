package com.app.jonathan.willimissbart.Persistence;


import com.app.jonathan.willimissbart.API.Models.StationModels.Station;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class StationsSingleton {
    private static StationsSingleton instance;
    private List<Station> stationElems = Lists.newArrayList();

    private StationsSingleton() {
        stationElems = new ArrayList<>();
    }

    public static StationsSingleton getInstance() {
        if (instance == null) {
            instance = new StationsSingleton();
        }

        return instance;
    }

    public static void setInstance(StationsSingleton instance) {
        StationsSingleton.instance = instance;
    }

    public List<Station> getStationElems() {
        return stationElems;
    }

    public void setStationElems(List<Station> stationElems) {
        this.stationElems.addAll(stationElems);
    }
}
