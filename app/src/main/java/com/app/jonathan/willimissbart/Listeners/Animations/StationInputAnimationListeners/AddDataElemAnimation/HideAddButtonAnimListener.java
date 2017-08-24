package com.app.jonathan.willimissbart.Listeners.Animations.StationInputAnimationListeners.AddDataElemAnimation;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;

import com.app.jonathan.willimissbart.Misc.Constants;


public class HideAddButtonAnimListener implements Animation.AnimationListener {
    private Button button;
    private Button button2;
    private LinearLayout linearLayout;

    public HideAddButtonAnimListener setButton(Button button) {
        this.button = button;
        return this;
    }

    public HideAddButtonAnimListener setButton2(Button button2) {
        this.button2 = button2;
        return this;
    }

    public HideAddButtonAnimListener setLinearLayout(LinearLayout linearLayout) {
        this.linearLayout = linearLayout;
        return this;
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        button.setVisibility(View.INVISIBLE);
        AlphaAnimation hideDoneButton = new AlphaAnimation(1.0f, 0.0f);
        hideDoneButton.setDuration(Constants.STANDARD_DURATION);
        hideDoneButton.setAnimationListener(
                new HideDoneButtonAnimListener()
                        .setButton(button)
                        .setButton2(button2)
                        .setLinearLayout(linearLayout)
        );
        button2.startAnimation(hideDoneButton);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
