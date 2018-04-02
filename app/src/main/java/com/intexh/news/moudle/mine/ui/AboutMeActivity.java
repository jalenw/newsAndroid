package com.intexh.news.moudle.mine.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.intexh.news.R;
import com.intexh.news.base.AppBaseActivity;
import com.intexh.news.moudle.mine.bean.CooperationBean;
import com.intexh.news.net.Apis;
import com.zjw.base.glide.GlideHelper;
import com.zjw.base.net.NetworkManager;
import com.zjw.base.utils.GsonUtils;
import com.zjw.base.utils.ValidateUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutMeActivity extends AppBaseActivity {


    @BindView(R.id.cover_tv)
    ImageView coverTv;
    @BindView(R.id.content_tv)
    TextView contentTv;
    @BindView(R.id.phone_1_tv)
    TextView phone1Tv;
    @BindView(R.id.phone_2_tv)
    TextView phone2Tv;
    CooperationBean cooperationBean;
    @BindView(R.id.twinkle)
    TwinklingRefreshLayout twinkle;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_about_me;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        super.initData();
        twinkle.setPureScrollModeOn();
        getUnmberInfo();
    }

    private void getUnmberInfo() {
        showProgress("请稍后");
        params.clear();
        NetworkManager.INSTANCE.post(Apis.aboutUs, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                hideProgress();
                String result = GsonUtils.getStringFromJSON(response, "datas");
                cooperationBean = GsonUtils.jsonToBean(result, CooperationBean.class);
                //   callTv.setText();
                if (ValidateUtils.isValidate(cooperationBean)) {
                    if (ValidateUtils.isValidate(cooperationBean.getLogo())) {
                        GlideHelper.INSTANCE.loadImage(coverTv, (String) cooperationBean.getLogo());
                        coverTv.setVisibility(View.VISIBLE);
                    }
                    phone1Tv.setText(ValidateUtils.isValidate(cooperationBean.getAboutphone()) ? cooperationBean.getAboutphone() : "");
                    phone2Tv.setText(ValidateUtils.isValidate(cooperationBean.getAbouttel()) ? cooperationBean.getAbouttel() : "");
                    contentTv.setText(cooperationBean.getAboutus());
                }
            }

            @Override
            public void onError(String errorMessage) {
                hideProgress();
            }
        });
    }

    @OnClick({R.id.back_iv, R.id.phone_1_tv, R.id.phone_2_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                this.finish();
                break;
            case R.id.phone_1_tv:
                if (!TextUtils.isEmpty(phone1Tv.getText().toString().trim())) {
                    Intent intent1 = new Intent(Intent.ACTION_DIAL);
                    intent1.setData(Uri.parse("tel:" + phone1Tv.getText().toString().trim()));
                    startActivity(intent1);
                }
                break;
            case R.id.phone_2_tv:
                if (!TextUtils.isEmpty(phone2Tv.getText().toString().trim())) {
                    Intent intent1 = new Intent(Intent.ACTION_DIAL);
                    intent1.setData(Uri.parse("tel:" + phone2Tv.getText().toString().trim()));
                    startActivity(intent1);
                }
                break;
        }
    }
}
