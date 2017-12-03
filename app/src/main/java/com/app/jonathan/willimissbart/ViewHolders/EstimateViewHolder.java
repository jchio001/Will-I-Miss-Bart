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
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EstimateViewHolder extends ViewHolder {
    private static final Set<String> LIGHT_HEX_CODES = ImmutableSet.of(
        "#ffffff", "#ffff33");
    private static final int RENDER_ESTIMATE = 0;
    private static final int RENDER_LOADING_ESTIMATES = 1;
    private static final int RENDER_NO_ESTIMATES = 2;
    private static Map<String, ColorDrawable> hexToDrawableMap = Maps.newHashMap();

    @Bind(R.id.icon_wrapper) FrameLayout iconWrapper;
    @Bind(R.id.subway_icon) IconTextView subwayIcon;
    @Bind(R.id.set_alarm) IconTextView setAlarm;
    @Bind(R.id.leaving_in_text) TextView departureInfo;

    private Leg leg;
    private List<Estimate> estimates;

    private long timeOfResp;

    public EstimateViewHolder(View v) {
        super(v);
        ButterKnife.bind(this, v);
    }

    @OnClick(R.id.departures_layout)
    public void onDepartureClick() {
        if (leg != null && estimates != null && !estimates.isEmpty()) {
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
        Estimate estimate = Iterables.getFirst(estimates, null);
        if (estimate != null) {
            if (!estimate.getMinutes().equals("Leaving")) {
                if (Utils.getEstimateInSeconds(estimate.getMinutes(), timeOfResp) < 45) {
                    Toast.makeText(itemView.getContext(),
                        R.string.too_late, Toast.LENGTH_SHORT).show();
                    return;
                }

                // Passed in estimates already factors in current time!
                setAlarm.setEnabled(false);
                new NotificationAlertDialog(
                    itemView.getContext(),
                    setAlarm, leg,
                    estimate,
                    timeOfResp).show();
            } else {
                Toast.makeText(itemView.getContext(),
                    R.string.r_u_stupid, Toast.LENGTH_SHORT).show();
            }
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
        Estimate estimate = Iterables.getFirst(estimates, null);
        if (estimate != null) {
            int currentEstimate = Utils.getEstimateInSeconds(
                !estimate.getMinutes().equals("Leaving") ? estimate.getMinutes() : "0",
                timeOfResp);
            if (currentEstimate > 0) {
                return "is leaving in about " + String.valueOf(currentEstimate / 60) + " minutes";
            } else if (-60 <= currentEstimate && currentEstimate <= 0) {
                return "is leaving now!";
            } else {
                return "probably already left...";
            }
        } else {
            return "";
        }
    }

    private int getRenderingState(List<Estimate> estimates) {
        if (estimates != null) {
            if (estimates.size() > 0) {
                return RENDER_ESTIMATE;
            } else {
                return RENDER_NO_ESTIMATES;
            }
        } else {
            return RENDER_LOADING_ESTIMATES;
        }
    }

    private void renderEstimate(Estimate estimate) {
        iconWrapper.setVisibility(View.VISIBLE);
        setAlarm.setVisibility(View.VISIBLE);

        subwayIcon.setTextColor(ContextCompat.getColor(itemView.getContext(),
            LIGHT_HEX_CODES.contains(estimate.getHexColor()) ? R.color.black : R.color.white));
        subwayIcon.setBackground(getDrawable(estimate.getHexColor()));

        departureInfo.setText(estimate.getEstimateAsString());
    }

    private void renderLoadingEstimates() {
        iconWrapper.setVisibility(View.INVISIBLE);
        setAlarm.setVisibility(View.INVISIBLE);
        departureInfo.setText(R.string.loading);
    }

    private void renderNoEstimates() {
        iconWrapper.setVisibility(View.GONE);
        setAlarm.setVisibility(View.GONE);
        departureInfo.setText(R.string.no_estimates);
    }

    public void renderWithEstimateList(Leg leg, List<Estimate> estimates, long timeOfResp) {
        this.leg = leg;
        this.estimates = estimates;
        this.timeOfResp = timeOfResp;

        int renderingState = getRenderingState(estimates);
        if (renderingState == RENDER_ESTIMATE) {
            renderEstimate(Iterables.getFirst(estimates, null));
        } else if (renderingState == RENDER_LOADING_ESTIMATES) {
            renderLoadingEstimates();
        } else if (renderingState == RENDER_NO_ESTIMATES) {
            renderNoEstimates();
        } else {
            throw new IllegalArgumentException("Invalid rendering state.");
        }
    }

    public void renderWithEstimate(Leg leg, Estimate estimate, long timeOfResp) {
        this.leg = leg;
        this.estimates = Lists.newArrayList(estimate);
        this.timeOfResp = timeOfResp;

        if (estimate != null) {
            renderEstimate(estimate);
        } else {
            renderNoEstimates();
        }
    }
}
