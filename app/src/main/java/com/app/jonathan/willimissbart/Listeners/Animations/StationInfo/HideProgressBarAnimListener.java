package com.app.jonathan.willimissbart.Listeners.Animations.StationInfo;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class HideProgressBarAnimListener implements AnimationListener {
    private ProgressBar progressBar;
    private AlphaAnimation showInfoAnim;
    private LinearLayout stationInfoLayout;

    public HideProgressBarAnimListener(ProgressBar progressBar,
                                       AlphaAnimation showInfoAnim,
                                       LinearLayout stationInfoLayout) {
        this.progressBar = progressBar;
        this.showInfoAnim = showInfoAnim;
        this.stationInfoLayout = stationInfoLayout;
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        progressBar.setVisibility(View.GONE);
        stationInfoLayout.startAnimation(showInfoAnim);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
