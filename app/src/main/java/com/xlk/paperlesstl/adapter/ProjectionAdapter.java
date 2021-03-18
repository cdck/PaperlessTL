package com.xlk.paperlesstl.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.xlk.paperlesstl.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/3/9.
 * @desc
 */
public class ProjectionAdapter extends BaseQuickAdapter<InterfaceDevice.pbui_Item_DeviceDetailInfo, BaseViewHolder> {
    List<Integer> selectedIds = new ArrayList<>();

    public ProjectionAdapter(@Nullable List<InterfaceDevice.pbui_Item_DeviceDetailInfo> data) {
        super(R.layout.item_single_button, data);
    }

    public void choose(int id) {
        if (selectedIds.contains(id)) {
            selectedIds.remove(selectedIds.indexOf(id));
        } else {
            selectedIds.add(id);
        }
        notifyDataSetChanged();
    }

    public List<Integer> getSelectedIds() {
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < getData().size(); i++) {
            InterfaceDevice.pbui_Item_DeviceDetailInfo dev = getData().get(i);
            if (selectedIds.contains(dev.getDevcieid())) {
                ids.add(dev.getDevcieid());
            }
        }
        selectedIds.clear();
        selectedIds.addAll(ids);
        return selectedIds;
    }

    public boolean isChooseAll() {
        return getSelectedIds().size() == getData().size();
    }

    public void setChooseAll(boolean all) {
        selectedIds.clear();
        if (all) {
            for (int i = 0; i < getData().size(); i++) {
                selectedIds.add(getData().get(i).getDevcieid());
            }
        }
        notifyDataSetChanged();
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, InterfaceDevice.pbui_Item_DeviceDetailInfo item) {
        holder.setText(R.id.item_view_1, item.getDevname().toStringUtf8());
        holder.getView(R.id.item_view_1).setSelected(selectedIds.contains(item.getDevcieid()));
    }
}
