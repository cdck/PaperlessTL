package com.xlk.paperlesstl.view.admin.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.view.admin.bean.SubmitMember;

import java.util.List;

import androidx.annotation.Nullable;

/**
 * @author xlk
 * @date 2020/4/3
 * @desc 投票和选举查看详情界面
 */
public class SubmitMemberAdapter extends BaseQuickAdapter<SubmitMember, BaseViewHolder> {
    public SubmitMemberAdapter(int layoutResId, @Nullable List<SubmitMember> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SubmitMember item) {
        helper.setText(R.id.number, String.valueOf(helper.getLayoutPosition() + 1))
                .setText(R.id.member, item.getMemberInfo().getMembername().toStringUtf8())
                .setText(R.id.answer, item.getAnswer());
    }
}
