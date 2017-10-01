package com.app.jonathan.willimissbart.Listeners.Animations.StationInfo;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.view.View;

import com.app.jonathan.willimissbart.API.RetrofitClient;
import com.app.jonathan.willimissbart.ViewHolders.StationInfoViewHolder;

public class StationInfoAnimListener implements AnimatorListener {
    private StationInfoViewHolder stationInfoViewHolder;
    private int visibility;
    private String abbr;

    public StationInfoAnimListener(StationInfoViewHolder stationInfoViewHolder,
                                   int visibility) {
        this.stationInfoViewHolder = stationInfoViewHolder;
        this.visibility = visibility;
    }

    public StationInfoAnimListener setAbbr(String abbr) {
        this.abbr = abbr;
        return this;
    }

    @Override
    public void onAnimationStart(Animator animation) {
        if (visibility == View.GONE) {
            stationInfoViewHolder.stationAddrLayout.setEnabled(false);
            stationInfoViewHolder.stationInfoLayout.setVisibility(View.INVISIBLE);
        } else {
            stationInfoViewHolder.stationInfoParent.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        stationInfoViewHolder.stationInfoParent.setVisibility(visibility);
        if (visibility == View.VISIBLE) {
            stationInfoViewHolder.progressBar.setVisibility(View.VISIBLE);
            RetrofitClient.getStationInfo(abbr);
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
