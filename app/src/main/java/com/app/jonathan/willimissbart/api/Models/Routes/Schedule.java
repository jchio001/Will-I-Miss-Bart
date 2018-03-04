package com.app.jonathan.willimissbart.api.Models.Routes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class Schedule implements Serializable {
    @SerializedName("request")
    @Expose
    private Request request;

    public Request getRequest() {
        return request;
    }
}
