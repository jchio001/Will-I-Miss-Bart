package com.app.jonathan.willimissbart.Misc;

import com.app.jonathan.willimissbart.API.Models.Etd.EtdRespWrapper;

public interface EstimatesListener {
    void onReceiveEstimates(EtdRespWrapper etdRespWrapper);
    void onEstimatesUpdated();
}
