package com.intexh.news.moudle.mine.ui;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.intexh.news.R;
import com.intexh.news.base.AppBaseActivity;
import com.intexh.news.moudle.disclose.bean.DraftBean;
import com.intexh.news.moudle.disclose.ui.DraftActivity;
import com.intexh.news.moudle.mine.event.CheckedEvent;
import com.intexh.news.moudle.news.bean.ChannelBean;
import com.intexh.news.net.Apis;
import com.intexh.news.utils.DataUtil;
import com.intexh.news.utils.DialogUtil;
import com.intexh.news.utils.ToastUtil;
import com.zjw.base.glide.GlideHelper;
import com.zjw.base.net.NetworkManager;
import com.zjw.base.utils.GsonUtils;
import com.zjw.base.utils.ValidateUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddInfoActivity extends AppBaseActivity {


    @BindView(R.id.title_et)
    EditText titleEt;
    @BindView(R.id.content_et)
    EditText contentEt;
    @BindView(R.id.show_iv)
    ImageView showIv;
    @BindView(R.id.redText_tv)
    CheckBox redTextTv;
    List<LocalMedia> localMedias;
    boolean isPunish = false;
    @BindView(R.id.punish_time_tv)
    TextView punishTimeTv;
    @BindView(R.id.column_tv)
    TextView columnTv;
    String currentChannalID;
    @BindView(R.id.title_size_tv)
    TextView titleSizeTv;
    @BindView(R.id.content_size_tv)
    TextView contentSizeTv;
    static int mNewsCode = 0xe02;
    List<ChannelBean> allList;//频道列表
    String[] list;
    @BindView(R.id.draft_tv)
    TextView draftTv;
    @BindView(R.id.draft_btn)
    TextView draftBtn;
    String resultID = "";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_info;
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
//                if (textSum < 0) {
//                    //titleSizeTv.setText("");
//                }
                if (textSum >= 0) {
                    contentSizeTv.setText(String.valueOf(textSum) + "/200");
                    contentSizeTv.setTextColor(getResources().getColor(R.color.hintColor));
                }
//                if (textSum > 1000) {
//                    contentSizeTv.setText(String.valueOf(400 - textSum));
//                    contentSizeTv.setTextColor(getResources().getColor(R.color.colorAccent));
//                }
            }
        });
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

                if (textSum >= 0) {
                    titleSizeTv.setText(String.valueOf(textSum) + "/30");
                    titleSizeTv.setTextColor(getResources().getColor(R.color.hintColor));
                }
            }
        });
        if (ValidateUtils.isValidate(getIntent().getStringExtra("bean"))) {
            DraftBean bean = GsonUtils.deSerializedFromJson(getIntent().getStringExtra("bean"), DraftBean.class);
            // DraftBean draftBean = GsonUtils.deSerializedFromJson(getIntent().getStringExtra("bean"), DraftBean.class);
            draftTv.setVisibility(View.GONE);
            draftBtn.setVisibility(View.GONE);
            initAllView(bean);
        }
        allList = new ArrayList<>();
        getChannel();

    }

    @OnClick({R.id.back_iv, R.id.draft_tv, R.id.add_pic_iv, R.id.draft_btn, R.id.punish_btn, R.id.punish_time_tv, R.id.column_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                this.finish();
                break;
            case R.id.draft_tv:
                Intent mintent = new Intent(mContext, DraftActivity.class);
                mintent.putExtra("TYPE", "news");
                startActivityForResult(mintent, mNewsCode);
                break;
            case R.id.add_pic_iv:
                getPicture();
                break;
            case R.id.draft_btn:
                isPunish = false;
                preparePunish();
                break;
            case R.id.punish_btn:
                isPunish = true;
                preparePunish();
                break;
            case R.id.punish_time_tv:
                showDialogPick(punishTimeTv);
                break;
            case R.id.column_tv:
                if (ValidateUtils.isValidate(list)) {
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Holo_Light_Dialog);
                        //    指定下拉列表的显示数据
                        //    设置一个下拉的列表选择项
                        builder.setItems(list, (dialog, which) -> {
                            columnTv.setText(list[which]);
                            currentChannalID = allList.get(which).getChannel_id();
                        });
                        AlertDialog r_dialog = builder.create();
                        r_dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        r_dialog.show();
                    }
                } else {
                    showToast("正在获取栏目，请稍后");
                }


                break;
        }
    }

    void preparePunish() {
        String title1 = titleEt.getText().toString().trim();
        String content1 = contentEt.getText().toString().trim();
        if (ValidateUtils.isValidate(title1) && ValidateUtils.isValidate(content1) && ValidateUtils.isValidate(currentChannalID) && ValidateUtils.isValidate(punishTimeTv.getText().toString().trim())) {
            if (title1.length() > 55) {
                ToastUtil.showToast(mContext, "标题应在30字内");
                return;
            }
            if (content1.length() > 1000) {
                ToastUtil.showToast(mContext, "内容应在200字内");
                return;
            }
            if (ValidateUtils.isValidate(localMedias)) {//有图片
                loadFile(localMedias.get(0).getCompressPath());
                return;
            }
            String pic = "";
            String isRed;
            if (redTextTv.isChecked()) {
                isRed = "1";
            } else {
                isRed = "0";
            }
            String draft;
            if (isPunish) {
                draft = "0";
            } else {
                draft = "1";

            }
            doDisclose(title1, content1, pic, isRed, draft, currentChannalID, DataUtil.getTimeStamp(punishTimeTv.getText().toString().trim(), "yyyy-MM-dd HH:mm") + "");

        } else {
            ToastUtil.showToast(mContext, "请完善资料");
        }
    }

    /**
     * 发布新闻 参数如下
     *
     * @param title
     * @param content
     * @param pic
     * @param new_red
     * @param draft
     * @param channel_id
     * @param publictime
     */
    private void doDisclose(String title, String content, String pic, String new_red, String draft, String channel_id, String publictime) {
        String url = Apis.doDisclose;
        HashMap<String, String> params = new HashMap<>();
        params.put("new_title", title);
        params.put("new_content", content);
        if (ValidateUtils.isValidate(pic)) {
            params.put("new_pic", pic);
        }
        if (ValidateUtils.isValidate(resultID)) {
            params.put("new_id", resultID);
            params.put("new_allow", "1");
            url = Apis.reverseDraft;
            DialogUtil.showConfirmHitDialog(mContext,"","是否同时发布到推荐频道","取消","确定",new DialogUtil.DialogImpl(){
                @Override
                public void onRight() {
                    super.onRight();
                    params.put("new_hot", "1");
                    params.put("new_red", new_red);//0不飘红，1飘红
                    params.put("type", "news");//	tips表示爆料，new表示资讯
                    params.put("new_draft", draft);//0表示不为草稿，1表示草稿
                    params.put("new_channelid", channel_id);
                    params.put("new_publictime", publictime);
                    showProgress("请稍后");
                    NetworkManager.INSTANCE.post(Apis.reverseDraft, params, new NetworkManager.OnRequestCallBack() {
                        @Override
                        public void onOk(String response) {
                            hideProgress();
                            showToast("操作成功");
                            EventBus.getDefault().post(new CheckedEvent());
                            clearAllView();
                        }

                        @Override
                        public void onError(String errorMessage) {
                            hideProgress();
                            showToast(errorMessage);
                        }
                    });
                }

                @Override
                public void onLeft() {
                    super.onLeft();
                    params.put("new_red", new_red);//0不飘红，1飘红
                    params.put("type", "news");//	tips表示爆料，new表示资讯
                    params.put("new_draft", draft);//0表示不为草稿，1表示草稿
                    params.put("new_channelid", channel_id);
                    params.put("new_publictime", publictime);
                    showProgress("请稍后");
                    NetworkManager.INSTANCE.post(Apis.reverseDraft, params, new NetworkManager.OnRequestCallBack() {
                        @Override
                        public void onOk(String response) {
                            hideProgress();
                            showToast("操作成功");
                            EventBus.getDefault().post(new CheckedEvent());
                            clearAllView();
                        }

                        @Override
                        public void onError(String errorMessage) {
                            hideProgress();
                            showToast(errorMessage);
                        }
                    });
                }
            });

            return;
        }
        params.put("new_red", new_red);//0不飘红，1飘红
        params.put("type", "news");//	tips表示爆料，new表示资讯
        params.put("new_draft", draft);//0表示不为草稿，1表示草稿
        params.put("channel_id", channel_id);
        params.put("new_publictime", publictime);
        showProgress("请稍后");
        NetworkManager.INSTANCE.post(url, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                hideProgress();
                showToast("操作成功");
                clearAllView();
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
                .enableCrop(true)
                .withAspectRatio(11, 6)
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
                        showIv.setVisibility(View.VISIBLE);
                        GlideHelper.INSTANCE.loadImage(showIv, "file://" + localMedias.get(0).getCompressPath());
                    }
                    break;
            }

        }
        if (resultCode == 0xe01) {
            DraftBean draftBean = GsonUtils.deSerializedFromJson(data.getStringExtra("draftBean"), DraftBean.class);
            initAllView(draftBean);
        } else {

        }
    }

    void loadFile(String path) {
        NetworkManager.INSTANCE.uploadImage(Apis.upLoadPic, path, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                String imgUrl = GsonUtils.getStringFromJSON(response, "datas", "thumb_name");
                String title = titleEt.getText().toString().trim();
                String content = contentEt.getText().toString().trim();
                String isRed;
                if (redTextTv.isChecked()) {
                    isRed = "1";
                } else {
                    isRed = "0";
                }
                if (isPunish) {
                    doDisclose(title, content, imgUrl, isRed, "0", currentChannalID, DataUtil.getTimeStamp(punishTimeTv.getText().toString().trim(), "yyyy-MM-dd HH:mm") + "");

                } else {
                    doDisclose(title, content, imgUrl, isRed, "1", currentChannalID, DataUtil.getTimeStamp(punishTimeTv.getText().toString().trim(), "yyyy-MM-dd HH:mm") + "");

                }
            }

            @Override
            public void onError(String errorMessage) {
                ToastUtil.showToast(mContext, errorMessage);
            }
        });
    }

    //将两个选择时间的dialog放在该函数中
    private void showDialogPick(final TextView timeText) {
        final StringBuffer time = new StringBuffer();
        //获取Calendar对象，用于获取当前时间
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        //实例化TimePickerDialog对象
        //选择完时间后会调用该回调函数
        final TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, (view, hourOfDay, minute1) -> {
            time.append(" " + hourOfDay + ":" + minute1);
            //设置TextView显示最终选择的时间
            timeText.setText(time);
        }, hour, minute, true);
        //实例化DatePickerDialog对象
        //选择完日期后会调用该回调函数
        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, (view, year1, monthOfYear, dayOfMonth) -> {
            //因为monthOfYear会比实际月份少一月所以这边要加1
            time.append(year1 + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
            //选择完日期后弹出选择时间对话框
            timePickerDialog.show();
        }, year, month, day);
        //弹出选择日期对话框
        datePickerDialog.show();
    }

    private void getChannel() {
        HashMap<String, String> params = new HashMap<>();
        NetworkManager.INSTANCE.post(Apis.getChannel, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                String result = GsonUtils.getStringFromJSON(response, "datas", "like");
                List<ChannelBean> channelBeanList = GsonUtils.jsonToBeanList(result, new TypeToken<List<ChannelBean>>() {
                }.getType());

                String dislike_result = GsonUtils.getStringFromJSON(response, "datas", "dislike");
                List<ChannelBean> dislike_List = GsonUtils.jsonToBeanList(dislike_result, new TypeToken<List<ChannelBean>>() {
                }.getType());

                if (ValidateUtils.isValidate(channelBeanList)) {
                    allList.addAll(channelBeanList);
                }
                if (ValidateUtils.isValidate(dislike_List)) {
                    allList.addAll(dislike_List);
                }
                int i = 0;
                list = new String[allList.size()];
                for (ChannelBean bean : allList
                        ) {
                    list[i++] = bean.getChannel_content();

                }
            }

            @Override
            public void onError(String errorMessage) {

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
        GlideHelper.INSTANCE.loadImage(showIv,null);
        if (ValidateUtils.isValidate(localMedias)){
            localMedias.clear();
        }
        punishTimeTv.setText("");
        columnTv.setText("");
        redTextTv.setChecked(false);
    }

    void initAllView(DraftBean draftBean) {
        titleEt.setText(draftBean.getNew_title());
        contentEt.setText(draftBean.getNew_content());
        if (ValidateUtils.isValidate(draftBean.getNew_pic())){
            GlideHelper.INSTANCE.loadImage(showIv, draftBean.getNew_pic());
            showIv.setVisibility(View.VISIBLE);
        }else {
            GlideHelper.INSTANCE.loadImage(showIv,null);
        }

        if (draftBean.getNew_draft().equals("0")) {
            isPunish = true;
        } else {
            isPunish = false;
        }
        resultID = draftBean.getNew_id();
        punishTimeTv.setText(DataUtil.getStrTime(draftBean.getNew_publictime()));
        String ChannelName;
        currentChannalID = draftBean.getNew_channelid();
        ChannelName = draftBean.getNew_channelname();
        columnTv.setText(ChannelName);
//        if (ValidateUtils.isValidate(draftBean.getNew_channelid()) && ValidateUtils.isValidate(allList)) {
//            for (ChannelBean bean : allList
//                    ) {
//                Log.e("wilson", bean.getChannel_id());
//                if (bean.getChannel_id().equals(draftBean.getNew_channelid())) {
//                    currentChannalID = draftBean.getNew_channelid();
//                    ChannelName = bean.getChannel_content();
//                    columnTv.setText(ChannelName);
//                }
//            }
//        } else {
//            Log.e("wilson", "没有channelID？");
//            ChannelName = draftBean.getNew_channelname();
//            columnTv.setText(ChannelName);
//        }
        switch (draftBean.getNew_red()) {
            case "0":
                redTextTv.setChecked(false);
                break;
            case "1":
                redTextTv.setChecked(true);
                break;
        }
    }

}
