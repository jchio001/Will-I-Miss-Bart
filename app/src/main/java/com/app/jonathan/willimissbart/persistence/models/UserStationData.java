package com.app.jonathan.willimissbart.persistence.models;


import android.os.Parcel;
import android.os.Parcelable;

import com.app.jonathan.willimissbart.api.Models.Station.Station;
import com.app.jonathan.willimissbart.persistence.StationsManager;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserStationData implements Serializable, Parcelable {
    @SerializedName("station")
    @Expose
    private String station;

    @SerializedName("abbr")
    @Expose
    private String abbr;

    @SerializedName("stn_index")
    @Expose
    private int stationIndex;

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public UserStationData createFromParcel(Parcel source) {
            return new UserStationData(source);
        }

        @Override
        public UserStationData[] newArray(int size) {
            return new UserStationData[size];
        }
    };

    public UserStationData() {}

    public UserStationData(Parcel in) {
        this.station = in.readString();
        this.abbr = in.readString();
        this.stationIndex = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(station);
        dest.writeString(abbr);
        dest.writeInt(stationIndex);
    }

    public String getStation() {
        return station;
    }

    public UserStationData setStation(String station) {
        this.station = station;
        return this;
    }

    public int getStationIndex() {
        return stationIndex;
    }

    public UserStationData setStationIndex(int stationIndex) {
        this.stationIndex = stationIndex;
        return this;
    }

    public String getAbbr() {
        return abbr;
    }

    public UserStationData setAbbr(String abbr) {
        this.abbr = abbr;
        return this;
    }

    public static UserStationData fromStationIndex(int stationIndex) {
        Station station = StationsManager.getStations().get(stationIndex);
        return new UserStationData()
            .setStation(station.getName())
            .setAbbr(station.getAbbr())
            .setStationIndex(stationIndex);
    }

}
