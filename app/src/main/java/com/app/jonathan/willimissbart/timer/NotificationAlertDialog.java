package com.app.jonathan.willimissbart.timer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.api.Models.Etd.Estimate;
import com.app.jonathan.willimissbart.api.Models.Routes.Leg;
import com.app.jonathan.willimissbart.misc.Constants;
import com.app.jonathan.willimissbart.misc.Utils;
import com.app.jonathan.willimissbart.viewholder.DigitViewHolder;
import com.joanzapata.iconify.widget.IconTextView;

import butterknife.Bind;
import butterknife.ButterKnife;

// Did not create custom classes for listeners because the code's pretty straightforward in them
// and will only be used within this class. Also, my package organization is already super bloaty
// and I'd rather not add more packages that contain 1 class with barely any code.
public class NotificationAlertDialog {

    @Bind(R.id.info_blurb) TextView infoBlurb;
    @Bind(R.id.tens_digit) LinearLayout tensDigit;
    @Bind(R.id.ones_digit) LinearLayout onesDigit;
    @Bind(R.id.error_tv) TextView errorTV;

    private String title;

    private IconTextView alarmIcon;
    private AlertDialog alertDialog;
    private DigitViewHolder tensDigitViewHolder;
    private DigitViewHolder onesDigitViewHolder;
    private Leg leg;
    private Estimate estimate;

    private long timeOfResp;

    public NotificationAlertDialog(final IconTextView alarmIcon,
                                   Leg leg,
                                   Estimate estimate,
                                   long timeOfResp) {
        Context context = alarmIcon.getContext();

        this.alarmIcon = alarmIcon;
        this.leg = leg;
        this.estimate = estimate;
        this.timeOfResp = timeOfResp;
        this.title = context.getString(
            R.string.notif_title_format,
            leg.getOrigin(),
            leg.getDestination());

        alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(
            alarmIcon.getContext(), R.style.AlertDialogTheme))
            .setPositiveButton("OK", null)
            .setNegativeButton("CANCEL", (dialog, which) -> alarmIcon.setEnabled(true))
            .setOnCancelListener(dialog -> alarmIcon.setEnabled(true))
            .setTitle(R.string.timer_notif_title)
            .setView(inflateAndRenderLayout())
            .create();
        alertDialog.setCanceledOnTouchOutside(true);
        setOnShowListener();
    }

    public void show() {
        alertDialog.show();
    }

    // Setting onClickListeners in onShowListener to prevent dialog from being cancelled
    // whenever any button is pressed
    private void setOnShowListener() {
        alertDialog.setOnShowListener(dialog -> {
            Button positive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positive.setOnClickListener(v -> {
                int estimateInSeconds = Utils.getEstimateInSeconds(estimate.getMinutes(), timeOfResp);
                int timerDuration = getMinutes() * 60;

                if (timerDuration > 0) {
                    showNotification(Math.min(timerDuration, estimateInSeconds));
                    alertDialog.dismiss();
                } else {
                    errorTV.setVisibility(View.VISIBLE);
                }
                alarmIcon.setEnabled(true);
            });
        });
    }

    private View inflateAndRenderLayout() {
        View v = LayoutInflater.from(alarmIcon.getContext())
            .inflate(R.layout.dialog_set_timer_layout, null);
        ButterKnife.bind(this, v);

        int estimateInSeconds = Utils.getEstimateInSeconds(estimate.getMinutes(), timeOfResp);
        int estimateInMinutes = estimateInSeconds / 60;

        infoBlurb.setText(getBlurb(estimateInSeconds));
        tensDigitViewHolder = new DigitViewHolder(tensDigit,
            errorTV, estimateInMinutes / 10, 6);
        onesDigitViewHolder = new DigitViewHolder(onesDigit, errorTV,
            estimateInMinutes % 10, 10);

        return v;
    }

    private int getMinutes() {
        return tensDigitViewHolder.getValue() * 10 + onesDigitViewHolder.getValue();
    }

    private String getBlurb(int curEstimate) {
        return alarmIcon.getContext().getString(
            R.string.notif_blurb_format,
            leg.getOrigin(),
            leg.getDestination(),
            Utils.secondsToFormattedString(curEstimate));
    }

    private void showNotification(int duration) {
        Context context = alarmIcon.getContext();
        Intent intent = new Intent(context, TimerService.class);
        intent.setAction(Constants.UPDATE);
        intent.putExtra(Constants.TITLE, title);
        intent.putExtra(Constants.SECONDS, duration);
        context.startService(intent);
        Toast.makeText(context, R.string.starting_timer, Toast.LENGTH_SHORT).show();
    }
}
