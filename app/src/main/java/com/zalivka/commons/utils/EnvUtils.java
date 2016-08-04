package com.zalivka.commons.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import com.zalivka.commons.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class EnvUtils {

    public static void dumpPic(Bitmap bm) {
        File f = new File(Environment.getExternalStorageDirectory(), "~pic2.png");
        f.delete();

        FileOutputStream fos = null;
        try {
            f.createNewFile();
            fos = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            IOUtils.closeQuietly(fos);
        }
    }

    public static boolean isTablet() {
        // nexus 7 -sw552dp bug?
        return Math.min(ScrProps.screenHeight, ScrProps.screenWidth) / ScrProps.sMetrics.density > 550;
    }


    public static boolean isLandscape() {
        return (StaticContextHolder.mCtx.getResources().getBoolean(R.bool.is_landscape));
    }

    public static boolean isLandscape(Activity activity) {
        return activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    }

    public static boolean isPortPhone() {
        return !isTablet() && !isLandscape();
    }

    public static boolean isLandPhone() {
        return !isTablet() && isLandscape();
    }

    public static boolean debug() {
        PackageManager pm = StaticContextHolder.mCtx.getPackageManager();
        ApplicationInfo info;
        try {
            info = pm.getApplicationInfo("ru.jecklandin.stickman", 0);
            return (0 != (info.flags & ApplicationInfo.FLAG_DEBUGGABLE));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getRealPathFromURI(Uri imageUri) {
        String path = null;
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = StaticContextHolder.mCtx.getContentResolver().query(imageUri,
                filePathColumn, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            path = cursor.getString(columnIndex);
        } else {
            try {
                File file = new File(new java.net.URI(imageUri.toString()));
                path = file.getAbsolutePath();
            } catch (URISyntaxException e) {
                Log.e("EnvUtils", "URI ERROR ", e);
            }
        }
        return path;
    }

    public static boolean isRigidInstalled() {
        try {
            StaticContextHolder.mCtx.getPackageManager().getApplicationInfo("com.zalivka.director", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static void installRigid(Context ctx) {
        Intent ir = new Intent();
        ir.setData(Uri.parse("market://details?id=com.zalivka.director"));
        ctx.startActivity(ir);
    }

    public static void installPodzalivka(Context ctx) {
        Intent ir = new Intent();
        ir.setData(Uri.parse("market://details?id=com.zalivka.fingerpaint"));
        ctx.startActivity(ir);
    }

    public static void setBasicOrientation(Activity activity) {
        if (true || EnvUtils.isTablet()) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            String orientation = PrefUtils.getString(PrefUtils.SCREEN_ORIENTATION);
            if ("portrait".equals(orientation)) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else if ("landscape".equals(orientation))
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            else throw new IllegalStateException();
        }
    }

    public static void mediascan(String path) {
        MediaScannerConnection.scanFile(StaticContextHolder.mCtx, new String[] {path}, null, null);
    }

    public static boolean isFreedomInstalled() {
        final PackageManager pm = StaticContextHolder.mCtx.getPackageManager();
        List<ApplicationInfo> packages = pm
                .getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            String packageName = packageInfo.packageName;
            if (packageName.contains("cc.madkite.freedom")
                    || packageName.contains("madkite.freedom")) {
                return true;
            }
        }
        return false;
    }

    public static int getVersionCode() {
        PackageInfo pInfo = null;
        try {
            pInfo = StaticContextHolder.mCtx.getPackageManager().getPackageInfo(StaticContextHolder.mCtx.getPackageName(), 0);
            return pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String getVersionName() {
        PackageInfo pInfo = null;
        try {
            pInfo = StaticContextHolder.mCtx.getPackageManager().getPackageInfo(StaticContextHolder.mCtx.getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean hasVibrator() {
        Vibrator mVibrator = (Vibrator)StaticContextHolder.mCtx.getSystemService(Context.VIBRATOR_SERVICE);
        return mVibrator.hasVibrator();
    }

    public static long firstInstallTime() {
        try {
            return StaticContextHolder.mCtx
                    .getPackageManager()
                    .getPackageInfo(StaticContextHolder.mCtx.getPackageName(), 0)
                    .firstInstallTime;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static long lastUpdateTime() {
        try {
            return StaticContextHolder.mCtx
                    .getPackageManager()
                    .getPackageInfo(StaticContextHolder.mCtx.getPackageName(), 0)
                    .lastUpdateTime;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
