package com.app.jonathan.willimissbart.ViewHolders;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.jonathan.willimissbart.API.Models.Etd.Estimate;
import com.app.jonathan.willimissbart.API.Models.Routes.Leg;
import com.app.jonathan.willimissbart.Dialogs.NotificationAlertDialog;
import com.app.jonathan.willimissbart.Misc.Utils;
import com.app.jonathan.willimissbart.R;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DepartureViewHolder extends ViewHolder {
    private static final Set<String> LIGHT_HEX_CODES = ImmutableSet.of(
        "#ffffff", "#ffff33");
    public static Map<String, ColorDrawable> hexToDrawableMap = Maps.newHashMap();

    @Bind(R.id.icon_wrapper) FrameLayout iconWrapper;
    @Bind(R.id.subway_icon) IconTextView subwayIcon;
    @Bind(R.id.set_alarm) IconTextView setAlarm;
    @Bind(R.id.leaving_in_text) TextView departureInfo;

    private Leg leg;
    private Estimate estimate;

    private long timeOfResp;

    public DepartureViewHolder(View v) {
        super(v);
        ButterKnife.bind(this, v);
    }

    @OnClick(R.id.leaving_in_text)
    public void onDepartureClick() {
        if (leg != null && estimate != null) {
            Context context = itemView.getContext();
            Toast.makeText(context,
                context.getString(
                    R.string.departures_departure_format,
                    leg.getOrigin(),
                    leg.getDestination(),
                    getRealTimeEstimateText()
                ), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.set_alarm)
    public void onAlarmIconClick() {
        if (!estimate.getMinutes().equals("Leaving")) {
            if (Utils.getEstimateInSeconds(estimate.getMinutes(), timeOfResp) < 45) {
                Toast.makeText(itemView.getContext(), R.string.too_late, Toast.LENGTH_SHORT).show();
                return;
            }

            // Passed in estimate already factors in current time!
            setAlarm.setEnabled(false);
            new NotificationAlertDialog(
                itemView.getContext(),
                setAlarm, leg,
                estimate,
                timeOfResp).show();
        } else {
            Toast.makeText(itemView.getContext(), R.string.r_u_stupid, Toast.LENGTH_SHORT).show();
        }
    }

    public void setUp(Leg leg, Estimate estimate, long timeOfResp) {
        this.leg = leg;
        this.estimate = estimate;
        this.timeOfResp = timeOfResp;
        if (estimate != null) {
            iconWrapper.setVisibility(View.VISIBLE);
            setAlarm.setVisibility(View.VISIBLE);

            subwayIcon.setTextColor(ContextCompat.getColor(itemView.getContext(),
                LIGHT_HEX_CODES.contains(
                    estimate.getHexColor()) ? R.color.black : R.color.white));
            subwayIcon.setBackground(getDrawable(estimate.getHexColor()));

            departureInfo.setText(estimate.getEstimateAsString());
        } else {
            iconWrapper.setVisibility(View.INVISIBLE);
            setAlarm.setVisibility(View.INVISIBLE);

            // TODO: polish this!
            departureInfo.setText("Loading...");
        }
    }

    private Drawable getDrawable(String color) {
        ColorDrawable drawable;
        if (hexToDrawableMap.containsKey(color)) {
            drawable = hexToDrawableMap.get(color);
        } else {
            drawable = new ColorDrawable(Color.parseColor(color));
            drawable.setColor(Color.parseColor(color));
            hexToDrawableMap.put(color, drawable);
        }
        return drawable;
    }

    private String getRealTimeEstimateText() {
        int currentEstimate = Utils.getEstimateInSeconds(
            !estimate.getMinutes().equals("Leaving") ? estimate.getMinutes() : "0",
            timeOfResp);
        if (currentEstimate > 0) {
            return "is leaving in " + Utils.secondsToFormattedString(currentEstimate);
        } else if (-60 <= currentEstimate && currentEstimate <= 0) {
            return "is leaving now!";
        } else {
            return "probably already left...";
        }
    }
}
