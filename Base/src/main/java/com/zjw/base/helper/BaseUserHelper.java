package com.zjw.base.helper;

import com.zjw.base.utils.Settings;
import com.zjw.base.utils.ValidateUtils;

/**
 * Created by Frank on 2017/3/31.
 */

public class BaseUserHelper {
    //保存用户信息的 sp字段配置
    public static final String CURRENT_USER_TOKEN= "current_user_token";

    //获取用户登录保存的标识（key，token 各种叫法）
    public static String getCurrentToken() {
        String token = Settings.getString(CURRENT_USER_TOKEN,null);
        return ValidateUtils.isValidate(token)?token:null;
    }

    //保存当前用户token
    public static void saveCurrentToken(String token){
        Settings.setString(CURRENT_USER_TOKEN, token);
    }

    public static void clearCurrentToken(){
        Settings.setString(CURRENT_USER_TOKEN, "");
    }



}
