package com.app.jonathan.willimissbart.Runnables;

import com.app.jonathan.willimissbart.Timers.NotificationCountDownTimer;

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
