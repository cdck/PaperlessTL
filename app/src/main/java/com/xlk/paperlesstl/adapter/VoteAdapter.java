package com.xlk.paperlesstl.adapter;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceVote;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.model.Constant;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Created by xlk on 2021/3/13.
 * @desc
 */
public class VoteAdapter extends BaseQuickAdapter<InterfaceVote.pbui_Item_MeetVoteDetailInfo, BaseViewHolder> {
    private int selectedId = -1;

    public VoteAdapter(@Nullable List<InterfaceVote.pbui_Item_MeetVoteDetailInfo> data) {
        super(R.layout.item_vote, data);
    }

    public void choose(int id) {
        selectedId = id;
        notifyDataSetChanged();
    }

    public InterfaceVote.pbui_Item_MeetVoteDetailInfo getSelected() {
        for (int i = 0; i < getData().size(); i++) {
            InterfaceVote.pbui_Item_MeetVoteDetailInfo item = getData().get(i);
            if (item.getVoteid() == selectedId) {
                return item;
            }
        }
        return null;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, InterfaceVote.pbui_Item_MeetVoteDetailInfo info) {
        int votestate = info.getVotestate();
        String voteState = Constant.getVoteState(getContext(), votestate);
        TextView tv_content = holder.getView(R.id.tv_content);
        LinearLayout ll_answer_a = holder.getView(R.id.ll_answer_a);
        TextView tv_a = holder.getView(R.id.tv_a);
        TextView tv_a_count = holder.getView(R.id.tv_a_count);
        LinearLayout ll_answer_b = holder.getView(R.id.ll_answer_b);
        TextView tv_b = holder.getView(R.id.tv_b);
        TextView tv_b_count = holder.getView(R.id.tv_b_count);
        LinearLayout ll_answer_c = holder.getView(R.id.ll_answer_c);
        TextView tv_c = holder.getView(R.id.tv_c);
        TextView tv_c_count = holder.getView(R.id.tv_c_count);
        LinearLayout ll_answer_d = holder.getView(R.id.ll_answer_d);
        TextView tv_d = holder.getView(R.id.tv_d);
        TextView tv_d_count = holder.getView(R.id.tv_d_count);
        LinearLayout ll_answer_e = holder.getView(R.id.ll_answer_e);
        TextView tv_e = holder.getView(R.id.tv_e);
        TextView tv_e_count = holder.getView(R.id.tv_e_count);
        ll_answer_a.setVisibility(View.GONE);
        ll_answer_b.setVisibility(View.GONE);
        ll_answer_c.setVisibility(View.GONE);
        ll_answer_d.setVisibility(View.GONE);
        ll_answer_e.setVisibility(View.GONE);
        holder.setText(R.id.tv_vote_status, voteState);
        String voteContent = holder.getAdapterPosition() + 1 + "、" + info.getContent().toStringUtf8();
        String voteType = Constant.getVoteType(getContext(), info.getType());
        String voteMode = Constant.getVoteMode(getContext(), info.getMode());
        voteContent += "（" + voteType + "，" + voteMode + "）";
        tv_content.setText(voteContent);
        List<InterfaceVote.pbui_SubItem_VoteItemInfo> itemList = info.getItemList();
        for (int i = 0; i < itemList.size(); i++) {
            InterfaceVote.pbui_SubItem_VoteItemInfo item = itemList.get(i);
            String answer = item.getText().toStringUtf8();
            String selcnt = item.getSelcnt() + " 票";
            if (i == 0) {
                tv_a.setText(answer);
                tv_a_count.setText(selcnt);
                ll_answer_a.setVisibility(View.VISIBLE);
            } else if (i == 1) {
                tv_b.setText(answer);
                tv_b_count.setText(selcnt);
                ll_answer_b.setVisibility(View.VISIBLE);
            } else if (i == 2) {
                tv_c.setText(answer);
                tv_c_count.setText(selcnt);
                ll_answer_c.setVisibility(View.VISIBLE);
            } else if (i == 3) {
                tv_d.setText(answer);
                tv_d_count.setText(selcnt);
                ll_answer_d.setVisibility(View.VISIBLE);
            } else if (i == 4) {
                tv_e.setText(answer);
                tv_e_count.setText(selcnt);
                ll_answer_e.setVisibility(View.VISIBLE);
            }
        }
        boolean isVote = info.getMaintype() == InterfaceMacro.Pb_MeetVoteType.Pb_VOTE_MAINTYPE_vote_VALUE;
        Button btn_launch_vote = holder.getView(R.id.btn_launch_vote);
        Button btn_stop_vote = holder.getView(R.id.btn_stop_vote);
        btn_launch_vote.setText(isVote ? getContext().getString(R.string.launch_vote) : getContext().getString(R.string.launch_election));
        btn_stop_vote.setText(isVote ? getContext().getString(R.string.stop_vote) : getContext().getString(R.string.stop_election));
        btn_launch_vote.setVisibility(
                (votestate == InterfaceMacro.Pb_MeetVoteStatus.Pb_vote_notvote_VALUE || votestate == InterfaceMacro.Pb_MeetVoteStatus.Pb_vote_endvote_VALUE)
                        ? View.VISIBLE : View.GONE);
        btn_stop_vote.setVisibility(votestate == InterfaceMacro.Pb_MeetVoteStatus.Pb_vote_voteing_VALUE
                ? View.VISIBLE : View.GONE);
        boolean isSelected = selectedId == info.getVoteid();
        holder.getView(R.id.item_root_view).setBackgroundColor(isSelected ?
                getContext().getColor(R.color.light_blue) : getContext().getColor(R.color.transparent));
    }
}
