package com.app.jonathan.willimissbart.Misc;

import android.app.Application;
import android.content.Context;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.joanzapata.iconify.fonts.IoniconsModule;


public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        Iconify.with(new FontAwesomeModule()) // TODO: remove font awesome
            .with(new IoniconsModule());
        context = this;
    }

    public static Context getContext() {
        return context;
    }
}
