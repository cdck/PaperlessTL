package com.xlk.paperlesstl.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.model.data.VoteResultSubmitMember;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Created by xlk on 2021/3/11.
 * @desc
 */
public class VoteSubmitMemberAdapter extends BaseQuickAdapter<VoteResultSubmitMember, BaseViewHolder> {
    public VoteSubmitMemberAdapter(@Nullable List<VoteResultSubmitMember> data) {
        super(R.layout.item_vote_submit_member, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, VoteResultSubmitMember item) {
        holder.setText(R.id.item_view_1, String.valueOf(holder.getLayoutPosition() + 1))
                .setText(R.id.item_view_2, item.getMemberName())
                .setText(R.id.item_view_3, item.getOptionStr());
    }
}
