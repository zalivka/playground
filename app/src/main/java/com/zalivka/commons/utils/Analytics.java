package com.zalivka.commons.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
//import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.zalivka.commons.BuildConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;


//
//                       _oo0oo_
//                      o8888888o
//                      88" . "88
//                      (| -_- |)
//                      0\  =  /0
//                    ___/`---'\___
//                  .' \\|     |// '.
//                 / \\|||  :  |||// \
//                / _||||| -:- |||||- \
//               |   | \\\  -  /// |   |
//               | \_|  ''\---/''  |_/ |
//               \  .-\__  '-'  ___/-. /
//             ___'. .'  /--.--\  `. .'___
//          ."" '<  `.___\_<|>_/___.' >' "".
//         | | :  `- \`.;`\ _ /`;.`/ - ` : | |
//         \  \ `_.   \_ __\ /__ _/   .-` /  /
//     =====`-.____`.___ \_____/___.-`___.-'=====
//                       `=---='
//
//
//     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//
//               ????         ??BUG
//

public class Analytics {

    public static final String FLURRY_ID = StaticContextHolder.mCtx.getPackageName().equals("com.zalivka.animation2") ?
            "J9FFKZ4GF7J65CNNR9R3" : "8KM8DGSWFYZ8TWRKMD7W";

    private static Tracker sTracker;
    private static Tracker sSeriousTracker;

    private static long HOUR = 60 * 60;
    private static long DAY = HOUR * 24;
    private static long WEEK = DAY * 7;

    // user dimensions
    public static final int DIMEN_PAID = 1;
    public static final int DIMEN_COUNTRY = 2;
    public static final int DIMEN_TIMEZONE = 3;
    public static final int DIMEN_WEEK = 4;

    public static void initTracker() {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(StaticContextHolder.mCtx);

        String id = Market.isPaid() ? "UA-41062130-5" : "UA-41062130-8";

        sTracker = analytics.newTracker(id);
        sSeriousTracker = analytics.newTracker("UA-41062130-10");
    }

    public static String getCohort(Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        long firstLaunch = prefs.getLong("firstLaunch", 0);
        if (firstLaunch == 0) {
            return "error";
        }
        long diffSeconds = (System.currentTimeMillis() - firstLaunch) / 1000;
        String coh = null;
        if (diffSeconds < HOUR) {
            coh = "immediate";
        } else if (diffSeconds < DAY) {
            coh = "day";
        } else if (diffSeconds < DAY * 4) {
            coh = "few_days";
        } else if (diffSeconds < WEEK + DAY) {
            coh = "week";
        } else {
            coh = "more_week";
        }
        return coh;
    }

    public static void event(String category, String action, String label) {
        if (BuildConfig.DEBUG) {
            return;
        }
        Map<String, String> flMap = new HashMap<String, String>();
        flMap.put("label", label+"");
        flMap.put("category", category+"");
//        FlurryAgent.logEvent(action, flMap);

        String dimenPaid = Stuff.sPurchasesInterface.isUnlocked("") ? "paid" : "free";

        sTracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .setCustomDimension(DIMEN_PAID, dimenPaid)
                .setCustomDimension(DIMEN_COUNTRY, CountryPolicy.getDeterminedCountry())
                .setCustomDimension(DIMEN_TIMEZONE, timeZoneOffset() + "")
                .setCustomDimension(DIMEN_WEEK, weekNumber() + "")
                .build());
    }

    public static void seriousEvent(String category, String action, String label) {
        if (BuildConfig.DEBUG) {
            return;
        }
        String dimenPaid = Stuff.sPurchasesInterface.isUnlocked("") ? "paid" : "free";

        sSeriousTracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .setCustomDimension(DIMEN_PAID, dimenPaid)
                .setCustomDimension(DIMEN_COUNTRY, CountryPolicy.getDeterminedCountry())
                .setCustomDimension(DIMEN_WEEK, weekNumber() + "")
                .build());
    }

    public static void startSession(Context ctx) {
        if (BuildConfig.DEBUG) {
            return;
        }
//        FlurryAgent.onStartSession(ctx, FLURRY_ID);
    }

    public static void endSession(Context ctx) {
        if (BuildConfig.DEBUG) {
            return;
        }
//        FlurryAgent.onEndSession(ctx);
    }

    public static void screenView(String scrName) {
        if (BuildConfig.DEBUG) {
            return;
        }
        sTracker.setScreenName(scrName);
        String dimenPaid = Stuff.sPurchasesInterface.isUnlocked("") ? "paid" : "free";
        sTracker.send(new HitBuilders.AppViewBuilder().setCustomDimension(DIMEN_PAID, dimenPaid).build());
//        FlurryAgent.onPageView();
    }

    private static int timeZoneOffset() {
        int offset = TimeZone.getDefault().getOffset(System.currentTimeMillis());
        return offset / (1000 * 3600);
    }

    // week count since 1970
    public static int weekNumber() {
        long seconds = System.currentTimeMillis() / 1000;
        return (int) (seconds / WEEK);
    }
}
