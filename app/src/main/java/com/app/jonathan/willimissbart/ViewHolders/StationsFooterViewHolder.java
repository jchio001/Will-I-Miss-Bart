package com.app.jonathan.willimissbart.ViewHolders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.app.jonathan.willimissbart.Activities.AppActivities.MainActivity;
import com.app.jonathan.willimissbart.Adapters.StationsAdapter;
import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.Persistence.Models.UserStationData;
import com.app.jonathan.willimissbart.Persistence.SPSingleton;
import com.app.jonathan.willimissbart.R;
import com.google.common.collect.Lists;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StationsFooterViewHolder {
    @Bind(R.id.origin) public TextView origin;
    @Bind(R.id.dest) public TextView dest;
    @Bind(R.id.done) public Button done;

    public View contentView;
    private StationsAdapter adapter;

    public StationsFooterViewHolder(View v) {
        ButterKnife.bind(this, v);
        this.contentView = v;
        updateFooterText("","");
    }

    public StationsFooterViewHolder setAdapter(StationsAdapter adapter) {
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
        UserStationData[] userData = adapter.getUserBartData();
        if (userData == null) {
            return;
        }

        Activity context = (Activity) contentView.getContext();
        SPSingleton.getInstance(context)
            .persistUserData(userData);
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Constants.USER_DATA, userData);
        contentView.getContext().startActivity(intent);
        context.finish();
    }
}
