package com.intexh.news.moudle.mine.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.intexh.news.R;
import com.intexh.news.base.AppBaseActivity;
import com.intexh.news.helper.UserHelper;
import com.intexh.news.moudle.mine.bean.LoginBean;
import com.intexh.news.moudle.mine.event.ReBongdingEvent;
import com.intexh.news.net.Apis;
import com.intexh.news.utils.DataUtil;
import com.intexh.news.utils.DialogUtil;
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

public class RebongdingActivity extends AppBaseActivity {


    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.tag_phone_tv)
    TextView tagPhoneTv;
    @BindView(R.id.phone_et)
    EditText phoneEt;
    @BindView(R.id.yzm_et)
    EditText yzmEt;
    @BindView(R.id.login_btn)
    Button loginBtn;
    String type;
    String mCodeUrl;
    String mVerifyUrl;
    @BindView(R.id.yzm_easytv)
    EasyTextView yzmEasytv;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_rebongding;
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
        type = getIntent().getStringExtra("TYPE");
        switch (type) {
            case "mail":
                mCodeUrl = Apis.getMailCode;
                mVerifyUrl = Apis.veriMailCode;
                titleTv.setText("绑定邮箱");
                tagPhoneTv.setText("邮箱");
                phoneEt.setHint("请输入邮箱地址");
                break;
            default:
                mCodeUrl = Apis.getRegisterMessage;
                mVerifyUrl = Apis.veriPhoneCode;
                titleTv.setText("绑定手机号");
                tagPhoneTv.setText("手机号");
                phoneEt.setHint("请输入手机号");
                break;
        }
    }

    @OnClick({R.id.back_iv, R.id.yzm_easytv, R.id.login_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                this.finish();
                break;
            case R.id.yzm_easytv:
                if (!ValidateUtils.isValidPhoneNumber(phoneEt.getText().toString().trim())) {
                    ToastUtil.showToast(mContext, "请输入正确手机号");
                    return;
                }
                yzmEt.setFocusable(true);
                yzmEt.setFocusableInTouchMode(true);
                yzmEt.requestFocus();
                InputMethodManager imm = (InputMethodManager) RebongdingActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                getVerifyCode();
                break;
            case R.id.login_btn:
                String phone = phoneEt.getText().toString().trim();
                String code = yzmEt.getText().toString().trim();
                if (ValidateUtils.isValidate(phone) && ValidateUtils.isValidate(code)) {
                    checkMObile(phone);

                } else {
                    ToastUtil.showToast(mContext, "请完善资料");
                }
                break;
        }
    }

    private void checkMObile(String phone) {
        showProgress();
        HashMap<String, String> params = new HashMap<>();
        params.put("phone", phone);
        NetworkManager.INSTANCE.post(Apis.checkMObile, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                hideProgress();
                String result = GsonUtils.getStringFromJSON(response, "datas");
                if (ValidateUtils.isValidate(result)) {
                    DialogUtil.showConfirmHitDialog(mContext, "", "改手机号存在账户信息，是否删除后绑定微信", "取消", "确定", new DialogUtil.DialogImpl() {
                        @Override
                        public void onRight() {
                            super.onRight();
                            String code = yzmEt.getText().toString().trim();
                            doLogin(phone, code);
                        }
                    });
                }
            }

            @Override
            public void onError(String errorMessage) {
                hideProgress();
                showToast(errorMessage);
            }
        });
    }

    public void getVerifyCode() {
        if (ValidateUtils.isValidate(phoneEt.getText().toString().trim())) {
            params.clear();
            HashMap<String, String> params = new HashMap<>();
            if (type.equals("mail")) {
                params.put("email", phoneEt.getText().toString().trim());
            } else {
                params.put("phone", phoneEt.getText().toString().trim());
            }
            NetworkManager.INSTANCE.post(mCodeUrl, params, new NetworkManager.OnRequestCallBack() {
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

    public void doLogin(String name, String code) {
        showProgress("正在提交");
        HashMap<String, String> params = new HashMap<>();
        params.clear();
        if (type.equals("mail")) {
            params.put("email", name);
            params.put("auth_code", code);
        } else {
            params.put("phone", name);
            params.put("captcha", code);
        }
        NetworkManager.INSTANCE.post(mVerifyUrl, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                hideProgress();
                LoginBean loginBean=UserHelper.getUserInfo();
                loginBean.setMember_mobile(phoneEt.getText().toString().trim());
                UserHelper.saverUserInfo(loginBean);
                ReBongdingEvent reBongdingEvent = new ReBongdingEvent();
                if (type.equals("mail")) {
                    reBongdingEvent.setMail(name);
                } else {
                    reBongdingEvent.setPhone(name);
                }
                EventBus.getDefault().post(reBongdingEvent);
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
