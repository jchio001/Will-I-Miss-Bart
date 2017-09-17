package com.app.jonathan.willimissbart.Listeners.Animations.StationInputAnimationListeners.InitialAnimation;


import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.GridView;

import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.ViewHolders.StationsFooterViewHolder;

public class ShowGridAnimListener implements Animation.AnimationListener {
    private GridView grid;
    private StationsFooterViewHolder footer;

    public ShowGridAnimListener setGrid(GridView grid) {
        this.grid = grid;
        return this;
    }

    public ShowGridAnimListener setFooter(StationsFooterViewHolder footer) {
        this.footer = footer;
        return this;
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        grid.setVisibility(View.VISIBLE);
        AlphaAnimation showFooter = new AlphaAnimation(0.0f, 1.0f);
        showFooter.setDuration(Constants.STANDARD_DURATION);
        showFooter.setAnimationListener(new ShowFooterAnimListener().setFooter(footer));
        footer.contentView.startAnimation(showFooter);
    }
}
