package com.intexh.news.moudle.mine.ui;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.intexh.news.R;
import com.intexh.news.base.AppBaseActivity;
import com.intexh.news.helper.UserHelper;
import com.intexh.news.moudle.mine.event.LoginOutEvent;
import com.intexh.news.net.Apis;
import com.intexh.news.utils.CacheUtil;
import com.intexh.news.utils.DialogUtil;
import com.intexh.news.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;
import com.zjw.base.config.BasicConfig;
import com.zjw.base.net.NetworkManager;
import com.zjw.base.utils.GsonUtils;
import com.zjw.base.utils.ValidateUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends AppBaseActivity {


    @BindView(R.id.back_iv)
    ImageView backIv;
    @BindView(R.id.cache_tv)
    TextView cacheTv;
    @BindView(R.id.current_version_tv)
    TextView currentVersionTv;
    @BindView(R.id.about_me_tv)
    TextView aboutMeTv;
    @BindView(R.id.logout_tv)
    TextView logoutTv;
    @BindView(R.id.version_tv)
    TextView versionTv;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    public void initView() {
        super.initView();
        cacheTv.setText(CacheUtil.getCacheSize(this));
        if (ValidateUtils.isValidate(UserHelper.getCurrentToken())) {
            logoutTv.setVisibility(View.VISIBLE);
        }
        String versionName = getAppVersionName(mContext);
        currentVersionTv.setText("V" + versionName);
        versionTv.setText("v" + versionName);
    }

    @OnClick({R.id.back_iv, R.id.cache_tv, R.id.current_version_tv, R.id.about_me_tv, R.id.logout_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                this.finish();
                break;
            case R.id.cache_tv:
                MobclickAgent.onEvent(mContext, "cleanCache");
                DialogUtil.showDefaultDialog(mContext, "", "是否清除缓存", "清除", "取消", new DialogUtil.DialogImpli() {
                    @Override
                    public void onOk() {
                        CacheUtil.cleanAllCache(mContext);
                        UserHelper.saverReadedNEws("");
                        ToastUtil.showToast(mContext, "清理完毕");
                        cacheTv.setText(CacheUtil.getCacheSize(mContext));
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                break;
            case R.id.current_version_tv:
                MobclickAgent.onEvent(mContext, "update");
                checkVersion();
                break;
            case R.id.about_me_tv:
                MobclickAgent.onEvent(mContext, "aboutUs");
                startActivity(AboutMeActivity.class);
                break;
            case R.id.logout_tv:
                DialogUtil.showConfirmHitDialog(mContext, "", "确认退出？", "取消", "确定", new DialogUtil.DialogImpl() {
                    @Override
                    public void onRight() {
                        super.onRight();
                        UserHelper.clearCurrentToken();
                        SettingActivity.this.finish();
                        EventBus.getDefault().post(new LoginOutEvent());
                    }
                });
                break;
        }
    }

    public static String getAppVersionName(Context context) {
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
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    void checkVersion() {
        showProgress("正在检查更新");
        HashMap<String, String> params = new HashMap<>();
        NetworkManager.INSTANCE.post(Apis.aboutUs, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                hideProgress();
                String result = GsonUtils.getStringFromJSON(response, "datas");
                String serverVersion = GsonUtils.getStringFromJSON(result, "app_android_version").replace("v","");
                String downLoad = GsonUtils.getStringFromJSON(result, "app_android_url");

                if (Double.parseDouble(getAppVersionName(mContext)) < Double.parseDouble(serverVersion)) {
                    DialogUtil.showConfirmHitDialog(mContext, "", "检测到新版本,是否前往下载", "取消", "去下载", new DialogUtil.DialogImpl() {
                        @Override
                        public void onRight() {
                            super.onRight();
                            Intent intent = new Intent();
                            intent.setAction("android.intent.action.VIEW");
                            Uri content_url;
                            if (downLoad.contains("http") || downLoad.contains("https")) {
                                content_url = Uri.parse(downLoad);
                            } else {
                                content_url = Uri.parse("http://" + downLoad);
                            }
                            intent.setData(content_url);
                            startActivity(intent);
                        }
                    });
                }else {
                    showToast("当前已是最新版本");
                }
            }

            @Override
            public void onError(String errorMessage) {
                hideProgress();
            }
        });
    }


}
