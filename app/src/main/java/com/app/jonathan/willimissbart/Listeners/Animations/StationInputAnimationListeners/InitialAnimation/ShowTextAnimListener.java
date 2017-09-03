package com.app.jonathan.willimissbart.Listeners.Animations.StationInputAnimationListeners.InitialAnimation;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;

import com.app.jonathan.willimissbart.Misc.Constants;


public class ShowTextAnimListener implements Animation.AnimationListener {
    private LinearLayout linearLayout1;
    private LinearLayout linearLayout2;
    private Button button;
    private Button button2;

    public ShowTextAnimListener setLinearLayout1(LinearLayout linearLayout1) {
        this.linearLayout1 = linearLayout1;
        return this;
    }

    public ShowTextAnimListener setLinearLayout2(LinearLayout linearLayout2) {
        this.linearLayout2 = linearLayout2;
        return this;
    }

    public ShowTextAnimListener setButton(Button button) {
        this.button = button;
        return this;
    }

    public ShowTextAnimListener setButton2(Button button) {
        this.button2 = button;
        return this;
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        linearLayout1.setVisibility(View.VISIBLE);

        AlphaAnimation showFirstDataElem = new AlphaAnimation(0.0f, 1.0f);
        showFirstDataElem.setDuration(Constants.LONG_DURATION);
        showFirstDataElem.setAnimationListener(
                new ShowFirstDataElemAnimListener()
                        .setLinearLayout(linearLayout2)
                        .setButton(button)
                        .setButton2(button2)
        );
        linearLayout2.startAnimation(showFirstDataElem);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
