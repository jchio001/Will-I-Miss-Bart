package com.app.jonathan.willimissbart.Listeners.SwipeRefresh;

import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import com.app.jonathan.willimissbart.Adapters.TripsAdapter;
import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.Misc.EstimatesManager;

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

        EstimatesManager.updateEstimates(now);
        lastRefreshTime = now;
        refreshState = Constants.REFRESH_STATE_INACTIVE;
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }
}
