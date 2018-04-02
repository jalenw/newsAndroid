package com.zjw.base.config;

import android.content.Context;

import com.zjw.base.utils.AppUtil;

/**
 * Created by Frank on 2017/3/25.
 * 全局配置  开发模式 环境 公共key等
 * 上线前注意修改 清单文件中的环信以及地图的key为对应的环境
 */

public enum BasicConfig {
    INSTANCE;

    /*-----运行类型（此项目忽略环境）-----*/
    private int launchType =1;  //1开发环境 2上线环境

    /*-----开发环境-----*/
    private String BASE_URL_DEV= "http://app.24xuanbao.com/mobile/index.php";   //测试环境:"http://news.intexh.com/mobile/index.php"  正式环境:"http://app.24xuanbao.com/mobile/index.php"     //服务器接口地址
    private String CLIENT_KEY_DEV = ""; //公约

    /*-----正式上线环境 与开发环境一致-----*/
    private String BASE_URL_PRO = BASE_URL_DEV;          //服务器接口地址
    private String CLIENT_KEY_PRO = CLIENT_KEY_DEV;

    /*-----leanCloudChatKit app key(聊天室appKey)-----*/
    public final String LC_APP_ID = "z1L9EnUFgsj9zyIEiknIzyaC-gzGzoHsz";
    public final String LC_APP_KEY = "QxdbWE9RJHk0sL3LjcF96KjK";

    private boolean isDebug;    //是否是debug模式


    public void init(Context context){
        isDebug = AppUtil.isApkDebugable(context);
    }

    public boolean isDebug() {
        return isDebug; //是否是debug模式
    }

    /**
     * 获取基础接口地址
     * @return
     */
    public String getBaseUrl(){
        return BASE_URL_PRO;
    }

    /**
     * 获取公钥
     * @return
     */
    public String getClientKey(){
        return CLIENT_KEY_PRO;
    }



}
