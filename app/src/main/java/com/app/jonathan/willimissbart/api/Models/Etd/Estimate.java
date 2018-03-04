package com.app.jonathan.willimissbart.api.Models.Etd;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Estimate implements Serializable {
    @SerializedName("minutes")
    @Expose
    private String minutes;

    @SerializedName("platform")
    @Expose
    private String platform;

    @SerializedName("direction")
    @Expose
    private String direction;

    @SerializedName("length")
    @Expose
    private String length;

    @SerializedName("color")
    @Expose
    private String color;

    @SerializedName("hexcolor")
    @Expose
    private String hexColor;

    //pretty much always 1/true/whatever
    @SerializedName("bikeflag")
    @Expose
    private String bikeFlag;

    public String getMinutes() {
        return minutes;
    }

    public void setMinutes(String minutes) {
        this.minutes = minutes;
    }

    public String getPlatform() {
        return platform;
    }

    public String getDirection() {
        return direction;
    }

    public String getLength() {
        return length;
    }

    public String getColor() {
        return color;
    }

    public String getHexColor() {
        return hexColor;
    }

    public String getBikeFlag() {
        return bikeFlag;
    }

    public String getEstimateAsString() {
        if (minutes.equals("Leaving")) {
            return "Leaving now!";
        } else {
            if (minutes.equals("1")) {
                return "1 minute";
            } else {
                return minutes + " minutes";
            }
        }
    }
}
