package com.app.jonathan.willimissbart.api.Models.Station;

import com.app.jonathan.willimissbart.api.Models.Generic.SimpleListItem;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Station implements Serializable, SimpleListItem {
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("abbr")
    @Expose
    private String abbr;

    @SerializedName("gtfs_latitude")
    @Expose
    private double latitude;

    @SerializedName("gtfs_longitude")
    @Expose
    private double longitude;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("city")
    @Expose
    private String city;

    @SerializedName("county")
    @Expose
    private String county;

    @SerializedName("state")
    @Expose
    private String state;

    @SerializedName("zipcode")
    @Expose
    private int zipCode;

    @SerializedName("index")
    @Expose
    private int index = -1;

    @Override
    public String getTag() {
        return abbr;
    }

    public String getName() {
        return name;
    }

    public String getAbbr() {
        return abbr;
    }

    public Station setAbbr(String abbr) {
        this.abbr = abbr;
        return this;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getCounty() {
        return county;
    }

    public String getState() {
        return state;
    }

    public int getZipCode() {
        return zipCode;
    }

    public int getIndex() {
        return index;
    }

    public Station setIndex(int index) {
        this.index = index;
        return this;
    }
}
