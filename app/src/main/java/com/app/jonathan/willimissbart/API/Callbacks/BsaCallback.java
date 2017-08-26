package com.app.jonathan.willimissbart.API.Callbacks;

import android.util.Log;

import com.app.jonathan.willimissbart.API.APIConstants;
import com.app.jonathan.willimissbart.API.Models.BSAModels.BsaResp;
import com.app.jonathan.willimissbart.API.Models.Generic.FailureEvent;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BsaCallback implements Callback<BsaResp> {
    public static final String tag = "BsaCallback";

    @Override
    public void onResponse(Call<BsaResp> call, Response<BsaResp> resp) {
        Log.i(tag, "Got BSA's");
        switch (resp.code()) {
            case APIConstants.HTTP_STATUS_OK:
                EventBus.getDefault().post(resp.body());
                break;
            default:
                EventBus.getDefault().post(new FailureEvent(tag, resp.code()));
                break;
        }
    }

    @Override
    public void onFailure(Call<BsaResp> call, Throwable t) {
        Log.e(tag, "Failed to get BSA's");
        EventBus.getDefault().post(new FailureEvent(tag, -1));
    }
}