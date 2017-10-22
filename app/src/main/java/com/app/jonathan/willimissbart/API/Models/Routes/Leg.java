package com.app.jonathan.willimissbart.API.Models.Routes;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Leg implements Serializable, Parcelable {
    @SerializedName("@origin")
    @Expose
    private String origin;

    @SerializedName("@destination")
    @Expose
    private String destination;

    @SerializedName("@origTimeMin")
    @Expose
    private String origTimeMin;

    @SerializedName("@destTimeMin")
    @Expose
    private String destTimeMin;

    @SerializedName("@line")
    @Expose
    private String line;

    @SerializedName("@bikeflag")
    @Expose
    private String bikeFlag;

    @SerializedName("@trainHeadStation")
    @Expose
    private String trainHeadStation; // final station on the leg

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Leg createFromParcel(Parcel source) {
            return new Leg(source);
        }

        @Override
        public Leg[] newArray(int size) {
            return new Leg[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(origin);
        dest.writeString(destination);
        dest.writeString(origTimeMin);
        dest.writeString(destTimeMin);
        dest.writeString(line);
        dest.writeString(bikeFlag);
        dest.writeString(trainHeadStation);
    }

    public Leg(Parcel parcel) {
        this.origin = parcel.readString();
        this.destination = parcel.readString();
        this.origTimeMin = parcel.readString();
        this.destTimeMin = parcel.readString();
        this.line = parcel.readString();
        this.bikeFlag = parcel.readString();
        this.trainHeadStation = parcel.readString();
    }

    public String getOrigin() {
        return origin;
    }

    public Leg setOrigin(String origin) {
        this.origin = origin;
        return this;
    }

    public String getDestination() {
        return destination;
    }

    public Leg setDestination(String destination) {
        this.destination = destination;
        return this;
    }

    public String getOrigTimeMin() {
        return origTimeMin;
    }

    public String getDestTimeMin() {
        return destTimeMin;
    }

    public String getLine() {
        return line;
    }

    public String getBikeFlag() {
        return bikeFlag;
    }

    public String getTrainHeadStation() {
        return trainHeadStation;
    }
}
