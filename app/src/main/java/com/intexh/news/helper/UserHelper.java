package com.intexh.news.helper;


import com.google.gson.reflect.TypeToken;
import com.intexh.news.moudle.main.bean.AreaBean;
import com.intexh.news.moudle.mine.bean.LoginBean;
import com.intexh.news.moudle.news.bean.TabBean;
import com.zjw.base.helper.BaseUserHelper;
import com.zjw.base.utils.GsonUtils;
import com.zjw.base.utils.Settings;

import java.util.List;


public class UserHelper extends BaseUserHelper {

    public static final String CURRENT_USER_INFO = "current_user_info";
    public static final String CURRENT_LIGHT_MODE = "current_light_mode";
    public static final String CURRENT_TEXT_MODE = "current_text_mode";
    public static final String CURRENT_JPUSH_MODE = "current_jpush_mode";
    public static final String CURRENT_NEWS_REDRED = "current_news_readed";
    public static final String SHOW_INTRODUCTION_PIC = "show_introduction_pic";
    public static final String CURRENT_CITY_NAME = "current_city_name";
    public static final String CURRENT_ADSBEANS = "current_ads_beans";
    public static final String CURRENT_NEWSBEANS = "current_news_beans";
    public static final String CURRENT_DEDCRo= "CURRENT_DEDCRo";
    private static final String CURRENT_NETWORK_STATE = "CURRENT_NETWORK_STATE";


    public static void clearUserInfo() {
        Settings.setString(CURRENT_USER_INFO, "");
    }

    public static void saverUserInfo(LoginBean bean) {
        Settings.setString(CURRENT_USER_INFO, GsonUtils.serializedToJson(bean));
    }

    public static LoginBean getUserInfo() {
        LoginBean userBean = GsonUtils.deSerializedFromJson(Settings.getString(CURRENT_USER_INFO, ""), LoginBean.class);
        return userBean;
    }

    public static void saverLightMode(String mode) {
        Settings.setString(CURRENT_LIGHT_MODE, mode);
    }

    public static String getLightMode() {
        String currentMode = Settings.getString(CURRENT_LIGHT_MODE, "");
        return currentMode;
    }

    public static void saverTextMode(double mode) {
        Settings.setDouble(CURRENT_TEXT_MODE, mode);
    }

    public static double getTextMode() {
        double currentMode = Settings.getDouble(CURRENT_TEXT_MODE, 1.0);
        return currentMode;
    }

    public static void saverJPUSHMode(boolean mode) {
        Settings.setBoolean(CURRENT_JPUSH_MODE, mode);
    }

    public static boolean getJPUSHMode() {
        boolean currentMode = Settings.getBoolean(CURRENT_JPUSH_MODE, true);
        return currentMode;
    }

    public static void saverReadedNEws(String newsID) {
        Settings.setString(CURRENT_NEWS_REDRED, newsID);
    }

    public static String getReadedNews() {
        String newsIDs = Settings.getString(CURRENT_NEWS_REDRED, "");
        return newsIDs;
    }

    public static void saveShowInTrodructPic(boolean hasShown) {
        Settings.setBoolean(SHOW_INTRODUCTION_PIC, hasShown);
    }

    public static boolean getShowInTrodructPic() {
        boolean hasShown = Settings.getBoolean(SHOW_INTRODUCTION_PIC, false);
        return hasShown;
    }

    public static void saverCity(AreaBean bean) {
        Settings.setString(CURRENT_CITY_NAME, GsonUtils.serializedToJson(bean));
    }

    public static AreaBean getCity() {
        AreaBean userBean = GsonUtils.deSerializedFromJson(Settings.getString(CURRENT_CITY_NAME, ""), AreaBean.class);
        return userBean;
    }

    public static void saveNewsBeans(List<TabBean> infoBeans, String id) {
        Settings.setString(CURRENT_NEWSBEANS + id, GsonUtils.serializedToJson(infoBeans));
    }

    public static List<TabBean> getNewsBeans(String id) {
        List<TabBean> beans = GsonUtils.deSerializedFromJson(Settings.getString(CURRENT_NEWSBEANS + id, ""), new TypeToken<List<TabBean>>() {
        }.getType());
        return beans;
    }
    public static void saveDecro( boolean hasdeCRo,String id) {
        Settings.setBoolean(CURRENT_DEDCRo + id, hasdeCRo);
    }

    public static boolean getDecro(String id) {
       boolean beans = Settings.getBoolean(CURRENT_DEDCRo + id, false);
        return beans;
    }
    public static void setCurrentNetworkState(int networkState) {
        Settings.setInt(CURRENT_NETWORK_STATE, networkState);
    }

    public static int getCurrentNetworkState() {
        int networkState = Settings.getInt(CURRENT_NETWORK_STATE, -1);
        return networkState;
    }
}
