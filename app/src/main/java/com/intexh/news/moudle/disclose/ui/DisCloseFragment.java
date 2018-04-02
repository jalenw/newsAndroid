package com.intexh.news.moudle.disclose.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.intexh.news.R;
import com.intexh.news.helper.UserHelper;
import com.intexh.news.moudle.disclose.bean.DraftBean;
import com.intexh.news.net.Apis;
import com.intexh.news.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;
import com.zjw.base.UI.BaseFragment;
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

import static android.app.Activity.RESULT_OK;

/**
 * Created by AndroidIntexh1 on 2017/11/1.
 */

public class DisCloseFragment extends BaseFragment {
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
    static int mDradtCode = 0xe01;
    String resultImg = "";
    String resultID = "";
    @BindView(R.id.title_size_tv)
    TextView titleSizeTv;
    @BindView(R.id.content_size_tv)
    TextView contentSizeTv;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_disclose;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void initView() {
        super.initView();
        titleEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //titleSizeTv.setText();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                int textSum = titleEt.getText().toString().length();
//                if (textSum <= 0) {
//                    //titleSizeTv.setText("");
//                }
                if (textSum >= 0) {
                //    titleTextLimit=false;
                    titleSizeTv.setText(String.valueOf(textSum)+"/30");
                    titleSizeTv.setTextColor(getResources().getColor(R.color.hintColor));
                }
//                if (textSum > 30) {
//               //     titleTextLimit=true;
//                    titleSizeTv.setText(String.valueOf(30 - textSum));
//                    titleSizeTv.setTextColor(getResources().getColor(R.color.colorAccent));
//                }
            }
        });
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
                if (textSum >= 0 ) {
                    contentSizeTv.setText(String.valueOf(textSum)+"/200");
                    contentSizeTv.setTextColor(getResources().getColor(R.color.hintColor));
                }
//                if (textSum > 400) {
//                    contentSizeTv.setText(String.valueOf(400 - textSum));
//                    contentSizeTv.setTextColor(getResources().getColor(R.color.colorAccent));
//                }
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.draft_tv, R.id.add_pic_iv, R.id.draft_btn, R.id.punish_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.draft_tv:
                Intent mintent=new Intent(mContext, DraftActivity.class);
                mintent.putExtra("TYPE","tips");
                startActivityForResult(mintent, mDradtCode);
                //  startActivity(DraftActivity.class);
                break;
            case R.id.add_pic_iv:
                getPicture();
                break;
            case R.id.draft_btn:
                isPunish = false;
                String title = titleEt.getText().toString().trim();
                String content = contentEt.getText().toString().trim();
                if (ValidateUtils.isValidate(title) && ValidateUtils.isValidate(content)) {
                    if (title.length()>30){
                        ToastUtil.showToast(mContext,"标题应在30字内");
                        return;
                    }
                    if (content.length()>1000){
                        ToastUtil.showToast(mContext,"内容应在200字内");
                        return;
                    }
                    if (ValidateUtils.isValidate(localMedias)) {//有图片
                        loadFile(localMedias.get(0).getCompressPath());
                    } else {
                        doDisclose(title, content, "", "1");
                    }
                } else {
                    ToastUtil.showToast(mContext, "请完善标题和内容");
                }
                break;
            case R.id.punish_btn:
//                if (ValidateUtils.isValidate(UserHelper.getCurrentToken())){
//
//                }
                isPunish = true;
                String title1 = titleEt.getText().toString().trim();
                String content1 = contentEt.getText().toString().trim();
                if (ValidateUtils.isValidate(title1) && ValidateUtils.isValidate(content1)) {
                    if (title1.length()>30){
                        ToastUtil.showToast(mContext,"标题应在30字内");
                        return;
                    }
                    if (content1.length()>1000){
                        ToastUtil.showToast(mContext,"内容应在200字内");
                        return;
                    }
                    if (ValidateUtils.isValidate(localMedias)) {//有图片
                        loadFile(localMedias.get(0).getCompressPath());
                    } else {
                        doDisclose(title1, content1, "", "0");
                    }
                } else {
                    ToastUtil.showToast(mContext, "请完善标题和内容");
                }
                break;
        }
    }

    private void doDisclose(String title, String content, String pic, String draft) {
        MobclickAgent.onEvent(mContext,"baoliao");
        showProgress("请稍后");
        String url = Apis.doDisclose;
        HashMap<String, String> params = new HashMap<>();
        params.put("new_title", title);
        params.put("new_content", content);
        if (ValidateUtils.isValidate(pic)) {
            params.put("new_pic", pic);
        }
        if (ValidateUtils.isValidate(resultID)) {
            params.put("new_pic", resultImg);
            params.put("new_id", resultID);
            url = Apis.reverseDraft;
        }
        params.put("new_red", "0");//0不飘红，1飘红
        params.put("type", "tips");//	tips表示爆料，new表示资讯
        params.put("new_draft", draft);//0表示不为草稿，1表示草稿
        NetworkManager.INSTANCE.post(url, params, new NetworkManager.OnRequestCallBack() {
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
                .previewImage(true)//预览
                .enableCrop(true)
                .withAspectRatio(11,6)
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
        if (resultCode == mDradtCode) {
            DraftBean draftBean = GsonUtils.deSerializedFromJson(data.getStringExtra("draftBean"), DraftBean.class);
            initAllView(draftBean);
        } else {

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
                hideProgress();
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
        if (ValidateUtils.isValidate(localMedias)){
            localMedias.clear();
        }
        showIv.setVisibility(View.GONE);
    }

    void initAllView(DraftBean draftBean) {
        titleEt.setText(draftBean.getNew_title());
        contentEt.setText(draftBean.getNew_content());
        if (ValidateUtils.isValidate(draftBean.getNew_pic())){
            GlideHelper.INSTANCE.loadImage(showIv, draftBean.getNew_pic());
            showIv.setVisibility(View.VISIBLE);
        }
        if (draftBean.getNew_draft().equals("0")) {
            isPunish = true;
        } else {
            isPunish = false;
        }
        resultImg = ValidateUtils.isValidate(draftBean.getNew_pic()) ? draftBean.getNew_pic() : "";
        resultID = draftBean.getNew_id();
    }
}
