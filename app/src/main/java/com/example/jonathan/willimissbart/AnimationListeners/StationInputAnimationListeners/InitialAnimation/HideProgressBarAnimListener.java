package com.example.jonathan.willimissbart.AnimationListeners.StationInputAnimationListeners.InitialAnimation;


import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.jonathan.willimissbart.Misc.Constants;

public class HideProgressBarAnimListener implements Animation.AnimationListener {
    private ProgressBar progressBar;
    private TextView textView;
    private LinearLayout linearLayout;
    private Button button;
    private Button button2;

    public HideProgressBarAnimListener setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
        return this;
    }

    public HideProgressBarAnimListener setTextView(TextView textView) {
        this.textView = textView;
        return this;
    }

    public HideProgressBarAnimListener setLinearLayout(LinearLayout linearLayout) {
        this.linearLayout = linearLayout;
        return this;
    }

    public HideProgressBarAnimListener setButton(Button button) {
        this.button = button;
        return this;
    }

    public HideProgressBarAnimListener setButton2(Button button2) {
        this.button2 = button2;
        return this;
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        progressBar.setVisibility(View.GONE);
        AlphaAnimation showTextAnim = new AlphaAnimation(0.0f, 1.0f);
        showTextAnim.setDuration(Constants.LONG_DURATION);
        showTextAnim.setAnimationListener(
                new ShowTextAnimListener()
                        .setTextView(textView)
                        .setLinearLayout(linearLayout)
                        .setButton(button)
                        .setButton2(button2)
        );
        textView.startAnimation(showTextAnim);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
