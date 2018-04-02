package com.intexh.news.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;

import com.intexh.news.R;


/**
 * Created by AndroidIntexh1 on 2017/7/22.
 */

public class EasyTextView extends android.support.v7.widget.AppCompatTextView {
    private float etvRadius;    //切角半径
    private float etvTopLeftRadius;
    private float etvTopRightRadius;
    private float etvBottomLeftRadius;
    private float etvBottomRightRadius;
    private int etvPressTextColor;
    private int etvNormalTextColor;
    private int etvPressBackgroundColor;
    private int etvNormalBackgroundColor;
    private GradientDrawable pressGd;
    private GradientDrawable normalGd;

    /**
     * public static final int RECTANGLE = 0;
     * public static final int OVAL = 1;
     * public static final int LINE = 2;
     * public static final int RING = 3;
     */
    private int etvShape;   //形状 默认是矩形

    public EasyTextView(Context context) {
        this(context,null);
    }
    public EasyTextView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    public EasyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributeSet(context,attrs);
        refreshUI();
    }

    private void refreshUI() {
        //创建drawable
        normalGd = new GradientDrawable();
        normalGd.setShape(etvShape);
        //创建按下drawable
        pressGd = new GradientDrawable();
        pressGd.setShape(etvShape);
        if(etvNormalBackgroundColor != -1 ){    //默认背景色不为空才创建shape
            setRadius(normalGd,etvNormalBackgroundColor);
        }
        if(etvPressBackgroundColor !=-1){
            setRadius(pressGd,etvPressBackgroundColor);
        }
        if(etvNormalBackgroundColor !=-1 && etvPressBackgroundColor !=-1){  //状态颜色都设置后才添加选择器
            StateListDrawable stateListDrawable = new StateListDrawable();
            stateListDrawable.addState(new int[]{-android.R.attr.state_pressed},normalGd);    //按下时的背景色
            stateListDrawable.addState(new int[]{android.R.attr.state_pressed},pressGd);    //按下时的背景色
            setBackground(stateListDrawable);
        }else if(etvNormalBackgroundColor!=-1 && etvPressBackgroundColor == -1){    //如果设置平常的颜色
            setBackground(normalGd);
        }
//        setTextColor(etvNormalTextColor);
        //其他情况显示默认TextView样式
        if(etvNormalTextColor != 0 && etvPressTextColor !=0){
            int[][] states = new int[2][];
            states[0] = new int[] { android.R.attr.state_pressed};
            states[1] = new int[] { -android.R.attr.state_pressed};
            int[] colors = {etvPressTextColor,etvNormalTextColor};
            ColorStateList colorStateList = new ColorStateList(states,colors);
            setTextColor(colorStateList);
        }else if(etvNormalTextColor!= 0 && etvPressTextColor ==0){
            setTextColor(etvNormalTextColor);
        }

    }

    private void setRadius(GradientDrawable gd,int color) {
        gd.setColor(color);
        if(etvRadius !=0){
            gd.setCornerRadius(etvRadius);
        }else{
            if(etvTopLeftRadius !=0 || etvTopRightRadius !=0 || etvBottomLeftRadius!=0 ||etvBottomRightRadius!=0){
                float[] radii = {etvTopLeftRadius,etvTopLeftRadius,etvTopRightRadius,etvTopRightRadius,etvBottomLeftRadius,
                        etvBottomLeftRadius,etvBottomRightRadius,etvBottomRightRadius};
                gd.setCornerRadii(radii);
            }
        }
    }

    private void initAttributeSet(Context context,AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.EasyTextView);
        etvShape = typedArray.getInt(R.styleable.EasyTextView_etvShape,0);
        etvRadius = typedArray.getDimension(R.styleable.EasyTextView_etvRadius,0);
        etvTopLeftRadius = typedArray.getDimension(R.styleable.EasyTextView_etvTopLeftRadius,0);
        etvTopRightRadius = typedArray.getDimension(R.styleable.EasyTextView_etvTopRightRadius,0);
        etvBottomLeftRadius = typedArray.getDimension(R.styleable.EasyTextView_etvBottomLeftRadius,0);
        etvBottomRightRadius = typedArray.getDimension(R.styleable.EasyTextView_etvBottomRightRadius,0);
        etvPressTextColor = typedArray.getColor(R.styleable.EasyTextView_etvPressTextColor,0);
        etvNormalTextColor = typedArray.getColor(R.styleable.EasyTextView_etvNormalTextColor,0);
        etvPressBackgroundColor = typedArray.getColor(R.styleable.EasyTextView_etvPressBackgroundColor,-1);
        etvNormalBackgroundColor = typedArray.getColor(R.styleable.EasyTextView_etvNormalBackgroundColor,-1);
    }
}
