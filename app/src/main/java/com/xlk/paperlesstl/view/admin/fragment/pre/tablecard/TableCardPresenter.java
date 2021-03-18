package com.xlk.paperlesstl.view.admin.fragment.pre.tablecard;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceTablecard;
import com.xlk.paperlesstl.model.Constant;
import com.xlk.paperlesstl.model.data.EventMessage;
import com.xlk.paperlesstl.model.data.EventType;
import com.xlk.paperlesstl.view.admin.BasePresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2020/11/4.
 * @desc
 */
public class TableCardPresenter extends BasePresenter {
    private final TableCardInterface view;
    /**
     * 桌牌背景文件
     */
    public List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> pictureData = new ArrayList<>();

    public TableCardPresenter(TableCardInterface view) {
        this.view = view;
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //会议双屏显示信息变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETTABLECARD_VALUE: {
                LogUtils.i(TAG, "busEvent 会议双屏显示信息变更通知");
                queryTableCard();
                break;
            }
            //会议目录文件变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORYFILE_VALUE: {
                byte[] bytes = (byte[]) msg.getObjects()[0];
                InterfaceBase.pbui_MeetNotifyMsgForDouble info = InterfaceBase.pbui_MeetNotifyMsgForDouble.parseFrom(bytes);
                int opermethod = info.getOpermethod();
                int id = info.getId();
                int subid = info.getSubid();
                LogUtils.i(TAG, "BusEvent 会议目录文件变更通知 id=" + id + ",subId=" + subid + ",opermethod=" + opermethod);
                queryBgPicture();
                break;
            }
            //当前的桌牌背景图片下载完成
            case EventType.BUS_TABLE_CARD_BG: {
                String filePath = (String) msg.getObjects()[0];
                int mediaid = (int) msg.getObjects()[1];
                view.updateTableCardBg(filePath, mediaid);
                break;
            }
            default:
                break;
        }
    }

    public void queryTableCard() {
        InterfaceTablecard.pbui_Type_MeetTableCardDetailInfo info = jni.queryTableCard();
        if (info != null) {
            int bgphotoid = info.getBgphotoid();
            if (bgphotoid != 0) {
                int modifyflag = info.getModifyflag();
                String fileName = jni.queryFileNameByMediaId(bgphotoid);
                FileUtils.createOrExistsDir(Constant.DIR_PICTURE);
                jni.creationFileDownload(Constant.DIR_PICTURE + "table_card_bg.png", bgphotoid, 1, 1, Constant.DOWNLOAD_TABLE_CARD_BG);
            } else {
                view.clearBgImage();
            }
            view.updateTableCard(info);
        }
    }

    public void queryBgPicture() {
        InterfaceFile.pbui_TypePageResQueryrFileInfo pbui_typePageResQueryrFileInfo = jni.queryFile(0,
                InterfaceMacro.Pb_MeetFileQueryFlag.Pb_MEET_FILETYPE_QUERYFLAG_ATTRIB_VALUE
                , 0, 0, 0, InterfaceMacro.Pb_MeetFileAttrib.Pb_MEETFILE_ATTRIB_TABLECARD_VALUE, 1, 0);
        pictureData.clear();
        if (pbui_typePageResQueryrFileInfo != null) {
            pictureData.addAll(pbui_typePageResQueryrFileInfo.getItemList());
            for (int i = 0; i < pictureData.size(); i++) {
                String name = pictureData.get(i).getName().toStringUtf8();
                int mediaid = pictureData.get(i).getMediaid();
                LogUtils.i(TAG, "queryBgPicture 背景图片文件名=" + name);
                FileUtils.createOrExistsDir(Constant.DIR_PICTURE);
                jni.creationFileDownload(Constant.DIR_PICTURE + name, mediaid, 0, 1, Constant.DOWNLOAD_NO_INFORM);
            }
        }
        LogUtils.i(TAG, "queryBgPicture itemList.size=" + pictureData.size());
        view.updatePictureRv();
    }
}
