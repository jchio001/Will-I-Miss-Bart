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
import com.app.jonathan.willimissbart.fragment.UserDataManager;
import com.app.jonathan.willimissbart.fragment.UserDataManager.UserDataSubscriber;
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

    private UserDataManager userDataManager;
    private SPManager spManager;

    private UserDataSubscriber userDataSubscriber = this::updateFooterText;

    public StationsFooterViewHolder(View v, UserDataManager userDataManager) {
        ButterKnife.bind(this, v);
        this.contentView = v;
        this.userDataManager = userDataManager;
        this.spManager = new SPManager(v.getContext());
        updateFooterText();
        userDataManager.subscribe(userDataSubscriber);
    }

    public StationsFooterViewHolder setAdapter(OriginDestStationsAdapter adapter) {
        this.adapter = adapter;
        return this;
    }

    public void updateFooterText() {
        Context context = origin.getContext();
        origin.setText(context.getString(R.string.stn_from,
            userDataManager.getOriginStationData().getAbbr()));
        dest.setText(context.getString(R.string.stn_to,
            userDataManager.getDestinationStationData().getAbbr()));
    }

    @OnClick(R.id.swap)
    public void onSwap() {
        ArrayList<UserStationData> userData = userDataManager.getUserDataCopy();

        UserStationData tmp = userData.get(0);
        userData.set(0, userData.get(1));
        userData.set(1, tmp);

        Context context = contentView.getContext();
        origin.setText(context.getString(R.string.stn_from, userData.get(0).getAbbr()));
        dest.setText(context.getString(R.string.stn_to, userData.get(1).getAbbr()));

        userDataManager.ignoreNextBroadcast(userDataSubscriber);
        userDataManager.update(userData, false);
    }

    @OnClick(R.id.done)
    public void done() {
        if (userDataManager.isDataFullyInitialized()) {
            Activity context = (Activity) contentView.getContext();
            ArrayList<UserStationData> userStationData = userDataManager.getUserDataCopy();
            spManager.persistUserData(userStationData);
            Intent intent = new Intent(context, MainActivity.class);
            intent.putParcelableArrayListExtra(Constants.USER_DATA, userStationData);
            contentView.getContext().startActivity(intent);
            context.finish();
        }
    }
}
