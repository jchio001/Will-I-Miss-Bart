package com.app.jonathan.willimissbart.ViewHolders;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.app.jonathan.willimissbart.R;
import com.joanzapata.iconify.widget.IconTextView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class StationInfoRowViewHolder {
    @Bind(R.id.activity_info_icon) IconTextView activityInfoIcon;
    @Bind(R.id.activity_blurb) TextView activityBlurb;
    @Bind(R.id.bottom_border) View bottomBorder;

    private static String[] icons = {"{ion-fork}", "{ion-android-cart}", "{ion-android-contacts}"};

    public StationInfoRowViewHolder(View v, String text, int index) {
        ButterKnife.bind(this, v);

        if (index == 2) {
            bottomBorder.setVisibility(View.GONE);
        }

        activityInfoIcon.setText(icons[index]);
        activityBlurb.setMovementMethod(LinkMovementMethod.getInstance());
        activityBlurb.setText(Html.fromHtml(text.trim()));
    }
}
