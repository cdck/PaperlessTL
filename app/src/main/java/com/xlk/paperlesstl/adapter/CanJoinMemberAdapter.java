package com.xlk.paperlesstl.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.xlk.paperlesstl.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Created by xlk on 2021/3/15.
 * @desc
 */
public class CanJoinMemberAdapter extends BaseQuickAdapter<InterfaceDevice.pbui_Item_DeviceResPlay, BaseViewHolder> {
    public int selectedId = -1;

    public void choose(int id) {
        selectedId = id;
        notifyDataSetChanged();
    }

    public void clearSelect() {
        selectedId = -1;
        notifyDataSetChanged();
    }
    public int getSelectedId() {
        for (int i = 0; i < getData().size(); i++) {
            if (getData().get(i).getDevceid() == selectedId) {
                return selectedId;
            }
        }
        return -1;
    }

    public CanJoinMemberAdapter(@Nullable List<InterfaceDevice.pbui_Item_DeviceResPlay> data) {
        super(R.layout.item_single_button, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, InterfaceDevice.pbui_Item_DeviceResPlay item) {
        holder.setText(R.id.item_view_1, item.getName().toStringUtf8());
        holder.getView(R.id.item_view_1).setSelected(selectedId == item.getDevceid());

    }
}
