package com.xlk.paperlesstl.view.offline;

import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.entity.node.BaseNode;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.mogujie.tt.protobuf.InterfaceMeet;
import com.xlk.paperlesstl.model.data.EventMessage;
import com.xlk.paperlesstl.view.base.BasePresenter;
import com.xlk.paperlesstl.view.offline.node.OfflineDirNode;
import com.xlk.paperlesstl.view.offline.node.OfflineFileNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Created by xlk on 2021/3/25.
 * @desc
 */
class OfflinePresenter extends BasePresenter<OfflineContract.View> implements OfflineContract.Presenter {

    public List<InterfaceMeet.pbui_Item_MeetMeetInfo> meetLists = new ArrayList<>();
    public List<BaseNode> allData = new ArrayList<>();
    public List<BaseNode> showFiles = new ArrayList<>();
    /**
     * 存放目录的展开和收缩状态
     */
    Map<Integer, Boolean> isExpandedMap = new HashMap<>();

    public OfflinePresenter(OfflineContract.View view) {
        super(view);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {

    }

    @Override
    public void queryMeeting() {
        InterfaceMeet.pbui_Type_MeetMeetInfo info = jni.queryAllMeeting();
        meetLists.clear();
        if (info != null) {
            meetLists.addAll(info.getItemList());
            LogUtils.e(TAG, "会议数量=" + meetLists.size());
            for (int i = 0; i < meetLists.size(); i++) {
                InterfaceMeet.pbui_Item_MeetMeetInfo item = meetLists.get(i);
                LogUtils.e(TAG, "会议ID=" + item.getId() + ",会议名称=" + item.getName().toStringUtf8());
            }
        }
        mView.updateMeetingList();
    }

    @Override
    public void queryFile() {
        InterfaceFile.pbui_Type_MeetDirDetailInfo object = jni.queryMeetDir();
        saveCurrentExpandStatus();
        allData.clear();
        if (object != null) {
            List<InterfaceFile.pbui_Item_MeetDirDetailInfo> itemList = object.getItemList();
            for (int i = 0; i < itemList.size(); i++) {
                InterfaceFile.pbui_Item_MeetDirDetailInfo info = itemList.get(i);
                int dirId = info.getId();
                if (info.getParentid() != 0) {
                    continue;
                }
                OfflineDirNode levelDirItem = new OfflineDirNode(new ArrayList<>(), dirId, info.getName().toStringUtf8(), info.getParentid(), info.getFilenum());
                levelDirItem.setExpanded(beforeIsExpanded(dirId));
                LogUtils.i(TAG, "添加目录id " + dirId);
                allData.add(levelDirItem);
                queryMeetDirFile(dirId);
            }
        }
        showFiles.clear();
        showFiles.addAll(allData);
        mView.showFiles();
    }

    private OfflineDirNode findDirNode(int dirId) {
        for (int i = 0; i < allData.size(); i++) {
            OfflineDirNode dirNode = (OfflineDirNode) allData.get(i);
            if (dirNode.getDirId() == dirId) {
                return dirNode;
            }
        }
        return null;
    }

    private void queryMeetDirFile(int dirId) {
        InterfaceFile.pbui_Type_MeetDirFileDetailInfo object = jni.queryMeetDirFile(dirId);
        OfflineDirNode dirNode = findDirNode(dirId);
        if (dirNode != null) {
            dirNode.setChildNode(new ArrayList<>());
            if (object != null) {
                List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> itemList = object.getItemList();
                List<BaseNode> fileNodes = new ArrayList<>();
                for (int j = 0; j < itemList.size(); j++) {
                    InterfaceFile.pbui_Item_MeetDirFileDetailInfo info = itemList.get(j);
                    OfflineFileNode levelFileItem = new OfflineFileNode(dirNode.getDirName(),
                            dirId, info.getMediaid(), info.getName().toStringUtf8(), info.getUploaderid(),
                            info.getUploaderRole(), info.getUploaderName().toStringUtf8(), info.getMstime(),
                            info.getSize(), info.getAttrib(), info.getFilepos());
                    fileNodes.add(levelFileItem);
                }
                dirNode.setChildNode(fileNodes);
            }
        } else {
            LogUtils.e(TAG, "找不到该目录id " + dirId);
        }
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
            if (baseNode instanceof OfflineDirNode) {
                OfflineDirNode dirNode = (OfflineDirNode) baseNode;
                int dirId = dirNode.getDirId();
                isExpandedMap.put(dirId, dirNode.isExpanded());
            }
        }
    }
}
