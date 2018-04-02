package com.intexh.news.moudle.mine.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
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

public class InverstAdsActivity extends AppBaseActivity {

    @BindView(R.id.back_iv)
    ImageView backIv;
    @BindView(R.id.name_etv)
    EditText nameEtv;
    @BindView(R.id.number_etv)
    EditText numberEtv;
    @BindView(R.id.company_name_etv)
    EditText companyNameEtv;
    @BindView(R.id.budget_etv)
    EditText budgetEtv;
    @BindView(R.id.apply_tv)
    TextView applyTv;
    @BindView(R.id.call_tv)
    TextView callTv;
    @BindView(R.id.call2_tv)
    TextView call2Tv;
    CooperationBean cooperationBean;
    @BindView(R.id.tag_linkman_tv)
    TextView tagLinkmanTv;
    @BindView(R.id.tag_number_tv)
    TextView tagNumberTv;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_inverst_ads;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    public void initView() {
        super.initView();
        tagLinkmanTv.setText(Html.fromHtml("<font color='#DF3031'>*</font>联系人"));
        tagNumberTv.setText(Html.fromHtml("<font color='#DF3031'>*</font>手机号"));
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
                if (ValidateUtils.isValidate(cooperationBean)) {
                    callTv.setText((ValidateUtils.isValidate(cooperationBean.getGdphone()) ? cooperationBean.getGdphone() : ""));
                    call2Tv.setText((ValidateUtils.isValidate(cooperationBean.getGdtel()) ? cooperationBean.getGdtel() : ""));
                }
            }

            @Override
            public void onError(String errorMessage) {
                hideProgress();
            }
        });
    }

    @Override
    public void initData() {
        super.initData();

    }

    @OnClick({R.id.back_iv, R.id.apply_tv, R.id.call_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                this.finish();
                break;
            case R.id.apply_tv:
                String name = nameEtv.getText().toString().trim();
                String phone = numberEtv.getText().toString().trim();
                String company = ValidateUtils.isValidate(companyNameEtv.getText().toString().trim())?companyNameEtv.getText().toString().trim():"";
                String money =ValidateUtils.isValidate(budgetEtv.getText().toString().trim())?budgetEtv.getText().toString().trim():"";
                if (ValidateUtils.isValidate(name) && ValidateUtils.isValidate(phone)) {
                    doApply(name, phone, company, money);
                } else {
                    ToastUtil.showToast(mContext, "请先完善信息");
                }
                break;
            case R.id.call_tv:
                if (ValidateUtils.isValidate(cooperationBean)) {
                    Intent intent1 = new Intent(Intent.ACTION_DIAL);
                    intent1.setData(Uri.parse("tel:" + cooperationBean.getGdphone()));
                    startActivity(intent1);
                }
                break;
            case R.id.call2_tv:
                if (ValidateUtils.isValidate(cooperationBean)) {
                    Intent intent1 = new Intent(Intent.ACTION_DIAL);
                    intent1.setData(Uri.parse("tel:" + cooperationBean.getGdtel()));
                    startActivity(intent1);
                }
                break;
        }
    }

    private void doApply(String name, String phone, String company, String money) {
        showProgress("请稍后");
        HashMap<String, String> params = new HashMap<>();
        params.put("contact_name", name);
        params.put("contact_phone", phone);
        params.put("contact_company", company);
        params.put("contact_count", money);
        NetworkManager.INSTANCE.post(Apis.inverstAds, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                hideProgress();
                showToast("提交成功");
                MobclickAgent.onEvent(mContext,"advertisement");
                InverstAdsActivity.this.finish();
            }

            @Override
            public void onError(String errorMessage) {
                hideProgress();

            }
        });
    }
}
