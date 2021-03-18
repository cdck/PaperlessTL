package com.xlk.paperlesstl.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.view.admin.bean.DevMember;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/3/10.
 * @desc
 */
public class WmMemberAdapter extends BaseQuickAdapter<DevMember, BaseViewHolder> {
    private List<Integer> selectedIds = new ArrayList<>();
    public WmMemberAdapter( @Nullable List<DevMember> data) {
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
                int id = getData().get(i).getDeviceDetailInfo().getDevcieid();
                selectedIds.add(id);
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
            int id = getData().get(i).getDeviceDetailInfo().getDevcieid();
            if (selectedIds.contains(id)) {
                ids.add(id);
            }
        }
        selectedIds.clear();
        selectedIds.addAll(ids);
        return selectedIds;
    }
    @Override
    protected void convert(@NotNull BaseViewHolder holder, DevMember devMember) {
        holder.setText(R.id.item_view_1, devMember.getMemberDetailInfo().getName().toStringUtf8());
        holder.getView(R.id.item_view_1).setSelected(selectedIds.contains(devMember.getDeviceDetailInfo().getDevcieid()));
    }
}
