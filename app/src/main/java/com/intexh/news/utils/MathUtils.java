package com.intexh.news.utils;

/**
 * Created by Frank on 2017/8/20.
 */

public class MathUtils {

    //是否是数字
    public static boolean isNumeric(String str){
        for (int i = 0; i < str.length(); i++){
            System.out.println(str.charAt(i));
            if (!Character.isDigit(str.charAt(i))){
                return false;
            }
        }
        return true;
    }
}
