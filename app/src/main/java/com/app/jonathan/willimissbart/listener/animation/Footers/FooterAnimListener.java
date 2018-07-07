package com.app.jonathan.willimissbart.listener.animation.Footers;

import android.animation.Animator;
import android.view.View;

import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.viewholder.UserRouteFooter;

public class FooterAnimListener implements Animator.AnimatorListener {

    private UserRouteFooter footer;
    private int visibility;

    public FooterAnimListener(UserRouteFooter footer, int visibilty) {
        this.footer = footer;
        this.visibility = visibilty;
    }

    @Override
    public void onAnimationStart(Animator animation) {
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (visibility == View.GONE) {
            footer.footerBody.setVisibility(visibility);
            footer.expandCollapseIcon.setText(R.string.ion_arrow_up_b);
        } else {
            footer.expandCollapseIcon.setText(R.string.ion_arrow_down_b);
        }
        footer.expandCollapseIcon.setEnabled(true);
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {
    }
}
