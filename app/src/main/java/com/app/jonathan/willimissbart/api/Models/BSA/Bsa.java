package com.app.jonathan.willimissbart.api.Models.BSA;

import com.app.jonathan.willimissbart.api.Models.Generic.CDataSection;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class Bsa implements Serializable {
    @SerializedName("station")
    @Expose
    private String station;

    @SerializedName("description")
    @Expose
    private CDataSection description;

    @SerializedName("posted")
    @Expose
    private String posted;

    @SerializedName("expires")
    @Expose
    private String expires;

    public String getFormattedMessage() {
        return ((station != null && !station.isEmpty()) ? (station + ": ") : "") + description.getcDataSection();
    }

    public String getStation() {
        return station;
    }

    public Bsa setStation(String station) {
        this.station = station;
        return this;
    }

    public CDataSection getDescription() {
        return description;
    }

    public Bsa setDescription(CDataSection description) {
        this.description = description;
        return this;
    }

    public String getPosted() {
        return posted;
    }

    public String getExpires() {
        return expires;
    }
}
