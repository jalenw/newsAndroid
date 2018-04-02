package com.zjw.base.utils;

import android.text.TextUtils;

import java.util.List;


/**
 * 验证工具类 3.0
 *
 * @author cp
 */
public class ValidateUtils {

    private static final String EMAIL_VALIDATION_REGEX = "^[\\w\\-\\+\\.]+@[\\w\\-]+\\.[A-Za-z]{2,}$";

    public static boolean isValidate(String str) {
        return !(TextUtils.isEmpty(str));
    }


    public static <T> boolean isValidate(List<T> list) {
        if (list != null && list.size() > 0) {
            return true;
        }
        return false;
    }


    public static <T> boolean isValidate(T t) {
        return t == null ? false : true;
    }

    public static <T> boolean isValidate(int i) {
        return i == 0 ? false : true;
    }


    public static boolean isValidPhoneNumber(String phone) {
        return !TextUtils.isEmpty(phone) && phone.matches("^\\+?[0-9]{11}$");
    }


    public static boolean isValidEmail(String emailAddress) {
        return emailAddress.matches(EMAIL_VALIDATION_REGEX);
    }

    public static boolean isValidAccount(String account) {
        return isValidPhoneNumber(account) && isValidEmail(account);
    }


}
