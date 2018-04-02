package com.zjw.base.net;

import android.app.Application;
import android.os.Build;
import android.text.TextUtils;
import android.widget.Toast;

import com.luck.picture.lib.entity.LocalMedia;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.zjw.base.config.BasicConfig;
import com.zjw.base.helper.BaseUserHelper;
import com.zjw.base.utils.AuthUtil;
import com.zjw.base.utils.GsonUtils;
import com.zjw.base.utils.LogCatUtil;
import com.zjw.base.utils.ValidateUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import okhttp3.Call;
import okhttp3.Response;


/**
 * Created by intexh on 2016/6/4.
 */
public enum NetworkManager {
    INSTANCE;

    public void init(Application application) {
        //必须调用初始化
        OkGo.init(application);
        OkGo.getInstance()
                .setConnectTimeout(10000)  //全局的连接超时时间 10秒
                .setReadTimeOut(10000)     //全局的读取超时时间 10秒
                .setWriteTimeOut(10000);    //全局的写入超时时间 10秒
    }


    /**
     * 默认所有参数都要签名
     *
     * @param api
     * @param params
     * @param callBack
     */
    public void post(final String api, Map<String, String> params, final OnRequestCallBack callBack) {
        post("", "key", BasicConfig.INSTANCE.getBaseUrl(), api, params, callBack);
    }
    public void postTemp(final String api, Map<String, String> params, final OnRequestCallBack callBack) {
        post("", "key","" , api, params, callBack);
    }

    public void postLive(final String api, Map<String, String> params, final OnRequestCallBack callBack) {
        post("", "key", "http://wenwan.intexh.com/live/index.php", api, params, callBack);
    }

    /**
     * @param tag
     * @param api      请求接口
     * @param callBack
     */
    private void post(String tag, final String tokenName, final String baseUrl, final String api, final Map<String, String> params, final OnRequestCallBack callBack) {
        if (!TextUtils.isEmpty(BaseUserHelper.getCurrentToken())) {
            params.put(tokenName, BaseUserHelper.getCurrentToken());
        }
        params.put("client", "android");
        OkGo.post(baseUrl + api)//
                .tag(tag)//
                .connTimeOut(5000)
                .params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        success(s, tokenName, baseUrl, api, callBack, params);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        error(e, baseUrl, api, params, callBack);
                    }
                });
    }

    /**
     * get请求
     *
     * @param api      请求的接口api
     * @param callBack 回调
     */
    public void get(final String api, final Map<String, String> params, final OnRequestCallBack callBack) {
        if (!TextUtils.isEmpty(BaseUserHelper.getCurrentToken())) {
            params.put("key", BaseUserHelper.getCurrentToken());
        }
        OkGo.get(BasicConfig.INSTANCE.getBaseUrl() + api)
                .params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        success(s, "key", BasicConfig.INSTANCE.getBaseUrl(), api, callBack, params);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        error(e, BasicConfig.INSTANCE.getBaseUrl(), api, params, callBack);
                    }
                });
    }


    private void error(Exception e, String finalBaseUrl, String api, Map<String, String> params, OnRequestCallBack callBack) {
        LogCatUtil.e("okhttp:", "*---------------------------------------------------------------------------*");
        LogCatUtil.e("okhttp:", "requestUrl= " + finalBaseUrl + api);
        LogCatUtil.e("okhttp:", "isSuccess = 请求错误" + e.getMessage());
        LogCatUtil.e("okhttp:", "requestParams： " + GsonUtils.serializedToJson(params));
        LogCatUtil.e("okhttp:", "response= " + e.getMessage());
        LogCatUtil.e("okhttp:", "*                                                                      *");
        callBack.onError(e.getMessage());
    }

    private void success(String s, String tokenName, String finalBaseUrl, String api, OnRequestCallBack callBack, Map<String, String> params) {
        LogCatUtil.e("okhttp:", "*---------------------------------------------------------------------------*");
        LogCatUtil.e("okhttp:", "requestUrl= " + finalBaseUrl + api);
        if (s == null) {
            LogCatUtil.e("okhttp:", "isSuccess = 请求成功 数据返回失败");
            return;
        }
        print(s, params);
        int code = GsonUtils.getIntFromJSON(s, "code");
        if (code == 200) {
            LogCatUtil.e("okhttp:", "isSuccess = 请求成功 数据返回正常");
            callBack.onOk(s);
        } else {
            if (code == 404) {
                EventBus.getDefault().post(new TokenInvalidEvent());
               // callBack.onError(GsonUtils.getStringFromJSON(s, "datas", "error"));
               // return;
            }
            callBack.onError(GsonUtils.getStringFromJSON(s, "datas", "error"));
            LogCatUtil.e("okhttp:", "isSuccess = 请求成功 数据返回失败");
        }
    }

    private void print(String s, Map<String, String> params) {
        LogCatUtil.e("okhttp:", "requestParams： " + GsonUtils.serializedToJson(params));
        LogCatUtil.e("okhttp:", "response= " + s);
        LogCatUtil.e("okhttp:", "*                                                             *");
    }

    /**
     * 上传图片
     */
    public void uploadImage(final String api, String localMedia, final OnRequestCallBack callBack) {
        final HashMap<String, String> params = new HashMap<>();
        params.put("key", BaseUserHelper.getCurrentToken());
        File file = new File(localMedia);
        List<File> files = new ArrayList<>();
        files.add(file);
        OkGo.post(BasicConfig.INSTANCE.getBaseUrl() + api)
                .params(params)
                .addFileParams("data", files)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        success(s, "key", BasicConfig.INSTANCE.getBaseUrl(), api, callBack, params);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                    }
                });

    }
//    public void upLoadImage(final String api, File file, final OnRequestCallBack callBack) {
//        final Map<String, String> params = new HashMap<>();
//        params.put("key", BaseUserHelper.getCurrentToken());
//        params.put("client", "android");
//        OkGo.post(BasicConfig.INSTANCE.getBaseUrl() + api)//
//                .isMultipart(true)       // 强制使用 multipart/form-data 表单上传（只是演示，不需要的话不要设置。默认就是false）
//                .params(params)        // 这里可以上传参数
//                .params("live_image", file)
//                .execute(new StringCallback() {
//                    @Override
//                    public void onSuccess(String s, Call call, Response response) {
//                        //上传成功
//                        success(s, BasicConfig.INSTANCE.getBaseUrl(), api, callBack, params);
//                    }
//
//
//                    @Override
//                    public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
//                        //这里回调上传进度(该回调在主线程,可以直接更新ui)
//                        LogCatUtil.e("frank", currentSize + ":" + totalSize + ":" + progress + ":" + networkSpeed);
//                    }
//
//                    @Override
//                    public void onError(Call call, Response response, Exception e) {
//                        super.onError(call, response, e);
//                        error(e, BasicConfig.INSTANCE.getBaseUrl(), api, params, callBack);
//                    }
//                });
//    }

    /**
     * 临时方法
     */
    private static String getMD5(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (Exception e) {
        }
        byte[] b = messageDigest.digest();
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            int a = b[i];
            if (a < 0)
                a += 256;
            if (a < 16)
                buf.append("0");
            buf.append(Integer.toHexString(a));

        }
        return buf.toString();  //32位
    }

    public static String getTime() {

        long time = System.currentTimeMillis() / 1000;//获取系统时间的10位的时间戳

        String str = String.valueOf(time);

        return str;

    }


    /**
     * 获取签名
     *
     * @param clientDevice
     * @param noiseStr
     * @param userToken
     * @param timeStamp
     * @param params
     * @return
     */
    private String getSignature(String clientDevice, String noiseStr, String userToken, String timeStamp, Map<String, String> params) {
        StringBuilder baseStr = new StringBuilder();
        baseStr.append(BasicConfig.INSTANCE.getClientKey());    //公钥
        baseStr.append(clientDevice);      //设备标识
        baseStr.append(noiseStr);    //Noise 串
        if (userToken != null && userToken.length() > 0) {
            baseStr.append(userToken);//有token才拼接 没有则是匿名请求接口
        }
        baseStr.append(timeStamp);  //utc格式时间
        if (params.size() > 0) {
            Map<String, String> sortParams = sortMapByKey(params);
            for (Map.Entry<String, String> entry : sortParams.entrySet()) {
                baseStr.append(entry.getKey() + "=" + entry.getValue());
            }
        }
        LogCatUtil.e("frank", "签名字符串：" + baseStr.toString());
        return AuthUtil.getSHA1(baseStr.toString());
    }


    /*---------------------------------------------------------------------------------------------------------*/
    private static String getSign(Map<String, String> params, String clientSecret) {
        String str = "";
        List<String> list = new ArrayList<>(params.keySet());
        Collections.sort(list);
        for (int i = 0; i < list.size(); i++) {
            str += list.get(i) + params.get(list.get(i));
        }
        return AuthUtil.getEncrypt(str, clientSecret);
    }

    /**
     * 使用 Map按key进行排序
     *
     * @param map
     * @return
     */
    public static Map<String, String> sortMapByKey(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<String, String> sortMap = new TreeMap<>(
                new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return o1.compareTo(o2);
                    }
                });

        sortMap.putAll(map);

        return sortMap;
    }

    public interface OnRequestCallBack {
        void onOk(String response);

        void onError(String errorMessage);

    }


}
