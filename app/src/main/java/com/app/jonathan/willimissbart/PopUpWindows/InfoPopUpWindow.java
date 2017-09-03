package com.app.jonathan.willimissbart.PopUpWindows;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.app.jonathan.willimissbart.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class InfoPopUpWindow extends PopupWindow {
    public InfoPopUpWindow(Context context) {
        super(LayoutInflater.from(context).inflate(R.layout.pop_up_window_info, null),
                800, WindowManager.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(
                ContextCompat.getDrawable(context, R.drawable.background_pop_up_window));
        ButterKnife.bind(this, getContentView());
        setFocusable(true);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.cancel)
    public void onDone() {
        dismiss();
    }
}
