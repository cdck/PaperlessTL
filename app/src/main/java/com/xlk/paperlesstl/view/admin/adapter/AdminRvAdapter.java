package com.xlk.paperlesstl.view.admin.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.view.admin.bean.AdminFunctionBean;

import java.util.List;

import androidx.annotation.Nullable;

/**
 * @author Created by xlk on 2020/9/17.
 * @desc 后台管理界面功能列表
 */
public class AdminRvAdapter extends BaseQuickAdapter<AdminFunctionBean, BaseViewHolder> {
    private int selectedPosition = 0;

    public AdminRvAdapter(@Nullable List<AdminFunctionBean> data) {
        super(R.layout.item_admin_meun, data);
    }

    public void setSelect(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

    @Override
    protected void convert(BaseViewHolder helper, AdminFunctionBean item) {
        ImageView item_admin_iv = helper.getView(R.id.item_admin_iv);
        item_admin_iv.setImageResource(item.getDrawableResId());
        int layoutPosition = helper.getLayoutPosition();
        helper.itemView.setSelected(layoutPosition == selectedPosition);
    }
}
