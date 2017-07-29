package com.example.jonathan.willimissbart.Persistence.Models;


import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Arrays;

public class UserBartData implements Serializable {
    @SerializedName("station")
    @Expose
    private String station;

    //For spinners
    @SerializedName("stn_index")
    @Expose
    private int stationIndex;

    @SerializedName("abbr")
    @Expose
    private String abbr;

    @SerializedName("direction")
    @Expose
    private String direction;

    //Also for spinners
    @SerializedName("dir_index")
    @Expose
    private int directionIndex;

    @SerializedName("days")
    @Expose
    private boolean[] days;

    public UserBartData() {}

    //Deep comparison
    public boolean equals(UserBartData userBartData) {
        try {
            return station.equals(userBartData.getStation())
                    && stationIndex == userBartData.getStationIndex()
                    && abbr.equals(userBartData.getAbbr())
                    && direction.equals(userBartData.getDirection())
                    && directionIndex == userBartData.getDirectionIndex()
                    && Arrays.equals(days, userBartData.getDays());
        } catch (NullPointerException e) {
            Log.e("UserBartData", "NPE at equals()");
            return false;
        }
    }

    public String getStation() {
        return station;
    }

    public UserBartData setStation(String station) {
        this.station = station;
        return this;
    }

    public int getStationIndex() {
        return stationIndex;
    }

    public UserBartData setStationIndex(int stationIndex) {
        this.stationIndex = stationIndex;
        return this;
    }

    public String getAbbr() {
        return abbr;
    }

    public UserBartData setAbbr(String abbr) {
        this.abbr = abbr;
        return this;
    }

    public String getDirection() {
        return direction;
    }

    public UserBartData setDirection(String direction) {
        this.direction = direction;
        return this;
    }

    public int getDirectionIndex() {
        return directionIndex;
    }

    public UserBartData setDirectionIndex(int directionIndex) {
        this.directionIndex = directionIndex;
        return this;
    }

    public boolean[] getDays() {
        return days;
    }

    public UserBartData setDays(boolean[] days) {
        this.days = days;
        return this;
    }
}
