package com.zjw.base.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.telephony.TelephonyManager;

import com.zjw.base.config.BaseMainApp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by Frank on 2017/3/8.
 */

public enum AppUtil {
    INSTANCE;

    /**
     * 返回当前程序版本名
     */
    public String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            LogCatUtil.e("VersionInfo", "Exception" + e.getMessage());
        }
        return versionName;
    }

    /**
     * 返回当前程序版本号
     */
    public int getAppVersionCode(Context context) {
        int versioncode = 0;
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versioncode = pi.versionCode;
            if (versioncode == 0) {
                return 0;
            }
        } catch (Exception e) {
            LogCatUtil.e("VersionInfo", "Exception" + e.getMessage());
        }
        return versioncode;
    }

    /**
     * 判断是否是debug包
     *
     * @param context
     * @return
     */
    public static boolean isApkDebugable(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {

        }
        return false;
    }

    public String getDeviceToken() {
        // 获取设备号
        TelephonyManager tm = (TelephonyManager) BaseMainApp.getAppContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        String device_token = tm.getDeviceId();
        return device_token;
    }



}
