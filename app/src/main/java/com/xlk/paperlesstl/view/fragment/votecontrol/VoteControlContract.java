package com.xlk.paperlesstl.view.fragment.votecontrol;

import com.mogujie.tt.protobuf.InterfaceVote;
import com.xlk.paperlesstl.view.base.IBasePresenter;
import com.xlk.paperlesstl.view.base.IBaseView;

/**
 * @author Created by xlk on 2021/3/13.
 * @desc
 */
public interface VoteControlContract {
    interface View extends IBaseView{
        void updateVoteList();

        void updateMemberList();

        void showSubmittedPop(InterfaceVote.pbui_Item_MeetVoteDetailInfo vote);

        void showChartPop(InterfaceVote.pbui_Item_MeetVoteDetailInfo vote);
    }
    interface Presenter extends IBasePresenter{
        void queryVote();
        void queryMember();

        void setVoteMainType(boolean isVote);

        void querySubmittedVoters(InterfaceVote.pbui_Item_MeetVoteDetailInfo vote,boolean isDetails);

        String[] queryYd(InterfaceVote.pbui_Item_MeetVoteDetailInfo vote);
    }
}
