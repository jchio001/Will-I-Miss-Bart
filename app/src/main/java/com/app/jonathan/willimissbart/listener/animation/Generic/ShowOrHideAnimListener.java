package com.app.jonathan.willimissbart.listener.animation.Generic;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

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
        v.setVisibility(visibility);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }
}
