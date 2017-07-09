package com.example.jonathan.willimissbart.Listeners.SwipeRefresh;

import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import com.example.jonathan.willimissbart.Misc.RefreshStateEnum;
import com.example.jonathan.willimissbart.Misc.SharedEtdDataBundle;
import com.example.jonathan.willimissbart.Misc.Utils;
import com.example.jonathan.willimissbart.Persistence.Models.UserBartData;

import java.util.List;

public class EtdRefreshListener implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RefreshStateEnum refreshState = RefreshStateEnum.INACTIVE;
    private SharedEtdDataBundle sharedEtdDataBundle;
    private List<UserBartData> userBartData;
    private long lastRefreshTime = System.currentTimeMillis() / 1000;

    public EtdRefreshListener(SwipeRefreshLayout swipeRefreshLayout) {
        this.swipeRefreshLayout = swipeRefreshLayout;
    }

    public RefreshStateEnum getRefreshState() {
        return refreshState;
    }

    public EtdRefreshListener setRefreshState(RefreshStateEnum refreshState) {
        this.refreshState = refreshState;
        return this;
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
                sharedEtdDataBundle.stationCntr = 0;
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

        Utils.fetchEtds(userBartData);
    }
}
