package com.xlk.paperlesstl.adapter;

import android.view.View;
import android.widget.CheckBox;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.view.admin.bean.DevMember;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/3/9.
 * @desc
 */
public class ChatMemberAdapter extends BaseQuickAdapter<DevMember, BaseViewHolder> {
    private List<Integer> selectedIds = new ArrayList<>();

    public ChatMemberAdapter(@Nullable List<DevMember> data) {
        super(R.layout.item_meet_chat_member, data);
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

    public boolean isCheckAll() {
        return getSelectedIds().size() == getData().size();
    }

    public void setCheckAll(boolean b) {
        selectedIds.clear();
        if (b) {
            for (int i = 0; i < getData().size(); i++) {
                DevMember devMember = getData().get(i);
                selectedIds.add(devMember.getMemberDetailInfo().getPersonid());
            }
        }
        notifyDataSetChanged();
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, DevMember devMember) {
        holder.setText(R.id.item_view_1, devMember.getMemberDetailInfo().getName().toStringUtf8())
                .setText(R.id.item_view_2, String.valueOf(devMember.getMemberDetailInfo().getPersonid()));
        CheckBox view = holder.getView(R.id.item_view_1);
        view.setChecked(selectedIds.contains(devMember.getMemberDetailInfo().getPersonid()));
    }
}
