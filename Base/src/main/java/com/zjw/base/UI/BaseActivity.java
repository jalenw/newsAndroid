package com.zjw.base.UI;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.zjw.base.R;
import com.zjw.base.config.BaseMainApp;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    private boolean isShowBackAnim = true;  //是否显示退出动画
    protected ActivityKeyDownListener activityKeyDownListener;
    private ProgressDialog loadingDialog;
    private TextView loading_tv;
    protected Context mContext;
    private View dialogLayout;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseMainApp.addActivity(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mContext = this;
    }


    /**
     * 是否显示退出动画
     */
    protected void setIsShowBackAnim(boolean isShowBackAnim) {
        this.isShowBackAnim = isShowBackAnim;
    }

    private long lastTimes;

    //防止多次点击
    protected boolean clickTimeAllow() {
        if (System.currentTimeMillis() - lastTimes > 500) {
            lastTimes = System.currentTimeMillis();
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
    }

    protected <T extends View> T $(int resId) {
        return (T) super.findViewById(resId);
    }


    @Override
    public void finish() {
        super.finish();
//        if (!isShowBackAnim) return;
//        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);    //activity退出动画
    }

    protected void onDestroy() {
        /*//强制content view尽早释放
        setContentView(new View(this));*/ //CP
        hideProgress();
        BaseMainApp.removeActivity(this);
        super.onDestroy();
        System.gc();
    }


    public void replaceFragment(Fragment fragment) {
        replaceFragment(fragment, true);
    }

    public void replaceFragment(Fragment fragment, boolean isAddToBackStack) {
        try {
            String tag = getClass().getSimpleName();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(android.R.id.content, fragment, tag);
            transaction.setTransition(FragmentTransaction.TRANSIT_NONE);//设置动画效果
            if (isAddToBackStack) {
                transaction.addToBackStack(tag);
            }
            transaction.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace(); //java.lang.IllegalStateException: Activity has been destroyed
        }
    }

    public void replaceFragment(int layoutId, Fragment fragment, boolean isAddToBackStack) {
        try {
            String tag = getClass().getSimpleName();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(layoutId, fragment, tag);
            transaction.setTransition(FragmentTransaction.TRANSIT_NONE);//设置动画效果
            if (isAddToBackStack) {
                transaction.addToBackStack(tag);
            }
            transaction.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace(); //java.lang.IllegalStateException: Activity has been destroyed
        }
    }

    public void addFragment(Fragment fragment, boolean isAddToBackStack) {
        addFragment(fragment, isAddToBackStack, true);
    }

    public void addFragment(Fragment fragment, boolean isAddToBackStack, boolean isShowAnim) {
        if (!fragment.isAdded()) {
            String tag = getClass().getSimpleName();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (isShowAnim) {
                transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out, R.anim.slide_right_in, R.anim.slide_right_out);
            }
            transaction.add(android.R.id.content, fragment, tag);
            if (isAddToBackStack) {
                transaction.addToBackStack(tag);
            }
            transaction.commitAllowingStateLoss();
        }
    }


    /**
     * [页面跳转]
     *
     * @param clz
     */
    public void startActivity(Class<?> clz) {
        startActivity(new Intent(getApplicationContext(), clz));
    }

    /**
     * [携带数据的页面跳转]
     *
     * @param clz
     * @param bundle
     */
    public void startActivity(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    public void showProgress(String message){
        showProgress(message,false);
    }
    public void showProgress(){
        showProgress("请稍后...",false);
    }
    public void showProgress(boolean isCancelable){
        showProgress("请稍后...",isCancelable);
    }

    public void showProgress(String message,boolean isCancelable){
        if(loadingDialog==null){
            LayoutInflater inflater = LayoutInflater.from(this);
            // 得到加载view
            dialogLayout = inflater.inflate(R.layout.layout_view_loading, null);
            loading_tv = (TextView) dialogLayout.findViewById(R.id.loading_tv);
            loadingDialog = new ProgressDialog(this, R.style.transparent_dialog_theme);
            loadingDialog.setCancelable(isCancelable);
            loadingDialog.setCanceledOnTouchOutside(false);
            loadingDialog.show();
            loadingDialog.setContentView(dialogLayout);// 设置布局 需要在show后执行 否则报requestFeature() must be called before adding content
            loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    onLoadingDismiss();
                }
            });
        }
        loading_tv.setText(message);
        if(!loadingDialog.isShowing()){
            loadingDialog.show();
        }
    }

    public void onLoadingDismiss(){

    }

    public void hideProgress(){
        if(loadingDialog!=null){
            loadingDialog.dismiss();
        }
    }

    public Context getAppContext(){
        return BaseMainApp.getAppContext();
    }
    public void showProgressDialog(String message) {
        if (dialog == null) {
            dialog = new ProgressDialog(this);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(false);
        }
        dialog.setMessage(message);
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }
    public void hideProgressDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
