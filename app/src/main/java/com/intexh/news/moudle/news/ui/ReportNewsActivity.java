package com.intexh.news.moudle.news.ui;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.intexh.news.moudle.disclose.bean.DraftBean;
import com.intexh.news.moudle.mine.ui.RegisterActivity;
import com.intexh.news.net.Apis;
import com.intexh.news.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;
import com.zjw.base.glide.GlideHelper;
import com.zjw.base.net.NetworkManager;
import com.zjw.base.utils.GsonUtils;
import com.zjw.base.utils.ValidateUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReportNewsActivity extends AppBaseActivity {


    @BindView(R.id.content_et)
    EditText contentEt;
    @BindView(R.id.show_iv)
    ImageView showIv;
    @BindView(R.id.content_size_tv)
    TextView contentSizeTv;
    List<LocalMedia> localMedias;
    String news_id;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_report_news;
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
        contentEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int textSum = contentEt.getText().toString().length();
//                if (textSum <= 0) {
//                    //titleSizeTv.setText("");
//                }
                if (textSum >= 0 && textSum <= 400) {
                    contentSizeTv.setText(String.valueOf(400 - textSum));
                    contentSizeTv.setTextColor(getResources().getColor(R.color.hintColor));
                }
                if (textSum > 400) {
                    contentSizeTv.setText(String.valueOf(400 - textSum));
                    contentSizeTv.setTextColor(getResources().getColor(R.color.colorAccent));
                }
            }
        });
        news_id = getIntent().getStringExtra("NEWS_ID");
    }

    @OnClick({R.id.back_iv, R.id.add_pic_iv, R.id.punish_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                this.finish();
                break;
            case R.id.add_pic_iv:
                getPicture();
                break;
            case R.id.punish_btn:
                String content = contentEt.getText().toString().trim();
                if (content.length() > 400) {
                    ToastUtil.showToast(mContext, "内容应在400字内");
                    return;
                }
                if (!ValidateUtils.isValidate(content)){
                    ToastUtil.showToast(mContext, "请输入举报内容");
                    return;
                }
                if (ValidateUtils.isValidate(localMedias)) {
                    loadFile(localMedias.get(0).getCompressPath());
                } else {
                    reportNews(news_id, content, "");
                }

                break;
        }
    }

    void reportNews(String id, String content, String picPath) {
        showProgress();
        Map<String, String> params = new HashMap<>();
        params.put("new_id", id);
        params.put("accusation_content", content);
        if (ValidateUtils.isValidate(picPath)) {
            params.put("accusation_pic", picPath);
        }
        NetworkManager.INSTANCE.post(Apis.reportNews, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                hideProgress();
                showToast("举报成功");
                MobclickAgent.onEvent(mContext,"report",id);
                ReportNewsActivity.this.finish();
            }

            @Override
            public void onError(String errorMessage) {
                hideProgress();
                showToast(errorMessage);
            }
        });
    }

    private void getPicture() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .selectionMode(PictureConfig.SINGLE)
                .maxSelectNum(1)
                .previewImage(true)//预览
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
        showProgress("上传图片");
        NetworkManager.INSTANCE.uploadImage(Apis.upLoadPic, path, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                hideProgress();
                String imgUrl = GsonUtils.getStringFromJSON(response, "datas", "thumb_name");
                String content = contentEt.getText().toString().trim();
                reportNews(news_id, content, imgUrl);
            }

            @Override
            public void onError(String errorMessage) {
                hideProgress();
                ToastUtil.showToast(mContext, errorMessage);
            }
        });
    }
}
