package com.example.jonathan.willimissbart.API.Models.BSAModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class CDataSection implements Serializable {
    @SerializedName("#cdata-section")
    @Expose
    private String cDataSection;

    public String getcDataSection() {
        return cDataSection;
    }
}
