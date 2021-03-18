package com.xlk.paperlesstl.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.blankj.utilcode.util.LogUtils;
import com.xlk.paperlesstl.jni.JniHelper;
import com.xlk.paperlesstl.model.Constant;
import com.xlk.paperlesstl.model.GlobalValue;
import com.xlk.paperlesstl.model.WpsModel;
import com.xlk.paperlesstl.model.data.EventMessage;
import com.xlk.paperlesstl.model.data.EventType;
import com.xlk.paperlesstl.view.admin.activity.AdminActivity;
import com.xlk.paperlesstl.view.meet.MeetingActivity;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

/**
 * @author xlk
 * @date 2020/4/26
 * @desc wps 文件处理的广播
 */
public class WpsReceiver extends BroadcastReceiver {
    private final String TAG = "WpsReceiver-->";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) return;
        switch (action) {
            //关闭文件时的广播
            case WpsModel.Reciver.ACTION_CLOSE:
                //通知注销掉WPS广播
                EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_WPS_RECEIVER).objects(false).build());
                String closeFile = intent.getStringExtra(WpsModel.ReciverExtra.CLOSEFILE);
                String thirdPackage1 = intent.getStringExtra(WpsModel.ReciverExtra.THIRDPACKAGE);
                LogUtils.e(TAG, "onReceive :  关闭文件收到广播 --> closeFile：" + closeFile + ", \n thirdPackage：" + thirdPackage1);
                jump2meet(context);
                break;
            //home键广播
            case WpsModel.Reciver.ACTION_HOME:
                //通知注销掉WPS广播
                EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_WPS_RECEIVER).objects(false).build());
                break;
            //保存文件时的广播
            case WpsModel.Reciver.ACTION_SAVE:
                String openFile = intent.getStringExtra(WpsModel.ReciverExtra.OPENFILE);
                String thirdPackage = intent.getStringExtra(WpsModel.ReciverExtra.THIRDPACKAGE);
                String savePath = intent.getStringExtra(WpsModel.ReciverExtra.SAVEPATH);
                LogUtils.e(TAG, "onReceive :  保存键广播 --> openfile： " + openFile + "\n thirdPackage：" + thirdPackage + "\n savePath：" + savePath);
                File file = new File(savePath);
                String fileName = file.getName();
                JniHelper.getInstance().uploadFile(0, Constant.ANNOTATION_FILE_DIRECTORY_ID, 0, fileName, savePath, 0, Constant.UPLOAD_WPS_FILE);
                break;
            default:
                break;
        }
    }


    private void jump2meet(Context context) {
        if (GlobalValue.isFromAdminOpenWps) {
            Intent intent = new Intent(context, AdminActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            Intent intent = new Intent(context, MeetingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
