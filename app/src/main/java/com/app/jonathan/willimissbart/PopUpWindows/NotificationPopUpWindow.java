package com.app.jonathan.willimissbart.PopUpWindows;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.app.jonathan.willimissbart.API.Models.BSAModels.Bsa;
import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.ViewHolders.NotifBlurbViewHolder;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class NotificationPopUpWindow extends PopupWindow {
    @Bind(R.id.notif_blurb_feed) LinearLayout notifFeed;

    public NotificationPopUpWindow(Context context, List<Bsa> bsaList) {
        super(LayoutInflater.from(context)
            .inflate(R.layout.pop_up_window_notif, null),
            800, WRAP_CONTENT);
        ButterKnife.bind(this, getContentView());
        setBackgroundDrawable(
            ContextCompat.getDrawable(context, R.drawable.background_pop_up_window));

        for (Bsa bsa: bsaList) {
            View v = LayoutInflater.from(context).inflate(R.layout.notif_blurb, null);
            new NotifBlurbViewHolder(v, bsa);
            notifFeed.addView(v);
        }

        setFocusable(true);
    }
}
