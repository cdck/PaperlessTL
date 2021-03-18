package com.xlk.paperlesstl.view.admin.fragment.system.member;

import android.content.Context;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfacePerson;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.model.data.EventMessage;
import com.xlk.paperlesstl.util.JxlUtil;
import com.xlk.paperlesstl.view.admin.BasePresenter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2020/9/21.
 * @desc
 */
public class FrequentlyMemberPresenter extends BasePresenter {
    private final WeakReference<Context> context;
    private final WeakReference<FrequentlyMemberInterface> view;
    private List<InterfacePerson.pbui_Item_PersonDetailInfo> memberInfos = new ArrayList<>();
    private int selectId;

    public FrequentlyMemberPresenter(Context context, FrequentlyMemberInterface view) {
        super();
        this.context = new WeakReference<Context>(context);
        this.view = new WeakReference<FrequentlyMemberInterface>(view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        context.clear();
        view.clear();
    }

    public void setSelectMember(int id) {
        selectId = id;
    }

    public void queryMember() {
        InterfacePerson.pbui_Type_PersonDetailInfo info = jni.queryFrequentlyMember();
        memberInfos.clear();
        if (info != null) {
            memberInfos.addAll(info.getItemList());
        }
        view.get().updateMemberRv(memberInfos);
    }

    public void addMembers(List<InterfacePerson.pbui_Item_PersonDetailInfo> memberInfos) {
        jni.addFrequentlyMembers(memberInfos);
    }

    public void addMember(InterfacePerson.pbui_Item_PersonDetailInfo build) {
        jni.addFrequentlyMember(build);
    }

    public void delMember() {
        if (selectId == 0) {
            ToastUtils.showShort(R.string.please_choose_member);
            return;
        }
        for (int i = 0; i < memberInfos.size(); i++) {
            if (memberInfos.get(i).getPersonid() == selectId) {
                jni.delFrequentlyMember(selectId);
                return;
            }
        }
        ToastUtils.showShort(R.string.please_choose_member);
    }

    public void modifyMember(InterfacePerson.pbui_Item_PersonDetailInfo.Builder builder) {
        if (selectId == 0) {
            ToastUtils.showShort(R.string.please_choose_member);
            return;
        }
        for (int i = 0; i < memberInfos.size(); i++) {
            if (memberInfos.get(i).getPersonid() == selectId) {
                InterfacePerson.pbui_Item_PersonDetailInfo build = builder.setPersonid(selectId).build();
                jni.modifyFrequentlyMember(build);
                return;
            }
        }
        ToastUtils.showShort(R.string.please_choose_member);
    }

    public void export() {
        if (memberInfos.isEmpty()) {
            ToastUtils.showShort(R.string.tip_data_empty);
            return;
        }
        JxlUtil.exportMember(memberInfos);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //常用人员变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_PEOPLE_VALUE: {
                if (msg.getMethod() == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_NOTIFY_VALUE) {
                    byte[] bytes = (byte[]) msg.getObjects()[0];
                    InterfaceBase.pbui_MeetNotifyMsg pbui_meetNotifyMsg = InterfaceBase.pbui_MeetNotifyMsg.parseFrom(bytes);
                    int id = pbui_meetNotifyMsg.getId();
                    int opermethod = pbui_meetNotifyMsg.getOpermethod();
                    LogUtils.i(TAG, "BusEvent 常用人员变更通知 id=" + id + ",opermethod=" + opermethod);
                    queryMember();
                }
                break;
            }
            default:break;
        }
    }
}
