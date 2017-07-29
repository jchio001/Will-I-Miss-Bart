package com.example.jonathan.willimissbart.API.Callbacks;


import android.util.Log;

import com.example.jonathan.willimissbart.API.APIConstants;
import com.example.jonathan.willimissbart.API.Models.EtdModels.EtdFailure;
import com.example.jonathan.willimissbart.API.Models.EtdModels.EtdResp;
import com.example.jonathan.willimissbart.API.Models.EtdModels.EtdRespBundle;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EtdCallback implements Callback<EtdResp> {
    public static final String tag = "EtdCallback";
    private String stationName;
    private String stationAbbr;
    private int index;

    public String getStationName() {
        return stationName;
    }

    public EtdCallback setStationName(String stationName) {
        this.stationName = stationName;
        return this;
    }

    public EtdCallback setStationAbbr(String stationAbbr) {
        this.stationAbbr = stationAbbr;
        return this;
    }

    public EtdCallback setIndex(int index) {
        this.index = index;
        return this;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public void onResponse(Call<EtdResp> call, Response<EtdResp> resp) {
        Log.i(tag, String.format("Got etd for: %s", stationAbbr));
        switch (resp.code()) {
            case APIConstants.HTTP_STATUS_OK:
                EventBus.getDefault().post(new EtdRespBundle(index, resp.body()));
                break;
            default:
                EventBus.getDefault().post(new EtdFailure(
                        tag, stationName, stationAbbr, resp.code(), index)
                );
                break;
        }
    }

    @Override
    public void onFailure(Call<EtdResp> call, Throwable t) {
        Log.e(tag, String.format("Failed to get etd for: %s", stationAbbr));
        EventBus.getDefault().post(new EtdFailure(tag, stationName, stationAbbr, -1, index));
    }
}
