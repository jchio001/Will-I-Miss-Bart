package com.app.jonathan.willimissbart.Listeners.SwipeRefresh;

import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import com.app.jonathan.willimissbart.API.APIConstants;
import com.app.jonathan.willimissbart.API.Callbacks.BsaCallback;
import com.app.jonathan.willimissbart.API.RetrofitClient;
import com.app.jonathan.willimissbart.Enums.RefreshStateEnum;


public class BsaRefreshListener extends BaseRefreshListener {
    public BsaRefreshListener(SwipeRefreshLayout swipeRefreshLayout) {
        super(swipeRefreshLayout);
    }

    @Override
    public void onRefresh() {
        synchronized (this) {
            if (refreshState == RefreshStateEnum.INACTIVE) {
                refreshState = RefreshStateEnum.REFRESHING;
            } else {
                return;
            }

            //prevents the user from spamming refresh
            long now = System.currentTimeMillis() / 1000;
            if (now - lastRefreshTime < 45) {
                Log.i("EtfRefreshListener", "Stop spamming idiot");
                refreshState = RefreshStateEnum.INACTIVE;
                swipeRefreshLayout.setRefreshing(false);
                return;
            }
            lastRefreshTime = now;
        }

        RetrofitClient.getInstance()
                .getMatchingService()
                .getBsa("bsa", APIConstants.API_KEY, 'y')
                .enqueue(new BsaCallback());
    }
}
