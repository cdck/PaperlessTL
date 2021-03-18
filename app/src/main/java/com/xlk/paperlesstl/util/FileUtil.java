package com.xlk.paperlesstl.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.xlk.paperlesstl.App;
import com.xlk.paperlesstl.BuildConfig;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.jni.JniHelper;
import com.xlk.paperlesstl.model.Constant;
import com.xlk.paperlesstl.model.GlobalValue;
import com.xlk.paperlesstl.model.WpsModel;
import com.xlk.paperlesstl.model.data.EventMessage;
import com.xlk.paperlesstl.model.data.EventType;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

import androidx.core.content.FileProvider;

/**
 * @author Created by xlk on 2021/3/2.
 * @desc
 */
public class FileUtil {
    private static final String TAG = "FileUtil-->";


    public static boolean isDoc(String fileName) {
        if (TextUtils.isEmpty(fileName)) return false;
        return fileName.endsWith(".doc") ||
                fileName.endsWith(".docx") ||
                fileName.endsWith(".txt") ||
                fileName.endsWith(".log") ||
                fileName.endsWith(".pdf");
    }

    public static boolean isPPT(String fileName) {
        if (TextUtils.isEmpty(fileName)) return false;
        return fileName.endsWith(".pptx") ||
                fileName.endsWith(".ppt");
    }

    public static boolean isXLS(String fileName) {
        if (TextUtils.isEmpty(fileName)) return false;
        return fileName.endsWith(".xls") ||
                fileName.endsWith(".xlsx");
    }

    public static boolean isDocument(String fileName) {
        if (TextUtils.isEmpty(fileName)) return false;
        return isDoc(fileName) || isPPT(fileName) || isXLS(fileName);
    }

    public static boolean isPicture(String fileName) {
        if (TextUtils.isEmpty(fileName)) return false;
        return fileName.endsWith(".jpg") ||
                fileName.endsWith(".png") ||
                fileName.endsWith(".gif") ||
                fileName.endsWith(".img") ||
                fileName.endsWith(".img") ||
                fileName.endsWith(".jpeg");
    }

    public static boolean isAudio(String fileName) {
        if (TextUtils.isEmpty(fileName)) return false;
        return fileName.endsWith(".mp4") ||
                fileName.endsWith(".mp3") ||
                fileName.endsWith(".3gp") ||
                fileName.endsWith(".rmvb") ||
                fileName.endsWith(".avi") ||
                fileName.endsWith(".mkv") ||
                fileName.endsWith(".flv");
    }

    public static boolean isVideo(String fileName){
        if (TextUtils.isEmpty(fileName)) return false;
        return fileName.endsWith(".mp4") ||
                fileName.endsWith(".3gp") ||
                fileName.endsWith(".rmvb") ||
                fileName.endsWith(".avi") ||
                fileName.endsWith(".mkv") ||
                fileName.endsWith(".flv");

    }


    public static boolean isOther(String fileName) {
        if (TextUtils.isEmpty(fileName)) return false;
        return !isDocument(fileName) && !isPicture(fileName) && !isAudio(fileName);
    }

    public static void openFile(Context context, String dir, String filename, int mediaid) {
        FileUtils.createOrExistsDir(dir);
        String pathname = dir + filename;
        File file = new File(pathname);
        if (!file.exists()) {
            LogUtils.d(TAG, "openFile -->" + "下载将要打开的文件 pathname= " + pathname);
            JniHelper.getInstance().creationFileDownload(pathname, mediaid, 1, 0,
                    Constant.DOWNLOAD_SHOULD_OPEN_FILE);
        } else {
            if (GlobalValue.downloadingFiles.contains(mediaid)) {
                ToastUtils.showShort(R.string.currently_downloading);
            } else {
                openFile(context, file);
            }
        }
    }

    public static void openFile(Context context, File file) {
        LogUtils.e(TAG, "打开文件：" + file.getAbsolutePath());
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        String fileName = file.getName();
        if (FileUtil.isAudio(fileName)) {
            return;
        } else if (FileUtil.isPicture(fileName)) {
            EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_PREVIEW_IMAGE).objects(Constant.DOWNLOAD_DIR + fileName).build());
            return;
        } else if (FileUtil.isDocument(fileName)) {
            //通知注册WPS广播
            EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_WPS_RECEIVER).objects(true).build());
            Bundle bundle = new Bundle();
            bundle.putString(WpsModel.OPEN_MODE, WpsModel.OpenMode.NORMAL); // 打开模式
//            bundle.putBoolean(WpsModel.ENTER_REVISE_MODE, true); // 以修订模式打开文档

            bundle.putBoolean(WpsModel.SEND_CLOSE_BROAD, true); // 文件关闭时是否发送广播
            bundle.putBoolean(WpsModel.SEND_SAVE_BROAD, true); // 文件保存时是否发送广播
            bundle.putBoolean(WpsModel.HOMEKEY_DOWN, true); // 单机home键是否发送广播
            bundle.putBoolean(WpsModel.BACKKEY_DOWN, true); // 单机back键是否发送广播

            bundle.putBoolean(WpsModel.SAVE_PATH, true); // 文件这次保存的路径
            bundle.putString(WpsModel.THIRD_PACKAGE, WpsModel.PackageName.NORMAL); // 第三方应用的包名，用于对改应用合法性的验证
//            bundle.putBoolean(WpsModel.CLEAR_TRACE, true);// 清除打开记录
//            bundle.putBoolean(CLEAR_FILE, true); //关闭后删除打开文件
            intent.setClassName(WpsModel.PackageName.NORMAL, WpsModel.ClassName.NORMAL);
            intent.putExtras(bundle);
        }
        uriX(context, intent, Constant.DOWNLOAD_DIR + fileName);
    }

    /**
     * 抽取成工具方法
     *
     * @param context
     * @param intent
     * @param filepath
     */
    public static void uriX(Context context, Intent intent, String filepath) {
        File file = new File(filepath);
        if (Build.VERSION.SDK_INT > 23) {//android 7.0以上时，URI不能直接暴露
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri uriForFile = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", file);
            intent.setDataAndType(uriForFile, "application/vnd.android.package-archive");
        } else {
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        }
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            ToastUtils.showShort(R.string.no_wps_software_found);
            e.printStackTrace();
        }
    }


    /**
     * 自动转换文件大小 22B 22KB 22MB 22GB
     *
     * @param fileS 文件的大小 file.size() 获取的值
     * @return
     */
    public static String formatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    /**
     * 根据文件名判断文件类型
     *
     * @param context  上下文
     * @param fileName 文件名
     */
    public static String getFileType(Context context, String fileName) {
        if (isDocument(fileName)) {
            return context.getString(R.string.documentation);
        } else if (isPicture(fileName)) {
            return context.getString(R.string.picture);
        } else if (isAudio(fileName)) {
            return context.getString(R.string.video);
        } else {
            return context.getString(R.string.other);
        }
    }

    /**
     * 删除文件、删除目录和子目录文件
     *
     * @param filePath 文件或目录路径
     */
    public static void delDirFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File subFile : files) {
                delDirFile(subFile.getAbsolutePath());
            }
            file.delete();
        } else {
            file.delete();
//            delFileByPath(filePath);
        }
        LogUtils.i(TAG, "delDirFile 删除成功=" + filePath);
    }

    /**
     * 读取文件内容
     *
     * @param action      读取完成后EventBus发送时的type
     * @param strFilePath txt文件的路径
     */
    public static void readTxtFile(int action, String strFilePath) {
        App.threadPool.execute(new Runnable() {
            @Override
            public void run() {
                long l = System.currentTimeMillis();
                String path = strFilePath;
                //文件内容字符串
                String content = "";
                //打开文件
                File file = new File(path);
                //如果path是传递过来的参数，可以做一个非目录的判断
                if (file.isDirectory()) {
                    Log.d(TAG, "readTxtFile The File doesn't not exist.");
                } else {
                    try {
                        InputStream instream = new FileInputStream(file);
                        if (instream != null) {
                            InputStreamReader inputreader = new InputStreamReader(instream);
                            BufferedReader buffreader = new BufferedReader(inputreader);
                            String line;
                            //分行读取
                            while ((line = buffreader.readLine()) != null) {
                                content += line + "\n";
                            }
                            instream.close();
                        }
                    } catch (java.io.FileNotFoundException e) {
                        Log.d(TAG, "readTxtFile The File doesn't not exist.");
                    } catch (IOException e) {
                        Log.d(TAG, "readTxtFile " + e.getMessage());
                    }
                }
                LogUtils.i(TAG, "readTxtFile 用时：" + (System.currentTimeMillis() - l));
                EventBus.getDefault().post(new EventMessage.Builder().type(action).objects(content).build());
            }
        });
    }
    /**
     * 将BitMap保存到指定目录下
     *
     * @param bitmap
     * @param file
     */
    public static void saveBitmap(Bitmap bitmap, File file) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bitmap != null && !bitmap.isRecycled()) {
                LogUtils.e(TAG, "saveBitmap :  回收 --> ");
                bitmap.recycle();
            }
        }
    }
    /**
     * 文件名是否合法
     *
     * @param fileName 传入不包含后缀
     * @return
     */
    public static boolean isLegalName(String fileName) {
        String regex = "[^\\s\\\\/:\\*\\?\\\"<>\\|](\\x20|[^\\s\\\\/:\\*\\?\\\"<>\\|])*[^\\s\\\\/:\\*\\?\\\"<>\\|\\.]$";
        return fileName.matches(regex);
    }
}
