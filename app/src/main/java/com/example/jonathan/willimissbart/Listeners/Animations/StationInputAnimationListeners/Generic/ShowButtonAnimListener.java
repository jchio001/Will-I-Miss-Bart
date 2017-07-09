package com.example.jonathan.willimissbart.Listeners.Animations.StationInputAnimationListeners.Generic;

import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;


public class ShowButtonAnimListener implements Animation.AnimationListener {
    Button button;

    public ShowButtonAnimListener setButton(Button button) {
        this.button = button;
        return this;
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        button.setEnabled(true);
        button.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
