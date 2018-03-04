package com.app.jonathan.willimissbart.runnable;

import com.app.jonathan.willimissbart.timer.NotificationCountDownTimer;

public class StartTimerRunnable implements Runnable {

    private NotificationCountDownTimer timer;

    public StartTimerRunnable(NotificationCountDownTimer timer) {
        this.timer = timer;
    }

    @Override
    public void run() {
        timer.start();
    }
}
