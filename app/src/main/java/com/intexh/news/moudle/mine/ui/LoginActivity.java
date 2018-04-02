package com.intexh.news.moudle.mine.ui;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.intexh.news.R;
import com.intexh.news.base.AppBaseActivity;
import com.intexh.news.net.Apis;
import com.intexh.news.utils.ToastUtil;
import com.zjw.base.net.NetworkManager;
import com.zjw.base.utils.ValidateUtils;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppBaseActivity {


    @BindView(R.id.register_tv)
    TextView registerTv;
    @BindView(R.id.phone_et)
    EditText phoneEt;
    @BindView(R.id.psw_et)
    EditText pswEt;
    @BindView(R.id.forget_tv)
    TextView forgetTv;
    @BindView(R.id.login_btn)
    Button loginBtn;
    @BindView(R.id.back_iv)
    ImageView backIv;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
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
        pswEt.addTextChangedListener(new TextWatcher() {
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

    @OnClick({R.id.register_tv, R.id.forget_tv, R.id.login_btn, R.id.back_iv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.register_tv:
                startActivity(RegisterActivity.class);
                break;
            case R.id.forget_tv:
                break;
            case R.id.login_btn:
                String phone = phoneEt.getText().toString().trim();
                String psw = pswEt.getText().toString().trim();
                if (ValidateUtils.isValidate(phone) && ValidateUtils.isValidate(psw)) {
                    doLogin(phone, psw);
                } else {
                    ToastUtil.showToast(mContext, "请完善资料");
                }
                break;
            case R.id.back_iv:
                this.finish();
                break;
        }
    }

    private void doLogin(String name, String psw) {
        HashMap<String, String> params = new HashMap<>();
        params.clear();
        params.put("phone", name);
        params.put("captcha", psw);
        params.put("client","");
        NetworkManager.INSTANCE.post(Apis.getLogin, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                hideProgress();

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
