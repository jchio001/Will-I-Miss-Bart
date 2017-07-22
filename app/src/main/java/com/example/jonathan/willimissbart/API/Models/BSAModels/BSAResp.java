package com.example.jonathan.willimissbart.API.Models.BSAModels;

import com.example.jonathan.willimissbart.API.Models.Meta.XMLData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BsaResp implements Serializable {
    @SerializedName("root")
    @Expose
    private BsaRoot root;

    public BsaRoot getRoot() {
        return root;
    }
}
