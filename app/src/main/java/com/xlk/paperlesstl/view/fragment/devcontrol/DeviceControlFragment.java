package com.xlk.paperlesstl.view.fragment.devcontrol;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.adapter.DeviceControlAdapter;
import com.xlk.paperlesstl.model.GlobalValue;
import com.xlk.paperlesstl.util.PopupUtil;
import com.xlk.paperlesstl.view.admin.fragment.pre.member.MemberRoleAdapter;
import com.xlk.paperlesstl.view.admin.fragment.pre.member.MemberRoleBean;
import com.xlk.paperlesstl.view.base.BaseFragment;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Created by xlk on 2021/3/12.
 * @desc
 */
public class DeviceControlFragment extends BaseFragment<DeviceControlPresenter> implements DeviceControlContract.View, View.OnClickListener {
    private RecyclerView rvDevice;
    private CheckBox cbAll, cbLift, cbMike;
    private ImageView ivAssistSignin;
    private ImageView ivTerminalBoot;
    private ImageView ivTerminalShutdown;
    private ImageView ivTerminalRestart;
    private ImageView ivSoftwareRestart;
    private ImageView ivRoleSet;
    private ImageView ivDocument;
    private ImageView ivRising;
    private ImageView ivFalling;
    private DeviceControlAdapter deviceControlAdapter;
    private PopupWindow rolePop;
    private MemberRoleAdapter memberRoleAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_device_control;
    }

    @Override
    protected void initView(View inflate) {
        cbAll = (CheckBox) inflate.findViewById(R.id.cb_all);
        rvDevice = (RecyclerView) inflate.findViewById(R.id.rv_device);
        cbLift = (CheckBox) inflate.findViewById(R.id.cb_lift);
        cbMike = (CheckBox) inflate.findViewById(R.id.cb_mike);
        ivAssistSignin = (ImageView) inflate.findViewById(R.id.iv_assist_signin);
        ivTerminalBoot = (ImageView) inflate.findViewById(R.id.iv_terminal_boot);
        ivTerminalShutdown = (ImageView) inflate.findViewById(R.id.iv_terminal_shutdown);
        ivTerminalRestart = (ImageView) inflate.findViewById(R.id.iv_terminal_restart);
        ivSoftwareRestart = (ImageView) inflate.findViewById(R.id.iv_software_restart);
        ivRoleSet = (ImageView) inflate.findViewById(R.id.iv_role_set);
        ivDocument = (ImageView) inflate.findViewById(R.id.iv_document);
        ivRising = (ImageView) inflate.findViewById(R.id.iv_rising);
        ivFalling = (ImageView) inflate.findViewById(R.id.iv_falling);
        ivAssistSignin.setOnClickListener(this);
        ivTerminalBoot.setOnClickListener(this);
        ivTerminalShutdown.setOnClickListener(this);
        ivTerminalRestart.setOnClickListener(this);
        ivSoftwareRestart.setOnClickListener(this);
        ivRoleSet.setOnClickListener(this);
        ivDocument.setOnClickListener(this);
        ivRising.setOnClickListener(this);
        ivFalling.setOnClickListener(this);

        cbAll.setOnClickListener(this);
    }

    @Override
    protected DeviceControlPresenter initPresenter() {
        return new DeviceControlPresenter(this);
    }

    @Override
    protected void initial() {
        presenter.queryMember();
    }

    @Override
    protected void onShow() {
        presenter.queryMember();
    }

    @Override
    public void updateDeviceList() {
        if (deviceControlAdapter == null) {
            deviceControlAdapter = new DeviceControlAdapter(presenter.devControlBeans);
            rvDevice.setLayoutManager(new LinearLayoutManager(getContext()));
            rvDevice.setAdapter(deviceControlAdapter);
            deviceControlAdapter.setOnItemClickListener((adapter, view, position) -> {
                deviceControlAdapter.choose(presenter.devControlBeans.get(position).getDeviceInfo().getDevcieid());
                cbAll.setChecked(deviceControlAdapter.isCheckAll());
            });
        } else {
            deviceControlAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void updateMemberRoleList() {
        if (rolePop != null && rolePop.isShowing()) {
            memberRoleAdapter.notifyDataSetChanged();
        }
    }

    private void showMemberRole() {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_member_role, null);
        rolePop = PopupUtil.createCenter(inflate, GlobalValue.screen_width / 3 * 2, GlobalValue.screen_height / 3 * 2, rvDevice);
        RecyclerView rv_member_role = inflate.findViewById(R.id.rv_member_role);
        Spinner sp_role = inflate.findViewById(R.id.sp_role);
        memberRoleAdapter = new MemberRoleAdapter(R.layout.item_member_role, presenter.memberRoleBeans);
        rv_member_role.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_member_role.setAdapter(memberRoleAdapter);
        memberRoleAdapter.setOnItemClickListener((adapter, view, position) -> {
            MemberRoleBean item = presenter.memberRoleBeans.get(position);
            memberRoleAdapter.setSelected(item.getMember().getPersonid());
            int index;
            int role = item.getSeat() != null ? item.getSeat().getRole() : 0;
            switch (role) {
                case InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_normal_VALUE:
                    index = 1;
                    break;
                case InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_compere_VALUE:
                    index = 2;
                    break;
                case InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_secretary_VALUE:
                    index = 3;
                    break;
                case InterfaceMacro.Pb_MeetMemberRole.Pb_role_admin_VALUE:
                    index = 4;
                    break;
                default:
                    index = 0;
                    break;
            }
            sp_role.setSelection(index);
        });
        inflate.findViewById(R.id.btn_confirm).setOnClickListener(v -> {
            MemberRoleBean selected = memberRoleAdapter.getSelected();
            if (selected == null) {
                ToastUtils.showShort(R.string.please_choose_member);
                return;
            }
            if (selected.getSeat() == null) {
                ToastUtils.showShort(R.string.please_choose_bind_member);
                return;
            }
            int index = sp_role.getSelectedItemPosition();
            int newRole;
            switch (index) {
                case 1:
                    newRole = InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_normal_VALUE;
                    break;
                case 2:
                    newRole = InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_compere_VALUE;
                    break;
                case 3:
                    newRole = InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_secretary_VALUE;
                    break;
                case 4:
                    newRole = InterfaceMacro.Pb_MeetMemberRole.Pb_role_admin_VALUE;
                    break;
                default:
                    newRole = InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_nouser_VALUE;
                    break;
            }
            jni.modifyMeetRanking(selected.getMember().getPersonid(), newRole, selected.getSeat().getDevid());
        });
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> rolePop.dismiss());
    }

    @Override
    public void onClick(View v) {
        List<Integer> deviceIds = deviceControlAdapter.getSelectedIds();
        if (v.getId() != R.id.iv_role_set
                && v.getId() != R.id.cb_all) {
            if (deviceIds.isEmpty()) {
                ToastUtils.showShort(R.string.please_choose_device_first);
                return;
            }
        }
        switch (v.getId()) {
            case R.id.cb_all: {
                boolean checked = cbAll.isChecked();
                cbAll.setChecked(checked);
                deviceControlAdapter.setCheckAll(checked);
                break;
            }
            //辅助签到
            case R.id.iv_assist_signin: {
                jni.assistedSignIn(deviceIds);
                break;
            }
            //终端关机
            case R.id.iv_terminal_boot: {
                jni.executeTerminalControl(InterfaceMacro.Pb_DeviceControlFlag.Pb_DEVICECONTORL_SHUTDOWN_VALUE,
                        0, 0, deviceIds);
                break;
            }
            //终端开机
            case R.id.iv_terminal_shutdown: {
                break;
            }
            //终端重启
            case R.id.iv_terminal_restart: {
                jni.executeTerminalControl(InterfaceMacro.Pb_DeviceControlFlag.Pb_DEVICECONTORL_REBOOT_VALUE,
                        0, 0, deviceIds);
                break;
            }
            //软件重启
            case R.id.iv_software_restart: {
                jni.executeTerminalControl(InterfaceMacro.Pb_DeviceControlFlag.Pb_DEVICECONTORL_PROGRAMRESTART_VALUE,
                        0, 0, deviceIds);
                break;
            }
            //角色设定
            case R.id.iv_role_set: {
                presenter.queryMember();
                showMemberRole();
                break;
            }
            //外部文档打开
            case R.id.iv_document: {
                presenter.modifyDeviceFlag(deviceIds);
                break;
            }
            //上升
            case R.id.iv_rising: {
                int value = 0;
                if (cbLift.isChecked()) {
                    value = InterfaceMacro.Pb_LiftFlag.Pb_LIFT_FLAG_MACHICE_VALUE;
                }
                if (cbMike.isChecked()) {
                    value = value | InterfaceMacro.Pb_LiftFlag.Pb_LIFT_FLAG_MIC_VALUE;
                }
                if (value == 0) {
                    ToastUtils.showShort(R.string.please_choose_lift_or_mike);
                    return;
                }
                jni.executeTerminalControl(InterfaceMacro.Pb_DeviceControlFlag.Pb_DEVICECONTORL_LIFTUP_VALUE, value, 0, deviceIds);
                break;
            }
            //下降
            case R.id.iv_falling: {
                int value = 0;
                if (cbLift.isChecked()) {
                    value = InterfaceMacro.Pb_LiftFlag.Pb_LIFT_FLAG_MACHICE_VALUE;
                }
                if (cbMike.isChecked()) {
                    value = value | InterfaceMacro.Pb_LiftFlag.Pb_LIFT_FLAG_MIC_VALUE;
                }
                if (value == 0) {
                    ToastUtils.showShort(R.string.please_choose_lift_or_mike);
                    return;
                }
                jni.executeTerminalControl(InterfaceMacro.Pb_DeviceControlFlag.Pb_DEVICECONTORL_LIFTDOWN_VALUE, value, 0, deviceIds);
                break;
            }
        }
    }
}
