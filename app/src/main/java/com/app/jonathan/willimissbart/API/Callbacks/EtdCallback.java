package com.app.jonathan.willimissbart.API.Callbacks;


import android.util.Log;

import com.app.jonathan.willimissbart.API.APIConstants;
import com.app.jonathan.willimissbart.API.Models.Etd.EtdFailure;
import com.app.jonathan.willimissbart.API.Models.Etd.EtdResp;
import com.app.jonathan.willimissbart.API.Models.Etd.EtdRespBundle;
import com.app.jonathan.willimissbart.Persistence.Models.UserStationData;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EtdCallback implements Callback<EtdResp> {
    public static final String tag = "EtdCallback";
    private UserStationData data;
    private int index;
    private boolean retryAfterFailure = false;

    public static String getTag() {
        return tag;
    }

    public UserStationData getData() {
        return data;
    }

    public EtdCallback setData(UserStationData data) {
        this.data = data;
        return this;
    }

    public int getIndex() {
        return index;
    }

    public EtdCallback setIndex(int index) {
        this.index = index;
        return this;
    }

    public boolean isRetryAfterFailure() {
        return retryAfterFailure;
    }

    public void setRetryAfterFailure(boolean retryAfterFailure) {
        this.retryAfterFailure = retryAfterFailure;
    }

    @Override
    public void onResponse(Call<EtdResp> call, Response<EtdResp> resp) {
        Log.i(tag, String.format("Got etd for: %s", data.getStation()));
        switch (resp.code()) {
            case APIConstants.HTTP_STATUS_OK:
                EventBus.getDefault()
                        .post(new EtdRespBundle(index, retryAfterFailure, resp.body()));
                break;
            default:
                EventBus.getDefault().post(new EtdFailure(this, resp.code()));
                break;
        }
    }

    @Override
    public void onFailure(Call<EtdResp> call, Throwable t) {
        Log.e(tag, String.format("Failed to get etd for: %s", data.getStation()));
        EventBus.getDefault().post(new EtdFailure(this, -1));
    }
}
