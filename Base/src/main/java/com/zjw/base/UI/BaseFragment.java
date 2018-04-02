package com.zjw.base.UI;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.zjw.base.R;
import com.zjw.base.config.BaseMainApp;
import com.zjw.base.utils.InputMethodUtils;
import java.lang.reflect.Field;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Frank on 2017/3/25.
 */

public abstract class BaseFragment extends Fragment implements View.OnClickListener {

    private int backgroundColor = 0;
    private TextView loading_tv;
    private ProgressDialog loadingDialog;
    private View dialogLayout;
    protected View rootView;
    private Activity mActivity;
    public Handler handler;
    private String fragmentName;
    public boolean isDestroy;
    private Unbinder unbinder;
    protected Context mContext;
    private ProgressDialog dialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            try {
                rootView = inflater.inflate(getLayoutId(), container, false);
                rootView.setClickable(true);
            } catch (OutOfMemoryError e) {
                System.gc();
                rootView = inflater.inflate(getLayoutId(), container, false);
            }
        }
        initTranslucentStatus();
        /*
         * 避免tab切换的时候重绘
         * 缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，
         * 要不然会发生这个rootview已经有parent的错误。
         */
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        if (backgroundColor != 0) {
            rootView.setBackgroundColor(ContextCompat.getColor(getActivityContext(), backgroundColor));
        }
        return rootView;
    }

    public View getRootView() {
        return rootView;
    }

    protected void initTranslucentStatus(){}

    /**
     * 设置背景
     */
    protected void setBackground(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handler = new Handler();
        unbinder = ButterKnife.bind(this, view);
        mContext=getContext();
        init();
        initView();
        initData();
        initListener();
        fragmentName = this.getClass().toString();
        fragmentName = fragmentName.substring(fragmentName.lastIndexOf(".") + 1);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isDestroy = true;

        InputMethodUtils.hideSoftInput(getActivity());
    }


    /**
     * /**
     * 初始化组件
     *
     * @param resId
     * @return
     */

    @SuppressWarnings("unchecked")
    public <T extends View> T $(int resId) {
        return $(resId, rootView);
    }

    public <T extends View> T $(int resId, View rootView) {
        if (rootView == null) {
            throw new IllegalStateException("Content view not yet created");
        }
        return (T) rootView.findViewById(resId);
    }

    protected abstract int getLayoutId();

    protected void init() {}

    protected void initView(){}

    protected void initData(){}

    protected void initListener() {}

    public Activity getActivityContext() {
        return mActivity;
    }

    public Context getAppContext() {
        return BaseMainApp.getAppContext();
    }

    public void startActivity(Class<?> activityName) {
        if (getActivityContext() != null) {
            startActivity(new Intent(getAppContext(), activityName));
        }
    }

    public void finishActivity() {
        if (getActivityContext() != null) {
            getActivityContext().finish();
        }
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

    private int statusBarHeight;

    public int getStatusBarHeight1() {
        if (statusBarHeight == 0) {
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            }
        }
        return statusBarHeight;
    }

    @Override
    public void onClick(View v) {

    }

    public void showProgress(String message) {
        showProgress(message, false);
    }

    public void showProgress(String message, boolean isCancelable) {
        if (loadingDialog == null) {
            LayoutInflater inflater = LayoutInflater.from(getActivityContext());
            // 得到加载view
            dialogLayout = inflater.inflate(R.layout.layout_view_loading, null);
            loading_tv = (TextView) dialogLayout.findViewById(R.id.loading_tv);
            loadingDialog = new ProgressDialog(getActivityContext(), R.style.transparent_dialog_theme);
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
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    public void onLoadingDismiss() {

    }

    public void hideProgress() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }
    public void showProgressDialog(String message) {
        if (dialog == null) {
            dialog = new ProgressDialog(mContext);
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
