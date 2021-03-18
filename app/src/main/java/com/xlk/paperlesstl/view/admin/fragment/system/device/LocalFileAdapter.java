package com.xlk.paperlesstl.view.admin.fragment.system.device;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xlk.paperlesstl.R;

import java.io.File;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * @author Created by xlk on 2020/11/11.
 * @desc
 */
public class LocalFileAdapter extends BaseQuickAdapter<File, BaseViewHolder> {
    public LocalFileAdapter(int layoutResId, @Nullable List<File> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, File item) {
        helper.setText(R.id.tv_file_name,item.getName());
    }
}
