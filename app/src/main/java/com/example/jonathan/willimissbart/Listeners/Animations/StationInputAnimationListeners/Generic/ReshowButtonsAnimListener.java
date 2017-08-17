package com.example.jonathan.willimissbart.Listeners.Animations.StationInputAnimationListeners.Generic;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;

import com.example.jonathan.willimissbart.Misc.Constants;


public class ReshowButtonsAnimListener implements Animation.AnimationListener {
    private Button button;
    private Button button2;

    public ReshowButtonsAnimListener setButton(Button button) {
        this.button = button;
        return this;
    }

    public ReshowButtonsAnimListener setButton2(Button button2) {
        this.button2 = button2;
        return this;
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        button.setEnabled(true);
        button.setVisibility(View.VISIBLE);
        AlphaAnimation showDoneButton = new AlphaAnimation(0.0f, 1.0f);
        showDoneButton.setDuration(Constants.STANDARD_DURATION);
        showDoneButton.setAnimationListener(
                new ShowButtonAnimListener()
                        .setButton(button2)
        );
        button2.startAnimation(showDoneButton);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }
}
