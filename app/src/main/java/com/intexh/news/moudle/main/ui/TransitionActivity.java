package com.intexh.news.moudle.main.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zjw.base.utils.Settings;
import com.zjw.base.utils.ValidateUtils;

public class TransitionActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setBackgroundDrawable(null);
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(TransitionActivity.this, SplashActivity.class);
        startActivity(intent);
        finish();
    }
}
