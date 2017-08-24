package com.app.jonathan.willimissbart.Listeners.Animations.StationInputAnimationListeners.AddDataElemAnimation;


import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;

import com.app.jonathan.willimissbart.Misc.Constants;

public class HideDoneButtonAnimListener implements Animation.AnimationListener {
    private Button button;
    private Button button2;
    private LinearLayout linearLayout;

    public HideDoneButtonAnimListener setButton(Button button) {
        this.button = button;
        return this;
    }

    public HideDoneButtonAnimListener setButton2(Button button2) {
        this.button2 = button2;
        return this;
    }

    public HideDoneButtonAnimListener setLinearLayout(LinearLayout linearLayout) {
        this.linearLayout = linearLayout;
        return this;
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        button2.setVisibility(View.INVISIBLE);
        linearLayout.setVisibility(View.INVISIBLE);
        AlphaAnimation showNewDataElem = new AlphaAnimation(0.0f, 1.0f);
        showNewDataElem.setDuration(Constants.STANDARD_DURATION);
        showNewDataElem.setAnimationListener(
                new ShowNewDataElemAnimListener()
                        .setButton(button)
                        .setButton2(button2)
                        .setLinearLayout(linearLayout)
        );
        linearLayout.startAnimation(showNewDataElem);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
