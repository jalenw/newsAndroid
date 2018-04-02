package com.intexh.news.html.inteface;

import android.os.Handler;
import android.os.Message;
import android.webkit.JavascriptInterface;


import com.intexh.news.base.AppBaseActivity;
import com.intexh.news.html.ui.WebViewActivity;
import com.zjw.base.UI.BaseFragment;


public class JavaScriptInterface {

    //   private final PayListener payListener;
    private AppBaseActivity appBaseActivity;
    private Handler handler;
    private BaseFragment fragment;

    public JavaScriptInterface(AppBaseActivity appBaseActivity, Handler handler) {
        this.appBaseActivity = appBaseActivity;
//        this.payListener = payListener;
        this.handler = handler;
    }

    public JavaScriptInterface(BaseFragment fragment, Handler handler) {
        this.fragment = fragment;
//        this.payListener = payListener;
        this.handler = handler;
    }


    @JavascriptInterface
    public void back() {
        Message message = new Message();
        message.what = WebViewActivity.BACK;
        handler.sendMessage(message);
    }

    @JavascriptInterface
    public void pay_style(String pay_sn, String order_id, String pay_name) {
//        payListener.payStart(pay_name);
        Message message = new Message();
        message.what = WebViewActivity.PAY;
        message.obj = pay_sn + "," + order_id + "," + pay_name;
        handler.sendMessage(message);
    }

    //分类回传方法(大类id，小类id)
    @JavascriptInterface
    public void getShopSort(String gc_id, String id) {
        Message message = new Message();
        message.what = WebViewActivity.SUBTHEME;
        message.obj = gc_id + "," + id;
        handler.sendMessage(message);
    }
    //商品详情页分享 H5调原生。

    /**
     * 图片，标题，描述，地址
     *
     * @param pic
     * @param title
     * @param describe
     * @param url
     */
    @JavascriptInterface
    public void share(String pic, String title, String describe, String url) {
        Message message = new Message();
        message.what = WebViewActivity.SHARE;
        message.obj = pic + "," + title + "," + describe + "," + url;
        handler.sendMessage(message);
    }

    /**
     * 聊天客服
     *
     * @param id
     * @param userID
     * @param userName
     * @param userAvater
     */
    @JavascriptInterface
    public void go2Chat(String id, String userID, String userName, String userAvater) {
        Message message = new Message();
        message.what = WebViewActivity.CHSTSERVICE;
        message.obj = id + "," + userID + "," + userName + "," + userAvater;
        handler.sendMessage(message);
    }
    @JavascriptInterface
    public void payCourse(String course_id, String name, String price) {
//        payListener.payStart(pay_name);
        Message message = new Message();
        message.what = WebViewActivity.PURCHESE;
        message.obj = course_id + "," + name + "," + price;
        handler.sendMessage(message);
    }

}