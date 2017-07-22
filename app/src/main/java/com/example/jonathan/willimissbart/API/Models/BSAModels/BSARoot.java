package com.example.jonathan.willimissbart.API.Models.BSAModels;

import com.example.jonathan.willimissbart.API.Models.Meta.UriData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class BsaRoot implements Serializable {
    @SerializedName("@id")
    @Expose
    private String id;

    @SerializedName("uri")
    @Expose
    private UriData uri;

    @SerializedName("bsa")
    @Expose
    private List<Bsa> bsaList;

    public String getId() {
        return id;
    }

    public UriData getUri() {
        return uri;
    }

    public List<Bsa> getBsaList() {
        return bsaList;
    }
}
