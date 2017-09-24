package com.app.jonathan.willimissbart.ViewHolders;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.app.jonathan.willimissbart.API.Models.DeparturesFeedModels.FlattenedEstimate;
import com.app.jonathan.willimissbart.Dialogs.NotificationAlertDialog;
import com.app.jonathan.willimissbart.R;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeparturesCardViewHolder extends ViewHolder {
    private static final Set<String> LIGHT_HEX_CODES = ImmutableSet.of(
        "#ffffff", "#ffff33");

    @Bind(R.id.departure_card) CardView departureCard;
    @Bind(R.id.subway_icon) IconTextView subwayIcon;
    @Bind(R.id.set_alarm) IconTextView setAlarm;
    @Bind(R.id.departures_info_blurb) TextView infoBlurb;
    @Bind(R.id.leaving_in_text) TextView departureInfo;

    private final Context context;
    private FlattenedEstimate flattenedEstimate;

    public static Map<String, ColorDrawable> hexToDrawableMap = Maps.newHashMap();

    public DeparturesCardViewHolder(View v, Context context) {
        super(v);
        ButterKnife.bind(this, v);
        this.context = context;
    }

    @OnClick(R.id.departures_info_blurb)
    public void onInfoBlurbClick() {
        Toast.makeText(context, infoBlurb.getText().toString(), Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.leaving_in_text)
    public void onDepartureClick() {
        if (flattenedEstimate.isSuccessful()) {
            Toast.makeText(context,
                context.getString(
                    R.string.departures_departure_format,
                    flattenedEstimate.getOriginName(),
                    flattenedEstimate.getDestName(),
                    flattenedEstimate.getFormattedRealTimeEstimate()
                ), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.set_alarm)
    public void onAlarmIconClick() {
        if (!flattenedEstimate.getMinutes().equals("Leaving")) {
            if (flattenedEstimate.getRealTimeEstimate() < 45) {
                Toast.makeText(context, R.string.too_late, Toast.LENGTH_SHORT).show();
                return;
            }

            // Passed in estimate already factors in current time!
            setAlarm.setEnabled(false);
            new NotificationAlertDialog(context, setAlarm, flattenedEstimate).show();
        } else {
            Toast.makeText(context, R.string.r_u_stupid, Toast.LENGTH_SHORT).show();
        }
    }

    public void setUp(FlattenedEstimate flattenedEstimate) {
        this.flattenedEstimate = flattenedEstimate;
        if (flattenedEstimate.getEstimate() != null) {
            infoBlurb.setVisibility(View.GONE);
            departureCard.setVisibility(View.VISIBLE);
            subwayIcon.setVisibility(View.VISIBLE);
            setAlarm.setVisibility(View.VISIBLE);

            subwayIcon.setTextColor(ContextCompat.getColor(context,
                LIGHT_HEX_CODES.contains(
                    flattenedEstimate.getHexColor()) ? R.color.black : R.color.white));
            subwayIcon.setBackground(getDrawable(flattenedEstimate.getHexColor()));
            departureInfo.setText(flattenedEstimate.getTitle());
        } else {
            if (flattenedEstimate.isSuccessful()) {
                infoBlurb.setVisibility(View.VISIBLE);
                departureCard.setVisibility(View.GONE);

                infoBlurb.setText(flattenedEstimate.getTitle());
            } else {
                infoBlurb.setVisibility(View.GONE);
                departureCard.setVisibility(View.VISIBLE);
                subwayIcon.setVisibility(View.GONE);
                setAlarm.setVisibility(View.GONE);

                departureInfo.setText(flattenedEstimate.getTitle());
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
}
