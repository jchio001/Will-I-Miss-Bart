package com.app.jonathan.willimissbart.Listeners.Animations.Footers;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.view.ViewGroup;

public class FooterUpdateListener implements AnimatorUpdateListener {
    ViewGroup footerBody;

    public FooterUpdateListener(ViewGroup footerBody) {
        this.footerBody = footerBody;
    }

    // forcefully update the height as the animation ticks
    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        int value = (Integer) animation.getAnimatedValue();
        footerBody.getLayoutParams().height = value;
        footerBody.requestLayout();
    }
}
