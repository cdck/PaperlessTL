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
public class WmProjectorAdapter extends BaseQuickAdapter<InterfaceDevice.pbui_Item_DeviceDetailInfo, BaseViewHolder> {
    private List<Integer> selectedIds = new ArrayList<>();

    public WmProjectorAdapter(@Nullable List<InterfaceDevice.pbui_Item_DeviceDetailInfo> data) {
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

    public void setCheckAll(boolean b) {
        selectedIds.clear();
        if (b) {
            for (int i = 0; i < getData().size(); i++) {
                int devcieid = getData().get(i).getDevcieid();
                selectedIds.add(devcieid);
            }
        }
        notifyDataSetChanged();
    }

    public boolean isCheckAll() {
        return getSelectedIds().size() == getData().size();
    }

    public List<Integer> getSelectedIds() {
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < getData().size(); i++) {
            int devcieid = getData().get(i).getDevcieid();
            if (selectedIds.contains(devcieid)) {
                ids.add(devcieid);
            }
        }
        selectedIds.clear();
        selectedIds.addAll(ids);
        return selectedIds;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, InterfaceDevice.pbui_Item_DeviceDetailInfo item) {
        holder.setText(R.id.item_view_1, item.getDevname().toStringUtf8());
        holder.getView(R.id.item_view_1).setSelected(selectedIds.contains(item.getDevcieid()));
    }
}
