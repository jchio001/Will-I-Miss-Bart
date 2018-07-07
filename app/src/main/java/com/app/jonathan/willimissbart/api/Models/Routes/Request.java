package com.app.jonathan.willimissbart.api.Models.Routes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Request implements Serializable {
    @SerializedName("trip")
    @Expose
    private ArrayList<Trip> trips;

    public ArrayList<Trip> getTrips() {
        return trips;
    }
}