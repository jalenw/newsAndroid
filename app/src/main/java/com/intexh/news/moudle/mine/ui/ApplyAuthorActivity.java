package com.intexh.news.moudle.mine.ui;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.intexh.news.R;
import com.intexh.news.base.AppBaseActivity;
import com.intexh.news.moudle.mine.bean.CooperationBean;
import com.intexh.news.net.Apis;
import com.intexh.news.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;
import com.zjw.base.net.NetworkManager;
import com.zjw.base.utils.GsonUtils;
import com.zjw.base.utils.ValidateUtils;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ApplyAuthorActivity extends AppBaseActivity {


    @BindView(R.id.back_iv)
    ImageView backIv;
    @BindView(R.id.name_etv)
    EditText nameEtv;
    @BindView(R.id.number_etv)
    EditText numberEtv;
    @BindView(R.id.card_number_etv)
    EditText cardNumberEtv;
    @BindView(R.id.check)
    CheckBox check;
    @BindView(R.id.apply_tv)
    TextView applyTv;
    @BindView(R.id.tips_tv)
    TextView tipsTv;
    @BindView(R.id.protcal_tv)
    TextView protcalTv;
    CooperationBean cooperationBean;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_apply_author;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    @Override
    public void initView() {
        super.initView();

    }

    @Override
    public void initData() {
        super.initData();
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
                cooperationBean= GsonUtils.jsonToBean(result, CooperationBean.class);
                if (ValidateUtils.isValidate(cooperationBean)) {
                    tipsTv.setText(ValidateUtils.isValidate(cooperationBean.getYanzheng()) ? cooperationBean.getYanzheng() : "");

                }
            }

            @Override
            public void onError(String errorMessage) {
                hideProgress();
            }
        });
    }

    @OnClick({R.id.back_iv, R.id.apply_tv, R.id.protcal_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                this.finish();
                break;
            case R.id.apply_tv:
                if (!check.isChecked()) {
                    ToastUtil.showToast(mContext, "需要同意作者协议");
                    return;
                }
                String name = nameEtv.getText().toString().trim();
                String phone = numberEtv.getText().toString().trim();
                String cardNumber = cardNumberEtv.getText().toString().trim();

                if (ValidateUtils.isValidate(name) && ValidateUtils.isValidPhoneNumber(phone) && ValidateUtils.isValidate(cardNumber)) {
                    applyForAuthor(name, phone, cardNumber);
                } else {
                    ToastUtil.showToast(mContext, "请完善个人资料");
                }

                break;
            case R.id.protcal_tv:
                if (!ValidateUtils.isValidate(cooperationBean)){
                    showToast("加载中");
                    return;
                }
                Intent min=new Intent(mContext,ProtacolActivity.class);
                min.putExtra("content",cooperationBean.getXieyi());
                startActivity(min);
                break;
        }
    }

    private void applyForAuthor(String name, String phone, String idNumber) {
        showProgress("请稍后");
        HashMap<String, String> params = new HashMap<>();
        params.put("truename", name);
        params.put("mobile", phone);
        params.put("idcard", idNumber);
        NetworkManager.INSTANCE.post(Apis.applyToAuthor, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                hideProgress();
                ToastUtil.showToast(mContext, "申请成功");
                MobclickAgent.onEvent(mContext,"author");
                ApplyAuthorActivity.this.finish();
            }

            @Override
            public void onError(String errorMessage) {
                hideProgress();
                ToastUtil.showToast(mContext, errorMessage);
            }
        });

    }
}
