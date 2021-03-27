package com.xlk.paperlesstl.view.bulletin;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBullet;
import com.mogujie.tt.protobuf.InterfaceFaceconfig;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.paperlesstl.model.Constant;
import com.xlk.paperlesstl.model.data.EventMessage;
import com.xlk.paperlesstl.model.data.EventType;
import com.xlk.paperlesstl.view.base.BasePresenter;

import java.util.List;

/**
 * @author Created by xlk on 2021/3/23.
 * @desc
 */
class BulletinPresenter extends BasePresenter<BulletinContract.View> implements BulletinContract.Presenter {
    public BulletinPresenter(BulletinContract.View view) {
        super(view);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            case EventType.BUS_BULLETIN_LOGO: {
                String filePath = (String) msg.getObjects()[0];
                Drawable drawable = Drawable.createFromPath(filePath);
                mView.updateBulletinLogo(drawable);
                break;
            }
            case EventType.BUS_BULLETIN_BG: {
                String filePath = (String) msg.getObjects()[0];
                Drawable drawable = Drawable.createFromPath(filePath);
                mView.updateBulletinBg(drawable);
                break;
            }
            //界面配置变更
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETFACECONFIG_VALUE: {
                LogUtils.d(TAG, "BusEvent -->" + "界面配置变更通知");
                queryBulletinInterfaceConfig();
                break;
            }
            //公告通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETBULLET_VALUE: {
                if (msg.getMethod() == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_STOP_VALUE) {
//                    byte[] bulletin = (byte[]) msg.getObjects()[0];
//                    InterfaceBullet.pbui_Type_StopBulletMsg info = InterfaceBullet.pbui_Type_StopBulletMsg.parseFrom(bulletin);
//                    int bulletid = info.getBulletid();
//                    LogUtils.i(TAG, "停止公告通知 bulletid=" + bulletid);
                    mView.closeBulletin(0);
                    break;
                }
            }
            default:
                break;
        }
    }

    @Override
    public void queryBulletinInterfaceConfig() {
        InterfaceFaceconfig.pbui_Type_FaceConfigInfo info = jni.queryInterFaceConfiguration();
        if (info != null) {
            List<InterfaceFaceconfig.pbui_Item_FacePictureItemInfo> pictureList = info.getPictureList();
            List<InterfaceFaceconfig.pbui_Item_FaceTextItemInfo> textList = info.getTextList();
            for (int i = 0; i < pictureList.size(); i++) {
                InterfaceFaceconfig.pbui_Item_FacePictureItemInfo item = pictureList.get(i);
                int faceid = item.getFaceid();
                int flag = item.getFlag();
                int mediaid = item.getMediaid();
                String userStr = "";
                if (faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACE_BulletinLogo_VALUE) {
                    //公告logo
                    userStr = Constant.NOTICE_LOGO_PNG_TAG;
                } else if (faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACE_BulletinBK_VALUE) {
                    //公告背景
                    userStr = Constant.NOTICE_BG_PNG_TAG;
                }
                if (!TextUtils.isEmpty(userStr)) {
                    FileUtils.createOrExistsDir(Constant.DIR_PICTURE);
                    jni.creationFileDownload(Constant.DIR_PICTURE + userStr + ".png", mediaid, 1, 0, userStr);
                }
            }

            for (int i = 0; i < textList.size(); i++) {
                InterfaceFaceconfig.pbui_Item_FaceTextItemInfo item = textList.get(i);
                int faceid = item.getFaceid();
                if (faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACE_BulletinTitle_VALUE) {
                    //标题
                    mView.updateContentTextView(item);
                } else if (faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACE_BulletinContent_VALUE) {
                    //内容
                    mView.updateTitleTextView(item);
                } else if (faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACE_BulletinBtn_VALUE) {
                    //按钮
                    mView.updateCloseButton(item);
                }
            }
        }
    }
}
