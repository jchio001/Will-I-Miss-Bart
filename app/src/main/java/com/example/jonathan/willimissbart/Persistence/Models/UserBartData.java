package com.example.jonathan.willimissbart.Persistence.Models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserBartData implements Serializable {
    @SerializedName("station")
    @Expose
    private String station;

    @SerializedName("abbr")
    @Expose
    private String abbr;

    @SerializedName("direction")
    @Expose
    private String direction;

    @SerializedName("days")
    @Expose
    private boolean[] days;

    public UserBartData() {}

    public String getStation() {
        return station;
    }

    public UserBartData setStation(String station) {
        this.station = station;
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

    public boolean[] getDays() {
        return days;
    }

    public UserBartData setDays(boolean[] days) {
        this.days = days;
        return this;
    }
}
