package com.app.jonathan.willimissbart.Listeners.Animations.Onboarding.InitialAnimation;

import android.view.View;
import android.view.animation.Animation;

import com.app.jonathan.willimissbart.ViewHolders.StationsFooterViewHolder;


public class ShowFooterAnimListener implements Animation.AnimationListener {
    private StationsFooterViewHolder footer;

    public ShowFooterAnimListener setFooter(StationsFooterViewHolder footer) {
        this.footer = footer;
        return this;
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        footer.contentView.setVisibility(View.VISIBLE);
        footer.done.setEnabled(true);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
