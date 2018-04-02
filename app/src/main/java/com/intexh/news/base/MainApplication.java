package com.intexh.news.base;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;


import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.TtsMode;
import com.mob.MobSDK;
import com.intexh.news.helper.UserHelper;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.zjw.base.config.BaseMainApp;
import com.zjw.base.utils.ValidateUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;


/**
 * Created by Frank on 2017/6/12.
 */

public class MainApplication extends BaseMainApp {
    public final static Map<String, Activity> webActivityList = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
        MobSDK.init(getAppContext());
        JPushInterface.init(this);
        Context context = getApplicationContext();
// 获取当前包名
        String packageName = context.getPackageName();
// 获取当前进程名
        String processName = getProcessName(android.os.Process.myPid());
// 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
// 初始化Bugly
        CrashReport.initCrashReport(context, "b50084bd8e", true, strategy);
//        CrashReport.initCrashReport(getApplicationContext(), "注册时申请的APPID", true);
        final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
// 将该app注册到微信
        msgApi.registerApp("wx0070a9a5bbfef3a0");
        if (ValidateUtils.isValidate(UserHelper.getJPUSHMode())) {
            if (UserHelper.getJPUSHMode()) {
                JPushInterface.resumePush(this);
            } else {
                JPushInterface.stopPush(this);
            }
        }
        //友盟
        UMConfigure.init(this, "5a95771ef29d9830ba000305", "AndroidNews", UMConfigure.DEVICE_TYPE_PHONE, "");
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        UMConfigure.setLogEnabled(true);
    }


    //网页
    public static void addActivity(String key, Activity context) { //记录打开的activity
        webActivityList.put(key, context);
    }

    public static void removeActivity(String key) {
        webActivityList.remove(key);
    }
    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }
}
