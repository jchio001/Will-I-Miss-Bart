package com.example.jonathan.willimissbart.Misc;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import com.example.jonathan.willimissbart.Persistence.SPSingleton;
import com.example.jonathan.willimissbart.R;

public class Utils {
    public static boolean noDaysSelected(boolean[] days) {
        for (boolean b : days) {
            if (b) {
                return false;
            }
        }
        return true;
    }

    public static Character directionToUrlParam(String direction) {
        return direction.equals("Both") ? null : direction.charAt(0);
    }

    public static String getUserBartData(Bundle b, Context context) {
        return (b == null) ? SPSingleton.getInstance(context).getUserData() :
                b.getString(Constants.USER_DATA, "");
    }


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
