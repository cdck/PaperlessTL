package com.xlk.paperlesstl.view.fragment.bullet;

import com.blankj.utilcode.util.LogUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBullet;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.paperlesstl.model.data.EventMessage;
import com.xlk.paperlesstl.view.base.BasePresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/3/13.
 * @desc
 */
public class BulletPresenter extends BasePresenter<BulletContract.View> implements BulletContract.Presenter {

    public List<InterfaceBullet.pbui_Item_BulletDetailInfo> noticeLists = new ArrayList<>();

    public BulletPresenter(BulletContract.View view) {
        super(view);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETBULLET_VALUE:
                LogUtils.d(TAG, "BusEvent -->" + "公告变更通知");
                queryBullet();
                break;
            default:
                break;
        }
    }

    @Override
    public void queryBullet() {
        InterfaceBullet.pbui_BulletDetailInfo notice = jni.queryNotice();
        noticeLists.clear();
        if (notice != null) {
            noticeLists.addAll(notice.getItemList());
        }
        mView.updateNoticeList();
    }
}
