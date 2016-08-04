package com.zalivka.commons.utils;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.zalivka.commons.R;

public class UIUtils {

	private static final int SEMI_TRANSP = 100;
	private static final int OPAQUE = 255;
	
	public static void setButtonEnabled(ImageButton btn, boolean enabled) {
    	btn.setEnabled(enabled);
    	btn.setAlpha(enabled ? OPAQUE : SEMI_TRANSP);
    }
	
	public static void setButtonEnabled(Button btn, boolean enabled) {
    	btn.setEnabled(enabled);
        btn.setAlpha(enabled ? 1 : 0.4f);
    	for (Drawable d : btn.getCompoundDrawables()) {
    		if (d != null) {
    			d.setColorFilter(enabled ? null : GraphicUtils.sPlainGrayFilter);
    		}
    	}
    }
	
	public static void setButtonEnabled(ToggleButton btn, boolean enabled) {
		btn.setEnabled(enabled);
		btn.getBackground().setAlpha(enabled ? OPAQUE : SEMI_TRANSP);
	}

    public static void makeFullScreen(boolean hasFocus, Activity activity) {
        if (hasFocus && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    public static void basicWindowSetup(Activity activity) {
        ScrProps.initialize(activity);
//        EnvUtils.setBasicOrientation(activity);
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public static void setFont(TextView tw, boolean bold) {

    }
}
