package com.xlk.paperlesstl.jni;

import android.graphics.PointF;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceAdmin;
import com.mogujie.tt.protobuf.InterfaceAgenda;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceBullet;
import com.mogujie.tt.protobuf.InterfaceContext;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceDownload;
import com.mogujie.tt.protobuf.InterfaceFaceconfig;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.mogujie.tt.protobuf.InterfaceFilescorevote;
import com.mogujie.tt.protobuf.InterfaceIM;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMeet;
import com.mogujie.tt.protobuf.InterfaceMeetfunction;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.mogujie.tt.protobuf.InterfacePerson;
import com.mogujie.tt.protobuf.InterfacePlaymedia;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.mogujie.tt.protobuf.InterfaceSignin;
import com.mogujie.tt.protobuf.InterfaceStop;
import com.mogujie.tt.protobuf.InterfaceStream;
import com.mogujie.tt.protobuf.InterfaceTablecard;
import com.mogujie.tt.protobuf.InterfaceTask;
import com.mogujie.tt.protobuf.InterfaceUpload;
import com.mogujie.tt.protobuf.InterfaceVideo;
import com.mogujie.tt.protobuf.InterfaceVote;
import com.mogujie.tt.protobuf.InterfaceWhiteboard;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.model.Constant;
import com.xlk.paperlesstl.model.GlobalValue;

import java.util.ArrayList;
import java.util.List;

import static com.xlk.paperlesstl.model.Constant.CAMERA_SUB_ID;
import static com.xlk.paperlesstl.model.Constant.RESOURCE_ID_0;
import static com.xlk.paperlesstl.model.Constant.SCREEN_SUB_ID;
import static com.xlk.paperlesstl.model.GlobalValue.downloadingFiles;
import static com.xlk.paperlesstl.util.ConvertUtil.s2b;

/**
 * @author Created by xlk on 2021/3/1.
 * @desc
 */
public class JniHelper {
    private final String TAG = "JniHelper-->";
    private Call jni;
    private static JniHelper instance;

    public static JniHelper getInstance() {
        if (instance == null) {
            synchronized (JniHelper.class) {
                if (instance == null) {
                    instance = new JniHelper();
                }
            }
        }
        return instance;
    }

    private JniHelper() {
        jni = Call.getInstance();
    }


    /**
     * 初始化无纸化网络平台
     *
     * @param uniqueId 唯一值
     */
    public boolean javaInitSys(String uniqueId) {
        LogUtils.i("javaInitSys=" + uniqueId);
        InterfaceBase.pbui_MeetCore_InitParam pb = InterfaceBase.pbui_MeetCore_InitParam.newBuilder()
                .setPconfigpathname(s2b(Constant.ROOT_DIR + "client.ini"))
                .setProgramtype(InterfaceMacro.Pb_ProgramType.Pb_MEET_PROGRAM_TYPE_MEETCLIENT.getNumber())
                .setStreamnum(4)
                .setLogtofile(0)
                .setKeystr(s2b(uniqueId))
                .build();
        boolean bret = true;
        if (-1 == jni.Init_walletSys(pb.toByteArray())) {
            bret = false;
        }
        return bret;
    }

    /**
     * 修改上下文属性
     * Pb_MemState_MainFace=0; //处于主界面
     * Pb_MemState_MemFace=1;//参会人员界面
     * Pb_MemState_AdminFace=2;//后台管理界面
     *
     * @param propertyid InterfaceMacro.Pb_ContextPropertyID
     * @param value      InterfaceMacro.Pb_MeetFaceStatus
     * @see InterfaceMacro.Pb_ContextPropertyID
     */
    public void modifyContextProperties(int propertyid, int value) {
        InterfaceContext.pbui_MeetContextInfo build = InterfaceContext.pbui_MeetContextInfo.newBuilder()
                .setPropertyid(propertyid)
                .setPropertyval(value)
                .build();
        byte[] bytes = build.toByteArray();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETCONTEXT_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SETPROPERTY_VALUE, bytes);
        LogUtils.e(TAG, "setInterfaceState:  修改上下文属性  --->>> propertyid= " + propertyid + ", value = " + value);
    }

    public void InitAndCapture(int type, int channelindex) {
        jni.InitAndCapture(type, channelindex);
    }

    /**
     * 查询指定ID的设备信息
     *
     * @param devid 设备id
     */
    public InterfaceDevice.pbui_Item_DeviceDetailInfo queryDevInfoById(int devid) {
        InterfaceBase.pbui_QueryInfoByID build = InterfaceBase.pbui_QueryInfoByID.newBuilder()
                .setId(devid).build();
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEINFO_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SINGLEQUERYBYID_VALUE, build.toByteArray());
        if (array != null) {
            try {
                InterfaceDevice.pbui_Type_DeviceDetailInfo pbui_type_deviceDetailInfo = InterfaceDevice.pbui_Type_DeviceDetailInfo.parseFrom(array);
                if (pbui_type_deviceDetailInfo != null) {
                    if (pbui_type_deviceDetailInfo.getPdevCount() > 0) {
                        LogUtils.e(TAG, "queryDevInfoById :  查询指定ID的设备信息成功 --> devid=" + devid);
                        InterfaceDevice.pbui_Item_DeviceDetailInfo pdev = pbui_type_deviceDetailInfo.getPdev(0);
                        return pdev;
                    }
                }
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryDevInfoById :  查询指定ID的设备信息失败 --> devid=" + devid);
        return null;
    }

    /**
     * 129.查询指定ID的会议
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public InterfaceMeet.pbui_Item_MeetMeetInfo queryMeetFromId(int value) throws InvalidProtocolBufferException {
        InterfaceBase.pbui_QueryInfoByID.Builder builder = InterfaceBase.pbui_QueryInfoByID.newBuilder();
        builder.setId(value);
        InterfaceBase.pbui_QueryInfoByID build = builder.build();
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETINFO_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SINGLEQUERYBYID_VALUE, build.toByteArray());
        if (array != null) {
            InterfaceMeet.pbui_Type_MeetMeetInfo info = InterfaceMeet.pbui_Type_MeetMeetInfo.parseFrom(array);
            if (!info.getItemList().isEmpty()) {
                LogUtils.e(TAG, "queryMeetFromId :  查询指定ID的会议成功 --> id=" + value);
                InterfaceMeet.pbui_Item_MeetMeetInfo pbui_item_meetMeetInfo = info.getItemList().get(0);
                return pbui_item_meetMeetInfo;
            }
        }
        LogUtils.e(TAG, "queryMeetFromId :  查询指定ID的会议失败 --> id=" + value);
        return null;
    }

    /**
     * 查询界面配置信息
     *
     * @return
     */
    public InterfaceFaceconfig.pbui_Type_FaceConfigInfo queryInterFaceConfiguration() {
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETFACECONFIG_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY_VALUE, null);
        if (array != null) {
            try {
                InterfaceFaceconfig.pbui_Type_FaceConfigInfo pbui_type_faceConfigInfo = InterfaceFaceconfig.pbui_Type_FaceConfigInfo.parseFrom(array);
                LogUtils.i(TAG, "queryInterFaceConfiguration :  查询界面配置 -->成功 ");
                return pbui_type_faceConfigInfo;
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryInterFaceConfiguration :  查询界面配置 -->失败 ");
        return null;
    }

    /**
     * 删除会议目录文件
     *
     * @param dirId 会议目录
     * @param item  会议文件
     */
    public void deleteMeetDirFile(int dirId, InterfaceFile.pbui_Item_MeetDirFileDetailInfo item) {
        InterfaceFile.pbui_Type_MeetDirFileDetailInfo build = InterfaceFile.pbui_Type_MeetDirFileDetailInfo.newBuilder()
                .setDirid(dirId)
                .addItem(item)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORYFILE_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL_VALUE, build.toByteArray());
    }

    /**
     * 创建一个文件下载
     *
     * @param pathName   下载媒体全路径名称
     * @param mediaId    媒体ID
     * @param isNewFile  =0 不覆盖同名文件,=1 覆盖下载
     * @param onlyFinish =1 表示只需要结束的通知
     * @param userStr    用户传入的自定义字串标识
     */
    public void creationFileDownload(String pathName, int mediaId, int isNewFile, int onlyFinish, String userStr) {
        if (downloadingFiles.contains(mediaId)) {
            LogUtils.e(TAG,"文件下载中...");
//            ToastUtils.showShort(R.string.file_downloading);
            return;
        }
        InterfaceDownload.pbui_Type_DownloadStart build = InterfaceDownload.pbui_Type_DownloadStart.newBuilder()
                .setMediaid(mediaId)
                .setNewfile(isNewFile)
                .setOnlyfinish(onlyFinish)
                .setPathname(s2b(pathName))
                .setUserstr(s2b(userStr)).build();
        LogUtils.e(TAG, "creationFileDownload:   --->>> mediaId=" + mediaId + ", 文件=" + pathName + ", userStr=" + userStr);
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DOWNLOAD.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD_VALUE, build.toByteArray());
        LogUtils.e(TAG, "creationFileDownload:  创建一个文件下载 --->>>pathName=  " + pathName);
        downloadingFiles.add(mediaId);
    }

    /**
     * 查询设备会议信息（14）
     */
    public InterfaceDevice.pbui_Type_DeviceFaceShowDetail queryDeviceMeetInfo() {
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEFACESHOW_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY_VALUE, null);
        if (array != null) {
            LogUtils.i(TAG, "queryDeviceMeetInfo:  查询设备会议信息--->>> 成功");
            try {
                return InterfaceDevice.pbui_Type_DeviceFaceShowDetail.parseFrom(array);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryDeviceMeetInfo:  查询设备会议信息 --->>> 失败");
        return null;

    }

    /**
     * 按属性ID查询指定上下文属性
     *
     * @param propertyid InterfaceMacro.Pb_ContextPropertyID
     */
    public InterfaceContext.pbui_MeetContextInfo queryContextProperty(int propertyid) {
        InterfaceContext.pbui_QueryMeetContextInfo build = InterfaceContext.pbui_QueryMeetContextInfo.newBuilder()
                .setPropertyid(propertyid)
                .build();
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETCONTEXT_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERYPROPERTY_VALUE, build.toByteArray());
        if (array != null) {
            try {
                InterfaceContext.pbui_MeetContextInfo pbui_meetContextInfo = InterfaceContext.pbui_MeetContextInfo.parseFrom(array);
                LogUtils.i(TAG, "queryContextProperty 按属性ID查询指定上下文属性 成功 --->>> propertyid=" + propertyid);
                return pbui_meetContextInfo;
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryContextProperty 按属性ID查询指定上下文属性 失败 --->>> propertyid+" + propertyid);
        return null;
    }

    /**
     * 207.发送签到
     *
     * @param memberid 签到的人员ID,为0表示当前绑定的人员
     * @param signType 签到方式
     * @param pwd      密码
     * @param picdata  手写签到数据
     */
    public void sendSign(int memberid, int signType, String pwd, ByteString picdata) {
        InterfaceSignin.pbui_Type_DoMeetSignIno.Builder builder = InterfaceSignin.pbui_Type_DoMeetSignIno.newBuilder();
        builder.setMemberid(memberid);
        builder.setSigninType(signType);
        builder.setPassword(s2b(pwd));
        builder.setPsigndata(picdata);
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSIGN.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD.getNumber(), builder.build().toByteArray());
        LogUtils.e(TAG, "sendSign:  发送签到 --->>> signType: " + signType);
    }


    /**
     * 223.白板删除记录
     *
     * @return
     */
    public void whiteBoardDeleteRecord(int memberid, int operid, int opermemberid, int srcmemid, long srcwbid, long utcstamp, int figuretype) {
        InterfaceWhiteboard.pbui_Type_MeetDoClearWhiteBoard.Builder builder = InterfaceWhiteboard.pbui_Type_MeetDoClearWhiteBoard.newBuilder();
        builder.setMemberid(memberid);
        builder.setOperid(operid);
        builder.setOpermemberid(opermemberid);
        builder.setSrcmemid(srcmemid);
        builder.setSrcwbid(srcwbid);
        builder.setUtcstamp(utcstamp);
        builder.setFiguretype(figuretype);
        InterfaceWhiteboard.pbui_Type_MeetDoClearWhiteBoard build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_WHITEBOARD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "whiteBoardDeleteRecord:  白板删除记录 --->>> ");
    }

    /**
     * 添加墨迹
     *
     * @return
     */
    public void addInk(int operid, int opermemberid, int srcmemid, long srcwbid, long utcstamp,
                       int figuretype, int linesize, int argb, List<PointF> allpinklist) {
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardInkItem.Builder builder = InterfaceWhiteboard.pbui_Type_MeetWhiteBoardInkItem.newBuilder();
        builder.setOperid(operid);
        builder.setOpermemberid(opermemberid);
        builder.setSrcmemid(srcmemid);
        builder.setSrcwbid(srcwbid);
        builder.setUtcstamp(utcstamp);
        builder.setFiguretype(figuretype);
        builder.setLinesize(linesize);
        builder.setArgb(argb);
        for (int i = 0; i < allpinklist.size(); i++) {
            builder.addPinklist(allpinklist.get(i).x);
            builder.addPinklist(allpinklist.get(i).y);
        }
        LogUtils.e(TAG, "addInk   发送的xy个数--->>> " + builder.getPinklistCount());
//        builder.addAllPinklist(allpinklist);
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardInkItem build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_WHITEBOARD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADDINK.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "addInk:  添加墨迹 --->>> ");
    }

    /**
     * 添加矩形、直线、圆形
     *
     * @return
     */
    public void addDrawFigure(int operid, int opermemberid, int srcmemid, long srcwbid, long utcstamp,
                              int type, int size, int color, List<Float> allpt) {
        InterfaceWhiteboard.pbui_Item_MeetWBRectDetail.Builder builder = InterfaceWhiteboard.pbui_Item_MeetWBRectDetail.newBuilder();
        builder.setOperid(operid);
        builder.setOpermemberid(opermemberid);
        builder.setSrcmemid(srcmemid);
        builder.setSrcwbid(srcwbid);
        builder.setUtcstamp(utcstamp);
        builder.setFiguretype(type);
        builder.setLinesize(size);
        builder.setArgb(color);
        builder.addAllPt(allpt);
        InterfaceWhiteboard.pbui_Item_MeetWBRectDetail build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_WHITEBOARD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADDRECT.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "addDrawFigure:  添加矩形、直线、圆形 --->>> ");
    }


    /**
     * 添加文本
     *
     * @param operid       操作ID 终端计算产生 为0表示清除所有等于opermemberid的白板操作
     * @param opermemberid 当前该命令的人员ID 为0表示清除所有等于opermemberid的白板操作
     * @param srcmemid     发起人的人员ID 白板标识使用
     * @param srcwbid      发起人的白板标识 取微秒级的时间作标识 白板标识使用
     * @param utcstamp     时间戳  微秒
     * @param figuretype   图形类型 为0表示清除所有等于figuretype的白板操作
     * @param fontsize     字体大小
     * @param fontflag     字体其它属性
     * @param argb         字体颜色
     * @param fontname     字体名称
     * @param lx           (lx,ly,左上角坐标)
     * @param ly           (lx,ly,左上角坐标)
     * @param ptext        内容
     */
    public void addText(int operid, int opermemberid, int srcmemid, long srcwbid, long utcstamp, int figuretype, int fontsize, int fontflag,
                        int argb, String fontname, float lx, float ly, String ptext) {
        InterfaceWhiteboard.pbui_Item_MeetWBTextDetail.Builder builder = InterfaceWhiteboard.pbui_Item_MeetWBTextDetail.newBuilder();
        builder.setOperid(operid);
        builder.setOpermemberid(opermemberid);
        builder.setSrcmemid(srcmemid);
        builder.setSrcwbid(srcwbid);
        builder.setUtcstamp(utcstamp);
        builder.setFiguretype(figuretype);
        builder.setFontsize(fontsize);
        builder.setFontflag(fontflag);
        builder.setArgb(argb);
        builder.setFontname(s2b(fontname));
        builder.setLx(lx);
        builder.setLy(ly);
        builder.setPtext(s2b(ptext));
        InterfaceWhiteboard.pbui_Item_MeetWBTextDetail build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_WHITEBOARD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADDTEXT.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "addText:  添加文本 --->>> " + ptext);
    }

    /**
     * 同意加入
     *
     * @return
     */
    public void agreeJoin(int opermemberid, int srcmemid, long srcwbid) {
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardOper.Builder builder = InterfaceWhiteboard.pbui_Type_MeetWhiteBoardOper.newBuilder();
        builder.setSrcmemid(srcmemid);
        builder.setSrcwbid(srcwbid);
        builder.setOpermemberid(opermemberid);
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardOper build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_WHITEBOARD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ENTER.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "agreeJoin :  同意加入 --> ");
    }

    /**
     * 拒绝加入
     *
     * @return
     */
    public void rejectJoin(int opermemberid, int srcmemid, long srcwbid) {
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardOper.Builder builder = InterfaceWhiteboard.pbui_Type_MeetWhiteBoardOper.newBuilder();
        builder.setOpermemberid(opermemberid);
        builder.setSrcmemid(srcmemid);
        builder.setSrcwbid(srcwbid);
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardOper build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_WHITEBOARD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_REJECT.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "rejectJoin:  拒绝加入 --->>> ");
    }

    /**
     * 广播本身退出白板
     *
     * @return
     */
    public void broadcastStopWhiteBoard(int operflag, String medianame, int opermemberid, int srcmemid, long srcwbid, List<Integer> alluserid) {
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardControl.Builder builder = InterfaceWhiteboard.pbui_Type_MeetWhiteBoardControl.newBuilder();
        builder.setOperflag(operflag);
        builder.setMedianame(s2b(medianame));
        builder.setOpermemberid(opermemberid);
        builder.setSrcmemid(srcmemid);
        builder.setSrcwbid(srcwbid);
        builder.addAllUserid(alluserid);
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardControl build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_WHITEBOARD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_CONTROL.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "inquiryStartWhiteBoard:  广播本身退出白板 --->>> ");
    }

    /**
     * 发起白板
     *
     * @return
     */
    public void coerceStartWhiteBoard(int operFlag, String mediaName, int operMemberid, int srcmemId, long srcwbId, List<Integer> allUserId) {
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardControl.Builder tmp3 = InterfaceWhiteboard.pbui_Type_MeetWhiteBoardControl.newBuilder();
        tmp3.setOperflag(operFlag);
        tmp3.setMedianame(s2b(mediaName));
        tmp3.setOpermemberid(operMemberid);
        tmp3.setSrcmemid(srcmemId);
        tmp3.setSrcwbid(srcwbId);
        tmp3.addAllUserid(allUserId);
        tmp3.setOperflag(InterfaceMacro.Pb_MeetPostilOperType.Pb_MEETPOTIL_FLAG_REQUESTOPEN.getNumber());
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardControl build = tmp3.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_WHITEBOARD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_CONTROL.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "coerceStartBoard:  发起白板 --->>> ");
    }

    /**
     * 添加图片
     *
     * @return
     */
    public void addPicture(int operid, int opermemberid, int srcmemid, long srcwbid, long utcstamp, int figuretype, float lx, float ly, ByteString picdata) {
        InterfaceWhiteboard.pbui_Item_MeetWBPictureDetail.Builder builder = InterfaceWhiteboard.pbui_Item_MeetWBPictureDetail.newBuilder();
        builder.setOperid(operid);
        builder.setOpermemberid(opermemberid);
        builder.setSrcmemid(srcmemid);
        builder.setSrcwbid(srcwbid);
        builder.setUtcstamp(utcstamp);
        builder.setFiguretype(figuretype);
        builder.setLx(lx);
        builder.setLy(ly);
        builder.setPicdata(picdata);
        LogUtils.e(TAG, "addPicture :   --->>> " + figuretype);
        InterfaceWhiteboard.pbui_Item_MeetWBPictureDetail build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_WHITEBOARD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADDPICTURE.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "addPicture:  共享添加图片 --->>> ");
    }

    /**
     * 白板清空记录
     *
     * @return
     */
    public void whiteBoardClearRecord(int operid, int opermemberid, int srcmemid, long srcwbid, long utcstamp, int figuretype) {
        InterfaceWhiteboard.pbui_Type_MeetDoClearWhiteBoard.Builder builder = InterfaceWhiteboard.pbui_Type_MeetDoClearWhiteBoard.newBuilder();
        builder.setOperid(operid);
        builder.setOpermemberid(opermemberid);
        builder.setSrcmemid(srcmemid);
        builder.setSrcwbid(srcwbid);
        builder.setUtcstamp(utcstamp);
        builder.setFiguretype(figuretype);
        InterfaceWhiteboard.pbui_Type_MeetDoClearWhiteBoard build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_WHITEBOARD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DELALL.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "whiteBoardClearInform:  222.白板清空记录 --->>> ");
    }

    /**
     * 查询参会人员
     */
    public InterfaceMember.pbui_Type_MemberDetailInfo queryMember() {
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY_VALUE, null);
        if (array != null) {
            try {
                InterfaceMember.pbui_Type_MemberDetailInfo pbui_type_memberDetailInfo = InterfaceMember.pbui_Type_MemberDetailInfo.parseFrom(array);
                LogUtils.i(TAG, "queryMember:  查询参会人员成功 --->>> ");
                return pbui_type_memberDetailInfo;
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryMember:  查询参会人员失败 --->>> ");
        return null;
    }

    /**
     * 查询参会人员详细信息
     */
    public InterfaceMember.pbui_Type_MeetMemberDetailInfo queryMemberDetailed() {
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DETAILINFO.getNumber(), null);
        if (array != null) {
            try {
                LogUtils.e(TAG, "queryMemberDetailed :  查询参会人员详细信息成功 --> ");
                return InterfaceMember.pbui_Type_MeetMemberDetailInfo.parseFrom(array);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryMemberDetailed :  查询参会人员详细信息失败 --> ");
        return null;
    }

    /**
     * 查询会议排位
     */
    public InterfaceRoom.pbui_Type_MeetSeatDetailInfo queryMeetRanking() {
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSEAT.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY_VALUE, null);
        if (array != null) {
            try {
                LogUtils.e(TAG, "queryMeetRanking :  查询会议排位成功 --> ");
                return InterfaceRoom.pbui_Type_MeetSeatDetailInfo.parseFrom(array);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryMeetRanking :  查询会议排位失败 --> ");
        return null;
    }

    /**
     * 修改会议排位
     */
    public void modifyMeetRanking(int nameid, int role, int seatid) {
        InterfaceRoom.pbui_Item_MeetSeatDetailInfo.Builder builder1 = InterfaceRoom.pbui_Item_MeetSeatDetailInfo.newBuilder();
        builder1.setNameId(nameid);
        builder1.setSeatid(seatid);
        builder1.setRole(role);
        InterfaceRoom.pbui_Type_MeetSeatDetailInfo.Builder builder = InterfaceRoom.pbui_Type_MeetSeatDetailInfo.newBuilder();
        builder.addItem(builder1);
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSEAT.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFY_VALUE, builder.build().toByteArray());
        LogUtils.e(TAG, "modifMeetRanking:  修改会议排位 --->>>nameid: " + nameid + ", role:  " + role + " , devid: " + seatid);
    }


    /**
     * 添加参会人员
     */
    public void addAttendPeople(InterfaceMember.pbui_Item_MemberDetailInfo info) {
        InterfaceMember.pbui_Type_MemberDetailInfo build = InterfaceMember.pbui_Type_MemberDetailInfo.newBuilder()
                .addItem(info).build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "addAttendPeople:  添加参会人员 --->>> ");
    }


    /**
     * 普通缓存会议数据
     *
     * @param type 要缓存的数据
     */
    public void cacheData(int type) {
        InterfaceBase.pbui_MeetCacheOper build = InterfaceBase.pbui_MeetCacheOper.newBuilder()
                .setCacheflag(InterfaceMacro.Pb_CacheFlag.Pb_MEET_CACEH_FLAG_ZERO.getNumber())
                .setId(1)
                .build();
        jni.call_method(type, InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_CACHE_VALUE, build.toByteArray());
    }

    /**
     * 强制缓存会议数据
     *
     * @param type 要缓存的数据
     */
    public void mandatoryCacheData(int type) {
        InterfaceBase.pbui_MeetCacheOper build = InterfaceBase.pbui_MeetCacheOper.newBuilder()
                .setCacheflag(InterfaceMacro.Pb_CacheFlag.Pb_MEET_CACEH_FLAG_FORCE.getNumber())
                .setId(1)
                .build();
        jni.call_method(type, InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_CACHE_VALUE, build.toByteArray());
    }

    /**
     * 初始化播放资源
     *
     * @param resid
     * @param w
     * @param h
     * @return
     */
    public void initVideoRes(int resid, int w, int h) {
        InterfacePlaymedia.pbui_Type_MeetInitPlayRes.Builder builder = InterfacePlaymedia.pbui_Type_MeetInitPlayRes.newBuilder();
        builder.setRes(resid);
        builder.setY(0);
        builder.setX(0);
        builder.setW(w);
        builder.setH(h);
        InterfacePlaymedia.pbui_Type_MeetInitPlayRes build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEDIAPLAY.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_INIT.getNumber(), build.toByteArray());
        LogUtils.d(TAG, "initVideoRes:  初始化播放资源 --->>> " + w + "," + h);
    }

    /**
     * 释放播放资源
     *
     * @return
     */
    public void releaseVideoRes(int resValue) {
        InterfacePlaymedia.pbui_Type_MeetDestroyPlayRes.Builder builder = InterfacePlaymedia.pbui_Type_MeetDestroyPlayRes.newBuilder();
        builder.setRes(resValue);
        InterfacePlaymedia.pbui_Type_MeetDestroyPlayRes build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEDIAPLAY.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DESTORY.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "releaseVideoRes :  释放播放资源 --->>> resValue= " + resValue);
    }


    /**
     * 按属性ID查询指定设备属性
     *
     * @param propetyid InterfaceMacro.Pb_MeetDevicePropertyID
     * @param devId     =0是本机
     * @return pbui_DeviceInt32uProperty（整数）如果是查询的网络状态=0离线，=1在线
     * pbui_DeviceStringProperty（字符串）
     */
    public byte[] queryDevicePropertiesById(int propetyid, int devId) {
        InterfaceDevice.pbui_MeetDeviceQueryProperty.Builder builder = InterfaceDevice.pbui_MeetDeviceQueryProperty.newBuilder();
        builder.setPropertyid(propetyid);
        builder.setDeviceid(devId);
        builder.setParamterval(0);
        InterfaceDevice.pbui_MeetDeviceQueryProperty build = builder.build();
        byte[] bytes = build.toByteArray();
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEINFO.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERYPROPERTY.getNumber(), bytes);
        if (array == null) {
            LogUtils.e(TAG, "queryDevicePropertiesById :  按属性ID查询指定设备属性失败 --->>> ");
            return null;
        }
        LogUtils.e(TAG, "queryDevicePropertiesById:  按属性ID查询指定设备属性成功 --->>> ");
        return array;
    }

    /**
     * 查询会议排位属性(用于查询参会人角色)
     *
     * @param propertyid 数据ID
     */
    public InterfaceBase.pbui_CommonInt32uProperty queryMeetRankingProperty(int propertyid) {
        InterfaceBase.pbui_CommonQueryProperty build = InterfaceBase.pbui_CommonQueryProperty.newBuilder()
//                .setParameterval(parameterval)
                .setPropertyid(propertyid)
                .build();
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSEAT_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERYPROPERTY_VALUE, build.toByteArray());
        if (array != null) {
            try {
                LogUtils.e(TAG, "queryMeetRankingProperty :  查询会议排位属性 成功 --> ");
                return InterfaceBase.pbui_CommonInt32uProperty.parseFrom(array);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryMeetRankingProperty :  查询会议排位属性 失败 --> ");
        return null;
    }

    /**
     * 查询会议功能
     */
    public InterfaceMeetfunction.pbui_Type_MeetFunConfigDetailInfo queryMeetFunction() {
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_FUNCONFIG.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array != null) {
            try {
                LogUtils.e(TAG, "queryMeetFunction :  查询会议功能成功 --> ");
                return InterfaceMeetfunction.pbui_Type_MeetFunConfigDetailInfo.parseFrom(array);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryMeetFunction :  查询会议功能失败 --> ");
        return null;
    }

    /**
     * 查询议程
     */
    public InterfaceAgenda.pbui_meetAgenda queryAgenda() {
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETAGENDA.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array != null) {
            try {
                LogUtils.e(TAG, "queryAgenda :  查询议程成功 --> ");
                return InterfaceAgenda.pbui_meetAgenda.parseFrom(array);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryAgenda :  查询议程失败 --> ");
        return null;
    }

    /**
     * 149.查询文件属性
     *
     * @return
     */
    public byte[] queryFileProperty(int propertyid, int parmeterval) {
        InterfaceBase.pbui_CommonQueryProperty.Builder builder = InterfaceBase.pbui_CommonQueryProperty.newBuilder();
        builder.setPropertyid(propertyid);
        builder.setParameterval(parmeterval);
        InterfaceBase.pbui_CommonQueryProperty build = builder.build();
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORYFILE.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERYPROPERTY.getNumber(), build.toByteArray());
        if (array == null) {
            LogUtils.e(TAG, "queryFileProperty 查询文件属性 失败");
            return new byte[0];
        }
        LogUtils.i(TAG, "queryFileProperty 查询文件属性 成功");
        return array;
    }


    /**
     * 查询会议目录
     */
    public InterfaceFile.pbui_Type_MeetDirDetailInfo queryMeetDir() {
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORY.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array != null) {
            try {
                LogUtils.e(TAG, "queryMeetDir :  查询会议目录成功 --> ");
                return InterfaceFile.pbui_Type_MeetDirDetailInfo.parseFrom(array);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryMeetDir :  查询会议目录失败 --> ");
        return null;
    }

    /**
     * 143.查询会议目录文件
     */
    public InterfaceFile.pbui_Type_MeetDirFileDetailInfo queryMeetDirFile(int dirId) {
        InterfaceBase.pbui_QueryInfoByID.Builder builder = InterfaceBase.pbui_QueryInfoByID.newBuilder();
        builder.setId(dirId);
        InterfaceBase.pbui_QueryInfoByID build = builder.build();
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORYFILE.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), build.toByteArray());
        if (array != null) {
            LogUtils.e(TAG, "queryMeetDirFile :  查询会议目录文件成功 --> " + dirId);
            try {
                return InterfaceFile.pbui_Type_MeetDirFileDetailInfo.parseFrom(array);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryMeetDirFile :  查询会议目录文件失败 --> " + dirId);
        return null;
    }

    /**
     * 上传文件
     *
     * @param uploadflag 上传标志 参见 Pb_Upload_Flag =1只有当上传结束才回调进度
     * @param dirid      上传的目录ID 参见 Pb_Upload_DefaultDirId
     * @param attrib     文件属性 参见 Pb_MeetFileAttrib
     * @param newname    上传后的新名称
     * @param pathname   全路径名
     * @param userval    用户自定义的值
     * @param userStr    用户传入的自定义字串
     */
    public void uploadFile(int uploadflag, int dirid, int attrib, String newname, String pathname, int userval, String userStr) {
        InterfaceUpload.pbui_Type_AddUploadFile.Builder builder = InterfaceUpload.pbui_Type_AddUploadFile.newBuilder();
        builder.setUploadflag(uploadflag);
        builder.setDirid(dirid);
        builder.setAttrib(attrib);
        builder.setNewname(s2b(newname));
        builder.setPathname(s2b(pathname));
        builder.setUserval(userval);
        builder.setUserstr(s2b(userStr));
        InterfaceUpload.pbui_Type_AddUploadFile build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_UPLOAD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "uploadFile :   --> 上传文件 " + newname + ", dirid= " + dirid + ", pathname= " + pathname);
    }


    /**
     * 媒体播放操作
     *
     * @param mediaid
     * @param devIds
     * @param pos
     * @param res
     * @param triggeruserval Pb_TriggerUsedef
     * @param flag           Pb_MeetPlayFlag
     */
    public void mediaPlayOperate(int mediaid, List<Integer> devIds, int pos, int res, int triggeruserval, int flag) {
        InterfacePlaymedia.pbui_Type_MeetDoMediaPlay build = InterfacePlaymedia.pbui_Type_MeetDoMediaPlay.newBuilder()
                .setPlayflag(flag)
                .setPos(pos)
                .setMediaid(mediaid)
                .setTriggeruserval(triggeruserval)
                .addRes(res)
                .addAllDeviceid(devIds)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEDIAPLAY_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_START_VALUE, build.toByteArray());
        LogUtils.e(TAG, "mediaPlayOperate  媒体播放操作 mediaid=" + mediaid);
    }


    /**
     * 查询参会人员权限
     */
    public InterfaceMember.pbui_Type_MemberPermission queryAttendPeoplePermissions() {
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBERPERMISSION.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array != null) {
            try {
                LogUtils.e(TAG, "queryAttendPeoplePermissions :  查询参会人员权限成功 --> ");
                return InterfaceMember.pbui_Type_MemberPermission.parseFrom(array);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryAttendPeoplePermissions :  查询参会人员权限失败 --> ");
        return null;
    }

    /**
     * 向指定设备申请权限
     *
     * @param devid     设备id
     * @param privilege 权限码 InterfaceMacro.Pb_MemberPermissionPropertyID
     */
    public void applyPermission(int devid, int privilege) {
        InterfaceDevice.pbui_Type_MeetRequestPrivilege.Builder builder = InterfaceDevice.pbui_Type_MeetRequestPrivilege.newBuilder();
        builder.addDevid(devid);
        builder.setPrivilege(privilege);
        InterfaceDevice.pbui_Type_MeetRequestPrivilege build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEOPER.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_REQUESTPRIVELIGE.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "applyPermission:  发送请求参会人员权限请求 --->>> 申请的权限： " + privilege);
    }

    /**
     * 网页查询
     */
    public InterfaceBase.pbui_meetUrl queryWebUrl() {
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEFAULTURL.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array != null) {
            try {
                LogUtils.e(TAG, "queryWebUrl  网页查询 --->>> 成功");
                return InterfaceBase.pbui_meetUrl.parseFrom(array);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryWebUrl  网页查询 --->>> 失败");
        return null;
    }

    /**
     * 会场设备排位详细信息
     *
     * @param id 会议室ID
     */
    public InterfaceRoom.pbui_Type_MeetRoomDevSeatDetailInfo placeDeviceRankingInfo(int id) {
        InterfaceBase.pbui_QueryInfoByID.Builder builder = InterfaceBase.pbui_QueryInfoByID.newBuilder();
        builder.setId(id);
        InterfaceBase.pbui_QueryInfoByID build = builder.build();
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOMDEVICE.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DETAILINFO_VALUE, build.toByteArray());
        if (array != null) {
            try {
                LogUtils.e(TAG, "placeDeviceRankingInfo :  查询会场设备排位详细信息成功 --> id=" + id);
                return InterfaceRoom.pbui_Type_MeetRoomDevSeatDetailInfo.parseFrom(array);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "placeDeviceRankingInfo :  查询会场设备排位详细信息失败 --> id=" + id);
        return null;
    }

    /**
     * 查询签到
     */
    public InterfaceSignin.pbui_Type_MeetSignInDetailInfo querySignin() throws InvalidProtocolBufferException {
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSIGN.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array == null) {
            LogUtils.e(TAG, "querySignin :  查询签到失败 --> ");
            return null;
        }
        LogUtils.e(TAG, "querySignin :  查询签到成功 --> ");
        return InterfaceSignin.pbui_Type_MeetSignInDetailInfo.parseFrom(array);
    }

    /**
     * 获取指定会场ID的底图ID
     *
     * @param roomid 会场ID
     * @return
     * @throws InvalidProtocolBufferException
     */
    public int queryMeetRoomProperty(int roomid) throws InvalidProtocolBufferException {
        InterfaceBase.pbui_CommonQueryProperty build = InterfaceBase.pbui_CommonQueryProperty.newBuilder()
                .setParameterval(roomid)
                .setParameterval2(roomid)
                .setPropertyid(InterfaceMacro.Pb_MeetRoomPropertyID.Pb_MEETROOM_PROPERTY_BGPHOTOID.getNumber())
                .build();
        byte[] bytes = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOM.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERYPROPERTY.getNumber(), build.toByteArray());
        int propertyval = 0;
        if (bytes != null) {
            InterfaceBase.pbui_CommonInt32uProperty pbui_commonInt32uProperty = InterfaceBase.pbui_CommonInt32uProperty.parseFrom(bytes);
            propertyval = pbui_commonInt32uProperty.getPropertyval();
        }
        LogUtils.e(TAG, "queryMeetRoomProperty :  获取指定会场ID的底图ID --> " + roomid + ",  propertyval:" + propertyval);
        return propertyval;
    }


    /**
     * 查询设备信息
     */
    public InterfaceDevice.pbui_Type_DeviceDetailInfo queryDeviceInfo() {
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEINFO.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array != null) {
            try {
                LogUtils.e(TAG, "queryDeviceInfo :  查询设备信息成功 --> ");
                return InterfaceDevice.pbui_Type_DeviceDetailInfo.parseFrom(array);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryDeviceInfo :  查询设备信息失败 --> ");
        return null;
    }

    /**
     * 查询全部会议交流信息
     */
    public InterfaceIM.pbui_Type_MeetIMDetailInfo queryAllChatMessage() {
        byte[] bytes = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETIM_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY_VALUE, null);
        if (bytes != null) {
            try {
                LogUtils.i(TAG, "queryAllChatMessage 查询全部会议交流信息成功");
                return InterfaceIM.pbui_Type_MeetIMDetailInfo.parseFrom(bytes);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryAllChatMessage 查询全部会议交流信息失败");
        return null;
    }

    /**
     * 根据参会人查询消息
     *
     * @param memberId 人员id
     */
    public InterfaceIM.pbui_TypePageResQueryrMsgInfo queryMessageByMemberId(int memberId) {
        InterfaceIM.pbui_Type_MeetComplexQueryIM build = InterfaceIM.pbui_Type_MeetComplexQueryIM.newBuilder()
                .setQueryflag(InterfaceMacro.Pb_MeetIMMSG_QueryFlag.Pb_COMPLEXQUERY_MEMBERID_VALUE |
                        InterfaceMacro.Pb_MeetIMMSG_QueryFlag.Pb_COMPLEXQUERY_MSGTYPE_VALUE)
                .setMsgtype(0)
                .setMemberid(memberId)
                .setPageindex(0)
                .build();
        byte[] bytes = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETIM_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_COMPLEXQUERY_VALUE, build.toByteArray());
        if (bytes != null) {
            try {
                LogUtils.i(TAG, "queryMessageByMemberId 查询指定参会人消息成功 " + memberId);
                return InterfaceIM.pbui_TypePageResQueryrMsgInfo.parseFrom(bytes);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryMessageByMemberId 查询指定参会人消息失败 " + memberId);
        return null;
    }

    /**
     * 185.发送会议交流信息
     *
     * @return
     */
    public void sendChatMessage(String msg, int msgType, List<Integer> ids) {
        InterfaceIM.pbui_Type_SendMeetIM.Builder builder = InterfaceIM.pbui_Type_SendMeetIM.newBuilder();
        builder.setMsg(s2b(msg));
        builder.setMsgtype(msgType);
        builder.addAllUserids(ids);
        InterfaceIM.pbui_Type_SendMeetIM build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETIM.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SEND.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "sendMeetInfo:  发送会议交流信息 --->>> ");
    }

    /**
     * 62.执行终端控制
     *
     * @param oper enum Pb_DeviceControlFlag
     * @return
     */
    public void executeTerminalControl(int oper, int operval1, int operval2, List<Integer> devids) {
        InterfaceDevice.pbui_Type_DeviceOperControl.Builder builder = InterfaceDevice.pbui_Type_DeviceOperControl.newBuilder();
        builder.setOper(oper);
        builder.setOperval1(operval1);
        builder.setOperval2(operval2);
        builder.addAllDevid(devids);
        InterfaceDevice.pbui_Type_DeviceOperControl build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICECONTROL.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_CONTROL.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "executeTerminalControl:  执行终端控制 --->>> oper= " + oper);
    }

    /**
     * 网络唤醒设备
     *
     * @param ids 设备id
     */
    public void wakeOnLan(List<Integer> ids) {
        InterfaceDevice.pbui_Type_MeetDoNetReboot build = InterfaceDevice.pbui_Type_MeetDoNetReboot.newBuilder()
                .addAllDevid(ids)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEOPER_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_REBOOT_VALUE, build.toByteArray());
    }

    /**
     * 辅助签到操作
     *
     * @param devids
     */
    public void assistedSignIn(List<Integer> devids) {
        InterfaceDevice.pbui_MeetDoEnterMeet.Builder builder = InterfaceDevice.pbui_MeetDoEnterMeet.newBuilder();
        builder.addAllDevid(devids);
        InterfaceDevice.pbui_MeetDoEnterMeet build = builder.build();
        byte[] bytes = build.toByteArray();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEOPER.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ENTER.getNumber(), bytes);
        LogUtils.e(TAG, "signAlterationOperate:  辅助签到操作 --->>> " + devids);
    }


    /**
     * 200.查询投票
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public InterfaceVote.pbui_Type_MeetVoteDetailInfo queryVote() {
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETVOTEINFO.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array != null) {
            try {
                LogUtils.e(TAG, "queryVote :  查询投票成功 --> ");
                return InterfaceVote.pbui_Type_MeetVoteDetailInfo.parseFrom(array);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryVote :  查询投票失败 --> ");
        return null;
    }

    public InterfaceVote.pbui_Type_MeetVoteDetailInfo queryVoteByType(int voteType) {
        InterfaceVote.pbui_Type_MeetVoteComplexQuery build = InterfaceVote.pbui_Type_MeetVoteComplexQuery.newBuilder()
                .setMaintype(voteType).build();
        byte[] bytes = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETVOTEINFO_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_COMPLEXQUERY_VALUE, build.toByteArray());
        if (bytes != null) {
            try {
                LogUtils.d(TAG, "queryVoteByType -->" + "查询指定类别投票成功：" + voteType);
                return InterfaceVote.pbui_Type_MeetVoteDetailInfo.parseFrom(bytes);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }

        }
        LogUtils.d(TAG, "queryVoteByType -->" + "查询指定类别投票失败：" + voteType);
        return null;
    }


    /**
     * 发起投票
     *
     * @param memberIds 参会人ID
     * @param voteid    投票ID
     * @param seconds   单位秒
     */
    public void launchVote(List<Integer> memberIds, int voteid, int seconds, int voteFlag) {
        InterfaceVote.pbui_ItemVoteStart.Builder b = InterfaceVote.pbui_ItemVoteStart.newBuilder();
        b.setVoteid(voteid);
        b.setVoteflag(voteFlag);
        b.setTimeouts(seconds);
        b.addAllMemberid(memberIds);
        InterfaceVote.pbui_Type_MeetStartVoteInfo.Builder builder = InterfaceVote.pbui_Type_MeetStartVoteInfo.newBuilder();
        builder.addItem(b);
        InterfaceVote.pbui_Type_MeetStartVoteInfo build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETONVOTING.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_START.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "launchVote:  发起投票 --->>> voteFlag=" + voteFlag);
    }

    /**
     * 删除投票
     */
    public void deleteVote(Integer voteid) {
        InterfaceVote.pbui_Type_MeetStopVoteInfo.Builder builder = InterfaceVote.pbui_Type_MeetStopVoteInfo.newBuilder();
        builder.addVoteid(voteid);
        InterfaceVote.pbui_Type_MeetStopVoteInfo build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETONVOTING.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "deleteVote:  删除投票 --->>> ");
    }

    /**
     * 停止投票
     */
    public void stopVote(int voteid) {
        InterfaceVote.pbui_Type_MeetStopVoteInfo.Builder builder = InterfaceVote.pbui_Type_MeetStopVoteInfo.newBuilder();
        builder.addVoteid(voteid);
        InterfaceVote.pbui_Type_MeetStopVoteInfo build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETONVOTING.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_STOP.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "stopVote:  停止投票 --->>> ");
    }

    /**
     * 查询指定投票的提交人
     */
    public InterfaceVote.pbui_Type_MeetVoteSignInDetailInfo querySubmittedVoters(int voteId) {
        InterfaceBase.pbui_QueryInfoByID build = InterfaceBase.pbui_QueryInfoByID.newBuilder()
                .setId(voteId).build();
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETVOTESIGNED.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), build.toByteArray());
        if (array != null) {
            try {
                LogUtils.e(TAG, "querySubmittedVoters :  查询指定投票的提交人成功 --> ");
                return InterfaceVote.pbui_Type_MeetVoteSignInDetailInfo.parseFrom(array);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "querySubmittedVoters :  查询指定投票的提交人失败 --> ");
        return null;
    }

    public void streamPlay(int sourceDevId, int subid, int triggeruserval, int resid, int deviceId) {
        List<Integer> res = new ArrayList<>();
        res.add(resid);
        List<Integer> devs = new ArrayList<>();
        devs.add(deviceId);
        streamPlay(sourceDevId, subid, triggeruserval, res, devs);
    }

    /**
     * 流播放(多个资源)
     *
     * @param sourceDevId    要抓取屏幕的设备ID
     * @param subid          2：抓取屏幕 3：摄像头
     * @param triggeruserval 一般写 0  Pb_TriggerUsedef
     * @param allres         播放所用的资源
     * @param alldeviceid    通知的目标设备（进行播放的设备）
     */
    public void streamPlay(int sourceDevId, int subid, int triggeruserval, List<Integer> allres, List<Integer> alldeviceid) {
        InterfaceStream.pbui_Type_MeetDoStreamPlay build = InterfaceStream.pbui_Type_MeetDoStreamPlay.newBuilder()
                .setSrcdeviceid(sourceDevId)
                .setSubid(subid)
                .setPlayflag(InterfaceMacro.Pb_MeetPlayFlag.Pb_MEDIA_PLAYFLAG_ZERO_VALUE)
                .setTriggeruserval(triggeruserval)
                .addAllRes(allres)
                .addAllDeviceid(alldeviceid)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_STREAMPLAY.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_START.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "streamPlay:  流播放 --->>> ");
    }

    /**
     * 流播放(单个资源)
     *
     * @param sourceDevId    要抓取屏幕的设备ID
     * @param subid          2：抓取屏幕 3：摄像头
     * @param triggeruserval 一般写 0  Pb_TriggerUsedef
     * @param resId          播放所用的资源
     * @param alldeviceid    通知的目标设备（进行播放的设备）
     */
    public void streamPlay(int sourceDevId, int subid, int triggeruserval, int resId, List<Integer> alldeviceid) {
        InterfaceStream.pbui_Type_MeetDoStreamPlay build = InterfaceStream.pbui_Type_MeetDoStreamPlay.newBuilder()
                .setSrcdeviceid(sourceDevId)
                .setSubid(subid)
                .setPlayflag(InterfaceMacro.Pb_MeetPlayFlag.Pb_MEDIA_PLAYFLAG_ZERO_VALUE)
                .setTriggeruserval(triggeruserval)
                .addRes(resId)
                .addAllDeviceid(alldeviceid)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_STREAMPLAY.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_START.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "streamPlay:  流播放单个资源 --->>> ");
    }

    /**
     * 播放目标设备的屏幕
     *
     * @param sourceId 设备id
     */
    public void playTargetScreen(int sourceId) {
        InterfaceStream.pbui_Type_MeetDoStreamPlay build = InterfaceStream.pbui_Type_MeetDoStreamPlay.newBuilder()
                .setSrcdeviceid(sourceId)
                .setSubid(SCREEN_SUB_ID)
                .setPlayflag(InterfaceMacro.Pb_MeetPlayFlag.Pb_MEDIA_PLAYFLAG_ZERO_VALUE)
                .setTriggeruserval(InterfaceMacro.Pb_TriggerUsedef.Pb_EXCEC_USERDEF_FLAG_ZERO_VALUE)
                .addRes(RESOURCE_ID_0)
                .addDeviceid(GlobalValue.localDeviceId)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_STREAMPLAY_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_START_VALUE,
                build.toByteArray());
        LogUtils.i(TAG, "playTargetScreen 播放目标设备的屏幕 sourceId=" + sourceId
                + ",GlobalValue.localDeviceId=" + GlobalValue.localDeviceId);
    }

    /**
     * 播放目标设备的摄像头
     *
     * @param sourceId 设备id
     */
    public void playTargetCamera(int sourceId) {
        InterfaceStream.pbui_Type_MeetDoStreamPlay build = InterfaceStream.pbui_Type_MeetDoStreamPlay.newBuilder()
                .setSrcdeviceid(sourceId)
                .setSubid(CAMERA_SUB_ID)
                .setPlayflag(InterfaceMacro.Pb_MeetPlayFlag.Pb_MEDIA_PLAYFLAG_ZERO_VALUE)
                .setTriggeruserval(InterfaceMacro.Pb_TriggerUsedef.Pb_EXCEC_USERDEF_FLAG_ZERO_VALUE)
                .addRes(RESOURCE_ID_0)
                .addDeviceid(GlobalValue.localDeviceId)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_STREAMPLAY.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_START.getNumber(), build.toByteArray());
        LogUtils.i(TAG, "playTargetCamera 播放目标设备的摄像头 sourceId=" + sourceId);
    }

    /**
     * 停止资源操作
     */
    public void stopResourceOperate(int res, int devId) {
        InterfaceStop.pbui_Type_MeetDoStopResWork build = InterfaceStop.pbui_Type_MeetDoStopResWork.newBuilder()
                .addRes(res)
                .addDeviceid(devId)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_STOPPLAY.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_CLOSE.getNumber(), build.toByteArray());
        LogUtils.i(TAG, "stopResourceOperate:  停止资源操作  ---> ");
    }

    /**
     * 停止资源操作
     */
    public void stopResourceOperate(int res, List<Integer> devIds) {
        InterfaceStop.pbui_Type_MeetDoStopResWork build = InterfaceStop.pbui_Type_MeetDoStopResWork.newBuilder()
                .addRes(res)
                .addAllDeviceid(devIds)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_STOPPLAY.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_CLOSE.getNumber(), build.toByteArray());
        LogUtils.i(TAG, "stopResourceOperate:  停止资源操作  ---> ");
    }

    /**
     * 停止资源操作
     */
    public void stopResourceOperate(List<Integer> res, List<Integer> devIds) {
        InterfaceStop.pbui_Type_MeetDoStopResWork build = InterfaceStop.pbui_Type_MeetDoStopResWork.newBuilder()
                .addAllRes(res)
                .addAllDeviceid(devIds)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_STOPPLAY.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_CLOSE.getNumber(), build.toByteArray());
        LogUtils.i(TAG, "stopResourceOperate:  停止资源操作  ---> ");
    }


    /**
     * 设置播放暂停
     */
    public void setPlayPause(int resIndex, List<Integer> devIds) {
        InterfacePlaymedia.pbui_Type_MeetDoPlayControl.Builder builder = InterfacePlaymedia.pbui_Type_MeetDoPlayControl.newBuilder();
        builder.setResindex(resIndex);
        builder.addAllDeviceid(devIds);
        InterfacePlaymedia.pbui_Type_MeetDoPlayControl build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEDIAPLAY.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_PAUSE.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "setPlayStop:  设置播放暂停 --->>> ");
    }

    /**
     * 设置播放回复
     */
    public void setPlayRecover(int resIndex, List<Integer> devIds) {
        InterfacePlaymedia.pbui_Type_MeetDoPlayControl.Builder builder = InterfacePlaymedia.pbui_Type_MeetDoPlayControl.newBuilder();
        builder.setResindex(resIndex);
        builder.addAllDeviceid(devIds);
        InterfacePlaymedia.pbui_Type_MeetDoPlayControl build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEDIAPLAY.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_PLAY.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "setPlayRecover:  设置播放回复 --->>> ");
    }

    /**
     * 设置播放位置
     */
    public void setPlayPlace(int resIndex, int pos, List<Integer> devIds, int triggeruserval, int playflag) {
        InterfacePlaymedia.pbui_Type_MeetDoSetPlayPos.Builder builder = InterfacePlaymedia.pbui_Type_MeetDoSetPlayPos.newBuilder();
        builder.setResindex(resIndex);
        builder.setPos(pos);
        builder.addAllDeviceid(devIds);
        builder.setTriggeruserval(triggeruserval);
        builder.setPlayflag(playflag);
        InterfacePlaymedia.pbui_Type_MeetDoSetPlayPos build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEDIAPLAY.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MOVE.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "setPlayPlace:  设置播放位置 --->>> ");
    }

    /**
     * 添加公告
     *
     * @param item
     */
    public void addBullet(InterfaceBullet.pbui_Item_BulletDetailInfo item) {
        InterfaceBullet.pbui_BulletDetailInfo.Builder builder = InterfaceBullet.pbui_BulletDetailInfo.newBuilder();
        builder.addItem(item);
        InterfaceBullet.pbui_BulletDetailInfo build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETBULLET.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "addNotice:  添加公告 --->>> ");
    }

    /**
     * 查询公告
     */
    public InterfaceBullet.pbui_BulletDetailInfo queryBullet() {
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETBULLET_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY_VALUE, null);
        if (array != null) {
            try {
                LogUtils.e(TAG, "queryBullet:  查询公告成功 --->>> ");
                return InterfaceBullet.pbui_BulletDetailInfo.parseFrom(array);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryBullet:  查询公告失败 --->>> ");
        return null;
    }

    /**
     * 发布公告
     *
     * @param bullet 公告
     */
    public void launchBullet(InterfaceBullet.pbui_Item_BulletDetailInfo bullet) {
        InterfaceBullet.pbui_Type_MeetPublishBulletInfo build = InterfaceBullet.pbui_Type_MeetPublishBulletInfo.newBuilder()
                .setItem(bullet)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETBULLET_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_PUBLIST_VALUE, build.toByteArray());
        LogUtils.e(TAG, "launchBullet:  发布公告 --->>> ");
    }

    /**
     * 停止公告
     *
     * @param bulletid
     * @param devids
     */
    public void stopBullect(int bulletid, List<Integer> devids) {
        InterfaceBullet.pbui_Type_StopBullet.Builder builder = InterfaceBullet.pbui_Type_StopBullet.newBuilder();
        builder.setBulletid(bulletid);
        builder.addAllPdevid(devids);
        InterfaceBullet.pbui_Type_StopBullet build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETBULLET.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_STOP.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "stopBullect :  停止公告 --> " + devids.toString());
    }

    /**
     * 修改公告
     *
     * @param item
     */
    public void modifyBullet(InterfaceBullet.pbui_Item_BulletDetailInfo item) {
        InterfaceBullet.pbui_BulletDetailInfo.Builder builder = InterfaceBullet.pbui_BulletDetailInfo.newBuilder();
        builder.addItem(item);
        InterfaceBullet.pbui_BulletDetailInfo build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETBULLET.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFY.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "modifyBullet:  修改公告 --->>> ");
    }

    /**
     * 删除公告
     *
     * @param item
     */
    public void deleteBullet(InterfaceBullet.pbui_Item_BulletDetailInfo item) {
        InterfaceBullet.pbui_BulletDetailInfo.Builder builder = InterfaceBullet.pbui_BulletDetailInfo.newBuilder();
        builder.addItem(item);
        InterfaceBullet.pbui_BulletDetailInfo build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETBULLET.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "deleteBullet:  删除公告 --->>> ");
    }


    /**
     * 查询可加入的同屏会话
     */
    public InterfaceDevice.pbui_Type_DeviceResPlay queryCanJoin() {
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEINFO.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_RESINFO.getNumber(), null);
        if (array != null) {
            try {
                LogUtils.e(TAG, "queryCanJoin  查询可加入的同屏会话成功 --->>> ");
                return InterfaceDevice.pbui_Type_DeviceResPlay.parseFrom(array);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryCanJoin  查询可加入的同屏会话失败 --->>> ");
        return null;
    }


    /**
     * 189.查询发起的投票
     */
    public InterfaceVote.pbui_Type_MeetOnVotingDetailInfo queryInitiateVote() {
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETONVOTING.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array != null) {
            try {
                LogUtils.e(TAG, "queryInitiateVote:  查询发起的投票成功 --->>> ");
                return InterfaceVote.pbui_Type_MeetOnVotingDetailInfo.parseFrom(array);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryInitiateVote:  查询发起的投票失败 --->>> ");
        return null;
    }

    /**
     * 查询指定id的投票
     */
    public InterfaceVote.pbui_Type_MeetOnVotingDetailInfo queryVoteById(int voteId) {
        InterfaceBase.pbui_QueryInfoByID build = InterfaceBase.pbui_QueryInfoByID.newBuilder()
                .setId(voteId)
                .build();
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETONVOTING.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SINGLEQUERYBYID.getNumber(), build.toByteArray());
        if (array != null) {
            try {
                LogUtils.e(TAG, "queryVoteById:  查询指定id的投票成功");
                return InterfaceVote.pbui_Type_MeetOnVotingDetailInfo.parseFrom(array);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryVoteById:  查询指定id的投票失败");
        return null;
    }


    /**
     * 停止设备对讲
     *
     * @param devid 发起端设备ID
     */
    public void stopDeviceIntercom(int devid) {
        InterfaceDevice.pbui_Type_DoExitDeviceChat build = InterfaceDevice.pbui_Type_DoExitDeviceChat.newBuilder()
                .setOperdeviceid(devid)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEOPER_VALUE, InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_EXITCHAT_VALUE, build.toByteArray());
    }

    /**
     * 设备对讲
     *
     * @param flag Interface_device.Pb_DeviceInviteFlag
     */
    public void deviceIntercom(List<Integer> devids, int flag) {
        InterfaceDevice.pbui_Type_DoDeviceChat build = InterfaceDevice.pbui_Type_DoDeviceChat.newBuilder()
                .addAllDevid(devids)
                .setInviteflag(flag)
                .build();
        LogUtils.d(TAG, "deviceIntercom -->" + "设备对讲 flag = " + flag);
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEOPER_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_REQUESTINVITE_VALUE, build.toByteArray());
    }

    /**
     * 回复设备对讲
     *
     * @param devid 回复的设备
     * @param flag  Interface_device.Pb_DeviceInviteFlag  =1同意，=0拒绝
     *              Pb_DEVICE_INVITECHAT_FLAG_DEAL
     */
    public void replyDeviceIntercom(int devid, int flag) {
        InterfaceDevice.pbui_Type_DeviceChat build = InterfaceDevice.pbui_Type_DeviceChat.newBuilder()
                .setOperdeviceid(devid)
                .setInviteflag(flag)
                .build();
        LogUtils.d(TAG, "deviceIntercom -->" + "回复设备对讲");
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEOPER_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_RESPONSEINVITE_VALUE, build.toByteArray());
    }


    /**
     * 回复参会人员权限请求
     *
     * @param devid      回复给的对象
     * @param returncode 1=同意,0=不同意
     */
    public void revertAttendPermissionsRequest(int devid, int returncode) {
        InterfaceDevice.pbui_Type_MeetResponseRequestPrivilege.Builder builder = InterfaceDevice.pbui_Type_MeetResponseRequestPrivilege.newBuilder();
        builder.addDevid(devid);
        builder.setReturncode(returncode);
        InterfaceDevice.pbui_Type_MeetResponseRequestPrivilege build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEOPER.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_RESPONSEPRIVELIGE.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "revertPermissionsRequest:    回复参会人员权限请求 --->>> ");
    }


    /**
     * 提交投票结果
     *
     * @param selcnt  选择的个数
     * @param voteid  投票ID
     * @param selitem 选择的答案（十进制代表数）
     */
    public void submitVoteResult(int selcnt, int voteid, int selitem) {
        InterfaceVote.pbui_Item_MeetSubmitVote.Builder builder1 = InterfaceVote.pbui_Item_MeetSubmitVote.newBuilder();
        builder1.setSelcnt(selcnt);
        builder1.setVoteid(voteid);
        builder1.setSelitem(selitem);
        InterfaceVote.pbui_Type_MeetSubmitVote.Builder builder = InterfaceVote.pbui_Type_MeetSubmitVote.newBuilder();
        builder.addItem(builder1);
        InterfaceVote.pbui_Type_MeetSubmitVote build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETONVOTING.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SUBMIT.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "submitVoteResult:  提交投票结果 --->>> ");
    }


    /**
     * 172.查询会议视频
     */
    public InterfaceVideo.pbui_Type_MeetVideoDetailInfo queryMeetVideo() {
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETVIDEO.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array != null) {
            try {
                InterfaceVideo.pbui_Type_MeetVideoDetailInfo info = InterfaceVideo.pbui_Type_MeetVideoDetailInfo.parseFrom(array);
                LogUtils.e(TAG, "queryMeetVedio :  查询会议视频成功 --> ");
                return info;
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryMeetVedio :  查询会议视频失败 --> ");
        return null;
    }

    /**
     * 191.新建一个投票
     *
     * @return
     */
    public void createVote(InterfaceVote.pbui_Item_MeetOnVotingDetailInfo vote) {
        InterfaceVote.pbui_Type_MeetOnVotingDetailInfo.Builder builder1 = InterfaceVote.pbui_Type_MeetOnVotingDetailInfo.newBuilder();
        builder1.addItem(vote);
        InterfaceVote.pbui_Type_MeetOnVotingDetailInfo build = builder1.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETONVOTING.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "createVote:  新建一个投票 --->>> ");
    }

    /**
     * 查询指定id的公告
     *
     * @param bulletid 公告id
     */
    public InterfaceBullet.pbui_BulletDetailInfo queryBulletById(int bulletid) {
        InterfaceBase.pbui_QueryInfoByID build = InterfaceBase.pbui_QueryInfoByID.newBuilder()
                .setId(bulletid)
                .build();
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETBULLET.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SINGLEQUERYBYID.getNumber(), build.toByteArray());
        if (array == null) {
            try {
                LogUtils.e(TAG, "queryBulletById :  查询指定的公告成功 --> ");
                return InterfaceBullet.pbui_BulletDetailInfo.parseFrom(array);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryBulletById :  查询指定的公告失败 --> ");
        return null;
    }

    /**
     * 192.修改一个投票
     *
     * @return
     */
    public void modifyVote(InterfaceVote.pbui_Item_MeetOnVotingDetailInfo item) {
        InterfaceVote.pbui_Type_MeetOnVotingDetailInfo.Builder builder = InterfaceVote.pbui_Type_MeetOnVotingDetailInfo.newBuilder();
        builder.addItem(item);
        InterfaceVote.pbui_Type_MeetOnVotingDetailInfo build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETONVOTING.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFY.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "modifyVote:  修改一个投票 --->>> ");
    }


    /**
     * 查询会议属性
     *
     * @param propertyid Pb_MeetPropertyID
     * @param val1
     * @param val2
     * @return pbui_CommonInt32uProperty\pbui_CommonInt64uProperty
     */
    public byte[] queryMeetingProperty(int propertyid, int val1, int val2) {
        InterfaceBase.pbui_CommonQueryProperty build = InterfaceBase.pbui_CommonQueryProperty.newBuilder()
                .setPropertyid(propertyid)
                .setParameterval(val1)
                .setParameterval2(val2)
                .build();
        byte[] bytes = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETINFO_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERYPROPERTY_VALUE, build.toByteArray());
        if (bytes == null) {
            LogUtils.e(TAG, "queryMeetingProperty -->" + "查询会议属性失败 propertyid=" + propertyid + ", val1=" + val1 + ", val2=" + val2);
            return null;
        }
        LogUtils.d(TAG, "queryMeetingProperty -->" + "查询会议属性成功 propertyid=" + propertyid + ", val1=" + val1 + ", val2=" + val2);
        return bytes;
    }


    /**
     * 查询参会人员属性
     *
     * @param propertyid   属性ID 参见 Pb_MemberPropertyID
     * @param parameterval 传入参数 为0表示本机设置定的人员id
     * @return
     */
    public InterfaceMember.pbui_Type_MeetMembeProperty queryMemberProperty(int propertyid, int parameterval) {
        InterfaceMember.pbui_Type_MeetMemberQueryProperty build = InterfaceMember.pbui_Type_MeetMemberQueryProperty.newBuilder()
                .setPropertyid(propertyid)
                .setParameterval(parameterval)
                .build();
        byte[] bytes = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERYPROPERTY.getNumber(), build.toByteArray());
        if (bytes != null) {
            try {
                LogUtils.d(TAG, "queryMemberProperty -->" + "查询参会人员属性成功");
                return InterfaceMember.pbui_Type_MeetMembeProperty.parseFrom(bytes);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryMemberProperty -->" + "查询参会人员属性失败");
        return null;
    }


    /**
     * 根据媒体ID查询文件名
     *
     * @param mediaId 文件id
     */
    public String queryFileNameByMediaId(int mediaId) {
        String fileName = "";
        byte[] bytes = queryFileProperty(InterfaceMacro.Pb_MeetFilePropertyID.Pb_MEETFILE_PROPERTY_NAME.getNumber(), mediaId);
        try {
            InterfaceBase.pbui_CommonTextProperty pbui_commonTextProperty = InterfaceBase.pbui_CommonTextProperty.parseFrom(bytes);
            fileName = pbui_commonTextProperty.getPropertyval().toStringUtf8();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        LogUtils.i(TAG, "queryFileNameByMediaId 根据媒体ID查询文件名=" + fileName);
        return fileName;
    }

    //*************************************************************** 文件评分相关 **************************************************************

    /**
     * 查询会议文件评分
     *
     * @return
     */
    public InterfaceFilescorevote.pbui_Type_UserDefineFileScore queryFileScore() {
        byte[] bytes = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_FILESCOREVOTE_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY_VALUE, null);
        if (bytes != null) {
            try {
                return InterfaceFilescorevote.pbui_Type_UserDefineFileScore.parseFrom(bytes);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 查询指定id的文件评分
     *
     * @param id 评分id
     * @return InterfaceFilescorevote.pbui_Type_UserDefineFileScore
     */
    public InterfaceFilescorevote.pbui_Type_UserDefineFileScore queryFileScoreById(int id) {
        InterfaceBase.pbui_QueryInfoByID build = InterfaceBase.pbui_QueryInfoByID.newBuilder().setId(id).build();
        byte[] bytes = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_FILESCOREVOTE_VALUE, InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SINGLEQUERYBYID_VALUE, build.toByteArray());
        if (bytes != null) {
            try {
                InterfaceFilescorevote.pbui_Type_UserDefineFileScore pbui_type_userDefineFileScore = InterfaceFilescorevote.pbui_Type_UserDefineFileScore.parseFrom(bytes);
                LogUtils.e(TAG, "查询指定id的文件评分 成功");
                return pbui_type_userDefineFileScore;
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "查询指定id的文件评分 失败");
        return null;
    }

    /**
     * 查询指定评分的提交人
     *
     * @param voteid
     * @return
     */
    public InterfaceFilescorevote.pbui_Type_UserDefineFileScoreMemberStatistic queryScoreSubmittedScore(int voteid) {
        InterfaceBase.pbui_QueryInfoByID build = InterfaceBase.pbui_QueryInfoByID.newBuilder().setId(voteid).build();
        byte[] bytes = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_FILESCOREVOTESIGN_VALUE, InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY_VALUE, build.toByteArray());
        if (bytes != null) {
            try {
                LogUtils.e(TAG, "queryScoreSubmittedScore -->" + "查询指定评分提交人成功");
                return InterfaceFilescorevote.pbui_Type_UserDefineFileScoreMemberStatistic.parseFrom(bytes);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryScoreSubmittedScore -->" + "查询指定评分提交人失败");
        return null;
    }

    /**
     * 发起评分
     *
     * @param voteid    评分ID
     * @param voteflag  Pb_VoteStartFlag
     * @param timeouts
     * @param memberIds 人员ID
     */
    public void startScore(int voteid, int voteflag, int timeouts, List<Integer> memberIds) {
        InterfaceFilescorevote.pbui_Type_StartUserDefineFileScore build = InterfaceFilescorevote.pbui_Type_StartUserDefineFileScore.newBuilder()
                .addAllMemberid(memberIds)
                .setTimeouts(timeouts)
                .setVoteflag(voteflag)
                .setVoteid(voteid)
                .build();
        LogUtils.d(TAG, "startScore -->" + "发起评分：votid= " + voteid + "，memberIds: " + memberIds.toString());
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_FILESCOREVOTE_VALUE, InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_START_VALUE, build.toByteArray());
    }

    /**
     * 停止评分
     *
     * @param voteid
     */
    public void stopScore(int voteid) {
        InterfaceFilescorevote.pbui_Type_DeleteUserDefineFileScore build = InterfaceFilescorevote.pbui_Type_DeleteUserDefineFileScore.newBuilder()
                .addVoteid(voteid).build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_FILESCOREVOTE_VALUE, InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_STOP_VALUE, build.toByteArray());
    }

    /**
     * 提交文件评分
     *
     * @param voteid
     * @param memberid
     * @param opinion  评分意见
     * @param allscore 所有分数
     */
    public void submitScore(int voteid, int memberid, String opinion, List<Integer> allscore) {
        InterfaceFilescorevote.pbui_Type_UserDefineFileScoreMemberStatisticNotify build = InterfaceFilescorevote.pbui_Type_UserDefineFileScoreMemberStatisticNotify.newBuilder()
                .setContent(s2b(opinion))
                .setMemberid(memberid)
                .addAllScore(allscore)
                .setVoteid(voteid).build();
        LogUtils.d(TAG, "submitScore -->" + "提交文件评分");
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_FILESCOREVOTESIGN_VALUE, InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SUBMIT_VALUE, build.toByteArray());
    }

    /**
     * 查询指定id的会场
     *
     * @param roomId 会场id
     */
    public InterfaceRoom.pbui_Item_MeetRoomDetailInfo queryRoomById(int roomId) {
        InterfaceBase.pbui_QueryInfoByID build = InterfaceBase.pbui_QueryInfoByID.newBuilder().setId(roomId).build();
        byte[] bytes = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOM_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SINGLEQUERYBYID_VALUE, build.toByteArray());
        if (bytes != null) {
            try {
                InterfaceRoom.pbui_Type_MeetRoomDetailInfo info = InterfaceRoom.pbui_Type_MeetRoomDetailInfo.parseFrom(bytes);
                InterfaceRoom.pbui_Item_MeetRoomDetailInfo item = info.getItem(0);
                LogUtils.i(TAG, "queryRoomById 查询指定id的会场成功");
                return item;
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryRoomById 查询指定id的会场失败");
        return null;
    }

    /**
     * 删除签到
     *
     * @param meetingId 会议id
     * @param memberIds 参会人id
     */
    public void deleteSignIn(int meetingId, List<Integer> memberIds) {
        InterfaceSignin.pbui_Type_DoDeleteMeetSignIno build = InterfaceSignin.pbui_Type_DoDeleteMeetSignIno.newBuilder()
                .setMeetingid(meetingId)
                .addAllMemberids(memberIds)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSIGN_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL_VALUE, build.toByteArray());
    }

    /**
     * 查询参会人员详细信息
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public InterfaceMember.pbui_Type_MeetMemberDetailInfo queryAttendPeopleDetailed() throws InvalidProtocolBufferException {
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DETAILINFO.getNumber(), null);
        if (array == null) {
            LogUtils.e(TAG, "queryAttendPeopleDetailed :  查询参会人员详细信息失败 --> ");
            return null;
        }
        LogUtils.e(TAG, "queryAttendPeopleDetailed :  查询参会人员详细信息成功 --> ");
        return InterfaceMember.pbui_Type_MeetMemberDetailInfo.parseFrom(array);
    }

    /**
     * 204.查询投票提交人属性
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public InterfaceBase.pbui_CommonInt32uProperty queryVoteSubmitterProperty(int voteid, int memberid, int propertyid) {
        InterfaceVote.pbui_Type_MeetVoteQueryProperty.Builder builder = InterfaceVote.pbui_Type_MeetVoteQueryProperty.newBuilder();
        builder.setVoteid(voteid);
        builder.setMemberid(memberid);
        builder.setPropertyid(propertyid);
        InterfaceVote.pbui_Type_MeetVoteQueryProperty build = builder.build();
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETVOTESIGNED.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERYPROPERTY.getNumber(), build.toByteArray());
        if (array == null) {
            LogUtils.e(TAG, "queryVoteSubmitterProperty :  查询投票提交人属性失败 --> ");
            return null;
        }
        LogUtils.e(TAG, "queryVoteSubmitterProperty :  查询投票提交人属性成功 --> ");
        try {
            return InterfaceBase.pbui_CommonInt32uProperty.parseFrom(array);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            LogUtils.e(TAG, "queryVoteSubmitterProperty -->" + "类型转换失败");
            return null;
        }
    }

    /**
     * 查询管理员
     *
     * @return InterfaceAdmin.pbui_TypeAdminDetailInfo
     */
    public InterfaceAdmin.pbui_TypeAdminDetailInfo queryAdmin() {
        byte[] bytes = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ADMIN_VALUE, InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY_VALUE, null);
        if (bytes != null) {
            try {
                InterfaceAdmin.pbui_TypeAdminDetailInfo pbui_typeAdminDetailInfo = InterfaceAdmin.pbui_TypeAdminDetailInfo.parseFrom(bytes);
                LogUtils.e(TAG, "queryAdmin 查询管理员成功");
                return pbui_typeAdminDetailInfo;
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryAdmin 查询管理员失败");
        return null;
    }

    /**
     * 添加管理员
     * Pb_String_LenLimit 注意限制
     */
    public void addAdmin(InterfaceAdmin.pbui_Item_AdminDetailInfo adminInfo) {
        InterfaceAdmin.pbui_TypeAdminDetailInfo build = InterfaceAdmin.pbui_TypeAdminDetailInfo.newBuilder()
                .addItem(adminInfo).build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ADMIN_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD_VALUE, build.toByteArray());
    }

    /**
     * 删除管理员
     */
    public void delAdmin(InterfaceAdmin.pbui_Item_AdminDetailInfo adminInfo) {
        InterfaceAdmin.pbui_TypeAdminDetailInfo build = InterfaceAdmin.pbui_TypeAdminDetailInfo.newBuilder()
                .addItem(adminInfo).build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ADMIN_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL_VALUE, build.toByteArray());
    }

    /**
     * 修改管理员
     *
     * @param adminInfo
     */
    public void modifyAdmin(InterfaceAdmin.pbui_Item_AdminDetailInfo adminInfo) {
        InterfaceAdmin.pbui_TypeAdminDetailInfo build = InterfaceAdmin.pbui_TypeAdminDetailInfo.newBuilder()
                .addItem(adminInfo).build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ADMIN_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFY_VALUE, build.toByteArray());
    }

    /**
     * 修改管理员自身密码
     *
     * @param name   用户名
     * @param newPwd 新密码
     * @param oldPwd 旧密码
     */
    public void modifyAdminPwd(String name, String newPwd, String oldPwd) {
        InterfaceAdmin.pbui_Type_AdminModifyPwd build = InterfaceAdmin.pbui_Type_AdminModifyPwd.newBuilder()
                .setAdminname(s2b(name))
                .setAdminnewpwd(s2b(newPwd))
                .setAdminoldpwd(s2b(oldPwd)).build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ADMIN_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SET_VALUE, build.toByteArray());
        LogUtils.i(TAG, "modifyAdminPwd 修改管理员自身密码 用户名=" + name + ",新密码=" + newPwd + ",旧密码=" + oldPwd);
    }

    /**
     * 查询会议管理员控制的会场
     *
     * @param adminId 管理员id
     */
    public InterfaceAdmin.pbui_Type_MeetManagerRoomDetailInfo queryAdminRoom(int adminId) {
        InterfaceBase.pbui_QueryInfoByID build = InterfaceBase.pbui_QueryInfoByID.newBuilder().setId(adminId).build();
        byte[] bytes = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MANAGEROOM_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SINGLEQUERYBYID_VALUE, build.toByteArray());
        if (bytes != null) {
            try {
                InterfaceAdmin.pbui_Type_MeetManagerRoomDetailInfo info = InterfaceAdmin.pbui_Type_MeetManagerRoomDetailInfo.parseFrom(bytes);
                LogUtils.e(TAG, "queryAdminRoom 查询会议管理员控制的会场成功 adminId=" + adminId);
                return info;
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryAdminRoom 查询会议管理员控制的会场失败 adminId=" + adminId);
        return null;
    }

    /**
     * 保存会议管理员控制的会场
     *
     * @param mgrid   管理员id
     * @param roomIds 会场id集合
     */
    public void saveAdminRoom(int mgrid, List<Integer> roomIds) {
        InterfaceAdmin.pbui_Type_MeetManagerRoomDetailInfo build = InterfaceAdmin.pbui_Type_MeetManagerRoomDetailInfo.newBuilder()
                .setMgrid(mgrid)
                .addAllRoomid(roomIds).build();
        byte[] bytes = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MANAGEROOM_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SAVE_VALUE, build.toByteArray());
    }

    /**
     * 查询公告
     */
    public InterfaceBullet.pbui_BulletDetailInfo queryNotice() {
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETBULLET.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array != null) {
            try {
                InterfaceBullet.pbui_BulletDetailInfo pbui_bulletDetailInfo = InterfaceBullet.pbui_BulletDetailInfo.parseFrom(array);
                LogUtils.e(TAG, "queryNotice:  查询公告成功 --->>> ");
                return pbui_bulletDetailInfo;
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryNotice:  查询公告失败 --->>> ");
        return null;
    }

    /**
     * 查询会议室
     */
    public InterfaceRoom.pbui_Type_MeetRoomDetailInfo queryRoom() {
        byte[] bytes = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOM_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY_VALUE, null);
        if (bytes != null) {
            try {
                InterfaceRoom.pbui_Type_MeetRoomDetailInfo pbui_type_meetRoomDetailInfo = InterfaceRoom.pbui_Type_MeetRoomDetailInfo.parseFrom(bytes);
                LogUtils.e(TAG, "queryRoom 查询会场成功");
                return pbui_type_meetRoomDetailInfo;
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryRoom 查询会场失败");
        return null;
    }

    /**
     * 查询会议
     */
    public InterfaceMeet.pbui_Type_MeetMeetInfo queryAllMeeting() {
        byte[] bytes = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETINFO.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (bytes != null) {
            try {
                InterfaceMeet.pbui_Type_MeetMeetInfo result = InterfaceMeet.pbui_Type_MeetMeetInfo.parseFrom(bytes);
                LogUtils.i(TAG, "queryAllMeeting 查询会议成功");
                return result;
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryAllMeeting 查询会议失败");
        return null;
    }
    /**
     * 删除会议
     *
     * @param meetMeetInfo
     */
    public void deleteMeeting(InterfaceMeet.pbui_Item_MeetMeetInfo meetMeetInfo) {
        InterfaceMeet.pbui_Type_MeetMeetInfo build = InterfaceMeet.pbui_Type_MeetMeetInfo.newBuilder()
                .addItem(meetMeetInfo).build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETINFO.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL.getNumber(),
                build.toByteArray());
    }

    /**
     * 复制会议
     */
    public void copyMeeting(InterfaceMeet.pbui_Item_MeetMeetInfo item) {
        InterfaceMeet.pbui_Type_MeetMeetInfo build = InterfaceMeet.pbui_Type_MeetMeetInfo.newBuilder()
                .addItem(item)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETINFO_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DUMP_VALUE, build.toByteArray());
    }

    /**
     * 添加会议
     */
    public void addMeeting(InterfaceMeet.pbui_Item_MeetMeetInfo item) {
        InterfaceMeet.pbui_Type_MeetMeetInfo build = InterfaceMeet.pbui_Type_MeetMeetInfo.newBuilder()
                .addItem(item)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETINFO_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD_VALUE, build.toByteArray());
    }

    /**
     * 修改会议
     */
    public void modifyMeeting(InterfaceMeet.pbui_Item_MeetMeetInfo item) {
        InterfaceMeet.pbui_Type_MeetMeetInfo build = InterfaceMeet.pbui_Type_MeetMeetInfo.newBuilder()
                .addItem(item)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETINFO_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFY_VALUE, build.toByteArray());
        LogUtils.i(TAG, "modifyMeeting 修改会议：" + item.getId() + ", 会议名称=" + item.getName().toStringUtf8() + ",时间=" + item.getStartTime() + "," + item.getEndTime());
    }

    /**
     * 删除会议
     */
    public void delMeeting(InterfaceMeet.pbui_Item_MeetMeetInfo item) {
        InterfaceMeet.pbui_Type_MeetMeetInfo build = InterfaceMeet.pbui_Type_MeetMeetInfo.newBuilder()
                .addItem(item)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETINFO_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL_VALUE, build.toByteArray());
    }

    /**
     * 修改会议状态
     *
     * @param meetId 会议id
     * @param status 会议状态
     */
    public void modifyMeetingStatus(int meetId, int status) {
        InterfaceMeet.pbui_Type_MeetModStatus build = InterfaceMeet.pbui_Type_MeetModStatus.newBuilder()
                .setMeetid(meetId)
                .setStatus(status)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETINFO_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFYSTATUS_VALUE, build.toByteArray());
        LogUtils.i(TAG, "modifyMeetingStatus 修改会议状态 meetId=" + meetId + ", status=" + status);
    }

    /**
     * 修改议程
     *
     * @param content 文本议程
     */
    public void modifyTextAgenda(String content) {
        InterfaceAgenda.pbui_meetAgenda build = InterfaceAgenda.pbui_meetAgenda.newBuilder()
                .setAgendatype(InterfaceMacro.Pb_AgendaType.Pb_MEET_AGENDA_TYPE_TEXT_VALUE)
                .setText(s2b(content))
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETAGENDA_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFY_VALUE, build.toByteArray());
    }

    /**
     * 修改议程
     *
     * @param mediaId 文件议程
     */
    public void modifyFileAgenda(int mediaId) {
        InterfaceAgenda.pbui_meetAgenda build = InterfaceAgenda.pbui_meetAgenda.newBuilder()
                .setAgendatype(InterfaceMacro.Pb_AgendaType.Pb_MEET_AGENDA_TYPE_FILE_VALUE)
                .setMediaid(mediaId)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETAGENDA_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFY_VALUE, build.toByteArray());
    }

    /**
     * 查询常用人员
     */
    public InterfacePerson.pbui_Type_PersonDetailInfo queryFrequentlyMember() {
        byte[] bytes = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_PEOPLE_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY_VALUE, null);
        if (bytes != null) {
            try {
                InterfacePerson.pbui_Type_PersonDetailInfo pbui_type_personDetailInfo = InterfacePerson.pbui_Type_PersonDetailInfo.parseFrom(bytes);
                LogUtils.e(TAG, "queryMember 查询常用人员成功");
                return pbui_type_personDetailInfo;
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryMember 查询常用人员失败");
        return null;
    }

    /**
     * 查询指定ID的常用人员
     *
     * @param id 常用人员id
     */
    public InterfacePerson.pbui_Item_PersonDetailInfo queryFrequentlyMemberById(int id) {
        InterfaceBase.pbui_QueryInfoByID build = InterfaceBase.pbui_QueryInfoByID.newBuilder().setId(id).build();
        byte[] bytes = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_PEOPLE_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SINGLEQUERYBYID_VALUE, build.toByteArray());
        if (bytes != null) {
            try {
                InterfacePerson.pbui_Type_PersonDetailInfo pbui_type_personDetailInfo = InterfacePerson.pbui_Type_PersonDetailInfo.parseFrom(bytes);
                if (pbui_type_personDetailInfo != null && pbui_type_personDetailInfo.getItemList().size() > 0) {
                    InterfacePerson.pbui_Item_PersonDetailInfo item = pbui_type_personDetailInfo.getItem(0);
                    LogUtils.e(TAG, "queryMember 查询常用人员成功");
                    return item;
                }
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryMember 查询常用人员失败");
        return null;
    }

    /**
     * 添加参会人员（单个）
     */
    public void createMember(InterfaceMember.pbui_Item_MemberDetailInfo item) {
        InterfaceMember.pbui_Type_MemberDetailInfo build = InterfaceMember.pbui_Type_MemberDetailInfo.newBuilder()
                .addItem(item)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD_VALUE, build.toByteArray());
    }

    /**
     * 添加参会人员（多个）
     */
    public void createMultipleMember(List<InterfaceMember.pbui_Item_MemberDetailInfo> items) {
        InterfaceMember.pbui_Type_MemberDetailInfo build = InterfaceMember.pbui_Type_MemberDetailInfo.newBuilder()
                .addAllItem(items)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD_VALUE, build.toByteArray());
    }

    /**
     * 修改参会人员
     */
    public void modifyFrequentlyMember(InterfaceMember.pbui_Item_MemberDetailInfo item) {
        InterfaceMember.pbui_Type_MemberDetailInfo build = InterfaceMember.pbui_Type_MemberDetailInfo.newBuilder()
                .addItem(item)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFY_VALUE, build.toByteArray());
    }

    /**
     * 修改参会人员排序
     */
    public void modifyMemberSort(List<Integer> memberIds) {
        InterfaceMember.pbui_Type_ModifyMemberPos build = InterfaceMember.pbui_Type_ModifyMemberPos.newBuilder()
                .addAllMemberid(memberIds)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_CONTROL_VALUE, build.toByteArray());
    }

    /**
     * 删除参会人员
     */
    public void delMember(InterfaceMember.pbui_Item_MemberDetailInfo item) {
        InterfaceMember.pbui_Type_MemberDetailInfo build = InterfaceMember.pbui_Type_MemberDetailInfo.newBuilder()
                .addItem(item)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL_VALUE, build.toByteArray());
    }

    /**
     * 保存参会人员权限
     */
    public void saveAttendPeoplePermissions(List<InterfaceMember.pbui_Item_MemberPermission> items) {
        InterfaceMember.pbui_Type_MemberPermission build = InterfaceMember.pbui_Type_MemberPermission.newBuilder()
                .addAllItem(items)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBERPERMISSION_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SAVE_VALUE, build.toByteArray());
    }

    /**
     * 添加常用参会人
     *
     * @param person
     */
    public void addFrequentlyMember(InterfacePerson.pbui_Item_PersonDetailInfo person) {
        InterfacePerson.pbui_Type_PersonDetailInfo build = InterfacePerson.pbui_Type_PersonDetailInfo.newBuilder()
                .addItem(person).build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_PEOPLE_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD_VALUE, build.toByteArray());
    }

    /**
     * 添加多个常用参会人
     *
     * @param memberInfos
     */
    public void addFrequentlyMembers(List<InterfacePerson.pbui_Item_PersonDetailInfo> memberInfos) {
        InterfacePerson.pbui_Type_PersonDetailInfo build = InterfacePerson.pbui_Type_PersonDetailInfo.newBuilder()
                .addAllItem(memberInfos).build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_PEOPLE_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD_VALUE, build.toByteArray());
    }

    /**
     * 添加会议目录
     */
    public void createMeetDir(InterfaceFile.pbui_Item_MeetDirDetailInfo item) {
        InterfaceFile.pbui_Type_MeetDirDetailInfo build = InterfaceFile.pbui_Type_MeetDirDetailInfo.newBuilder()
                .addItem(item)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORY_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD_VALUE, build.toByteArray());
    }

    /**
     * 修改会议目录
     */
    public void modifyMeetDir(InterfaceFile.pbui_Item_MeetDirDetailInfo item) {
        InterfaceFile.pbui_Type_MeetDirDetailInfo build = InterfaceFile.pbui_Type_MeetDirDetailInfo.newBuilder()
                .addItem(item)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORY_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFY_VALUE, build.toByteArray());
    }

    /**
     * 修改会议目录排序
     *
     * @param items
     */
    public void modifyMeetDirSort(List<InterfaceFile.pbui_Item_MeetingDirPosItem> items) {
        InterfaceFile.pbui_Type_ModMeetDirPos build = InterfaceFile.pbui_Type_ModMeetDirPos.newBuilder()
                .addAllItem(items)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORY_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SET_VALUE, build.toByteArray());
    }

    /**
     * 删除会议目录
     */
    public void deleteMeetDir(InterfaceFile.pbui_Item_MeetDirDetailInfo item) {
        InterfaceFile.pbui_Type_MeetDirDetailInfo build = InterfaceFile.pbui_Type_MeetDirDetailInfo.newBuilder()
                .addItem(item)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORY_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL_VALUE, build.toByteArray());
    }

    /**
     * 修改会议目录文件名称
     */
    public void modifyMeetDirFileName(int dirId, InterfaceFile.pbui_Item_ModMeetDirFile item) {
        InterfaceFile.pbui_Type_ModMeetDirFile build = InterfaceFile.pbui_Type_ModMeetDirFile.newBuilder()
                .setDirid(dirId)
                .addItem(item)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORYFILE_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFYINFO_VALUE, build.toByteArray());
    }

    /**
     * 修改会议目录文件排序
     *
     * @param dirId   目录id
     * @param fileIds 文件id集合
     */
    public void modifyMeetDirFileSort(int dirId, List<Integer> fileIds) {
        InterfaceFile.pbui_Type_ModMeetDirFilePos build = InterfaceFile.pbui_Type_ModMeetDirFilePos.newBuilder()
                .setDirid(dirId)
                .addAllFileid(fileIds)
                .build();
        LogUtils.i(TAG, "modifyMeetDirFileSort 修改会议目录文件排序 目录id=" + dirId + ", 文件id=" + fileIds);
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORYFILE_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SET_VALUE, build.toByteArray());
    }

    /**
     * 查询会议目录权限
     *
     * @param dirId 目录id
     */
    public InterfaceFile.pbui_Type_MeetDirRightDetailInfo queryMeetDirPermission(int dirId) {
        InterfaceBase.pbui_QueryInfoByID build = InterfaceBase.pbui_QueryInfoByID.newBuilder()
                .setId(dirId)
                .build();
        byte[] bytes = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORYRIGHT_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY_VALUE, build.toByteArray());
        if (bytes != null) {
            try {
                InterfaceFile.pbui_Type_MeetDirRightDetailInfo pbui_type_meetDirRightDetailInfo = InterfaceFile.pbui_Type_MeetDirRightDetailInfo.parseFrom(bytes);
                LogUtils.i(TAG, "queryDirPermission 查询会议目录权限成功 dirId=" + dirId);
                return pbui_type_meetDirRightDetailInfo;
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryDirPermission 查询会议目录权限失败 dirId=" + dirId);
        return null;
    }

    /**
     * 保存会议目录权限
     */
    public void saveMeetDirPermission(int dirId, List<Integer> memberIds) {
        InterfaceFile.pbui_Type_MeetDirRightDetailInfo build = InterfaceFile.pbui_Type_MeetDirRightDetailInfo.newBuilder()
                .setDirid(dirId)
                .addAllMemberid(memberIds)
                .build();
        LogUtils.i(TAG, "saveDirPermission 保存会议目录权限 dirId=" + dirId + ", 参会人=" + memberIds);
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORYRIGHT_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SAVE_VALUE, build.toByteArray());
    }

    public void addFile2Dir(int dirId, InterfaceFile.pbui_Item_MeetDirFileDetailInfo item) {
        InterfaceFile.pbui_Type_MeetDirFileDetailInfo build = InterfaceFile.pbui_Type_MeetDirFileDetailInfo.newBuilder()
                .setDirid(dirId)
                .addItem(item)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORYFILE_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD_VALUE, build.toByteArray());
    }

    /**
     * 查询会场设备的流通道信息
     *
     * @param id 会议室id
     */
    public InterfaceVideo.pbui_Type_MeetVideoDetailInfo queryPlaceStream(int id) {
        InterfaceBase.pbui_QueryInfoByID build = InterfaceBase.pbui_QueryInfoByID.newBuilder()
                .setId(id)
                .build();
        byte[] bytes = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOMDEVICE_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MAKEVIDEO_VALUE, build.toByteArray());
        if (bytes != null) {
            try {
                InterfaceVideo.pbui_Type_MeetVideoDetailInfo info = InterfaceVideo.pbui_Type_MeetVideoDetailInfo.parseFrom(bytes);
                LogUtils.i(TAG, "queryPlaceStream 查询会场设备的流通道信息成功，会场id=" + id);
                return info;
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.i(TAG, "queryPlaceStream 查询会场设备的流通道信息失败，会场id=" + id);
        return null;
    }

    /**
     * 添加会议视频
     */
    public void addMeetVideo(InterfaceVideo.pbui_Item_MeetVideoDetailInfo item) {
        InterfaceVideo.pbui_Type_MeetVideoDetailInfo build = InterfaceVideo.pbui_Type_MeetVideoDetailInfo.newBuilder().addItem(item).build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETVIDEO.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD.getNumber(), build.toByteArray());
    }

    /**
     * 修改会议视频
     */
    public void modifyMeetVideo(InterfaceVideo.pbui_Item_MeetVideoDetailInfo item) {
        InterfaceVideo.pbui_Type_MeetVideoDetailInfo build = InterfaceVideo.pbui_Type_MeetVideoDetailInfo.newBuilder().addItem(item).build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETVIDEO.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFY.getNumber(), build.toByteArray());
    }

    /**
     * 删除会议视频
     */
    public void deleteMeetVideo(InterfaceVideo.pbui_Item_MeetVideoDetailInfo item) {
        InterfaceVideo.pbui_Type_MeetVideoDetailInfo build = InterfaceVideo.pbui_Type_MeetVideoDetailInfo.newBuilder().addItem(item).build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETVIDEO_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL_VALUE, build.toByteArray());
    }

    /**
     * 191.新建一个投票
     *
     * @return
     */
    public void createMultipleVote(List<InterfaceVote.pbui_Item_MeetOnVotingDetailInfo> vote) {
        InterfaceVote.pbui_Type_MeetOnVotingDetailInfo.Builder builder1 = InterfaceVote.pbui_Type_MeetOnVotingDetailInfo.newBuilder();
        builder1.addAllItem(vote);
        InterfaceVote.pbui_Type_MeetOnVotingDetailInfo build = builder1.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETONVOTING.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "createVote:  新建多个投票 --->>> ");
    }

    /**
     * 查询会议双屏显示
     */
    public InterfaceTablecard.pbui_Type_MeetTableCardDetailInfo queryTableCard() {
        byte[] bytes = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETTABLECARD_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY_VALUE, null);
        if (bytes != null) {
            try {
                InterfaceTablecard.pbui_Type_MeetTableCardDetailInfo info = InterfaceTablecard.pbui_Type_MeetTableCardDetailInfo.parseFrom(bytes);
                LogUtils.i(TAG, "queryTableCard 查询会议双屏显示" + "成功");
                return info;
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryTableCard 查询会议双屏显示" + "失败");
        return null;
    }

    /**
     * 高级查询文件
     *
     * @param dirId     为0表示从平台里查询(平台里查询这种情况下 role和uploadid是无效的)
     * @param queryflag 查询标志 参见Pb_MeetFileQueryFlag
     * @param role      上传者角色
     * @param uploadid  上传人员ID 为0表示全部
     * @param filetype  文件类型 为0表示全部 参见 Pb_MeetFileType
     * @param attrib    文件属性 为0表示全部 参见Pb_MeetFileAttrib
     * @param pageindex 分页值
     * @param pagenum   分页大小 为0表示返回全部
     */
    public InterfaceFile.pbui_TypePageResQueryrFileInfo queryFile(int dirId, int queryflag, int role, int uploadid, int filetype
            , int attrib, int pageindex, int pagenum) {
        InterfaceFile.pbui_Type_ComplexQueryMeetDirFile build = InterfaceFile.pbui_Type_ComplexQueryMeetDirFile.newBuilder()
                .setDirid(dirId)
                .setQueryflag(queryflag)
                .setRole(role)
                .setUploadid(uploadid)
                .setFiletype(filetype)
                .setAttrib(attrib)
                .setPageindex(pageindex)
                .setPagenum(pagenum)
                .build();
        byte[] bytes = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORYFILE_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_COMPLEXPAGEQUERY_VALUE, build.toByteArray());
        if (bytes != null) {
            try {
                InterfaceFile.pbui_TypePageResQueryrFileInfo info = InterfaceFile.pbui_TypePageResQueryrFileInfo.parseFrom(bytes);
                LogUtils.e(TAG, "queryFile 高级查询文件 成功");
                return info;
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryFile 高级查询文件 失败");
        return null;
    }

    /**
     * 修改双屏显示
     *
     * @param bgphotoid  底图媒体id
     * @param modifyflag 参见 Pb_TableCard_ModifyFlag
     * @param items      个数一定是三个
     */
    public void modifyTableCard(int bgphotoid, int modifyflag, List<InterfaceTablecard.pbui_Item_MeetTableCardDetailInfo> items) {
        InterfaceTablecard.pbui_Type_MeetTableCardDetailInfo build = InterfaceTablecard.pbui_Type_MeetTableCardDetailInfo.newBuilder()
                .setBgphotoid(bgphotoid)
                .setModifyflag(modifyflag)
                .addAllItem(items)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETTABLECARD_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFY_VALUE,
                build.toByteArray());
    }

    /**
     * 修改会议功能
     *
     * @param modifyflag 修改标志 参见 InterfaceMeetfunction#Pb_FunCon_ModifyFlag
     * @param items      会议功能数据
     */
    public void modifyMeetFunction(int modifyflag, List<InterfaceMeetfunction.pbui_Item_MeetFunConfigDetailInfo> items) {
        InterfaceMeetfunction.pbui_Type_MeetFunConfigDetailInfo build = InterfaceMeetfunction.pbui_Type_MeetFunConfigDetailInfo.newBuilder()
                .setModifyflag(modifyflag)
                .addAllItem(items)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_FUNCONFIG_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SAVE_VALUE, build.toByteArray());
    }

    /**
     * 会议通知
     *
     * @param value InterfaceMember#Pb_MeetSMSOper
     */
    public void informMeeting(int value) {
        InterfaceMember.pbui_Type_MeetSMSNotify build = InterfaceMember.pbui_Type_MeetSMSNotify.newBuilder()
                .setTemplateindex(value)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_NOTIFY_VALUE, build.toByteArray());
    }

    /**
     * 查询指定id的会议
     *
     * @param meetingId 会议id
     */
    public InterfaceMeet.pbui_Type_MeetMeetInfo queryMeetingById(int meetingId) {
        InterfaceBase.pbui_QueryInfoByID build = InterfaceBase.pbui_QueryInfoByID.newBuilder()
                .setId(meetingId)
                .build();
        byte[] bytes = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETINFO_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SINGLEQUERYBYID_VALUE, build.toByteArray());
        if (bytes != null) {
            try {
                InterfaceMeet.pbui_Type_MeetMeetInfo info = InterfaceMeet.pbui_Type_MeetMeetInfo.parseFrom(bytes);
                LogUtils.e(TAG, "queryMeetingById 查询指定id的会议 成功=" + meetingId);
                return info;
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryMeetingById 查询指定id的会议 失败=" + meetingId);
        return null;
    }

    /**
     * 查询任务
     */
    public InterfaceTask.pbui_Type_MeetTaskInfo queryTask() {
        byte[] bytes = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETTASK_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY_VALUE, null);
        if (bytes != null) {
            try {
                InterfaceTask.pbui_Type_MeetTaskInfo pbui_type_meetTaskInfo = InterfaceTask.pbui_Type_MeetTaskInfo.parseFrom(bytes);
                LogUtils.i(TAG, "queryTask 查询任务" + "成功");
                return pbui_type_meetTaskInfo;
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryTask 查询任务" + "失败");
        return null;
    }

    /**
     * 查询任务详情
     *
     * @param id 任务id
     */
    public InterfaceTask.pbui_Item_MeetTaskDetailInfo queryTaskById(int id) {
        InterfaceBase.pbui_QueryInfoByID build = InterfaceBase.pbui_QueryInfoByID.newBuilder()
                .setId(id)
                .build();
        byte[] bytes = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETTASK_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SINGLEQUERYBYID_VALUE, build.toByteArray());
        if (bytes != null) {
            try {
                InterfaceTask.pbui_Type_MeetTaskDetailInfo info = InterfaceTask.pbui_Type_MeetTaskDetailInfo.parseFrom(bytes);
                InterfaceTask.pbui_Item_MeetTaskDetailInfo item = info.getItem(0);
                LogUtils.i(TAG, "queryTaskById 查询任务详情" + "成功");
                return item;
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryTaskById 查询任务详情" + "失败");
        return null;
    }

    public void addTask(InterfaceTask.pbui_Item_MeetTaskDetailInfo item) {
        InterfaceTask.pbui_Type_MeetTaskDetailInfo build = InterfaceTask.pbui_Type_MeetTaskDetailInfo.newBuilder()
                .addItem(item)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETTASK_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD_VALUE, build.toByteArray());
    }

    public void delTask(int id) {
        InterfaceBase.pbui_QueryInfoByID build = InterfaceBase.pbui_QueryInfoByID.newBuilder()
                .setId(id)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETTASK_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL_VALUE, build.toByteArray());
    }

    public void updateTask(InterfaceTask.pbui_Item_MeetTaskDetailInfo item) {
        InterfaceTask.pbui_Type_MeetTaskDetailInfo build = InterfaceTask.pbui_Type_MeetTaskDetailInfo.newBuilder()
                .addItem(item)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETTASK_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_UPDATE_VALUE, build.toByteArray());
    }

    public void startTask(int id) {
        InterfaceBase.pbui_QueryInfoByID build = InterfaceBase.pbui_QueryInfoByID.newBuilder()
                .setId(id)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETTASK_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_START_VALUE, build.toByteArray());
    }

    public void stopTask(int id) {
        InterfaceBase.pbui_QueryInfoByID build = InterfaceBase.pbui_QueryInfoByID.newBuilder()
                .setId(id)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETTASK_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_STOP_VALUE, build.toByteArray());
    }

    /**
     * 修改设备
     *
     * @param modflag    指定需要修改的标志位 参见 Pb_DeviceModifyFlag
     * @param devId      设备id
     * @param devName    设备名称
     * @param lift0      升降话筒组id
     * @param lift1      升降话筒组id
     * @param deviceflag 参见 Interface_Macro.proto Pb_MeetDeviceFlag 定义
     * @param ipInfo     ip信息
     */
    public void modifyDevice(int modflag, int devId, String devName, int lift0, int lift1, int deviceflag, InterfaceDevice.pbui_SubItem_DeviceIpAddrInfo ipInfo) {
        List<InterfaceDevice.pbui_SubItem_DeviceIpAddrInfo> lists = new ArrayList<>();
        lists.add(ipInfo);
        InterfaceDevice.pbui_DeviceModInfo build = InterfaceDevice.pbui_DeviceModInfo.newBuilder()
                .setModflag(modflag)
                .setDevcieid(devId)
                .setDevname(s2b(devName))
                .addAllIpinfo(lists)
//                .addIpinfo(ipInfo)
                .setLiftgroupres0(lift0)
                .setLiftgroupres1(lift1)
                .setDeviceflag(deviceflag)
                .build();
        LogUtils.e(TAG, "modifyDevice 修改设备：devId=" + devId + ", devName=" + devName);
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEINFO_VALUE, InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFYINFO_VALUE, build.toByteArray());
    }

    /**
     * 修改设备
     *
     * @param devId      设备id
     * @param deviceflag 参见 Interface_Macro.proto Pb_MeetDeviceFlag 定义
     */
    public void modifyDevice(int devId, int deviceflag) {
        InterfaceDevice.pbui_DeviceModInfo build = InterfaceDevice.pbui_DeviceModInfo.newBuilder()
                .setModflag(InterfaceMacro.Pb_DeviceModifyFlag.Pb_DEVICE_MODIFYFLAG_DEVICEFLAG_VALUE)
                .setDevcieid(devId)
                .setDeviceflag(deviceflag)
                .build();
        LogUtils.e(TAG, "modifyDevice 修改设备：devId=" + devId + ", deviceflag=" + deviceflag);
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEINFO_VALUE, InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFYINFO_VALUE, build.toByteArray());
    }

    /**
     * 修改设备信息
     *
     * @param deviceId   设备id
     * @param deviceflag 参见 InterfaceMacro#Pb_MeetDeviceFlag
     */
    public void modifyDeviceParam(int deviceId, int deviceflag) {
        InterfaceDevice.pbui_DeviceModInfo build = InterfaceDevice.pbui_DeviceModInfo.newBuilder()
                .setModflag(InterfaceMacro.Pb_DeviceModifyFlag.Pb_DEVICE_MODIFYFLAG_DEVICEFLAG_VALUE)
                .setDevcieid(deviceId)
                .setDeviceflag(deviceflag)
                .build();
        LogUtils.i(TAG, "modifyDeviceParam 修改设备访客模式 deviceflag=" + deviceflag);
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEINFO_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFYINFO_VALUE, build.toByteArray());
    }

    /**
     * 删除设备
     *
     * @param devId 设备id
     */
    public void deleteDevice(int devId) {
        InterfaceDevice.pbui_DeviceDel build = InterfaceDevice.pbui_DeviceDel.newBuilder().addDevid(devId).build();
        LogUtils.e(TAG, "deleteDevice 删除设备：" + devId);
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEINFO_VALUE, InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL_VALUE, build.toByteArray());
    }

    /**
     * 发送远程配置
     *
     * @param devIds   设备id
     * @param jsonText json格式
     */
    public void remoteConfig(List<Integer> devIds, String jsonText) {
        InterfaceDevice.pbui_Type_MeetDoRemoteSet build = InterfaceDevice.pbui_Type_MeetDoRemoteSet.newBuilder()
                .addAllDeviceid(devIds)
                .setJsontext(s2b(jsonText))
                .build();
        LogUtils.i(TAG, "remoteConfig 发送远程配置=" + jsonText);
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEOPER_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_REMOTESET_VALUE, build.toByteArray());
    }

    /* 添加会议室
     *
     * @param name    会议室名称
     * @param address 会议室地址
     * @param remarks 会议室备注
     */
    public void addRoom(String name, String address, String remarks) {
        InterfaceRoom.pbui_Item_MeetRoomDetailInfo build = InterfaceRoom.pbui_Item_MeetRoomDetailInfo.newBuilder()
                .setName(s2b(name)).setAddr(s2b(address)).setComment(s2b(remarks)).build();
        InterfaceRoom.pbui_Type_MeetRoomDetailInfo build1 = InterfaceRoom.pbui_Type_MeetRoomDetailInfo.newBuilder()
                .addItem(build).build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOM_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD_VALUE, build1.toByteArray());
    }

    /**
     * 删除会议室
     *
     * @param roomId 会议室id
     */
    public void delRoom(int roomId) {
        InterfaceRoom.pbui_Item_MeetRoomDetailInfo build = InterfaceRoom.pbui_Item_MeetRoomDetailInfo.newBuilder()
                .setRoomid(roomId).build();
        InterfaceRoom.pbui_Type_MeetRoomDetailInfo build1 = InterfaceRoom.pbui_Type_MeetRoomDetailInfo.newBuilder()
                .addItem(build).build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOM_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL_VALUE, build1.toByteArray());
    }

    /**
     * 修改会议室
     *
     * @param roomId  会议室id
     * @param name    会议室名称
     * @param address 会议室地址
     * @param remarks 会议室备注
     */
    public void modifyRoom(int roomId, String name, String address, String remarks) {
        InterfaceRoom.pbui_Item_MeetRoomDetailInfo build = InterfaceRoom.pbui_Item_MeetRoomDetailInfo.newBuilder()
                .setRoomid(roomId).setName(s2b(name)).setAddr(s2b(address)).setComment(s2b(remarks)).build();
        InterfaceRoom.pbui_Type_MeetRoomDetailInfo build1 = InterfaceRoom.pbui_Type_MeetRoomDetailInfo.newBuilder()
                .addItem(build).build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOM_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFY_VALUE, build1.toByteArray());
    }

    /**
     * 添加设备到会议室
     *
     * @param roomId 会议室id
     * @param devId  设备id
     */
    public void addDeviceToRoom(int roomId, int devId) {
        InterfaceRoom.pbui_Type_MeetRoomModDeviceInfo build = InterfaceRoom.pbui_Type_MeetRoomModDeviceInfo.newBuilder()
                .setRoomid(roomId).addDeviceid(devId).build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOMDEVICE_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD_VALUE, build.toByteArray());
    }

    /**
     * 从会议室删除设备
     *
     * @param roomId 会议室id
     * @param devId  设备id
     */
    public void removeDeviceFromRoom(int roomId, int devId) {
        InterfaceRoom.pbui_Type_MeetRoomModDeviceInfo build = InterfaceRoom.pbui_Type_MeetRoomModDeviceInfo.newBuilder()
                .setRoomid(roomId).addDeviceid(devId).build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOMDEVICE_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL_VALUE, build.toByteArray());
    }

    /**
     * 修改界面配置项
     * pbui_Type_FaceConfigInfo(仅pbui_Item_FaceTextItemInfo有效
     * || 仅pbui_Item_FacePictureItemInfo有效
     * || 仅 pbui_Item_FaceOnlyTextItemInfo有效)
     *
     * @param bytes
     */
    public void modifyInterfaceConfig(byte[] bytes) {
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETFACECONFIG_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFY_VALUE, bytes);
    }

    /**
     * 设置会场底图信息
     *
     * @param roomId     会场ID
     * @param bgpicid    底图媒体ID 设置时使用
     * @param bgpathname 图片文件路径 上传底图图片时使用
     * @param userval    用户自定义的值
     */
    public InterfaceRoom.pbui_TypeSetRoomModBGReturn setRoomPicture(int roomId, int bgpicid, ByteString bgpathname, int userval) {
        InterfaceRoom.pbui_Type_MeetRoomModBGInfo build = InterfaceRoom.pbui_Type_MeetRoomModBGInfo.newBuilder()
                .setRoomid(roomId)
                .setBgpicid(bgpicid)
                .setBgpathname(bgpathname)
                .setUserval(userval)
                .build();
        byte[] bytes = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOM_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SET_VALUE, build.toByteArray());
        if (bytes != null) {
            try {
                InterfaceRoom.pbui_TypeSetRoomModBGReturn pbui_typeSetRoomModBGReturn = InterfaceRoom.pbui_TypeSetRoomModBGReturn.parseFrom(bytes);
                LogUtils.i(TAG, "setRoomPicture 设置会场底图信息" + "成功");
                return pbui_typeSetRoomModBGReturn;
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "setRoomPicture 设置会场底图信息" + "失败");
        return null;
    }

    /**
     * 设置会场设备坐标朝向信息
     *
     * @param roomId 会议室id
     * @param items  设备坐标朝向信息
     */
    public void setPlaceDeviceRankInfo(int roomId, List<InterfaceRoom.pbui_Item_MeetRoomDevPosInfo> items) {
        InterfaceRoom.pbui_Type_MeetRoomDevPosInfo build = InterfaceRoom.pbui_Type_MeetRoomDevPosInfo.newBuilder()
                .setRoomid(roomId)
                .addAllItem(items)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOMDEVICE_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SET_VALUE, build.toByteArray());

    }

    /**
     * 删除常用参会人
     *
     * @param personId 人员id
     */
    public void delFrequentlyMember(int personId) {
        InterfacePerson.pbui_Item_PersonDetailInfo build1 = InterfacePerson.pbui_Item_PersonDetailInfo.newBuilder()
                .setPersonid(personId).build();
        InterfacePerson.pbui_Type_PersonDetailInfo build = InterfacePerson.pbui_Type_PersonDetailInfo.newBuilder()
                .addItem(build1).build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_PEOPLE_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL_VALUE, build.toByteArray());
    }

    /**
     * 修改常用参会人
     *
     * @param person
     */
    public void modifyFrequentlyMember(InterfacePerson.pbui_Item_PersonDetailInfo person) {
        InterfacePerson.pbui_Type_PersonDetailInfo build = InterfacePerson.pbui_Type_PersonDetailInfo.newBuilder()
                .addItem(person).build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_PEOPLE_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFY_VALUE, build.toByteArray());
    }


    /**
     * 网页查询
     */
    public InterfaceBase.pbui_meetUrl queryUrl() throws InvalidProtocolBufferException {
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEFAULTURL.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array == null) {
            LogUtils.e(TAG, "webQuery  网页查询 --->>> 失败");
            return null;
        }
        LogUtils.e(TAG, "webQuery  网页查询 --->>> 成功");
        return InterfaceBase.pbui_meetUrl.parseFrom(array);
    }

    /**
     * isetdefault=1表示修改系统全局的 =0表示修改当前会议的
     *
     * @param item
     */
    public void addUrl(InterfaceBase.pbui_Item_UrlDetailInfo item) {
        LogUtils.i(TAG, "addUrl id=" + item.getId() + ",名称=" + item.getName().toStringUtf8() + ",地址=" + item.getAddr().toStringUtf8());
        InterfaceBase.pbui_meetUrl build = InterfaceBase.pbui_meetUrl.newBuilder()
                .setIsetdefault(0)
                .addItem(item)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEFAULTURL_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD_VALUE, build.toByteArray());
    }

    public void modifyUrl(InterfaceBase.pbui_Item_UrlDetailInfo item) {
        LogUtils.i(TAG, "modifyUrl id=" + item.getId() + ",名称=" + item.getName().toStringUtf8() + ",地址=" + item.getAddr().toStringUtf8());
        InterfaceBase.pbui_meetUrl build = InterfaceBase.pbui_meetUrl.newBuilder()
                .setIsetdefault(0)
                .addItem(item)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEFAULTURL_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFY_VALUE, build.toByteArray());
    }

    /**
     * 升级设备
     *
     * @param mediaId 文件媒体id
     */
    public void updateDevice(int mediaId) {
        InterfaceDevice.pbui_Type_DoDeviceUpdate build = InterfaceDevice.pbui_Type_DoDeviceUpdate.newBuilder()
                .setMediaid(mediaId)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEINFO_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_UPDATE_VALUE, build.toByteArray());
    }


    public void delUrl(InterfaceBase.pbui_Item_UrlDetailInfo item) {
        LogUtils.i(TAG, "delUrl id=" + item.getId() + ",名称=" + item.getName().toStringUtf8() + ",地址=" + item.getAddr().toStringUtf8());
        InterfaceBase.pbui_meetUrl build = InterfaceBase.pbui_meetUrl.newBuilder()
                .setIsetdefault(0)
                .addItem(item)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEFAULTURL_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL_VALUE, build.toByteArray());
    }

    /**
     * 查询指定id的界面配置
     */
    public InterfaceFaceconfig.pbui_Type_FaceConfigInfo queryInterFaceConfigurationById(int faceId) {
        InterfaceBase.pbui_QueryInfoByID build = InterfaceBase.pbui_QueryInfoByID.newBuilder().setId(faceId).build();
        byte[] bytes = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETFACECONFIG_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SINGLEQUERYBYID_VALUE, build.toByteArray());
        if (bytes != null) {
            try {
                InterfaceFaceconfig.pbui_Type_FaceConfigInfo info = InterfaceFaceconfig.pbui_Type_FaceConfigInfo.parseFrom(bytes);
                LogUtils.e(TAG, "queryInterFaceConfigurationById 查询指定id的界面配置成功 faceId=" + faceId);
                return info;
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryInterFaceConfigurationById 查询指定id的界面配置失败 faceId=" + faceId);
        return null;
    }

    /**
     * 登录
     *
     * @param adminName 用户名
     * @param adminPwd  用户密码(ascill/md5ascill)
     * @param isAscill  =0md5字符密码 =1明文密码
     * @param loginMode =0管理员登陆 =1常用人员登陆 =2离线本地模式
     * @see InterfaceMacro.Pb_String_LenLimit
     */
    public void login(String adminName, String adminPwd, int isAscill, int loginMode) {
        InterfaceAdmin.pbui_Type_AdminLogon build = InterfaceAdmin.pbui_Type_AdminLogon.newBuilder()
                .setAdminname(s2b(adminName))
                .setAdminpwd(s2b(adminPwd))
                .setIsascill(isAscill)
                .setLogonmode(loginMode)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ADMIN_VALUE, InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_LOGON_VALUE, build.toByteArray());
    }

    /**
     * 254.设置播放暂停
     *
     * @return
     */
    public void setPlayStop(int resIndex, List<Integer> devIds) {
        InterfacePlaymedia.pbui_Type_MeetDoPlayControl.Builder builder = InterfacePlaymedia.pbui_Type_MeetDoPlayControl.newBuilder();
        builder.setResindex(resIndex);
        builder.addAllDeviceid(devIds);
        InterfacePlaymedia.pbui_Type_MeetDoPlayControl build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEDIAPLAY.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_PAUSE.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "setPlayStop:  设置播放暂停 --->>> ");
    }

    /**
     * 查询指定投票的提交人
     *
     * @param voteid 投票id
     * @return
     */
    public InterfaceVote.pbui_Type_MeetVoteSignInDetailInfo querySubmitterByVoteId(int voteid) {
        InterfaceBase.pbui_QueryInfoByID build = InterfaceBase.pbui_QueryInfoByID.newBuilder()
                .setId(voteid)
                .build();
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETVOTESIGNED.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), build.toByteArray());
        if (array != null) {
            try {
                InterfaceVote.pbui_Type_MeetVoteSignInDetailInfo pbui_type_meetVoteSignInDetailInfo = InterfaceVote.pbui_Type_MeetVoteSignInDetailInfo.parseFrom(array);
                LogUtils.i(TAG, "querySubmitterByVoteId 查询指定投票的提交人成功 voteid=" + voteid);
                return pbui_type_meetVoteSignInDetailInfo;
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "querySubmitterByVoteId 查询指定投票的提交人失败 voteid=" + voteid);
        return null;
    }

    /**
     * 判断是否连接服务器（在线）
     * @return 是否在线
     */
    public boolean isOnline() {
        boolean isonline = false;
        byte[] bytes = queryDevicePropertiesById(InterfaceMacro.Pb_MeetDevicePropertyID.Pb_MEETDEVICE_PROPERTY_NETSTATUS_VALUE,
                GlobalValue.localDeviceId);
        if (bytes != null) {
            InterfaceDevice.pbui_DeviceInt32uProperty pbui_deviceInt32uProperty = null;
            try {
                pbui_deviceInt32uProperty = InterfaceDevice.pbui_DeviceInt32uProperty.parseFrom(bytes);
                int propertyval = pbui_deviceInt32uProperty.getPropertyval();
                isonline = propertyval == 1;
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        return isonline;
    }

    /**
     * 创建一个文件离线本地缓存
     *
     * @param dirid      目录ID
     * @param mediaid    媒体ID
     * @param newfile    =0不覆盖同名文件,=1重新下载
     * @param onlyfinish =1表示只需要结束的通知
     * @param userStr    自定义标识
     */
    public void createFileCache(int dirid, int mediaid, int newfile, int onlyfinish, String userStr) {
        InterfaceDownload.pbui_Type_DownloadCache build = InterfaceDownload.pbui_Type_DownloadCache.newBuilder()
                .setDirid(dirid)
                .setMediaid(mediaid)
                .setNewfile(newfile)
                .setOnlyfinish(onlyfinish)
                .setUserstr(s2b(userStr))
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DOWNLOAD.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SAVE.getNumber(), build.toByteArray());
    }
}
