package com.app.jonathan.willimissbart.API.Models.StationInfo;

import com.app.jonathan.willimissbart.API.Models.Generic.CDataSection;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Station implements Serializable {
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

    @SerializedName("intro")
    @Expose
    private CDataSection intro;

    @SerializedName("food")
    @Expose
    private CDataSection food;

    @SerializedName("shopping")
    @Expose
    private CDataSection shopping;

    @SerializedName("attraction")
    @Expose
    private CDataSection attraction;

    public String getName() {
        return name;
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

    public CDataSection getIntro() {
        return intro;
    }

    public CDataSection getFood() {
        return food;
    }

    public CDataSection getShopping() {
        return shopping;
    }

    public CDataSection getAttraction() {
        return attraction;
    }

    public String getFullAddress() {
        return address + " " + city + ", " + state + " " + String.valueOf(zipCode);
    }
}
