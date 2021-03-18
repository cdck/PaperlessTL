package com.xlk.paperlesstl.view.fragment.annotate;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.xlk.paperlesstl.App;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.model.Constant;
import com.xlk.paperlesstl.model.GlobalValue;
import com.xlk.paperlesstl.model.data.EventMessage;
import com.xlk.paperlesstl.model.data.SeatMember;
import com.xlk.paperlesstl.view.base.BasePresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/3/9.
 * @desc
 */
public class AnnotatePresenter extends BasePresenter<AnnotateContract.View> implements AnnotateContract.Presenter {

    public List<InterfaceMember.pbui_Item_MemberDetailInfo> memberInfos = new ArrayList<>();
    public List<SeatMember> seatMembers = new ArrayList<>();
    public List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> annotateFiles = new ArrayList<>();
    public List<Integer> saveConsentDevices = new ArrayList<>();

    public AnnotatePresenter(AnnotateContract.View view) {
        super(view);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //会议排位变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSEAT_VALUE: {
                queryMeetRanking();
                break;
            }
            //参会人员变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER_VALUE: {
                queryMember();
                break;
            }
            //会议目录文件变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORYFILE_VALUE: {
                if (msg.getMethod() == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_NOTIFY.getNumber()) {
                    byte[] o = (byte[]) msg.getObjects()[0];
                    InterfaceBase.pbui_MeetNotifyMsgForDouble info = InterfaceBase.pbui_MeetNotifyMsgForDouble.parseFrom(o);
                    if (info.getId() == Constant.ANNOTATION_FILE_DIRECTORY_ID) {
                        queryFile();
                    }
                }
                break;
            }
            //设备交互
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEOPER_VALUE: {
                if (msg.getMethod() == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_RESPONSEPRIVELIGE.getNumber()) {
                    //收到参会人员权限请求回复
                    byte[] o = (byte[]) msg.getObjects()[0];
                    InterfaceDevice.pbui_Type_MeetRequestPrivilegeResponse object = InterfaceDevice.pbui_Type_MeetRequestPrivilegeResponse.parseFrom(o);
                    // returncode 1=同意,0=不同意
                    int returncode = object.getReturncode();
                    // 发起请求的设备ID
                    int deviceid = object.getDeviceid();
                    // 发起请求的人员ID
                    int memberid = object.getMemberid();
                    LogUtils.i(TAG, "busEvent 收到参会人员权限请求回复 returncode=" + returncode + ",deviceid=" + deviceid + ",memberid=" + memberid);
                    //查看批注文件权限有了
                    if (returncode == 1) {
                        if (!saveConsentDevices.contains(deviceid)) {
                            saveConsentDevices.add(deviceid);
                        }
                        for (int i = 0; i < memberInfos.size(); i++) {
                            if (memberInfos.get(i).getPersonid() == memberid) {
                                String name = memberInfos.get(i).getName().toStringUtf8();
                                ToastUtils.showShort(App.appContext.getString(R.string.agreed_postilview, name));
                                break;
                            }
                        }
                        queryFile();
                    } else {
                        if (saveConsentDevices.contains(deviceid)) {
                            saveConsentDevices.remove(deviceid);
                        }
                        for (int i = 0; i < memberInfos.size(); i++) {
                            if (memberInfos.get(i).getPersonid() == memberid) {
                                String name = memberInfos.get(i).getName().toStringUtf8();
                                ToastUtils.showShort(App.appContext.getString(R.string.reject_postilview, name));
                                break;
                            }
                        }
                    }
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void queryMember() {
        InterfaceMember.pbui_Type_MemberDetailInfo object = jni.queryMember();
        memberInfos.clear();
        if (object != null) {
            memberInfos.addAll(object.getItemList());
        }
        queryMeetRanking();
    }

    private void queryMeetRanking() {
        InterfaceRoom.pbui_Type_MeetSeatDetailInfo object = jni.queryMeetRanking();
        seatMembers.clear();
        if (object != null) {
            List<InterfaceRoom.pbui_Item_MeetSeatDetailInfo> itemList = object.getItemList();
            for (int i = 0; i < itemList.size(); i++) {
                InterfaceRoom.pbui_Item_MeetSeatDetailInfo item = itemList.get(i);
                for (int j = 0; j < memberInfos.size(); j++) {
                    if (memberInfos.get(j).getPersonid() == item.getNameId()) {
                        seatMembers.add(new SeatMember(memberInfos.get(j), item));
                    }
                }
            }
        }
        mView.updateMember();
    }

    @Override
    public void queryFile() {
        InterfaceFile.pbui_Type_MeetDirFileDetailInfo info = jni.queryMeetDirFile(Constant.ANNOTATION_FILE_DIRECTORY_ID);
        annotateFiles.clear();
        if (info != null) {
            annotateFiles.addAll(info.getItemList());
        }
        mView.updateFiles();
    }

    @Override
    public boolean hasPermission(int deviceId) {
        if (deviceId == GlobalValue.localDeviceId) {
            return true;
        }
        return saveConsentDevices.contains(deviceId);
    }
}
