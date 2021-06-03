package com.xlk.paperlesstl.util;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.provider.Settings;

import com.blankj.utilcode.util.AppUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * @author xlk
 * @date 2020/3/9
 * @desc
 */
public class AppUtil {

    /**
     * 获取手机的唯一标识符
     * AndroidId 和 Serial Number结合使用
     *
     * @param context
     * @return
     */
    public static String getUniqueId(Context context) {
        String androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String id = androidID + Build.SERIAL;
        try {
            return toMD5(id);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return id;
        }
    }

    /**
     * 字符串md5加密
     *
     * @param text 要加密的字符串
     * @return 加密后的字符串
     * @throws NoSuchAlgorithmException 算法异常
     */
    private static String toMD5(String text) throws NoSuchAlgorithmException {
        //获取摘要器 MessageDigest
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        //通过摘要器对字符串的二进制字节数组进行hash计算
        byte[] digest = messageDigest.digest(text.getBytes());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digest.length; i++) {
            //循环每个字符 将计算结果转化为正整数;
            int digestInt = digest[i] & 0xff;
            //将10进制转化为较短的16进制
            String hexString = Integer.toHexString(digestInt);
            //转化结果如果是个位数会省略0,因此判断并补0
            if (hexString.length() < 2) {
                sb.append(0);
            }
            //将循环结果添加到缓冲区
            sb.append(hexString);
        }
        //返回整个结果
        return sb.toString();
    }


    public static void getCameraConfig(){

    }
    /**
     * 判断是否有指定摄像头
     * @param context 上下文
     * @param type    =0检查是否有后置摄像头，=1检查是否有前置摄像头
     * @return @return {@code true}: 有<br>{@code false}: 没有
     */
    public static boolean checkCamera(Context context, int type) {
        try {
            // 不兼容Android 5.0以下版本
            CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            String[] cameraIds = manager.getCameraIdList();
            if (cameraIds.length > 0) {
                if (type == 0) {
                    //后置摄像头
                    return cameraIds[0] != null;
                } else {
                    //后置摄像头
                    return cameraIds[1] != null;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return false;
    }
}
