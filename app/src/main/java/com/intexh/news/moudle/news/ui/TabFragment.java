package com.intexh.news.moudle.news.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.SinaRefreshView;
import com.intexh.news.R;
import com.intexh.news.base.BaseDecorationFragment;
import com.intexh.news.helper.UserHelper;
import com.intexh.news.moudle.news.adapter.NewsTabAdapter;
import com.intexh.news.moudle.news.bean.AdsBean;
import com.intexh.news.moudle.news.bean.TabBean;
import com.intexh.news.moudle.news.event.NewNewsEvent;
import com.intexh.news.net.Apis;
import com.intexh.news.utils.DataUtil;
import com.intexh.news.utils.ToastUtil;
import com.intexh.news.widget.LoadMoreBottomView;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.zjw.base.UI.BaseFragment;
import com.zjw.base.net.NetworkManager;
import com.zjw.base.utils.GsonUtils;
import com.zjw.base.utils.ValidateUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.Unbinder;

/**
 * Created by AndroidIntexh1 on 2017/11/8.
 */

public class TabFragment extends BaseDecorationFragment {
    TwinklingRefreshLayout twinkle;
    NewsTabAdapter adapter;
    List<AdsBean> adsBeanList;
    List<AdsBean> textAdsList;
    List<AdsBean> picAdsList;
    int adPage = 0;
    int adIndex = 0;
    int adListIndex = 0;
    int adN = 0;
    int adTextIndex = 0;

    public List<TabBean> getTabBeans4new() {
        return tabBeans4new;
    }

    public void setTabBeans4new(List<TabBean> tabBeans4new) {
        this.tabBeans4new = tabBeans4new;
    }

    List<TabBean> tabBeans4new;//用来检测更新
    int page = 0;//是否为最新页
    String currentCityName = "";
    List<TabBean> tempNews = new ArrayList<>();
    List<TabBean> newsList;


    public List<TabBean> getNewsList() {
        String todayUnix = DataUtil.getStrTime2(System.currentTimeMillis() / 1000 + "");
        Log.e("wilson", "今天的时间" + todayUnix);
        long todayLong = DataUtil.getTimeStamp(todayUnix, "yyyy-MM-dd");

        if (ValidateUtils.isValidate(newsList)) {
            List<TabBean> todayBeans = new ArrayList<>();
            for (TabBean beans : newsList
                    ) {
                String day = DataUtil.getStrTime2(beans.getSorttime());
                long timeUnix = DataUtil.getTimeStamp(day, "yyyy-MM-dd");
//                if (timeUnix == todayLong) {
                    todayBeans.add(beans);
//                }
            }
            return todayBeans;
        } else {
            return null;
        }
    }

    public void setNewsList(List<TabBean> newsList) {
        this.newsList = newsList;
    }


    private StickyRecyclerHeadersDecoration headersDecor;

    public static Fragment instance(String position, int currentPostion) {
        TabFragment fragment = new TabFragment();
        Bundle args = new Bundle();
        args.putString("position", position);
        args.putInt("currentPosition", currentPostion);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected void initView() {
        super.initView();
        textAdsList = new ArrayList<>();
        picAdsList = new ArrayList<>();
        Log.e("wilson", "页面重新加载");
        if (ValidateUtils.isValidate(UserHelper.getCity())) {
            currentCityName = UserHelper.getCity().getArea_name();
        }
        twinkle = getTwinkle();
        SinaRefreshView header = new SinaRefreshView(getContext());
        header.setArrowResource(R.mipmap.refresh_arrow);
        TextView refreshTextView = header.findViewById(com.lcodecore.tkrefreshlayout.R.id.tv);
        LoadMoreBottomView bottomView = new LoadMoreBottomView(mContext);
        refreshTextView.setTextSize(14);
        twinkle.setHeaderView(header);
        twinkle.setBottomView(bottomView);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!ValidateUtils.isValidate(tabBeans4new)) {
                    return;
                }
                HashMap<String, String> params = new HashMap<>();
                Map<String, String> keyValue = new HashMap<>();
                String json;
                if (tabBeans4new.size() == 1) {
                    keyValue.put(TabFragment.this.getArguments().getString("position"), tabBeans4new.get(0).getSorttime());
                    json = GsonUtils.serializedToJson(keyValue);
                } else {
                    if (Long.parseLong(tabBeans4new.get(0).getSorttime()) > Long.parseLong(tabBeans4new.get(1).getSorttime())) {
                        keyValue.put(TabFragment.this.getArguments().getString("position"), Long.parseLong(tabBeans4new.get(0).getSorttime()) + "");
                    } else {
                        keyValue.put(TabFragment.this.getArguments().getString("position"), Long.parseLong(tabBeans4new.get(1).getSorttime()) + "");
                    }
                    json = GsonUtils.serializedToJson(keyValue);
                }
                //请求是否有新的新闻
                params.put("channel_string", json);
                NetworkManager.INSTANCE.post(Apis.hasNewAdd, params, new NetworkManager.OnRequestCallBack() {
                    @Override
                    public void onOk(String response) {
                        int hasNewNumber;
                        String result = GsonUtils.getStringFromJSON(response, "datas");
                        if (Integer.parseInt(TabFragment.this.getArguments().getString("position")) != 0) {
                            hasNewNumber = Integer.parseInt(GsonUtils.getStringFromJSON(result, TabFragment.this.getArguments().getString("position")));
                            NewNewsEvent newsEvent = new NewNewsEvent();
                            newsEvent.setId(TabFragment.this.getArguments().getString("position"));
                            newsEvent.setNumber(hasNewNumber);
                            EventBus.getDefault().post(newsEvent);
                        }
                        if (Integer.parseInt(TabFragment.this.getArguments().getString("position")) == 0) {
                            Log.e("wilson", "post事件");
                            List<Integer> numbers = GsonUtils.jsonToBeanList(result, new TypeToken<List<Integer>>() {
                            }.getType());
                            NewNewsEvent newsEvent = new NewNewsEvent();
                            newsEvent.setId(TabFragment.this.getArguments().getString("position"));
                            newsEvent.setNumber(numbers.get(0));
                            EventBus.getDefault().post(newsEvent);
                            ;
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {

                    }
                });
            }
        }, 2000, 60000);//五分钟更新一次 是否有新新闻
    }

    @Override
    protected void initData() {
        super.initData();
//        List<TabBean> savedBeans = UserHelper.getNewsBeans(TabFragment.this.getArguments().getString("position") + "");
//        if (ValidateUtils.isValidate(savedBeans)) {
//            adapter.clear();
//            adapter.addAll(savedBeans);
//            setNewsList(savedBeans);//添加到set
//            setTabBeans4new(savedBeans);
//            Log.e("wilson", "设置newsLIst");
//        } else {
            getAds("0", adPage);//请求广告
//        }
    }


    private void getAds(String type, int p) {
        HashMap<String, String> params = new HashMap<>();
        params.put("type", type);//广告类型1图片广告，2文字广告，3视频广告，4音频广告’
        params.put("areaname", currentCityName);
        params.put("channelid", TabFragment.this.getArguments().getString("position"));
        params.put("page", String.valueOf(p));
        NetworkManager.INSTANCE.post(Apis.getAdsList, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                String result = GsonUtils.getStringFromJSON(response, "datas");
                adsBeanList = GsonUtils.jsonToBeanList(result, new TypeToken<List<AdsBean>>() {
                }.getType());
                for (AdsBean bean : adsBeanList)
                {
                    if (bean!=null) {
                        if (bean.getInfo_type().equals("1")) {
                            picAdsList.add(bean);
                        }
                        if (bean.getInfo_type().equals("2")) {
                            textAdsList.add(bean);
                        }
                    }
                }
                if (adsBeanList.size()==10)
                {
                    adPage++;
                    getAds("0",adPage);
                }
                else
                {
                    getNewsList(TabFragment.this.getArguments().getString("position"), "");
                }
            }

            @Override
            public void onError(String errorMessage) {
                hideProgress();
                getNewsList(TabFragment.this.getArguments().getString("position"), "");
            }
        });
    }

    private void getNewsList(String channelID, String time) {
        HashMap<String, String> params = new HashMap<>();
        params.put("channel_id", channelID);
        params.put("time", ValidateUtils.isValidate(time) ? time : "");
        NetworkManager.INSTANCE.post(Apis.getNewsList, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {

                hideProgress();
                if (twinkle != null) {
                    twinkle.finishLoadmore();
                    twinkle.finishRefreshing();
                }
                String result = GsonUtils.getStringFromJSON(response, "datas", "news");
                List<TabBean> news = GsonUtils.jsonToBeanList(result, new TypeToken<List<TabBean>>() {
                }.getType());
                if (page == 0) {
                    setTabBeans4new(news);
                }
                page++;
                if (ValidateUtils.isValidate(news)) {
                    if (time.equals("")) {//刷新操作
                        adapter.clear();
                        NewNewsEvent newsEvent = new NewNewsEvent();
                        newsEvent.setId(TabFragment.this.getArguments().getString("position"));
                        newsEvent.setNumber(0);
                        EventBus.getDefault().post(newsEvent);
                    }
                    if (time.equals("")) {
                        tempNews.clear();
                    }
                    tempNews.addAll(news);
                    adapter.clear();
                    //首先要确保广告存在
                    if (ValidateUtils.isValidate(textAdsList)) {//添加文字广告
                            for (TabBean newsBean : news) {
                                AdsBean beanText = textAdsList.get(adTextIndex);
                                newsBean.setAdText(ValidateUtils.isValidate(beanText.getInfo_content()) ? beanText.getInfo_content() : "");
                                newsBean.setNew_tigs(ValidateUtils.isValidate(beanText.getInfo_title()) ? beanText.getInfo_title() : "");
                                newsBean.setAdUrl(ValidateUtils.isValidate(beanText.getInfo_url()) ? beanText.getInfo_url() : "");
                                adTextIndex++;
                                if (adTextIndex == textAdsList.size()) {
                                    adTextIndex = 0;
                                }
                            }
                    }
                    if (ValidateUtils.isValidate(picAdsList)) {
                            if (adN==0&&tempNews.size()>=5) {
                                AdsBean beanPic = picAdsList.get(adListIndex);
                                TabBean tabBean = new TabBean();
                                tabBean.setNew_pic(beanPic.getInfo_cover());
                                tabBean.setNew_title("...../");//广告
                                tabBean.setNew_tigs(beanPic.getInfo_title());
                                tabBean.setNew_content(beanPic.getInfo_content());
                                tabBean.setNew_id(beanPic.getInfo_id());
                                tabBean.setAdUrl(beanPic.getInfo_url());
                                tabBean.setSorttime(tempNews.get(4).getSorttime());
                                tempNews.add(5, tabBean);
                                adListIndex++;
                                adN++;
                                if (adListIndex==picAdsList.size())
                                {
                                    adListIndex=0;
                                }
                            }
                            adIndex = ((adN+1)*21-26);
                            while (adIndex<=tempNews.size())
                            {
                                AdsBean beanPic = picAdsList.get(adListIndex);
                                TabBean tabBean = new TabBean();
                                tabBean.setNew_pic(beanPic.getInfo_cover());
                                tabBean.setNew_title("...../");//广告
                                tabBean.setNew_tigs(beanPic.getInfo_title());
                                tabBean.setNew_content(beanPic.getInfo_content());
                                tabBean.setNew_id(beanPic.getInfo_id());
                                tabBean.setAdUrl(beanPic.getInfo_url());
                                tabBean.setSorttime(tempNews.get(((adN+1)*21-27)).getSorttime());
                                tempNews.add(((adN+1)*21-26), tabBean);
                                adN++;
                                adIndex = ((adN+1)*21-26);
                                adListIndex++;
                                if (adListIndex==picAdsList.size())
                                {
                                    adListIndex=0;
                                }
                            }
                    }
//                    UserHelper.saveNewsBeans(tempNews, TabFragment.this.getArguments().getString("position") + "");
                    adapter.addAll(tempNews);
                    setNewsList(adapter.getAllData());//添加到set
                } else {
                    ToastUtil.showToast(mContext, "没有更多了");
                }
            }

            @Override
            public void onError(String errorMessage) {
                hideProgress();
                if (twinkle != null) {
                    twinkle.finishLoadmore();
                    twinkle.finishRefreshing();
                }
            }
        });
    }


    @Override
    protected void initListener() {
        super.initListener();
        twinkle.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                handler.postDelayed(() -> {
                    picAdsList.clear();
                    textAdsList.clear();
                    adPage = 0;
                    adIndex = 0;
                    adListIndex = 0;
                    adN = 0;
                    adTextIndex = 0;
                    getAds("0", adPage);//请求图片广告
                    page = 0;
                }, 500);
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                handler.postDelayed(() -> {
                    String lastedTime = adapter.getItem(adapter.getAllData().size() - 1).getSorttime();
                    getNewsList(TabFragment.this.getArguments().getString("position"), lastedTime);
                }, 500);
            }
        });
    }

    @Override
    protected void setAdapterAndDecor(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(adapter = new NewsTabAdapter(mContext));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setVerticalScrollBarEnabled(false);
        headersDecor = new StickyRecyclerHeadersDecoration(adapter);
        recyclerView.addItemDecoration(headersDecor);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
