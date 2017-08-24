package com.app.jonathan.willimissbart.API.Models.Generic;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Message implements Serializable {
    @SerializedName("warning")
    @Expose
    private String warming;

    public String getWarming() {
        return warming;
    }
}
