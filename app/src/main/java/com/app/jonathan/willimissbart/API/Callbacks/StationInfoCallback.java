package com.app.jonathan.willimissbart.API.Callbacks;

import android.util.Log;

import com.app.jonathan.willimissbart.API.APIConstants;
import com.app.jonathan.willimissbart.API.Models.Generic.FailureEvent;
import com.app.jonathan.willimissbart.API.Models.StationInfoModels.StationInfoResp;
import com.app.jonathan.willimissbart.API.Models.StationInfoModels.StationInfoRoot;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StationInfoCallback implements Callback<StationInfoResp> {
    public static final String tag = "StationInfoCallBack";

    public void onResponse(Call<StationInfoResp> call, Response<StationInfoResp> resp) {
        Log.i(tag, "Got Station Info.");
        switch (resp.code()) {
            case APIConstants.HTTP_STATUS_OK:
                EventBus.getDefault().post(resp.body().getRoot().getStations().getStation());
                break;
            default:
                EventBus.getDefault().post(new FailureEvent(tag, resp.code()));
                break;
        }
    }

    @Override
    public void onFailure(Call<StationInfoResp> call, Throwable t) {
        Log.e(tag, "Failed to get BSA's");
        EventBus.getDefault().post(new FailureEvent(tag, -1));
    }
}
