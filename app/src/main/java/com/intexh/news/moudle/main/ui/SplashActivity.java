package com.intexh.news.moudle.main.ui;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.ImageView;

import com.google.gson.reflect.TypeToken;
import com.intexh.news.R;
import com.intexh.news.html.ui.WebViewActivity;
import com.intexh.news.moudle.mine.bean.CoverBean;
import com.intexh.news.net.Apis;
import com.zjw.base.glide.GlideHelper;
import com.zjw.base.net.NetworkManager;
import com.zjw.base.utils.GsonUtils;
import com.zjw.base.utils.ValidateUtils;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import android.view.WindowManager;
import java.util.Random;

public class SplashActivity extends Activity {

    private final int SPLASH_DISPLAY_LENGHT = 3000;
    @BindView(R.id.splash_iv)
    ImageView splashIv;
    private Handler handler;
    boolean hasBlocked=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getCoverList();
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        handler = new Handler();
        // 延迟SPLASH_DISPLAY_LENGHT时间然后跳转到MainActivity
        handler.postDelayed(() -> {
            if (hasBlocked){
                return;
            }
            Intent intent = new Intent(SplashActivity.this,
                    MainActivity.class);
            startActivity(intent);
            SplashActivity.this.finish();
        }, SPLASH_DISPLAY_LENGHT);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    private void getCoverList() {
        HashMap<String, String> params = new HashMap<>();
        NetworkManager.INSTANCE.post(Apis.getCover, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                String result = GsonUtils.getStringFromJSON(response, "datas");
                List<CoverBean> coverBeans = GsonUtils.jsonToBeanList(result, new TypeToken<List<CoverBean>>() {
                }.getType());
                if (ValidateUtils.isValidate(coverBeans)) {
                    Random rand = new Random();
                    int i = rand.nextInt(coverBeans.size());
                    GlideHelper.INSTANCE.loadImage(splashIv, coverBeans.get(i).getInfo_cover());
                    splashIv.setOnClickListener(view -> {
                        hasBlocked=true;
                        Intent intent = new Intent(SplashActivity.this, WebViewActivity.class);
                        intent.putExtra("KEY_WEBURL", coverBeans.get(i).getInfo_url());
                        startActivityForResult(intent, 0x21);
                    });
                }
            }

            @Override
            public void onError(String errorMessage) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==0x21&& resultCode==0x21
                ) {
            Intent intent = new Intent(SplashActivity.this,
                    MainActivity.class);
            Log.e("wilson","开启主页");
            startActivity(intent);
            SplashActivity.this.finish();
        }
    }
}