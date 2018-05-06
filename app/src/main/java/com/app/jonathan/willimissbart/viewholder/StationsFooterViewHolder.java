package com.app.jonathan.willimissbart.viewholder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.activity.core.MainActivity;
import com.app.jonathan.willimissbart.adapter.OriginDestStationsAdapter;
import com.app.jonathan.willimissbart.misc.Constants;
import com.app.jonathan.willimissbart.persistence.SPManager;
import com.app.jonathan.willimissbart.persistence.models.UserStationData;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StationsFooterViewHolder {

    @Bind(R.id.origin) public TextView origin;
    @Bind(R.id.dest) public TextView dest;
    @Bind(R.id.done) public Button done;

    public View contentView;
    private OriginDestStationsAdapter adapter;

    public StationsFooterViewHolder(View v) {
        ButterKnife.bind(this, v);
        this.contentView = v;
        updateFooterText("","");
    }

    public StationsFooterViewHolder setAdapter(OriginDestStationsAdapter adapter) {
        this.adapter = adapter;
        return this;
    }

    public void updateFooterText(String originAbbr, String destAbbr) {
        Context context = origin.getContext();
        origin.setText(context.getString(R.string.stn_from, originAbbr));
        dest.setText(context.getString(R.string.stn_to, destAbbr));
    }

    @OnClick(R.id.swap)
    public void onSwap() {
        adapter.swap();
    }

    @OnClick(R.id.done)
    public void done() {
        ArrayList<UserStationData> userData = adapter.getUserBartData();
        if (userData == null) {
            return;
        }

        Activity context = (Activity) contentView.getContext();
        SPManager.persistUserData(context, userData);
        Intent intent = new Intent(context, MainActivity.class);
        intent.putParcelableArrayListExtra(Constants.USER_DATA, userData);
        contentView.getContext().startActivity(intent);
        context.finish();
    }
}
