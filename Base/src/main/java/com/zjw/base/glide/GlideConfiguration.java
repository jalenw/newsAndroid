/*
package com.zjw.base.glide;

import android.content.Context;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.AppGlideModule;

import java.io.InputStream;

@GlideModule
public class GlideConfiguration extends AppGlideModule {

    //通过GlideBuilder设置默认的结构(Engine,BitmapPool ,ArrayPool,MemoryCache等等).
    @Override  
    public void applyOptions(Context context, GlideBuilder builder) {
        // Apply options to the builder here.
//        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
        //重新设置内存限制
        builder.setMemoryCache(new LruResourceCache(10*1024*1024));
    }

    */
/**
     * 清单解析的开启
     *
     * 这里不开启，避免添加相同的modules两次
     * @return
     *//*

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

    @Override
    public void registerComponents(Context context, Registry registry) {
        registry.append(String.class, InputStream.class,new CustomBaseGlideUrlLoader.Factory());
    }

    */
/**
     * 说明：
     * Glide默认的Bitmap格式是RGB_565 ，比ARGB_8888格式的内存开销要小一半
     * Glide加载的图片质量要差于Picasso （看不出
     * 如果你对默认的RGB_565效果还比较满意，可以不做任何事，但是如果你觉得难以接受，
     * 可以创建一个新的GlideModule将Bitmap格式转换到ARGB_8888）
     * 同时在AndroidManifest.xml中将GlideModule定义为meta-data（当前）
     * <meta-data android:name="com.zjw.base.glide.GlideConfiguration"
     android:value="GlideModule"/>
     *//*


}  */
