package com.example.jonathan.willimissbart.API.Models.EtdModels;


import com.example.jonathan.willimissbart.API.Models.Meta.XMLData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EtdResp implements Serializable{
    @SerializedName("root")
    @Expose
    private EtdRoot root;

    public EtdRoot getRoot() {
        return root;
    }
}
