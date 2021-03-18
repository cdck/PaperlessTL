package com.xlk.paperlesstl.view.admin.fragment.reserve.task;

import com.blankj.utilcode.util.LogUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceTask;
import com.xlk.paperlesstl.model.Constant;
import com.xlk.paperlesstl.model.data.EventMessage;
import com.xlk.paperlesstl.model.data.EventType;
import com.xlk.paperlesstl.util.FileUtil;
import com.xlk.paperlesstl.view.admin.BasePresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2020/11/16.
 * @desc
 */
public class TaskManagerPresenter extends BasePresenter {
    private final TaskManagerInterface view;
    public List<InterfaceTask.pbui_Item_MeetTaskInfo> tasks = new ArrayList<>();
    /**
     * 所有的发布文件
     */
    public List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> releaseFileData = new ArrayList<>();
    /**
     * 所有的会议发布设备
     */
    public List<InterfaceDevice.pbui_Item_DeviceDetailInfo> releaseDevices = new ArrayList<>();

    public TaskManagerPresenter(TaskManagerInterface view) {
        this.view = view;
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //上传会议发布文件完毕
            case EventType.BUS_UPLOAD_RELEASE_FILE_FINISH: {
                LogUtils.i(TAG, "busEvent 上传会议发布文件完毕");
                queryReleaseFile();
                break;
            }
            default:
                break;
        }
    }

    /**
     * 查询任务
     */
    public void queryTask() {
        InterfaceTask.pbui_Type_MeetTaskInfo info = jni.queryTask();
        tasks.clear();
        if (info != null) {
            tasks.addAll(info.getItemList());
        }
        view.updateTask();
    }

    /**
     * 查询指定任务详情
     *
     * @param taskId 任务id
     */
    public void queryTaskDetail(int taskId) {
        InterfaceTask.pbui_Item_MeetTaskDetailInfo taskInfo = jni.queryTaskById(taskId);
        view.updateUI(taskInfo);
    }

    //查询会议发布文件
    void queryReleaseFile() {
        InterfaceFile.pbui_TypePageResQueryrFileInfo pbui_typePageResQueryrFileInfo = jni.queryFile(
                0, InterfaceMacro.Pb_MeetFileQueryFlag.Pb_MEET_FILETYPE_QUERYFLAG_ATTRIB_VALUE, 0, 0, 0,
                InterfaceMacro.Pb_MeetFileAttrib.Pb_MEETFILE_ATTRIB_PUBLISH_VALUE, 1, 0);
        //过滤掉媒体id小于0的文件，测试查看结果是：文档类和其它类的文件媒体id都<0
        releaseFileData.clear();
        if (pbui_typePageResQueryrFileInfo != null) {
            List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> itemList = pbui_typePageResQueryrFileInfo.getItemList();
            for (int i = 0; i < itemList.size(); i++) {
                InterfaceFile.pbui_Item_MeetDirFileDetailInfo item = itemList.get(i);
                String fileName = item.getName().toStringUtf8();
                if (FileUtil.isAudio(fileName)) {
                    releaseFileData.add(item);
                }
            }
        }
        LogUtils.i(TAG, "queryReleaseFile releaseFileData.size=" + releaseFileData.size());
        view.updateReleaseFileRv();
    }

    void queryDevice() {
        InterfaceDevice.pbui_Type_DeviceDetailInfo pbui_type_deviceDetailInfo = jni.queryDeviceInfo();
        releaseDevices.clear();
        if (pbui_type_deviceDetailInfo != null) {
            List<InterfaceDevice.pbui_Item_DeviceDetailInfo> pdevList = pbui_type_deviceDetailInfo.getPdevList();
            for (int i = 0; i < pdevList.size(); i++) {
                InterfaceDevice.pbui_Item_DeviceDetailInfo dev = pdevList.get(i);
                if (Constant.isThisDevType(InterfaceMacro.Pb_DeviceIDType.Pb_DeviceIDType_MeetPublish_VALUE, dev.getDevcieid())) {
                    releaseDevices.add(dev);
                }
            }
        }
        view.updateReleaseDeviceRv();
    }

}
