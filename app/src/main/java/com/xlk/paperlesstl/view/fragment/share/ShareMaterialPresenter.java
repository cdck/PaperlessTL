package com.xlk.paperlesstl.view.fragment.share;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.paperlesstl.model.Constant;
import com.xlk.paperlesstl.model.data.EventMessage;
import com.xlk.paperlesstl.view.base.BasePresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/3/11.
 * @desc
 */
public class ShareMaterialPresenter extends BasePresenter<ShareMaterialContract.View> implements ShareMaterialContract.Presenter {

    public List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> allFiles = new ArrayList<>();

    public ShareMaterialPresenter(ShareMaterialContract.View view) {
        super(view);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //会议目录文件变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORYFILE_VALUE: {
                if (msg.getMethod() == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_NOTIFY.getNumber()) {
                    byte[] o = (byte[]) msg.getObjects()[0];
                    InterfaceBase.pbui_MeetNotifyMsgForDouble info = InterfaceBase.pbui_MeetNotifyMsgForDouble.parseFrom(o);
                    if (info.getId() == Constant.SHARED_FILE_DIRECTORY_ID) {
                        queryFile();
                    }
                }
                break;
            }
        }
    }

    @Override
    public void queryFile() {
        InterfaceFile.pbui_Type_MeetDirFileDetailInfo object = jni.queryMeetDirFile(Constant.SHARED_FILE_DIRECTORY_ID);
        allFiles.clear();
        if (object != null) {
            allFiles.addAll(object.getItemList());
        }
        mView.updateFileRv();
    }
}
