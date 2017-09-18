package com.app.jonathan.willimissbart.API.Models.EtdModels;

import com.app.jonathan.willimissbart.Persistence.Models.UserStationData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class EtdStation implements Serializable {
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("abbr")
    @Expose
    private String abbr;

    @SerializedName("etd")
    @Expose
    private List<Etd> etds;

    public EtdStation(UserStationData data) {
        this.name = data.getStation();
        this.abbr = data.getAbbr();
    }

    public String getName() {
        return name;
    }

    public String getAbbr() {
        return abbr;
    }

    public List<Etd> getEtds() {
        return etds;
    }
}
