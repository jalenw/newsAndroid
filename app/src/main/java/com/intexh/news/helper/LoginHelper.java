package com.intexh.news.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.intexh.news.moudle.mine.bean.LoginBean;
import com.intexh.news.moudle.mine.event.LoginEvent;
import com.intexh.news.moudle.mine.ui.MessageLoginActivity;
import com.intexh.news.net.Apis;
import com.intexh.news.utils.DialogUtil;
import com.zjw.base.net.NetworkManager;
import com.zjw.base.utils.GsonUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;


/**
 * Created by AndroidIntexh1 on 2017/12/22.
 */

public class LoginHelper {
    private Context mContext;
    private ProgressDialog dialog;
    public LoginHelper(Context context) {
        mContext = context;
    }

   public void showDialog() {
        DialogUtil.showLoginBottomDialog(mContext, (dialog, type) -> {
            switch (type) {
                case "phoneTv":
                    Intent min=new Intent(mContext,MessageLoginActivity.class);
                    mContext.startActivity(min);
                    dialog.dismiss();
                    break;
                case "wechatTv":
                    requestWeixin();
                    dialog.dismiss();
                    break;
            }
        });
    }

    private void requestWeixin() {
        Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
        wechat.removeAccount(true);
//回调信息，可以在这里获取基本的授权返回的信息，但是注意如果做提示和UI操作要传到主线程handler里去执行
        wechat.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                //输出所有授权信息
                String openid = null;
                platform.getDb().exportData();
                Log.e("_wilson", "授权结果" + "" + platform.getDb().exportData());
                Iterator ite = hashMap.entrySet().iterator();
                while (ite.hasNext()) {
                    Map.Entry entry = (Map.Entry) ite.next();
                    Object key = entry.getKey();
                    Object value = entry.getValue();
                    Log.e("_wilson", "授权信息res :" + key + "： " + value);
                    System.out.println(key + "： " + value);
                }
                weChatLogin(hashMap.get("headimgurl").toString(), hashMap.get("nickname").toString(), hashMap.get("openid").toString(), hashMap.get("unionid").toString());
                Log.e("_wilson", "执行微信登录啊 :");
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                hideProgressDialog();
            }

            @Override
            public void onCancel(Platform platform, int i) {
                hideProgressDialog();
            }
        });
        wechat.showUser(null);//授权并获取用户信息

    }

    private void weChatLogin(String headimgurl, String nickname, String openid, String unionid) {
        HashMap<String, String> params = new HashMap<>();
        params.clear();
        params.put("headimgurl", headimgurl);
        params.put("nickname", nickname);
        params.put("openid", openid);
        params.put("unionid", unionid);
        NetworkManager.INSTANCE.post(Apis.getWechatLogin, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                hideProgressDialog();
                String result = GsonUtils.getStringFromJSON(response, "datas");
                LoginBean loginBean = GsonUtils.jsonToBean(result, LoginBean.class);
                UserHelper.saveCurrentToken(loginBean.getKey());
                UserHelper.saverUserInfo(loginBean);
                EventBus.getDefault().post(new LoginEvent());
            }

            @Override
            public void onError(String errorMessage) {
                hideProgressDialog();
            }
        });
    }
    public void showProgressDialog(String message) {
        if (dialog == null) {
            dialog = new ProgressDialog(mContext);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(false);
        }
        dialog.setMessage(message);
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    public void hideProgressDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
