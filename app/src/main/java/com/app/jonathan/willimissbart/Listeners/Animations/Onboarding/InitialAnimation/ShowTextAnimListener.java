package com.app.jonathan.willimissbart.Listeners.Animations.Onboarding.InitialAnimation;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.ViewHolders.StationsFooterViewHolder;


public class ShowTextAnimListener implements Animation.AnimationListener {
    private TextView textView;
    private LinearLayout gridLayout;
    private StationsFooterViewHolder footer;

    public ShowTextAnimListener setTextView(TextView textView) {
        this.textView = textView;
        return this;
    }

    public ShowTextAnimListener setGridLayout(LinearLayout gridLayout) {
        this.gridLayout = gridLayout;
        return this;
    }

    public ShowTextAnimListener setFooter(StationsFooterViewHolder footer) {
        this.footer = footer;
        return this;
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        textView.setVisibility(View.VISIBLE);
        AlphaAnimation showGridAnim = new AlphaAnimation(0.0f, 1.0f);
        showGridAnim.setDuration(Constants.LONG_DURATION);
        showGridAnim.setAnimationListener(new ShowGridAnimListener()
            .setGridLayout(gridLayout).setFooter(footer));
        gridLayout.startAnimation(showGridAnim);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
