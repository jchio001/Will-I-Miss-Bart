package com.app.jonathan.willimissbart.ViewHolders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.jonathan.willimissbart.API.Models.EtdModels.Etd;
import com.app.jonathan.willimissbart.API.Models.EtdModels.EtdStation;
import com.app.jonathan.willimissbart.Persistence.Models.UserBartData;
import com.app.jonathan.willimissbart.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeparturesViewHolder {
    @Bind(R.id.parent_station_abbr) TextView parentAbbr;
    @Bind(R.id.main_bart_data_layout) LinearLayout mainBartDataLayout;

    private List<EtdsViewHolder> etdsViewHolders = new ArrayList<>();
    private ErrorTextViewViewHolder errorTextViewViewHolder;
    private final Context context;
    private String abbr;
    private String name;
    private boolean success;

    public DeparturesViewHolder(View v,
                                Context context,
                                EtdStation station,
                                UserBartData data,
                                boolean success,
                                long timeInSeconds) {
        ButterKnife.bind(this, v);
        this.context = context;
        this.success = success;
        this.abbr = station.getAbbr();
        this.name = station.getName();
        parentAbbr.setText(abbr);
        setUpEtds(station.getEtds(), data, timeInSeconds);
    }

    private void setUpEtds(List<Etd> etds, UserBartData data, long timeInSeconds) {
        LayoutInflater vi = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        if (etds != null) {
            for (int i = 1; i < etds.size() + 1; ++i) {
                View v = vi.inflate(R.layout.etd_layout, null);
                etdsViewHolders.add(new EtdsViewHolder(v, context,
                    etds.get(i - 1), abbr, timeInSeconds));
                mainBartDataLayout.addView(v, i);
            }
        } else {
           View v = vi.inflate(R.layout.etd_error_tv, null);
            errorTextViewViewHolder = new ErrorTextViewViewHolder(v, data, success);
            mainBartDataLayout.addView(v, 1);
        }
    }

    @OnClick(R.id.parent_station_abbr)
    public void onAbbrLongPress() {
        Toast.makeText(context, name, Toast.LENGTH_LONG).show();
    }
}
