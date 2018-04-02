package com.intexh.news.base;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.intexh.news.helper.LoginHelper;
import com.intexh.news.helper.UserHelper;
import com.intexh.news.moudle.mine.bean.LoginBean;
import com.intexh.news.moudle.mine.event.LightModeEvent;
import com.intexh.news.moudle.mine.event.LoginEvent;
import com.intexh.news.moudle.mine.event.TextModeEvent;
import com.intexh.news.moudle.mine.ui.MessageLoginActivity;
import com.intexh.news.net.Apis;
import com.intexh.news.utils.DialogUtil;
import com.intexh.news.utils.ToastUtil;
import com.zjw.base.UI.BaseActivity;
import com.zjw.base.net.NetworkManager;
import com.zjw.base.net.TokenInvalidEvent;
import com.zjw.base.utils.GsonUtils;
import com.zjw.base.utils.ValidateUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.ButterKnife;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;

import static android.support.v7.app.AppCompatDelegate.MODE_NIGHT_AUTO;
import static android.support.v7.app.AppCompatDelegate.MODE_NIGHT_NO;
import static android.support.v7.app.AppCompatDelegate.MODE_NIGHT_YES;
import com.mob.MobSDK;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
/**
 * Created by zhangjiawei on 2017/7/19.
 */

public abstract class AppBaseActivity extends BaseActivity {
    public boolean isDestroy;
    protected Map<String, String> params = new HashMap<>();
    protected LayoutInflater inflater;
    public Handler handler;

    public void showWarningToast(String message) {
        ToastUtil.showWarningToast(mContext, message);
    }

    public void showToast(int resIcon, String message) {
        ToastUtil.showToast(mContext, message, resIcon);
    }

    public void showToast(String message) {
        ToastUtil.showToast(mContext, message);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String mode = UserHelper.getLightMode();
        if (ValidateUtils.isValidate(mode)) {
            switch (mode) {
                case "WHITE":
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
                    break;
                case "DARK":
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
                    break;
                case "AUTO":
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_AUTO);
                    break;
            }
        } else {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
        }

        EventBus.getDefault().register(this);
        inflater = getLayoutInflater();
        handler = new Handler();
        if (getLayoutId() != 0) {
            View view = inflater.inflate(getLayoutId(), null);
            ButterKnife.bind(this, view);
            setContentView(view);
        }

        init(savedInstanceState);
        initView();
        initData();
        initListener();
        CrashHandler crashHandler=CrashHandler.getInstance();
        crashHandler.init(this);
    }

//    @Subscribe
//    public void onEvent(LightModeEvent event) {
//        // UI updates must run on MainThread
//        String mode = UserHelper.getLightMode();
//        switch (mode) {
//            case "WHITE":
//                getDelegate().setLocalNightMode(MODE_NIGHT_NO);
//                //   AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO );
//                break;
//            case "DARK":
//                getDelegate().setLocalNightMode(MODE_NIGHT_YES);
//                //  AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
//                break;
//            case "AUTO":
//                getDelegate().setLocalNightMode(MODE_NIGHT_AUTO);
//                //AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_AUTO);
//                break;
//        }
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(TextModeEvent event) {
        getResources();
    }

    protected void init(Bundle savedInstanceState) {
    }

    public void initView() {

    }

    public void initData() {
    }

    public void initListener() {
    }

    @Subscribe
    public void onMessageEvent(TokenInvalidEvent event) {
        UserHelper.clearCurrentToken();
        UserHelper.clearUserInfo();
        showToast("请先登录");
        LoginHelper helper=new LoginHelper(mContext);
        helper.showDialog();
    }

    protected abstract int getLayoutId();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        isDestroy = true;

    }

    @Override
    public Resources getResources() {
        Resources resources = super.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.fontScale = Float.parseFloat(UserHelper.getTextMode() + "");
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return resources;
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SplashScreen"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);          //统计时长
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SplashScreen"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(this);
    }
}
