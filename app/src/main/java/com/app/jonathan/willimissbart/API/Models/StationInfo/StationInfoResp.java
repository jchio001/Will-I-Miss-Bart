package com.app.jonathan.willimissbart.API.Models.StationInfo;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StationInfoResp implements Serializable {
    @SerializedName("root")
    @Expose
    private StationInfoRoot root;

    public StationInfoRoot getRoot() {
        return root;
    }
}
