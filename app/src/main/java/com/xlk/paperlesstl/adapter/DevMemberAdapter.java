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
 * @author Created by xlk on 2021/3/8.
 * @desc
 */
public class DevMemberAdapter extends BaseQuickAdapter<DevMember, BaseViewHolder> {
    List<Integer> selectedIds = new ArrayList<>();

    public DevMemberAdapter(@Nullable List<DevMember> data) {
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
            DevMember devMember = getData().get(i);
            if (selectedIds.contains(devMember.getMemberDetailInfo().getPersonid())) {
                ids.add(devMember.getMemberDetailInfo().getPersonid());
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
                selectedIds.add(getData().get(i).getMemberDetailInfo().getPersonid());
            }
        }
        notifyDataSetChanged();
    }
    @Override
    protected void convert(@NotNull BaseViewHolder holder, DevMember devMember) {
        holder.setText(R.id.item_view_1, devMember.getMemberDetailInfo().getName().toStringUtf8());
        holder.getView(R.id.item_view_1).setSelected(selectedIds.contains(devMember.getMemberDetailInfo().getPersonid()));
    }
}
