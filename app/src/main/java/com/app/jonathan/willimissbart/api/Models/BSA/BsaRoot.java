package com.app.jonathan.willimissbart.api.Models.BSA;

import com.app.jonathan.willimissbart.api.Models.Generic.CDataSection;
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
    private CDataSection uri;

    @SerializedName("bsa")
    @Expose
    private List<Bsa> bsaList;

    public String getId() {
        return id;
    }

    public CDataSection getUri() {
        return uri;
    }

    public List<Bsa> getBsaList() {
        return bsaList;
    }
}
