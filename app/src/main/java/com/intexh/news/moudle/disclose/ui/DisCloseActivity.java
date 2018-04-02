package com.intexh.news.moudle.disclose.ui;

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
import butterknife.Unbinder;

public class DisCloseActivity extends AppBaseActivity {
    @BindView(R.id.draft_tv)
    TextView draftTv;
    @BindView(R.id.title_et)
    EditText titleEt;
    @BindView(R.id.content_et)
    EditText contentEt;
    @BindView(R.id.add_pic_iv)
    ImageView addPicIv;
    @BindView(R.id.draft_btn)
    TextView draftBtn;
    @BindView(R.id.punish_btn)
    TextView punishBtn;
    Unbinder unbinder;
    List<LocalMedia> localMedias;
    @BindView(R.id.show_iv)
    ImageView showIv;
    boolean isPunish = false;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_dis_close;
    }


    @OnClick({R.id.draft_tv, R.id.add_pic_iv, R.id.draft_btn, R.id.punish_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.draft_tv:
                startActivity(DraftActivity.class);
                break;
            case R.id.add_pic_iv:
                getPicture();
                break;
            case R.id.draft_btn:
                isPunish = false;

                String title = titleEt.getText().toString().trim();
                String content = contentEt.getText().toString().trim();
                if (ValidateUtils.isValidate(title) && ValidateUtils.isValidate(content)) {
                    if (ValidateUtils.isValidate(localMedias)) {//有图片
                        loadFile(localMedias.get(0).getCompressPath());
                    } else {
                        doDisclose(title, content, "", "0");
                    }
                } else {
                    ToastUtil.showToast(mContext, "请完善标题和内容");
                }
                break;
            case R.id.punish_btn:
                isPunish = true;
                String title1 = titleEt.getText().toString().trim();
                String content1 = contentEt.getText().toString().trim();
                if (ValidateUtils.isValidate(title1) && ValidateUtils.isValidate(content1)) {
                    if (ValidateUtils.isValidate(localMedias)) {//有图片
                        loadFile(localMedias.get(0).getCompressPath());
                    } else {
                        doDisclose(title1, content1, "", "1");
                    }
                } else {
                    ToastUtil.showToast(mContext, "请完善标题和内容");
                }
                break;
        }
    }

    private void doDisclose(String title, String content, String pic, String draft) {
        showProgress("请稍后");
        HashMap<String, String> params = new HashMap<>();
        params.put("new_title", title);
        params.put("new_content", content);
        if (ValidateUtils.isValidate(pic)) {
            params.put("new_pic", pic);
        }
        params.put("new_red", "0");//0不飘红，1飘红
        params.put("type", "tips");//	tips表示爆料，new表示资讯
        params.put("new_draft", draft);//0表示不为草稿，1表示草稿
        NetworkManager.INSTANCE.post(Apis.doDisclose, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                hideProgress();
                ToastUtil.showToast(mContext, "操作成功");
                clearAllView();

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
                String title = titleEt.getText().toString().trim();
                String content = contentEt.getText().toString().trim();
                if (isPunish) {
                    doDisclose(title, content, imgUrl, "0");

                } else {
                    doDisclose(title, content, imgUrl, "1");
                }
            }

            @Override
            public void onError(String errorMessage) {
                ToastUtil.showToast(mContext, errorMessage);
            }
        });
    }

    /**
     * 发布或草稿之后清除容器内容
     */
    void clearAllView() {
        titleEt.setText("");
        contentEt.setText("");
        showIv.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}