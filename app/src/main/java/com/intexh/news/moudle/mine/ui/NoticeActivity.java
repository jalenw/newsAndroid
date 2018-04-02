package com.intexh.news.moudle.mine.ui;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.SinaRefreshView;
import com.intexh.news.R;
import com.intexh.news.base.AppBaseActivity;
import com.intexh.news.moudle.mine.adapter.UniversalMessageAdapter;
import com.intexh.news.moudle.mine.bean.NoticeBean;
import com.intexh.news.moudle.news.ui.TabFragment;
import com.intexh.news.net.Apis;
import com.intexh.news.widget.LoadMoreBottomView;
import com.zjw.base.net.NetworkManager;
import com.zjw.base.utils.GsonUtils;
import com.zjw.base.utils.ValidateUtils;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NoticeActivity extends AppBaseActivity {


    @BindView(R.id.back_iv)
    ImageView backIv;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    UniversalMessageAdapter adapter;
    int page = 0;
    @BindView(R.id.twinkle)
    TwinklingRefreshLayout twinkle;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_notice;
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
        recyclerView.setAdapter(adapter = new UniversalMessageAdapter(mContext));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setVerticalScrollBarEnabled(false);

    }

    @Override
    public void initData() {
        super.initData();
        getNoticeList();
    }

    private void getNoticeList() {
        HashMap<String, String> params = new HashMap<>();
        params.put("page", page + "");
        NetworkManager.INSTANCE.post(Apis.getNotice, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                String reslut = GsonUtils.getStringFromJSON(response, "datas");
                List<NoticeBean> noticeBeans = GsonUtils.jsonToBeanList(reslut, new TypeToken<List<NoticeBean>>() {
                }.getType());
                if (ValidateUtils.isValidate(noticeBeans)) {
                    if (page == 0) {
                        adapter.clear();
                    }
                    adapter.addAll(noticeBeans);
                    page++;
                }else {
                    showToast("没有更多了");
                    page--;
                }
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }

    @Override
    public void initListener() {
        super.initListener();
        twinkle.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                handler.postDelayed(() -> {
                    page=0;
                    getNoticeList();
                }, 500);
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                handler.postDelayed(() -> {
                    getNoticeList();
                    /*page++;
                    initHomeListData();*/
                }, 500);
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
}
