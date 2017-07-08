package com.example.jonathan.willimissbart.API.Models.StationModels;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class XMLData implements Serializable {
    @SerializedName("@version")
    @Expose
    private String version;

    @SerializedName("@encoding")
    @Expose
    private String encoding;

    public String getVersion() {
        return version;
    }

    public String getEncoding() {
        return encoding;
    }
}
