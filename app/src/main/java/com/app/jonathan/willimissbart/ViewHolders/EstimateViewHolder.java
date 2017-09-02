package com.app.jonathan.willimissbart.ViewHolders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.app.jonathan.willimissbart.API.Models.EtdModels.Estimate;
import com.app.jonathan.willimissbart.BroadcastReceiver.TimerNotificationReceiver;
import com.app.jonathan.willimissbart.CountDownTimer.NotificationCountDownTimer;
import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.Misc.MyApplication;
import com.app.jonathan.willimissbart.Misc.Utils;
import com.app.jonathan.willimissbart.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EstimateViewHolder {
    @Bind(R.id.color) View colorView;
    @Bind(R.id.estimate) TextView estimateTV;

    private final Context context;
    private final View view;
    private String estimateMins;

    public EstimateViewHolder(View v, Context context, Estimate estimate) {
        ButterKnife.bind(this, v);
        this.context = context;
        this.view = v;
        this.estimateMins = estimate.getMinutes();
        colorView.setBackground(getDrawable(estimate.getHexcolor()));
        estimateTV.setText(getEstimateText(estimateMins));
    }

    @OnClick(R.id.estimate_parent)
    public void promptForNotification() {
        if (!estimateMins.equals("Leaving")) {
            final String title = getTitle();
            Intent intent = new Intent();
            intent.setAction(Constants.UPDATE);
            intent.putExtra(Constants.TITLE, title);
            intent.putExtra(Constants.SECONDS, Utils.getTimerDuration(estimateMins));
            context.sendBroadcast(intent);
            Toast.makeText(context, R.string.starting_timer, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, R.string.r_u_stupid, Toast.LENGTH_SHORT).show();
        }
    }

    private Drawable getDrawable(String color) {
        GradientDrawable drawable = (GradientDrawable) context.getResources()
                .getDrawable(R.drawable.black_rectangular_border);
        drawable.setColor(Color.parseColor(color));
        return drawable;
    }

    private String getEstimateText(String minutes) {
        return minutes.equals("Leaving") ? minutes : minutes + "m";
    }

    private String getTitle() {
        ViewGroup parent = (ViewGroup) view.getParent();
        return parent.getTag(R.string.origin) + " to " +
            parent.getTag(R.string.destination) + " leaving in:";
    }
}
