package com.intexh.news.moudle.news.ui;


import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.intexh.news.R;
import com.intexh.news.base.AppBaseActivity;
import com.intexh.news.helper.LoginHelper;
import com.intexh.news.helper.UserHelper;
import com.intexh.news.moudle.news.adapter.NewsTabAdapter;
import com.intexh.news.moudle.news.adapter.SearchAdapter;
import com.intexh.news.moudle.news.bean.TabBean;
import com.intexh.news.net.Apis;
import com.intexh.news.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;
import com.zjw.base.net.NetworkManager;
import com.zjw.base.utils.GsonUtils;
import com.zjw.base.utils.ValidateUtils;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends AppBaseActivity {


    @BindView(R.id.back_iv)
    ImageView backIv;
    @BindView(R.id.search_edt)
    EditText searchEdt;
    @BindView(R.id.search_tv)
    TextView searchTv;
    @BindView(R.id.hot_recycler)
    RecyclerView hotRecycler;
    @BindView(R.id.history_recycle)
    RecyclerView historyRecycle;
    SearchAdapter historyAdapter;
    SearchAdapter hotAdapter;
    @BindView(R.id.news_recycle)
    RecyclerView newsRecycle;
    NewsTabAdapter adapter;
    View tagLL;
    @BindView(R.id.deleteall_tv)
    TextView deleteallTv;
    @BindView(R.id.null_tv)
    TextView nullTv;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
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
        tagLL = findViewById(R.id.tag_ll);
        hotRecycler.setLayoutManager(new GridLayoutManager(mContext, 2));
        hotRecycler.setAdapter(hotAdapter = new SearchAdapter(mContext, false));
        historyRecycle.setLayoutManager(new GridLayoutManager(mContext, 2));
        historyRecycle.setAdapter(historyAdapter = new SearchAdapter(mContext, true));

        newsRecycle.setLayoutManager(new LinearLayoutManager(mContext));
        newsRecycle.setAdapter(adapter = new NewsTabAdapter(mContext));
        // hotRecycler.setAdapter();
        getHotSearch();
        getSearchHistory();
        searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    @Override
    public void initData() {
        super.initData();
    }

    @OnClick({R.id.back_iv, R.id.search_tv, R.id.deleteall_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                this.finish();
                break;
            case R.id.search_tv:
                if (ValidateUtils.isValidate(searchEdt.getText().toString().trim())) {
                    tagLL.setVisibility(View.GONE);
                    newsRecycle.setVisibility(View.VISIBLE);
                    if (searchEdt.getText().toString().trim().length() <= 20) {
                        MobclickAgent.onEvent(mContext,"searchNews");
                        doSearch(searchEdt.getText().toString().trim());
                    } else {
                        showToast("搜索不能超过20个字");
                    }

                } else {
                    showToast("搜索不能为空");
                }
                break;
            case R.id.deleteall_tv:
                doDeleteHistory("ALL0x001");
                historyAdapter.removeAll();
                break;
        }
    }

    private void doSearch(String trim) {
        showProgress();
        HashMap<String, String> params = new HashMap<>();
        params.put("search", trim);
        NetworkManager.INSTANCE.post(Apis.getSearch, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                hideProgress();
                String result = GsonUtils.getStringFromJSON(response, "datas", "newlist");
                List<TabBean> newsList = GsonUtils.jsonToBeanList(result, new TypeToken<List<TabBean>>() {
                }.getType());
                if (ValidateUtils.isValidate(newsList)) {
                    nullTv.setVisibility(View.GONE);
                    adapter.clear();
                    adapter.addAll(newsList);
                } else {
                    nullTv.setVisibility(View.VISIBLE);
                    showToast("没有相应结果");
                }
            }

            @Override
            public void onError(String errorMessage) {
                hideProgress();
                showToast(errorMessage);
            }
        });
    }

    private void getSearchHistory() {
        showProgress("加载中");
        HashMap<String, String> params = new HashMap<>();
        NetworkManager.INSTANCE.post(Apis.getSearchHistory, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                hideProgress();
                String result = GsonUtils.getStringFromJSON(response, "datas");
                List<String> histortList = GsonUtils.jsonToBeanList(result, new TypeToken<List<String>>() {
                }.getType());
                if (ValidateUtils.isValidate(histortList)) {
                    if (histortList.get(0).equals("")){
                        return;
                    }
                    historyAdapter.addAll(histortList);
                }
            }

            @Override
            public void onError(String errorMessage) {
                hideProgress();
            }
        });
    }

    private void getHotSearch() {
        HashMap<String, String> params = new HashMap<>();
        NetworkManager.INSTANCE.post(Apis.getHotSearch, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                String result = GsonUtils.getStringFromJSON(response, "datas");
                List<String> hotList = GsonUtils.jsonToBeanList(result, new TypeToken<List<String>>() {
                }.getType());
                if (ValidateUtils.isValidate(hotList)) {
                    hotAdapter.addAll(hotList);
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
        hotAdapter.setOnItemClickListener(position -> {
            tagLL.setVisibility(View.GONE);
            newsRecycle.setVisibility(View.VISIBLE);
            searchEdt.setText(hotAdapter.getItem(position));
            doSearch(hotAdapter.getItem(position));

        });
        historyAdapter.setOnItemClickListener(position -> {
            tagLL.setVisibility(View.GONE);
            newsRecycle.setVisibility(View.VISIBLE);
            searchEdt.setText(historyAdapter.getItem(position));
            doSearch(historyAdapter.getItem(position));

        });
        historyAdapter.setOndeleteItem(position -> {
            doDeleteHistory(historyAdapter.getItem(position));
            historyAdapter.remove(position);

        });
    }

    private void doDeleteHistory(String deleteWord) {
        HashMap<String, String> params = new HashMap<>();
        if (deleteWord.equals("ALL0x001")) {
            params.put("type", "deleteAll");
        } else {
            params.put("type", "deleteWord");
            params.put("deleteWord", deleteWord);
        }
        NetworkManager.INSTANCE.post(Apis.getDeleteSearch, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {

            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }


}
