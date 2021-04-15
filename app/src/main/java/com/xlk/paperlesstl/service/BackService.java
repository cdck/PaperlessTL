package com.xlk.paperlesstl.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceBullet;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceDownload;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.mogujie.tt.protobuf.InterfacePlaymedia;
import com.mogujie.tt.protobuf.InterfaceStream;
import com.mogujie.tt.protobuf.InterfaceUpload;
import com.mogujie.tt.protobuf.InterfaceWhiteboard;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.jni.JniHelper;
import com.xlk.paperlesstl.model.Constant;
import com.xlk.paperlesstl.model.GlobalValue;
import com.xlk.paperlesstl.model.WpsModel;
import com.xlk.paperlesstl.model.data.EventMessage;
import com.xlk.paperlesstl.model.data.EventType;
import com.xlk.paperlesstl.util.FileUtil;
import com.xlk.paperlesstl.view.bulletin.BulletinActivity;
import com.xlk.paperlesstl.view.video.VideoActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

import androidx.annotation.Nullable;

import static com.xlk.paperlesstl.App.appContext;

/**
 * @author Created by xlk on 2021/3/1.
 * @desc
 */
public class BackService extends Service implements NetworkUtils.OnNetworkStatusChangedListener {
    private final String TAG = "BackService-->";
    private WpsReceiver receiver;
    private JniHelper jni = JniHelper.getInstance();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        NetworkUtils.registerNetworkStatusChangedListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        NetworkUtils.unregisterNetworkStatusChangedListener(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBusEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //平台登陆验证返回
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEVALIDATE_VALUE: {
                byte[] s = (byte[]) msg.getObjects()[0];
                InterfaceBase.pbui_Type_DeviceValidate deviceValidate = InterfaceBase.pbui_Type_DeviceValidate.parseFrom(s);
                int valflag = deviceValidate.getValflag();
                List<Integer> valList = deviceValidate.getValList();
                List<Long> user64BitdefList = deviceValidate.getUser64BitdefList();
                String binaryString = Integer.toBinaryString(valflag);
                LogUtils.i("initFailed valflag=" + valflag + "，二进制：" + binaryString + ", valList=" + valList.toString() + ", user64List=" + user64BitdefList.toString());
                int count = 0, index;
                //  1 1101 1111
                char[] chars = binaryString.toCharArray();
                for (int i = 0; i < chars.length; i++) {
                    if ((chars[chars.length - 1 - i]) == '1') {
                        //有效位个数+1
                        count++;
                        //有效位当前位于valList的索引（跟i是无关的）
                        index = count - 1;
                        int code = valList.get(index);
                        LogUtils.d("initFailed 有效位：" + i + ",当前有效位的个数：" + count);
                        switch (i) {
                            case 0:
                                LogUtils.e("initFailed 区域服务器ID：" + code);
                                break;
                            case 1:
                                LogUtils.e("initFailed 设备ID：" + code);
                                GlobalValue.localDeviceId = code;
                                break;
                            case 2:
                                LogUtils.e("initFailed 状态码：" + code);
                                initializationResult(code);
                                break;
                            case 3:
                                LogUtils.e("initFailed 到期时间：" + code);
                                break;
                            case 4:
                                LogUtils.e("initFailed 企业ID：" + code);
                                break;
                            case 5:
                                LogUtils.e("initFailed 协议版本：" + code);
                                break;
                            case 6:
                                LogUtils.e("initFailed 注册时自定义的32位整数值：" + code);
                                break;
                            case 7:
                                LogUtils.e("initFailed 当前在线设备数：" + code);
                                break;
                            case 8:
                                LogUtils.e("initFailed 最大在线设备数：" + code);
                                break;
                            default:
                                break;
                        }
                    }
                }
                break;
            }
            //平台初始化结果
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_READY_VALUE: {
                int method = msg.getMethod();
                byte[] bytes = (byte[]) msg.getObjects()[0];
                if (method == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_NOTIFY_VALUE) {
                    InterfaceBase.pbui_Ready error = InterfaceBase.pbui_Ready.parseFrom(bytes);
                    int areaid = error.getAreaid();
                    LogUtils.i(TAG, "BusEvent -->" + "平台初始化结果 连接上的区域服务器ID=" + areaid);
                    GlobalValue.initializationIsOver = true;
                    EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_INITIALIZED).build());
                    EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_CLOSE_SERVER_DISCONNECTED_DIALOG).build());
                } else if (method == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_LOGON_VALUE) {
                    InterfaceBase.pbui_Type_LogonError error = InterfaceBase.pbui_Type_LogonError.parseFrom(bytes);
                    //Pb_WalletSystem_ErrorCode
                    int errcode = error.getErrcode();
                    LogUtils.i(TAG, "BusEvent -->" + "平台初始化结果 errcode=" + errcode);
                }
                break;
            }
            //设备寄存器变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEINFO_VALUE: {
                byte[] bytes = (byte[]) msg.getObjects()[0];
                InterfaceDevice.pbui_Type_MeetDeviceBaseInfo info = InterfaceDevice.pbui_Type_MeetDeviceBaseInfo.parseFrom(bytes);
                int deviceid = info.getDeviceid();
                //寄存器id 0:net status  50:res status  63:base info
                int attribid = info.getAttribid();
                LogUtils.i(TAG, "busEvent 设备寄存器变更通知 attribid=" + attribid + ",deviceid=" + deviceid+",本机设备ID="+GlobalValue.localDeviceId);
                if (attribid == 0 && deviceid == GlobalValue.localDeviceId) {
                    EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_NETWORK_CONNECTED).build());
                }
                break;
            }
            //平台下载
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DOWNLOAD_VALUE: {
                downloadInform(msg);
                break;
            }
            //上传进度通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_UPLOAD_VALUE: {
                uploadInform(msg);
                break;
            }
            //处理WPS广播监听
            case EventType.BUS_WPS_RECEIVER: {
                boolean isopen = (boolean) msg.getObjects()[0];
                if (isopen) {
                    registerWpsBroadCase();
                } else {
                    unregisterWpsBroadCase();
                }
                break;
            }
            //媒体播放通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEDIAPLAY_VALUE: {
                LogUtils.i(TAG, "onBusEvent 媒体播放通知");
                mediaPlayInform(msg);
                break;
            }
            //流播放通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_STREAMPLAY_VALUE: {
                LogUtils.i(TAG, "onBusEvent 流播放通知");
                streamPlayInform(msg);
                break;
            }
            //添加图片通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_WHITEBOARD_VALUE: {
                if (msg.getMethod() == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADDPICTURE_VALUE) {
                    byte[] o1 = (byte[]) msg.getObjects()[0];
                    addPicInform(o1);
                }
                break;
            }
            //参会人员权限变更通知    pbui_MeetNotifyMsg
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBERPERMISSION_VALUE: {
                LogUtils.i(TAG, "busEvent 参会人员权限变更通知");
                queryPermission();
                break;
            }
            //设备控制
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICECONTROL_VALUE: {
                deviceControlInform(msg);
                break;
            }
            //公告通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETBULLET_VALUE: {
                bulletinInform(msg);
                break;
            }
            default:
                break;
        }
    }


    private void initializationResult(int code) {
        String msg;
        switch (code) {
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_NONE_VALUE:
                msg = appContext.getString(R.string.error_0);
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_EXPIRATION_VALUE:
                msg = appContext.getString(R.string.error_1);
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_OPER_VALUE:
                msg = appContext.getString(R.string.error_2);
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_ENTERPRISE_VALUE:
                msg = appContext.getString(R.string.error_3);
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_NODEVICEID_VALUE:
                msg = appContext.getString(R.string.error_4);
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_NOALLOWIN_VALUE:
                msg = appContext.getString(R.string.error_5);
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_FILEERROR_VALUE:
                msg = appContext.getString(R.string.error_6);
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_INVALID_VALUE:
                msg = appContext.getString(R.string.error_7);
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_IDOCCUPY_VALUE:
                msg = appContext.getString(R.string.error_8);
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_NOTBEING_VALUE:
                msg = appContext.getString(R.string.error_9);
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_ONLYDEVICEID_VALUE:
                msg = appContext.getString(R.string.error_10);
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_DEVICETYPENOMATCH_VALUE:
                msg = appContext.getString(R.string.error_11);
                break;
            default:
                msg = "";
                break;
        }
        if (!msg.isEmpty()) {
            ToastUtils.showShort(msg);
        }
        //平台初始化成功
//        if (code == InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_NONE_VALUE) {
//            GlobalValue.initializationIsOver = true;
//            EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_INITIALIZED).build());
//            EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_CLOSE_SERVER_DISCONNECTED_DIALOG).build());
//        }
    }

    private void bulletinInform(EventMessage msg) throws InvalidProtocolBufferException {
        byte[] bulletin = (byte[]) msg.getObjects()[0];
        if (msg.getMethod() == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_PUBLIST_VALUE) {
            LogUtils.i(TAG, "发布公告通知");
            InterfaceBullet.pbui_BulletDetailInfo detailInfo = InterfaceBullet.pbui_BulletDetailInfo.parseFrom(bulletin);
            List<InterfaceBullet.pbui_Item_BulletDetailInfo> itemList = detailInfo.getItemList();
            if (!itemList.isEmpty()) {
                InterfaceBullet.pbui_Item_BulletDetailInfo info = itemList.get(0);
                Bundle bundle = new Bundle();
                bundle.putInt(BulletinActivity.EXTRA_BULLETIN_ID, info.getBulletid());
                bundle.putString(BulletinActivity.EXTRA_BULLETIN_TITLE, info.getTitle().toStringUtf8());
                bundle.putString(BulletinActivity.EXTRA_BULLETIN_CONTENT, info.getContent().toStringUtf8());
                bundle.putInt(BulletinActivity.EXTRA_BULLETIN_TYPE, info.getType());
                bundle.putInt(BulletinActivity.EXTRA_BULLETIN_START_TIME, info.getStarttime());
                bundle.putInt(BulletinActivity.EXTRA_BULLETIN_TIMEOUTS, info.getTimeouts());
                BulletinActivity.jump(bundle, this);
            }
        }
    }

    private void deviceControlInform(EventMessage msg) throws InvalidProtocolBufferException {
        byte[] o = (byte[]) msg.getObjects()[0];
        InterfaceDevice.pbui_Type_DeviceControl object = InterfaceDevice.pbui_Type_DeviceControl.parseFrom(o);
        int oper = object.getOper();//enum Pb_DeviceControlFlag
        int operval1 = object.getOperval1();//操作对应的参数 如更换主界面的媒体ID
        int operval2 = object.getOperval2();//操作对应的参数
        if (oper == InterfaceMacro.Pb_DeviceControlFlag.Pb_DEVICECONTORL_MODIFYLOGO.getNumber()) {
            LogUtils.i(TAG, "deviceControl: 更换Logo通知");
            //本地没有才下载
//            FileUtil.createDir(Constant.configuration_picture_dir);
//            jni.creationFileDownload(Constant.configuration_picture_dir + Constant.MAIN_LOGO_PNG_TAG + ".png", operval1, 1, 0, Constant.MAIN_LOGO_PNG_TAG);
        } else if (oper == InterfaceMacro.Pb_DeviceControlFlag.Pb_DEVICECONTORL_SHUTDOWN.getNumber()) {//关机
            LogUtils.i(TAG, "deviceControl: 关机");
        } else if (oper == InterfaceMacro.Pb_DeviceControlFlag.Pb_DEVICECONTORL_REBOOT.getNumber()) {//重启
            LogUtils.i(TAG, "deviceControl: 重启");
        } else if (oper == InterfaceMacro.Pb_DeviceControlFlag.Pb_DEVICECONTORL_PROGRAMRESTART.getNumber()) {//重启软件
            LogUtils.i(TAG, "deviceControl: 重启软件");
            AppUtils.relaunchApp(true);
        } else if (oper == InterfaceMacro.Pb_DeviceControlFlag.Pb_DEVICECONTORL_LIFTUP.getNumber()) {//升
            LogUtils.i(TAG, "deviceControl: 升");
        } else if (oper == InterfaceMacro.Pb_DeviceControlFlag.Pb_DEVICECONTORL_LIFTDOWN.getNumber()) {//降
            LogUtils.i(TAG, "deviceControl: 降");
        } else if (oper == InterfaceMacro.Pb_DeviceControlFlag.Pb_DEVICECONTORL_LIFTSTOP.getNumber()) {//停止升（降）
            LogUtils.i(TAG, "deviceControl: 停止升(降)");
        } else if (oper == InterfaceMacro.Pb_DeviceControlFlag.Pb_DEVICECONTORL_MODIFYMAINBG.getNumber()) {//更换主界面
            LogUtils.i(TAG, "deviceControl: 更换主界面");
        } else if (oper == InterfaceMacro.Pb_DeviceControlFlag.Pb_DEVICECONTORL_MODIFYPROJECTBG.getNumber()) {//更换投影界面
            LogUtils.i(TAG, "deviceControl: 更换投影界面");
        } else if (oper == InterfaceMacro.Pb_DeviceControlFlag.Pb_DEVICECONTORL_MODIFYSUBBG.getNumber()) {//更换子界面
            LogUtils.i(TAG, "deviceControl: 更换子界面");
        } else if (oper == InterfaceMacro.Pb_DeviceControlFlag.Pb_DEVICECONTORL_MODIFYFONTCOLOR.getNumber()) {//更换字体颜色
            LogUtils.i(TAG, "deviceControl: 更换字体颜色");
        }
    }

    private void queryPermission() {
        LogUtils.i(TAG, "queryPermission");
        InterfaceMember.pbui_Type_MemberPermission memberPermission = jni.queryAttendPeoplePermissions();
        if (memberPermission == null) return;
        GlobalValue.allPermissions = memberPermission.getItemList();
        for (int i = 0; i < GlobalValue.allPermissions.size(); i++) {
            InterfaceMember.pbui_Item_MemberPermission permission = GlobalValue.allPermissions.get(i);
            if (permission.getMemberid() == GlobalValue.localMemberId) {
                GlobalValue.localPermission = permission.getPermission();
                return;
            }
        }
    }

    private void addPicInform(byte[] datas) throws InvalidProtocolBufferException {
        InterfaceWhiteboard.pbui_Item_MeetWBPictureDetail object = InterfaceWhiteboard.pbui_Item_MeetWBPictureDetail.parseFrom(datas);
        int rPicSrcmemid = object.getSrcmemid();
        long rPicSrcwbid = object.getSrcwbid();
        ByteString rPicData = object.getPicdata();
        int opermemberid = object.getOpermemberid();
        GlobalValue.operid = object.getOperid();
//        if (!isSharing) {
//            if (disposePicOpermemberid == opermemberid && disposePicSrcmemid == rPicSrcmemid
//                    && disposePicSrcwbidd == rPicSrcwbid) {
//                DrawFragment.tempPicData = rPicData;
//                disposePicOpermemberid = 0;
//                disposePicSrcmemid = 0;
//                disposePicSrcwbidd = 0;
//            }
//        } else {
//            EventBus.getDefault().postSticky(new EventMessage.Builder().type(EventType.BUS_SHARE_PIC).objects(object).build());
//        }
    }

    private void uploadInform(EventMessage msg) throws InvalidProtocolBufferException {
        byte[] datas = (byte[]) msg.getObjects()[0];
        InterfaceUpload.pbui_TypeUploadPosCb uploadPosCb = InterfaceUpload.pbui_TypeUploadPosCb.parseFrom(datas);
        String pathName = uploadPosCb.getPathname().toStringUtf8();
        String userStr = uploadPosCb.getUserstr().toStringUtf8();
        int status = uploadPosCb.getStatus();
        int mediaId = uploadPosCb.getMediaId();
        int per = uploadPosCb.getPer();
//        int uploadflag = uploadPosCb.getUploadflag();
//        int userval = uploadPosCb.getUserval();
        byte[] bytes = jni.queryFileProperty(InterfaceMacro.Pb_MeetFilePropertyID.Pb_MEETFILE_PROPERTY_NAME.getNumber(), mediaId);
        InterfaceBase.pbui_CommonTextProperty pbui_commonTextProperty = InterfaceBase.pbui_CommonTextProperty.parseFrom(bytes);
        String fileName = pbui_commonTextProperty.getPropertyval().toStringUtf8();
        LogUtils.i(TAG, "uploadInform -->" + "上传进度：" + per + "\npathName= " + pathName);
        if (status == InterfaceMacro.Pb_Upload_State.Pb_UPLOADMEDIA_FLAG_HADEND_VALUE) {
            //结束上传
            if (userStr.equals(Constant.UPLOAD_DRAW_PIC)) {
                //从画板上传的图片
                FileUtils.delete(pathName);
            }
            ToastUtils.showShort(getString(R.string.upload_completed, fileName));
            LogUtils.i(TAG, "uploadInform -->" + fileName + " 上传完毕");
        } else if (status == InterfaceMacro.Pb_Upload_State.Pb_UPLOADMEDIA_FLAG_NOSERVER_VALUE) {
            LogUtils.i(TAG, "uploadInform -->" + " 没找到可用的服务器");
        } else if (status == InterfaceMacro.Pb_Upload_State.Pb_UPLOADMEDIA_FLAG_ISBEING_VALUE) {
            LogUtils.i(TAG, "uploadInform -->" + pathName + " 已经存在");
        }
    }

    private void streamPlayInform(EventMessage msg) throws InvalidProtocolBufferException {
        byte[] datas = (byte[]) msg.getObjects()[0];
        InterfaceStream.pbui_Type_MeetStreamPlay meetStreamPlay = InterfaceStream.pbui_Type_MeetStreamPlay.parseFrom(datas);
        int res = meetStreamPlay.getRes();
        int createdeviceid = meetStreamPlay.getCreatedeviceid();
        LogUtils.i(TAG, "streamPlayInform -->" + "流播放通知 res =" + res);
        if (res != 0) {
            //只处理资源ID为0的播放资源
            return;
        }
        //触发器ID
        int triggerid = meetStreamPlay.getTriggerid();
        //流源设备ID 采集端的设备ID
        int deviceid = meetStreamPlay.getDeviceid();
        int subid = meetStreamPlay.getSubid();
        int triggeruserval = meetStreamPlay.getTriggeruserval();
        boolean isMandatory = triggeruserval == InterfaceMacro.Pb_TriggerUsedef.Pb_EXCEC_USERDEF_FLAG_NOCREATEWINOPER_VALUE;
        if (GlobalValue.isMandatoryPlaying) {//当前正在被强制播放中
            if (isMandatory) {//收到新的强制性播放
                LogUtils.i(TAG, "streamPlayInform -->" + "当前属于强制性播放中，收到新的强制流播放播放");
            } else {//收到的不是强制性播放
                LogUtils.i(TAG, "streamPlayInform -->" + "当前属于强制性播放中，不处理非强制的流播放通知");
                return;
            }
        }
        if (createdeviceid != GlobalValue.localDeviceId) {
            //是否是强制性播放
            GlobalValue.isMandatoryPlaying = isMandatory;
        }
        GlobalValue.haveNewPlayInform = true;
        startActivity(new Intent(this, VideoActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                .putExtra(Constant.EXTRA_VIDEO_ACTION, InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_STREAMPLAY_VALUE)
                .putExtra(Constant.EXTRA_VIDEO_DEVICE_ID, deviceid)
                .putExtra(Constant.EXTRA_VIDEO_SUB_ID, subid)
        );
    }

    private void mediaPlayInform(EventMessage msg) throws InvalidProtocolBufferException {
        byte[] datas = (byte[]) msg.getObjects()[0];
        InterfacePlaymedia.pbui_Type_MeetMediaPlay mediaPlay = InterfacePlaymedia.pbui_Type_MeetMediaPlay.parseFrom(datas);
        int res = mediaPlay.getRes();
        LogUtils.i(TAG, "mediaPlayInform -->" + "媒体播放通知 res= " + res);
        if (res != 0) {
            //只处理资源ID为0的播放资源
            return;
        }
        int mediaid = mediaPlay.getMediaid();
        int createdeviceid = mediaPlay.getCreatedeviceid();
        int triggerid = mediaPlay.getTriggerid();
        int triggeruserval = mediaPlay.getTriggeruserval();
        boolean isMandatory = triggeruserval == InterfaceMacro.Pb_TriggerUsedef.Pb_EXCEC_USERDEF_FLAG_NOCREATEWINOPER_VALUE;
        int type = mediaid & Constant.MAIN_TYPE_BITMASK;
        int subtype = mediaid & Constant.SUB_TYPE_BITMASK;
        if (type == Constant.MEDIA_FILE_TYPE_AUDIO || type == Constant.MEDIA_FILE_TYPE_VIDEO) {
            LogUtils.i(TAG, "mediaPlayInform -->" + "媒体播放通知：isVideoPlaying= " + GlobalValue.isVideoPlaying);
            if (createdeviceid != GlobalValue.localDeviceId) {
                GlobalValue.isMandatoryPlaying = isMandatory;
            }
            GlobalValue.haveNewPlayInform = true;
            startActivity(new Intent(this, VideoActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    .putExtra(Constant.EXTRA_VIDEO_ACTION, InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEDIAPLAY_VALUE)
                    .putExtra(Constant.EXTRA_VIDEO_SUBTYPE, subtype)
                    .putExtra(Constant.EXTRA_VIDEO_MEDIA_ID, mediaid)
            );
        } else {
            LogUtils.i(TAG, "mediaPlayInform -->" + "媒体播放通知：下载文件后打开");
            //创建好下载目录
            FileUtils.createOrExistsDir(Constant.DOWNLOAD_DIR);
            /** **** **  查询该媒体ID的文件名  ** **** **/
            byte[] bytes = JniHelper.getInstance().queryFileProperty(InterfaceMacro.Pb_MeetFilePropertyID.Pb_MEETFILE_PROPERTY_NAME.getNumber(), mediaid);
            InterfaceBase.pbui_CommonTextProperty pbui_commonTextProperty = InterfaceBase.pbui_CommonTextProperty.parseFrom(bytes);
            String fielName = pbui_commonTextProperty.getPropertyval().toStringUtf8();
            String pathname = Constant.DOWNLOAD_DIR + fielName;
            File file = new File(pathname);
            if (file.exists()) {
                if (GlobalValue.downloadingFiles.contains(mediaid)) {
                    ToastUtils.showShort(R.string.file_downloading);
                } else {
                    FileUtil.openFile(this, file);
                }
                return;
            }
            JniHelper.getInstance().creationFileDownload(pathname, mediaid, 0, 0, Constant.DOWNLOAD_SHOULD_OPEN_FILE);
        }
    }

    private void registerWpsBroadCase() {
        if (receiver == null) {
            receiver = new WpsReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(WpsModel.Reciver.ACTION_SAVE);
            filter.addAction(WpsModel.Reciver.ACTION_CLOSE);
            filter.addAction(WpsModel.Reciver.ACTION_HOME);
//            filter.addAction(WpsModel.Reciver.ACTION_BACK);
            registerReceiver(receiver, filter);
        }
    }

    private void unregisterWpsBroadCase() {
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
    }

    private void downloadInform(EventMessage msg) throws InvalidProtocolBufferException {
        byte[] data2 = (byte[]) msg.getObjects()[0];
        InterfaceDownload.pbui_Type_DownloadCb pbui_type_downloadCb = InterfaceDownload.pbui_Type_DownloadCb.parseFrom(data2);
        int mediaid = pbui_type_downloadCb.getMediaid();
        int progress = pbui_type_downloadCb.getProgress();
        int nstate = pbui_type_downloadCb.getNstate();
        int err = pbui_type_downloadCb.getErr();
        String filepath = pbui_type_downloadCb.getPathname().toStringUtf8();
        String userStr = pbui_type_downloadCb.getUserstr().toStringUtf8();
        String fileName = filepath.substring(filepath.lastIndexOf("/") + 1).toLowerCase();
        LogUtils.i(TAG, "downloadInform userStr=" + userStr + ",进度=" + progress + ",nstate=" + nstate);
        if (nstate == InterfaceMacro.Pb_Download_State.Pb_STATE_MEDIA_DOWNLOAD_WORKING_VALUE) {
            //主页背景
            if (!userStr.equals(Constant.MAIN_BG_PNG_TAG)
                    //主页logo
                    && !userStr.equals(Constant.MAIN_LOGO_PNG_TAG)
                    //子界面背景
                    && !userStr.equals(Constant.MEET_BG_PNG_TAG)
                    //公告背景
                    && !userStr.equals(Constant.NOTICE_BG_PNG_TAG)
                    //公告logo
                    && !userStr.equals(Constant.NOTICE_LOGO_PNG_TAG)
                    //投影背景
                    && !userStr.equals(Constant.PROJECTIVE_BG_PNG_TAG)
                    //投影logo
                    && !userStr.equals(Constant.PROJECTIVE_LOGO_PNG_TAG)
                    //会场底图
                    && !userStr.equals(Constant.ROOM_BG_PNG_TAG)
                    //下载议程文件
                    && !userStr.equals(Constant.DOWNLOAD_AGENDA_FILE)
                    //归档文件
                    && !userStr.equals(Constant.ARCHIVE_DOWNLOAD_FILE)
                    //归档议程文件
                    && !userStr.equals(Constant.ARCHIVE_AGENDA_FILE)
            ) {
                ToastUtils.showShort(getString(R.string.file_downloaded_percent, fileName, progress + "%"));
            }
            if (userStr.equals(Constant.ARCHIVE_DOWNLOAD_FILE) || userStr.equals(Constant.ARCHIVE_AGENDA_FILE)) {
                EventBus.getDefault().post(new EventMessage.Builder().type(EventType.ARCHIVE_BUS_DOWNLOAD_FILE).objects(mediaid, fileName, progress).build());
            }
        } else if (nstate == InterfaceMacro.Pb_Download_State.Pb_STATE_MEDIA_DOWNLOAD_EXIT_VALUE) {
            //下载退出---不管成功与否,下载结束最后一次的状态都是这个
            if (GlobalValue.downloadingFiles.contains(mediaid)) {
                int index = GlobalValue.downloadingFiles.indexOf(mediaid);
                GlobalValue.downloadingFiles.remove(index);
            }
            File file = new File(filepath);
            if (file.exists()) {
                LogUtils.i(TAG, "BusEvent -->" + "下载完成：" + filepath);
                switch (userStr) {
                    case Constant.MAIN_BG_PNG_TAG:
                        EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_MAIN_BG).objects(filepath, mediaid).build());
                        break;
                    case Constant.MAIN_LOGO_PNG_TAG:
                        EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_MAIN_LOGO).objects(filepath, mediaid).build());
                        break;
                    case Constant.MEET_BG_PNG_TAG:
                        EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_MEET_BG).objects(filepath, mediaid).build());
                        break;
                    case Constant.NOTICE_BG_PNG_TAG:
                        EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_BULLETIN_BG).objects(filepath, mediaid).build());
                        break;
                    case Constant.NOTICE_LOGO_PNG_TAG:
                        EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_BULLETIN_LOGO).objects(filepath, mediaid).build());
                        break;
                    case Constant.PROJECTIVE_BG_PNG_TAG:
                        EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_PROJECTIVE_BG).objects(filepath, mediaid).build());
                        break;
                    case Constant.PROJECTIVE_LOGO_PNG_TAG:
                        EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_PROJECTIVE_LOGO).objects(filepath, mediaid).build());
                        break;
                    case Constant.ROOM_BG_PNG_TAG:
                        EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_ROOM_BG).objects(filepath, mediaid).build());
                        break;
                    //下载的议程文件
                    case Constant.DOWNLOAD_AGENDA_FILE:
                        EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_AGENDA_FILE).objects(filepath, mediaid).build());
                        break;
                    //下载完成后需要打开的文件
                    case Constant.DOWNLOAD_SHOULD_OPEN_FILE:
                        EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_MATERIAL_FILE).objects(filepath, mediaid).build());
                        FileUtil.openFile(this, file);
                        break;
                    //会议资料文件下载完成
                    case Constant.DOWNLOAD_MATERIAL_FILE:
                        EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_MATERIAL_FILE).objects(filepath, mediaid).build());
                        break;
                    //归档议程文件，下载成功
                    case Constant.ARCHIVE_AGENDA_FILE:
                        EventBus.getDefault().post(new EventMessage.Builder().type(EventType.ARCHIVE_BUS_AGENDA_FILE).objects(filepath, mediaid).build());
                        break;
                    //桌牌背景图片，下载完成
                    case Constant.DOWNLOAD_TABLE_CARD_BG:
                        EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_TABLE_CARD_BG).objects(filepath, mediaid).build());
                        break;
                    default:
                        break;
                }
            } else {
                LogUtils.i(TAG, "downloadInform 没有找到文件 filepath=" + filepath);
            }
        } else {
            LogUtils.i(TAG, "downloadInform 下载状态：" + nstate + ", 下载错误码：" + err + ", 文件名：" + fileName);
        }
    }

    @Override
    public void onDisconnected() {
        LogUtils.i(TAG, "网络变更 onDisconnected");
//        EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_NETWORK_CONNECTED).build());
    }

    @Override
    public void onConnected(NetworkUtils.NetworkType networkType) {
        LogUtils.i(TAG, "网络变更 onConnected networkType=" + networkType.toString());
//        EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_NETWORK_CONNECTED).objects(networkType).build());
    }
}
