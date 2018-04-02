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
import com.intexh.news.net.Apis;
import com.intexh.news.utils.ToastUtil;
import com.zjw.base.glide.GlideHelper;
import com.zjw.base.net.NetworkManager;
import com.zjw.base.utils.GsonUtils;
import com.zjw.base.utils.ValidateUtils;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeedBackActivity extends AppBaseActivity {


    @BindView(R.id.back_iv)
    ImageView backIv;
    @BindView(R.id.draft_tv)
    TextView draftTv;
    @BindView(R.id.content_et)
    EditText contentEt;
    @BindView(R.id.add_pic_iv)
    ImageView addPicIv;
    @BindView(R.id.phone_edt)
    EditText phoneEdt;
    @BindView(R.id.punish_btn)
    TextView punishBtn;
    @BindView(R.id.show_iv)
    ImageView showIv;
    List<LocalMedia> localMedias;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_feed_back;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
//
//    feedback_content 是
//    string 反馈内容
//    feedback_pic 否
//    string 图片
//    feedback_mobile 否
//    string 联系电话号码
//    feedback_draft//0表示非草稿，1表示草稿

    @Override

    public void initData() {
        super.initData();

    }

    @OnClick({R.id.back_iv, R.id.draft_tv, R.id.add_pic_iv, R.id.punish_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                this.finish();
                break;
            case R.id.draft_tv:
                break;
            case R.id.add_pic_iv:
                getPicture();
                break;
//            case R.id.draft_btn:
//                if (!ValidateUtils.isValidate(contentEt.getText().toString().trim())) {
//                    showToast("请输入反馈文字");
//                    return;
//                }
//
//                break;
            case R.id.punish_btn:
                if (!ValidateUtils.isValidate(contentEt.getText().toString().trim())) {
                    showToast("请输入反馈文字");
                    return;
                }
                preparePunish();
                break;
        }
    }

    void preparePunish() {
        if (ValidateUtils.isValidate(localMedias)) {//有图片
            loadFile(localMedias.get(0).getCompressPath());
            return;
        }
        String content = contentEt.getText().toString().trim();
        String pic = "";
        String phone;
        if (ValidateUtils.isValidate(phoneEdt.getText().toString().trim())) {
            phone = phoneEdt.getText().toString().trim();
        } else {
            phone = "";
        }

        String feedback_draft = "1";
        doFeedBack(content, pic, phone, feedback_draft);

    }

    private void doFeedBack(String content, String pic, String phone, String feedback_draft) {
        HashMap<String, String> params = new HashMap<>();
        showToast("正在提交");
        params.put("feedback_content", content);
        params.put("feedback_pic", pic);
        params.put("feedback_mobile", phone);
        params.put("feedback_draft", feedback_draft);
        NetworkManager.INSTANCE.post(Apis.feedBack, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                hideProgress();
                showToast("提交成功");
                FeedBackActivity.this.finish();

            }

            @Override
            public void onError(String errorMessage) {
                showToast("提交失败");
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
                .enableCrop(true)
                .withAspectRatio(11,6)
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
                        GlideHelper.INSTANCE.loadImage(showIv, "file://" + localMedias.get(0).getCompressPath());
                    }
                    break;
            }
        }
    }

    void loadFile(String path) {
        NetworkManager.INSTANCE.uploadImage(Apis.upLoadPic, path, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                String imgUrl = GsonUtils.getStringFromJSON(response, "datas", "thumb_name");
                String content = contentEt.getText().toString().trim();
                String phone;
                if (ValidateUtils.isValidate(phoneEdt.getText().toString().trim())) {
                    phone = phoneEdt.getText().toString().trim();
                } else {
                    phone = "";
                }
                doFeedBack(content, imgUrl, phone, "1");
            }

            @Override
            public void onError(String errorMessage) {
                ToastUtil.showToast(mContext, errorMessage);
            }
        });
    }
}
