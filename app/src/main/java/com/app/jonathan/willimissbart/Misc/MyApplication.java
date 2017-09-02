package com.app.jonathan.willimissbart.Misc;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.app.jonathan.willimissbart.Service.TimerService;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;


public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        Iconify.with(new FontAwesomeModule());
        context = this;
        //Intent intent = new Intent(this, TimerService.class);
        //startService(intent);
    }

    public static Context getContext() {
        return context;
    }
}
