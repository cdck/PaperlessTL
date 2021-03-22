package com.xlk.paperlesstl.view.admin.fragment.pre.meetingManage;

import com.mogujie.tt.protobuf.InterfaceRoom;

/**
 * @author Created by xlk on 2021/3/22.
 * @desc
 */
public class SpRoomBean {
    InterfaceRoom.pbui_Item_MeetRoomDetailInfo room;

    public SpRoomBean(InterfaceRoom.pbui_Item_MeetRoomDetailInfo room) {
        this.room = room;
    }

    public InterfaceRoom.pbui_Item_MeetRoomDetailInfo getRoom() {
        return room;
    }

    @Override
    public String toString() {
        return room.getName().toStringUtf8();
    }
}
