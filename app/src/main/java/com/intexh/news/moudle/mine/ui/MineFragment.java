package com.intexh.news.moudle.mine.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.intexh.news.R;
import com.intexh.news.helper.LoginHelper;
import com.intexh.news.helper.ShareHelper;
import com.intexh.news.helper.UserHelper;
import com.intexh.news.moudle.mine.bean.LoginBean;
import com.intexh.news.moudle.mine.bean.ShareBean;
import com.intexh.news.moudle.mine.event.LightModeEvent;
import com.intexh.news.moudle.mine.event.LoginEvent;
import com.intexh.news.moudle.mine.event.LoginOutEvent;
import com.intexh.news.moudle.mine.event.ReverseEvent;
import com.intexh.news.moudle.mine.event.TextModeEvent;
import com.intexh.news.net.Apis;
import com.intexh.news.utils.DialogUtil;
import com.intexh.news.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;
import com.zjw.base.UI.BaseFragment;
import com.zjw.base.glide.GlideHelper;
import com.zjw.base.net.NetworkManager;
import com.zjw.base.net.TokenInvalidEvent;
import com.zjw.base.utils.GsonUtils;
import com.zjw.base.utils.ValidateUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.jpush.android.api.JPushInterface;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * Created by AndroidIntexh1 on 2017/11/1.
 */

public class MineFragment extends BaseFragment {
    @BindView(R.id.avarter_iv)
    ImageView avarterIv;
    @BindView(R.id.name_tv)
    TextView nameTv;
    @BindView(R.id.add_info_tv)
    TextView addInfoTv;
    @BindView(R.id.manage_info_tv)
    TextView manageInfoTv;
    @BindView(R.id.check_info_tv)
    TextView checkInfoTv;
    @BindView(R.id.share_news_tv)
    TextView shareNewsTv;
    @BindView(R.id.be_author_tv)
    TextView beAuthorTv;
    @BindView(R.id.notification_tv)
    TextView notificationTv;
    @BindView(R.id.ads_tv)
    TextView adsTv;
    @BindView(R.id.cooperation_tv)
    TextView cooperationTv;
    @BindView(R.id.cover_tv)
    TextView coverTv;
    @BindView(R.id.feed_back)
    TextView feedBack;
    @BindView(R.id.setting_tv)
    TextView settingTv;
    Unbinder unbinder;
    @BindView(R.id.edit_tv)
    TextView editTv;
    @BindView(R.id.light_mode_rg)
    RadioGroup lightModeRg;
    @BindView(R.id.push_state_sw)
    Switch pushStateSw;
    @BindView(R.id.text_min_rb)
    RadioButton textMinRb;
    @BindView(R.id.text_mid_rb)
    RadioButton textMidRb;
    @BindView(R.id.text_max_rb)
    RadioButton textMaxRb;
    @BindView(R.id.text_mode_rg)
    RadioGroup textModeRg;
    @BindView(R.id.info_ll)
    LinearLayout infoLl;
    @BindView(R.id.author_v)
    View authorV;
    @BindView(R.id.check_info_v)
    View checkInfoV;
    @BindView(R.id.store_ll)
    RelativeLayout storeLl;
    @BindView(R.id.comment_ll)
    RelativeLayout commentLl;
    @BindView(R.id.light_white_rb)
    RadioButton lightWhiteRb;
    @BindView(R.id.light_dark_rb)
    RadioButton lightDarkRb;
    @BindView(R.id.light_auto_rb)
    RadioButton lightAutoRb;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void initView() {
        super.initView();
        // if (ValidateUtils.isValidate(UserHelper.getUserInfo()))
        EventBus.getDefault().register(this);
        if (ValidateUtils.isValidate(UserHelper.getUserInfo())) {
            //是否手机绑定
            if (!ValidateUtils.isValidate(UserHelper.getUserInfo().getMember_mobile())) {
                DialogUtil.showConfirmHitDialog(mContext, "", "您还未绑定手机", "取消", "去绑定", new DialogUtil.DialogImpl() {
                    @Override
                    public void onRight() {
                        super.onRight();
                        Intent min = new Intent(mContext, RebongdingActivity.class);
                        min.putExtra("TYPE", "phone");
                        startActivity(min);
                    }
                });
            }
            GlideHelper.INSTANCE.loadCircleImage(avarterIv, UserHelper.getUserInfo().getMember_avatar());
            nameTv.setText(ValidateUtils.isValidate(UserHelper.getUserInfo().getMember_nickname()) ? UserHelper.getUserInfo().getMember_nickname() : "未设置昵称");
            editTv.setVisibility(View.VISIBLE);
            if (ValidateUtils.isValidate(UserHelper.getUserInfo().getMember_type())) {
                switch (UserHelper.getUserInfo().getMember_type()) {
                    case "0":
                        infoLl.setVisibility(View.GONE);
                        break;
                    case "1":
                        infoLl.setVisibility(View.VISIBLE);
                        checkInfoV.setVisibility(View.GONE);
                        checkInfoTv.setVisibility(View.GONE);
                        beAuthorTv.setVisibility(View.GONE);
                        authorV.setVisibility(View.GONE);
                        break;
                    case "2":
                        infoLl.setVisibility(View.VISIBLE);
                        beAuthorTv.setVisibility(View.VISIBLE);
                        authorV.setVisibility(View.VISIBLE);
                        checkInfoV.setVisibility(View.VISIBLE);
                        checkInfoTv.setVisibility(View.VISIBLE);
                        break;
                }
            }
        }
        if (UserHelper.getTextMode() == 0.8) {
            textModeRg.check(R.id.text_min_rb);
        } else if (UserHelper.getTextMode() == 1.0) {
            textModeRg.check(R.id.text_mid_rb);
        } else {
            textModeRg.check(R.id.text_max_rb);
        }
        switch (UserHelper.getLightMode()) {
            case "WHITE":
                lightModeRg.check(R.id.light_white_rb);
                break;
            case "DARK":
                lightModeRg.check(R.id.light_dark_rb);
                break;
            case "AUTO":
                lightModeRg.check(R.id.light_auto_rb);
                break;
            default:
                lightModeRg.check(R.id.light_white_rb);
                break;
        }
        lightModeRg.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i) {
                case R.id.light_white_rb:
                    UserHelper.saverLightMode("WHITE");
                    EventBus.getDefault().post(new LightModeEvent());
                    break;
                case R.id.light_dark_rb:
                    UserHelper.saverLightMode("DARK");
                    EventBus.getDefault().post(new LightModeEvent());
                    break;
                case R.id.light_auto_rb:
                    UserHelper.saverLightMode("AUTO");
                    EventBus.getDefault().post(new LightModeEvent());
                    break;
            }
        });
        textModeRg.setOnCheckedChangeListener((radioGroup, i) -> {
            // ToastUtil.showToast(mContext, "重启后生效");
            switch (i) {
                case R.id.text_min_rb:
                    UserHelper.saverTextMode(0.8);
                    EventBus.getDefault().post(new TextModeEvent());
                    break;
                case R.id.text_mid_rb:
                    UserHelper.saverTextMode(1.0);
                    EventBus.getDefault().post(new TextModeEvent());
                    break;
                case R.id.text_max_rb:
                    UserHelper.saverTextMode(1.2);
                    EventBus.getDefault().post(new TextModeEvent());
                    break;
            }
            DialogUtil.showConfirmHitDialog(mContext, "", "重启后生效，确定现在重启", "取消", "确定", new DialogUtil.DialogImpl() {
                @Override
                public void onRight() {
                    super.onRight();
                    restartApplication();
                }
            });

        });
        //权限管理：

        if (ValidateUtils.isValidate(UserHelper.getJPUSHMode())) {
            pushStateSw.setChecked(UserHelper.getJPUSHMode());
        } else {
            pushStateSw.setChecked(true);
        }
        pushStateSw.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                JPushInterface.resumePush(mContext);
                UserHelper.saverJPUSHMode(true);
            } else {
                JPushInterface.stopPush(mContext);
                UserHelper.saverJPUSHMode(false);
            }
        });
    }

    @Subscribe
    public void onMessageEvent(LoginEvent event) {
        //初始化个人信息
        GlideHelper.INSTANCE.loadCircleImage(avarterIv, UserHelper.getUserInfo().getMember_avatar());
        nameTv.setText(ValidateUtils.isValidate(UserHelper.getUserInfo().getMember_nickname()) ? UserHelper.getUserInfo().getMember_nickname() : "未设置昵称");
        editTv.setVisibility(View.VISIBLE);
        if (ValidateUtils.isValidate(UserHelper.getUserInfo().getMember_type())) {
            switch (UserHelper.getUserInfo().getMember_type()) {
                case "0":
                    infoLl.setVisibility(View.GONE);
                    break;
                case "1":
                    checkInfoV.setVisibility(View.GONE);
                    checkInfoTv.setVisibility(View.GONE);
                    infoLl.setVisibility(View.VISIBLE);
                    beAuthorTv.setVisibility(View.GONE);
                    authorV.setVisibility(View.GONE);
                    break;
                case "2":
                    infoLl.setVisibility(View.VISIBLE);
                    checkInfoV.setVisibility(View.VISIBLE);
                    checkInfoTv.setVisibility(View.VISIBLE);
                    beAuthorTv.setVisibility(View.VISIBLE);
                    authorV.setVisibility(View.VISIBLE);
                    break;
            }
        }
        //是否手机绑定
        if (!ValidateUtils.isValidate(UserHelper.getUserInfo().getMember_mobile())) {
            DialogUtil.showConfirmHitDialog(mContext, "", "您还未绑定手机", "取消", "去绑定", new DialogUtil.DialogImpl() {
                @Override
                public void onRight() {
                    super.onRight();
                    Intent min = new Intent(mContext, RebongdingActivity.class);
                    min.putExtra("TYPE", "phone");
                    startActivity(min);
                }
            });
        }
    }

    @Subscribe
    public void onMessageEvent(LoginOutEvent event) {
        //初始化个人信息
        UserHelper.clearUserInfo();
        UserHelper.clearCurrentToken();
        GlideHelper.INSTANCE.loadCircleImage(avarterIv, R.mipmap.default_avarter);
        nameTv.setText("点击登录");
        infoLl.setVisibility(View.GONE);
        editTv.setVisibility(View.GONE);
    }

    @Subscribe
    public void onMessageEvent(ReverseEvent event) {
        getUser();
        //初始化个人信息
        //是否手机绑定
        if (!ValidateUtils.isValidate(UserHelper.getUserInfo().getMember_mobile())) {
            DialogUtil.showConfirmHitDialog(mContext, "", "您还未绑定手机", "取消", "去绑定", new DialogUtil.DialogImpl() {
                @Override
                public void onRight() {
                    super.onRight();
                    Intent min = new Intent(mContext, RebongdingActivity.class);
                    min.putExtra("TYPE", "phone");
                    startActivity(min);
                }
            });
        }
    }

    @Subscribe
    public void onMessageEvent(TokenInvalidEvent event) {
        //初始化个人信息
        UserHelper.clearUserInfo();
        UserHelper.clearCurrentToken();
        GlideHelper.INSTANCE.loadCircleImage(avarterIv, R.mipmap.default_avarter);
        nameTv.setText("点击登录");
        infoLl.setVisibility(View.GONE);
        editTv.setVisibility(View.GONE);
        beAuthorTv.setVisibility(View.VISIBLE);
        authorV.setVisibility(View.VISIBLE);
    }

    private void getUser() {
        HashMap<String, String> params = new HashMap<>();
        NetworkManager.INSTANCE.post(Apis.getUserInfo, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                String result = GsonUtils.getStringFromJSON(response, "datas");
                LoginBean loginBean = GsonUtils.jsonToBean(result, LoginBean.class);
                UserHelper.saverUserInfo(loginBean);
                GlideHelper.INSTANCE.loadCircleImage(avarterIv, UserHelper.getUserInfo().getMember_avatar());
                nameTv.setText(ValidateUtils.isValidate(UserHelper.getUserInfo().getMember_nickname()) ? UserHelper.getUserInfo().getMember_nickname() : "未设置昵称");
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.avarter_iv, R.id.name_tv, R.id.add_info_tv, R.id.manage_info_tv, R.id.check_info_tv, R.id.share_news_tv, R.id.be_author_tv, R.id.notification_tv, R.id.ads_tv,
            R.id.cooperation_tv, R.id.cover_tv, R.id.feed_back, R.id.setting_tv, R.id.edit_tv,
            R.id.store_ll, R.id.comment_ll})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.avarter_iv:
                break;
            case R.id.edit_tv:
                startActivity(ReverseInfoActivity.class);
                break;
            case R.id.name_tv:
                if (ValidateUtils.isValidate(UserHelper.getCurrentToken())) {
                    return;
                }
                DialogUtil.showLoginBottomDialog(mContext, (dialog, type) -> {
                    switch (type) {
                        case "phoneTv":
                            startActivity(MessageLoginActivity.class);
                            dialog.dismiss();
                            break;
                        case "wechatTv":
                            requestWeixin();
                            dialog.dismiss();
                            break;
                        case "qqTv":
                            requestQQ();
                            dialog.dismiss();
                            break;
                    }
                });
                break;
            case R.id.add_info_tv:
                startActivity(AddInfoActivity.class);
                break;
            case R.id.manage_info_tv:

                startManagerActiviy("MANAGE");
                break;
            case R.id.check_info_tv:
                startManagerActiviy("CHECK");
                break;
            case R.id.share_news_tv:
                DialogUtil.showShareBottomDialog(mContext, (dialog, type) -> {
                    ShareBean shareBean = new ShareBean();
                    shareBean.setTitle("选报 | 精选您的新闻。");
                    String sdcardPath = Environment.getExternalStorageDirectory().toString();
                    String iconPath = sdcardPath + "/baiduTTS/ic_launcher.png";
                    shareBean.setImgPath(iconPath);
                    shareBean.setText("不花一分钱，找个秘书团。24小时精选\"您的行业新闻\"，可读可听，全球资讯，随时随地。");
                    shareBean.setUrl(Apis.authorWeb);
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
                    MobclickAgent.onEvent(mContext, "friendShare");
                });
                break;
            case R.id.be_author_tv:
                startActivity(ApplyAuthorActivity.class);
                break;
            case R.id.notification_tv:
                startActivity(NoticeActivity.class);
                break;
            case R.id.ads_tv:
                startActivity(InverstAdsActivity.class);
                break;
            case R.id.cooperation_tv:
                startActivity(CooperationActivity.class);
                break;
            case R.id.cover_tv:
                startActivity(NewsCoverActivity.class);
                break;
            case R.id.feed_back:
                startActivity(FeedBackActivity.class);
                break;
            case R.id.setting_tv:
//                String temp="http://180.150.177.135:8080/easypass-app/course/shareCourseDetail";
//                HashMap<String,String> param=new HashMap<>();
//                param.put("token","e056f3d1d60c1570fd7a23d10cf36d0cb70b84fa81492a5a9289f83ea2030f54");
//                param.put("courseId","20");
//                param.put("shareType","4");
//                NetworkManager.INSTANCE.postTemp(temp, param, new NetworkManager.OnRequestCallBack() {
//                @Override
//                public void onOk(String response) {
//
//                }
//
//                @Override
//                public void onError(String errorMessage) {
//
//                }
//            });

                startActivity(SettingActivity.class);
                break;
            case R.id.store_ll:
                if (!ValidateUtils.isValidate(UserHelper.getCurrentToken())) {
                    ToastUtil.showToast(mContext, "请先登录");
                    LoginHelper helper = new LoginHelper(mContext);
                    helper.showDialog();
                    return;
                }
                startUiversal("STORE");
                break;
            case R.id.comment_ll:
                if (!ValidateUtils.isValidate(UserHelper.getCurrentToken())) {
                    ToastUtil.showToast(mContext, "请先登录");
                    LoginHelper helper = new LoginHelper(mContext);
                    helper.showDialog();
                    return;
                }
                startUiversal("COMMENT");
                break;
        }
    }

    void startUiversal(String type) {
        Intent mintent = new Intent(mContext, StoreUniversalActivity.class);
        mintent.putExtra(StoreUniversalActivity.TYPE, type);
        startActivity(mintent);
    }

    void startManagerActiviy(String type) {
        Intent mintent = new Intent(mContext, ManagerInfoActivity.class);
        mintent.putExtra(ManagerInfoActivity.TYPE, type);
        startActivity(mintent);
    }

    private void qqLogin(String headimgurl, String nickname, String openid) {
        showProgress("正在登录");
        HashMap<String, String> params = new HashMap<>();
        params.clear();
        params.put("headimgurl", headimgurl);
        params.put("nickname", nickname);
        params.put("openid", openid);
        NetworkManager.INSTANCE.post(Apis.getQQLogin, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                hideProgress();
                String result = GsonUtils.getStringFromJSON(response, "datas");
                LoginBean loginBean = GsonUtils.jsonToBean(result, LoginBean.class);
                UserHelper.saveCurrentToken(loginBean.getKey());
                UserHelper.saverUserInfo(loginBean);
                EventBus.getDefault().post(new LoginEvent());
            }

            @Override
            public void onError(String errorMessage) {
                hideProgress();

            }
        });
    }

    private void weChatLogin(String headimgurl, String nickname, String openid, String unionid) {
        HashMap<String, String> params = new HashMap<>();
        params.clear();
        params.put("headimgurl", headimgurl);
        params.put("nickname", nickname);
        params.put("openid", openid);
        params.put("unionid", unionid);
        NetworkManager.INSTANCE.post(Apis.getWechatLogin, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                String result = GsonUtils.getStringFromJSON(response, "datas");
                LoginBean loginBean = GsonUtils.jsonToBean(result, LoginBean.class);
                UserHelper.saveCurrentToken(loginBean.getKey());
                UserHelper.saverUserInfo(loginBean);
                EventBus.getDefault().post(new LoginEvent());
            }

            @Override
            public void onError(String errorMessage) {
            }
        });
    }

    private void requestQQ() {
        Platform qq = ShareSDK.getPlatform(QZone.NAME);
        qq.removeAccount(true);
        //qq = ShareSDK.getPlatform(this, QQ.NAME);
//回调信息，可以在这里获取基本的授权返回的信息，但是注意如果做提示和UI操作要传到主线程handler里去执行
        qq.setPlatformActionListener(new PlatformActionListener() {

            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                // TODO Auto-generated method stub
                arg2.printStackTrace();
            }

            @Override
            public void onComplete(Platform arg0, int action, HashMap<String, Object> res) {
                // TODO Auto-generated method stub
                //输出所有授权信息
                //arg0.getDb().exportData();
                //输出所有授权信息
                String openid = null;
                arg0.getDb().exportData();
                Iterator ite = res.entrySet().iterator();
                while (ite.hasNext()) {
                    Map.Entry entry = (Map.Entry) ite.next();
                    Object key = entry.getKey();
                    Object value = entry.getValue();
                    Log.e("_wilson", "res :" + key + "： " + value);
                    System.out.println(key + "： " + value);
                }
                if (action == Platform.ACTION_USER_INFOR) {
                    PlatformDb platDB = qq.getDb();//获取数平台数据DB
//                    //通过DB获取各种数据
                    openid = platDB.getUserId();
                    qqLogin(res.get("figureurl_qq_2").toString(), res.get("nickname").toString(), openid);
                }

            }

            @Override
            public void onCancel(Platform arg0, int arg1) {
                // TODO Auto-generated method stub

            }
        });
        qq.showUser(null);//执行登录，登录后在回调里面获取用户资料
    }

    private void requestWeixin() {
        Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
        wechat.removeAccount(true);
//回调信息，可以在这里获取基本的授权返回的信息，但是注意如果做提示和UI操作要传到主线程handler里去执行
        wechat.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                hideProgress();
                //输出所有授权信息
                String openid = null;
                platform.getDb().exportData();
                Log.e("_wilson", "授权结果" + "" + platform.getDb().exportData());
                weChatLogin(hashMap.get("headimgurl").toString(), hashMap.get("nickname").toString(), hashMap.get("openid").toString(), hashMap.get("unionid").toString());
                Log.e("_wilson", "执行微信登录啊 :");
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                hideProgressDialog();
            }

            @Override
            public void onCancel(Platform platform, int i) {
                hideProgressDialog();
            }
        });
        wechat.showUser(null);//授权并获取用户信息
    }

    private void restartApplication() {
        final Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
