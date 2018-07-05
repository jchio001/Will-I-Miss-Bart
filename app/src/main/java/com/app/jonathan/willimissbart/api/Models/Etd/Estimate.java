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

    public Estimate(String minutes,
                    String platform,
                    String direction,
                    String length,
                    String color,
                    String hexColor,
                    String bikeFlag) {
        this.minutes = minutes;
        this.platform = platform;
        this.direction = direction;
        this.length = length;
        this.color = color;
        this.hexColor = hexColor;
        this.bikeFlag = bikeFlag;
    }

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
        if (minutes.equals("Leaving") || minutes.equals("0")) {
            return "Leaving now!";
        } else {
            if (minutes.equals("1")) {
                return "1 minute";
            } else {
                return minutes + " minutes";
            }
        }
    }

    // This does a DEEP COPY!
    // Yes I could save some processing power just by changing the field, but that could lead to
    // some nasty now and in the future.
    public Estimate updateMinutes(String updatedMinutes) {
        return new Estimate(updatedMinutes, platform, direction, length, color, hexColor, bikeFlag);
    }
}
