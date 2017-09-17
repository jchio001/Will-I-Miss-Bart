package com.app.jonathan.willimissbart.Listeners.Animations.StationInputAnimationListeners.InitialAnimation;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.ViewHolders.StationsFooterViewHolder;


public class ShowTextAnimListener implements Animation.AnimationListener {
    private LinearLayout linearLayout;
    private GridView grid;
    private StationsFooterViewHolder footer;

    public ShowTextAnimListener setLinearLayout(LinearLayout linearLayout) {
        this.linearLayout = linearLayout;
        return this;
    }

    public ShowTextAnimListener setGrid(GridView grid) {
        this.grid = grid;
        return this;
    }

    public ShowTextAnimListener setFooter(StationsFooterViewHolder footer) {
        this.footer = footer;
        return this;
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        linearLayout.setVisibility(View.VISIBLE);
        AlphaAnimation showGridAnim = new AlphaAnimation(0.0f, 1.0f);
        showGridAnim.setDuration(Constants.LONG_DURATION);
        showGridAnim.setAnimationListener(new ShowGridAnimListener()
            .setGrid(grid).setFooter(footer));
        grid.startAnimation(showGridAnim);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
