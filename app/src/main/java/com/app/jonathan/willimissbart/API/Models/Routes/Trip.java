package com.app.jonathan.willimissbart.API.Models.Routes;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.collect.Lists;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class Trip implements Serializable, Parcelable {
    public static final SimpleDateFormat format =
        new SimpleDateFormat("MM/dd/yyyy h:mm aa", Locale.ENGLISH);

    @SerializedName("@origin")
    @Expose
    private String origin;

    @SerializedName("@destination")
    @Expose
    private String destination;

    @SerializedName("@origTimeMin")
    @Expose
    private String origTimeMin;

    @SerializedName("@origTimeDate")
    @Expose
    private String origTimeDate;

    @SerializedName("@destTimeMin")
    @Expose
    private String destTimeMin;

    @SerializedName("@tripTime")
    @Expose
    private Integer tripTime;

    @SerializedName("@fare")
    @Expose
    private String fare;

    @SerializedName("@clipper")
    @Expose
    private String clipper;

    @SerializedName("leg")
    @Expose
    private List<Leg> legList;

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Trip createFromParcel(Parcel source) {
            return new Trip(source);
        }

        @Override
        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(origin);
        dest.writeString(destination);
        dest.writeString(origTimeMin);
        dest.writeString(destTimeMin);
        dest.writeInt(tripTime);
        dest.writeString(fare);
        dest.writeString(clipper);
        dest.writeTypedList(legList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Trip(Parcel parcel) {
        this.origin = parcel.readString();
        this.destination = parcel.readString();
        this.origTimeMin = parcel.readString();
        this.destTimeMin = parcel.readString();
        this.tripTime = parcel.readInt();
        this.fare = parcel.readString();
        this.clipper = parcel.readString();
        this.legList = Lists.newArrayList();
        parcel.readTypedList(legList, Leg.CREATOR);
    }

    public String getOrigin() {
        return origin;
    }

    public Trip setOrigin(String origin) {
        this.origin = origin;
        return this;
    }

    public String getDestination() {
        return destination;
    }

    public Trip setDestination(String destination) {
        this.destination = destination;
        return this;
    }

    public String getOrigTimeMin() {
        return origTimeMin;
    }

    public String getOrigTimeDate() {
        return origTimeDate;
    }

    public String getDestTimeMin() {
        return destTimeMin;
    }

    public Integer getTripTime() {
        return tripTime;
    }

    public String getFare() {
        return fare;
    }

    public String getClipper() {
        return clipper;
    }

    public List<Leg> getLegList() {
        return legList;
    }

    public long getEpochTime() {
        try {
            return format.parse(origTimeDate + origTimeMin).getTime();
        } catch (ParseException e) {
            // For now, FAIL HARD.
            throw new RuntimeException(e);
        }
    }
}
