package com.xlk.paperlesstl.service;

import com.mogujie.tt.protobuf.InterfaceBullet;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceFilescorevote;
import com.mogujie.tt.protobuf.InterfaceVote;
import com.xlk.paperlesstl.model.data.EventMessage;
import com.xlk.paperlesstl.view.base.IBasePresenter;
import com.xlk.paperlesstl.view.base.IBaseView;

/**
 * @author Created by xlk on 2021/3/9.
 * @desc
 */
public interface FabContract {
    interface View extends IBaseView {
        void showVideoChatWindow(int inviteflag, int operdeviceid);

        void notifyOnLineAdapter();

        void showVoteWindow(InterfaceVote.pbui_Item_MeetOnVotingDetailInfo item);

        void closeVoteView();

        void applyPermissionsInform(InterfaceDevice.pbui_Type_MeetRequestPrivilegeNotify info);

        void showBulletWindow(InterfaceBullet.pbui_Item_BulletDetailInfo info);

        void closeBulletWindow(int bulletid);

        void showNoteView(String content);

        void showScoreView(InterfaceFilescorevote.pbui_Type_StartUserDefineFileScoreNotify info);

        void closeScoreView();

        void updateVoteRv();

        void openArtBoardInform(EventMessage msg);

        void updateCanJoinList();
    }

    interface Presenter extends IBasePresenter {
        String getMemberNameByDeviceId(int deviceId);

        void queryVote();

        void queryCanJoinScreen();
    }
}
