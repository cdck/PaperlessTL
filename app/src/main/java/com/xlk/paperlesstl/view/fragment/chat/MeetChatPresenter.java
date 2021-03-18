package com.xlk.paperlesstl.view.fragment.chat;

import com.blankj.utilcode.util.LogUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceIM;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.xlk.paperlesstl.model.GlobalValue;
import com.xlk.paperlesstl.model.data.EventMessage;
import com.xlk.paperlesstl.view.admin.bean.ChatMessage;
import com.xlk.paperlesstl.view.admin.bean.DevMember;
import com.xlk.paperlesstl.view.base.BasePresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/3/9.
 * @desc
 */
public class MeetChatPresenter extends BasePresenter<MeetChatContract.View> implements MeetChatContract.Presenter {

    private List<InterfaceMember.pbui_Item_MemberDetailInfo> memberInfos = new ArrayList<>();
    public List<DevMember> onlineMembers = new ArrayList<>();

    public MeetChatPresenter(MeetChatContract.View view) {
        super(view);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //会议交流
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETIM_VALUE: {
                byte[] o = (byte[]) msg.getObjects()[0];
                InterfaceIM.pbui_Type_MeetIM meetIM = InterfaceIM.pbui_Type_MeetIM.parseFrom(o);
                if (meetIM.getMsgtype() == 0) {//文本类消息
                    mView.addChatMessage(new ChatMessage(0, meetIM));
                }
                break;
            }
            //参会人变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER_VALUE: {
                LogUtils.i(TAG, "busEvent 参会人变更通知");
                queryMember();
                break;
            }
            //界面状态变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEMEETSTATUS_VALUE: {
                LogUtils.i(TAG, "busEvent 界面状态变更通知");
                int o = (int) msg.getObjects()[1];
                if (o > 0) {
                    queryMember();
                }
                break;
            }
            //设备寄存器变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEINFO_VALUE: {
                int o1 = (int) msg.getObjects()[1];
                if (msg.getMethod() == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_NOTIFY_VALUE
                        && o1 > 0) {
                    LogUtils.i(TAG, "busEvent 设备寄存器变更通知");
                    queryMember();
                }
                break;
            }
        }
    }

    @Override
    public void queryMember() {
        InterfaceMember.pbui_Type_MemberDetailInfo object = jni.queryMember();
        memberInfos.clear();
        if (object != null) {
            memberInfos.addAll(object.getItemList());
        }
        queryDevice();
    }

    private void queryDevice() {
        InterfaceDevice.pbui_Type_DeviceDetailInfo object = jni.queryDeviceInfo();
        onlineMembers.clear();
        if (object != null) {
            List<InterfaceDevice.pbui_Item_DeviceDetailInfo> pdevList = object.getPdevList();
            for (int i = 0; i < pdevList.size(); i++) {
                InterfaceDevice.pbui_Item_DeviceDetailInfo dev = pdevList.get(i);
                int devcieid = dev.getDevcieid();
                int memberid = dev.getMemberid();
                int facestate = dev.getFacestate();
                int netstate = dev.getNetstate();
                if (facestate == 1 && netstate == 1 && devcieid != GlobalValue.localDeviceId) {
                    for (int j = 0; j < memberInfos.size(); j++) {
                        InterfaceMember.pbui_Item_MemberDetailInfo memberDetailInfo = memberInfos.get(j);
                        int personid = memberDetailInfo.getPersonid();
                        if (personid == memberid) {
                            onlineMembers.add(new DevMember(dev, memberDetailInfo));
                        }
                    }
                }

            }
        }
        mView.updateMembers();
    }
}
