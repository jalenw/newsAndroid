package com.intexh.news.moudle.main.ui;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.intexh.news.R;
import com.intexh.news.base.AppBaseActivity;
import com.intexh.news.helper.LoginHelper;
import com.intexh.news.helper.UserHelper;
import com.intexh.news.moudle.disclose.ui.DisCloseFragment;
import com.intexh.news.moudle.main.bean.AreaBean;
import com.intexh.news.moudle.main.event.LocationEvent;
import com.intexh.news.moudle.main.service.NetworkChangeReceiver;
import com.intexh.news.moudle.mine.event.LightModeEvent;
import com.intexh.news.moudle.mine.ui.MineFragment;
import com.intexh.news.moudle.mine.ui.RebongdingActivity;
import com.intexh.news.moudle.news.event.NewNewsEvent;
import com.intexh.news.moudle.news.event.NewNumberEvent;
import com.intexh.news.moudle.news.event.WechatPayEvent;
import com.intexh.news.moudle.news.ui.NewsFragment;
import com.intexh.news.net.Apis;
import com.intexh.news.utils.DialogUtil;
import com.intexh.news.utils.ToastUtil;
import com.intexh.news.widget.NoScrollViewpager;
import com.intexh.news.widget.ViewPagerScroller;
import com.zjw.base.net.NetworkManager;
import com.zjw.base.utils.GsonUtils;
import com.zjw.base.utils.LogCatUtil;
import com.zjw.base.utils.ValidateUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import static android.support.v7.app.AppCompatDelegate.MODE_NIGHT_AUTO;
import static android.support.v7.app.AppCompatDelegate.MODE_NIGHT_NO;
import static android.support.v7.app.AppCompatDelegate.MODE_NIGHT_YES;
import static com.intexh.news.moudle.mine.ui.SettingActivity.getAppVersionName;

public class MainActivity extends AppBaseActivity {

    private static final String TAB_NEWS = "tab_home";
    private static final String TAB_DISCLOSE = "tab_notice";
    private static final String TAB_MINE = "tab_search";

    @BindView(R.id.main_view_pager)
    NoScrollViewpager mainViewPager;
    @BindView(R.id.disclose_rb)
    RadioButton discloseRb;
    @BindView(R.id.mine_rb)
    RadioButton mineRb;
    @BindView(R.id.news_rb)
    RadioButton newsRb;
    @BindView(R.id.main_rg)
    RadioGroup mainRg;
    @BindView(R.id.step1_iv)
    ImageView step1Iv;
    @BindView(R.id.step2_iv)
    ImageView step2Iv;
    @BindView(R.id.step3_iv)
    ImageView step3Iv;
    View introduceLl;
    //    @BindView(R.id.number_tv)
//    RadioButton numberTv;
    int unreadNumber = 0;

    private NewsFragment newsFragment;
    private DisCloseFragment disCloseFragment;
    private MineFragment mineFragment;


    private List<Fragment> fragments = new ArrayList<>();
    int REQUEST_LOCATION_PERMISSION = 1;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    NetworkChangeReceiver receiver;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        super.initView();
        receiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(receiver, intentFilter);
        checkLocationPermession();
//        if (!ValidateUtils.isValidate(UserHelper.getCity())) {
//            checkLocationPermession();
//        }
        initPermission();
        introduceLl = findViewById(R.id.introduce_ll);
        introduceLl.setClickable(true);
        introduceLl.setFocusable(true);
        if (UserHelper.getShowInTrodructPic()) {//初始化 是否显示提示图片
            introduceLl.setVisibility(View.GONE);
        } else {
            introduceLl.setVisibility(View.VISIBLE);
        }
        initRadioGroup();
        //设置viewpager滑动时间
        mainViewPager.setOffscreenPageLimit(4);
        ViewPagerScroller pagerScroller = new ViewPagerScroller(this);
        pagerScroller.setScrollDuration(800);//设置时间，时间越长，速度越慢
        pagerScroller.initViewPagerScroll(mainViewPager);
        newsFragment = new NewsFragment();
        disCloseFragment = new DisCloseFragment();
        mineFragment = new MineFragment();

        fragments.add(newsFragment);
        fragments.add(disCloseFragment);
        fragments.add(mineFragment);

        mainViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        });
    }

    @Override
    public void initData() {
        super.initData();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);

        if (!this.isNotificationEnabled(this))
        {
            DialogUtil.showConfirmHitDialog(mContext, "", "您还未开启通知", "取消", "去开启", new DialogUtil.DialogImpl() {
                @Override
                public void onRight() {
                    super.onRight();
                    goToSet();
                }
            });
        }
        setAlias();
        checkVersion();
    }

    private void setAlias() {
        String alias = "222222";
        JPushInterface.setAlias(getApplicationContext(),alias, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                LogCatUtil.e("","设置别名="+i+":"+s);
            }
        });
    }

    void checkVersion() {
        HashMap<String, String> params = new HashMap<>();
        NetworkManager.INSTANCE.post(Apis.aboutUs, params, new NetworkManager.OnRequestCallBack() {
            @Override
            public void onOk(String response) {
                String result = GsonUtils.getStringFromJSON(response, "datas");
                String serverVersion = GsonUtils.getStringFromJSON(result, "app_android_version");
                if (serverVersion!=null) {
                    serverVersion = serverVersion.replace("v", "");
                    String downLoad = GsonUtils.getStringFromJSON(result, "app_android_url");

                    if (Double.parseDouble(getAppVersionName(mContext)) < Double.parseDouble(serverVersion)) {
                        DialogUtil.showConfirmHitDialog(mContext, "", "检测到新版本,是否前往下载", "取消", "去下载", new DialogUtil.DialogImpl() {
                            @Override
                            public void onRight() {
                                super.onRight();
                                Intent intent = new Intent();
                                intent.setAction("android.intent.action.VIEW");
                                Uri content_url;
                                if (downLoad.contains("http") || downLoad.contains("https")) {
                                    content_url = Uri.parse(downLoad);
                                } else {
                                    content_url = Uri.parse("http://" + downLoad);
                                }
                                intent.setData(content_url);
                                startActivity(intent);
                            }
                        });
                    } else {
                    }
                }
            }
            @Override
            public void onError(String errorMessage) {
                hideProgress();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean isNotificationEnabled(Context context) {

        String CHECK_OP_NO_THROW = "checkOpNoThrow";
        String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;

        Class appOpsClass = null;
         /* Context.APP_OPS_MANAGER */
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                    String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);

            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }


    private void goToSet(){
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
        }
        startActivity(localIntent);
    }

    private void initRadioGroup() {
        mainRg.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.news_rb:
                    changeTab(TAB_NEWS);
                    break;
                case R.id.disclose_rb:
                    changeTab(TAB_DISCLOSE);
                    if (!ValidateUtils.isValidate(UserHelper.getCurrentToken())) {
                        showToast("请先登录");
                        LoginHelper helper = new LoginHelper(mContext);
                        helper.showDialog();
                    }
                    break;
                case R.id.mine_rb:
                    changeTab(TAB_MINE);
                    break;

            }
        });
    }

    private void changeTab(String tabTag) {
        switch (tabTag) {
            case TAB_NEWS:
                mainViewPager.setCurrentItem(0);
                newsRb.setChecked(true);
                break;
            case TAB_DISCLOSE:
                mainViewPager.setCurrentItem(1);
                discloseRb.setChecked(true);
                break;
            case TAB_MINE:
                mainViewPager.setCurrentItem(2);
                mineRb.setChecked(true);
                break;

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            DialogUtil.showConfirmHitDialog(mContext, "", "是否退出应用", "取消", "确定", new DialogUtil.DialogImpl() {
                @Override
                public void onRight() {
                    super.onRight();
                    finish();
                }
            });

            return true;
        }
        return false;
    }

    /**
     * 微信支付状态监听
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(WechatPayEvent event) {
        switch (event.getPayStatus()) {
            case "ok":
                //   ToastUtil.showToast(mContext,"微信支付完成");
                LogCatUtil.e("frank", "微信支付完成=");
//paySuccess(weChatPayJumpUrl);
                ToastUtil.showToast(this, "支付完成");
                break;
            case "error":
                // loading_layout.setVisibility(View.GONE);
                LogCatUtil.e("frank", "微信支付错误=");
                ToastUtil.showToast(this, "支付失败，请重新支付");
                break;
            case "cancel":
                //  loading_layout.setVisibility(View.GONE);
                LogCatUtil.e("frank", "微信支付取消=");
                ToastUtil.showToast(this, "支付取消");
                break;
        }

    }

    @OnClick({R.id.step1_iv, R.id.step2_iv, R.id.step3_iv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.step1_iv:
                step1Iv.setVisibility(View.GONE);
                step2Iv.setVisibility(View.VISIBLE);
                break;
            case R.id.step2_iv:
                step2Iv.setVisibility(View.GONE);
                step3Iv.setVisibility(View.VISIBLE);
                break;
            case R.id.step3_iv:
                step3Iv.setVisibility(View.GONE);
                introduceLl.setVisibility(View.GONE);
                UserHelper.saveShowInTrodructPic(true);
                break;
        }
    }


    @Subscribe
    public void onEventMainThread(LightModeEvent event) {
        // UI updates must run on MainThread
        String mode = UserHelper.getLightMode();
        switch (mode) {
            case "WHITE":
                getDelegate().setLocalNightMode(MODE_NIGHT_NO);
                //AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO );
                break;
            case "DARK":
                getDelegate().setLocalNightMode(MODE_NIGHT_YES);
                // AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
                break;
            case "AUTO":
                getDelegate().setLocalNightMode(MODE_NIGHT_AUTO);
                //AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_AUTO);
                break;
        }
    }

    void checkLocationPermession() {
        //如果有权限直接执行
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //请求成功
            location();
        }
        //如果没有权限那么申请权限
        else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }

    /*
* 当请求获取权限后会执行此回调方法，来执行自己的业务逻辑
* */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == this.REQUEST_LOCATION_PERMISSION) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //执行自己的业务逻辑
                location();
            } else {
                DialogUtil.showConfirmHitDialog(mContext, "定位需要开启权限", "定位需要开启位置权限", "取消", "确定", new DialogUtil.DialogImpl() {
                    @Override
                    public void onLeft() {
                        super.onLeft();
                        LocationEvent event = new LocationEvent();
                        event.setLocationSuccess(false);
                        EventBus.getDefault().post(event);
                    }

                    @Override
                    public void onRight() {
                        super.onRight();
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                        intent.setData(uri);
                        mContext.startActivity(intent);
                    }
                });
            }
        } else {
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 获取当前定位
     */
    void location() {
        mLocationListener = amapLocation -> {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
//可在其中解析amapLocation获取相应内容。
                    amapLocation.getCity();//城市信息
                    Log.e("wilson",""+  amapLocation.getCity());
                    AreaBean bean = new AreaBean();
                    bean.setArea_name(amapLocation.getCity());
                    UserHelper.saverCity(bean);
                    LocationEvent event = new LocationEvent();
                    event.setLocationSuccess(true);
                    EventBus.getDefault().post(event);
                    mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                }
            }
        };
//初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
//设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //获取一次定位结果：
//该方法默认为false。
        mLocationOption.setOnceLocation(true);
//获取最近3s内精度最高的一次定位结果：
//设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);

//设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
//单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(20000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
//启动定位
        if(null != mLocationClient){
            mLocationClient.setLocationOption(mLocationOption);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
            Log.e("wilson",""+  "开启定位...");
        }
    }

    /**
     * android 6.0 以上需要动态申请权限
     */
    private void initPermission() {
        String permissions[] = {
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_SETTINGS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE
        };
        ArrayList<String> toApplyList = new ArrayList<String>();
        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                //进入到这里代表没有权限.
            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }
    }

}
