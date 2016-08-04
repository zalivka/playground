package com.zalivka.commons.utils;

import android.graphics.Typeface;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Fonts {

    public static final int THIN = 0;
    public static final int LIGHT = 1;
    public static final int MEDIUM = 2;
    public static final int BOLD = 3;

    public static Typeface sRobotoRegularTypeface;
    public static Typeface sRobotoBoldTypeface;
    public static Typeface sGraffitiTypeface;
    public static Typeface sRafaleTypeface;

    public static Map<String, Typeface> sEmbeddedFonts = new HashMap<>();

    public static final boolean sUseFonts;
    static {
        sUseFonts = Arrays.asList("en", "fr", "de", "ru", "es", "pt", "it").contains(
                StaticContextHolder.mCtx.getResources().getConfiguration().locale.getLanguage())
        && !"com.zalivka.fingerpaint".equals(StaticContextHolder.mCtx.getPackageName());

        if (sUseFonts) {
            sRobotoRegularTypeface = Typeface.createFromAsset(StaticContextHolder.mCtx.getAssets(), "fonts/Roboto-Regular.ttf");
            sRobotoBoldTypeface = Typeface.createFromAsset(StaticContextHolder.mCtx.getAssets(), "fonts/Roboto-Bold.ttf");
            sGraffitiTypeface = Typeface.createFromAsset(StaticContextHolder.mCtx.getAssets(), "fonts/SpriteGraffiti.ttf");
            sRafaleTypeface = Typeface.createFromAsset(StaticContextHolder.mCtx.getAssets(), "fonts/rafale.ttf");

            sEmbeddedFonts.put("graffiti", sGraffitiTypeface);
            sEmbeddedFonts.put("rafale", sRafaleTypeface);
            sEmbeddedFonts.put("roboto/bold", sRobotoBoldTypeface);
            sEmbeddedFonts.put("roboto/regular", sRobotoRegularTypeface);
        }
    }

    public static Set<String> getAvailableFonts() {
        return sEmbeddedFonts.keySet();
    }

    public static Typeface getTypeface(int type) {
        if (!sUseFonts)
            return Typeface.defaultFromStyle(type);

        if (type == MEDIUM)
            return sRobotoRegularTypeface;
        else if (type == BOLD)
            return sRobotoBoldTypeface;
        else
            return Typeface.defaultFromStyle(Typeface.NORMAL);
    }

    public static Typeface getByName(String name) {
        Typeface tf = sEmbeddedFonts.get(name);
        return tf == null ? Typeface.DEFAULT : tf;
    }

    public static void setFont(TextView view, int fontStyle) {
        view.setTypeface(getTypeface(fontStyle));
    }
}
