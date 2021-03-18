package com.xlk.paperlesstl.adapter;

import android.widget.Button;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.xlk.paperlesstl.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Created by xlk on 2021/3/4.
 * @desc 主页未绑定设备的参会人
 */
public class MemberAdapter extends BaseQuickAdapter<InterfaceMember.pbui_Item_MemberDetailInfo, BaseViewHolder> {
    private int selectedId = -1;

    public MemberAdapter(@Nullable List<InterfaceMember.pbui_Item_MemberDetailInfo> data) {
        super(R.layout.item_single_button, data);
    }

    public void setSelect(int id) {
        selectedId = id;
        notifyDataSetChanged();
    }
    public int getSelectedId(){
        return selectedId;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, InterfaceMember.pbui_Item_MemberDetailInfo item) {
        Button item_view_1 = holder.getView(R.id.item_view_1);
        item_view_1.setText(item.getName().toStringUtf8());
        item_view_1.setSelected(selectedId == item.getPersonid());
    }
}
