package com.zjw.base.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 */
public class AuthUtil {


    /**
     * 根据明文和盐值，获取加密后的密文
     *
     * @param str
     * @param salt
     * @return
     */

    public static String getEncrypt(String str, String salt) {

        StringBuilder sb = new StringBuilder(salt);
        String reverseSalt = sb.reverse().toString();
        return md5(mixString(md5(str), reverseSalt).substring(0, 32));

    }

    /**
     * 只需要根据明文加密，获取加密后的密文
     *
     * @param str
     * @return
     */
    public static String getEncrypt(String str) {
        return md5(str);

    }


    /**
     * 梅花间竹混合字符串
     *
     * @param str1
     * @param str2
     * @return
     */

    public static String mixString(String str1, String str2) {

        int len1 = str1.length();

        int len2 = str2.length();

        int minLen = len1 > len2 ? len2 : len1;

        String maxStr = len1 > len2 ? str1 : str2;

        StringBuilder sb = new StringBuilder();

        int index = 0;

        while (index < minLen) {

            sb.append(str1.charAt(index)).append(str2.charAt(index));

            index++;

        }

        sb.append(maxStr.substring(index));

        return sb.toString();
    }

    /**
     * md5加密，生成32位的加密后的字符串
     *
     * @param input 输入参数
     * @return
     */
    public static String md5(String input) {
        String ret = null;
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            byte[] btInput = input.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest(); // MD5 的计算结果是一个 128 位的长整数，共16个字节
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            ret = new String(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 获取sha1 散列值
     *
     * @param val
     * @return
     */
    public static String getSHA1(String val) {
        return shaEncrypt(val,"SHA-1");
    }

    /**
     * SHA加密
     *
     * @param strSrc
     *            明文
     * @return 加密之后的密文
     */
    public static String shaEncrypt(String strSrc,String type) {
        MessageDigest md = null;
        String strDes = null;
        byte[] bt = strSrc.getBytes();
        try {
            md = MessageDigest.getInstance(type);// 将此换成SHA-1、SHA-512、SHA-384等参数
            md.update(bt);
            strDes = bytes2Hex(md.digest()); // to HexString
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        return strDes;
    }

    /**
     * byte数组转换为16进制字符串
     *
     * @param bts
     *            数据源
     * @return 16进制字符串
     */
    public static String bytes2Hex(byte[] bts) {
        String des = "";
        String tmp = null;
        for (int i = 0; i < bts.length; i++) {
            tmp = (Integer.toHexString(bts[i] & 0xFF));
            if (tmp.length() == 1) {
                des += "0";
            }
            des += tmp;
        }
        return des;
    }

}
