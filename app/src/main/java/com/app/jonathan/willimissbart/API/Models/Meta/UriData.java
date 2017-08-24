package com.app.jonathan.willimissbart.API.Models.Meta;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UriData implements Serializable {
    @SerializedName("#cdata-section")
    @Expose
    private String cDataSection;
}
