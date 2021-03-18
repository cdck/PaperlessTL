package com.xlk.paperlesstl.view.admin.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.view.admin.bean.DevMember;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * @author xlk
 * @date 2020/3/26
 * @desc 悬浮框中在线参会人
 */
public class WmScreenMemberAdapter extends BaseQuickAdapter<DevMember, BaseViewHolder> {
    List<Integer> ids = new ArrayList<>();

    public WmScreenMemberAdapter(int layoutResId, @Nullable List<DevMember> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DevMember item) {
        helper.setText(R.id.item_view_1, item.getMemberDetailInfo().getName().toStringUtf8());
        helper.getView(R.id.item_view_1).setSelected(ids.contains(item.getDeviceDetailInfo().getDevcieid()));
    }

    public List<Integer> getChooseIds() {
        return ids;
    }

    /**
     * 作为单选adapter时才使用
     *
     * @return
     */
    public DevMember getChoose() {
        for (int i = 0; i < getData().size(); i++) {
            DevMember devMember = getData().get(i);
            if (ids.contains(devMember.getDeviceDetailInfo().getDevcieid())) {
                return devMember;
            }
        }
        return null;
    }

    public List<Integer> getChooseMemberIds() {
        List<Integer> temps = new ArrayList<>();
        for (int i = 0; i < getData().size(); i++) {
            DevMember devMember = getData().get(i);
            if (ids.contains(devMember.getDeviceDetailInfo().getDevcieid())) {
                temps.add(devMember.getMemberDetailInfo().getPersonid());
            }
        }
        return temps;
    }

    public void notifyChecks() {
        List<Integer> temp = new ArrayList<>();
        for (int i = 0; i < getData().size(); i++) {
            if (ids.contains(getData().get(i).getDeviceDetailInfo().getDevcieid())) {
                temp.add(getData().get(i).getDeviceDetailInfo().getDevcieid());
            }
        }
        ids = temp;
        notifyDataSetChanged();
    }

    public void choose(int devId) {
        if (ids.contains(devId)) {
            ids.remove(ids.indexOf(devId));
        } else {
            ids.add(devId);
        }
        notifyDataSetChanged();
    }

    public boolean isChooseAll() {
        return getData().size() == ids.size();
    }

    public void setChooseAll(boolean isAll) {
        ids.clear();
        if (isAll) {
            for (int i = 0; i < getData().size(); i++) {
                ids.add(getData().get(i).getDeviceDetailInfo().getDevcieid());
            }
        }
        notifyDataSetChanged();
    }

    public void clearChoose() {
        ids.clear();
    }
}
