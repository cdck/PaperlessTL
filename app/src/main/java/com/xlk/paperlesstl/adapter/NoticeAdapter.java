package com.xlk.paperlesstl.adapter;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceBullet;
import com.xlk.paperlesstl.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Created by xlk on 2021/3/13.
 * @desc
 */
public class NoticeAdapter extends BaseQuickAdapter<InterfaceBullet.pbui_Item_BulletDetailInfo, BaseViewHolder> {
    private int selectedId = -1;

    public NoticeAdapter(@Nullable List<InterfaceBullet.pbui_Item_BulletDetailInfo> data) {
        super(R.layout.item_notice, data);
    }

    public void choose(int id) {
        selectedId = id;
        notifyDataSetChanged();
    }

    public InterfaceBullet.pbui_Item_BulletDetailInfo getSelected() {
        for (int i = 0; i < getData().size(); i++) {
            InterfaceBullet.pbui_Item_BulletDetailInfo info = getData().get(i);
            if (info.getBulletid() == selectedId) {
                return info;
            }
        }
        return null;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, InterfaceBullet.pbui_Item_BulletDetailInfo item) {
        holder.setText(R.id.tv_number, String.valueOf(holder.getLayoutPosition() + 1))
                .setText(R.id.tv_title, item.getTitle().toStringUtf8());
        boolean isSelected = selectedId == item.getBulletid();
        TextView tv_number = holder.getView(R.id.tv_number);
        TextView tv_title = holder.getView(R.id.tv_title);
        tv_number.setTextColor(isSelected ?
                getContext().getColor(R.color.red) : getContext().getColor(R.color.black));
        tv_title.setTextColor(isSelected ?
                getContext().getColor(R.color.red) : getContext().getColor(R.color.black));
    }
}
