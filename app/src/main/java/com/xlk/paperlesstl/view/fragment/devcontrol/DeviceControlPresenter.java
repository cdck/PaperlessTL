package com.xlk.paperlesstl.view.fragment.devcontrol;

import com.blankj.utilcode.util.LogUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.xlk.paperlesstl.model.Constant;
import com.xlk.paperlesstl.model.data.EventMessage;
import com.xlk.paperlesstl.view.admin.bean.DevControlBean;
import com.xlk.paperlesstl.view.admin.fragment.pre.member.MemberRoleBean;
import com.xlk.paperlesstl.view.base.BasePresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/3/12.
 * @desc
 */
public class DeviceControlPresenter extends BasePresenter<DeviceControlContract.View> implements DeviceControlContract.Presenter {

    private List<InterfaceRoom.pbui_Item_MeetRoomDevSeatDetailInfo> seatInfos = new ArrayList<>();
    public List<DevControlBean> devControlBeans = new ArrayList<>();
    public List<MemberRoleBean> memberRoleBeans = new ArrayList<>();
    private List<InterfaceDevice.pbui_Item_DeviceDetailInfo> deviceInfos = new ArrayList<>();

    public DeviceControlPresenter(DeviceControlContract.View view) {
        super(view);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //设备寄存器变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEINFO_VALUE: {
                queryDevice();
                break;
            }
            //参会人员变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER_VALUE: {
                LogUtils.i(TAG, "BusEvent -->" + "参会人员变更通知");
                queryMember();
                break;
            }
            //会场设备信息变更通知和界面状态变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOMDEVICE_VALUE:
                //设备当前会议
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEMEETSTATUS_VALUE:
                //会议排位变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSEAT_VALUE:
                queryPlaceDeviceRankingInfo();
                break;
        }
    }

    @Override
    public void queryMember() {
        InterfaceMember.pbui_Type_MemberDetailInfo object = jni.queryMember();
        memberRoleBeans.clear();
        if (object != null) {
            List<InterfaceMember.pbui_Item_MemberDetailInfo> itemList = object.getItemList();
            for (int i = 0; i < itemList.size(); i++) {
                memberRoleBeans.add(new MemberRoleBean(itemList.get(i)));
            }
        }
        queryPlaceDeviceRankingInfo();
    }

    @Override
    public void queryPlaceDeviceRankingInfo() {
        InterfaceRoom.pbui_Type_MeetRoomDevSeatDetailInfo object = jni.placeDeviceRankingInfo(queryCurrentRoomId());
        seatInfos.clear();
        if (object != null) {
            seatInfos.addAll(object.getItemList());
            for (int i = 0; i < memberRoleBeans.size(); i++) {
                MemberRoleBean roleBean = memberRoleBeans.get(i);
                for (int j = 0; j < seatInfos.size(); j++) {
                    InterfaceRoom.pbui_Item_MeetRoomDevSeatDetailInfo seat = seatInfos.get(j);
                    if (roleBean.getMember().getPersonid() == seat.getMemberid()) {
                        roleBean.setSeat(seat);
                    }
                }
            }
        }
        mView.updateMemberRoleList();
        queryDevice();
    }

    private void queryDevice() {
        InterfaceDevice.pbui_Type_DeviceDetailInfo object = jni.queryDeviceInfo();
        devControlBeans.clear();
        deviceInfos.clear();
        if (object != null) {
            deviceInfos.addAll(object.getPdevList());
            for (int i = 0; i < deviceInfos.size(); i++) {
                InterfaceDevice.pbui_Item_DeviceDetailInfo dev = deviceInfos.get(i);
                int devcieid = dev.getDevcieid();
                //是否是茶水设备
                boolean isTea = Constant.isThisDevType(InterfaceMacro.Pb_DeviceIDType.Pb_DeviceIDType_MeetService_VALUE, devcieid);
                //是否是会议数据库设备
                boolean isDatabase = Constant.isThisDevType(InterfaceMacro.Pb_DeviceIDType.Pb_DeviceIDType_MeetDBServer_VALUE, devcieid);
                //公共设备需要添加进去
                if (isTea || isDatabase) {
                    devControlBeans.add(new DevControlBean(dev));
                } else {
                    for (int j = 0; j < seatInfos.size(); j++) {
                        InterfaceRoom.pbui_Item_MeetRoomDevSeatDetailInfo seat = seatInfos.get(j);
                        if (seat.getDevid() == devcieid) {
                            devControlBeans.add(new DevControlBean(dev, seat));
                            break;
                        }
                    }
                }
            }
        }
        mView.updateDeviceList();
    }

    @Override
    public void modifyDeviceFlag(List<Integer> deviceIds) {
        for (int i = 0; i < deviceInfos.size(); i++) {
            InterfaceDevice.pbui_Item_DeviceDetailInfo dev = deviceInfos.get(i);
            int devcieid = dev.getDevcieid();
            int deviceflag = dev.getDeviceflag();
            if (deviceIds.contains(devcieid)) {
                jni.modifyDevice(devcieid, deviceflag ^ InterfaceMacro.Pb_MeetDeviceFlag.Pb_MEETDEVICE_FLAG_OPENOUTSIDE_VALUE);
            }
        }
    }
}
