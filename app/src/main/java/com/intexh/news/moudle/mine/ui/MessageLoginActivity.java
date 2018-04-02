package com.intexh.news.moudle.mine.ui;


import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.intexh.news.R;
import com.intexh.news.base.AppBaseActivity;
import com.intexh.news.helper.UserHelper;
import com.intexh.news.moudle.mine.bean.LoginBean;
import com.intexh.news.moudle.mine.event.LoginEvent;
import com.intexh.news.net.Apis;
import com.intexh.news.utils.GetMobileUtil;
import com.intexh.news.utils.ToastUtil;
import com.intexh.news.widget.EasyTextView;
import com.zjw.base.net.NetworkManager;
import com.zjw.base.utils.GsonUtils;
import com.zjw.base.utils.ValidateUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MessageLoginActivity extends AppBaseActivity {


    @BindView(R.id.back_iv)
    ImageView backIv;
    @BindView(R.id.phone_et)
    EditText phoneEt;
    @BindView(R.id.yzm_easytv)
    EasyTextView yzmEasytv;
    @BindView(R.id.yzm_et)
    EditText yzmEt;
    @BindView(R.id.login_btn)
    Button loginBtn;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_message_login;
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
        yzmEt.addTextChangedListener(new TextWatcher() {
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
                yzmEt.setFocusable(true);
                yzmEt.setFocusableInTouchMode(true);
                yzmEt.requestFocus();
                InputMethodManager imm = (InputMethodManager)MessageLoginActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                getVerifyCode();
                break;
            case R.id.login_btn:
                String phone = phoneEt.getText().toString().trim();
                String code = yzmEt.getText().toString().trim();
                if (ValidateUtils.isValidate(phone) && ValidateUtils.isValidate(code)) {
                    doLogin(phone, code);
                } else {
                    ToastUtil.showToast(mContext, "请完善资料");
                }
                break;
        }
    }

    public void getVerifyCode() {
        if (ValidateUtils.isValidPhoneNumber(phoneEt.getText().toString().trim())) {
            HashMap<String, String> params = new HashMap<>();
            params.clear();
            params.put("phone", phoneEt.getText().toString().trim());
            params.put("type", "2");//短信类型:1为注册,2为登录,3为找回密码
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

    private void doLogin(String name, String psw) {
        showProgress("正在登录");
        HashMap<String, String> params = new HashMap<>();
        params.clear();
        params.put("phone", name);
        params.put("captcha", psw);
        NetworkManager.INSTANCE.post(Apis.getLogin, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                hideProgress();
                String result = GsonUtils.getStringFromJSON(response, "datas");
                LoginBean loginBean = GsonUtils.jsonToBean(result, LoginBean.class);
                UserHelper.saveCurrentToken(loginBean.getKey());
                UserHelper.saverUserInfo(loginBean);
                EventBus.getDefault().post(new LoginEvent());
                finish();

            }

            @Override
            public void onError(String errorMessage) {
                hideProgress();
                showToast(errorMessage);

            }
        });
    }
}
