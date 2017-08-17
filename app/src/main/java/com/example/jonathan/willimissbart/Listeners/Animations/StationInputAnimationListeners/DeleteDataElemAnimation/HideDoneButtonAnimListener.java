package com.example.jonathan.willimissbart.Listeners.Animations.StationInputAnimationListeners.DeleteDataElemAnimation;


import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.jonathan.willimissbart.Listeners.Animations.StationInputAnimationListeners.Generic.ReshowButtonsAnimListener;
import com.example.jonathan.willimissbart.Misc.Constants;

public class HideDoneButtonAnimListener implements Animation.AnimationListener {
    private Button button;
    private Button button2;
    private LinearLayout parent;
    private LinearLayout viewToDelete;


    public HideDoneButtonAnimListener setButton(Button button) {
        this.button = button;
        return this;
    }

    public HideDoneButtonAnimListener setButton2(Button button2) {
        this.button2 = button2;
        return this;
    }

    public HideDoneButtonAnimListener setParent(LinearLayout parent) {
        this.parent = parent;
        return this;
    }

    public HideDoneButtonAnimListener setViewToDelete(LinearLayout viewToDelete) {
        this.viewToDelete = viewToDelete;
        return this;
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        button2.setVisibility(View.INVISIBLE);
        AlphaAnimation deleteView = new AlphaAnimation(1.0f, 0.0f);
        deleteView.setDuration(Constants.STANDARD_DURATION);
        deleteView.setAnimationListener(new DeleteViewAnimListener()
                .setButton(button)
                .setButton2(button2)
                .setParent(parent)
                .setViewToDelete(viewToDelete));
        viewToDelete.startAnimation(deleteView);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
