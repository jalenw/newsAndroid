package com.intexh.news.moudle.main.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


import com.intexh.news.helper.UserHelper;
import com.intexh.news.utils.ToastUtil;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.net.ConnectivityManager.TYPE_MOBILE;
import static android.net.ConnectivityManager.TYPE_WIFI;

/**
 * Created by AndroidIntexh1 on 2017/12/23.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
    //Network的state值
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectionManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable()) {
            switch (networkInfo.getType()) {
                case TYPE_MOBILE:
                    UserHelper.setCurrentNetworkState(1);
                    //ToastUtil.showToast(context, "正在使用2G/3G/4G网络");
                    break;
                case TYPE_WIFI:
                    UserHelper.setCurrentNetworkState(2);
                   // ToastUtil.showToast(context, "正在使用wifi上网");
                    break;
                default:
                    break;
            }
        } else {
            UserHelper.setCurrentNetworkState(0);
            ToastUtil.showToast(context, "网络连接失败，请检查网络");
        }
    }
}

