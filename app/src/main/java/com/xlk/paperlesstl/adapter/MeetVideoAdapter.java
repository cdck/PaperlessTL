package com.xlk.paperlesstl.adapter;

import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceVideo;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.model.data.VideoDev;
import com.xlk.paperlesstl.ui.MarqueeTextView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Created by xlk on 2021/3/11.
 * @desc
 */
public class MeetVideoAdapter extends BaseQuickAdapter<VideoDev, BaseViewHolder> {
    private int selectedDevId, selectedId;

    public MeetVideoAdapter(@Nullable List<VideoDev> data) {
        super(R.layout.item_single_checkbox, data);
    }

    public void choose(int devId, int id) {
        selectedDevId = devId;
        selectedId = id;
        notifyDataSetChanged();
    }

    public VideoDev getSelected() {
        for (int i = 0; i < getData().size(); i++) {
            VideoDev videoDev = getData().get(i);
            InterfaceVideo.pbui_Item_MeetVideoDetailInfo item = videoDev.getVideoDetailInfo();
            if (item.getDeviceid() == selectedDevId && item.getId() == selectedId) {
                return videoDev;
            }
        }
        return null;
    }


    @Override
    protected void convert(@NotNull BaseViewHolder holder, VideoDev videoDev) {
        holder.setText(R.id.item_view_2, videoDev.getName());
        boolean isOnline = videoDev.getDeviceDetailInfo().getNetstate() == 1;
        boolean isSelected = selectedDevId == videoDev.getDeviceDetailInfo().getDevcieid() && selectedId == videoDev.getVideoDetailInfo().getId();
        CheckBox view = holder.getView(R.id.item_view_1);
        view.setChecked(isSelected);
        MarqueeTextView textView = holder.getView(R.id.item_view_2);
        textView.setTextColor(isOnline ?
                getContext().getColor(R.color.blue) : getContext().getColor(R.color.black));
        LinearLayout item_root_view = holder.getView(R.id.item_root_view);
        item_root_view.setBackgroundColor(isOnline ?
                getContext().getColor(R.color.white) : getContext().getColor(R.color.gray));
    }
}
