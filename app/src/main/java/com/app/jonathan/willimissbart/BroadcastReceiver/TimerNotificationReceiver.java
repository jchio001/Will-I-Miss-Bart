package com.app.jonathan.willimissbart.BroadcastReceiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;

import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.Service.TimerService;

public class TimerNotificationReceiver extends BroadcastReceiver {

    @Override
    public synchronized void onReceive(Context context, Intent intent) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        switch (intent.getAction()) {
            case Constants.DISMISS:
                v.cancel();

                Intent stopIntent = new Intent(context, TimerService.class);
                context.stopService(stopIntent);
                NotificationManager manager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(Constants.TIMER_NOTIF_ID);
                break;
            default:
                break;
        }
    }
}
