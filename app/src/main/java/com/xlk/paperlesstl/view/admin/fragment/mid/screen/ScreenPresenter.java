package com.xlk.paperlesstl.view.admin.fragment.mid.screen;

import android.content.Context;

import com.blankj.utilcode.util.LogUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.xlk.paperlesstl.model.Constant;
import com.xlk.paperlesstl.model.data.EventMessage;
import com.xlk.paperlesstl.view.admin.BasePresenter;
import com.xlk.paperlesstl.view.admin.bean.DevMember;

import java.util.ArrayList;
import java.util.List;

import static com.xlk.paperlesstl.view.admin.fragment.mid.screen.ScreenFragment.isAdminPage;

/**
 * @author Created by xlk on 2021/3/6.
 * @desc
 */
public class ScreenPresenter extends BasePresenter {
    private final String TAG = "ScreenPresenter-->";
    private final ScreenInterface view;
    private final Context cxt;
    private List<InterfaceDevice.pbui_Item_DeviceDetailInfo> deviceDetailInfos = new ArrayList<>();
    public List<InterfaceMember.pbui_Item_MemberDetailInfo> memberDetailInfos = new ArrayList<>();
    public List<InterfaceDevice.pbui_Item_DeviceDetailInfo> onLineProjectors = new ArrayList<>();
    public List<DevMember> sourceMembers = new ArrayList<>();
    public List<DevMember> targetMembers = new ArrayList<>();

    public ScreenPresenter(Context context, ScreenInterface view) {
        super();
        this.cxt = context;
        this.view = view;
    }

    @Override
    public void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEINFO_VALUE://设备寄存器变更通知
                LogUtils.d(TAG, "BusEvent -->" + "设备寄存器变更通知 ");
                queryDeviceInfo();
                break;
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER_VALUE://参会人员变更通知
                LogUtils.d(TAG, "BusEvent -->" + "参会人员变更通知");
                queryMember();
                break;
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEMEETSTATUS_VALUE://界面状态变更通知
                LogUtils.d(TAG, "BusEvent -->" + "界面状态变更通知");
                queryDeviceInfo();
                break;
        }
    }

    public void queryDeviceInfo() {
        InterfaceDevice.pbui_Type_DeviceDetailInfo deviceDetailInfo = jni.queryDeviceInfo();
        if (deviceDetailInfo == null) {
            return;
        }
        deviceDetailInfos.clear();
        deviceDetailInfos.addAll(deviceDetailInfo.getPdevList());
        queryMember();
    }

    public void queryMember() {
        InterfaceMember.pbui_Type_MemberDetailInfo attendPeople = jni.queryMember();
        if (attendPeople == null) {
            return;
        }
        memberDetailInfos.clear();
        memberDetailInfos.addAll(attendPeople.getItemList());
        onLineProjectors.clear();
        sourceMembers.clear();
        targetMembers.clear();
        for (int i = 0; i < deviceDetailInfos.size(); i++) {
            InterfaceDevice.pbui_Item_DeviceDetailInfo dev = deviceDetailInfos.get(i);
            int devcieid = dev.getDevcieid();
            int memberid = dev.getMemberid();
            int netstate = dev.getNetstate();
            int facestate = dev.getFacestate();
//                if (devcieid == Values.localDeviceId) {
//                    continue;
//                }
            if (netstate == 1) {//在线
                if (Constant.isThisDevType(InterfaceMacro.Pb_DeviceIDType.Pb_DeviceIDType_MeetProjective_VALUE, devcieid)) {//在线的投影机
                    onLineProjectors.add(dev);
                } else {
                    for (int j = 0; j < memberDetailInfos.size(); j++) {
                        InterfaceMember.pbui_Item_MemberDetailInfo member = memberDetailInfos.get(j);
                        if (member.getPersonid() == memberid) {
                            //isAdminPage表示是否是后台管理界面启动的
                            if (facestate == 1 || isAdminPage) {
                                sourceMembers.add(new DevMember(dev, member));
                                targetMembers.add(new DevMember(dev, member));
                                break;
                            }
                        }
                    }
                }
            }
        }
        view.notifyOnLineAdapter();
    }
}
