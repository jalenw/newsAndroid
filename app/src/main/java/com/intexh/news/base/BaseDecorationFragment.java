package com.intexh.news.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.intexh.news.R;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.zjw.base.config.BaseMainApp;

import java.lang.reflect.Field;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by AndroidIntexh1 on 2017/12/29.
 */

public abstract class BaseDecorationFragment extends Fragment {

    private RecyclerView recyclerView;
    private int backgroundColor = 0;
    private TextView loading_tv;
    private ProgressDialog loadingDialog;
    private View dialogLayout;
    protected View rootView;
    private Activity mActivity;
    public Handler handler;
    public String fragmentName;
    public boolean isDestroy;
    private Unbinder unbinder;
    protected Context mContext;
    private ProgressDialog dialog;
    TwinklingRefreshLayout twinkle;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_recycler, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        twinkle=view.findViewById(R.id.twinkle);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setAdapterAndDecor(recyclerView);
        init();
        initView();
        initData();
        initListener();
        fragmentName = this.getClass().toString();
        fragmentName = fragmentName.substring(fragmentName.lastIndexOf(".") + 1);
    }

    public RecyclerView getRecycle() {
        return recyclerView;
    }
    public TwinklingRefreshLayout getTwinkle() {
        return twinkle;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handler = new Handler();
        unbinder = ButterKnife.bind(this, view);
        mContext = getContext();


    }

    protected void init() {
    }

    protected void initView() {
    }

    protected void initData() {
    }

    protected void initListener() {
    }

    protected abstract void setAdapterAndDecor(RecyclerView list);

    public void startActivity(Class<?> activityName) {
        if (getActivityContext() != null) {
            startActivity(new Intent(getAppContext(), activityName));
        }
    }

    public Activity getActivityContext() {
        return mActivity;
    }

    public Context getAppContext() {
        return BaseMainApp.getAppContext();
    }

    public void showProgress(String message) {
        showProgress(message, false);
    }

    public void showProgress(String message, boolean isCancelable) {
        if (loadingDialog == null) {
            LayoutInflater inflater = LayoutInflater.from(getActivityContext());
            // 得到加载view
            dialogLayout = inflater.inflate(com.zjw.base.R.layout.layout_view_loading, null);
            loading_tv = (TextView) dialogLayout.findViewById(com.zjw.base.R.id.loading_tv);
            loadingDialog = new ProgressDialog(getActivityContext(), com.zjw.base.R.style.transparent_dialog_theme);
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