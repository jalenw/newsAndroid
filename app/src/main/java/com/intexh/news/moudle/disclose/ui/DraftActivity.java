package com.intexh.news.moudle.disclose.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.gson.reflect.TypeToken;
import com.intexh.news.R;
import com.intexh.news.base.AppBaseActivity;
import com.intexh.news.moudle.disclose.adapter.DraftAdapter;
import com.intexh.news.moudle.disclose.bean.DraftBean;
import com.intexh.news.net.Apis;
import com.zjw.base.net.NetworkManager;
import com.zjw.base.utils.GsonUtils;
import com.zjw.base.utils.ValidateUtils;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DraftActivity extends AppBaseActivity {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    DraftAdapter adapter;
    Intent draftFragIntent;
    String currentTYpe;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_draft;
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
        draftFragIntent=getIntent();
        currentTYpe=draftFragIntent.getStringExtra("TYPE");
        recyclerView.setAdapter(adapter = new DraftAdapter(mContext));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setVerticalScrollBarEnabled(false);
    }

    @Override
    public void initData() {
        super.initData();
        HashMap<String, String> params = new HashMap<>();
        params.put("type", currentTYpe);
        showProgress();
        NetworkManager.INSTANCE.post(Apis.getDraft, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                hideProgress();
                String result = GsonUtils.getStringFromJSON(response, "datas");
                List<DraftBean> draftBeanList = GsonUtils.jsonToBeanList(result, new TypeToken<List<DraftBean>>() {
                }.getType());
                if (ValidateUtils.isValidate(draftBeanList)) {
                    adapter.addAll(draftBeanList);
                }else {
                    showToast("还没有内容");
                }
            }

            @Override
            public void onError(String errorMessage) {
                hideProgress();
            }
        });

    }

    @Override
    public void initListener() {
        super.initListener();
        adapter.setOnDeleteItem(position -> {
            showProgress();
            HashMap<String, String> params = new HashMap<>();
            params.put("id", adapter.getItem(position).getNew_id());
            params.put("type", currentTYpe);
            NetworkManager.INSTANCE.post(Apis.deleteDraft, params, new NetworkManager.OnRequestCallBack() {
                @Override
                public void onOk(String response) {
                    hideProgress();
                    adapter.remove(position);
                }

                @Override
                public void onError(String errorMessage) {
                    hideProgress();
                    showToast(errorMessage);
                }
            });
        });
        adapter.setOnItemClickListener(position -> {
            draftFragIntent.putExtra("draftBean",GsonUtils.serializedToJson(adapter.getItem(position)));
            setResult(DisCloseFragment.mDradtCode,draftFragIntent);
            this.finish();
            // Intent min=new Intent(mContext,)
        });
    }

    @OnClick(R.id.back_iv)
    public void onViewClicked() {
        setResult(002,draftFragIntent);
        this.finish();
    }
}
