package com.xlk.paperlesstl.view.admin.fragment.mid.camera;

import com.blankj.utilcode.util.LogUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.mogujie.tt.protobuf.InterfaceStop;
import com.mogujie.tt.protobuf.InterfaceVideo;
import com.xlk.paperlesstl.model.Constant;
import com.xlk.paperlesstl.model.GlobalValue;
import com.xlk.paperlesstl.model.data.EventMessage;
import com.xlk.paperlesstl.model.data.EventType;
import com.xlk.paperlesstl.view.admin.bean.DevMember;
import com.xlk.paperlesstl.view.admin.bean.VideoDev;
import com.xlk.paperlesstl.view.admin.BasePresenter;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Created by xlk on 2020/10/26.
 * @desc
 */
public class AdminCameraControlPresenter extends BasePresenter {
    private final AdminCameraControlInterface view;
    private List<InterfaceVideo.pbui_Item_MeetVideoDetailInfo> videoDetailInfos = new ArrayList<>();
    private List<InterfaceDevice.pbui_Item_DeviceDetailInfo> deviceDetailInfos = new ArrayList<>();
    private List<VideoDev> videoDevs = new ArrayList<>();
    public List<InterfaceMember.pbui_Item_MemberDetailInfo> memberDetailInfos = new ArrayList<>();
    public List<InterfaceDevice.pbui_Item_DeviceDetailInfo> onLineProjectors = new ArrayList<>();
    public List<DevMember> onLineMember = new ArrayList<>();

    public AdminCameraControlPresenter(AdminCameraControlInterface view) {
        super();
        this.view = view;
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //会议视频变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETVIDEO_VALUE:
                queryMeetVideo();
                break;
            //设备寄存器变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEINFO_VALUE:
                byte[] datas = (byte[]) msg.getObjects()[0];
                int datalen = (int) msg.getObjects()[1];
                InterfaceDevice.pbui_Type_MeetDeviceBaseInfo baseInfo = InterfaceDevice.pbui_Type_MeetDeviceBaseInfo.parseFrom(datas);
                int deviceid = baseInfo.getDeviceid();
                int attribid = baseInfo.getAttribid();
                LogUtils.d(TAG, "BusEvent -->" + "设备寄存器变更通知 deviceid= " + deviceid + ", attribid= " + attribid + ", datalen= " + datalen);
                queryDeviceInfo();
                break;
            //参会人员变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER_VALUE:
                LogUtils.d(TAG, "BusEvent -->" + "参会人员变更通知");
                queryMember();
                break;
            //界面状态变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEMEETSTATUS_VALUE:
                LogUtils.d(TAG, "BusEvent -->" + "界面状态变更通知");
                queryDeviceInfo();
                break;
            //后台播放数据 DECODE
            case EventType.BUS_VIDEO_DECODE:
                Object[] objs = msg.getObjects();
                view.updateDecode(objs);
                break;
            //后台播放数据 YUV
            case EventType.BUS_YUV_DISPLAY:
                Object[] objs1 = msg.getObjects();
                view.updateYuv(objs1);
                break;
            //停止资源通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_STOPPLAY_VALUE:
                byte[] o1 = (byte[]) msg.getObjects()[0];
                if (msg.getMethod() == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_CLOSE_VALUE) {
                    //停止资源通知
                    InterfaceStop.pbui_Type_MeetStopResWork stopResWork = InterfaceStop.pbui_Type_MeetStopResWork.parseFrom(o1);
                    List<Integer> resList = stopResWork.getResList();
                    for (int resid : resList) {
                        LogUtils.i(TAG, "BusEvent -->" + "停止资源通知 resid: " + resid);
                        view.stopResWork(resid);
                    }
                } else if (msg.getMethod() == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_NOTIFY_VALUE) {
                    //停止播放通知
                    InterfaceStop.pbui_Type_MeetStopPlay stopPlay = InterfaceStop.pbui_Type_MeetStopPlay.parseFrom(o1);
                    int resid = stopPlay.getRes();
                    int createdeviceid = stopPlay.getCreatedeviceid();
                    LogUtils.i(TAG, "BusEvent -->" + "停止播放通知 resid= " + resid + ", createdeviceid= " + createdeviceid);
                    view.stopResWork(resid);
                }
                break;
            default:break;
        }
    }


    public void queryDeviceInfo() {
            InterfaceDevice.pbui_Type_DeviceDetailInfo deviceDetailInfo = jni.queryDeviceInfo();
            deviceDetailInfos.clear();
            if (deviceDetailInfo != null) {
                deviceDetailInfos.addAll(deviceDetailInfo.getPdevList());
            }
            queryMeetVideo();
            queryMember();
    }

    public void queryMeetVideo() {
        InterfaceVideo.pbui_Type_MeetVideoDetailInfo object = jni.queryMeetVideo();
        if (object == null) {
            videoDevs.clear();
            view.updateRv(videoDevs);
            return;
        }
        videoDetailInfos.clear();
        videoDetailInfos.addAll(object.getItemList());
        videoDevs.clear();
        for (int i = 0; i < videoDetailInfos.size(); i++) {
            InterfaceVideo.pbui_Item_MeetVideoDetailInfo videoDetailInfo = videoDetailInfos.get(i);
            int deviceid = videoDetailInfo.getDeviceid();
            for (int j = 0; j < deviceDetailInfos.size(); j++) {
                InterfaceDevice.pbui_Item_DeviceDetailInfo detailInfo = deviceDetailInfos.get(j);
                if (detailInfo.getDevcieid() == deviceid) {
                    videoDevs.add(new VideoDev(videoDetailInfo, detailInfo));
                }
            }
        }
        view.updateRv(videoDevs);
    }

    public void queryMember() {
            InterfaceMember.pbui_Type_MemberDetailInfo attendPeople = jni.queryMember();
            if (attendPeople == null) {
                return;
            }
            memberDetailInfos.clear();
            memberDetailInfos.addAll(attendPeople.getItemList());
            onLineProjectors.clear();
            onLineMember.clear();
            for (int i = 0; i < deviceDetailInfos.size(); i++) {
                InterfaceDevice.pbui_Item_DeviceDetailInfo dev = deviceDetailInfos.get(i);
                int devcieid = dev.getDevcieid();
                int memberid = dev.getMemberid();
                int netstate = dev.getNetstate();
                int facestate = dev.getFacestate();
                if (devcieid == GlobalValue.localDeviceId) {
                    continue;
                }
                //在线
                if (netstate == 1) {
                    //在线的投影机
                    if (Constant.isThisDevType(InterfaceMacro.Pb_DeviceIDType.Pb_DeviceIDType_MeetProjective_VALUE, devcieid)) {
                        onLineProjectors.add(dev);
                    } else {
                        if (facestate == 1) {
                            for (int j = 0; j < memberDetailInfos.size(); j++) {
                                InterfaceMember.pbui_Item_MemberDetailInfo member = memberDetailInfos.get(j);
                                if (member.getPersonid() == memberid) {
                                    onLineMember.add(new DevMember(dev, member));
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            view.notifyOnLineAdapter();
    }

    public void initVideoRes(int width, int height) {
        jni.initVideoRes(Constant.RESOURCE_ID_1, width / 2, height / 2);
        jni.initVideoRes(Constant.RESOURCE_ID_2, width / 2, height / 2);
        jni.initVideoRes(Constant.RESOURCE_ID_3, width / 2, height / 2);
        jni.initVideoRes(Constant.RESOURCE_ID_4, width / 2, height / 2);
    }

    public void releaseVideoRes() {
        jni.releaseVideoRes(Constant.RESOURCE_ID_1);
        jni.releaseVideoRes(Constant.RESOURCE_ID_2);
        jni.releaseVideoRes(Constant.RESOURCE_ID_3);
        jni.releaseVideoRes(Constant.RESOURCE_ID_4);
    }

    public void stopResource(List<Integer> resIds) {
        List<Integer> ids = new ArrayList<>();
        ids.add(GlobalValue.localDeviceId);
        jni.stopResourceOperate(resIds, ids);
    }

    public void watch(VideoDev videoDev, int resId) {
        InterfaceVideo.pbui_Item_MeetVideoDetailInfo videoDetailInfo = videoDev.getVideoDetailInfo();
        int deviceid = videoDetailInfo.getDeviceid();
        int subid = videoDetailInfo.getSubid();
        List<Integer> res = new ArrayList<>();
        res.add(resId);
        List<Integer> ids = new ArrayList<>();
        ids.add(GlobalValue.localDeviceId);
        jni.streamPlay(deviceid, subid, 0, res, ids);
    }
}
