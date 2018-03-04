package com.app.jonathan.willimissbart.misc;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.app.jonathan.willimissbart.api.Models.Station.Station;
import com.app.jonathan.willimissbart.notification.TimerNotificationBuilder;
import com.app.jonathan.willimissbart.persistence.StationsManager;
import com.app.jonathan.willimissbart.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;

// Random utility functions
public class Utils {

    public static List<Station> loadStations(String stationsJSON) {
        if (StationsManager.getStations().isEmpty()) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Station>>() {}.getType();
            List<Station> stations = gson.fromJson(stationsJSON, listType);
            StationsManager.setStations(stations);
            return stations;
        } else {
            return NotGuava.newArrayList();
        }
    }

    public static String generateTimerText(int seconds) {
        long minutesLeft = seconds / 60;
        long secondsLeft = seconds % 60;
        return String.format(
                Locale.ENGLISH,
                "%s:%s",
                (minutesLeft < 10 ? "0" : "") + String.valueOf(minutesLeft),
                (secondsLeft < 10 ? "0" : "") + String.valueOf(secondsLeft));
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

    /**
     * Converts a minute into an arrival estimate. Dampening factor is smaller for smaller intervals
     * as the train will be more likely to leave earlier
     * @param min A string representing an estimate in minutes
     * @param timeOfResp The time at which an estimate is fetched (in epoch seconds)
     * @return A dampened timer estimate in seconds.
     */
    public static int getEstimateInSeconds(String min, long timeOfResp) {
        int minAsInt = Integer.valueOf(min);
        long now = System.currentTimeMillis() / 1000;

        int diff = 0;
        if (now > timeOfResp) {
            diff = (int) (now - timeOfResp);
        }

        return minAsInt * 60 * ((minAsInt > 5) ? 98 : 95) / 100 - diff;
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

            return displayMetrics.heightPixels - statusBarHeight;
        }

        return height;
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    private static double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
            + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;

        return Math.abs(dist);
    }

    // Fetches the closest station for a given location;\
    public static int getClosestStation(Location location) {
        int minIndex = -1;
        double curMin = 0;

        List<Station> stations = StationsManager.getStations();
        for (int i = 0; i < stations.size(); ++i) {
            Station station = stations.get(i);
            if (minIndex == -1) {
                minIndex = 0;
                curMin = getDistance(location.getLatitude(), location.getLongitude(),
                    station.getLatitude(), station.getLongitude());
            } else {
                double distance = getDistance(location.getLatitude(), location.getLongitude(),
                    station.getLatitude(), station.getLongitude());
                if (distance < curMin) {
                    minIndex = i;
                    curMin = distance;
                }
            }
        }

        return minIndex;
    }

    public static boolean isLocationEnabled(Context context) {
        return ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE))
            .isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
