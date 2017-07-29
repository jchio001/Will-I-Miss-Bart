package com.example.jonathan.willimissbart.ViewHolders;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jonathan.willimissbart.API.Models.EtdModels.Estimate;
import com.example.jonathan.willimissbart.API.Models.EtdModels.Etd;
import com.example.jonathan.willimissbart.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnLongClick;

public class EtdViewHolder {
    @Bind(R.id.etd_layout) LinearLayout etdLayout;
    @Bind(R.id.station_abbr) TextView stationAbbrTv;

    List<EstimateViewHolder> estimateViewHolders = new ArrayList<>();
    private final Context context;
    private String destinationName;

    public EtdViewHolder(View v, Context context, Etd etd) {
        ButterKnife.bind(this, v);
        this.context = context;
        this.destinationName = etd.getDestination();
        stationAbbrTv.setText(etd.getAbbreviation());
        setUpEstimates(etd.getEstimates());
    }

    @OnLongClick(R.id.station_abbr)
    public boolean onAbbrLongClick() {
        Toast.makeText(context, destinationName, Toast.LENGTH_SHORT).show();
        return true;
    }

    private void setUpEstimates(List<Estimate> estimates) {
        if (estimates != null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE
            );

            for (int i = 1; i < estimates.size() + 1; ++i) {
                View v = vi.inflate(R.layout.estimate_layout, null);
                estimateViewHolders.add(new EstimateViewHolder(v, context, estimates.get(i - 1)));
                etdLayout.addView(v, i);
            }
        }
    }
}
