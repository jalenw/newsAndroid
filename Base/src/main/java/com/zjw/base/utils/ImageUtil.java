package com.zjw.base.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;

import com.zjw.base.config.BaseMainApp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class ImageUtil {
    private final static String TAG = "ImageUtil";
    private final static int DEFAULT_BITMAP_COMPRESS_OPTIONS = 87; // 默认的bimap压缩质量等级
    // 1~100


    public static String compressImage(String path) {
        return compressImage(path,
                UIUtil.getWindowWidth(BaseMainApp.getAppContext()));
    }

    /**
     * 根据设置宽度按比例压缩图片
     * @param path
     * @param width
     * @return
     */
    public static String compressImage(String path, int width) {
        Bitmap smallerBitMap = ImageUtil.decodeSampledBitmapFromFile(BaseMainApp.getAppContext(), path, width);
        File dir = new File(Environment.getExternalStorageDirectory().getPath()
                + File.separator + "AppPhone" + File.separator + "image");
        dir.mkdirs();
        String outputPath = dir.getAbsoluteFile() + File.separator
                + UUID.randomUUID().toString() + ".png";
        boolean isSaveImageOk = ImageUtil.saveBitmapToFile(smallerBitMap,
                outputPath);
        if (!isSaveImageOk) { // 如果存储失败,这存至临时文件夹
            String files = Environment.getExternalStorageDirectory().getPath()
                    + File.separator + "AppPhone" + File.separator + "tmp";
            File dirs = new File(files);
            dirs.mkdirs();
            outputPath = dirs.getAbsoluteFile() + File.separator
                    + UUID.randomUUID().toString() + ".png";
            ImageUtil.saveBitmapToFile(smallerBitMap, outputPath);
        }
        recycleBitmap(smallerBitMap);
        return outputPath;
    }

    /**
     * 图片压缩方法：
     *
     * @param context  appcontext
     * @param file     路径
     * @param reqWidth 传屏幕宽度
     * @return
     */
    @SuppressLint("NewApi")
    public static Bitmap decodeSampledBitmapFromFile(Context context,
                                                     String file, int reqWidth) {
        int degree = readPictureDegree(file);
        Bitmap bitmap = null;
        try {
            bitmap = compressBitmapNew(file, reqWidth);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rotateImageView(degree, bitmap);
    }

    /**
     * 旋转图片
     *
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotateImageView(int angle, Bitmap bitmap) throws OutOfMemoryError {
        if (bitmap == null)
            return null;
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
    }

    /**
     * 保存Bitmap到指定的文件
     *
     * @param path
     * @param mBitmap
     */
    // @SuppressLint("NewApi")
    public static boolean saveBitmapToFile(Bitmap mBitmap, String path) {
        File file = new File(path);
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs())
                return false;
        }
        FileOutputStream fOut = null;
        try {
            String ext = path.substring(path.lastIndexOf('.') + 1)
                    .toLowerCase();
            Bitmap.CompressFormat format = "png".equals(ext) ? Bitmap.CompressFormat.PNG
                    : Bitmap.CompressFormat.JPEG;
            fOut = new FileOutputStream(file);
            mBitmap.compress(format, DEFAULT_BITMAP_COMPRESS_OPTIONS, fOut);
            // Log.i("ImageUtil","save："+mBitmap.getByteCount());
            fOut.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeOutputStream(fOut);
        }
    }


    /**
     * 回收bitmap
     */
    public static void recycleBitmap(Bitmap bitmap) {
        // 先判断是否已经回收
        if (bitmap != null && !bitmap.isRecycled()) {
            // 回收并且置为null
            bitmap.recycle();
        }
        System.gc();
    }

    /**
     * 关闭输出流
     */
    private static void closeOutputStream(OutputStream out) {
        try {
            if (out != null)
                out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭输入流
     */
    private static void closeInputStream(InputStream in) {
        try {
            if (in != null)
                in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 图片按比例压缩
     *
     * @param path
     * @param size
     * @return
     * @throws IOException
     * @author yusucheng
     */
    public static Bitmap compressBitmapNew(String path, int size)
            throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        // 生成压缩的图片
        int i = 0;
        Bitmap bitmap;
        while (true) {
            // 这一步是根据要设置的大小，使宽和高都能满足
            if ((options.outWidth >> i <= size)
                    && (options.outHeight >> i <= size)) {
                // 这个参数表示 新生成的图片为原始图片的几分之一。
                options.inSampleSize = (int) Math.pow(2.0D, i);
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                options.inDither = true;
                // 这里之前设置为了true，所以要改为false，否则就创建不出图片
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeFile(path, options);
                break;
            }
            i += 1;
        }
        return bitmap;
    }


    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

}