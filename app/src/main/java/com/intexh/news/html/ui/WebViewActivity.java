package com.intexh.news.html.ui;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;


import com.intexh.news.R;
import com.intexh.news.base.AppBaseActivity;
import com.intexh.news.base.MainApplication;
import com.intexh.news.helper.ShareHelper;
import com.intexh.news.html.inteface.JavaScriptInterface;
import com.intexh.news.html.manager.WebViewClientUtil;
import com.intexh.news.moudle.mine.bean.ShareBean;
import com.intexh.news.net.Apis;
import com.intexh.news.utils.DialogUtil;
import com.umeng.analytics.MobclickAgent;
import com.zjw.base.utils.LogCatUtil;
import com.zjw.base.utils.ValidateUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebViewActivity extends AppBaseActivity implements WebViewClientUtil.WebClientLoadListener {

    private static final String KEY_WEBURL = "KEY_WEBURL";
    private static final String PARAMS = "PARAMS";
    private static final String TITLE = "TITLE";
    private static final String CONTENT = "CONTENT";
    public static final int BACK = 0x1fe;
    public static final int WEBLOADED = 0x2fe;
    public static final int PAY = 0x3fe;
    public static final int SUBTHEME = 0x4fe;
    public static final int SHARE = 0x5fe;
    public static final int CHSTSERVICE = 0x6fe;
    public static final int PURCHESE = 0x7fe;
    public static final String SENDPOST = "SENDPOST";


    @BindView(R.id.back_iv)
    ImageView back_iv;
    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.error_tv)
    TextView error_tv;
    @BindView(R.id.title_tv)
    TextView title_tv;

    //上传文件的webview
    public final static int FILECHOOSER_RESULTCODE = 1;
    @BindView(R.id.send_tv)
    ImageView moreTv;
    //
    private String mCameraFilePath = null;
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadCallbackAboveL;
    //    private MyChromeClient myChromeClient;
    private String url;
    private String weChatPayJumpUrl;
    private int count = 0;
    private boolean isStartTimer;
    private String mTitle;
    private String mContent;
    Intent splashIntent;
    boolean hasTitle=false;

    /**
     * 不带参数的地址
     *
     * @param context
     * @param url
     */
    public static void startActivity(Context context, String url) {
        if (context == null) return;
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KEY_WEBURL, url);
        context.startActivity(intent);
    }

    /**
     * 带标题
     *
     * @param context
     * @param url
     */
    public static void startActivity(Context context, String title, String url) {
        if (context == null) return;
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KEY_WEBURL, url);
        intent.putExtra(TITLE, title);
        context.startActivity(intent);
    }
    /**
     * 带标题
     *
     * @param context
     * @param url
     */
    public static void startActivity(Context context, String title, String url,String content) {
        if (context == null) return;
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KEY_WEBURL, url);
        intent.putExtra(TITLE, title);
        intent.putExtra(CONTENT, content);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_web_view;
    }

    @Override
    public void initView() {
        initWebView();
        splashIntent = getIntent();
        back_iv.setOnClickListener(v ->
                {
                    setResult(0x21, splashIntent);
                    finish();
                }
        );
        moreTv.setOnClickListener(view -> {
            DialogUtil.showBottomMenuDialog(mContext, "刷新", "分享", "其他浏览器打开", new DialogUtil.BottomMenuDialogImpl() {
                @Override
                public void onMenu1() {
                    DialogUtil.showShareBottomDialog(mContext, (dialog, type) -> {
                        ShareBean shareBean = new ShareBean();
                        shareBean.setTitle(ValidateUtils.isValidate(mTitle)?mTitle:"选报 | 精选您的新闻。");
                        String sdcardPath = Environment.getExternalStorageDirectory().toString();
                        String iconPath = sdcardPath + "/baiduTTS/ic_launcher.png";
                        shareBean.setImgPath(iconPath);
                        shareBean.setText(ValidateUtils.isValidate(mContent)?mContent:"不花一分钱，找个秘书团。24小时精选\"您的行业新闻\"，可读可听，全球资讯，随时随地。");
                        shareBean.setUrl(url);
                        switch (type) {
                            case "wechatTv":
                                ShareHelper.INSTANCE.shareToWeChat(mContext, shareBean);
                                break;
                            case "friendTv":
                                ShareHelper.INSTANCE.shareToWeChatFriend(mContext, shareBean);
                                break;
                            case "qqTv":
                                ShareHelper.INSTANCE.shareToQQ(mContext, shareBean);
                                break;
                            case "weiboTv":
                                ShareHelper.INSTANCE.shareToSinaWeibo(shareBean);
                                break;
                            case "zoneTv":
                                ShareHelper.INSTANCE.shareToQQFriend(mContext, shareBean);
                                break;
                            case "dingdingTv":
                                ShareHelper.INSTANCE.shareToDingding(mContext, shareBean);
                                break;
                        }
                        MobclickAgent.onEvent(mContext, "adShare");
                    });

                }

                @Override
                public void onMenu2() {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(url);
                    intent.setData(content_url);
                    startActivity(intent);
                }

                @Override
                public void onMenuTitle() {
                    // webView.loadUrl(url);
                    webView.reload();
                    Log.e("wilson", "SKT1");
                    //  webView.reload();
                }
            });
        });
    }

    private void initWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JavaScriptInterface(this, javascriptIntefaceHandler), "kljs");
        webView.setWebViewClient(new WebViewClientUtil(this, this));
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setLoadsImagesAutomatically(true);// 加载网页中的图片
        webView.getSettings().setUseWideViewPort(true); //设置使用视图的宽端口
        webView.getSettings().setAllowFileAccess(true);// 可以读取文件缓存(manifest生效)
        webView.setWebChromeClient(new MyChromeClientT1());
        //webView.setWebChromeClient(myChromeClient = new MyChromeClientT1(this, javascriptIntefaceHandler, mUploadMessage, mUploadCallbackAboveL));
        // webView.setDownloadListener(new MyDownloadStart(this));//设置下载监听
        // webview 从Lollipop(5.0)开始webview默认不允许混合模式，https当中不能加载http资源，需要设置开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        loadUrl();
    }

    private void loadUrl() {
        showProgress("加载中");
        url = getIntent().getStringExtra(KEY_WEBURL);
        mTitle = getIntent().getStringExtra(TITLE);
       // title_tv.setText(mTitle);
        mContent=getIntent().getStringExtra(CONTENT);
        //如果现在访问的地址在web界面集合中 关闭旧的界面
        if (MainApplication.webActivityList.containsKey(url)) {
            MainApplication.webActivityList.get(url).finish();
            MainApplication.removeActivity(url);
        }
        MainApplication.addActivity(url, this);
        //判断 如果url是完整地址 直接加载 不拼接 baseUrl
        if (url.contains("http") || url.contains("https")) {
            webView.loadUrl(url);
        } else {
            webView.loadUrl("http://" + url);
        }
        LogCatUtil.e("frank", "网页请求地址：" + url);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (MainApplication.webActivityList.containsKey(url)) {
            MainApplication.removeActivity(url);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (webView.canGoBack() && event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            webView.goBack();
            return true;
        }
        setResult(0x21, splashIntent);
        finish();
        return super.onKeyDown(keyCode, event);

    }

    @Override
    public void loadError() {
        //网页加载错误
        hideProgress();
        webView.setVisibility(View.GONE);
        error_tv.setVisibility(View.VISIBLE);
        error_tv.setOnClickListener(v -> finish());
    }

    @Override
    public void loadFinished(String title) {
        //网页加载完成 这里设置标题
        hideProgress();
        if (mTitle!=null){
            title_tv.setText(mTitle);
            return;
        }
        if (title != null) return;
        title_tv.setText(title);
    }


    /*--------------------------------H5 调用原生--------------------------------*/

    private Handler javascriptIntefaceHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BACK:
                    if (url.contains("address_list")) {

                    }
                    //   WebViewActivity.startActivity(WebViewActivity.this,"地址管理", Apis.addressSelect + UserHelper.getCurrentToken());
                    finish();
                    break;
                case PAY:

                    break;
                case SUBTHEME:
                    break;
                case SHARE:
                    break;

            }
        }
    };


    private Uri imageUri;

    public class MyChromeClientT1 extends WebChromeClient {


        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
          //title_tv.setText(title);
        }


        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            mUploadCallbackAboveL = filePathCallback;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            startActivityForResult(
                    Intent.createChooser(i, "File Browser"),
                    FILECHOOSER_RESULTCODE);
            return true;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage && null == mUploadCallbackAboveL) return;
            Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
            if (mUploadCallbackAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, intent);
            } else if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent data) {
        if (requestCode != FILECHOOSER_RESULTCODE
                || mUploadCallbackAboveL == null) {
            return;
        }
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
            } else {
                String dataString = data.getDataString();
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        mUploadCallbackAboveL.onReceiveValue(results);
        mUploadCallbackAboveL = null;
        return;
    }

}
