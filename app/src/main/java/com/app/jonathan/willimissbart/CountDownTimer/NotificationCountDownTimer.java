package com.app.jonathan.willimissbart.CountDownTimer;

import android.content.Intent;
import android.os.CountDownTimer;

import com.app.jonathan.willimissbart.BroadcastReceiver.TimerNotificationReceiver;
import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.Misc.MyApplication;
import com.app.jonathan.willimissbart.Misc.Utils;

public class NotificationCountDownTimer extends CountDownTimer {
    private String title;
    private int time = 0;

    public NotificationCountDownTimer(int seconds,
                                      long countDownInterval,
                                      String title) {
        super(seconds * 1000, countDownInterval);
        this.title = title;
        this.time = seconds;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        --time;
        Utils.createOrUpdateNotification(title, time);
    }

    @Override
    public void onFinish() {
        Utils.createOrUpdateNotification(title, 0);
        Intent intent = new Intent(MyApplication.getContext(), TimerNotificationReceiver.class);
        intent.setAction(Constants.ALARM);
        MyApplication.getContext().sendBroadcast(intent);
        this.cancel();
    }
}
