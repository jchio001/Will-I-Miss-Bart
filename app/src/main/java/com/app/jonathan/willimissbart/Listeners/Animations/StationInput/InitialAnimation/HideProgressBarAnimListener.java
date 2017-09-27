package com.app.jonathan.willimissbart.Listeners.Animations.StationInput.InitialAnimation;


import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.ViewHolders.StationsFooterViewHolder;

public class HideProgressBarAnimListener implements Animation.AnimationListener {
    private ProgressBar progressBar;
    private LinearLayout linearLayout;
    private LinearLayout gridLayout;
    private StationsFooterViewHolder footer;

    public HideProgressBarAnimListener setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
        return this;
    }

    public HideProgressBarAnimListener setLinearLayout(LinearLayout linearLayout) {
        this.linearLayout = linearLayout;
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
                .setLinearLayout(linearLayout)
                .setGridLayout(gridLayout)
                .setFooter(footer)
        );
        linearLayout.startAnimation(showTextAnim);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
