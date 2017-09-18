package com.app.jonathan.willimissbart.Persistence.Models;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Arrays;

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

    //Deep comparison TODO Remove this
    public boolean equals(UserStationData userStationData) {
        if (userStationData == null) {
            Log.e("UserStationData", "passed in data is null");
            return false;
        }

        return station.equals(userStationData.getStation())
            && abbr.equals(userStationData.getAbbr())
            && stationIndex == userStationData.getStationIndex();
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

}
