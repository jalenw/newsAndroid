package com.zjw.base.utils;

import android.os.AsyncTask;

import com.zjw.base.config.BaseMainApp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 图片压缩工具类
 */
public class CompressImageUtil {

    /**
     * 压缩单张
     * @param imagePath
     * @param listener
     */
    public static void compressSingleImage(String imagePath, final OnCompressListener listener) {
        if (!ValidateUtils.isValidate(imagePath))
            return;
        new AsyncTask<String, String, String>() {

            @Override
            protected String doInBackground(String... params) {
                return ImageUtil.compressImage(params[0]);
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                List<String> imageUrl = new ArrayList<>();
                imageUrl.add(result);
                listener.onCompressOk(imageUrl);
            }
        }.executeOnExecutor(BaseMainApp.getInstance().getExecutor(5), imagePath);
    }


    /**
     * 压缩多张
     * @param imageList
     * @param listener
     */
    public static void compressImageList(List<String> imageList, OnCompressListener listener) {
        String[] params = imageList.toArray(new String[imageList.size()]);

        List<String> imageLocalUrlList = Collections
                .synchronizedList(new ArrayList<String>()); // 压缩图片地址集合

        ExecutorService executorService = Executors.newCachedThreadPool();
        for (String param : params) {
            CompressImageTask task = new CompressImageTask(param,
                    imageLocalUrlList);
            executorService.submit(task);
        }
        executorService.shutdown();
        while (true) {
            if (executorService.isTerminated()) {
                if (listener != null) {
                    listener.onCompressOk(imageLocalUrlList);
                }
                imageLocalUrlList.clear();
                break;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {

            }
        }
    }



    static class CompressImageTask implements Runnable {
        private String path;
        private List<String> list;

        public CompressImageTask(String path, List<String> syncList) {
            super();
            this.path = path;
            this.list = syncList;
        }

        @Override
        public void run() {
            String outputPath = ImageUtil.compressImage(path);
            list.add(outputPath);
        }
    }


    public interface OnCompressListener {
        void onCompressOk(List<String> localUrlList);
    }


}
