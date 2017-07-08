package com.example.jonathan.willimissbart.Misc;


import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import com.example.jonathan.willimissbart.R;

public class Utils {
    public static Snackbar showSnackBar(Context context, View parent, int colorId, String message) {
        Snackbar snackbar = Snackbar.make(parent, message, Snackbar.LENGTH_LONG);
        View rootView = snackbar.getView();
        snackbar.getView().setBackgroundColor(context.getResources().getColor(colorId));
        TextView tv = (TextView) rootView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(context.getResources().getColor(R.color.white));
        snackbar.show();
        return snackbar;
    }
}
