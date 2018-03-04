package com.app.jonathan.willimissbart.viewholder;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.app.jonathan.willimissbart.api.Models.Etd.Estimate;
import com.app.jonathan.willimissbart.api.Models.Routes.Leg;
import com.app.jonathan.willimissbart.dialog.NotificationAlertDialog;
import com.app.jonathan.willimissbart.misc.NotGuava;
import com.app.jonathan.willimissbart.misc.Utils;
import com.app.jonathan.willimissbart.R;
import com.joanzapata.iconify.widget.IconTextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EstimateViewHolder extends ViewHolder {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({RenderingState.RENDER_ESTIMATE, RenderingState.RENDER_LOADING_ESTIMATES,
        RenderingState.RENDER_NO_ESTIMATES})
    @interface RenderingState {
        int RENDER_ESTIMATE = 0;
        int RENDER_LOADING_ESTIMATES = 1;
        int RENDER_NO_ESTIMATES = 2;
    }

    @Bind(R.id.route_color_icon) View routeColorIcon;
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
        Estimate estimate = NotGuava.getFirst(estimates, null);
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
                    setAlarm, leg,
                    estimate,
                    timeOfResp).show();
            } else {
                Toast.makeText(itemView.getContext(),
                    R.string.r_u_stupid, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getRealTimeEstimateText() {
        Estimate estimate = NotGuava.getFirst(estimates, null);
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

    private @RenderingState int getRenderingState(List<Estimate> estimates) {
        if (estimates != null) {
            if (estimates.size() > 0) {
                return RenderingState.RENDER_ESTIMATE;
            } else {
                return RenderingState.RENDER_NO_ESTIMATES;
            }
        } else {
            return RenderingState.RENDER_LOADING_ESTIMATES;
        }
    }

    private void renderEstimate(Estimate estimate) {
        routeColorIcon.setVisibility(View.VISIBLE);
        setAlarm.setVisibility(View.VISIBLE);

        GradientDrawable circleBackground = (GradientDrawable) routeColorIcon.getBackground();
        circleBackground.setColor(Color.parseColor(estimate.getHexColor()));

        departureInfo.setText(estimate.getEstimateAsString());
    }

    private void renderLoadingEstimates() {
        routeColorIcon.setVisibility(View.INVISIBLE);
        setAlarm.setVisibility(View.INVISIBLE);
        departureInfo.setText(R.string.loading);
    }

    private void renderNoEstimates() {
        routeColorIcon.setVisibility(View.GONE);
        setAlarm.setVisibility(View.GONE);
        departureInfo.setText(R.string.no_estimates);
    }

    public void renderWithEstimateList(Leg leg, List<Estimate> estimates, long timeOfResp) {
        this.leg = leg;
        this.estimates = estimates;
        this.timeOfResp = timeOfResp;

        switch (getRenderingState(estimates)) {
            case RenderingState.RENDER_ESTIMATE:
                renderEstimate(NotGuava.getFirst(estimates, null));
                break;
            case RenderingState.RENDER_LOADING_ESTIMATES:
                renderLoadingEstimates();
                break;
            case RenderingState.RENDER_NO_ESTIMATES:
                renderNoEstimates();
                break;
        }
    }

    public void renderWithEstimate(Leg leg, Estimate estimate, long timeOfResp) {
        this.leg = leg;
        this.estimates = NotGuava.newArrayList(estimate);
        this.timeOfResp = timeOfResp;

        if (estimate != null) {
            renderEstimate(estimate);
        } else {
            renderNoEstimates();
        }
    }
}
