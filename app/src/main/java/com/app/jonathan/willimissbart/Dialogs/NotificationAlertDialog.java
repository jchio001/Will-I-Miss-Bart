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

import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.Service.TimerService;
import com.app.jonathan.willimissbart.ViewHolders.DigitViewHolder;

import butterknife.Bind;
import butterknife.ButterKnife;

// Did not create custom classes for listeners because the code's pretty straightforward in them
// and will only be used within this class. Also, my package organization is already super bloaty
// and I'd rather not add more packages that contain 1 class with barely any code.
public class NotificationAlertDialog extends AlertDialog {
    @Bind(R.id.tens_digit) LinearLayout tensDigit;
    @Bind(R.id.ones_digit) LinearLayout onesDigit;
    @Bind(R.id.error_tv) TextView errorTV;

    private AlertDialog alertDialog;
    private DigitViewHolder tensDigitViewHolder;
    private DigitViewHolder onesDigitViewHolder;
    private String title;

    private int baseAlarmDuration;

    public NotificationAlertDialog(Context context,
                                   String title,
                                   int baseAlarmDuration) {
        super(context);
        this.title = title;
        this.baseAlarmDuration = baseAlarmDuration;
        View v = LayoutInflater.from(context).inflate(R.layout.layout_timer_interval, null);
        ButterKnife.bind(this, v);
        tensDigitViewHolder = new DigitViewHolder(tensDigit, errorTV, 6);
        onesDigitViewHolder = new DigitViewHolder(onesDigit, errorTV, 10);
        alertDialog = new Builder(new ContextThemeWrapper(context, R.style.AlertDialogTheme))
            .setPositiveButton("OK", null)
            .setNegativeButton("CANCEL", null)
            .setTitle(R.string.timer_notif_title)
            .setView(v)
            .create();
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
                        int minutes = getMinutes();
                        if (baseAlarmDuration - minutes * 60 >= 60) {
                            showNotification(minutes);
                            alertDialog.dismiss();
                        } else {
                            errorTV.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });
    }

    private int getMinutes() {
        return tensDigitViewHolder.getValue() * 10 + onesDigitViewHolder.getValue();
    }

    private void showNotification(int minutesEarly) {
        Intent intent = new Intent(getContext(), TimerService.class);
        intent.setAction(Constants.UPDATE);
        intent.putExtra(Constants.TITLE, title);
        intent.putExtra(Constants.SECONDS, baseAlarmDuration - minutesEarly * 60);
        getContext().startService(intent);
        Toast.makeText(getContext(), R.string.starting_timer, Toast.LENGTH_SHORT).show();
    }
}
