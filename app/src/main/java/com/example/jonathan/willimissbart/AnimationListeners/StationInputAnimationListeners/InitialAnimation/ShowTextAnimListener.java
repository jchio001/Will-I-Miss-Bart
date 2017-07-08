package com.example.jonathan.willimissbart.AnimationListeners.StationInputAnimationListeners.InitialAnimation;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jonathan.willimissbart.Misc.Constants;


public class ShowTextAnimListener implements Animation.AnimationListener {
    private TextView textView;
    private LinearLayout linearLayout;
    private Button button;
    private Button button2;

    public ShowTextAnimListener setTextView(TextView textView) {
        this.textView = textView;
        return this;
    }

    public ShowTextAnimListener setLinearLayout(LinearLayout linearLayout) {
        this.linearLayout = linearLayout;
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
        textView.setVisibility(View.VISIBLE);

        AlphaAnimation showFirstDataElem = new AlphaAnimation(0.0f, 1.0f);
        showFirstDataElem.setDuration(Constants.LONG_DURATION);
        showFirstDataElem.setAnimationListener(
                new ShowFirstDataElemAnimListener()
                        .setLinearLayout(linearLayout)
                        .setButton(button)
                        .setButton2(button2)
        );
        linearLayout.startAnimation(showFirstDataElem);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
