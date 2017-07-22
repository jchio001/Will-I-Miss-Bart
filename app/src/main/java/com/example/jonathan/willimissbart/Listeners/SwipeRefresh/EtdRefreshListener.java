package com.example.jonathan.willimissbart.Listeners.SwipeRefresh;

import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import com.example.jonathan.willimissbart.Enums.RefreshStateEnum;
import com.example.jonathan.willimissbart.Misc.SharedEtdDataBundle;
import com.example.jonathan.willimissbart.Misc.Utils;
import com.example.jonathan.willimissbart.Persistence.Models.UserBartData;

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

        sharedEtdDataBundle.stationCntr = 0;
        Utils.fetchEtds(userBartData);
    }
}
