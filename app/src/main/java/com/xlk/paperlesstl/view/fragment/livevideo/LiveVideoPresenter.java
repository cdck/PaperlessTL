package com.xlk.paperlesstl.view.fragment.livevideo;

import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.entity.node.BaseNode;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceStop;
import com.mogujie.tt.protobuf.InterfaceVideo;
import com.xlk.paperlesstl.model.GlobalValue;
import com.xlk.paperlesstl.model.data.EventMessage;
import com.xlk.paperlesstl.model.data.EventType;
import com.xlk.paperlesstl.model.data.VideoDev;
import com.xlk.paperlesstl.util.FileUtil;
import com.xlk.paperlesstl.view.base.BasePresenter;
import com.xlk.paperlesstl.view.fragment.livevideo.node.LevelDirNode;
import com.xlk.paperlesstl.view.fragment.livevideo.node.LevelFileNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xlk.paperlesstl.model.Constant.RESOURCE_ID_1;
import static com.xlk.paperlesstl.model.Constant.RESOURCE_ID_2;
import static com.xlk.paperlesstl.model.Constant.RESOURCE_ID_3;
import static com.xlk.paperlesstl.model.Constant.RESOURCE_ID_4;

/**
 * @author Created by xlk on 2021/3/11.
 * @desc
 */
public class LiveVideoPresenter extends BasePresenter<LiveVideoContract.View> implements LiveVideoContract.Presenter {

    private List<InterfaceVideo.pbui_Item_MeetVideoDetailInfo> meetVideos = new ArrayList<>();
    public List<VideoDev> videoDevs = new ArrayList<>();
    /**
     * 存放目录的展开和收缩状态
     */
    Map<Integer, Boolean> isExpandedMap = new HashMap<>();
    List<BaseNode> allData = new ArrayList<>();

    public LiveVideoPresenter(LiveVideoContract.View view) {
        super(view);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //会议视频变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETVIDEO_VALUE: {
                queryMeetVideo();
                break;
            }
            //设备寄存器变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEINFO_VALUE:
                //界面状态变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEMEETSTATUS_VALUE: {
                LogUtils.d(TAG, "BusEvent -->" + "界面状态变更通知");
                queryDevice();
                break;
            }
            //后台播放数据 DECODE
            case EventType.BUS_VIDEO_DECODE: {
                Object[] objs = msg.getObjects();
                mView.updateDecode(objs);
                break;
            }
            //后台播放数据 YUV
            case EventType.BUS_YUV_DISPLAY: {
                Object[] objs = msg.getObjects();
                mView.updateYuv(objs);
                break;
            }
            //停止资源通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_STOPPLAY_VALUE: {
                byte[] o1 = (byte[]) msg.getObjects()[0];
                if (msg.getMethod() == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_CLOSE_VALUE) {
                    //停止资源通知
                    InterfaceStop.pbui_Type_MeetStopResWork stopResWork = InterfaceStop.pbui_Type_MeetStopResWork.parseFrom(o1);
                    List<Integer> resList = stopResWork.getResList();
                    for (int resid : resList) {
                        LogUtils.i(TAG, "BusEvent -->" + "停止资源通知 resid: " + resid);
                        mView.stopResWork(resid);
                    }
                } else if (msg.getMethod() == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_NOTIFY_VALUE) {
                    //停止播放通知
                    InterfaceStop.pbui_Type_MeetStopPlay stopPlay = InterfaceStop.pbui_Type_MeetStopPlay.parseFrom(o1);
                    int resid = stopPlay.getRes();
                    int createdeviceid = stopPlay.getCreatedeviceid();
                    LogUtils.i(TAG, "BusEvent -->" + "停止播放通知 resid= " + resid + ", createdeviceid= " + createdeviceid);
                    mView.stopResWork(resid);
                }
                break;
            }
            //会议目录
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORY_VALUE: {
                queryFile();
                break;
            }
            //会议目录文件
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORYFILE_VALUE:
                byte[] bytes = (byte[]) msg.getObjects()[0];
                InterfaceBase.pbui_MeetNotifyMsgForDouble data = InterfaceBase.pbui_MeetNotifyMsgForDouble.parseFrom(bytes);
                int id = data.getId();
                LogUtils.d(TAG, "BusEvent -->" + "会议目录文件变更通知 id=" + id);
                queryFile();
                break;
        }

    }

    @Override
    public void initVideoRes() {
        jni.initVideoRes(RESOURCE_ID_1, GlobalValue.screen_width, GlobalValue.screen_height);
        jni.initVideoRes(RESOURCE_ID_2, GlobalValue.screen_width, GlobalValue.screen_height);
        jni.initVideoRes(RESOURCE_ID_3, GlobalValue.screen_width, GlobalValue.screen_height);
        jni.initVideoRes(RESOURCE_ID_4, GlobalValue.screen_width, GlobalValue.screen_height);
    }

    @Override
    public void releaseVideoRes() {
        jni.releaseVideoRes(RESOURCE_ID_1);
        jni.releaseVideoRes(RESOURCE_ID_2);
        jni.releaseVideoRes(RESOURCE_ID_3);
        jni.releaseVideoRes(RESOURCE_ID_4);
    }

    @Override
    public void stopResource(List<Integer> resIds) {
        List<Integer> ids = new ArrayList<>();
        ids.add(GlobalValue.localDeviceId);
        jni.stopResourceOperate(resIds, ids);
    }

    @Override
    public void queryMeetVideo() {
        InterfaceVideo.pbui_Type_MeetVideoDetailInfo object = jni.queryMeetVideo();
        meetVideos.clear();
        if (object != null) {
            meetVideos.addAll(object.getItemList());
        }
        queryDevice();
    }

    private void queryDevice() {
        InterfaceDevice.pbui_Type_DeviceDetailInfo object = jni.queryDeviceInfo();
        videoDevs.clear();
        if (object != null) {
            List<InterfaceDevice.pbui_Item_DeviceDetailInfo> pdevList = object.getPdevList();
            for (int i = 0; i < meetVideos.size(); i++) {
                InterfaceVideo.pbui_Item_MeetVideoDetailInfo info = meetVideos.get(i);
                int deviceid = info.getDeviceid();
                for (int j = 0; j < pdevList.size(); j++) {
                    InterfaceDevice.pbui_Item_DeviceDetailInfo dev = pdevList.get(j);
                    if (dev.getDevcieid() == deviceid) {
                        videoDevs.add(new VideoDev(info, dev));
                    }
                }
            }
        }
        mView.updateMeetVideo();
    }

    @Override
    public void queryFile() {
        InterfaceFile.pbui_Type_MeetDirDetailInfo object = jni.queryMeetDir();
        List<InterfaceFile.pbui_Item_MeetDirDetailInfo> itemList = object.getItemList();
        saveCurrentExpandStatus();
        allData.clear();
        for (int i = 0; i < itemList.size(); i++) {
            InterfaceFile.pbui_Item_MeetDirDetailInfo item = itemList.get(i);
            if (item.getParentid() != 0) continue;
            int dirId = item.getId();
            LevelDirNode levelDirNode = new LevelDirNode(new ArrayList<>(), dirId, item.getName().toStringUtf8());
            levelDirNode.setExpanded(beforeIsExpanded(dirId));
            allData.add(levelDirNode);
            queryMeetDirFile(dirId);
        }
        mView.updateFiles();
    }

    private void queryMeetDirFile(int dirId) {
        InterfaceFile.pbui_Type_MeetDirFileDetailInfo object = jni.queryMeetDirFile(dirId);
        LevelDirNode dirNode = findDirNode(dirId);
        if (dirNode != null) {
            dirNode.setChildNode(new ArrayList<>());
            if (object != null) {
                List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> itemList = object.getItemList();
                List<BaseNode> fileNodes = new ArrayList<>();
                for (int j = 0; j < itemList.size(); j++) {
                    InterfaceFile.pbui_Item_MeetDirFileDetailInfo info = itemList.get(j);
                    String fileName = info.getName().toStringUtf8();
                    if (FileUtil.isVideo(fileName)) {
                        LevelFileNode levelFileNode = new LevelFileNode(dirId, info.getMediaid(), fileName);
                        fileNodes.add(levelFileNode);
                    }
                }
                dirNode.setChildNode(fileNodes);
            }
        } else {
            LogUtils.e(TAG, "找不到该目录id " + dirId);
        }
    }


    private LevelDirNode findDirNode(int dirId) {
        for (int i = 0; i < allData.size(); i++) {
            LevelDirNode dirNode = (LevelDirNode) allData.get(i);
            if (dirNode.getDirId() == dirId) {
                return dirNode;
            }
        }
        return null;
    }

    /**
     * 获取之前的目录是否是展开状态
     *
     * @param dirId 目录id
     */
    private boolean beforeIsExpanded(int dirId) {
        if (isExpandedMap.containsKey(dirId)) {
            return isExpandedMap.get(dirId);
        }
        return false;
    }

    /**
     * 保存当前所有的目录的展开状态
     */
    private void saveCurrentExpandStatus() {
        isExpandedMap.clear();
        for (int i = 0; i < allData.size(); i++) {
            BaseNode baseNode = allData.get(i);
            if (baseNode instanceof LevelDirNode) {
                LevelDirNode dirNode = (LevelDirNode) baseNode;
                int dirId = dirNode.getDirId();
                isExpandedMap.put(dirId, dirNode.isExpanded());
            }
        }
    }
}
