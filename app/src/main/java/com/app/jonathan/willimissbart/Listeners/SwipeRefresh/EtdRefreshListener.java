package com.app.jonathan.willimissbart.Listeners.SwipeRefresh;

import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import com.app.jonathan.willimissbart.Enums.RefreshStateEnum;
import com.app.jonathan.willimissbart.Misc.SharedEtdDataBundle;
import com.app.jonathan.willimissbart.Misc.Utils;
import com.app.jonathan.willimissbart.Persistence.Models.UserBartData;

import java.util.List;

public class EtdRefreshListener extends BaseRefreshListener {
    private SharedEtdDataBundle sharedEtdDataBundle;
    private List<UserBartData> userBartData;

    public EtdRefreshListener(SwipeRefreshLayout swipeRefreshLayout) {
        super(swipeRefreshLayout);
    }

    public EtdRefreshListener setSharedEtdDataBundle(SharedEtdDataBundle sharedEtdDataBundle) {
        this.sharedEtdDataBundle = sharedEtdDataBundle;
        return this;
    }

    public EtdRefreshListener setUserBartData(List<UserBartData> userBartData) {
        this.userBartData = userBartData;
        return this;
    }

    @Override
    public void onRefresh() {
        synchronized (this) {
            if (refreshState == RefreshStateEnum.INACTIVE) {
                refreshState = RefreshStateEnum.REFRESHING;
            } else {
                return;
            }

            //prevents the user from spamming refreshOnNewData
            long now = System.currentTimeMillis() / 1000;
            if (now - lastRefreshTime < 45) {
                Log.i("EtfRefreshListener", "Stop spamming idiot");
                refreshState = RefreshStateEnum.INACTIVE;
                swipeRefreshLayout.setRefreshing(false);
                return;
            }
            lastRefreshTime = now;
        }

        sharedEtdDataBundle.stationCntr = 0;
        Utils.fetchEtds(userBartData);
    }

    // Forcefully makes API calls needed to refresh the feed (ignores the 45 second cooldown)
    // This method should only be used to load the feed as a last resort
    public synchronized void forceRefresh() {
        lastRefreshTime = System.currentTimeMillis() / 1000;
        sharedEtdDataBundle.stationCntr = 0;
        Utils.fetchEtds(userBartData);
    }
}
