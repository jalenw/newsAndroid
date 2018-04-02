package com.zjw.base.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.zjw.base.config.BaseMainApp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by Frank on 2017/6/12.
 */

public class UIUtil {

    /**
     * 转换像素单位（px）为dp单位
     *
     * @param context
     * @param pixel
     * @return
     */
    public static int px2dp(Context context, float pixel)
    {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displaymetrics);
        //dip和具体像素值的对应公式是dip值 =dpi/160* pixel值，可以看出在dpi（像素密度）为160dpi的设备上1px=1dip
        return Math.round((pixel * (float) displaymetrics.densityDpi) / DisplayMetrics.DENSITY_MEDIUM);
    }


    /**
     * 转换dp单位为像素单位（px）
     *
     * @param context
     * @param dp
     * @return
     */
    public static int dp2px(Context context, float dp)
    {
        final float scale = BaseMainApp.getAppContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f); //Then add 0.5f to round the figure up to the nearest whole number, when converting to an integer
    }


    /**
     * 根据手机的分辨率从 sp 的单位 转成为 px
     */
    public static int sp2px(Context context, float spValue) {
        float fontScale = BaseMainApp.getAppContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取手机屏幕宽度
     * @param context
     * @return
     */
    public static int getWindowWidth(Context context)
    {
        if(context == null) return 0;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);

        return metrics.widthPixels;
    }

    /**
     * 获取手机屏幕高度
     * @param context
     * @return
     */
    public static int getWindowHeight(Context context)
    {
        if(context == null) return 0;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);

        return metrics.heightPixels;
    }

    /**
     * 将Bitmap转换成InputStream
     *
     * @param bm
     * @return
     */
    public static InputStream Bitmap2InputStream(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return new ByteArrayInputStream(baos.toByteArray());
    }

    /**
     * 将Bitmap转换成InputStream
     *
     * @param bm
     * @param quality
     * @return
     */
    public static InputStream Bitmap2InputStream(Bitmap bm, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, quality, baos);
        return new ByteArrayInputStream(baos.toByteArray());
    }

}
