package com.app.jonathan.willimissbart.Listeners.Animations.Onboarding.InitialAnimation;


import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.ViewHolders.StationsFooterViewHolder;

public class HideProgressBarAnimListener implements Animation.AnimationListener {
    private ProgressBar progressBar;
    private TextView textView;
    private LinearLayout gridLayout;
    private StationsFooterViewHolder footer;

    public HideProgressBarAnimListener setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
        return this;
    }

    public HideProgressBarAnimListener setTextView(TextView textView) {
        this.textView = textView;
        return this;
    }

    public HideProgressBarAnimListener setGridLayout(LinearLayout gridLayout) {
        this.gridLayout = gridLayout;
        return this;
    }

    public HideProgressBarAnimListener setFooter(StationsFooterViewHolder footer) {
        this.footer = footer;
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
                .setTextView(textView)
                .setGridLayout(gridLayout)
                .setFooter(footer)
        );
        textView.startAnimation(showTextAnim);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
