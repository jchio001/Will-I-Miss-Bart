package com.app.jonathan.willimissbart.listener.swiperefresh;

import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import com.app.jonathan.willimissbart.adapter.TripsAdapter;
import com.app.jonathan.willimissbart.misc.Constants;
import com.app.jonathan.willimissbart.misc.EstimatesManager;

public class TripRefreshListener extends BaseRefreshListener {

    private TripsAdapter adapter;

    public TripRefreshListener(SwipeRefreshLayout swipeRefreshLayout,
                               TripsAdapter adapter) {
        super(swipeRefreshLayout);
        this.adapter = adapter;
    }

    @Override
    public void onRefresh() {
        synchronized (this) {
            if (refreshState == Constants.REFRESH_STATE_INACTIVE) {
                refreshState = Constants.REFRESH_STATE_REFRESHING;
            } else {
                return;
            }
        }

        long now = System.currentTimeMillis() / 1000;
        if (now - lastRefreshTime < 60) {
            Log.i("TripRefreshListener", "Stop spamming idiot");
            refreshState = Constants.REFRESH_STATE_INACTIVE;
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        lastRefreshTime = now;
        refreshState = Constants.REFRESH_STATE_INACTIVE;
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }
}
