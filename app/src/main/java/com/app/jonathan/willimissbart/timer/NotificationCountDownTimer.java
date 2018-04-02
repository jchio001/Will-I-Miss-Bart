package com.app.jonathan.willimissbart.timer;

import android.content.Intent;
import android.os.CountDownTimer;

import com.app.jonathan.willimissbart.misc.Constants;
import com.app.jonathan.willimissbart.misc.MyApplication;
import com.app.jonathan.willimissbart.misc.Utils;

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
        Utils.createOrUpdateNotification(title, --time);
    }

    @Override
    public void onFinish() {
        --time;
        if (time > 0) {
            Intent intent = new Intent(MyApplication.getContext(), TimerService.class);
            intent.setAction(Constants.UPDATE);
            intent.putExtra(Constants.TITLE, title);
            intent.putExtra(Constants.SECONDS, time);
            MyApplication.getContext().startService(intent);
        } else {
            Utils.createOrUpdateNotification(title, -1);
        }
        this.cancel();
    }
}
