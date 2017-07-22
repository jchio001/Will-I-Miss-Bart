package com.example.jonathan.willimissbart.Listeners.SwipeRefresh;

import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import com.example.jonathan.willimissbart.Enums.RefreshStateEnum;


public abstract class BaseRefreshListener implements SwipeRefreshLayout.OnRefreshListener {
    protected SwipeRefreshLayout swipeRefreshLayout;
    RefreshStateEnum refreshState = RefreshStateEnum.INACTIVE;
    protected long lastRefreshTime = System.currentTimeMillis() / 1000;

    public BaseRefreshListener(SwipeRefreshLayout swipeRefreshLayout) {
        this.swipeRefreshLayout = swipeRefreshLayout;
    }

    public RefreshStateEnum getRefreshState() {
        return refreshState;
    }

    public BaseRefreshListener setRefreshState(RefreshStateEnum refreshState) {
        this.refreshState = refreshState;
        return this;
    }
}
