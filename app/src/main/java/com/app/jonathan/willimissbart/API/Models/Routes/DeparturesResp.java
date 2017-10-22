package com.app.jonathan.willimissbart.API.Models.Routes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DeparturesResp implements Serializable {
    @SerializedName("root")
    @Expose
    private DeparturesRoot root;

    public DeparturesRoot getRoot() {
        return root;
    }
}
