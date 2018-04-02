package com.intexh.news.moudle.news.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.intexh.news.R;
import com.intexh.news.base.AppBaseActivity;
import com.intexh.news.moudle.mine.ui.MessageLoginActivity;
import com.intexh.news.moudle.news.adapter.CommentAdapter;
import com.intexh.news.moudle.news.bean.AllCommentBean;
import com.intexh.news.net.Apis;
import com.umeng.analytics.MobclickAgent;
import com.zjw.base.net.NetworkManager;
import com.zjw.base.utils.GsonUtils;
import com.zjw.base.utils.ValidateUtils;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CommentActivity extends AppBaseActivity {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    String new_id;
    CommentAdapter commentAdapter;
    @BindView(R.id.comment_edt)
    EditText commentEdt;
    @BindView(R.id.punish_tv)
    TextView punishTv;
    boolean isSubComment = false;
    AllCommentBean bean;//评论列表item

    @Override
    protected int getLayoutId() {
        return R.layout.activity_comment;
    }

    @Override
    public void initView() {
        super.initView();
        new_id = getIntent().getStringExtra("new_id");
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(commentAdapter = new CommentAdapter(mContext));
    }

    @Override
    public void initData() {
        super.initData();
        getAllComment(new_id);
    }

    private void getAllComment(String new_id) {
        showProgress("加载中");
        HashMap<String, String> params = new HashMap<>();
        params.put("new_id", new_id);
        NetworkManager.INSTANCE.post(Apis.getNewsAllComment, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                hideProgress();
                String result = GsonUtils.getStringFromJSON(response, "datas");
                List<AllCommentBean> allCommentBeanList = GsonUtils.jsonToBeanList(result, new TypeToken<List<AllCommentBean>>() {
                }.getType());
                if (ValidateUtils.isValidate(allCommentBeanList)) {
                    commentAdapter.clear();
                    commentAdapter.addAll(allCommentBeanList);
                }
            }

            @Override
            public void onError(String errorMessage) {
                hideProgress();
                showToast(errorMessage);
            }
        });
    }

    @Override
    public void initListener() {
        super.initListener();
        commentAdapter.setCommentOnclick(position -> {

            bean = commentAdapter.getItem(position);
            commentEdt.setHint("回复：" + bean.getReply_unickname());
            commentEdt.setFocusable(true);
            commentEdt.setFocusableInTouchMode(true);
            commentEdt.requestFocus();
            InputMethodManager imm = (InputMethodManager)CommentActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

            isSubComment = true;

        });
        commentAdapter.setPointedClick(position -> {
            bean = commentAdapter.getItem(position);
            HashMap<String, String> params = new HashMap<>();
            params.put("new_id", new_id);
            params.put("reply_id", bean.getReply_id());
            NetworkManager.INSTANCE.post(Apis.pointComment, params, new NetworkManager.OnRequestCallBack() {
                @Override
                public void onOk(String response) {
                    hideProgress();
                    showToast("点赞成功");
//                    getAllComment(new_id);
                }

                @Override
                public void onError(String errorMessage) {
                    hideProgress();
                }
            });
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }


    @OnClick({R.id.back_iv, R.id.punish_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                this.finish();
                break;
            case R.id.punish_tv:
                if (!ValidateUtils.isValidate(commentEdt.getText().toString().trim())) {
                    showToast("评论不能为空");
                    return;
                }
                if (!isSubComment) {
                    showProgress();
                    HashMap<String, String> params = new HashMap<>();
                    params.put("new_id", new_id);
                    params.put("reply_content", commentEdt.getText().toString().trim());
                    NetworkManager.INSTANCE.post(Apis.commentNews, params, new NetworkManager.OnRequestCallBack() {
                        @Override
                        public void onOk(String response) {
                            hideProgress();
                            showToast("评论成功");
                            MobclickAgent.onEvent(mContext,"comment");
                            commentEdt.setText("");
                            getAllComment(new_id);
                        }

                        @Override
                        public void onError(String errorMessage) {
                            hideProgress();
                        }
                    });
                } else {//子评论
                    showProgress();
                    HashMap<String, String> params = new HashMap<>();
                    params.put("new_id", new_id);
                    params.put("reply_content", commentEdt.getText().toString().trim());
                    params.put("r_id", bean.getReply_id());
                    NetworkManager.INSTANCE.post(Apis.commentNews, params, new NetworkManager.OnRequestCallBack() {
                        @Override
                        public void onOk(String response) {
                            hideProgress();
                            showToast("评论成功");
                            MobclickAgent.onEvent(mContext,"comment");
                            commentEdt.setText("");
                            getAllComment(new_id);
                        }

                        @Override
                        public void onError(String errorMessage) {
                            hideProgress();
                        }
                    });
                }
                break;
        }
    }
}
