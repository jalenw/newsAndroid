package com.intexh.news.html.manager;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.intexh.news.html.ui.WebViewActivity;
import com.zjw.base.utils.LogCatUtil;


public class WebViewClientUtil extends WebViewClient {
    private final WebClientLoadListener loadListener;
    Activity activity;

    Fragment fragment;

    public WebViewClientUtil(Activity activity,WebClientLoadListener loadListener) {
        this.activity = activity;
        this.loadListener = loadListener;
    }
    public WebViewClientUtil(Fragment fragment, WebClientLoadListener loadListener) {
        this.fragment = fragment;
        this.loadListener = loadListener;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        LogCatUtil.e("frank","加载的网页："+url);
        view.loadUrl(url);
        //WebViewActivity.startActivity(activity,url);
        return true;
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        loadListener.loadError();
    }

    /**
     * 界面加载完后回调
     * @param view
     * @param url
     */
        @Override
        public void onPageFinished(WebView view, String url) {
            String title = view.getTitle(); // 获取网页标题
            loadListener.loadFinished(title);
            super.onPageFinished(view, url);
        }
    public interface WebClientLoadListener{
        void loadError();
        void loadFinished(String title);
    }

}