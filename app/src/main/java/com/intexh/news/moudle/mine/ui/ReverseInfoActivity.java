package com.intexh.news.moudle.mine.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.intexh.news.R;
import com.intexh.news.base.AppBaseActivity;
import com.intexh.news.helper.UserHelper;
import com.intexh.news.moudle.mine.bean.LoginBean;
import com.intexh.news.moudle.mine.event.LoginOutEvent;
import com.intexh.news.moudle.mine.event.ReBongdingEvent;
import com.intexh.news.moudle.mine.event.ReverseEvent;
import com.intexh.news.net.Apis;
import com.intexh.news.utils.ToastUtil;
import com.zjw.base.glide.GlideHelper;
import com.zjw.base.net.NetworkManager;
import com.zjw.base.utils.GsonUtils;
import com.zjw.base.utils.ValidateUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReverseInfoActivity extends AppBaseActivity {

    @BindView(R.id.avatar_iv)
    ImageView avatarIv;
    @BindView(R.id.nickname_edt)
    EditText nicknameEdt;
    @BindView(R.id.number_edt)
    TextView numberEdt;
    @BindView(R.id.mail_edt)
    TextView mailEdt;
    @BindView(R.id.name_tv)
    TextView nameTv;
    List<LocalMedia> localMedias;

    String memberAvatar;
    String phoneNum;
    String mail;
    String nickname;
    String trueName;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_reverse_info;
    }

    @Override
    public void initView() {
        super.initView();
        LoginBean bean = UserHelper.getUserInfo();
        memberAvatar = ValidateUtils.isValidate(bean.getMember_avatar()) ? bean.getMember_avatar() : "";
        phoneNum = ValidateUtils.isValidate(bean.getMember_mobile()) ? bean.getMember_mobile() : "";
        mail = ValidateUtils.isValidate(bean.getMember_email()) ? bean.getMember_email() : "";
        nickname = ValidateUtils.isValidate(bean.getMember_nickname()) ? bean.getMember_nickname() : "";
        trueName = ValidateUtils.isValidate(bean.getMember_truename()) ? bean.getMember_truename() : "";
        //
        GlideHelper.INSTANCE.loadCircleImage(avatarIv, memberAvatar);
        numberEdt.setText(phoneNum);
        mailEdt.setText(mail);
        nicknameEdt.setText(nickname);
        nameTv.setText(trueName);
    }

    @Subscribe
    public void onMessageEvent(ReBongdingEvent event) {
        mailEdt.setText(ValidateUtils.isValidate(UserHelper.getUserInfo().getMember_mobile())?UserHelper.getUserInfo().getMember_mobile():"");
        HashMap<String,String> params=new HashMap<>();
        params.put("member_mobile",UserHelper.getUserInfo().getMember_mobile());
        NetworkManager.INSTANCE.post(Apis.reverseUserInfo, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {

            }

            @Override
            public void onError(String errorMessage) {

            }
        });
//
//        if (ValidateUtils.isValidate(event.getMail())) {
//            mailEdt.setText(event.getMail());
//        } else {
//            mailEdt.setText(event.getPhone());
//        }
    }

    @OnClick({R.id.back_iv, R.id.save_tv, R.id.avatar_ll,R.id.number_edt,R.id.mail_edt,R.id.name_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                this.finish();
                break;
            case R.id.save_tv:
                phoneNum = ValidateUtils.isValidate(numberEdt.getText().toString().trim()) ? numberEdt.getText().toString().trim() : "";
                mail = ValidateUtils.isValidate(mailEdt.getText().toString().trim()) ? mailEdt.getText().toString().trim() : "";
                nickname = ValidateUtils.isValidate(nicknameEdt.getText().toString().trim()) ? nicknameEdt.getText().toString().trim() : "";
                trueName = ValidateUtils.isValidate(nameTv.getText().toString().trim()) ? nameTv.getText().toString().trim() : "";
                if (ValidateUtils.isValidate(localMedias)) {
                    //   memberAvatar = ValidateUtils.isValidate(localMedias) ? localMedias.get(0).getCompressPath() : "";
                    loadFile(localMedias.get(0).getCompressPath());
                } else {
                    saveUserInfo("", phoneNum, mail, nickname, trueName);
                }
                break;
            case R.id.avatar_ll:
                getPicture();
                break;
            case R.id.number_edt:
                if (ValidateUtils.isValidate(UserHelper.getUserInfo().getMember_mobile())){
                   startActivity(ApplyAuthorActivity.class);
                }else {
                    go2Rebongding("phone");
                }
                break;
            case R.id.mail_edt:
                go2Rebongding("mail");
                break;
            case R.id.name_tv:
                if (!ValidateUtils.isValidate(nameTv.getText().toString().trim())){
                    startActivity(ApplyAuthorActivity.class);
                }
                break;
        }
    }
    void go2Rebongding(String type){
        Intent min=new Intent(mContext,RebongdingActivity.class);
        min.putExtra("TYPE",type);
        startActivity(min);
    }

    private void saveUserInfo(String memberAvatar, String phoneNum, String mail, String nickname, String trueName) {

        showProgress("正在修改");
        HashMap<String, String> params = new HashMap<>();
        params.put("member_avatar", memberAvatar);
        params.put("member_mobile", phoneNum);
        params.put("member_email", mail);
        params.put("member_nickname", nickname);
        params.put("member_truename", trueName);
        NetworkManager.INSTANCE.post(Apis.reverseUserInfo, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                hideProgress();
                ToastUtil.showToast(mContext, "修改成功");
                EventBus.getDefault().post(new ReverseEvent());
                ReverseInfoActivity.this.finish();
            }

            @Override
            public void onError(String errorMessage) {
                hideProgress();
            }
        });
    }


    private void getPicture() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .selectionMode(PictureConfig.SINGLE)
                .maxSelectNum(1)
                .compress(true)
                .forResult(PictureConfig.CHOOSE_REQUEST);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    localMedias = PictureSelector.obtainMultipleResult(data);
                    Log.e("wilson", "图片回调结果");
                    if (ValidateUtils.isValidate(localMedias)) {
                        GlideHelper.INSTANCE.loadCircleImage(avatarIv, "file://" + localMedias.get(0).getCompressPath());
                    }
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    void loadFile(String path) {
        NetworkManager.INSTANCE.uploadImage(Apis.upLoadPic, path, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                String imgUrl = GsonUtils.getStringFromJSON(response, "datas", "thumb_name");
                saveUserInfo(imgUrl, phoneNum, mail, nickname, trueName);

            }

            @Override
            public void onError(String errorMessage) {
                ToastUtil.showToast(mContext, errorMessage);
            }
        });
    }
}
