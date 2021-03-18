package com.xlk.paperlesstl.view.fragment.votecontrol;

import com.blankj.utilcode.util.LogUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.mogujie.tt.protobuf.InterfaceVote;
import com.xlk.paperlesstl.model.data.EventMessage;
import com.xlk.paperlesstl.view.admin.bean.SubmitMember;
import com.xlk.paperlesstl.view.base.BasePresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/3/13.
 * @desc
 */
public class VoteControlPresenter extends BasePresenter<VoteControlContract.View> implements VoteControlContract.Presenter {
    public List<InterfaceVote.pbui_Item_MeetVoteDetailInfo> voteInfos = new ArrayList<>();
    public List<InterfaceMember.pbui_Item_MeetMemberDetailInfo> memberInfos = new ArrayList<>();
    public List<SubmitMember> submitMembers = new ArrayList<>();
    private int mainType;

    public VoteControlPresenter(VoteControlContract.View view) {
        super(view);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //投票变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETVOTEINFO_VALUE:
                LogUtils.d(TAG, "BusEvent -->" + "投票变更通知");
                queryVote();
                break;
            //参会人员变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER_VALUE:
                //参会人员权限变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBERPERMISSION_VALUE:
                //界面状态变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEMEETSTATUS_VALUE:
                queryMember();
            default:
                break;
        }
    }

    @Override
    public void setVoteMainType(boolean isVote) {
        if (isVote) {
            mainType = InterfaceMacro.Pb_MeetVoteType.Pb_VOTE_MAINTYPE_vote_VALUE;
        } else {
            mainType = InterfaceMacro.Pb_MeetVoteType.Pb_VOTE_MAINTYPE_election_VALUE;
        }
    }

    @Override
    public void queryVote() {
        InterfaceVote.pbui_Type_MeetVoteDetailInfo object = jni.queryVote();
        voteInfos.clear();
        if (object != null) {
            List<InterfaceVote.pbui_Item_MeetVoteDetailInfo> itemList = object.getItemList();
            for (int i = 0; i < itemList.size(); i++) {
                InterfaceVote.pbui_Item_MeetVoteDetailInfo vote = itemList.get(i);
                if (vote.getMaintype() == mainType) {
                    voteInfos.add(vote);
                }
            }
        }
        mView.updateVoteList();
    }

    @Override
    public void queryMember() {
        try {
            InterfaceMember.pbui_Type_MeetMemberDetailInfo object = jni.queryAttendPeopleDetailed();
            memberInfos.clear();
            if (object != null) {
                memberInfos.addAll(object.getItemList());
            }
            mView.updateMemberList();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void querySubmittedVoters(InterfaceVote.pbui_Item_MeetVoteDetailInfo vote, boolean isDetails) {
        InterfaceVote.pbui_Type_MeetVoteSignInDetailInfo info = jni.querySubmittedVoters(vote.getVoteid());
        if (info == null) {
            return;
        }
        submitMembers.clear();
        List<InterfaceVote.pbui_SubItem_VoteItemInfo> optionInfo = vote.getItemList();
        List<InterfaceVote.pbui_Item_MeetVoteSignInDetailInfo> submittedMembers = info.getItemList();
        for (int i = 0; i < submittedMembers.size(); i++) {
            InterfaceVote.pbui_Item_MeetVoteSignInDetailInfo item = submittedMembers.get(i);
            InterfaceMember.pbui_Item_MeetMemberDetailInfo memberInfo = null;
            String chooseText = "";
            for (int j = 0; j < memberInfos.size(); j++) {
                InterfaceMember.pbui_Item_MeetMemberDetailInfo memebrItem = memberInfos.get(j);
                LogUtils.i(TAG, "querySubmittedVoters " + memebrItem.getMemberid()
                        + "," + memebrItem.getMembername().toStringUtf8()
                        + ",投票人员id=" + item.getId());
                if (memebrItem.getMemberid() == item.getId()) {
                    memberInfo = memebrItem;
                    break;
                }
            }
            if (memberInfo == null) {
                LogUtils.d(TAG, "querySubmittedVoters -->" + "没有找打提交人名字");
                break;
            }
            int selcnt = item.getSelcnt();
            //int变量的二进制表示的字符串
            String string = Integer.toBinaryString(selcnt);
            //查找字符串中为1的索引位置
            int length = string.length();
            int selectedItem = 0;
            for (int j = 0; j < length; j++) {
                char c = string.charAt(j);
                //将 char 装换成int型整数
                int a = c - '0';
                if (a == 1) {
                    //索引从0开始
                    selectedItem = length - j - 1;
                    for (int k = 0; k < optionInfo.size(); k++) {
                        if (k == selectedItem) {
                            InterfaceVote.pbui_SubItem_VoteItemInfo voteOptionsInfo = optionInfo.get(k);
                            String text = voteOptionsInfo.getText().toStringUtf8();
                            if (chooseText.length() == 0) {
                                chooseText = text;
                            } else {
                                chooseText += " | " + text;
                            }
                        }
                    }
                }
            }
            submitMembers.add(new SubmitMember(memberInfo, item, chooseText));
        }
        if (isDetails) {
            mView.showSubmittedPop(vote);
        } else {
            mView.showChartPop(vote);
        }
    }

    @Override
    public String[] queryYd(InterfaceVote.pbui_Item_MeetVoteDetailInfo vote) {
        InterfaceBase.pbui_CommonInt32uProperty yingDaoInfo = jni.queryVoteSubmitterProperty(vote.getVoteid(), 0, InterfaceMacro.Pb_MeetVotePropertyID.Pb_MEETVOTE_PROPERTY_ATTENDNUM.getNumber());
        InterfaceBase.pbui_CommonInt32uProperty yiTouInfo = jni.queryVoteSubmitterProperty(vote.getVoteid(), 0, InterfaceMacro.Pb_MeetVotePropertyID.Pb_MEETVOTE_PROPERTY_VOTEDNUM.getNumber());
        InterfaceBase.pbui_CommonInt32uProperty shiDaoInfo = jni.queryVoteSubmitterProperty(vote.getVoteid(), 0, InterfaceMacro.Pb_MeetVotePropertyID.Pb_MEETVOTE_PROPERTY_CHECKINNUM.getNumber());
        int yingDao = yingDaoInfo == null ? 0 : yingDaoInfo.getPropertyval();
        int yiTou = yiTouInfo == null ? 0 : yiTouInfo.getPropertyval();
        int shiDao = shiDaoInfo == null ? 0 : shiDaoInfo.getPropertyval();
        String yingDaoStr = "应到：" + yingDao + "人 ";
        String shiDaoStr = "实到：" + shiDao + "人 ";
        String yiTouStr = "已投：" + yiTou + "人 ";
        String weiTouStr = "未投：" + (yingDao - yiTou) + "人";
        LogUtils.d(TAG, "queryYd :  应到人数: " + yingDaoStr + "，实到：" + shiDaoStr + ", 已投人数: " + yiTouStr + "， 未投：" + weiTouStr);
        return new String[]{yingDaoStr, shiDaoStr, yiTouStr, weiTouStr};
    }
}
