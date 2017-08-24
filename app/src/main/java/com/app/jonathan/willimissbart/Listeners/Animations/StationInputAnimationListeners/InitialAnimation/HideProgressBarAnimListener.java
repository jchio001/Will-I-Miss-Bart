package com.app.jonathan.willimissbart.Listeners.Animations.StationInputAnimationListeners.InitialAnimation;


import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.app.jonathan.willimissbart.Misc.Constants;

public class HideProgressBarAnimListener implements Animation.AnimationListener {
    private ProgressBar progressBar;
    private LinearLayout linearLayout1;
    private LinearLayout linearLayout2;
    private Button button1;
    private Button button2;

    public HideProgressBarAnimListener setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
        return this;
    }

    public HideProgressBarAnimListener setLinearLayout1(LinearLayout linearLayout1) {
        this.linearLayout1 = linearLayout1;
        return this;
    }

    public HideProgressBarAnimListener setLinearLayout2(LinearLayout linearLayout2) {
        this.linearLayout2 = linearLayout2;
        return this;
    }

    public HideProgressBarAnimListener setButton1(Button button1) {
        this.button1 = button1;
        return this;
    }

    public HideProgressBarAnimListener setButton2(Button button2) {
        this.button2 = button2;
        return this;
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        progressBar.setVisibility(View.GONE);
        AlphaAnimation showTextAnim = new AlphaAnimation(0.0f, 1.0f);
        showTextAnim.setDuration(Constants.LONG_DURATION);
        showTextAnim.setAnimationListener(
                new ShowTextAnimListener()
                        .setLinearLayout1(linearLayout1)
                        .setLinearLayout2(linearLayout2)
                        .setButton(button1)
                        .setButton2(button2)
        );
        linearLayout1.startAnimation(showTextAnim);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
