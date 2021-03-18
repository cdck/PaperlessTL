package com.xlk.paperlesstl.view.fragment.material;

import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.entity.node.BaseNode;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.paperlesstl.model.Constant;
import com.xlk.paperlesstl.model.data.EventMessage;
import com.xlk.paperlesstl.view.base.BasePresenter;
import com.xlk.paperlesstl.view.fragment.material.node.LevelDirNode;
import com.xlk.paperlesstl.view.fragment.material.node.LevelFileNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Created by xlk on 2021/3/8.
 * @desc
 */
public class MaterialPresenter extends BasePresenter<MaterialContract.View> implements MaterialContract.Presenter {
    List<BaseNode> allData = new ArrayList<>();
    List<BaseNode> showFiles = new ArrayList<>();
    /**
     * 存放目录的展开和收缩状态
     */
    Map<Integer, Boolean> isExpandedMap = new HashMap<>();
    Map<Integer, Boolean> isSelectedMap = new HashMap<>();

    public MaterialPresenter(MaterialContract.View view) {
        super(view);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //会议目录
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORY_VALUE: {
                LogUtils.i(TAG, "会议目录变更通知");
                queryDir();
                break;
            }
            //会议目录文件
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORYFILE_VALUE: {
                byte[] bytes = (byte[]) msg.getObjects()[0];
                InterfaceBase.pbui_MeetNotifyMsgForDouble info = InterfaceBase.pbui_MeetNotifyMsgForDouble.parseFrom(bytes);
                int id = info.getId();
                int subid = info.getSubid();
                int opermethod = info.getOpermethod();
                LogUtils.d(TAG, "会议目录文件变更通知 id=" + id + ",subid=" + subid + ",opermethod=" + opermethod);
                if (id != Constant.SHARED_FILE_DIRECTORY_ID && id != Constant.ANNOTATION_FILE_DIRECTORY_ID) {//过滤去除批注和共享资料
                    //文件变更,相对的目录中的文件数量也需要更新
                    queryDir();
                }
                break;
            }
        }
    }

    @Override
    public void queryDir() {
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
                if (dirId != Constant.SHARED_FILE_DIRECTORY_ID && dirId != Constant.ANNOTATION_FILE_DIRECTORY_ID) {
                    LevelDirNode levelDirItem = new LevelDirNode(new ArrayList<>(), dirId, info.getName().toStringUtf8());
                    levelDirItem.setExpanded(beforeIsExpanded(dirId));
                    LogUtils.i(TAG, "添加目录id " + dirId);
                    allData.add(levelDirItem);
                    queryMeetDirFile(dirId);
                }
            }
        }
        showFiles.clear();
        showFiles.addAll(allData);
        mView.showFiles();
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
                    LevelFileNode levelFileItem = new LevelFileNode(
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
            if (baseNode instanceof LevelDirNode) {
                LevelDirNode dirNode = (LevelDirNode) baseNode;
                int dirId = dirNode.getDirId();
                isExpandedMap.put(dirId, dirNode.isExpanded());
            }
        }
    }
}
