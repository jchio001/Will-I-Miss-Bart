package com.example.jonathan.willimissbart.ViewHolders;


import android.view.View;
import android.widget.TextView;

import com.example.jonathan.willimissbart.Persistence.Models.UserBartData;
import com.example.jonathan.willimissbart.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainFeedElemViewHolder {
    @Bind(R.id.station_name) TextView stationName;

    public MainFeedElemViewHolder(View v, UserBartData data) {
        ButterKnife.bind(this, v);
        stationName.setText(data.getStation());
    }
}
