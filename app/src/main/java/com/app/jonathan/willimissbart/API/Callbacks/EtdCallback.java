package com.app.jonathan.willimissbart.API.Callbacks;


import android.util.Log;

import com.app.jonathan.willimissbart.API.APIConstants;
import com.app.jonathan.willimissbart.API.Models.Etd.Estimate;
import com.app.jonathan.willimissbart.API.Models.Etd.Etd;
import com.app.jonathan.willimissbart.API.Models.Etd.EtdFailure;
import com.app.jonathan.willimissbart.API.Models.Etd.EtdResp;
import com.app.jonathan.willimissbart.API.Models.Etd.EtdRoot;
import com.app.jonathan.willimissbart.API.Models.Etd.EtdStation;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;
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
                EtdRoot etdRoot = resp.body().getRoot();
                filterEtds(etdRoot.getStations().get(0));
                EventBus.getDefault().post(etdRoot);
                break;
            default:
                EventBus.getDefault().post(new EtdFailure(isReturnRoute));
                break;
        }
    }

    @Override
    public void onFailure(Call<EtdResp> call, Throwable t) {
        Log.e(tag, "Failed to get etds");
        EventBus.getDefault().post(new EtdFailure(isReturnRoute));
    }

    private void filterEtds(EtdStation etdStation) {
        List<Etd> filtered = Lists.newArrayList();
        for (Etd etd : etdStation.getEtds()) {
            if (destSet.contains(etd.getAbbreviation())) {
                filtered.add(etd);
            }
        }

        etdStation.setEtds(filtered);
    }

    private Map<String, List<Estimate>> filterAndMapEtds(EtdStation etdStation) {
        List<Etd> filtered = Lists.newArrayList();
        for (Etd etd : etdStation.getEtds()) {
            if (destSet.contains(etd.getAbbreviation())) {
                filtered.add(etd);
            }
        }
        etdStation.setEtds(filtered);

        Map<String, List<Estimate>> origDestToEstimates = Maps.newHashMap();
        for (Etd etd: etdStation.getEtds()) {
            origDestToEstimates.put(
                etdStation.getAbbr() + etd.getAbbreviation(), etd.getEstimates());
        }
        return origDestToEstimates;
    }
}
