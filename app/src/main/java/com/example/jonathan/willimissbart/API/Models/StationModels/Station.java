package com.example.jonathan.willimissbart.API.Models.StationModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Station {
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

    public String getName() {
        return name;
    }

    public Station setName(String name) {
        this.name = name;
        return this;
    }

    public String getAbbr() {
        return abbr;
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

    public String getCounty() {
        return county;
    }

    public String getState() {
        return state;
    }

    public int getZipCode() {
        return zipCode;
    }
}
