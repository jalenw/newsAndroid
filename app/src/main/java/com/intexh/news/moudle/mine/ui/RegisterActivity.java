package com.intexh.news.moudle.mine.ui;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.intexh.news.R;
import com.intexh.news.base.AppBaseActivity;
import com.intexh.news.net.Apis;
import com.intexh.news.utils.GetMobileUtil;
import com.intexh.news.utils.ToastUtil;
import com.intexh.news.widget.EasyTextView;
import com.zjw.base.net.NetworkManager;
import com.zjw.base.utils.ValidateUtils;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppBaseActivity {


    @BindView(R.id.back_iv)
    ImageView backIv;
    @BindView(R.id.phone_et)
    EditText phoneEt;
    @BindView(R.id.yzm_easytv)
    EasyTextView yzmEasytv;
    @BindView(R.id.yzm_et)
    EditText yzmEt;
    @BindView(R.id.psw_edt)
    EditText pswEdt;
    @BindView(R.id.comfirm_psw_edt)
    EditText confirmPswEdt;
    @BindView(R.id.login_btn)
    Button loginBtn;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
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
        confirmPswEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (ValidateUtils.isValidate(charSequence)) {
                    loginBtn.setBackgroundResource(R.drawable.shape_login_button);
                    loginBtn.setEnabled(true);
                } else {
                    loginBtn.setBackgroundResource(R.drawable.shape_login_button_off);
                    loginBtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @OnClick({R.id.back_iv, R.id.yzm_easytv, R.id.login_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                this.finish();
                break;
            case R.id.yzm_easytv:
                if (!ValidateUtils.isValidPhoneNumber(phoneEt.getText().toString().trim())) {
                    ToastUtil.showToast(mContext, "请输入正确的手机号码");
                    return;
                }
                getVerifyCode();
                break;
            case R.id.login_btn:
                //
                String phone = phoneEt.getText().toString().trim();
                String veriCode = yzmEt.getText().toString().trim();
                String psw = pswEdt.getText().toString().trim();
                String confirmPsw = confirmPswEdt.getText().toString().trim();
                if (ValidateUtils.isValidate(psw) && !psw.equals(confirmPsw)) {
                    ToastUtil.showToast(mContext, "两次输入密码不一致");
                }
                if (ValidateUtils.isValidate(phone) && ValidateUtils.isValidate(veriCode) && ValidateUtils.isValidate(psw) && ValidateUtils.isValidate(confirmPsw)) {
                    requstRegister(phone, psw, veriCode);
                }
                break;
        }
    }

    public void getVerifyCode() {
        if (ValidateUtils.isValidPhoneNumber(phoneEt.getText().toString().trim())) {
            HashMap<String,String> params=new HashMap<>();
            params.clear();
            params.put("phone", phoneEt.getText().toString().trim());
            params.put("type", "1");//短信类型:1为注册,2为登录,3为找回密码
            NetworkManager.INSTANCE.post(Apis.getRegisterMessage, params, new NetworkManager.OnRequestCallBack() {
                @Override
                public void onOk(String response) {
                    showToast("验证码已发送，请注意查收");
                    GetMobileUtil.INSTANCE.countDownReSend(yzmEasytv, 60);
                }

                @Override
                public void onError(String errorMessage) {
                    showToast(errorMessage);
                }
            });
        }
    }

    private void requstRegister(String phone, String psw, String verifyCode) {
        showProgress("正在注册");
        HashMap<String, String> params = new HashMap<>();
        params.put("phone", phone);
        params.put("password", psw);
        params.put("captcha", verifyCode);
        NetworkManager.INSTANCE.post(Apis.getRegister, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                hideProgress();
                showToast("注册成功");
                RegisterActivity.this.finish();
                startActivity(LoginActivity.class);
            }

            @Override
            public void onError(String errorMessage) {
                showToast(errorMessage);
                hideProgress();
            }
        });
    }
}
