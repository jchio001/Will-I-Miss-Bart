package com.app.jonathan.willimissbart.Notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;

import com.app.jonathan.willimissbart.BroadcastReceiver.TimerNotificationReceiver;
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

    public Notification build() {;
        return new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setCustomContentView(createView(time))
                .setOngoing(true)
                .build();
    }

    public RemoteViews createView(int time) {
        Intent intent = new Intent();
        intent.setAction(Constants.DISMISS);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        RemoteViews r = new RemoteViews(context.getPackageName(), R.layout.notification_timer);
        r.setTextViewText(R.id.notif_title, title);
        r.setTextViewText(R.id.notif_timer, Utils.generateTimerText(time));
        r.setOnClickPendingIntent(R.id.notif_close, pendingIntent);

        return r;
    }
}
