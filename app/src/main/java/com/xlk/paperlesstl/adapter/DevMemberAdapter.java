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
    List<Integer> deviceIds = new ArrayList<>();

    public DevMemberAdapter(@Nullable List<DevMember> data) {
        super(R.layout.item_single_button, data);
    }

    public void choose(int devId) {
        if (deviceIds.contains(devId)) {
            deviceIds.remove(deviceIds.indexOf(devId));
        } else {
            deviceIds.add(devId);
        }
        notifyDataSetChanged();
    }

    public List<Integer> getDeviceIds() {
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < getData().size(); i++) {
            DevMember devMember = getData().get(i);
            if (deviceIds.contains(devMember.getDeviceDetailInfo().getDevcieid())) {
                ids.add(devMember.getDeviceDetailInfo().getDevcieid());
            }
        }
        deviceIds.clear();
        deviceIds.addAll(ids);
        return deviceIds;
    }

    public boolean isChooseAll() {
        return getDeviceIds().size() == getData().size();
    }

    public void setChooseAll(boolean all) {
        deviceIds.clear();
        if (all) {
            for (int i = 0; i < getData().size(); i++) {
                deviceIds.add(getData().get(i).getDeviceDetailInfo().getDevcieid());
            }
        }
        notifyDataSetChanged();
    }
    @Override
    protected void convert(@NotNull BaseViewHolder holder, DevMember devMember) {
        holder.setText(R.id.item_view_1, devMember.getMemberDetailInfo().getName().toStringUtf8());
        holder.getView(R.id.item_view_1).setSelected(deviceIds.contains(devMember.getDeviceDetailInfo().getDevcieid()));
    }
}
