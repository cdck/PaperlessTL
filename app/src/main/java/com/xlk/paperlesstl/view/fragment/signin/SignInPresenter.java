package com.xlk.paperlesstl.view.fragment.signin;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.xlk.paperlesstl.model.Constant;
import com.xlk.paperlesstl.model.data.EventMessage;
import com.xlk.paperlesstl.model.data.EventType;
import com.xlk.paperlesstl.view.base.BasePresenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Created by xlk on 2021/3/12.
 * @desc
 */
public class SignInPresenter extends BasePresenter<SignInContract.View> implements SignInContract.Presenter {

    private List<InterfaceRoom.pbui_Item_MeetRoomDevSeatDetailInfo> seatDetailInfos = new ArrayList<>();
    private Timer timer;
    private TimerTask task;
    private List<InterfaceMember.pbui_Item_MemberDetailInfo> members = new ArrayList<>();

    public SignInPresenter(SignInContract.View view) {
        super(view);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            case EventType.BUS_ROOM_BG: {
                String filePath = (String) msg.getObjects()[0];
                mView.updateRoomBg(filePath);
                break;
            }
            //会场设备信息变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOMDEVICE_VALUE: {
                LogUtils.e(TAG, "BusEvent 会场设备信息变更通知 -->");
                executeLater();
                break;
            }
            //会场信息变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOM_VALUE: {
                LogUtils.e(TAG, "BusEvent 会场信息变更通知 -->");
                queryRoomBg();
                break;
            }
            //签到变更
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSIGN_VALUE: {
                LogUtils.e(TAG, "签到变更通知");
                queryPlaceDeviceRankingInfo();
                break;
            }
            //参会人员变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER_VALUE: {
                LogUtils.i(TAG, "busEvent 参会人员变更通知");
                queryMember();
                break;
            }
        }
    }

    private void executeLater() {
        //解决短时间内收到很多通知，查询很多次的问题
        if (timer == null) {
            timer = new Timer();
            LogUtils.i(TAG, "创建timer");
            task = new TimerTask() {
                @Override
                public void run() {
                    queryPlaceDeviceRankingInfo();
                    task.cancel();
                    timer.cancel();
                    task = null;
                    timer = null;
                }
            };
            LogUtils.i(TAG, "500毫秒之后查询");
            timer.schedule(task, 500);
        }
    }

    @Override
    public void queryMember() {
        InterfaceMember.pbui_Type_MemberDetailInfo object = jni.queryMember();
        members.clear();
        if (object != null) {
            members.addAll(object.getItemList());
        }
    }

    @Override
    public void queryRoomBg() {
        try {
            int mediaId = jni.queryMeetRoomProperty(queryCurrentRoomId());
            if (mediaId != 0) {
                FileUtils.createOrExistsDir(Constant.DOWNLOAD_DIR);
                jni.creationFileDownload(Constant.DOWNLOAD_DIR + Constant.ROOM_BG_PNG_TAG + ".png", mediaId, 1, 0, Constant.ROOM_BG_PNG_TAG);
                return;
            }
            queryPlaceDeviceRankingInfo();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void queryPlaceDeviceRankingInfo() {
        queryMember();
        InterfaceRoom.pbui_Type_MeetRoomDevSeatDetailInfo object = jni.placeDeviceRankingInfo(queryCurrentRoomId());
        int checkedMemberCount = 0;
        seatDetailInfos.clear();
        if (object != null) {
            seatDetailInfos.addAll(object.getItemList());
            for (int i = 0; i < seatDetailInfos.size(); i++) {
                InterfaceRoom.pbui_Item_MeetRoomDevSeatDetailInfo item = seatDetailInfos.get(i);
//                if (Constant.isThisDevType(InterfaceMacro.Pb_DeviceIDType.Pb_DeviceIDType_MeetClient_VALUE, item.getDevid())) {
//                    allMemberCount++;
                if (item.getIssignin() == 1) {
                    checkedMemberCount++;
                }
//                }
            }
        }
        mView.updateView(seatDetailInfos, members.size(), checkedMemberCount);
    }
}
