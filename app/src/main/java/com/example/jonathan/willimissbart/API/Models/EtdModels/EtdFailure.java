package com.example.jonathan.willimissbart.API.Models.EtdModels;


public class EtdFailure {
    public String tag;
    public String stationName;
    public int errorCode;
    public int index;

    public EtdFailure(String tag, String stationName, int errorCode, int index) {
        this.tag = tag;
        this.stationName = stationName;
        this.errorCode = errorCode;
        this.index = index;
    }
}
