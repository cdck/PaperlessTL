package com.xlk.paperlesstl.adapter;

import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.model.Constant;
import com.xlk.paperlesstl.view.admin.bean.DevControlBean;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/3/12.
 * @desc
 */
public class DeviceControlAdapter extends BaseQuickAdapter<DevControlBean, BaseViewHolder> {
    List<Integer> selectedIds = new ArrayList<>();

    public DeviceControlAdapter(@Nullable List<DevControlBean> data) {
        super(R.layout.item_device_control, data);
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
            int devcieid = getData().get(i).getDeviceInfo().getDevcieid();
            if (selectedIds.contains(devcieid)) {
                ids.add(devcieid);
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
                selectedIds.add(getData().get(i).getDeviceInfo().getDevcieid());
            }
        }
        notifyDataSetChanged();
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, DevControlBean item) {
        InterfaceRoom.pbui_Item_MeetRoomDevSeatDetailInfo seatInfo = item.getSeatInfo();
        InterfaceDevice.pbui_Item_DeviceDetailInfo dev = item.getDeviceInfo();
        int deviceflag = dev.getDeviceflag();
        boolean isout = InterfaceMacro.Pb_MeetDeviceFlag.Pb_MEETDEVICE_FLAG_OPENOUTSIDE_VALUE == (deviceflag & InterfaceMacro.Pb_MeetDeviceFlag.Pb_MEETDEVICE_FLAG_OPENOUTSIDE_VALUE);
        boolean online = dev.getNetstate() == 1;
        holder.setText(R.id.item_view_1, String.valueOf(holder.getLayoutPosition() + 1))
                .setText(R.id.item_view_2, seatInfo != null ? seatInfo.getMembername().toStringUtf8() : "")
                .setText(R.id.item_view_3, dev.getDevname().toStringUtf8())
                .setText(R.id.item_view_4, online ? getContext().getString(R.string.online) : getContext().getString(R.string.offline))
                .setText(R.id.item_view_5, Constant.getInterfaceStateName(getContext(), dev.getFacestate()))
                .setText(R.id.item_view_6, isout ? "√" : "")
                .setText(R.id.item_view_7, Constant.getDeviceTypeName(getContext(), dev.getDevcieid()));

        int textColor = online ? getContext().getColor(R.color.online) : getContext().getColor(R.color.light_black);
        holder.setTextColor(R.id.item_view_1, textColor)
                .setTextColor(R.id.item_view_2, textColor)
                .setTextColor(R.id.item_view_3, textColor)
                .setTextColor(R.id.item_view_4, textColor)
                .setTextColor(R.id.item_view_5, textColor)
                .setTextColor(R.id.item_view_6, textColor)
                .setTextColor(R.id.item_view_7, textColor);
        boolean isSelected = selectedIds.contains(dev.getDevcieid());
        CheckBox cb = holder.getView(R.id.item_view_1);
        cb.setChecked(isSelected);
//        LinearLayout item_root_view = holder.getView(R.id.item_root_view);
//        int backgroundColor = isSelected ? getContext().getColor(R.color.light_blue) : getContext().getColor(R.color.white);
//        holder.setBackgroundColor(R.id.item_view_1, backgroundColor)
//                .setBackgroundColor(R.id.item_view_2, backgroundColor)
//                .setBackgroundColor(R.id.item_view_3, backgroundColor)
//                .setBackgroundColor(R.id.item_view_4, backgroundColor)
//                .setBackgroundColor(R.id.item_view_5, backgroundColor)
//                .setBackgroundColor(R.id.item_view_6, backgroundColor)
//                .setBackgroundColor(R.id.item_view_7, backgroundColor)
//                .setBackgroundColor(R.id.item_view_8, backgroundColor);

    }
}
