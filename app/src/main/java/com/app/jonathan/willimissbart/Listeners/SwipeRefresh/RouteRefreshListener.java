package com.app.jonathan.willimissbart.Listeners.SwipeRefresh;

import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import com.app.jonathan.willimissbart.Adapters.RoutesAdapter;
import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.Misc.EstimatesManager;

public class RouteRefreshListener extends BaseRefreshListener {
    private RoutesAdapter adapter;

    public RouteRefreshListener(SwipeRefreshLayout swipeRefreshLayout,
                                RoutesAdapter adapter) {
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
            Log.i("RouteRefreshListener", "Stop spamming idiot");
            refreshState = Constants.REFRESH_STATE_INACTIVE;
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        EstimatesManager.updateEstimates((int) (now - lastRefreshTime));
        lastRefreshTime = now;
        refreshState = Constants.REFRESH_STATE_INACTIVE;
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }
}
