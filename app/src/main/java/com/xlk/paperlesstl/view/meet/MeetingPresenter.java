package com.xlk.paperlesstl.view.meet;

import android.content.Context;

import com.blankj.utilcode.util.LogUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMeetfunction;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.xlk.paperlesstl.App;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.model.Constant;
import com.xlk.paperlesstl.model.GlobalValue;
import com.xlk.paperlesstl.model.data.EventMessage;
import com.xlk.paperlesstl.model.data.EventType;
import com.xlk.paperlesstl.model.data.MeetFunctionBean;
import com.xlk.paperlesstl.util.DateUtil;
import com.xlk.paperlesstl.view.admin.bean.DevMember;
import com.xlk.paperlesstl.view.base.BasePresenter;

import java.util.ArrayList;
import java.util.List;

import cc.shinichi.library.ImagePreview;

/**
 * @author Created by xlk on 2021/3/8.
 * @desc
 */
public class MeetingPresenter extends BasePresenter<MeetingContract.View> implements MeetingContract.Presenter {
    public List<MeetFunctionBean> meetFunctions = new ArrayList<>();
    private List<InterfaceMember.pbui_Item_MemberDetailInfo> memberDetailInfos = new ArrayList<>();
    public List<DevMember> onlineMembers = new ArrayList<>();
    public List<InterfaceDevice.pbui_Item_DeviceDetailInfo> onlineProjectors = new ArrayList<>();
    List<String> picPath = new ArrayList<>();
    private Context cxt;

    public MeetingPresenter(Context context, MeetingContract.View view) {
        super(view);
        cxt = context;
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //时间回调
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_TIME_VALUE: {
                Object[] objs = msg.getObjects();
                byte[] data = (byte[]) objs[0];
                InterfaceBase.pbui_Time pbui_time = InterfaceBase.pbui_Time.parseFrom(data);
                //微秒 转换成毫秒 除以 1000
                String[] gtmDate = DateUtil.getGTMDate(pbui_time.getUsec() / 1000);
                mView.updateTime(gtmDate[0], gtmDate[1], gtmDate[2]);
                break;
            }
            //会议功能变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_FUNCONFIG_VALUE: {
                LogUtils.d(TAG, "BusEvent --> 会议功能变更通知");
                queryMeetFunction();
                break;
            }
            //设备会议信息变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEFACESHOW_VALUE: {
                LogUtils.i(TAG, "BusEvent --> 设备会议信息变更通知");
                queryDeviceMeetInfo();
                break;
            }
            //会议排位变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSEAT_VALUE: {
                LogUtils.d(TAG, "BusEvent -->" + "会议排位变更通知");
                queryLocalRole();
                break;
            }
            case EventType.BUS_PUSH_FILE: {
                int mediaId = (int) msg.getObjects()[0];
                mView.showPushPop(mediaId);
                break;
            }
            //设备寄存器变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEINFO_VALUE: {
                queryDevice();
                break;
            }
            //参会人变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER_VALUE: {
                queryMember();
                break;
            }
            //界面状态变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEMEETSTATUS_VALUE: {
                LogUtils.i(TAG, "BusEvent -->" + "界面状态变更通知");
                queryMember();
                break;
            }
            //参会人员权限变更通知    pbui_MeetNotifyMsg
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBERPERMISSION_VALUE: {
                LogUtils.i(TAG, "busEvent 参会人员权限变更通知");
                queryPermission();
                break;
            }
            //打开预览图片文件
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
            case EventType.BUS_SIGN_IN_DETAILS: {
                mView.showSignInDetailsFragment();
                break;
            }
            case EventType.BUS_SIGN_IN: {
                mView.showSignInFragment();
                break;
            }
            default:
                break;
        }
    }

    private void previewImage(int index) {
        if (picPath.isEmpty()) {
            return;
        }
        ImagePreview.getInstance()
                .setContext(cxt)
                .setImageList(picPath)//设置图片地址集合
                .setIndex(index)//设置开始的索引
                .setShowDownButton(false)//设置是否显示下载按钮
                .setShowCloseButton(false)//设置是否显示关闭按钮
                .setEnableDragClose(true)//设置是否开启下拉图片退出
                .setEnableUpDragClose(true)//设置是否开启上拉图片退出
                .setEnableClickClose(true)//设置是否开启点击图片退出
                .setShowErrorToast(true)
                .start();
    }

    @Override
    public void initial() {
        jni.modifyContextProperties(InterfaceMacro.Pb_ContextPropertyID.Pb_MEETCONTEXT_PROPERTY_ROLE_VALUE,
                InterfaceMacro.Pb_MeetFaceStatus.Pb_MemState_MemFace_VALUE);
        //缓存会议目录
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORY.getNumber());
        //会议目录文件
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORYFILE.getNumber());
        //缓存会议评分
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_FILESCOREVOTESIGN_VALUE);
        // 缓存会场设备
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOMDEVICE.getNumber());
        //缓存会场设备
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOMDEVICE.getNumber());
        // 缓存会议排位
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSEAT.getNumber());
        // 缓存参会人信息
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER.getNumber());
        //缓存参会人权限
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBERPERMISSION.getNumber());
        //缓存投票信息
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETVOTEINFO.getNumber());
        //人员签到
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSIGN.getNumber());
        //公告信息
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETBULLET.getNumber());
        //会议视频
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETVIDEO.getNumber());
    }

    @Override
    public void queryMember() {
        InterfaceMember.pbui_Type_MemberDetailInfo object = jni.queryMember();
        memberDetailInfos.clear();
        if (object != null) {
            memberDetailInfos.addAll(object.getItemList());
        }
        queryDevice();
    }

    private void queryDevice() {
        InterfaceDevice.pbui_Type_DeviceDetailInfo object = jni.queryDeviceInfo();
        onlineMembers.clear();
        onlineProjectors.clear();
        if (object != null) {
            List<InterfaceDevice.pbui_Item_DeviceDetailInfo> pdevList = object.getPdevList();
            for (int i = 0; i < pdevList.size(); i++) {
                InterfaceDevice.pbui_Item_DeviceDetailInfo dev = pdevList.get(i);
                int devcieid = dev.getDevcieid();
                int netstate = dev.getNetstate();
                int facestate = dev.getFacestate();
                int memberid = dev.getMemberid();
                if (netstate == 1) {
                    if (Constant.isThisDevType(InterfaceMacro.Pb_DeviceIDType.Pb_DeviceIDType_MeetProjective_VALUE, devcieid)) {
                        onlineProjectors.add(dev);
                    }
                    if (facestate == 1 && devcieid != GlobalValue.localDeviceId) {
                        for (int j = 0; j < memberDetailInfos.size(); j++) {
                            InterfaceMember.pbui_Item_MemberDetailInfo member = memberDetailInfos.get(j);
                            if (member.getPersonid() == memberid) {
                                onlineMembers.add(new DevMember(dev, member));
                                break;
                            }
                        }
                    }
                }
            }
        }
        mView.updateMemberAndProjectorAdapter();
    }

    @Override
    public void initVideoRes() {
        jni.initVideoRes(Constant.RESOURCE_ID_0, GlobalValue.screen_width, GlobalValue.screen_height);
        jni.initVideoRes(Constant.RESOURCE_ID_10, GlobalValue.screen_width, GlobalValue.screen_height);
        jni.initVideoRes(Constant.RESOURCE_ID_11, GlobalValue.screen_width, GlobalValue.screen_height);
    }

    @Override
    public void releaseVideoRes() {
        jni.releaseVideoRes(Constant.RESOURCE_ID_0);
        jni.releaseVideoRes(Constant.RESOURCE_ID_10);
        jni.releaseVideoRes(Constant.RESOURCE_ID_11);
    }

    @Override
    public void queryDeviceMeetInfo() {
        InterfaceDevice.pbui_Type_DeviceFaceShowDetail deviceMeetInfo = jni.queryDeviceMeetInfo();
        if (deviceMeetInfo != null) {
            GlobalValue.localMeetingId = deviceMeetInfo.getMeetingid();
            GlobalValue.localMemberId = deviceMeetInfo.getMemberid();
            GlobalValue.localMemberName = deviceMeetInfo.getMembername().toStringUtf8();
            GlobalValue.localMeetingName = deviceMeetInfo.getMeetingname().toStringUtf8();
            GlobalValue.localRoomId = deviceMeetInfo.getRoomid();
            mView.updateMeetName(GlobalValue.localMeetingName);
            mView.updateMemberName(GlobalValue.localMemberName);
        }
        queryLocalRole();
    }

    private void queryLocalRole() {
        InterfaceBase.pbui_CommonInt32uProperty property = jni.queryMeetRankingProperty(InterfaceMacro.Pb_MeetSeatPropertyID.Pb_MEETSEAT_PROPERTY_ROLEBYMEMBERID.getNumber());
        if (property != null) {
            int propertyval = property.getPropertyval();
            GlobalValue.localRole = propertyval;
            if (propertyval == InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_compere.getNumber()
                    || propertyval == InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_secretary.getNumber()
                    || propertyval == InterfaceMacro.Pb_MeetMemberRole.Pb_role_admin.getNumber()) {
                //当前是主持人或秘书或管理员，设置拥有所有权限
                GlobalValue.hasAllPermissions = true;
                mView.hasOtherFunction(true);
                if (propertyval == InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_compere.getNumber()) {
                    mView.updateMemberRole(App.appContext.getString(R.string.role_host,GlobalValue.localMemberName));
                } else if (propertyval == InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_secretary.getNumber()) {
                    mView.updateMemberRole(App.appContext.getString(R.string.role_secretary,GlobalValue.localMemberName));
                } else {
                    mView.updateMemberRole(App.appContext.getString(R.string.role_admin,GlobalValue.localMemberName));
                }
            } else {
                GlobalValue.hasAllPermissions = false;
                mView.hasOtherFunction(false);
                mView.updateMemberRole(App.appContext.getString(R.string.role_member,GlobalValue.localMemberName));
            }
        }
    }

    @Override
    public void queryMeetFunction() {
        InterfaceMeetfunction.pbui_Type_MeetFunConfigDetailInfo object = jni.queryMeetFunction();
        meetFunctions.clear();
        if (object != null) {
            List<InterfaceMeetfunction.pbui_Item_MeetFunConfigDetailInfo> itemList = object.getItemList();
            for (int i = 0; i < itemList.size(); i++) {
                InterfaceMeetfunction.pbui_Item_MeetFunConfigDetailInfo item = itemList.get(i);
                int funcode = item.getFuncode();
                if (
                        funcode != InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_VOTERESULT_VALUE
                                && funcode != InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_DOCUMENT_VALUE
                ) {
                    LogUtils.i(TAG, "会议功能=" + funcode);
                    meetFunctions.add(new MeetFunctionBean(funcode, item.getPosition()));
                }
            }
        }
        //其它功能模块
        int size = meetFunctions.size();
        meetFunctions.add(new MeetFunctionBean(Constant.FUN_CODE, size));
        mView.updateMeetFunction();
    }

    @Override
    public void queryPermission() {
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

}
