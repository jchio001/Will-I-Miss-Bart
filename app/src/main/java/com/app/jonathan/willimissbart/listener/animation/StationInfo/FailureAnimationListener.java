package com.app.jonathan.willimissbart.listener.animation.StationInfo;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.app.jonathan.willimissbart.R;


public class FailureAnimationListener implements Animation.AnimationListener {

    private ProgressBar progressBar;

    public FailureAnimationListener(ProgressBar progessBar) {
        this.progressBar = progessBar;
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        progressBar.setVisibility(View.GONE);

        Context context = progressBar.getContext();
        Toast.makeText(context, R.string.failed_station_info_req, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }
}
