package com.intexh.news.moudle.mine.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.intexh.news.R;
import com.intexh.news.base.AppBaseActivity;
import com.intexh.news.moudle.mine.bean.LoginBean;
import com.intexh.news.moudle.news.adapter.NewsTabAdapter;
import com.intexh.news.moudle.news.bean.TabBean;
import com.zjw.base.utils.GsonUtils;
import com.zjw.base.utils.Settings;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UniversalDetailActivity extends AppBaseActivity {


    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    NewsTabAdapter adapter;
    TabBean tabBean;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_universal_detail;
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
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(adapter = new NewsTabAdapter(mContext));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setVerticalScrollBarEnabled(false);
    }

    @Override
    public void initData() {
        super.initData();
        tabBean = GsonUtils.deSerializedFromJson(getIntent().getStringExtra("bean"), TabBean.class);
        adapter.add(tabBean);
    }

    @OnClick(R.id.back_iv)
    public void onViewClicked() {
        this.finish();
    }
}
