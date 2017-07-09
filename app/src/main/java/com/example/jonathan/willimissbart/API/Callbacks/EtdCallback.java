package com.example.jonathan.willimissbart.API.Callbacks;


import com.example.jonathan.willimissbart.API.APIConstants;
import com.example.jonathan.willimissbart.API.Models.EtdModels.EtdFailure;
import com.example.jonathan.willimissbart.API.Models.EtdModels.EtdResp;
import com.example.jonathan.willimissbart.API.Models.EtdModels.EtdRespBundle;
import com.example.jonathan.willimissbart.API.Models.Generic.FailureEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EtdCallback implements Callback<EtdResp> {
    private final String tag = "EtdCallBack";
    private String stationName;
    private int index;

    public EtdCallback setStationName(String stationName) {
        this.stationName = stationName;
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
        System.out.println(tag + " " + String.valueOf(index));
        switch (resp.code()) {
            case APIConstants.HTTP_STATUS_OK:
                EventBus.getDefault().post(new EtdRespBundle(index, resp.body()));
                break;
            default:
                EventBus.getDefault().post(new EtdFailure(tag, stationName, resp.code()));
                break;
        }
    }

    @Override
    public void onFailure(Call<EtdResp> call, Throwable t) {
        EventBus.getDefault().post(new EtdFailure(tag, stationName, -1));
    }
}
