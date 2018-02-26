package com.app.jonathan.willimissbart.API.Callbacks;

import android.util.Log;

import com.app.jonathan.willimissbart.API.Models.Etd.EtdResp;
import com.app.jonathan.willimissbart.API.Models.Etd.EtdRespWrapper;
import com.app.jonathan.willimissbart.API.RetrofitClient.StatusCode;
import com.app.jonathan.willimissbart.Misc.EstimatesManager;

import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EtdCallback implements Callback<EtdResp> {
    public static final String tag = "EtdCallback";

    private Set<String> destSet; // note: all the strings are abbreviations!

    public EtdCallback setDestSet(Set<String> destSet) {
        this.destSet = destSet;
        return this;
    }

    @Override
    public void onResponse(Call<EtdResp> call, Response<EtdResp> resp) {
        Log.i(tag, "Etds fetched!");
        switch (resp.code()) {
            case StatusCode.HTTP_STATUS_OK:
                EstimatesManager
                    .persistThenPost(new EtdRespWrapper(resp.body().getRoot(), destSet));
                break;
            default:
                EstimatesManager.persistThenPost(new EtdRespWrapper(null, destSet));
                break;
        }
    }

    @Override
    public void onFailure(Call<EtdResp> call, Throwable t) {
        Log.e(tag, "Failed to get etds");
        EstimatesManager.persistThenPost(new EtdRespWrapper(null, destSet));
    }
}
