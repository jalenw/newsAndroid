package com.intexh.news.moudle.mine.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.intexh.news.R;
import com.intexh.news.base.AppBaseActivity;
import com.intexh.news.moudle.mine.adapter.CommentAdapter;
import com.intexh.news.moudle.mine.adapter.PointedAdapter;
import com.intexh.news.moudle.mine.adapter.StoreAdapter;
import com.intexh.news.moudle.mine.adapter.UniversalMessageAdapter;
import com.intexh.news.moudle.mine.bean.CommentBean;
import com.intexh.news.moudle.mine.bean.PointedBean;
import com.intexh.news.moudle.mine.bean.StoreBean;
import com.intexh.news.moudle.news.bean.TabBean;
import com.intexh.news.net.Apis;
import com.intexh.news.utils.ToastUtil;
import com.zjw.base.net.NetworkManager;
import com.zjw.base.utils.GsonUtils;
import com.zjw.base.utils.ValidateUtils;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StoreUniversalActivity extends AppBaseActivity {


    @BindView(R.id.back_iv)
    ImageView backIv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    static public String TYPE = "TYPE";
    String type;
    StoreAdapter storeAdapter;
    PointedAdapter pointedAdapter;
    CommentAdapter commentAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_store;
    }

    @Override
    public void initView() {
        super.initView();
        type = getIntent().getStringExtra(TYPE);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        switch (type) {
            case "STORE":
                titleTv.setText("收藏");
                recyclerView.setAdapter(storeAdapter = new StoreAdapter(mContext));
                break;
            case "LIKE":
                titleTv.setText("点赞");
                recyclerView.setAdapter(pointedAdapter = new PointedAdapter(mContext));
                break;
            case "COMMENT":
                titleTv.setText("评论");
                recyclerView.setAdapter(commentAdapter = new CommentAdapter(mContext));
                break;
        }

    }

    @Override
    public void initData() {
        super.initData();
        switch (type) {
            case "STORE":
                getUserStore();
                break;
            case "LIKE":
                getUserLike();
                break;
            case "COMMENT":
                getUserComment();
                break;
        }
    }

    private void getUserComment() {
        showProgress();
        params.clear();
        NetworkManager.INSTANCE.post(Apis.getUserCommented, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                hideProgress();
                String result = GsonUtils.getStringFromJSON(response, "datas");
                List<CommentBean> storeBeanList = GsonUtils.jsonToBeanList(result, new TypeToken<List<CommentBean>>() {
                }.getType());
                if (ValidateUtils.isValidate(storeBeanList)) {
                    commentAdapter.addAll(storeBeanList);
                }else {
                ToastUtil.showToast(mContext,"没有相关数据");
            }
        }

            @Override
            public void onError(String errorMessage) {
                hideProgress();
            }
        });
    }

    private void getUserLike() {
        showProgress();
        params.clear();
        NetworkManager.INSTANCE.post(Apis.getUserLike, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                hideProgress();
                String result = GsonUtils.getStringFromJSON(response, "datas");
                List<PointedBean> storeBeanList = GsonUtils.jsonToBeanList(result, new TypeToken<List<PointedBean>>() {
                }.getType());
                if (ValidateUtils.isValidate(storeBeanList)) {
                    pointedAdapter.addAll(storeBeanList);
                }
            }

            @Override
            public void onError(String errorMessage) {
                hideProgress();
            }
        });
    }

    private void getUserStore() {
        showProgress();
        params.clear();
        NetworkManager.INSTANCE.post(Apis.getUserStore, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                hideProgress();
                String result = GsonUtils.getStringFromJSON(response, "datas");
                List<TabBean> storeBeanList = GsonUtils.jsonToBeanList(result, new TypeToken<List<TabBean>>() {
                }.getType());
                if (ValidateUtils.isValidate(storeBeanList)) {
                    storeAdapter.addAll(storeBeanList);
                }else {
                    ToastUtil.showToast(mContext,"没有相关数据");
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick(R.id.back_iv)
    public void onViewClicked() {
        this.finish();
    }

    @Override
    public void initListener() {
        super.initListener();
        switch (type) {
            case "STORE":
                storeAdapter.setOnDeleteComment(position -> {
                    showProgress();
                    HashMap<String, String> params = new HashMap<>();
                    params.put("new_id", storeAdapter.getItem(position).getNew_id());
                    NetworkManager.INSTANCE.post(Apis.storeNews, params, new NetworkManager.OnRequestCallBack() {
                        @Override
                        public void onOk(String response) {
                            storeAdapter.remove(position);
                            hideProgress();
                            showToast("删除成功");

                        }

                        @Override
                        public void onError(String errorMessage) {
                            hideProgress();
                            showToast(errorMessage);
                        }
                    });
                });
                storeAdapter.setOnItemClickListener(position -> {
                    Intent min=new Intent(mContext,UniversalDetailActivity.class);
                    min.putExtra("bean",GsonUtils.serializedToJson(storeAdapter.getItem(position)));
                    startActivity(min);
                });
                break;
            case "LIKE":
//                pointedAdapter.setOnDeleteComment(position -> {
//                    HashMap<String, String> params = new HashMap<>();
//                    params.put("r_id", pointedAdapter.getItem(position).getNew_id());
//                    NetworkManager.INSTANCE.post(Apis.deleteComments, params, new NetworkManager.OnRequestCallBack() {
//                        @Override
//                        public void onOk(String response) {
//                            hideProgress();
//                            showToast("删除成功");
//                        }
//
//                        @Override
//                        public void onError(String errorMessage) {
//                            hideProgress();
//                        }
//                    });
//                });
                pointedAdapter.setOnItemClickListener(position -> {
                    Intent min=new Intent(mContext,UniversalDetailActivity.class);
                    min.putExtra("bean",GsonUtils.serializedToJson(pointedAdapter.getItem(position)));
                    startActivity(min);
                });
                break;
            case "COMMENT":
//                showProgress();
                commentAdapter.setOnDeleteComment(position -> {
                    commentAdapter.remove(position);
                    showProgress();
                    HashMap<String, String> params = new HashMap<>();
                    params.put("r_id", commentAdapter.getItem(position).getNew_id());
                    NetworkManager.INSTANCE.post(Apis.deleteComments, params, new NetworkManager.OnRequestCallBack() {
                        @Override
                        public void onOk(String response) {
                            hideProgress();
                            showToast("删除成功");
                        }

                        @Override
                        public void onError(String errorMessage) {
                            hideProgress();
                            showToast(errorMessage);
                        }
                    });
                });
                commentAdapter.setOnItemClickListener(position -> {
                    Intent min=new Intent(mContext,UniversalDetailActivity.class);
                    min.putExtra("bean",GsonUtils.serializedToJson(commentAdapter.getItem(position)));
                    startActivity(min);
                });
                break;
        }

    }
}
