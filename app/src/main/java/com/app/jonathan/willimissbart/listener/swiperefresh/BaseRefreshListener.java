package com.app.jonathan.willimissbart.listener.swiperefresh;

import android.support.v4.widget.SwipeRefreshLayout;

import com.app.jonathan.willimissbart.misc.Constants;


public abstract class BaseRefreshListener implements SwipeRefreshLayout.OnRefreshListener {

    protected SwipeRefreshLayout swipeRefreshLayout;
    protected int refreshState = Constants.REFRESH_STATE_INACTIVE;
    protected long lastRefreshTime = System.currentTimeMillis() / 1000;

    public BaseRefreshListener(SwipeRefreshLayout swipeRefreshLayout) {
        this.swipeRefreshLayout = swipeRefreshLayout;
    }

    public BaseRefreshListener setRefreshState(int refreshState) {
        this.refreshState = refreshState;
        return this;
    }
}
