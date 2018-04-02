package com.intexh.news.module.message.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.intexh.news.html.ui.WebViewActivity;
import com.zjw.base.utils.GsonUtils;
import com.zjw.base.utils.LogCatUtil;

import java.util.Objects;

import cn.jpush.android.api.JPushInterface;

import static com.intexh.news.net.Apis.mBaseUrl;

public class JPushReceiver extends BroadcastReceiver {
    private static final String TAG = "news";
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        LogCatUtil.e(TAG, "[MyReceiver] 接收EXTRA_EXTRA: " +bundle.getString(JPushInterface.EXTRA_EXTRA));
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            LogCatUtil.e(TAG, "[MyReceiver] 接收Registration Id : " + regId);
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            LogCatUtil.e(TAG,"[MyReceiver] 接收到推送下来的自定义消息: "+ bundle.getString(JPushInterface.EXTRA_EXTRA));
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            LogCatUtil.e(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            LogCatUtil.e(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            LogCatUtil.e(TAG, "[MyReceiver] 用户点击打开了通知,进入app");
            handleMessage(context,bundle);
//
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            LogCatUtil.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK:" + bundle.getString(JPushInterface.EXTRA_EXTRA));
        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            LogCatUtil.e(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
        } else {
            LogCatUtil.e(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    private void handleMessage(final Context context, Bundle bundle) {
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        String newsId = GsonUtils.getStringFromJSON(extras, "news_id");
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("KEY_WEBURL", mBaseUrl+"wap/news_detail.html?new_id="+newsId);
        context.startActivity(intent);
    }

    private void saveMessage(Objects bean){

    }

}
