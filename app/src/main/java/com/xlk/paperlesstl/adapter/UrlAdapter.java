package com.xlk.paperlesstl.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.xlk.paperlesstl.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Created by xlk on 2021/3/11.
 * @desc
 */
public class UrlAdapter extends BaseQuickAdapter<InterfaceBase.pbui_Item_UrlDetailInfo, BaseViewHolder> {
    public UrlAdapter(@Nullable List<InterfaceBase.pbui_Item_UrlDetailInfo> data) {
        super(R.layout.item_web_url, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, InterfaceBase.pbui_Item_UrlDetailInfo item) {
        holder.setText(R.id.item_view_1, item.getName().toStringUtf8())
                .setText(R.id.item_view_2, item.getAddr().toStringUtf8());
    }
}
