package com.intexh.news.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lcodecore.tkrefreshlayout.IBottomView;
import com.intexh.news.R;

/**
 * 创建者     gao hua
 * 创建时间   8/4 0004 15:16
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */

public class LoadMoreBottomView extends LinearLayout implements IBottomView {

    private ImageView ivLoading;

    public LoadMoreBottomView(Context context) {
        this(context, null);
    }

    public LoadMoreBottomView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LoadMoreBottomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View bottomView = View.inflate(context, R.layout.load_more_bottom, this);
        ivLoading = (ImageView) bottomView.findViewById(R.id.load_more_loading);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onPullingUp(float fraction, float maxBottomHeight, float bottomHeight) {

    }

    @Override
    public void startAnim(float maxBottomHeight, float bottomHeight) {
        ((AnimationDrawable)ivLoading.getDrawable()).start();
    }

    @Override
    public void onPullReleasing(float fraction, float maxBottomHeight, float bottomHeight) {

    }

    @Override
    public void onFinish() {

    }

    @Override
    public void reset() {

    }
}
