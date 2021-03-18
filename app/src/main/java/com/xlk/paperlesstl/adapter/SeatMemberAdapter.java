package com.xlk.paperlesstl.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.model.data.SeatMember;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Created by xlk on 2021/3/9.
 * @desc
 */
public class SeatMemberAdapter extends BaseQuickAdapter<SeatMember, BaseViewHolder> {
    private int selectedId = -1;

    public SeatMemberAdapter(@Nullable List<SeatMember> data) {
        super(R.layout.item_seat_member, data);
    }

    public void setSelectedId(int id) {
        selectedId = id;
        notifyDataSetChanged();
    }

    public int getSelectedId() {
        for (int i = 0; i < getData().size(); i++) {
            if (getData().get(i).getSeatDetailInfo().getSeatid() == selectedId) {
                return selectedId;
            }
        }
        return -1;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder helper, SeatMember item) {
        helper.setText(R.id.item_tv_name, item.getMemberDetailInfo().getName().toStringUtf8());
        int devid = item.getSeatDetailInfo().getSeatid();
        boolean iss = selectedId == devid;
        helper.getView(R.id.item_seat_member_ll).setSelected(iss);
        helper.getView(R.id.item_iv_icon).setSelected(iss);
    }
}
