package com.zalivka.commons.utils;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;

import java.util.Arrays;
import java.util.List;

public class Hacks {

    public static boolean checkLP(){
        List names = Arrays.asList("com.chelpus.luckypatch", "com.dimonvideo.luckypatcher", "com.android.protips");
        List<PackageInfo> packagesList = StaticContextHolder.mCtx.getPackageManager().getInstalledPackages(0);
        for (int i=0; i < packagesList.size(); i++) {
            PackageInfo packInfo = packagesList.get(i);
            if ((packInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && names.contains(packInfo.packageName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkFreedom(){
        List names = Arrays.asList("cc.madkite.freedom");
        List<PackageInfo> packagesList = StaticContextHolder.mCtx.getPackageManager().getInstalledPackages(0);
        for (int i=0; i < packagesList.size(); i++) {
            PackageInfo packInfo = packagesList.get(i);
            if ((packInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && names.contains(packInfo.packageName)) {
                return true;
            }
        }
        return false;
    }
}
