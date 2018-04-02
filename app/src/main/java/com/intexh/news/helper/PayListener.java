package com.intexh.news.helper;

/**
 * Created by Frank on 2017/6/10.
 * 支付宝与余额支付回调 微信支付用eventbus注册监听
 */

public interface PayListener {
    void payStart(String orderId);
    void weCahtPayStart(String jumpUrl);
    void paySuccess(String jumpUrl);
    void payFail(String error);

}
