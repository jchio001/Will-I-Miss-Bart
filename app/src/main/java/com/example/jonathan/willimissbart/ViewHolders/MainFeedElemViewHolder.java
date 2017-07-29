package com.example.jonathan.willimissbart.ViewHolders;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jonathan.willimissbart.API.Models.EtdModels.Etd;
import com.example.jonathan.willimissbart.API.Models.EtdModels.EtdStation;
import com.example.jonathan.willimissbart.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnLongClick;

public class MainFeedElemViewHolder {
    @Bind(R.id.parent_station_abbr) TextView parentAbbr;
    @Bind(R.id.main_bart_data_layout) LinearLayout mainBartDataLayout;

    private List<EtdViewHolder> etdViewHolders = new ArrayList<>();
    private ErrorTextViewViewHolder errorTextViewViewHolder;
    private final Context context;
    private String name;
    boolean success;

    public MainFeedElemViewHolder(View v, Context context, EtdStation station, boolean success) {
        ButterKnife.bind(this, v);
        this.context = context;
        this.success = success;
        parentAbbr.setText(station.getAbbr());
        this.name = station.getName();
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

    @OnLongClick(R.id.parent_station_abbr)
    public boolean onAbbrLongPress() {
        Toast.makeText(context, name, Toast.LENGTH_LONG).show();
        return true;
    }
}
