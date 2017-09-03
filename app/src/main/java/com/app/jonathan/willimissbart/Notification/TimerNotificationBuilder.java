package com.app.jonathan.willimissbart.Notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;

import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.Misc.MyApplication;
import com.app.jonathan.willimissbart.Misc.Utils;
import com.app.jonathan.willimissbart.R;

public class TimerNotificationBuilder {
    private Context context = MyApplication.getContext();
    private String title;

    private int time = -1;

    public TimerNotificationBuilder(String title, int time) {
        this.time = time;
        this.title = title;
    }

    public Notification build(boolean isHeadsUp) {
        return new NotificationCompat.Builder(context)
            .setDefaults(isHeadsUp ? Notification.DEFAULT_ALL : 0)
            .setCustomContentView(createView(time))
            .setOngoing(true)
            .setPriority(isHeadsUp ? Notification.PRIORITY_HIGH : Notification.PRIORITY_DEFAULT)
            .setSmallIcon(R.mipmap.ic_launcher)
            .build();
    }

    // use this if
    public Notification buildHeadsUp() {
        return new NotificationCompat.Builder(context)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setCustomContentView(createView(time))
            .setOngoing(true)
            .setPriority(Notification.PRIORITY_HIGH)
            .build();
    }


    public RemoteViews createView(int time) {
        Intent intent = new Intent();
        intent.setAction(Constants.DISMISS);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        RemoteViews r = new RemoteViews(context.getPackageName(), R.layout.notification_timer);
        r.setTextViewText(R.id.notif_title, title);
        r.setTextViewText(R.id.notif_timer, time != 0 ? Utils.generateTimerText(time) :
            context.getString(R.string.train_leaving));
        r.setOnClickPendingIntent(R.id.notif_close, pendingIntent);

        return r;
    }
}
