package com.zjw.base.utils;

import java.util.UUID;

/**
 * Created by Fearless on 16/8/12.
 */
public class UUIDUtil {

    private static long num=0;

    /**
     * 随机生成UUID
     * @return
     */
    public static synchronized String getUUID(){
        UUID uuid=UUID.randomUUID();
        String str = uuid.toString();
        String uuidStr=str.replace("-", "");
        return uuidStr;
    }
    /**
     * 根据字符串生成固定UUID
     * @param name
     * @return
     */
    public static synchronized String getUUID(String name){
        UUID uuid=UUID.nameUUIDFromBytes(name.getBytes());
        String str = uuid.toString();
        String uuidStr=str.replace("-", "");
        return uuidStr;
    }

}
