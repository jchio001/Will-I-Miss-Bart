package com.app.jonathan.willimissbart.Misc;

public class Constants {
    // SHARED PREFERENCE KEYS
    public static final String STATION_LIST_KEY = "STATION_LIST";
    public static final String USER_DATA = "USER_DATA";
    public static final String MUTE_NOTIF = "MUTE_NOTIF";

    // ANIMATION DURATION
    public static final int LONG_DURATION = 600;
    public static final int STANDARD_DURATION = 400;
    public static final int SHORT_DURATION = 200;

    // INTENT KEY
    public static final String TITLE = "title";
    public static final String SECONDS = "seconds";
    public static final String STATION_INDEX = "station_index";
    public static final String NOT_THIS_INDEX = "not_this_index";
    public static final String ANNOUNCEMENTS = "announcements";

    // INTENT TAGS
    public static final String DISMISS = "WillIMissBart.dismiss";
    public static final String UPDATE = "WillIMissBart.update";

    // NOTIFICATION IDS
    public static final int TIMER_NOTIF_ID = 1;

    // VIBRATION PATTERN
    public static final long[] VIBRATION_PATTERN = {0, 1000, 1000};

    // INTENT REQUEST CODES
    public static final int UPDATING_STATIONS = 0;
    public static final int UPDATED_ORIGIN = 1;
    public static final int UPDATED_DEST = 2;

    // REFRESH STATE ENUMS
    public static final int REFRESH_STATE_INACTIVE = 0;
    public static final int REFRESH_STATE_REFRESHING = 1;
    public static final int REFRESH_STATE_RELOADING = 2; // TODO: may not need this
}
