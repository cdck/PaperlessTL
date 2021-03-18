package com.xlk.paperlesstl.view.admin.fragment.system.room;

import android.content.Context;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.model.Constant;
import com.xlk.paperlesstl.model.data.EventMessage;
import com.xlk.paperlesstl.view.admin.BasePresenter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Created by xlk on 2020/9/19.
 * @desc
 */
public class AdminRoomManagePresenter extends BasePresenter {
    private final WeakReference<Context> context;
    private final WeakReference<AdminRoomManageInterface> view;
    private List<InterfaceRoom.pbui_Item_MeetRoomDetailInfo> rooms = new ArrayList<>();
    private List<InterfaceDevice.pbui_Item_DeviceDetailInfo> roomDevices = new ArrayList<>();
    private List<InterfaceDevice.pbui_Item_DeviceDetailInfo> allDevices = new ArrayList<>();
    /**
     * 所有会议室的设备id
     */
    private HashMap<Integer, List<Integer>> roomDevIds = new HashMap<>();
    /**
     * 当前选中的会议室id
     */
    private int currentRoomId = 0;
    /**
     * 选中的的会议室设备id和所有设备id
     */
    private int leftDevId, rightDevId;

    public AdminRoomManagePresenter(Context context, AdminRoomManageInterface view) {
        super();
        this.context = new WeakReference<Context>(context);
        this.view = new WeakReference<AdminRoomManageInterface>(view);
    }

    void queryRoom() {
        InterfaceRoom.pbui_Type_MeetRoomDetailInfo info = jni.queryRoom();
        rooms.clear();
        if (info != null) {
            List<InterfaceRoom.pbui_Item_MeetRoomDetailInfo> itemList = info.getItemList();
            for (int i = 0; i < itemList.size(); i++) {
                InterfaceRoom.pbui_Item_MeetRoomDetailInfo item = itemList.get(i);
                LogUtils.i(TAG, "queryRoom 会场id=" + item.getRoomid() + ",会场名称=" + item.getName().toStringUtf8());
                if (!item.getName().toStringUtf8().isEmpty()) {
                    rooms.add(item);
                }
            }
        }
        view.get().updateRoomRv(rooms);
        for (InterfaceRoom.pbui_Item_MeetRoomDetailInfo item : rooms) {
            queryDeviceByRoomId(item.getRoomid());
        }
    }

    void queryDeviceByRoomId(int roomId) {
            InterfaceRoom.pbui_Type_MeetRoomDevSeatDetailInfo info = jni.placeDeviceRankingInfo(roomId);
            List<Integer> tempRoomDevIds = new ArrayList<>();
            roomDevIds.put(roomId, tempRoomDevIds);
            if (info != null) {
                List<InterfaceRoom.pbui_Item_MeetRoomDevSeatDetailInfo> itemList = info.getItemList();
                for (InterfaceRoom.pbui_Item_MeetRoomDevSeatDetailInfo item : itemList) {
                    tempRoomDevIds.add(item.getDevid());
                }
                roomDevIds.put(roomId, tempRoomDevIds);
            }
            //当前选中的会议室有变更的时候进行更新
            if (currentRoomId != 0 && currentRoomId == roomId) {
                queryAllDevice(roomId);
            }
    }

    void queryAllDevice(int roomId) {
        LogUtils.i(TAG, "queryAllDevice roomId=" + roomId);
            currentRoomId = roomId;
            InterfaceDevice.pbui_Type_DeviceDetailInfo info = jni.queryDeviceInfo();
            roomDevices.clear();
            allDevices.clear();
            //当前会议室中的设备id
            List<Integer> currentRoomIds = roomDevIds.get(roomId);
            if (currentRoomIds != null) {
                //存放已经存在会议室中的设备id
                List<Integer> judgeIds = new ArrayList<>();
                for (List<Integer> next : roomDevIds.values()) {
                    judgeIds.addAll(next);
                }
                if (info != null) {
                    List<InterfaceDevice.pbui_Item_DeviceDetailInfo> pdevList = info.getPdevList();
                    for (InterfaceDevice.pbui_Item_DeviceDetailInfo item : pdevList) {
                        int devcieid = item.getDevcieid();
                        //当前设备存在于当前会议室中
                        if (currentRoomIds.contains(devcieid)) {
                            roomDevices.add(item);
                            //当前设备不存在于任何会议室中
                        } else if (!judgeIds.contains(devcieid)) {
                            //过滤掉未识别的设备（服务器）
                            if (!Constant.getDeviceTypeName(context.get(), devcieid).isEmpty()
                                    //过滤掉会议数据库设备
                                    && !Constant.isThisDevType(InterfaceMacro.Pb_DeviceIDType.Pb_DeviceIDType_MeetDBServer_VALUE, devcieid)
                                    //过滤掉茶水设备
                                    && !Constant.isThisDevType(InterfaceMacro.Pb_DeviceIDType.Pb_DeviceIDType_MeetService_VALUE, devcieid)) {
                                allDevices.add(item);
                            } else {
                                LogUtils.e(TAG, "queryAllDevice 过滤掉的设备：" + item.getDevcieid() + ",name=" + item.getDevname().toStringUtf8());
                            }
                        }
                    }
                }
            }
            view.get().updateRoomDeviceRv(roomDevices);
            view.get().updateAllDeviceRv(allDevices);
    }

    public void setSelectedLeftDevId(int devcieid) {
        leftDevId = devcieid;
    }

    public void setSelectedRightDevId(int devcieid) {
        rightDevId = devcieid;
    }

    public void add() {
        if (currentRoomId == 0) {
            ToastUtils.showShort(R.string.please_choose_room_first);
            return;
        }
        if (rightDevId == 0) {
            ToastUtils.showShort(R.string.please_choose_device_first);
            return;
        }
        for (int i = 0; i < allDevices.size(); i++) {
            if (allDevices.get(i).getDevcieid() == rightDevId) {
                jni.addDeviceToRoom(currentRoomId, rightDevId);
                return;
            }
        }
        //之前选中的设备已经不存在了
        ToastUtils.showShort(R.string.please_choose_device_first);
    }

    public void remove() {
        if (currentRoomId == 0) {
            ToastUtils.showShort(R.string.please_choose_room_first);
            return;
        }
        if (leftDevId == 0) {
            ToastUtils.showShort(R.string.please_choose_device_first);
            return;
        }
        for (int i = 0; i < roomDevices.size(); i++) {
            if (roomDevices.get(i).getDevcieid() == leftDevId) {
                jni.removeDeviceFromRoom(currentRoomId, leftDevId);
                return;
            }
        }
        //之前选中的设备已经不存在了
        ToastUtils.showShort(R.string.please_choose_device_first);
    }

    public void addRoom(String name, String address, String remarks) {
        jni.addRoom(name, address, remarks);
    }

    public void delRoom() {
        if (currentRoomId == 0) {
            ToastUtils.showShort(R.string.please_choose_room_first);
            return;
        }
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getRoomid() == currentRoomId) {
                jni.delRoom(currentRoomId);
                return;
            }
        }
        //之前选中的会议室已经不存在了
        ToastUtils.showShort(R.string.please_choose_room_first);
    }

    public void modifyRoom(String name, String address, String remarks) {
        if (currentRoomId == 0) {
            ToastUtils.showShort(R.string.please_choose_room_first);
            return;
        }
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getRoomid() == currentRoomId) {
                jni.modifyRoom(currentRoomId, name, address, remarks);
                return;
            }
        }
        //之前选中的会议室已经不存在了
        ToastUtils.showShort(R.string.please_choose_room_first);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        context.clear();
        view.clear();
    }

    @Override
    public void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //会场信息变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOM_VALUE: {
                if (msg.getMethod() == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_NOTIFY_VALUE) {
                    LogUtils.i(TAG, "BusEvent 会场信息变更通知");
                    queryRoom();
                }
                break;
            }
            //会场设备信息变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOMDEVICE_VALUE: {
                if (msg.getMethod() == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_NOTIFY_VALUE) {
                    byte[] bytes = (byte[]) msg.getObjects()[0];
                    InterfaceBase.pbui_MeetNotifyMsgForDouble pbui_meetNotifyMsgForDouble = InterfaceBase.pbui_MeetNotifyMsgForDouble.parseFrom(bytes);
                    int id = pbui_meetNotifyMsgForDouble.getId();
                    int opermethod = pbui_meetNotifyMsgForDouble.getOpermethod();
                    int subid = pbui_meetNotifyMsgForDouble.getSubid();
                    LogUtils.i(TAG, "BusEvent 会场设备信息变更通知: opermethod=" + opermethod + ",id=" + id + ",subId=" + subid);
                    queryDeviceByRoomId(id);
                }
                break;
            }
            //设备寄存器变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEINFO_VALUE: {
                if (msg.getMethod() == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_NOTIFY_VALUE) {
                    if (currentRoomId != 0) {
                        for (int i = 0; i < rooms.size(); i++) {
                            if (rooms.get(i).getRoomid() == currentRoomId) {
                                queryAllDevice(currentRoomId);
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
}
