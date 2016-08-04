package com.zalivka.commons.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class ScrProps {

	static {
		ScrProps.initialize(StaticContextHolder.mCtx);
	}

	public static int screenHeight;
	public static int screenWidth;

	public static DisplayMetrics sMetrics;

	public static void initialize(Context ctx) {
		WindowManager wm = (WindowManager) ctx
				.getSystemService(Context.WINDOW_SERVICE);
		Display disp = wm.getDefaultDisplay();
		ScrProps.screenHeight = disp.getHeight();
		ScrProps.screenWidth = disp.getWidth();
		sMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(sMetrics);
	}

	public static int getDensity() {
		return sMetrics.densityDpi;
	}

	public static int scale(int p) {
		return (int) (sMetrics == null ? 0 : p * sMetrics.density);
	}

	public static float scale(float f) {
		return sMetrics == null ? 0 : f * sMetrics.density;
	}

    public static float getMultiplier() {
        return sMetrics == null ? 1f : sMetrics.density * 0.75f;
    }
	
	public static boolean isLandscape() {
		return screenHeight < screenWidth;
	}
	
	public static int maxDim() {
		return Math.max(screenHeight, screenWidth);
	}
	
	public static int minDim() {
		return Math.min(screenHeight, screenWidth);
	}
}
