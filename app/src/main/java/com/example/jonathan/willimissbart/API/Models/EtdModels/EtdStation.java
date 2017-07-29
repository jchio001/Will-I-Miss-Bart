package com.example.jonathan.willimissbart.API.Models.EtdModels;

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

    public String getName() {
        return name;
    }

    public EtdStation setName(String name) {
        this.name = name;
        return this;
    }

    public String getAbbr() {
        return abbr;
    }

    public EtdStation setAbbr(String abbr) {
        this.abbr = abbr;
        return this;
    }

    public List<Etd> getEtds() {
        return etds;
    }
}
