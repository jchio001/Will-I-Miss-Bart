package com.app.jonathan.willimissbart.viewholder;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.api.Models.BSA.Bsa;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NotifBlurbViewHolder {

    @Bind(R.id.notif_blurb_text) TextView notifBlurb;

    private Bsa bsa;

    public NotifBlurbViewHolder(View v, Bsa bsa) {
        ButterKnife.bind(this, v);
        this.bsa = bsa;
        notifBlurb.setText(bsa.getFormattedMessage());
    }

    @OnClick(R.id.notif_blurb_parent)
    public void onClick() {
        Toast.makeText(notifBlurb.getContext(), bsa.getFormattedMessage(), Toast.LENGTH_LONG).show();
    }
}
