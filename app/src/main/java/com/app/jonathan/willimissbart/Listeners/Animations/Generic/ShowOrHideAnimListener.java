package com.app.jonathan.willimissbart.Listeners.Animations.Generic;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import com.app.jonathan.willimissbart.API.Models.StationInfoModels.Station;
import com.app.jonathan.willimissbart.ViewHolders.StationInfoViewHolder;

public class ShowOrHideAnimListener implements AnimationListener {
    private View v;
    private int visibility;

    public ShowOrHideAnimListener(View v, int visibility) {
        this.v = v;
        this.visibility = visibility;
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        v.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
