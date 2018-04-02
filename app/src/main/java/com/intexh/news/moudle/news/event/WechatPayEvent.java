package com.intexh.news.moudle.news.event;

/**
 * Created by AndroidIntexh1 on 2017/10/20.
 */

public class WechatPayEvent {
    public String getPayStatus() {
        return PayStatus;
    }

    public void setPayStatus(String payStatus) {
        PayStatus = payStatus;
    }

    private String PayStatus;

    public WechatPayEvent(String PayStatus) {
        setPayStatus(PayStatus);
    }
}
