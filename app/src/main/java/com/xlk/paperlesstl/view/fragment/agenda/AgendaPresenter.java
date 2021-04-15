package com.xlk.paperlesstl.view.fragment.agenda;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceAgenda;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.model.Constant;
import com.xlk.paperlesstl.model.GlobalValue;
import com.xlk.paperlesstl.model.data.EventMessage;
import com.xlk.paperlesstl.model.data.EventType;
import com.xlk.paperlesstl.view.base.BasePresenter;

import java.io.File;

/**
 * @author Created by xlk on 2021/3/8.
 * @desc
 */
public class AgendaPresenter extends BasePresenter<AgendaContract.View> implements AgendaContract.Presenter {
    public AgendaPresenter(AgendaContract.View view) {
        super(view);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //议程文件下载完成
            case EventType.BUS_AGENDA_FILE:
                String path = (String) msg.getObjects()[0];
                LogUtils.e("议程文件下载完成 path=" + path);
                mView.displayFile(path);
                break;
            case EventType.BUS_X5_INSTALL://腾讯X5内核加载完成
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETAGENDA_VALUE://议程变更通知
                queryAgenda();
                break;
            default:
                break;
        }
    }

    @Override
    public void queryAgenda() {
        mView.initDefault();
        InterfaceAgenda.pbui_meetAgenda meetAgenda = jni.queryAgenda();
        if (meetAgenda != null) {
            int agendatype = meetAgenda.getAgendatype();
            if (agendatype == InterfaceMacro.Pb_AgendaType.Pb_MEET_AGENDA_TYPE_TEXT_VALUE) {
                String s = meetAgenda.getText().toStringUtf8();
                mView.updateAgendaTv(s);
            } else if (agendatype == InterfaceMacro.Pb_AgendaType.Pb_MEET_AGENDA_TYPE_FILE_VALUE) {
                int mediaid = meetAgenda.getMediaid();
                byte[] bytes = jni.queryFileProperty(InterfaceMacro.Pb_MeetFilePropertyID.Pb_MEETFILE_PROPERTY_NAME.getNumber(), mediaid);
                try {
                    InterfaceBase.pbui_CommonTextProperty textProperty = InterfaceBase.pbui_CommonTextProperty.parseFrom(bytes);
                    String fileName = textProperty.getPropertyval().toStringUtf8();
                    LogUtils.i(TAG, "fun_queryAgenda 获取到文件议程 -->" + mediaid + ", 文件名：" + fileName);
                    FileUtils.createOrExistsDir(Constant.DOWNLOAD_DIR);
                    File file = new File(Constant.DOWNLOAD_DIR + fileName);
                    if (file.exists()) {
                        if (GlobalValue.downloadingFiles.contains(mediaid)) {
                            LogUtils.e("queryAgenda 文件下载中=" + fileName);
                            ToastUtils.showShort(R.string.file_downloading);
                        } else {
                            mView.displayFile(file.getAbsolutePath());
                        }
                    } else {
                        jni.creationFileDownload(Constant.DOWNLOAD_DIR + fileName, mediaid, 1, 0, Constant.DOWNLOAD_AGENDA_FILE);
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            } else if (agendatype == InterfaceMacro.Pb_AgendaType.Pb_MEET_AGENDA_TYPE_TIME_VALUE) {

            }
        }
    }
}
