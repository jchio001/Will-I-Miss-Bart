package com.app.jonathan.willimissbart.API.Models.EtdModels;


public class EtdRespBundle {
    private int index;
    private EtdResp etdResp;

    public EtdRespBundle(int index, EtdResp etdResp) {
        this.index = index;
        this.etdResp = etdResp;
    }

    public int getIndex() {
        return index;
    }

    public EtdResp getEtdResp() {
        return etdResp;
    }
}
