package com.app.jonathan.willimissbart.Listeners.Animations.Generic;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.view.ViewGroup;

public class UpdateListener implements AnimatorUpdateListener {
    ViewGroup footerBody;

    public UpdateListener(ViewGroup footerBody) {
        this.footerBody = footerBody;
    }

    // forcefully update the height as the animation ticks
    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        footerBody.getLayoutParams().height = (Integer) animation.getAnimatedValue();
        footerBody.requestLayout();
    }
}
