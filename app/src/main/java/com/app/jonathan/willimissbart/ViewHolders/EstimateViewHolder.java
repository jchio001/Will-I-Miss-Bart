package com.app.jonathan.willimissbart.ViewHolders;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.TextView;

import com.app.jonathan.willimissbart.API.Models.EtdModels.Estimate;
import com.app.jonathan.willimissbart.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EstimateViewHolder {
    @Bind(R.id.color) View colorView;
    @Bind(R.id.estimate) TextView estimateTV;

    private final Context context;

    public EstimateViewHolder(View v, Context context, Estimate estimate) {
        ButterKnife.bind(this, v);
        this.context = context;
        colorView.setBackground(getDrawable(estimate.getHexcolor()));
        estimateTV.setText(getEstimateText(estimate.getMinutes()));
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
}