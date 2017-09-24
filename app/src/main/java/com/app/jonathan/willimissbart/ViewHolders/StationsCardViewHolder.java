package com.app.jonathan.willimissbart.ViewHolders;


import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.jonathan.willimissbart.Misc.Utils;
import com.app.jonathan.willimissbart.R;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StationsCardViewHolder {
    @Bind(R.id.stn_main_view) public LinearLayout mainView;
    @Bind(R.id.stn_abbr) public TextView abbr;
    @Bind(R.id.origin_or_dest) public TextView originOrDest;

    private WeakReference<StationInfoViewHolder> stationInfoViewHolder;

    public StationsCardViewHolder(View v, StationInfoViewHolder stationInfoViewHolder) {
        ButterKnife.bind(this, v);
        this.stationInfoViewHolder = new WeakReference<>(stationInfoViewHolder);
    }

    @OnClick(R.id.stn_info_icon)
    public void onMoreInfo() {
        if (stationInfoViewHolder != null && stationInfoViewHolder.get() != null) {
            Utils.hideKeyboard((Activity) mainView.getContext());
            stationInfoViewHolder.get().show(abbr.getText().toString());
        }
    }
}
