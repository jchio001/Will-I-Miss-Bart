package com.app.jonathan.willimissbart.API.Models.EtdModels;

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
    private String hexcolor;

    //pretty much always 1/true/whatever
    @SerializedName("bikeflag")
    @Expose
    private String bikeflag;

    public String getMinutes() {
        return minutes;
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

    public String getHexcolor() {
        return hexcolor;
    }

    public String getBikeflag() {
        return bikeflag;
    }
}
