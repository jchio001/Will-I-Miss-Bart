package com.app.jonathan.willimissbart.API.Callbacks;


import android.util.Log;

import com.app.jonathan.willimissbart.API.APIConstants;
import com.app.jonathan.willimissbart.API.Models.Etd.EtdResp;
import com.app.jonathan.willimissbart.API.Models.Etd.EtdRespWrapper;
import com.app.jonathan.willimissbart.Misc.EstimatesManager;

import org.greenrobot.eventbus.EventBus;

import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EtdCallback implements Callback<EtdResp> {
    public static final String tag = "EtdCallback";

    private Set<String> destSet; // note: all the strings are abbreviations!
    private boolean isReturnRoute = false;

    public EtdCallback setDestSet(Set<String> destSet) {
        this.destSet = destSet;
        return this;
    }

    public EtdCallback isReturnRoute(boolean returnRoute) {
        isReturnRoute = returnRoute;
        return this;
    }

    @Override
    public void onResponse(Call<EtdResp> call, Response<EtdResp> resp) {
        Log.i(tag, "Etds fetched!");
        switch (resp.code()) {
            case APIConstants.HTTP_STATUS_OK:
                EstimatesManager
                    .post(new EtdRespWrapper(resp.body().getRoot(), destSet, isReturnRoute));
                break;
            default:
                EstimatesManager.post(new EtdRespWrapper(null, destSet, isReturnRoute));
                break;
        }
    }

    @Override
    public void onFailure(Call<EtdResp> call, Throwable t) {
        Log.e(tag, "Failed to get etds");
        EstimatesManager.post(new EtdRespWrapper(null, destSet, isReturnRoute));
    }
}
