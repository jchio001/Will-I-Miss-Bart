package com.example.jonathan.willimissbart.API.Callbacks;

import android.util.Log;

import com.example.jonathan.willimissbart.API.APIConstants;
import com.example.jonathan.willimissbart.API.Models.Generic.FailureEvent;
import com.example.jonathan.willimissbart.API.Models.StationModels.StationsResp;

import org.greenrobot.eventbus.EventBus;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StationsCallback implements Callback<StationsResp> {
    public static final String tag = "StationsCallback";

    @Override
    public void onResponse(Call<StationsResp> call, Response<StationsResp> resp) {
        Log.i(tag, String.valueOf(resp.code()));
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
    public void onFailure(Call<StationsResp> call, Throwable t) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        t.printStackTrace(printWriter);
        System.out.println(result.toString());
        EventBus.getDefault().post(new FailureEvent(tag, -1));
    }
}
