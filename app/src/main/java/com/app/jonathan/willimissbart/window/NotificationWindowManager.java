package com.app.jonathan.willimissbart.window;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.api.Models.BSA.Bsa;
import com.app.jonathan.willimissbart.misc.Constants;
import com.app.jonathan.willimissbart.persistence.SPManager;
import com.app.jonathan.willimissbart.viewholder.NotifBlurbViewHolder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class NotificationWindowManager extends PopupWindow {

    @Bind(R.id.notif_blurb_feed) LinearLayout notifFeed;
    @Bind(R.id.mute_notif) CheckBox muteNotifCheckBox;

    public static boolean isChecked = false;
    public final static SimpleDateFormat dateFormat = new SimpleDateFormat(
        "yyyy/MM/dd", Locale.ENGLISH);

    public NotificationWindowManager(Context context, List<Bsa> bsaList) {
        super(LayoutInflater.from(context)
            .inflate(R.layout.bsa_notif_layout, null),
            800, WRAP_CONTENT);
        ButterKnife.bind(this, getContentView());
        muteNotifCheckBox.setChecked(isChecked);
        setBackgroundDrawable(
            ContextCompat.getDrawable(context, R.drawable.background_pop_up_window));

        for (Bsa bsa: bsaList) {
            View v = LayoutInflater.from(context).inflate(R.layout.single_bsa_cell, null);
            new NotifBlurbViewHolder(v, bsa);
            notifFeed.addView(v);
        }

        setFocusable(true);
    }

    @OnClick(R.id.mute_notif)
    public void onChangeMuteSettings() {
        isChecked = !isChecked;
        if (isChecked) {
            SPManager.persistString(notifFeed.getContext(), Constants.MUTE_NOTIF,
                dateFormat.format(new Date()));
        } else {
            SPManager.persistString(notifFeed.getContext(), Constants.MUTE_NOTIF, "");
        }
    }
}
