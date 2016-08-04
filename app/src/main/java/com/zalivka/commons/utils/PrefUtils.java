package com.zalivka.commons.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrefUtils {

    // global list of stuff
    public static final String FIRST_INSTALLED = "stickman.first_installed";
    public static final String UPDATES_HISTORY = "stickman.updates_history";

    // global list of hints
    public static final String VECTOR_CROP = "fingerpaint.vectorcrop";
    public static final String FINGERPAINT_TOOLS = "fingerpaint.tools";

    public static final String DEBUG_PURCHASE_SCREEN = "stickman.debug.purchase_screen";

    public static final String SCREEN_ORIENTATION = "orientation";

    public static final int STOP = -1;

    private static SharedPreferences getPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(StaticContextHolder.mCtx);
    }

    public static int getCount(String key) {
        return getPrefs().getInt(key, 0);
    }

    public static int increment(String key) {
        int newNumber = getCount(key) + 1;
        getPrefs().edit().putInt(key, newNumber).commit();
        return newNumber;
    }

    public static void cancel(String key) {
        getPrefs().edit().putInt(key, STOP).commit();
    }

    public static boolean isCancelled(String key) {
        return STOP == getPrefs().getInt(key, 0);
    }

    public static boolean recordFirstInstalledVersion() {
        if (getPrefs().contains(FIRST_INSTALLED))
            return false;

        getPrefs().edit().putInt(FIRST_INSTALLED, EnvUtils.getVersionCode()).commit();
        return true;
    }

    public static void writeString(String key, String value) {
        getPrefs().edit().putString(key, value).commit();
    }

    public static void writeBoolean(String key, boolean value) {
        getPrefs().edit().putBoolean(key, value).commit();
    }

    public static void writeInt(String key, int value) {
        getPrefs().edit().putInt(key, value).commit();
    }

    public static String getString(String key) {
        return getPrefs().getString(key, "");
    }

    public static String getString(String key, String defaultVal) {
        return getPrefs().getString(key, defaultVal);
    }

    public static boolean getBoolean(String key, boolean def) {
        return getPrefs().getBoolean(key, def);
    }

    public static int getInt(String key, int def) {
        return getPrefs().getInt(key, def);
    }
}
