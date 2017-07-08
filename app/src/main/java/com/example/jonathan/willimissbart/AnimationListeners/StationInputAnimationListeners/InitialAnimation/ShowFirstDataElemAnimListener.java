package com.example.jonathan.willimissbart.AnimationListeners.StationInputAnimationListeners.InitialAnimation;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.jonathan.willimissbart.Misc.Constants;


public class ShowFirstDataElemAnimListener implements Animation.AnimationListener {
    private LinearLayout linearLayout;
    private Button button;
    private Button button2;

    public ShowFirstDataElemAnimListener setLinearLayout(LinearLayout linearLayout) {
        this.linearLayout = linearLayout;
        return this;
    }

    public ShowFirstDataElemAnimListener setButton(Button button) {
        this.button = button;
        return this;
    }

    public ShowFirstDataElemAnimListener setButton2(Button button2) {
        this.button2 = button2;
        return this;
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        linearLayout.setVisibility(View.VISIBLE);
        AlphaAnimation showAddMoreTextAnim = new AlphaAnimation(0.0f, 1.0f);
        showAddMoreTextAnim.setDuration(Constants.STANDARD_DURATION);
        showAddMoreTextAnim.setAnimationListener(
                new ShowAddButtonAnimListener()
                        .setButton(button)
                        .setButton2(button2)
        );
        button.startAnimation(showAddMoreTextAnim);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
