package com.xlk.paperlesstl.view.admin.fragment.reserve.task;

import com.mogujie.tt.protobuf.InterfaceTask;

/**
 * @author Created by xlk on 2020/11/16.
 * @desc
 */
public interface TaskManagerInterface {
    void updateTask();

    void updateUI(InterfaceTask.pbui_Item_MeetTaskDetailInfo taskInfo);

    void updateReleaseFileRv();

    void updateReleaseDeviceRv();
}
