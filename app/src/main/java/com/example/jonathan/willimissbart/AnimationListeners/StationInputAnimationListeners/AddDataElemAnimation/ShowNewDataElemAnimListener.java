package com.example.jonathan.willimissbart.AnimationListeners.StationInputAnimationListeners.AddDataElemAnimation;


import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.jonathan.willimissbart.AnimationListeners.StationInputAnimationListeners.Generic.ShowButtonAnimListener;
import com.example.jonathan.willimissbart.Misc.Constants;

public class ShowNewDataElemAnimListener implements Animation.AnimationListener {
    private Button button;
    private Button button2;
    private LinearLayout linearLayout;

    public ShowNewDataElemAnimListener setButton(Button button) {
        this.button = button;
        return this;
    }

    public ShowNewDataElemAnimListener setButton2(Button button2) {
        this.button2 = button2;
        return this;
    }

    public ShowNewDataElemAnimListener setLinearLayout(LinearLayout linearLayout) {
        this.linearLayout = linearLayout;
        return this;
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        linearLayout.setVisibility(View.VISIBLE);
        AlphaAnimation reshowAddButtonAnim = new AlphaAnimation(0.0f, 1.0f);
        reshowAddButtonAnim.setDuration(Constants.STANDARD_DURATION);
        reshowAddButtonAnim.setAnimationListener(
                new ReshowAddButtonAnimListener()
                        .setButton(button)
                        .setButton2(button2)
        );
        button.startAnimation(reshowAddButtonAnim);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        linearLayout.setVisibility(View.VISIBLE);
    }
}
