package com.app.jonathan.willimissbart.Dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.jonathan.willimissbart.API.Models.Etd.Estimate;
import com.app.jonathan.willimissbart.API.Models.Routes.Leg;
import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.Misc.Utils;
import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.Service.TimerService;
import com.app.jonathan.willimissbart.ViewHolders.DigitViewHolder;
import com.joanzapata.iconify.widget.IconTextView;

import butterknife.Bind;
import butterknife.ButterKnife;

// Did not create custom classes for listeners because the code's pretty straightforward in them
// and will only be used within this class. Also, my package organization is already super bloaty
// and I'd rather not add more packages that contain 1 class with barely any code.
public class NotificationAlertDialog extends AlertDialog {
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

    public NotificationAlertDialog(Context context,
                                   final IconTextView alarmIcon,
                                   Leg leg,
                                   Estimate estimate,
                                   long timeOfResp) {
        super(context);
        this.alarmIcon = alarmIcon;
        this.leg = leg;
        this.estimate = estimate;
        this.timeOfResp = timeOfResp;
        this.title = context.getString(
            R.string.notif_title_format,
            leg.getOrigin(),
            leg.getDestination());
        View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_set_timer_layout, null);
        ButterKnife.bind(this, v);

        int estimateInSeconds = Utils.getEstimateInSeconds(
            estimate.getMinutes(), timeOfResp);
        int estimateInMinutes = estimateInSeconds / 60;

        infoBlurb.setText(getBlurb(estimateInSeconds));
        tensDigitViewHolder = new DigitViewHolder(tensDigit, errorTV, estimateInMinutes / 10, 6);
        onesDigitViewHolder = new DigitViewHolder(onesDigit, errorTV, estimateInMinutes % 10, 10);

        alertDialog = new Builder(new ContextThemeWrapper(getContext(), R.style.AlertDialogTheme))
            .setPositiveButton("OK", null)
            .setNegativeButton("CANCEL", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alarmIcon.setEnabled(true);
                }
            })
            .setOnCancelListener(new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                   alarmIcon.setEnabled(true);
                }
            })
            .setTitle(R.string.timer_notif_title)
            .setView(v)
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
        alertDialog.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int estimateInSeconds = Utils.getEstimateInSeconds(estimate.getMinutes(), timeOfResp);
                        int timerDuration = getMinutes() * 60;
                        if (estimateInSeconds - timerDuration >= 60) {
                            showNotification(timerDuration);
                            alertDialog.dismiss();
                        } else {
                            errorTV.setVisibility(View.VISIBLE);
                        }
                        alarmIcon.setEnabled(true);
                    }
                });
            }
        });
    }

    private int getMinutes() {
        return tensDigitViewHolder.getValue() * 10 + onesDigitViewHolder.getValue();
    }

    private String getBlurb(int curEstimate) {
        return getContext().getString(
            R.string.notif_blurb_format,
            leg.getOrigin(),
            leg.getDestination(),
            Utils.secondsToFormattedString(curEstimate));
    }

    private void showNotification(int duration) {
        Intent intent = new Intent(getContext(), TimerService.class);
        intent.setAction(Constants.UPDATE);
        intent.putExtra(Constants.TITLE, title);
        intent.putExtra(Constants.SECONDS, duration);
        getContext().startService(intent);
        Toast.makeText(getContext(), R.string.starting_timer, Toast.LENGTH_SHORT).show();
    }
}
