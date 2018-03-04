package com.app.jonathan.willimissbart.api.Models.BSA;

import com.app.jonathan.willimissbart.api.Models.Generic.CDataSection;
import com.app.jonathan.willimissbart.api.Models.Generic.SimpleListItem;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class Bsa implements Serializable, SimpleListItem {
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

    @Override
    public String getTag() {
        return ((!station.isEmpty()) ? (station + ": ") : "") + description.getcDataSection();
    }

    public String getStation() {
        return station;
    }

    public CDataSection getDescription() {
        return description;
    }

    public String getPosted() {
        return posted;
    }

    public String getExpires() {
        return expires;
    }
}
