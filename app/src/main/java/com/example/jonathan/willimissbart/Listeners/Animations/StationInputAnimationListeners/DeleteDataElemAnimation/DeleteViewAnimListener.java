package com.example.jonathan.willimissbart.Listeners.Animations.StationInputAnimationListeners.DeleteDataElemAnimation;


import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;

public class DeleteViewAnimListener implements Animation.AnimationListener {
    private Button button;
    private Button button2;
    private LinearLayout parent;
    private LinearLayout viewToDelete;

    public DeleteViewAnimListener setButton(Button button) {
        this.button = button;
        return this;
    }

    public DeleteViewAnimListener setButton2(Button button2) {
        this.button2 = button2;
        return this;
    }

    public DeleteViewAnimListener setParent(LinearLayout parent) {
        this.parent = parent;
        return this;
    }

    public DeleteViewAnimListener setViewToDelete(LinearLayout viewToDelete) {
        this.viewToDelete = viewToDelete;
        return this;
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        parent.removeView(viewToDelete);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
