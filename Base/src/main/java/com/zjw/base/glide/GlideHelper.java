package com.zjw.base.glide;

import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.zjw.base.R;
import com.zjw.base.config.BaseMainApp;
import com.zjw.base.utils.UIUtil;

/**
 * Created by Frank on 2017/3/31.
 */

public enum GlideHelper {
    INSTANCE;

    public static final int GLIDE_CIRCLE = 1;
    public static final int GLIDE_ROUND = 2;

    /**
     * 加载普通的方形图片
     *
     * @param imageView
     * @param url
     */
    public void loadImage(ImageView imageView, String url) {
        if(url!=null && url.length()>0 &&(url.contains("http:")||url.contains("https:")||url.contains("file:"))){
            RequestOptions options = new RequestOptions();
//                    .placeholder(R.drawable.nopic_big) ;
            Glide.with(BaseMainApp.getAppContext())
                    .load(url)
                    .apply(options)
                    .into(imageView);
        }
    }
    /**
     * 加载视频缩略图
     *
     * @param imageView
     * @param url
     */
    public void loadVideoThumbImage(ImageView imageView, String url) {
        if(url!=null && url.length()>0 &&(url.contains("http://")||url.contains("https://")||url.contains("file://"))){
            RequestOptions options = new RequestOptions()
                    .centerCrop();
//                    .error(R.drawable.nopic_big)
//                    .placeholder(R.drawable.nopic_big) ;
            Glide.with(BaseMainApp.getAppContext())
                    .load(url)
                    .apply(options)
                    .into(imageView);
        }
    }
    //本地
    public void loadImage(ImageView imageView, int resIcon) {
        Glide.with(BaseMainApp.getAppContext())
                .load(resIcon)
                .into(imageView);
    }

    /**
     * 加载圆角图片
     *
     * @param imageView
     * @param url
     * @param radius    切角半径
     */
    public void loadImage(ImageView imageView, String url, int radius) {
        if(url!=null && url.length()>0 &&(url.contains("http://")||url.contains("https://")||url.contains("file:///"))){
            loadImage(imageView, url, GLIDE_ROUND, radius);
        }
    }
    //本地
    public void loadImage(ImageView imageView, int imageResId, int radius) {
        loadLocalImge(imageView, imageResId, GLIDE_ROUND, radius);
    }

    //网络图片控制尺寸
    public void loadImageSize(ImageView imageView, String url, int width, int height) {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .override(width,height);
        Glide.with(BaseMainApp.getAppContext())
                .load(url)
                .apply(options)
                .into(imageView);
    }

    /**
     * 加载圆形图片
     *
     * @param imageView
     * @param url
     */
    public void loadCircleImage(ImageView imageView, String url) {
        if(url!=null && url.length()>0 &&(url.contains("http://")||url.contains("https://")||url.contains("file:///"))){
            loadImage(imageView, url, GLIDE_CIRCLE, 0);
        }
    }

    //本地
    public void loadCircleImage(ImageView imageView, int imageResId) {
        loadLocalImge(imageView, imageResId, GLIDE_CIRCLE, 0);
    }

    //网络圆角或切角
    private void loadImage(ImageView imageView, String url, int type, int radius) {
        Glide.with(BaseMainApp.getAppContext())
                .load(url.contains("file:///")?url:url)
                .apply(getType(type,radius))
                .into(imageView);
    }

    private RequestOptions getType(int type) {
        return getType(type,0);
    }
    private RequestOptions getType(int type,int radius) {
        RequestOptions options = new RequestOptions();
        options.centerCrop();
        if(type == GLIDE_CIRCLE){
            options.circleCrop().placeholder(R.drawable.head_icon);   //4.0版本圆形
        }else{
            options.transform(new RoundedCorners(UIUtil.dp2px(BaseMainApp.getAppContext(),radius))); //4.0版本圆角
        }
        return options;
    }

    //本地圆角或切角
    private void loadLocalImge(ImageView imageView, int imageResId, int type, int radius) {
        Glide.with(BaseMainApp.getAppContext())
                .load(imageResId)
                .apply(getType(type,radius))
                .into(imageView);
    }

    //本地圆角或切角
    public void loadLocalImage(ImageView imageView, String url, int type, int radius) {
        Glide.with(BaseMainApp.getAppContext())
                .load("file:///"+url)
                .apply(getType(type,radius))
                .into(imageView);
    }


    //验证图片地址
   /* public GlideUrl getGlideUrl(String url) {
        //Authorization 请求头信息
        LazyHeaders headers = new LazyHeaders.Builder()
                .addHeader("Authorization", "http://cdn.dotasell.com")
                .build();
        return new GlideUrl(url, headers);
    }*/


}
