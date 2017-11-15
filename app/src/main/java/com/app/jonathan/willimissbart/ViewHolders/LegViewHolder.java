package com.app.jonathan.willimissbart.ViewHolders;


import android.content.Context;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.jonathan.willimissbart.API.Models.Etd.Estimate;
import com.app.jonathan.willimissbart.API.Models.Routes.Leg;
import com.app.jonathan.willimissbart.Misc.EstimatesManager;
import com.app.jonathan.willimissbart.R;

import java.util.List;

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

    public void setUp(Leg leg, long timeOfResp) {
        this.leg = leg;
        Context context = itemView.getContext();
        trainInfo.setText(context.getString(R.string.train_info,
            leg.getOrigin(), leg.getTrainHeadStation(), leg.getOrigTimeMin()));
        stopInfo.setText(context.getString(R.string.stop_info,
            leg.getDestination(), leg.getDestTimeMin()));

        displayEstimates(leg.getOrigin(), timeOfResp);
    }

    public void displayEstimates(String origin, long timeOfResp) {
        List<Estimate> estimates =
            EstimatesManager.getEstimates(origin + leg.getTrainHeadStation());
        ViewGroup parent = (ViewGroup) itemView;
        if (estimates != null && parent.getChildCount() == 2) {
            for (Estimate estimate : estimates) {
                View v = LayoutInflater.from(itemView.getContext())
                    .inflate(R.layout.layout_departure, null, false);
                new DepartureViewHolder(v).setUp(leg, estimate, timeOfResp);
                parent.addView(v);
            }
        }
    }
}
