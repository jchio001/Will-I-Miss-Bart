package com.app.jonathan.willimissbart.timer;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.app.jonathan.willimissbart.misc.Constants;
import com.app.jonathan.willimissbart.timer.TimerNotificationBuilder;
import com.app.jonathan.willimissbart.runnable.StartTimerRunnable;
import com.app.jonathan.willimissbart.timer.NotificationCountDownTimer;

public class TimerService extends Service {

    private NotificationCountDownTimer timer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            switch (intent.getAction()) {
                case Constants.UPDATE:
                    String title = intent.getStringExtra(Constants.TITLE);
                    int seconds = intent.getIntExtra(Constants.SECONDS, -1);
                    setTimer(new NotificationCountDownTimer(seconds, 1000L, title));
                    startForeground(Constants.TIMER_NOTIF_ID,
                        new TimerNotificationBuilder(title, seconds).build(seconds < 0));
                    break;
                default:
                    break;
            }
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }

        super.onDestroy();
    }

    private void setTimer(NotificationCountDownTimer newTimer) {
        if (timer != null) {
            timer.cancel();
        }

        timer = newTimer;
        new Handler().post(new StartTimerRunnable(timer));
    }
}
