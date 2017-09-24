package com.app.jonathan.willimissbart.Listeners.Animations.StationInfo;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.app.jonathan.willimissbart.Listeners.Animations.Generic.ShowOrHideAnimListener;
import com.app.jonathan.willimissbart.Misc.Constants;

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
        showInfoAnim = new AlphaAnimation(0.0f, 1.0f);
        showInfoAnim.setDuration(Constants.STANDARD_DURATION);
        showInfoAnim.setAnimationListener(new ShowOrHideAnimListener(
            stationInfoLayout, View.VISIBLE));
        stationInfoLayout.startAnimation(showInfoAnim);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
