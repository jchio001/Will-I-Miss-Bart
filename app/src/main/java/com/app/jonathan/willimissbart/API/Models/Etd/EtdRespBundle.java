package com.app.jonathan.willimissbart.API.Models.Etd;


public class EtdRespBundle {
    private int index;
    private boolean retryAfterFailure = false;
    private EtdResp etdResp;

    public EtdRespBundle(int index, boolean retryAfterFailure, EtdResp etdResp) {
        this.index = index;
        this.retryAfterFailure = retryAfterFailure;
        this.etdResp = etdResp;
    }

    public int getIndex() {
        return index;
    }

    public boolean isRetryAfterFailure() {
        return retryAfterFailure;
    }

    public EtdResp getEtdResp() {
        return etdResp;
    }
}
