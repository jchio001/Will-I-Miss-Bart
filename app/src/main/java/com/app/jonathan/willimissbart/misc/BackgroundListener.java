package com.app.jonathan.willimissbart.misc;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.Lifecycle.Event;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.os.Bundle;
import android.util.Log;

/**
 * Listens to the app being backgrounded/foregrounded.
 */
public class BackgroundListener implements Application.ActivityLifecycleCallbacks {

    private EstimatesManager estimatesManager;

    private int activityCounter = 0;

    public BackgroundListener(EstimatesManager estimatesManager) {
        this.estimatesManager = estimatesManager;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        ++activityCounter;
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
        --activityCounter;
        if (activityCounter == 0) {
            Log.i("BackgroundListener", "Stopping minutely update job!");
            estimatesManager.stopMinutelyUpdate();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }
}
