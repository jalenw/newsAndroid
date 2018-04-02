package com.intexh.news.moudle.news.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.intexh.news.R;
import com.intexh.news.helper.LoginHelper;
import com.intexh.news.helper.ShareHelper;
import com.intexh.news.helper.UserHelper;
import com.intexh.news.html.ui.WebViewActivity;
import com.intexh.news.moudle.main.adapter.easyadapter.BaseViewHolder;
import com.intexh.news.moudle.main.adapter.easyadapter.RecyclerArrayAdapter;
import com.intexh.news.moudle.mine.bean.ShareBean;
import com.intexh.news.moudle.news.bean.TabBean;
import com.intexh.news.moudle.news.ui.CommentActivity;
import com.intexh.news.moudle.news.ui.ReportNewsActivity;
import com.intexh.news.moudle.news.ui.ShowPic;
import com.intexh.news.net.Apis;
import com.intexh.news.utils.ChinaDateUtil;
import com.intexh.news.utils.DataUtil;
import com.intexh.news.utils.DialogUtil;
import com.intexh.news.utils.ToastUtil;
import com.intexh.news.utils.ViewUtil;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.umeng.analytics.MobclickAgent;
import com.zjw.base.glide.GlideHelper;
import com.zjw.base.net.NetworkManager;
import com.zjw.base.utils.ValidateUtils;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import butterknife.BindView;

import static com.intexh.news.net.Apis.mBaseUrl;

/**
 * Created by AndroidIntexh1 on 2017/11/8.
 */

public class NewsTabAdapter extends RecyclerArrayAdapter<TabBean> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {


    private Context mContext;
    private LayoutInflater inflater;
    boolean isOpened = false;
    boolean stored = false;

    public NewsTabAdapter(Context context) {
        super(context);
        mContext = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_tab_news, parent, false));
    }

    @Override
    public long getHeaderId(int position) {
        String day = DataUtil.getStrTime2(getAllData().get(position).getSorttime());
        long timeUnix = DataUtil.getTimeStamp(day, "yyyy-MM-dd");
        return timeUnix;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return new RecyclerView.ViewHolder(inflater.inflate(R.layout.item_news_head, parent, false)) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        View itemView = holder.itemView;
        TextView mHead = itemView.findViewById(R.id.head_tv);
        String time = getAllData().get(position).getSorttime();
        String[] times = DataUtil.getStrTime2(time).split("-");
        String chinaDate = ChinaDateUtil.oneDay(Integer.parseInt(times[0]), Integer.parseInt(times[1]), Integer.parseInt(times[2]));
        mHead.setText(chinaDate);
    }

    class ViewHolder extends BaseViewHolder<TabBean> {
        TextView timeTv;
        ImageView redpointIv;
        TextView titleTv;
        TextView contentTv;
        TextView openId;
        ImageView shareIv;
        ImageView storeIv;
        ImageView pointIv;
        TextView writeTv;
        ImageView picIv;
        TextView authorTv;
        ImageView adIv;
        TextView reportTv;
        RadioButton numberTv;
        TextView adTextTv;
        RelativeLayout picAdLl;
        LinearLayout allLl;
        TextView showTopIv;
        LinearLayout authorLl;
        View normalLineV;
        View titleLineV;
        LinearLayout txtadsLl;

        public ViewHolder(View itemView) {
            super(itemView);
            timeTv = itemView.findViewById(R.id.time_tv);
            redpointIv = itemView.findViewById(R.id.redpoint_iv);
            titleTv = itemView.findViewById(R.id.title_tv);
            contentTv = itemView.findViewById(R.id.content_tv);
            openId = itemView.findViewById(R.id.open_id);
            shareIv = itemView.findViewById(R.id.share_iv);
            storeIv = itemView.findViewById(R.id.store_iv);
            pointIv = itemView.findViewById(R.id.point_iv);

            writeTv = itemView.findViewById(R.id.write_tv);
            picIv = itemView.findViewById(R.id.pic_iv);
            authorTv = itemView.findViewById(R.id.author_tv);

            adIv = itemView.findViewById(R.id.ads_iv);
            reportTv = itemView.findViewById(R.id.report_tv);
            numberTv = itemView.findViewById(R.id.number_tv);
            adTextTv = itemView.findViewById(R.id.ad_text_tv);
            picAdLl = itemView.findViewById(R.id.pic_ad_ll);
            allLl = itemView.findViewById(R.id.all_ll);
            showTopIv = itemView.findViewById(R.id.show_top_iv);
            authorLl = itemView.findViewById(R.id.author_ll);
            normalLineV = itemView.findViewById(R.id.normal_line_v);
            titleLineV = itemView.findViewById(R.id.title_line_v);
            txtadsLl = itemView.findViewById(R.id.txtads_ll);
        }

        @Override
        public void setData(TabBean data) {
            super.setData(data);
            //展开
            if (data.getNew_title().equals("...../")) {//如果是图片广告
                normalLineV.setBackgroundColor(mContext.getResources().getColor(R.color.redline));
                GlideHelper.INSTANCE.loadImage(redpointIv, R.mipmap.big_redpoint);
                allLl.setVisibility(View.GONE);
                picAdLl.setVisibility(View.VISIBLE);

                adIv.setAdjustViewBounds(true);
//获取屏幕宽度
                WindowManager m = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
                DisplayMetrics outMetrics = new DisplayMetrics();
                m.getDefaultDisplay().getMetrics(outMetrics);
//计算宽高，我需要的图片尺寸是280*136
                int width = outMetrics.widthPixels - ViewUtil.dp2px(mContext, 53f); //乘以2是因为左右两侧的宽度
                int height = width*19/50; //280*136
                Log.e("wilson", "长宽" + width + "  /" + height);

//设置图片参数
                ViewGroup.LayoutParams layoutParams = adIv.getLayoutParams();
                layoutParams.width = width;
                layoutParams.height = height;
                adIv.setLayoutParams(layoutParams);

                GlideHelper.INSTANCE.loadImage(adIv, data.getNew_pic(), 6);
                timeTv.setText(DataUtil.getStrTime4(data.getSorttime()));
                adIv.setOnClickListener(view -> {
                    WebViewActivity.startActivity(mContext,data.getNew_tigs(), data.getAdUrl(),data.getNew_content());
                    HashMap<String, String> params = new HashMap<>();
                    params.put("advertiseid", data.getNew_id());
                    params.put("type", "flow");
                    NetworkManager.INSTANCE.post(Apis.clickNews, params, new NetworkManager.OnRequestCallBack() {
                        @Override
                        public void onOk(String response) {

                        }

                        @Override
                        public void onError(String errorMessage) {

                        }
                    });
                });
                return;
            }
            GlideHelper.INSTANCE.loadImage(redpointIv, R.mipmap.small_redpoint);
            picAdLl.setVisibility(View.GONE);
            allLl.setVisibility(View.VISIBLE);
            if (data.getNew_top().equals("1")) {
                showTopIv.setVisibility(View.VISIBLE);
            } else {
                showTopIv.setVisibility(View.GONE);
            }
            View.OnClickListener openListener = view -> {
                if (isOpened) {
                    //界面隐藏
                    openId.setText("展开");
                    writeTv.setVisibility(View.GONE);
                    pointIv.setVisibility(View.GONE);
                    storeIv.setVisibility(View.GONE);
                    numberTv.setVisibility(View.GONE);
                    adTextTv.setVisibility(View.GONE);
                    picIv.setVisibility(View.GONE);
                    contentTv.setVisibility(View.GONE);
                    isOpened = false;
                    authorLl.setVisibility(View.GONE);
                    titleLineV.setVisibility(View.GONE);
                    txtadsLl.setVisibility(View.GONE);
                    if (data.getNew_top().equals("1")) {
                        showTopIv.setVisibility(View.VISIBLE);
                    }
                    //指示适配
                    GlideHelper.INSTANCE.loadImage(redpointIv, R.mipmap.small_redpoint);
                    titleTv.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));//加粗
                    normalLineV.setBackgroundColor(mContext.getResources().getColor(R.color.greyRed));
                    timeTv.setTextColor(mContext.getResources().getColor(R.color.blackText));
                } else {
                    MobclickAgent.onEvent(mContext, "newsClick", data.getNew_id());
                    openId.setText("收起");
                    authorLl.setVisibility(View.VISIBLE);
                    writeTv.setVisibility(View.VISIBLE);
                    pointIv.setVisibility(View.VISIBLE);
                    storeIv.setVisibility(View.VISIBLE);
                    contentTv.setVisibility(View.VISIBLE);
                    titleLineV.setVisibility(View.VISIBLE);
                    txtadsLl.setVisibility(View.VISIBLE);
                    if (data.getNew_commentnum().equals("0")) {
                        numberTv.setVisibility(View.GONE);
                    } else {
                        numberTv.setVisibility(View.VISIBLE);
                    }
                    if (ValidateUtils.isValidate(data.getNew_pic())) {
                        GlideHelper.INSTANCE.loadImage(picIv, data.getNew_pic());
                        picIv.setVisibility(View.VISIBLE);
                        picIv.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                Bitmap bitmap = ((BitmapDrawable)picIv.getDrawable()).getBitmap();
                                Intent intent = new Intent(mContext, ShowPic.class);
                                intent.putExtra("bitmap", Bitmap2Bytes(bitmap));
                                mContext.startActivity(intent);
                            }
                        });
                    }
                    isOpened = true;
                    if (ValidateUtils.isValidate(data.getAdText())) {
                        adTextTv.setVisibility(View.VISIBLE);
                        adTextTv.setText(data.getAdText());
                    }
                    else
                    {
                        txtadsLl.setVisibility(View.GONE);
                    }
                    showTopIv.setVisibility(View.GONE);
                    //指示适配
                    GlideHelper.INSTANCE.loadImage(redpointIv, R.mipmap.big_redpoint);
                    titleTv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
                    normalLineV.setBackgroundColor(mContext.getResources().getColor(R.color.redline));
                    timeTv.setTextColor(mContext.getResources().getColor(R.color.redline));
                }
            };
            allLl.setOnClickListener(openListener);
            //新闻飘红
            if (ValidateUtils.isValidate(data.getNew_red())) {//爆料默认不存在飘红等
                if (data.getNew_red().equals("1")) {
                    titleTv.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                    contentTv.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                } else {
                    titleTv.setTextColor(mContext.getResources().getColor(R.color.blackText));
                    contentTv.setTextColor(mContext.getResources().getColor(R.color.blackText));
                }
            }
            adTextTv.setOnClickListener(view -> {
                if (ValidateUtils.isValidate(data.getAdUrl())) {
                    WebViewActivity.startActivity(mContext, data.getNew_tigs(),data.getAdUrl(),data.getAdText());
                    HashMap<String, String> params = new HashMap<>();
                    params.put("advertiseid", data.getNew_id());
                    params.put("type", "flow");
                    NetworkManager.INSTANCE.post(Apis.clickNews, params, new NetworkManager.OnRequestCallBack() {
                        @Override
                        public void onOk(String response) {

                        }

                        @Override
                        public void onError(String errorMessage) {

                        }
                    });
                }
            });
            if (ValidateUtils.isValidate(data.getNew_commentnum())) {
                numberTv.setText(data.getNew_commentnum());
            }

            //分享
            shareIv.setOnClickListener(view -> {
                DialogUtil.showShareBottomDialog(mContext, (dialog, type) -> {
                    ShareBean shareBean = new ShareBean();
                    shareBean.setTitle(data.getNew_title() + " | 选报APP");
                    shareBean.setText(data.getNew_content());
                    if (ValidateUtils.isValidate(data.getNew_pic())) {
                        shareBean.setPic(data.getNew_pic());
                    } else {
                        String sdcardPath = Environment.getExternalStorageDirectory().toString();
                        String iconPath = sdcardPath + "/baiduTTS/ic_launcher.png";
                        shareBean.setImgPath(iconPath);
                    }
                    shareBean.setUrl(Apis.shareNews + "new_id=" + data.getNew_id());
                    switch (type) {
                        case "wechatTv":
                            ShareHelper.INSTANCE.shareToWeChat(mContext, shareBean);
                            break;
                        case "friendTv":

                            ShareHelper.INSTANCE.shareToWeChatFriend(mContext, shareBean);
                            break;
                        case "qqTv":
                            ShareHelper.INSTANCE.shareToQQ(mContext, shareBean);
                            break;
                        case "weiboTv":
                            ShareHelper.INSTANCE.shareToSinaWeibo(shareBean);
                            break;
                        case "zoneTv":
                            ShareHelper.INSTANCE.shareToQQFriend(mContext, shareBean);
                            break;
                        case "dingdingTv":
                            ShareHelper.INSTANCE.shareToDingding(mContext, shareBean);
                            break;
                    }
                    MobclickAgent.onEvent(mContext, "newsShare");
                });
            });
            //评论
            writeTv.setOnClickListener(view -> {
                if (!ValidateUtils.isValidate(UserHelper.getCurrentToken())) {
                    ToastUtil.showToast(mContext, "请先登录");
                    LoginHelper helper = new LoginHelper(mContext);
                    helper.showDialog();
                    return;
                }
                Intent mintent = new Intent(mContext, CommentActivity.class);
                mintent.putExtra("new_id", data.getNew_id());
                mContext.startActivity(mintent);
            });
            titleTv.setText(data.getNew_title());
            contentTv.setText(data.getNew_content());
            if (ValidateUtils.isValidate(data.getSorttime())) {
                timeTv.setText(DataUtil.getStrTime4(data.getSorttime()));
            }
            authorTv.setText("编辑:" + data.getNew_uname());
            pointIv.setOnClickListener(view -> {//跳转评论页面
                if (!ValidateUtils.isValidate(UserHelper.getCurrentToken())) {
                    ToastUtil.showToast(mContext, "请先登录");
                    LoginHelper helper = new LoginHelper(mContext);
                    helper.showDialog();
                    return;
                }
                Intent mintent = new Intent(mContext, CommentActivity.class);
                mintent.putExtra("new_id", data.getNew_id());
                mContext.startActivity(mintent);
            });
            stored = data.isCollected();
            if (data.isCollected()) {
                GlideHelper.INSTANCE.loadImage(storeIv, R.mipmap.store_on);
            } else {
                GlideHelper.INSTANCE.loadImage(storeIv, R.mipmap.store_off);
            }
            storeIv.setOnClickListener(view -> {
                doStore(data.getNew_id());

            });

            reportTv.setOnClickListener(view -> {
                Intent min = new Intent(mContext, ReportNewsActivity.class);
                min.putExtra("NEWS_ID", data.getNew_id());
                mContext.startActivity(min);
            });
        }
        public byte[] Bitmap2Bytes(Bitmap bm) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
            return baos.toByteArray();
        }

        private void doStore(String new_id) {

            HashMap<String, String> params = new HashMap<>();
            params.put("new_id", new_id);
            NetworkManager.INSTANCE.post(Apis.storeNews, params, new NetworkManager.OnRequestCallBack() {
                @Override
                public void onOk(String response) {
                    if (stored) {
                        GlideHelper.INSTANCE.loadImage(storeIv, R.mipmap.store_off);
                        ToastUtil.showToast(mContext, "取消收藏");
                        stored = false;
                    } else {
                        ToastUtil.showToast(mContext, "收藏成功");
                        MobclickAgent.onEvent(mContext, "collect", new_id);
                        GlideHelper.INSTANCE.loadImage(storeIv, R.mipmap.store_on);
                        stored = true;
                    }
                }

                @Override
                public void onError(String errorMessage) {

                }
            });
        }
    }
}
