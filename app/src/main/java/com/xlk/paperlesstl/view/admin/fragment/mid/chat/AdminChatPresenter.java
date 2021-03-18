package com.xlk.paperlesstl.view.admin.fragment.mid.chat;

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
import com.xlk.paperlesstl.view.admin.BasePresenter;

import java.util.ArrayList;
import java.util.List;

import static com.xlk.paperlesstl.util.ConvertUtil.s2b;


/**
 * @author Created by xlk on 2020/10/26.
 * @desc
 */
public class AdminChatPresenter extends BasePresenter {
    private final AdminChatInterface view;
    List<ChatMessage> chatMessages = new ArrayList<>();
    private List<InterfaceMember.pbui_Item_MemberDetailInfo> memberDetailInfos = new ArrayList<>();
//    private List<InterfaceDevice.pbui_Item_DeviceDetailInfo> deviceDetailInfos = new ArrayList<>();
    private List<DevMember> onlineDevMembers = new ArrayList<>();

    public AdminChatPresenter(AdminChatInterface view) {
        super();
        this.view = view;
    }

    public void queryMember() {
        InterfaceMember.pbui_Type_MemberDetailInfo attendPeople = jni.queryMember();
        if (attendPeople == null) {
            return;
        }
        memberDetailInfos.clear();
        memberDetailInfos.addAll(attendPeople.getItemList());
        queryDevice();
    }

    private void queryDevice() {
        InterfaceDevice.pbui_Type_DeviceDetailInfo deviceDetailInfo = jni.queryDeviceInfo();
        onlineDevMembers.clear();
        if (deviceDetailInfo != null) {
            List<InterfaceDevice.pbui_Item_DeviceDetailInfo> pdevList = deviceDetailInfo.getPdevList();
            for (int i = 0; i < pdevList.size(); i++) {
                InterfaceDevice.pbui_Item_DeviceDetailInfo detailInfo = pdevList.get(i);
                int devcieid = detailInfo.getDevcieid();
                int memberid = detailInfo.getMemberid();
                int facestate = detailInfo.getFacestate();
                int netstate = detailInfo.getNetstate();
                if (facestate == 1 && netstate == 1 && devcieid != GlobalValue.localDeviceId) {
                    for (int j = 0; j < memberDetailInfos.size(); j++) {
                        InterfaceMember.pbui_Item_MemberDetailInfo memberDetailInfo = memberDetailInfos.get(j);
                        int personid = memberDetailInfo.getPersonid();
                        if (personid == memberid) {
                            onlineDevMembers.add(new DevMember(detailInfo, memberDetailInfo));
                        }
                    }
                }
            }
        }
        view.updateMemberRv(onlineDevMembers);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //收到新的会议交流信息
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETIM_VALUE: {
                LogUtils.i(TAG, "busEvent 收到新的会议交流信息");
                byte[] bytes = (byte[]) msg.getObjects()[0];
                InterfaceIM.pbui_Type_MeetIM info = InterfaceIM.pbui_Type_MeetIM.parseFrom(bytes);
                //只接收文本类型的消息
                if (info.getMsgtype() == InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Message_VALUE) {
                    ChatMessage chatMessage = new ChatMessage(0, info);
                    chatMessages.add(chatMessage);
                    view.updateChatRv(chatMessages);
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
            default:
                break;
        }
    }

    public void sendChatMessage(String msg, int msgType, List<Integer> ids) {
        jni.sendChatMessage(msg, msgType, ids);
        InterfaceIM.pbui_Type_MeetIM build = InterfaceIM.pbui_Type_MeetIM.newBuilder()
                .setMsgtype(0)
                .setRole(GlobalValue.localRole)
                .setMemberid(GlobalValue.localMemberId)
                .setMsg(s2b(msg))
                //需要换算成秒单位
                .setUtcsecond(System.currentTimeMillis() / 1000)
                .setMeetname(s2b(GlobalValue.localMeetingName))
                .setRoomname(s2b(GlobalValue.localRoomName))
                .setMembername(s2b(GlobalValue.localMemberName))
                .setSeatename(s2b(GlobalValue.localDeviceName))
                .addAllUserids(ids)
                .build();
        chatMessages.add(new ChatMessage(1, build));
        view.updateChatRv(chatMessages);
    }
}
