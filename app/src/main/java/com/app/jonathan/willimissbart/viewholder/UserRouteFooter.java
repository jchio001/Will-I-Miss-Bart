package com.app.jonathan.willimissbart.viewholder;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.activity.core.SelectStationActivity;
import com.app.jonathan.willimissbart.fragment.UserDataManager;
import com.app.jonathan.willimissbart.listener.animation.Footers.FooterAnimListener;
import com.app.jonathan.willimissbart.misc.Constants;
import com.app.jonathan.willimissbart.misc.Utils;
import com.app.jonathan.willimissbart.persistence.models.UserStationData;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserRouteFooter extends LinearLayout {

    @Bind(R.id.user_route_expansion) public LinearLayout footerBody;
    @Bind(R.id.user_route_expand) public IconTextView expandCollapseIcon;
    @Bind(R.id.user_route_origin) public TextView origin;
    @Bind(R.id.user_route_dest) public TextView destination;
    @Bind(R.id.include_return) public CheckBox includeReturn;

    private ValueAnimator expandAnimation;
    private ValueAnimator collapseAnimation;
    // userData is the user's data with pending changes which may or mat not have been persisted
    private ArrayList<UserStationData> userData;

    private UserDataManager userDataManager;

    private final AnimatorUpdateListener updateListener = new AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            footerBody.getLayoutParams().height = (Integer) animation.getAnimatedValue();
            footerBody.requestLayout();
        }
    };

    public UserRouteFooter(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        inflate(context, R.layout.layout_user_route_footer, this);
        ButterKnife.bind(this, this);
        createAnimations();
    }

    public void withUserDataManager(UserDataManager userDataManager) {
        this.userDataManager = userDataManager;

        this.userData = userDataManager.getUserDataCopy();
        origin.setText(userData.get(0).getAbbr());
        destination.setText(userData.get(1).getAbbr());
        includeReturn.setChecked(userDataManager.includeReturnRoute());
    }

    @OnClick(R.id.user_route_expand)
    public void onOrientationChanged() {
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
        collapseAnimation.start();
        Utils.hideKeyboard((Activity) footerBody.getContext());
        userDataManager.commitAndUpdate(userData, includeReturn.isChecked());
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
        if (footerBody.getVisibility() == View.GONE) {
            onOrientationChanged();
        }
    }

    private void createAnimations() {
        footerBody.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int measuredHeight = footerBody.getMeasuredHeight();

        expandAnimation = ValueAnimator.ofInt(0, measuredHeight);
        expandAnimation.setDuration(Constants.SHORT_DURATION);
        expandAnimation.addUpdateListener(updateListener);
        expandAnimation.addListener(new FooterAnimListener(this, View.VISIBLE));

        collapseAnimation = ValueAnimator.ofInt(measuredHeight, 0);
        collapseAnimation.setDuration(Constants.SHORT_DURATION);
        collapseAnimation.addUpdateListener(updateListener);
        collapseAnimation.addListener(new FooterAnimListener(this, View.GONE));
    }
}
