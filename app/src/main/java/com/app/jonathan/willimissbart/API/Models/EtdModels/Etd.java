package com.app.jonathan.willimissbart.API.Models.EtdModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Etd implements Serializable {
    @SerializedName("destination")
    @Expose
    private String destination;

    @SerializedName("abbreviation")
    @Expose
    private String abbreviation;

    @SerializedName("limited")
    @Expose
    private String limited;

    @SerializedName("estimate")
    @Expose
    private List<Estimate> estimates;

    public String getDestination() {
        return destination;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getLimited() {
        return limited;
    }

    public List<Estimate> getEstimates() {
        return estimates;
    }
}
