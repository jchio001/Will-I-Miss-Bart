package com.app.jonathan.willimissbart.BroadcastReceiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.app.jonathan.willimissbart.CountDownTimer.NotificationCountDownTimer;
import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.Misc.MyApplication;
import com.app.jonathan.willimissbart.Misc.Utils;
import com.app.jonathan.willimissbart.Runnables.StartTimerRunnable;
import com.app.jonathan.willimissbart.Service.TimerService;

public class TimerNotificationReceiver extends BroadcastReceiver {
    public static NotificationCountDownTimer timer;

    @Override
    public synchronized void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Constants.DISMISS:
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }

                Intent stopIntent = new Intent(context, TimerService.class);
                context.stopService(stopIntent);
                NotificationManager manager = (NotificationManager)
                        context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(Constants.TIMER_NOTIF_ID);
                break;
            case Constants.UPDATE:
                String title = intent.getStringExtra(Constants.TITLE);
                int seconds = intent.getIntExtra(Constants.SECONDS, -1);
                Utils.createOrUpdateNotification(title, seconds);
                setTimer(new NotificationCountDownTimer(seconds, 1000L, title));
                break;
            default:
                break;
        }
    }

    public static void setTimer(NotificationCountDownTimer newTimer) {
        if (timer != null) {
            timer.cancel();
        }

        timer = newTimer;
        new Handler().post(new StartTimerRunnable(timer));
    }
}
