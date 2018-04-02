package com.intexh.news.utils;

import android.os.CountDownTimer;
import android.widget.TextView;

/**
 * Created by AndroidIntexh1 on 2017/7/22.
 */

public enum GetMobileUtil {
    INSTANCE;

    private boolean mDestroy;

    public void onDestroy(){
        mDestroy = true;
    }
    public void countDownReSend(final TextView textView, long sec) {
        mDestroy = false;
        if (textView == null) return;
        textView.setTag(textView.getTextColors());
        textView.setEnabled(false);
        new CountDownTimer(sec * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                if (mDestroy) {
                    cancel();
                    return;
                }
                textView.setText((millisUntilFinished / 1000) + "秒倒计时");
            }

            public void onFinish() {
                if (mDestroy)
                    return;
                textView.setText("重新获取");
                textView.setEnabled(true);
                textView.setSelected(false);
            }
        }.start();
    }

}