package com.app.jonathan.willimissbart.API.Models.Routes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DeparturesRoot implements Serializable {
    @SerializedName("schedule")
    @Expose
    private Schedule schedule;

    public Schedule getSchedule() {
        return schedule;
    }
}
