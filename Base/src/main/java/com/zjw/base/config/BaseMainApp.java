package com.zjw.base.config;

import android.app.Activity;
import android.content.Context;
import android.os.StrictMode;
import android.support.multidex.MultiDexApplication;

import com.zjw.base.net.NetworkManager;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Frank on 2017/3/25.
 * application 父级配置
 */

public class BaseMainApp extends MultiDexApplication {

    private final static ArrayList<Activity> activitys = new ArrayList<>();
    private final static ArrayList<Activity> tempActivitys = new ArrayList<>(); //临时记录activity界面
    private static BaseMainApp instance;

    // 创建一个单线程的线程池。
    // 用于写入数据库
    public static ExecutorService persistenceExecutorService = Executors
            .newSingleThreadExecutor();
    private ExecutorService multiThreadExecutorService;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        BasicConfig.INSTANCE.init(this);    //初始化全局配置工具类
        NetworkManager.INSTANCE.init(this); //初始化网络请求
//        initDebugSetting(); //严苛模式检查
    }

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }

    public static void addActivity(Activity context) {
        activitys.add(context);
    }

    public static void removeActivity(Activity context) {
        activitys.remove(context);
    }
    public static void addTempActivity(Activity context) {
        tempActivitys.add(context);
    }

    public static void removeTempActivity(Activity context) {
        tempActivitys.remove(context);
    }
    public static void finishAllTempActivity() {
        Activity activity;
        for (int i = tempActivitys.size() - 1; i >= 0; i--) {
            activity = tempActivitys.get(i);
            if (activity != null && !activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    public static void finishAllActivity() {
        Activity activity;
        for (int i = activitys.size() - 1; i >= 0; i--) {
            activity = activitys.get(i);
            if (activity != null && !activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    public static BaseMainApp getInstance() {
        return instance;
    }

    //线程池用于保存数据库
    public ExecutorService getExecutor(int size) {
        if (size <= 1) {
            return persistenceExecutorService;
        } else {
            if (multiThreadExecutorService == null) {
                multiThreadExecutorService = Executors.newFixedThreadPool(5);
            }
            return Executors.newFixedThreadPool(5);
        }
    }

    /**
     * 载入调试设置
     */
    private void initDebugSetting() {
        if (BasicConfig.INSTANCE.isDebug()) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectCustomSlowCalls() //API等级11，检查代码执行缓慢
//                    .detectDiskReads()        //磁盘读写检查
//                    .detectDiskWrites()
//                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyDialog() //弹出违规提示对话框
                    .penaltyLog() //在Logcat 中打印违规异常信息
//                    .penaltyFlashScreen() //API等级11 会造成屏幕闪烁，不过一般的设备可能没有这个功能。
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                    .detectLeakedSqlLiteObjects()   //用来检查 SQLiteCursor 或者 其他 SQLite对象是否被正确关闭
                    .detectLeakedClosableObjects() //API等级11    用于资源没有正确关闭时提醒
                    .detectActivityLeaks()      //activity 内存泄漏检测
                    .penaltyLog()
//                    .penaltyDeath()     //当触发违规条件时，直接Crash掉当前应用程序。
                    .build());
        }
    }
}
