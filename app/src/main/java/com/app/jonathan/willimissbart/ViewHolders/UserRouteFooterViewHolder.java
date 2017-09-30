package com.app.jonathan.willimissbart.ViewHolders;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.jonathan.willimissbart.Activities.AppActivities.SelectStationActivity;
import com.app.jonathan.willimissbart.Fragments.DeparturesFragment;
import com.app.jonathan.willimissbart.Listeners.Animations.Footers.FooterAnimListener;
import com.app.jonathan.willimissbart.Listeners.Animations.Generic.UpdateListener;
import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.Misc.Utils;
import com.app.jonathan.willimissbart.Persistence.Models.UserStationData;
import com.app.jonathan.willimissbart.R;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserRouteFooterViewHolder {
    @Bind(R.id.user_route_expansion) public LinearLayout footerBody;
    @Bind(R.id.user_route_expand) public IconTextView expandCollapseIcon;
    @Bind(R.id.user_route_origin) public TextView origin;
    @Bind(R.id.user_route_dest) public TextView destination;
    @Bind(R.id.user_route_minutes) public EditText minutes;

    private DeparturesFragment departuresFragment;
    private ValueAnimator expandAnimation;
    private ValueAnimator collapseAnimation;
    // userData is the user's data with pending changes which may or mat not have been persisted
    private List<UserStationData> userData;

    public UserRouteFooterViewHolder(View v, DeparturesFragment departuresFragment,
                                     List<UserStationData> userData) {
        ButterKnife.bind(this, v);
        this.departuresFragment = departuresFragment;
        origin.setText(userData.get(0).getAbbr());
        destination.setText(userData.get(1).getAbbr());
        this.userData = userData;
        initAnimations();
    }

    @OnClick(R.id.user_route_expand)
    public void onExpandOrCollapse() {
        expandCollapseIcon.setEnabled(false);
        Utils.hideKeyboard((Activity) footerBody.getContext());
        if (footerBody.getVisibility() == View.GONE) {
            footerBody.setVisibility(View.VISIBLE);
            expandAnimation.start();
        } else {
            collapseAnimation.start();
        }
    }

    @OnClick({R.id.user_route_origin, R.id.user_route_dest})
    public void onChangeStationReq(View v) {
        Intent intent = new Intent(footerBody.getContext(), SelectStationActivity.class);
        boolean isOrigin = v.getId() == R.id.user_route_origin;
        intent.putExtra(Constants.TITLE, isOrigin ? R.string.select_origin : R.string.select_dest);
        intent.putExtra(Constants.NOT_THIS_INDEX, isOrigin ?
            userData.get(1).getStationIndex() : userData.get(0).getStationIndex());
        ((Activity) footerBody.getContext())
            .startActivityForResult(intent, Constants.UPDATING_STATIONS);
    }

    @OnClick(R.id.user_route_update)
    public void onUserRouteUpdate() {
        Utils.hideKeyboard((Activity) footerBody.getContext());
        departuresFragment.persistUpdatesAndRefresh();
    }

    @OnClick(R.id.user_route_swap)
    public void onSwap() {
        UserStationData tmp = userData.get(0);
        userData.set(0, userData.get(1));
        userData.set(1, tmp);

        origin.setText(userData.get(0).getAbbr());
        destination.setText(userData.get(1).getAbbr());
    }

    public void updateStations(int resultCode, String newAbbr) {
        TextView abbrTextView = (resultCode == Constants.UPDATED_ORIGIN) ? origin : destination;
        abbrTextView.setText(newAbbr);
    }

    public void initAnimations() {
        footerBody.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int measuredHeight = footerBody.getMeasuredHeight();

        expandAnimation = ValueAnimator.ofInt(0, measuredHeight);
        expandAnimation.setDuration(Constants.SHORT_DURATION);
        expandAnimation.addUpdateListener(new UpdateListener(footerBody));
        expandAnimation.addListener(new FooterAnimListener(this, View.VISIBLE));

        collapseAnimation = ValueAnimator.ofInt(measuredHeight, 0);
        collapseAnimation.setDuration(Constants.SHORT_DURATION);
        collapseAnimation.addUpdateListener(new UpdateListener(footerBody));
        collapseAnimation.addListener(new FooterAnimListener(this, View.GONE));
    }
}
