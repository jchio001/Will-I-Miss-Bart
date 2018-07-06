package com.app.jonathan.willimissbart.misc;

import android.app.Application;
import android.content.Context;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.IoniconsModule;

public class MyApplication extends Application {

    // TODO: Fix this! This is super jank and 100% leaks. Get the context from the service!
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        Iconify.with(new IoniconsModule());
        registerActivityLifecycleCallbacks(new BackgroundListener(EstimatesManager.get()));

        context = this;
    }

    public static Context getContext() {
        return context;
    }
}
