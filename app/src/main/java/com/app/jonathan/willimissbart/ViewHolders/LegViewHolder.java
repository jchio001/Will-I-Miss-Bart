package com.app.jonathan.willimissbart.ViewHolders;


import android.content.Context;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.TextView;

import com.app.jonathan.willimissbart.API.Models.Routes.Leg;
import com.app.jonathan.willimissbart.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LegViewHolder extends ViewHolder {
    @Bind(R.id.train_info) TextView trainInfo;
    @Bind(R.id.stop_info) TextView stopInfo;

    private Leg leg;

    public LegViewHolder(View v) {
        super(v);
        ButterKnife.bind(this, v);
    }

    public void setUp(Leg leg) {
        this.leg = leg;
        Context context = itemView.getContext();
        trainInfo.setText(context.getString(R.string.train_info,
            leg.getOrigin(), leg.getTrainHeadStation(), leg.getOrigTimeMin()));
        stopInfo.setText(context.getString(R.string.stop_info,
            leg.getDestination(), leg.getDestTimeMin()));
    }
}
