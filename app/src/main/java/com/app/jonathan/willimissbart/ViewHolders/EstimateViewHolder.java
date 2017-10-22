package com.app.jonathan.willimissbart.ViewHolders;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.jonathan.willimissbart.API.Models.Etd.Estimate;
import com.app.jonathan.willimissbart.Misc.Utils;
import com.app.jonathan.willimissbart.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Deprecated
public class EstimateViewHolder {
    @Bind(R.id.estimate_parent) LinearLayout parent;
    @Bind(R.id.color) View colorView;
    @Bind(R.id.estimate) TextView estimateTV;

    private final Context context;
    private final View view;
    private String estimateMins;

    private long respTimeInSeconds;

    public EstimateViewHolder(View v, Context context, Estimate estimate, long timeInSeconds) {
        ButterKnife.bind(this, v);
        this.context = context;
        this.view = v;
        this.estimateMins = estimate.getMinutes();
        this.respTimeInSeconds = timeInSeconds;
        colorView.setBackground(getDrawable(estimate.getHexcolor()));
        estimateTV.setText(getEstimateText(estimateMins));
    }

    @OnClick(R.id.estimate_parent)
    public void promptForNotification() {
        if (!estimateMins.equals("Leaving")) {
            int estimate = Utils.getTimerDuration(estimateMins, respTimeInSeconds);
            if (estimate < 45) {
                Toast.makeText(context, R.string.too_late, Toast.LENGTH_SHORT).show();
                return;
            }

            // Passed in estimate already factors in current time!
            parent.setEnabled(false);
            // new NotificationAlertDialog(this, estimateMins, respTimeInSeconds).show();
        } else {
            Toast.makeText(context, R.string.r_u_stupid, Toast.LENGTH_SHORT).show();
        }
    }

    public Context getContext() {
        return context;
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

    public String getTitle() {
        ViewGroup parent = (ViewGroup) view.getParent();
        return parent.getTag(R.string.origin) + " to " + parent.getTag(R.string.destination) + " leaving in:";
    }

    public void enable() {
        parent.setEnabled(true);
    }
}
