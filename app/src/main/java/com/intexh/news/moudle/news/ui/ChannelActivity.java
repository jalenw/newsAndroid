package com.intexh.news.moudle.news.ui;


import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.intexh.news.R;
import com.intexh.news.base.AppBaseActivity;
import com.intexh.news.helper.LoginHelper;
import com.intexh.news.helper.UserHelper;
import com.intexh.news.moudle.news.adapter.ChannelListAdapter;
import com.intexh.news.moudle.news.bean.ChannelBean;
import com.intexh.news.moudle.news.event.ChangeChannelEvent;
import com.intexh.news.net.Apis;
import com.intexh.news.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;
import com.zjw.base.net.NetworkManager;
import com.zjw.base.utils.GsonUtils;
import com.zjw.base.utils.ValidateUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChannelActivity extends AppBaseActivity {


    @BindView(R.id.search_iv)
    ImageView searchIv;
    @BindView(R.id.edit_tv)
    TextView editTv;
    @BindView(R.id.all_recycle)
    RecyclerView allRecycle;
    ChannelListAdapter channelListAdapter;
    ChannelListAdapter disLikeAdapter;
    @BindView(R.id.current_recycle)
    SwipeMenuRecyclerView currentRecycle;
    List<ChannelBean> likeChannelList;
    List<ChannelBean> disLikeChannelList;
    boolean isEditing = false;
    @BindView(R.id.cover_current_tv)
    TextView coverCurrentTv;//覆盖TV
    @BindView(R.id.cover_all_tv)
    TextView coverAllTv;//覆盖TV

    @Override
    protected int getLayoutId() {
        return R.layout.activity_channel;
    }

    @Override
    public void initView() {
        super.initView();
        likeChannelList = new ArrayList<>();
        currentRecycle.setAdapter(channelListAdapter = new ChannelListAdapter(mContext));
        currentRecycle.setLayoutManager(new GridLayoutManager(mContext, 4));
        currentRecycle.setVerticalScrollBarEnabled(false);

        OnItemMoveListener mItemMoveListener = new OnItemMoveListener() {
            @Override
            public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
                int fromPosition = srcHolder.getAdapterPosition();
                int toPosition = targetHolder.getAdapterPosition();

                // Item被拖拽时，交换数据，并更新adapter。
                Collections.swap(likeChannelList, fromPosition, toPosition);
                channelListAdapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {
                int position = srcHolder.getAdapterPosition();
                // Item被侧滑删除时，删除数据，并更新adapter。
                likeChannelList.remove(position);
                channelListAdapter.notifyItemRemoved(position);
            }
        };
        currentRecycle.setOnItemMoveListener(mItemMoveListener);// 监听拖拽，更新UI。
        currentRecycle.setLongPressDragEnabled(true);
        allRecycle.setLayoutManager(new GridLayoutManager(mContext, 4));
        allRecycle.setAdapter(disLikeAdapter = new ChannelListAdapter(mContext));
        allRecycle.setVerticalScrollBarEnabled(false);
    }

    @Override
    public void initData() {
        super.initData();
        getChannel();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.search_iv, R.id.edit_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.search_iv:
                submitChange();
                this.finish();
                break;
            case R.id.edit_tv:
                MobclickAgent.onEvent(mContext,"manageChannel");
                if (isEditing) {
                    coverAllTv.setVisibility(View.VISIBLE);
                    coverCurrentTv.setVisibility(View.VISIBLE);
                    editTv.setText("编辑");
                    isEditing = false;
                    submitChange();
                } else {
                    coverAllTv.setVisibility(View.GONE);
                    coverCurrentTv.setVisibility(View.GONE);
                    editTv.setText("提交");
                    isEditing = true;
                    //提交顺序修改
                }
                changeMyChannelState(isEditing, likeChannelList);
                channelListAdapter.clear();
                channelListAdapter.addAll(likeChannelList);
                break;
        }
    }

    void changeMyChannelState(boolean onEditing, List<ChannelBean> channelBeans) {
        for (ChannelBean bean : channelBeans
                ) {
            bean.setEditing(onEditing);
        }
    }

    //提交排序
    private void submitChange() {
        final StringBuffer idString = new StringBuffer();
        ChannelBean.saveObject(getAppContext(),"kChannelBeanList",likeChannelList);
        for (ChannelBean bean : likeChannelList
                ) {
            idString.append(bean.getChannel_id() + ",");
        }
        showProgress("正在提交");
        HashMap<String, String> params = new HashMap<>();
        params.put("like", idString.toString());
        NetworkManager.INSTANCE.post(Apis.editChannel, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                hideProgress();
                showToast("提交成功");
                EventBus.getDefault().post(new ChangeChannelEvent());
            }

            @Override
            public void onError(String errorMessage) {
                hideProgress();
                showToast(errorMessage);
            }
        });
    }

    private void getChannel() {
        showProgress();
        HashMap<String, String> params = new HashMap<>();
        NetworkManager.INSTANCE.post(Apis.getChannel, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                hideProgress();
                String result = GsonUtils.getStringFromJSON(response, "datas", "like");
                String resultDislike = GsonUtils.getStringFromJSON(response, "datas", "dislike");
                List<ChannelBean> olderlist = (List<ChannelBean>)ChannelBean.readObject(getAppContext(),"kChannelBeanList");
                if (olderlist.size()==0||olderlist==null) {
                    olderlist = GsonUtils.jsonToBeanList(result, new TypeToken<List<ChannelBean>>() {
                    }.getType());
                }
                if (ValidateUtils.isValidate(olderlist)) {
                    likeChannelList.addAll(olderlist);
                    channelListAdapter.addAll(likeChannelList);
                }
                disLikeChannelList = GsonUtils.jsonToBeanList(resultDislike, new TypeToken<List<ChannelBean>>() {
                }.getType());
                if (ValidateUtils.isValidate(disLikeChannelList)) {
                    disLikeAdapter.addAll(disLikeChannelList);
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
        disLikeAdapter.setOnItemClickListener(position -> {
//            isEditing = true;
//            editTv.setText("提交");
            //在订阅区增加
            likeChannelList.add(disLikeAdapter.getItem(position));

            channelListAdapter.clear();
            channelListAdapter.addAll(likeChannelList);

            disLikeChannelList.remove(position);
            disLikeAdapter.clear();
            disLikeAdapter.addAll(disLikeChannelList);
        });
        channelListAdapter.setOnItemClickListener(position -> {
//            isEditing = true;
//            editTv.setText("提交");
            //
            disLikeChannelList.add(channelListAdapter.getItem(position));
            disLikeAdapter.clear();
            disLikeAdapter.addAll(disLikeChannelList);

            likeChannelList.remove(position);
            channelListAdapter.clear();
            channelListAdapter.addAll(likeChannelList);

        });
    }
}
