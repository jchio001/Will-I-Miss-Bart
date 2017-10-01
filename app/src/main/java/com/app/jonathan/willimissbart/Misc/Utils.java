package com.app.jonathan.willimissbart.Misc;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioManager;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.app.jonathan.willimissbart.API.APIConstants;
import com.app.jonathan.willimissbart.API.Callbacks.EtdCallback;
import com.app.jonathan.willimissbart.API.Models.DeparturesFeedModels.FlattenedEstimate;
import com.app.jonathan.willimissbart.API.Models.EtdModels.Estimate;
import com.app.jonathan.willimissbart.API.Models.EtdModels.Etd;
import com.app.jonathan.willimissbart.API.Models.EtdModels.EtdStation;
import com.app.jonathan.willimissbart.API.Models.StationModels.Station;
import com.app.jonathan.willimissbart.API.RetrofitClient;
import com.app.jonathan.willimissbart.Notification.TimerNotificationBuilder;
import com.app.jonathan.willimissbart.Persistence.Models.UserStationData;
import com.app.jonathan.willimissbart.Persistence.StationsSingleton;
import com.app.jonathan.willimissbart.R;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;

// Random utility functions
public class Utils {
    public static Character directionToUrlParam(String direction) {
        return direction.equals("Both") ? null : direction.charAt(0);
    }

    public static void loadStations(String stationsJSON) {
        if (StationsSingleton.getStations().isEmpty()) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Station>>() {}.getType();
            List<Station> stations = gson.fromJson(stationsJSON, listType);
            StationsSingleton.getInstance().setStations(stations);
        }
    }

    //usually filtered
    public static void fetchEtds(List<UserStationData> userStationData) {
        for (int i = 0; i < userStationData.size(); ++i) {
            UserStationData data = userStationData.get(i);
            RetrofitClient.getInstance()
                .getMatchingService()
                .getEtd("etd", APIConstants.API_KEY, 'y', data.getAbbr(), null)
                .clone()
                .enqueue(
                    new EtdCallback()
                        .setData(userStationData.get(i))
                        .setIndex(i));
        }
    }

    public static String generateTimerText(int seconds) {
        long minutesLeft = seconds / 60;
        long secondsLeft = seconds % 60;
        String time =  String.format(
                Locale.ENGLISH,
                "%s:%s",
                (minutesLeft < 10 ? "0" : "") + String.valueOf(minutesLeft),
                (secondsLeft < 10 ? "0" : "") + String.valueOf(secondsLeft));
        return time;
    }

    // Converts seconds into a string of format: %d min(s) %d seconds
    public static String secondsToFormattedString(int seconds) {
        return String.format(Locale.ENGLISH,
            "%d min" + (seconds / 60 > 1 ? "s" : "") + " %d seconds",
            seconds / 60,
            seconds % 60);
    }

    public static void createOrUpdateNotification(String title, int time) {
        Context context = MyApplication.getContext();
        if (time < 0) {
            ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE))
                .setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE))
                .vibrate(Constants.VIBRATION_PATTERN, 0);
        }

        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(Constants.TIMER_NOTIF_ID,
                        new TimerNotificationBuilder(title, time).build(time < 0));
    }

    // Converts a minute into a timer estimate given:
    // - A estimate in minutes
    // - The time at which an estimate is fetched
    // Dampening factor is smaller for smaller intervals as the train will be more likely
    // to leave earlier
    public static int getTimerDuration(String min, long timeInSeconds) {
        int minAsInt = Integer.valueOf(min);
        return minAsInt * 60 * ((minAsInt > 5) ? 95 : 90) / 100
            - ((int) ((System.currentTimeMillis() / 1000) - timeInSeconds));
    }

    public static List<FlattenedEstimate> flattenEstimates(EtdStation[] etdStations,
                                                           List<UserStationData> associatedData,
                                                           long[] timeOfResponse,
                                                           boolean[] successArr,
                                                           int size) {
        List<FlattenedEstimate> flattenedEstimates = Lists.newArrayList();
        for (int i = 0; i < size; ++i) {
            if (successArr[i]) {
                if (etdStations[i].getEtds() != null && !etdStations[i].getEtds().isEmpty()) {
                    for (Etd etd : etdStations[i].getEtds()) {
                        flattenedEstimates.add(
                            new FlattenedEstimate(associatedData.get(i), etd, timeOfResponse[i], null));
                        for (Estimate estimate : etd.getEstimates()) {
                            flattenedEstimates.add(
                                new FlattenedEstimate(
                                    associatedData.get(i), etd, timeOfResponse[i], estimate));
                        }
                    }
                } else {
                    flattenedEstimates.add(new FlattenedEstimate(
                        associatedData.get(i), null, timeOfResponse[i], null));
                }
            } else {
                flattenedEstimates.add(new FlattenedEstimate(associatedData.get(i)));
            }
        }
        return flattenedEstimates;
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static Snackbar showSnackbar(Context context, View parent, int colorId, int stringId) {
        Snackbar snackbar = Snackbar.make(parent, stringId, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(context, colorId));
        View rootView = snackbar.getView();
        TextView tv = (TextView) rootView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(ContextCompat.getColor(context, R.color.white));
        snackbar.show();
        return snackbar;
    }

    private static int height = -1;
    public static int getStationInfoLayoutHeight(Activity activity) {
        if (height == -1) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            int resourceId = activity.getResources().getIdentifier("status_bar_height",
                "dimen", "android");
            int statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);

            TypedValue tv = new TypedValue();
            activity.getTheme().resolveAttribute(R.attr.actionBarSize, tv, true);
            return displayMetrics.heightPixels - (TypedValue.complexToDimensionPixelSize(
                tv.data, activity.getResources().getDisplayMetrics())) -
                statusBarHeight;
        }

        return height;
    }
}
