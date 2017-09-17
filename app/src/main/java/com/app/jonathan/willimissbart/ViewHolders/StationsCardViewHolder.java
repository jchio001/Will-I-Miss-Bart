package com.app.jonathan.willimissbart.ViewHolders;


import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.jonathan.willimissbart.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class StationsCardViewHolder {
    @Bind(R.id.stn_main_view) public LinearLayout mainView;
    @Bind(R.id.stn_abbr) public TextView abbr;
    @Bind(R.id.origin_or_dest) public TextView originOrDest;

    public StationsCardViewHolder(View v) {
        ButterKnife.bind(this, v);
    }
}
