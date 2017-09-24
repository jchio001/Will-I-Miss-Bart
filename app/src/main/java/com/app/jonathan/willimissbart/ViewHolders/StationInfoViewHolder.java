package com.app.jonathan.willimissbart.ViewHolders;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.app.jonathan.willimissbart.API.Models.StationInfoModels.Station;
import com.app.jonathan.willimissbart.Listeners.Animations.Generic.ShowOrHideAnimListener;
import com.app.jonathan.willimissbart.Listeners.Animations.Generic.UpdateListener;
import com.app.jonathan.willimissbart.Listeners.Animations.StationInfo.HideProgressBarAnimListener;
import com.app.jonathan.willimissbart.Listeners.Animations.StationInfo.StationInfoAnimListener;
import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StationInfoViewHolder {
    public ScrollView stationInfoParent;
    @Bind(R.id.stn_addr_layout) public RelativeLayout stationAddrLayout;
    @Bind(R.id.stn_info) public LinearLayout stationInfoLayout;
    @Bind(R.id.activity_table) LinearLayout activityTable;
    @Bind(R.id.stn_info_progress_bar) public ProgressBar progressBar;
    @Bind(R.id.stn_info_title) TextView stationInfoTitle;
    @Bind(R.id.stn_info_blurb) TextView stationInfoBlurb;
    @Bind(R.id.stn_info_addr) TextView stationInfoAddress;

    private ValueAnimator expandAnimation;
    private ValueAnimator collapseAnimation;
    private AlphaAnimation showInfoAnim;
    private AlphaAnimation hideProgressBar;
    private StationInfoAnimListener showLayoutListener;

    // I don't need to reset the values because by the time the user clicks on this, new lat long
    // values will already be stored.
    private double lat;
    private double lng;

    private int height;

    public StationInfoViewHolder(ScrollView stationInfoParent, int height) {
        this.stationInfoParent = stationInfoParent;
        this.height = height;
        ButterKnife.bind(this, stationInfoParent);
        EventBus.getDefault().register(this);
        initAnimations();
    }

    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.stn_info_close)
    public void onClose() {
        hideProgressBar.setAnimationListener(null);
        hideProgressBar.cancel();

        showInfoAnim.setAnimationListener(null);
        showInfoAnim.cancel();

        progressBar.setVisibility(View.INVISIBLE);

        collapseAnimation.start();
    }

    @OnClick(R.id.stn_addr_layout)
    public void openMaps() {
        Uri googleMapsUri = Uri.parse(String.format(Locale.ENGLISH,
            "google.navigation:q=%f,%f", lat, lng));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, googleMapsUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        stationInfoParent.getContext().startActivity(mapIntent);
    }

    @Subscribe
    public void onStationInfoResp(Station stationInfo) {
        lat = stationInfo.getLatitude();
        lng = stationInfo.getLongitude();
        stationInfoAddress.setText(stationInfo.getFullAddress());
        stationInfoBlurb.setText(stationInfo.getIntro().getcDataSection().trim());

        // Yes, I did check BART's API and no, I can't do this with a loop.
        new StationInfoRowViewHolder(activityTable.getChildAt(0),
            stationInfo.getFood().getcDataSection(), 0);
        new StationInfoRowViewHolder(activityTable.getChildAt(1),
            stationInfo.getShopping().getcDataSection(), 1);
        new StationInfoRowViewHolder(activityTable.getChildAt(2),
            stationInfo.getAttraction().getcDataSection(), 2);

        // TODO: enable this once the animation has ended. QOL update, Low priority.
        stationAddrLayout.setEnabled(true);

        // TODO: can I reuse the same animation?
        showInfoAnim = new AlphaAnimation(0.0f, 1.0f);
        showInfoAnim.setDuration(Constants.STANDARD_DURATION);
        showInfoAnim.setAnimationListener(new ShowOrHideAnimListener(
            stationInfoLayout, View.VISIBLE));

        hideProgressBar = new AlphaAnimation(1.0f, 0.0f);
        hideProgressBar.setDuration(Constants.STANDARD_DURATION);
        hideProgressBar.setAnimationListener(new HideProgressBarAnimListener(progressBar,
            showInfoAnim, stationInfoLayout));
        progressBar.startAnimation(hideProgressBar);
    }

    public void show(String abbr) {
        stationInfoParent.setVisibility(View.VISIBLE);
        stationInfoTitle.setText(stationInfoParent.getContext()
            .getString(R.string.stn_info_title, abbr));
        showLayoutListener.setAbbr(abbr);
        expandAnimation.start();
    }

    public void initAnimations() {
        expandAnimation = ValueAnimator.ofInt(0, height);
        expandAnimation.setDuration(Constants.SHORT_DURATION);
        expandAnimation.addUpdateListener(new UpdateListener(stationInfoParent));
        showLayoutListener = new StationInfoAnimListener(this, View.VISIBLE);
        expandAnimation.addListener(showLayoutListener);

        collapseAnimation = ValueAnimator.ofInt(height, 0);
        collapseAnimation.setDuration(Constants.SHORT_DURATION);
        collapseAnimation.addUpdateListener(new UpdateListener(stationInfoParent));
        collapseAnimation.addListener(new StationInfoAnimListener(this, View.GONE));
    }
}
