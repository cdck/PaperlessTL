package com.xlk.paperlesstl.view.admin.fragment.pre.meetingManage;

import com.mogujie.tt.protobuf.InterfaceMeet;
import com.xlk.paperlesstl.view.admin.BaseInterface;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Created by xlk on 2020/10/15.
 * @desc
 */
public interface MeetingManageInterface extends BaseInterface {
    /**
     * 更新会议列表
     * @param meetings 会议数据
     */
    void updateMeetingRv(List<InterfaceMeet.pbui_Item_MeetMeetInfo> meetings);

    /**
     * 更新会议室Spinner
     */
    void updateRooms();
}
