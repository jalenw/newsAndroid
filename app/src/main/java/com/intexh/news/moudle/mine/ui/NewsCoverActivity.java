package com.intexh.news.moudle.mine.ui;


import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.SinaRefreshView;
import com.intexh.news.R;
import com.intexh.news.base.AppBaseActivity;
import com.intexh.news.moudle.mine.adapter.CoverAdapter;
import com.intexh.news.moudle.mine.bean.CoverBean;
import com.intexh.news.moudle.news.ui.TabFragment;
import com.intexh.news.net.Apis;
import com.intexh.news.widget.LoadMoreBottomView;
import com.zjw.base.net.NetworkManager;
import com.zjw.base.utils.GsonUtils;
import com.zjw.base.utils.ValidateUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewsCoverActivity extends AppBaseActivity {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    CoverAdapter coverAdapter;
    int page = 1;
    @BindView(R.id.twinkle)
    TwinklingRefreshLayout twinkle;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_news_cover;
    }

    @Override
    public void initView() {
        super.initView();
        SinaRefreshView header = new SinaRefreshView(mContext);
        header.setArrowResource(R.mipmap.refresh_arrow);
        TextView refreshTextView = header.findViewById(com.lcodecore.tkrefreshlayout.R.id.tv);
        LoadMoreBottomView bottomView = new LoadMoreBottomView(mContext);
        refreshTextView.setTextSize(14);
        twinkle.setHeaderView(header);
        twinkle.setBottomView(bottomView);
        twinkle.startRefresh();
        coverAdapter = new CoverAdapter(mContext);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        recyclerView.setAdapter(coverAdapter);
    }

    @Override
    public void initData() {
        super.initData();
       // getCoverList();
    }

    private void getCoverList() {
        params.clear();
        params.put("page", page + "");
        NetworkManager.INSTANCE.post(Apis.getCover, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                twinkle.finishLoadmore();
                twinkle.finishRefreshing();
                String result = GsonUtils.getStringFromJSON(response, "datas");
                List<CoverBean> coverBeans = GsonUtils.jsonToBeanList(result, new TypeToken<List<CoverBean>>() {
                }.getType());
                if (ValidateUtils.isValidate(coverBeans)) {
                    coverAdapter.clear();
                    coverAdapter.addAll(coverBeans);
                } else {
                    page--;
                    showToast("没有更多了");
                }
            }

            @Override
            public void onError(String errorMessage) {
                twinkle.finishLoadmore();
                twinkle.finishRefreshing();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    public void initListener() {
        super.initListener();
        twinkle.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                handler.postDelayed(() -> {
                    page = 1;
                    getCoverList();
                }, 500);
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                handler.postDelayed(() -> {
                    page++;
                    if (page >= 4) {
                        showToast("没有更多了");
                        twinkle.finishLoadmore();
                        return;
                    }
                    getCoverList();

                }, 500);
            }
        });
    }

    @OnClick(R.id.back_iv)
    public void onViewClicked() {
        this.finish();
    }
}
