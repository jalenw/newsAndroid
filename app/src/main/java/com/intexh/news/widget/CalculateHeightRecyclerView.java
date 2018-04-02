package com.intexh.news.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * 创建者     gao hua
 * 创建时间   6/21 0021 11:33
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class CalculateHeightRecyclerView extends RecyclerView {
    public CalculateHeightRecyclerView(Context context) {
        super(context);
    }

    public CalculateHeightRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CalculateHeightRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightSpec);
    }
}
