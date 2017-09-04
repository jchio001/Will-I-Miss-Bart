package com.app.jonathan.willimissbart.ViewHolders;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.app.jonathan.willimissbart.API.Models.EtdModels.Estimate;
import com.app.jonathan.willimissbart.Dialogs.NotificationAlertDialog;
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

    private long timeInSeconds;

    public EstimateViewHolder(View v, Context context, Estimate estimate, long timeInSeconds) {
        ButterKnife.bind(this, v);
        this.context = context;
        this.view = v;
        this.estimateMins = estimate.getMinutes();
        this.timeInSeconds = timeInSeconds;
        colorView.setBackground(getDrawable(estimate.getHexcolor()));
        estimateTV.setText(getEstimateText(estimateMins));
    }

    @OnClick(R.id.estimate_parent)
    public void promptForNotification() {
        if (!estimateMins.equals("Leaving")) {
            int estimate = Utils.getTimerDuration(estimateMins, timeInSeconds);
            if (estimate < 30) {
                Toast.makeText(context, R.string.too_late, Toast.LENGTH_SHORT).show();
                return;
            }

            // Passed in estimate already factors in current time!
            new NotificationAlertDialog(context, getTitle(), estimate).show();
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
