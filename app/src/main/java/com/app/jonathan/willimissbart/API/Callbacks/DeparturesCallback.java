package com.app.jonathan.willimissbart.API.Callbacks;

import android.util.Log;

import com.app.jonathan.willimissbart.API.APIConstants;
import com.app.jonathan.willimissbart.API.Models.Routes.DeparturesResp;
import com.app.jonathan.willimissbart.API.Models.Generic.FailureEvent;
import com.app.jonathan.willimissbart.API.Models.Routes.RoutesFailure;
import com.app.jonathan.willimissbart.API.Models.Routes.TripsWrapper;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeparturesCallback implements Callback<DeparturesResp> {
    public static final String tag = "DeparturesCallback";
    public boolean isReturnRoute;

    public DeparturesCallback(boolean isReturnRoute) {
        this.isReturnRoute = isReturnRoute;
    }

    @Override
    public void onResponse(Call<DeparturesResp> call, Response<DeparturesResp> resp) {
        Log.i(tag, "Got Departures");
        switch (resp.code()) {
            case APIConstants.HTTP_STATUS_OK:
                EventBus.getDefault().post(new TripsWrapper()
                    .setIsReturnRoute(isReturnRoute)
                    .setTrips(resp.body().getRoot().getSchedule().getRequest().getTrips()));
                break;
            default:
                EventBus.getDefault().post(new RoutesFailure(isReturnRoute));
                break;
        }
    }

    @Override
    public void onFailure(Call<DeparturesResp> call, Throwable t) {
        Log.e(tag, "Failed to get BSA's");
        EventBus.getDefault().post(new RoutesFailure(isReturnRoute));
    }
}
