package com.example.jonathan.willimissbart.AnimationListeners.StationInputAnimationListeners.InitialAnimation;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;

import com.example.jonathan.willimissbart.AnimationListeners.StationInputAnimationListeners.Generic.ShowButtonAnimListener;
import com.example.jonathan.willimissbart.Misc.Constants;


public class ShowAddButtonAnimListener implements Animation.AnimationListener {
    private Button button;
    private Button button2;

    public ShowAddButtonAnimListener setButton(Button button) {
        this.button = button;
        return this;
    }

    public ShowAddButtonAnimListener setButton2(Button button2) {
        this.button2 = button2;
        return this;
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        button.setVisibility(View.VISIBLE);
        AlphaAnimation showDoneButtonAnim = new AlphaAnimation(0.0f, 1.0f);
        showDoneButtonAnim.setDuration(Constants.STANDARD_DURATION);
        showDoneButtonAnim.setAnimationListener(
                new ShowButtonAnimListener()
                        .setButton(button2)
        );
        button2.startAnimation(showDoneButtonAnim);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
