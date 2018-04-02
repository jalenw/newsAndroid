package com.zjw.base.utils;


import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;


import com.zjw.base.config.BaseMainApp;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;


/**
 * @author Milk (QQ: 249828165) 字符串处理类
 */
public class StringUtil {

    public static String getStringResources(int rId) {
        if (BaseMainApp.getInstance() != null) {
            return BaseMainApp.getAppContext().getResources().getString(rId);
        } else {
            return "";
        }
    }


    public static String toString(Object value) {
        if (value instanceof String) {
            return (String) value;
        } else if (value != null) {
            return String.valueOf(value);
        }
        return null;
    }

    /**
     * 注意不要使用java的string.split或是apache commons-langs的StringUtils.split
     *
     * @param s
     * @param delimiter
     * @return
     */
    public static String[] explode(String s, String delimiter) {
        if (s == null) {
            return null;
        }
        int delimiterLength;
        int stringLength = s.length();
        if (delimiter == null || (delimiterLength = delimiter.length()) == 0) {
            return new String[]{s};
        }

        // a two pass solution is used because a one pass solution would
        // require the possible resizing and copying of memory structures
        // In the worst case it would have to be resized n times with each
        // resize having a O(n) copy leading to an O(n^2) algorithm.

        int count;
        int start;
        int end;

        // Scan s and count the tokens.
        count = 0;
        start = 0;
        while ((end = s.indexOf(delimiter, start)) != -1) {
            count++;
            start = end + delimiterLength;
        }
        count++;

        // allocate an array to return the tokens,
        // we now know how big it should be
        String[] result = new String[count];

        // Scan s again, but this time pick out the tokens
        count = 0;
        start = 0;
        while ((end = s.indexOf(delimiter, start)) != -1) {
            result[count] = s.substring(start, end);
            count++;
            start = end + delimiterLength;
        }
        end = stringLength;
        result[count] = s.substring(start, end);

        return result;
    }

    public static String join(Iterable<?> list, String separator) {
        StringBuilder sb = new StringBuilder();
        if (list == null)
            return sb.toString();

        for (Object obj : list) {
            if (sb.length() > 0)
                sb.append(separator);
            sb.append(obj);
        }

        return sb.toString();
    }

    public static String md5(String str) {
        MessageDigest messageDigest = null;

        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        messageDigest.reset();
        try {
            messageDigest.update(str.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        byte[] byteArray = messageDigest.digest();

        StringBuffer md5StrBuff = new StringBuffer();

        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }

        return md5StrBuff.toString();
    }

    public static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        t.printStackTrace(pw);
        pw.flush();
        sw.flush();
        return sw.toString();
    }

    public static String addslashes(String str) {
        /**
         "\0" => "",
         "'"  => "&#39;",
         "\"" => "&#34;",
         "\\" => "&#92;",
         // more secure
         "<"  => "&lt;",
         ">"  => "&gt;",
         */
        StringBuilder sb = new StringBuilder();
        int len = str.length();
        char c;
        for (int i = 0; i < len; i++) {
            c = str.charAt(i);
            switch (c) {
                case '\0':
                    continue;
                case '\\':
                    sb.append("\\");
                    continue;
                case '\'':
                    sb.append("\\'");
                    continue;
                case '"':
                    sb.append("\\\"");
                    continue;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * @param word 输入的内容
     * @param key  在内容中需要改变颜色的关键字
     * @return
     */
    public static CharSequence changeColor(String word, String key, int color) {
        if (TextUtils.isEmpty(key)) {
            return word;
        }
        if (word.contains(key)) {
            int index = word.indexOf(key);
            int len = key.length();
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(word);
            stringBuilder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(BaseMainApp.getAppContext(), color)), index, (index + len), Spannable.SPAN_EXCLUSIVE_INCLUSIVE); // 设置指定位置文字的颜色
            return stringBuilder;
        } else {
            return word;
        }
    }

    /**
     * 格式化数字为千分位显示；
     *
     * @return
     */
    public static String fmtMicrometer(String text) {
        BigDecimal bd=new BigDecimal(text);
        DecimalFormat df=new DecimalFormat(",###,##0.000");
        return df.format(bd);
    }

}
