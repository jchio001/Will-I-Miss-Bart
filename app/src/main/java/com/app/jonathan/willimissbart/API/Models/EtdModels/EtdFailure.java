package com.app.jonathan.willimissbart.API.Models.EtdModels;


import com.app.jonathan.willimissbart.API.Callbacks.EtdCallback;
import com.app.jonathan.willimissbart.Persistence.Models.UserStationData;

public class EtdFailure {
    public String tag;
    public int errorCode;
    public boolean retryAfterFailure;
    public UserStationData data;
    public int index;

    public EtdFailure(EtdCallback etdCallback, int errorCode) {
        this.tag = EtdCallback.tag;
        this.errorCode = errorCode;
        this.retryAfterFailure = etdCallback.isRetryAfterFailure();
        this.data = etdCallback.getData();
        this.index = etdCallback.getIndex();
    }
}
