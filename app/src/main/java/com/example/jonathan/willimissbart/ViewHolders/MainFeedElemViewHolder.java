package com.example.jonathan.willimissbart.ViewHolders;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jonathan.willimissbart.API.Models.EtdModels.Etd;
import com.example.jonathan.willimissbart.API.Models.EtdModels.EtdStation;
import com.example.jonathan.willimissbart.Persistence.Models.UserBartData;
import com.example.jonathan.willimissbart.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainFeedElemViewHolder {
    @Bind(R.id.station_name) TextView stationName;
    @Bind(R.id.main_bart_data_layout) LinearLayout mainBartDataLayout;

    private List<EtdViewHolder> etdViewHolders = new ArrayList<>();
    private ErrorTextViewViewHolder errorTextViewViewHolder;
    private final Context context;

    boolean success;

    public MainFeedElemViewHolder(View v, Context context, EtdStation station, boolean success) {
        ButterKnife.bind(this, v);
        this.context = context;
        this.success = success;
        stationName.setText(station.getName());
        setUpEtds(station.getEtds());
    }

    private void setUpEtds(List<Etd> etds) {
        LayoutInflater vi = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE
        );
        if (etds != null) {
            for (int i = 1; i < etds.size() + 1; ++i) {
                View v = vi.inflate(R.layout.etd_layout, null);
                etdViewHolders.add(new EtdViewHolder(v, context, etds.get(i - 1)));
                mainBartDataLayout.addView(v, i);
            }
        } else {
           View v = vi.inflate(R.layout.etd_error_tv, null);
            errorTextViewViewHolder = new ErrorTextViewViewHolder(v, success);
            mainBartDataLayout.addView(v, 1);
        }
    }
}
