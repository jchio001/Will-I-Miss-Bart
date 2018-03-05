package com.app.jonathan.willimissbart.listener.animation.Onboarding;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import com.app.jonathan.willimissbart.misc.Constants;
import com.app.jonathan.willimissbart.viewholder.StationsFooterViewHolder;

public class ShowGridAnimListener implements Animation.AnimationListener {

    private LinearLayout gridLayout;
    private StationsFooterViewHolder footer;

    public ShowGridAnimListener setGridLayout(LinearLayout gridLayout) {
        this.gridLayout = gridLayout;
        return this;
    }

    public ShowGridAnimListener setFooter(StationsFooterViewHolder footer) {
        this.footer = footer;
        return this;
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        gridLayout.setVisibility(View.VISIBLE);
        AlphaAnimation showFooter = new AlphaAnimation(0.0f, 1.0f);
        showFooter.setDuration(Constants.STANDARD_DURATION);
        showFooter.setAnimationListener(new ShowFooterAnimListener().setFooter(footer));
        footer.contentView.startAnimation(showFooter);
    }
}
