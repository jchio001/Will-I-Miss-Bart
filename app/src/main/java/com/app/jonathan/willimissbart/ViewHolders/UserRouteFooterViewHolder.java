package com.app.jonathan.willimissbart.ViewHolders;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.jonathan.willimissbart.Listeners.Animations.Footers.FooterAnimListener;
import com.app.jonathan.willimissbart.Listeners.Animations.Footers.FooterUpdateListener;
import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.Misc.Utils;
import com.app.jonathan.willimissbart.Persistence.Models.UserStationData;
import com.app.jonathan.willimissbart.R;
import com.joanzapata.iconify.widget.IconTextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserRouteFooterViewHolder {
    @Bind(R.id.user_route_expansion) public LinearLayout footerBody;
    @Bind(R.id.user_route_expand) public IconTextView expandCollapseIcon;
    @Bind(R.id.user_route_origin) public TextView origin;
    @Bind(R.id.user_route_dest) public TextView destination;
    @Bind(R.id.user_route_minutes) public EditText minutes;

    private ValueAnimator expandAnimation;
    private ValueAnimator collapseAnimation;

    public UserRouteFooterViewHolder(View v, UserStationData[] userData) {
        ButterKnife.bind(this, v);
        origin.setText(userData[0].getAbbr());
        destination.setText(userData[1].getAbbr());
        initAnimations();
    }

    @OnClick(R.id.user_route_minutes)
    public void onMinutesClick() {
        minutes.setSelection(minutes.getText().length());
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

    public void initAnimations() {
        footerBody.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int measuredHeight = footerBody.getMeasuredHeight();

        expandAnimation = ValueAnimator.ofInt(0, measuredHeight);
        expandAnimation.setDuration(Constants.SHORT_DURATION);
        expandAnimation.addUpdateListener(new FooterUpdateListener(footerBody));
        expandAnimation.addListener(new FooterAnimListener(this, View.VISIBLE));

        collapseAnimation = ValueAnimator.ofInt(measuredHeight, 0);
        collapseAnimation.setDuration(Constants.SHORT_DURATION);
        collapseAnimation.addUpdateListener(new FooterUpdateListener(footerBody));
        collapseAnimation.addListener(new FooterAnimListener(this, View.GONE));
    }
}
