package com.xlk.paperlesstl.view.admin.activity;

import android.content.Context;

import com.blankj.utilcode.util.LogUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceContext;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMeet;
import com.xlk.paperlesstl.model.GlobalValue;
import com.xlk.paperlesstl.model.data.EventMessage;
import com.xlk.paperlesstl.model.data.EventType;
import com.xlk.paperlesstl.view.base.BasePresenter;

import java.util.ArrayList;
import java.util.List;

import cc.shinichi.library.ImagePreview;

import static com.xlk.paperlesstl.model.Constant.RESOURCE_ID_0;

/**
 * @author Created by xlk on 2020/9/17.
 * @desc
 */
public class AdminPresenter extends BasePresenter<AdminContract.View> implements AdminContract.Presenter {

    private final String TAG = "AdminPresenter-->";
    private final Context cxt;
    List<String> picPath = new ArrayList<>();

    AdminPresenter(Context context, AdminContract.View view) {
        super(view);
        this.cxt = context;
        //  修改本机界面状态
        jni.modifyContextProperties(InterfaceMacro.Pb_ContextPropertyID.Pb_MEETCONTEXT_PROPERTY_ROLE_VALUE,
                InterfaceMacro.Pb_MeetFaceStatus.Pb_MemState_AdminFace_VALUE);
        //强制缓存会议信息
        jni.mandatoryCacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETINFO_VALUE);
        // 缓存会议排位
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSEAT_VALUE);
        //缓存会议桌牌
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETTABLECARD_VALUE);
        // 缓存会议室
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOM_VALUE);
        // 缓存会场设备
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOMDEVICE_VALUE);
        //缓存会议目录
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORY_VALUE);
        //会议目录文件
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORYFILE_VALUE);
        //缓存会议评分
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_FILESCOREVOTESIGN_VALUE);
        // 缓存参会人信息
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER_VALUE);
        //缓存参会人权限
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBERPERMISSION_VALUE);
        //缓存投票信息
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETVOTEINFO_VALUE);
        //人员签到
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSIGN_VALUE);
        //公告信息
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETBULLET_VALUE);
        //会议视频
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETVIDEO_VALUE);
        //会议管理员
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ADMIN_VALUE);
        //会议管理员控制的会场
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MANAGEROOM_VALUE);
        //缓存会议目录权限
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORYRIGHT_VALUE);
        //缓存任务
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETTASK_VALUE);
        initVideoRes();
    }

    @Override
    public void onDestroy() {
        releaseVideoRes();
        super.onDestroy();
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //设备当前会议的一些信息
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEMEETSTATUS_VALUE: {
                LogUtils.i(TAG, "busEvent 设备当前会议的一些信息");
                queryCurrentMeeting();
                break;
            }
            //会议信息变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETINFO_VALUE: {
                byte[] bytes = (byte[]) msg.getObjects()[0];
                InterfaceBase.pbui_MeetNotifyMsg pbui_meetNotifyMsg = InterfaceBase.pbui_MeetNotifyMsg.parseFrom(bytes);
                int id = pbui_meetNotifyMsg.getId();
                int opermethod = pbui_meetNotifyMsg.getOpermethod();
                LogUtils.i(TAG, "BusEvent -->" + "会议信息变更通知 id=" + id + ",opermethod=" + opermethod);
                queryCurrentMeeting();
                break;
            }
            //设备会议信息变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEFACESHOW_VALUE: {
                LogUtils.i(TAG, "BusEvent -->" + "设备会议信息变更通知");
                queryCurrentMeeting();
                break;
            }
            //设备寄存器变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEINFO_VALUE: {
                LogUtils.i(TAG, "BusEvent 设备寄存器变更通知");
                queryLocalDeviceInfo();
                queryOnline();
                break;
            }
            //打开下载完成的图片
            case EventType.BUS_PREVIEW_IMAGE: {
                String filepath = (String) msg.getObjects()[0];
                LogUtils.i(TAG, "BusEvent 将要打开的图片路径：" + filepath);
                int index = 0;
                if (!picPath.contains(filepath)) {
                    picPath.add(filepath);
                    index = picPath.size() - 1;
                } else {
                    for (int i = 0; i < picPath.size(); i++) {
                        if (picPath.get(i).equals(filepath)) {
                            index = i;
                        }
                    }
                }
                previewImage(index);
                break;
            }
            default:
                break;
        }
    }

    void queryCurrentMeeting() {
        InterfaceContext.pbui_MeetContextInfo pbui_meetContextInfo = jni.queryContextProperty(
                InterfaceMacro.Pb_ContextPropertyID.Pb_MEETCONTEXT_PROPERTY_CURMEETINGID_VALUE);
        int currentMeetId = 0;
        if (pbui_meetContextInfo != null) {
            currentMeetId = pbui_meetContextInfo.getPropertyval();
        }
        LogUtils.i(TAG, "queryCurrentMeeting 当前会议ID=" + currentMeetId);
        if (currentMeetId != 0) {
            queryMeetingById(currentMeetId);
        } else {
            mView.updateMeetStatus(null);
        }
    }

    void queryMeetingById(int meetId) {
        try {
            InterfaceMeet.pbui_Item_MeetMeetInfo info = jni.queryMeetFromId(meetId);
            if (info != null) {
                mView.updateMeetStatus(info);
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    void queryLocalDeviceInfo() {
        InterfaceDevice.pbui_Item_DeviceDetailInfo detailInfo = jni.queryDevInfoById(GlobalValue.localDeviceId);
        if (detailInfo != null) {
            String devName = detailInfo.getDevname().toStringUtf8();
            mView.updateDeviceName(devName);
        }
    }

    void queryOnline() {
        try {
            byte[] bytes = jni.queryDevicePropertiesById(InterfaceMacro.Pb_MeetDevicePropertyID.Pb_MEETDEVICE_PROPERTY_NETSTATUS_VALUE,
                    0);
            if (bytes == null) {
                mView.updateOnlineStatus(false);
                return;
            }
            InterfaceDevice.pbui_DeviceInt32uProperty pbui_deviceInt32uProperty = InterfaceDevice.pbui_DeviceInt32uProperty.parseFrom(bytes);
            int propertyval = pbui_deviceInt32uProperty.getPropertyval();
            mView.updateOnlineStatus(propertyval == 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void initVideoRes() {
        jni.initVideoRes(RESOURCE_ID_0, GlobalValue.screen_width, GlobalValue.screen_height);
    }

    void releaseVideoRes() {
        jni.releaseVideoRes(RESOURCE_ID_0);
    }

    private void previewImage(int index) {
        if (picPath.isEmpty()) {
            return;
        }
        ImagePreview.getInstance()
                .setContext(cxt)
                //设置图片地址集合
                .setImageList(picPath)
                //设置开始的索引
                .setIndex(index)
                //设置是否显示下载按钮
                .setShowDownButton(false)
                //设置是否显示关闭按钮
                .setShowCloseButton(false)
                //设置是否开启下拉图片退出
                .setEnableDragClose(true)
                //设置是否开启上拉图片退出
                .setEnableUpDragClose(true)
                //设置是否开启点击图片退出
                .setEnableClickClose(true)
                .setShowErrorToast(true)
                .start();
    }
}
