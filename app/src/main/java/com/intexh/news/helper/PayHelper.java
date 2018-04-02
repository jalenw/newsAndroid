package com.intexh.news.helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.alipay.sdk.app.PayTask;
import com.intexh.news.base.AppBaseActivity;
import com.intexh.news.net.Apis;
import com.intexh.news.utils.ToastUtil;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zjw.base.net.NetworkManager;
import com.zjw.base.utils.GsonUtils;
import com.zjw.base.utils.ValidateUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Frank on 2017/5/19.
 */

public class PayHelper {
    private final AppBaseActivity context;
    // private final JsPayListener jsPayListener;
//    private Handler handler;
    private int localPayType = -1;
    private boolean isFictitiousOrder;
    private ProgressDialog dialog;

    public PayHelper(AppBaseActivity context) {
        //     this.handler = handler;
        this.context = context;
        // this.jsPayListener = jsPayListener;
    }

    public void getWechatOrder(String newid, String charge, String about) {
        showProgressDialog("请稍候");
        HashMap<String, String> params = new HashMap<>();
        params.put("newid", newid);
        params.put("charge", charge);
        params.put("about", "");
        NetworkManager.INSTANCE.post(Apis.wechatPay, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                if (dialog != null) dialog.dismiss();
                String result = GsonUtils.getStringFromJSON(response, "datas");
                weChatPay(result);

            }

            @Override
            public void onError(String errorMessage) {
                if (dialog != null) dialog.dismiss();
            }
        });
    }

    public void getAliPay(String newid, String charge, String about) {
        showProgressDialog("请稍候");
        HashMap<String, String> params = new HashMap<>();
        params.put("newid", newid);
        params.put("charge", charge);
        params.put("about", "");
        NetworkManager.INSTANCE.post(Apis.aliPay, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                if (dialog != null) dialog.dismiss();
                String result = GsonUtils.getStringFromJSON(response, "datas");
                aliPay(result);

            }

            @Override
            public void onError(String errorMessage) {
                if (dialog != null) dialog.dismiss();
            }
        });
    }

    ;

    /**
     * order_sn位为下订单后的返回信息
     *
     * @param orderInfo
     */
    public void weChatPay(String orderInfo) {
        if (!ValidateUtils.isValidate(orderInfo)) {
            ToastUtil.showToast(context, "订单不可用");
            return;
        }
        String appId = GsonUtils.getStringFromJSON(orderInfo, "appid");
        IWXAPI msgApi = WXAPIFactory.createWXAPI(context, appId);
        PayReq payReq = new PayReq();
        payReq.appId = appId;
        payReq.timeStamp = GsonUtils.getStringFromJSON(orderInfo, "timestamp");
        payReq.prepayId = GsonUtils.getStringFromJSON(orderInfo, "prepayid");
        payReq.nonceStr = GsonUtils.getStringFromJSON(orderInfo, "noncestr");
        payReq.partnerId = GsonUtils.getStringFromJSON(orderInfo, "partnerid");
        payReq.sign = GsonUtils.getStringFromJSON(orderInfo, "sign");
        payReq.packageValue = GsonUtils.getStringFromJSON(orderInfo, "package");
        Log.e("_wilson", "请求订单成功");
        msgApi.sendReq(payReq);
    }

    /**
     * 支付宝支付
     *
     * @param orderInfo 下订单后的返回信息
     */
    private void aliPay(String orderInfo) {
//        String signStr = GsonUtils.getStringFromJSON(orderInfo, "" +
//                "");
        if (!ValidateUtils.isValidate(orderInfo)) {
            ToastUtil.showToast(context, "订单号有误");
            return;
        }
        Runnable payRunnable = () -> {
            PayTask alipay = new PayTask(context);
            Map<String, String> stringStringMap = alipay.payV2(orderInfo, true);
            PayResult payResult = new PayResult(stringStringMap);
            final String resultStatus = payResult.getResultStatus();
            context.runOnUiThread(() -> {
                if (TextUtils.equals(resultStatus, "9000")) {
                    ToastUtil.showToast(context, "支付成功");
                } else {
                    ToastUtil.showToast(context, "支付失败，请重新支付");
                }
            });
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    public void showProgressDialog(String message) {
        if (dialog == null) {
            dialog = new ProgressDialog(context);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(false);
        }
        dialog.setMessage(message);
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }
}
