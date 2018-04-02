package com.intexh.news.moudle.news.ui;


import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizeBag;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.flyco.tablayout.SlidingTabLayout;
import com.google.gson.reflect.TypeToken;
import com.intexh.news.R;
import com.intexh.news.helper.LoginHelper;
import com.intexh.news.helper.UserHelper;
import com.intexh.news.moudle.mine.bean.LoginBean;
import com.intexh.news.moudle.mine.event.LoginEvent;
import com.intexh.news.moudle.mine.event.LoginOutEvent;
import com.intexh.news.moudle.news.bean.ChannelBean;
import com.intexh.news.moudle.news.bean.TabBean;
import com.intexh.news.moudle.news.event.ChangeChannelEvent;
import com.intexh.news.moudle.news.event.NewNewsEvent;
import com.intexh.news.moudle.news.event.NewNumberEvent;
import com.intexh.news.net.Apis;
import com.intexh.news.utils.ToastUtil;
import com.intexh.news.utils.VoiceUtils;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.umeng.analytics.MobclickAgent;
import com.zjw.base.UI.BaseFragment;
import com.zjw.base.glide.GlideHelper;
import com.zjw.base.net.NetworkManager;
import com.zjw.base.utils.GsonUtils;
import com.zjw.base.utils.ValidateUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Field;
import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by AndroidIntexh1 on 2017/11/1.
 */

public class NewsFragment extends BaseFragment {
    @BindView(R.id.search_iv)
    ImageView searchIv;
    @BindView(R.id.add_iv)
    ImageView addIv;
    Unbinder unbinder;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    SpeechSynthesizer mSpeechSynthesizer;
    boolean isPlay = false;
    boolean isFirst = true;
    FragmentPagerAdapter fragmentPagerAdapter;
    List<Fragment> tabFragments;
    @BindView(R.id.play_iv)
    ImageView playIv;
    @BindView(R.id.power_iv)
    ImageView powerIv;
    @BindView(R.id.tag_ll)
    LinearLayout tagLl;
    List<SpeechSynthesizeBag> bags;
    int currentNewsID = 0;
    List<TabBean> readedNews = new ArrayList<>();//已听过的新闻
    List<TabBean> newsList;//源新闻列表
    @BindView(R.id.bar_rll)
    RelativeLayout barRll;
    @BindView(R.id.sliding_tb)
    SlidingTabLayout slidingTb;
    List<ChannelBean> channelBeanList;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_news;
    }

    @Override
    protected void initView() {
        super.initView();
        VoiceUtils utils = new VoiceUtils();
        utils.init(getContext(), 0);
        mSpeechSynthesizer = utils.getSyntheszer();
        EventBus.getDefault().register(this);
        getChannel();
    }

    @Subscribe
    public void onEvent(LoginEvent event) {
        getChannel();
    }

    @Subscribe
    public void onEvent(LoginOutEvent event) {
        getChannel();
    }

    @Subscribe
    public void onEvent(ChangeChannelEvent event) {
        getChannel();
    }

    @Subscribe
    public void onEvent(NewNewsEvent event) {
        if (event.getNumber() == 0) {
            for (int i = 0; i < channelBeanList.size(); i++) {
                if (channelBeanList.get(i).getChannel_id().equals(event.getId())) {
                    slidingTb.hideMsg(i);
                }
            }
        } else {
            for (int i = 0; i < channelBeanList.size(); i++) {
                if (channelBeanList.get(i).getChannel_id().equals(event.getId())) {
                    slidingTb.showDot(i);
                }
            }
        }
    }


    private void getChannel() {
        channelBeanList = (List<ChannelBean>)ChannelBean.readObject(getActivityContext(),"kChannelBeanList");
        if (channelBeanList==null||channelBeanList.size()==0) {
            HashMap<String, String> params = new HashMap<>();
            NetworkManager.INSTANCE.post(Apis.getChannel, params, new NetworkManager.OnRequestCallBack() {
                @Override
                public void onOk(String response) {
                    String result = GsonUtils.getStringFromJSON(response, "datas", "like");
                    channelBeanList = GsonUtils.jsonToBeanList(result, new TypeToken<List<ChannelBean>>() {
                    }.getType());
                    if (ValidateUtils.isValidate(channelBeanList)) {
                        //List<ChannelBean> wholeBeans = new ArrayList<>();
                        ChannelBean recomend = new ChannelBean();
                        recomend.setChannel_id("0");
                        recomend.setChannel_content("推荐");
                        channelBeanList.add(0, recomend);
//                    wholeBeans.add(recomend);
                        //  wholeBeans.addAll(channelBeanList);
                        initViewpager(channelBeanList);
                    }
                    ChannelBean.saveObject(getActivityContext(),"kChannelBeanList",channelBeanList);
                }

                @Override
                public void onError(String errorMessage) {

                }
            });
        }
        else
        {
            HashMap<String, String> params = new HashMap<>();
            NetworkManager.INSTANCE.post(Apis.getChannel, params, new NetworkManager.OnRequestCallBack() {
                @Override
                public void onOk(String response) {
                    String result = GsonUtils.getStringFromJSON(response, "datas", "like");
                    List<ChannelBean> tempList = GsonUtils.jsonToBeanList(result, new TypeToken<List<ChannelBean>>() {
                    }.getType());
                    if (ValidateUtils.isValidate(tempList)) {
                        ChannelBean recomend = new ChannelBean();
                        recomend.setChannel_id("0");
                        recomend.setChannel_content("推荐");
                        tempList.add(0, recomend);
                        if (tempList.size()!=channelBeanList.size())
                        {
                            channelBeanList = tempList;
                        }
                        initViewpager(channelBeanList);
                    }
                    ChannelBean.saveObject(getActivityContext(),"kChannelBeanList",channelBeanList);
                }

                @Override
                public void onError(String errorMessage) {
                }
            });
        }
    }

    private void initViewpager(List<ChannelBean> channelBeanList) {
        tabFragments = new ArrayList<>();
        for (int i = 0; i < channelBeanList.size(); i++) {
            Fragment fragment = TabFragment.instance(channelBeanList.get(i).getChannel_id(), i);
            tabFragments.add(fragment);
        }
        fragmentPagerAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return tabFragments.get(position);
            }

            @Override
            public int getCount() {
                return channelBeanList.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return channelBeanList.get(position).getChannel_content();
            }
        };
        viewpager.setAdapter(fragmentPagerAdapter);
        slidingTb.setViewPager(viewpager);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
    }

    @Override
    protected void initListener() {
        super.initListener();
        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentNewsID = 0;
                mSpeechSynthesizer.stop();
                isFirst = true;
                isPlay=false;
                powerIv.setVisibility(View.GONE);
                playIv.setVisibility(View.GONE);
                tagLl.setVisibility(View.VISIBLE);
                MobclickAgent.onEvent(mContext, "channel", "" + channelBeanList.get(position).getChannel_content());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
//                mSpeechSynthesizer.stop();
//                isFirst = true;
//                powerIv.setVisibility(View.GONE);
//                playIv.setVisibility(View.GONE);
//                tagLl.setVisibility(View.VISIBLE);
            }
        });
    }

    @OnClick({R.id.search_iv, R.id.add_iv, R.id.play_iv, R.id.power_iv, R.id.tag_ll})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.search_iv:
                if (!ValidateUtils.isValidate(UserHelper.getCurrentToken())) {
                    ToastUtil.showToast(mContext, "请先登录");
                    LoginHelper helper = new LoginHelper(mContext);
                    helper.showDialog();
                    return;
                }
                startActivity(SearchActivity.class);
                break;
            case R.id.add_iv:
                if (!ValidateUtils.isValidate(UserHelper.getCurrentToken())) {
                    ToastUtil.showToast(mContext, "请先登录");
                    LoginHelper helper = new LoginHelper(mContext);
                    helper.showDialog();
                    return;
                }
                startActivity(ChannelActivity.class);
                break;
            case R.id.play_iv:
                startNews();
                break;
            case R.id.power_iv:
                mSpeechSynthesizer.stop();
                isFirst = true;
                isPlay = false;
                powerIv.setVisibility(View.GONE);
                playIv.setVisibility(View.GONE);
                GlideHelper.INSTANCE.loadImage(playIv, R.mipmap.news_play);
                tagLl.setVisibility(View.VISIBLE);
                break;
            case R.id.tag_ll:
//                if (!ValidateUtils.isValidate(((TabFragment) tabFragments.get(viewpager.getCurrentItem())).getNewsList())) {
//                    ToastUtil.showToast(mContext, "新闻都听过啦");
//                    return;
//                }
                newsList = ((TabFragment) tabFragments.get(viewpager.getCurrentItem())).getNewsList();
                tagLl.setVisibility(View.GONE);
                GlideHelper.INSTANCE.loadImage(playIv, R.mipmap.news_play);
                powerIv.setVisibility(View.VISIBLE);
                playIv.setVisibility(View.VISIBLE);
                mSpeechSynthesizer.setSpeechSynthesizerListener(new SpeechSynthesizerListener() {
                    @Override
                    public void onSynthesizeStart(String s) {
                    }

                    @Override
                    public void onSynthesizeDataArrived(String s, byte[] bytes, int i) {
                    }

                    @Override
                    public void onSynthesizeFinish(String s) {
                    }

                    @Override
                    public void onSpeechStart(String s) {

                    }

                    @Override
                    public void onSpeechProgressChanged(String s, int i) {

                    }

                    @Override
                    public void onSpeechFinish(String s) {
//                        readedNews.add(newsList.get(currentNewsID));
//                        UserHelper.saverReadedNEws(GsonUtils.serializedToJson(readedNews));//保存已读新闻
                        currentNewsID++;
                        findNews2Read(newsList, currentNewsID);
                    }

                    @Override
                    public void onError(String s, SpeechError speechError) {

                    }
                });
                startNews();
                break;

        }
    }
    void startNews(){
        MobclickAgent.onEvent(mContext, "listenNews");
        mSpeechSynthesizer.speak(",");
        if (isPlay) {
            isPlay = false;
            mSpeechSynthesizer.pause();
            GlideHelper.INSTANCE.loadImage(playIv, R.mipmap.news_play);
        } else {
            currentNewsID = 0;
//            if (isFirst) {
                findNews2Read(newsList, currentNewsID);
//                isFirst = false;
//            } else {
//                mSpeechSynthesizer.resume();
//            }
            isPlay = true;
            GlideHelper.INSTANCE.loadImage(playIv, R.mipmap.news_pause);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSpeechSynthesizer.release();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    boolean newsIsReaded(TabBean bean) {
        if (ValidateUtils.isValidate(UserHelper.getReadedNews())) {
            List<TabBean> readBeans = GsonUtils.deSerializedFromJson(UserHelper.getReadedNews(), new TypeToken<List<TabBean>>() {
            }.getType());
            for (TabBean temp : readBeans
                    ) {
                if (temp.getNew_id().equals(bean.getNew_id())) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    void findNews2Read(List<TabBean> newslIsts, int position) {
        for (int i = position; i < newslIsts.size(); i++) {
//            if (!newsIsReaded(newslIsts.get(i))) {
                mSpeechSynthesizer.speak(newslIsts.get(i).getNew_title() + "," + newslIsts.get(i).getNew_content());
//                return;
//            }
        }
    }
}
