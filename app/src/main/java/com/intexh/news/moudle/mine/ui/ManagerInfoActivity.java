package com.intexh.news.moudle.mine.ui;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.SinaRefreshView;
import com.intexh.news.R;
import com.intexh.news.base.AppBaseActivity;
import com.intexh.news.moudle.disclose.adapter.DraftAdapter;
import com.intexh.news.moudle.disclose.bean.DraftBean;
import com.intexh.news.moudle.mine.event.CheckedEvent;
import com.intexh.news.moudle.news.bean.ChannelBean;
import com.intexh.news.moudle.news.event.NewNumberEvent;
import com.intexh.news.moudle.news.ui.TabFragment;
import com.intexh.news.net.Apis;
import com.intexh.news.utils.DataUtil;
import com.intexh.news.utils.DialogUtil;
import com.intexh.news.utils.ToastUtil;
import com.intexh.news.widget.LoadMoreBottomView;
import com.zjw.base.net.NetworkManager;
import com.zjw.base.utils.GsonUtils;
import com.zjw.base.utils.ValidateUtils;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ManagerInfoActivity extends AppBaseActivity {


    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.search_edt)
    EditText searchEdt;
    @BindView(R.id.search_rll)
    RelativeLayout searchRll;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.search_iv)
    ImageView searchIv;
    int page = 0;
    public static String TYPE = "INFO_TYPE";
    @BindView(R.id.twinkle)
    TwinklingRefreshLayout twinkle;
    DraftAdapter draftAdapter;
    @BindView(R.id.column_tv)
    TextView columnTv;
    @BindView(R.id.time_tv)
    TextView timeTv;
    String[] list;
    String currentChannalID;
    List<ChannelBean> allList;//频道列表
    String mSearchUrl;
    int searchPage = 0;//搜索的分页
    String currentType;//是管理还是审核
    boolean searchMode=false;

    String currentKey;
    String currentID;
    String currentTime;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_manager_info;
    }

    @Override
    public void initView() {
        super.initView();
        allList = new ArrayList<>();
        SinaRefreshView header = new SinaRefreshView(mContext);
        header.setArrowResource(R.mipmap.refresh_arrow);
        TextView refreshTextView = header.findViewById(com.lcodecore.tkrefreshlayout.R.id.tv);
        LoadMoreBottomView bottomView = new LoadMoreBottomView(mContext);
        refreshTextView.setTextSize(14);
        twinkle.setHeaderView(header);
        twinkle.setBottomView(bottomView);
        //  twinkle.startRefresh();
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(draftAdapter = new DraftAdapter(mContext));
        currentType = getIntent().getStringExtra(TYPE);
        switch (getIntent().getStringExtra(TYPE)) {
            case "MANAGE":
                titleTv.setText("管理资讯");
                break;
            case "CHECK":
                titleTv.setText("审核资讯");
                break;
        }
        getChannel();
    }

    @Subscribe
    public void onEventMainThread(CheckedEvent event) {

        switch (currentType) {
            case "MANAGE":
                if (ValidateUtils.isValidate(draftAdapter)) {
                    draftAdapter.clear();
                }
                getData(Apis.managerInfo);
                mSearchUrl = Apis.getNewsManager;
                break;
            case "CHECK":
                if (ValidateUtils.isValidate(draftAdapter)) {
                    draftAdapter.clear();
                }
                getData(Apis.checkInfo);
                mSearchUrl = Apis.getNewsCheck;
                break;
        }
    }

    @Override
    public void initData() {
        super.initData();
        twinkle.startRefresh();
        switch (getIntent().getStringExtra(TYPE)) {

            case "MANAGE":
              //  getData(Apis.managerInfo);
                mSearchUrl = Apis.getNewsManager;
                break;
            case "CHECK":
               // getData(Apis.checkInfo);
                mSearchUrl = Apis.getNewsCheck;
                break;
        }
    }

    private void getData(String url) {
        HashMap<String, String> params = new HashMap<>();
        params.put("page", page + "");
        NetworkManager.INSTANCE.post(url, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                if (twinkle != null) {
                    twinkle.finishRefreshing();
                    twinkle.finishLoadmore();
                }
                hideProgress();
                String result = GsonUtils.getStringFromJSON(response, "datas");
                List<DraftBean> list = GsonUtils.jsonToBeanList(result, new TypeToken<List<DraftBean>>() {
                }.getType());
                if (ValidateUtils.isValidate(list)) {
                    if (page == 0) {
                        draftAdapter.clear();
                    }
                    draftAdapter.addAll(list);
                } else {
                    page--;
                    showToast("没有更多了");
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (twinkle != null) {
                    twinkle.finishRefreshing();
                    twinkle.finishLoadmore();
                }
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

    @OnClick({R.id.back_iv, R.id.search_tv, R.id.search_iv, R.id.column_tv, R.id.time_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                this.finish();
                break;
            case R.id.search_tv:
                String chooseTim = "";
                if (ValidateUtils.isValidate(timeTv.getText().toString().trim())) {
                    chooseTim = DataUtil.getTimeStamp(timeTv.getText().toString().trim(), "yyyy-MM-dd HH:mm") + "";
                }
                doGetNews(searchEdt.getText().toString().trim(), ValidateUtils.isValidate(currentChannalID) ? currentChannalID : "",
                        chooseTim);
                break;
            case R.id.search_iv:
                searchRll.setVisibility(View.VISIBLE);
                searchIv.setVisibility(View.GONE);
                break;
            case R.id.column_tv:
                if (ValidateUtils.isValidate(list)) {
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Holo_Light_Dialog);
                        //    指定下拉列表的显示数据
                        //    设置一个下拉的列表选择项
                        builder.setItems(list, (dialog, which) -> {
                            columnTv.setText(list[which]);
                            currentChannalID = allList.get(which).getChannel_id();
                            String chooseTime = "";
                            if (ValidateUtils.isValidate(timeTv.getText().toString().trim())) {
                                chooseTime = DataUtil.getTimeStamp(timeTv.getText().toString().trim(), "yyyy-MM-dd HH:mm") + "";
                            }
                            doGetNews(searchEdt.getText().toString().trim(), ValidateUtils.isValidate(currentChannalID) ? currentChannalID : "",
                                    chooseTime);
                        });
                        AlertDialog r_dialog = builder.create();
                        r_dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        r_dialog.show();
                    }

                } else {
                    showToast("正在获取栏目，请稍后");
                }
                break;
            case R.id.time_tv:
                showDialogPick(timeTv);
                break;
        }
    }

    @Override
    public void initListener() {
        super.initListener();
        draftAdapter.setOnItemClickListener(position -> {
            Intent mintent = new Intent(mContext, AddInfoActivity.class);
            mintent.putExtra("bean", GsonUtils.serializedToJson(draftAdapter.getItem(position)));
            startActivity(mintent);
        });
        twinkle.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                handler.postDelayed(() -> {
                    if (searchMode){
                        doGetNews(currentKey,currentID,currentTime);
                        return;
                    }
                    page = 0;
                    switch (getIntent().getStringExtra(TYPE)) {
                        case "MANAGE":
                            getData(Apis.managerInfo);
                            break;
                        case "CHECK":
                            getData(Apis.checkInfo);
                            break;
                    }
                }, 500);
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                handler.postDelayed(() -> {
                    /*page++;
                    initHomeListData();*/
                    if (searchMode){
                        doGetNews(currentKey,currentID,currentTime);
                        return;
                    }
                    page++;
                    switch (getIntent().getStringExtra(TYPE)) {
                        case "MANAGE":
                            getData(Apis.managerInfo);
                            break;
                        case "CHECK":
                            getData(Apis.checkInfo);
                            break;
                    }
                }, 500);
            }
        });
        draftAdapter.setOnDeleteItem(position -> {
            DialogUtil.showConfirmHitDialog(mContext, "", "是否确认删除", "取消", "确定", new DialogUtil.DialogImpl() {
                @Override
                public void onRight() {
                    super.onRight();
                    deleteInfo(draftAdapter.getItem(position).getNew_id());
                    draftAdapter.remove(position);
                }
            });
        });
    }

    private void deleteInfo(String new_id) {
        showProgress("正在删除");
        HashMap<String, String> params = new HashMap<>();
        params.put("new_id", new_id);
        NetworkManager.INSTANCE.post(Apis.deleteNews, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                hideProgress();
                showToast("删除成功");
            }

            @Override
            public void onError(String errorMessage) {
                hideProgress();
                showToast("删除失败");
            }
        });
    }

    //将两个选择时间的dialog放在该函数中
    private void showDialogPick(final TextView timeText) {
        final StringBuffer time = new StringBuffer();
        //获取Calendar对象，用于获取当前时间
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        //实例化TimePickerDialog对象
        //选择完时间后会调用该回调函数
        final TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, (view, hourOfDay, minute1) -> {
            time.append(" " + hourOfDay + ":" + minute1);
            //设置TextView显示最终选择的时间

        }, hour, minute, true);
        //实例化DatePickerDialog对象
        //选择完日期后会调用该回调函数
        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, (view, year1, monthOfYear, dayOfMonth) -> {
            //因为monthOfYear会比实际月份少一月所以这边要加1
            time.append(year1 + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
            timeText.setText(time);
            String chooseTime = DataUtil.getTimeStamp(timeTv.getText().toString().trim(), "yyyy-MM-dd HH:mm") + "";
            doGetNews(searchEdt.getText().toString().trim(), ValidateUtils.isValidate(currentChannalID) ? currentChannalID : "", chooseTime);
            //选择完日期后弹出选择时间对话框
            //timePickerDialog.show();
        }, year, month, day);
        //弹出选择日期对话框
        datePickerDialog.show();
    }

    private void getChannel() {
        HashMap<String, String> params = new HashMap<>();
        NetworkManager.INSTANCE.post(Apis.getChannel, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                String result = GsonUtils.getStringFromJSON(response, "datas", "like");
                List<ChannelBean> channelBeanList = GsonUtils.jsonToBeanList(result, new TypeToken<List<ChannelBean>>() {
                }.getType());

                String dislike_result = GsonUtils.getStringFromJSON(response, "datas", "dislike");
                List<ChannelBean> dislike_List = GsonUtils.jsonToBeanList(dislike_result, new TypeToken<List<ChannelBean>>() {
                }.getType());

                if (ValidateUtils.isValidate(channelBeanList)) {
                    allList.addAll(channelBeanList);
                }
                if (ValidateUtils.isValidate(dislike_List)) {
                    allList.addAll(dislike_List);
                }
                int i = 0;
                list = new String[allList.size()];
                for (ChannelBean bean : allList
                        ) {
                    list[i++] = bean.getChannel_content();

                }
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }

    void doGetNews(String searchKey, String channelID, String time) {
        searchMode=true;
        showProgress("加载中");
        HashMap<String, String> params = new HashMap<>();
        params.put("search", searchKey);
        params.put("channel_id", channelID);
        params.put("time", time);
        params.put("page", searchPage + "");
        currentKey=ValidateUtils.isValidate(searchKey)?searchKey:"";
        currentID=ValidateUtils.isValidate(channelID)?channelID:"";
        currentTime=ValidateUtils.isValidate(time)?time:"";
        NetworkManager.INSTANCE.post(mSearchUrl, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                hideProgress();
                twinkle.finishLoadmore();
                twinkle.finishRefreshing();
                String result = GsonUtils.getStringFromJSON(response, "datas");
                List<DraftBean> list = GsonUtils.jsonToBeanList(result, new TypeToken<List<DraftBean>>() {
                }.getType());
                if (searchPage==0){
                    draftAdapter.clear();
                }
                if (ValidateUtils.isValidate(list)) {
                    draftAdapter.addAll(list);
                    searchPage++;
                } else {
                    if (searchPage==0){
                        ToastUtil.showToast(mContext, "没有相关结果");
                    }else {
                        ToastUtil.showToast(mContext, "没有更多结果");
                    }
                    searchPage--;
                }
            }

            @Override
            public void onError(String errorMessage) {
                hideProgress();
                twinkle.finishLoadmore();
                twinkle.finishRefreshing();
                showToast(errorMessage);
            }
        });
    }

}
