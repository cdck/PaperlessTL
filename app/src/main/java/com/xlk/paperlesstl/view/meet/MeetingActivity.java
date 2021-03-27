package com.xlk.paperlesstl.view.meet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.adapter.DevMemberAdapter;
import com.xlk.paperlesstl.adapter.MeetFunctionAdapter;
import com.xlk.paperlesstl.adapter.ProjectionAdapter;
import com.xlk.paperlesstl.model.Constant;
import com.xlk.paperlesstl.model.GlobalValue;
import com.xlk.paperlesstl.model.data.MeetFunctionBean;
import com.xlk.paperlesstl.util.PopupUtil;
import com.xlk.paperlesstl.view.admin.fragment.after.signin.AdminSignInFragment;
import com.xlk.paperlesstl.view.admin.fragment.mid.camera.AdminCameraControlFragment;
import com.xlk.paperlesstl.view.admin.fragment.mid.screen.ScreenFragment;
import com.xlk.paperlesstl.view.base.BaseActivity;
import com.xlk.paperlesstl.view.draw.DrawActivity;
import com.xlk.paperlesstl.view.fragment.agenda.AgendaFragment;
import com.xlk.paperlesstl.view.fragment.annotate.AnnotateFragment;
import com.xlk.paperlesstl.view.fragment.chat.MeetChatFragment;
import com.xlk.paperlesstl.view.fragment.devcontrol.DeviceControlFragment;
import com.xlk.paperlesstl.view.fragment.livevideo.LiveVideoFragment;
import com.xlk.paperlesstl.view.fragment.material.MaterialFragment;
import com.xlk.paperlesstl.view.fragment.bullet.BulletFragment;
import com.xlk.paperlesstl.view.fragment.share.ShareMaterialFragment;
import com.xlk.paperlesstl.view.fragment.signin.SignInFragment;
import com.xlk.paperlesstl.view.fragment.votecontrol.VoteControlFragment;
import com.xlk.paperlesstl.view.fragment.web.WebFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class MeetingActivity extends BaseActivity<MeetingPresenter> implements MeetingContract.View, View.OnClickListener {

    private android.widget.ImageView ivMin;
    private android.widget.ImageView ivKeyboard;
    private android.widget.ImageView ivServe;
    private android.widget.TextView tvTime;
    private android.widget.TextView tvWeek;
    private android.widget.TextView tvDay;
    private android.widget.TextView tvMemberName;
    private android.widget.TextView tvMeetName;
    private RecyclerView rvMenu;
    private android.widget.FrameLayout flMeet;
    private MeetFunctionAdapter functionAdapter;
    private PopupWindow pushFilePop;
    private DevMemberAdapter devMemberAdapter;
    private ProjectionAdapter projectionAdapter;
    private LinearLayout ll_bottom_view, ll_null;
    private int saveFunCode = -1;
    private AgendaFragment agendaFragment;
    private MaterialFragment materialFragment;
    private AnnotateFragment annotateFragment;
    private MeetChatFragment meetChatFragment;
    private ShareMaterialFragment shareMaterialFragment;
    private LiveVideoFragment liveVideoFragment;
    private WebFragment webFragment;
    private SignInFragment signInFragment;
    private AdminSignInFragment adminSignInFragment;
    private LinearLayout ll_other_menu;
    List<ImageView> otherMenus = new ArrayList<>();
    private ImageView iv_menu_back, iv_menu_device, iv_menu_video, iv_menu_vote, iv_menu_election, iv_menu_screen, iv_menu_bulletin;
    private DeviceControlFragment deviceControlFragment;
    private AdminCameraControlFragment adminCameraControlFragment;
    private VoteControlFragment voteControlFragment;
    private ScreenFragment screenFragment;
    private BulletFragment noticeFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_meeting;
    }

    @Override
    protected MeetingPresenter initPresenter() {
        return new MeetingPresenter(this, this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        initView();
        presenter.initial();
        presenter.initVideoRes();
        initial();
    }

    private void initial() {
        presenter.queryDeviceMeetInfo();
        presenter.queryMeetFunction();
        presenter.queryPermission();
        presenter.queryMember();
    }

    private void initView() {
        ll_bottom_view = (LinearLayout) findViewById(R.id.ll_bottom_view);
        ll_null = (LinearLayout) findViewById(R.id.ll_null);

        ivMin = (ImageView) findViewById(R.id.iv_min);
        ivKeyboard = (ImageView) findViewById(R.id.iv_keyboard);
        ivServe = (ImageView) findViewById(R.id.iv_serve);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvWeek = (TextView) findViewById(R.id.tv_week);
        tvDay = (TextView) findViewById(R.id.tv_day);
        tvMemberName = (TextView) findViewById(R.id.tv_member_name);
        tvMeetName = (TextView) findViewById(R.id.tv_meet_name);
        rvMenu = (RecyclerView) findViewById(R.id.rv_menu);
        flMeet = (FrameLayout) findViewById(R.id.fl_meet);
        ivMin.setOnClickListener(this);
        ivKeyboard.setOnClickListener(this);
        ivServe.setOnClickListener(this);

        ll_other_menu = findViewById(R.id.ll_other_menu);

        iv_menu_back = findViewById(R.id.iv_menu_back);
        iv_menu_device = findViewById(R.id.iv_menu_device);
        iv_menu_video = findViewById(R.id.iv_menu_video);
        iv_menu_vote = findViewById(R.id.iv_menu_vote);
        iv_menu_election = findViewById(R.id.iv_menu_election);
        iv_menu_screen = findViewById(R.id.iv_menu_screen);
        iv_menu_bulletin = findViewById(R.id.iv_menu_bulletin);
        otherMenus.add(iv_menu_back);
        otherMenus.add(iv_menu_device);
        otherMenus.add(iv_menu_video);
        otherMenus.add(iv_menu_vote);
        otherMenus.add(iv_menu_election);
        otherMenus.add(iv_menu_screen);
        otherMenus.add(iv_menu_bulletin);

        iv_menu_back.setOnClickListener(this);
        iv_menu_device.setOnClickListener(this);
        iv_menu_video.setOnClickListener(this);
        iv_menu_vote.setOnClickListener(this);
        iv_menu_election.setOnClickListener(this);
        iv_menu_screen.setOnClickListener(this);
        iv_menu_bulletin.setOnClickListener(this);
    }

    @Override
    public void updateTime(String time, String day, String week) {
        tvTime.setText(time);
        tvDay.setText(day);
        tvWeek.setText(week);
    }

    @Override
    public void updateMeetName(String meetingName) {
        tvMeetName.setText(meetingName);
    }

    @Override
    public void hasOtherFunction(boolean has) {
        LogUtils.d(TAG, "hasOtherFunction --> 是否拥有权限操作其它功能：" + has);
        if (!has) {
//            if (funPop != null && funPop.isShowing()) {
//                funPop.dismiss();
//            }
//            if (saveFunCode > Constant.FUN_CODE) {
//                //之前有权限时在其它界面，权限消失后切换到默认第一个页面去
//                setDefaultFun(firstFunCode);
//            }
        }
    }

    @Override
    public void updateMemberName(String memberName) {
        tvMemberName.setText(memberName);
    }

    @Override
    public void updateMemberRole(String string) {
        tvMemberName.setText(string);
    }

    @Override
    public void updateMemberAndProjectorAdapter() {
        if (pushFilePop != null && pushFilePop.isShowing()) {
            devMemberAdapter.notifyDataSetChanged();
            projectionAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showPushPop(int mediaId) {
        View inflate = LayoutInflater.from(this).inflate(R.layout.pop_push_file_layout, null, false);
        pushFilePop = PopupUtil.createHalfPop(inflate, flMeet);
        CheckBox cb_member = inflate.findViewById(R.id.cb_member);
        CheckBox cb_projector = inflate.findViewById(R.id.cb_projector);
        RecyclerView rv_member = inflate.findViewById(R.id.rv_member);
        RecyclerView rv_projector = inflate.findViewById(R.id.rv_projector);
        projectionAdapter = new ProjectionAdapter(presenter.onlineProjectors);
        rv_projector.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        rv_projector.setAdapter(projectionAdapter);
        projectionAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                projectionAdapter.choose(presenter.onlineProjectors.get(position).getDevcieid());
                cb_projector.setChecked(projectionAdapter.isChooseAll());
            }
        });
        cb_projector.setOnClickListener(v -> {
            boolean checked = cb_projector.isChecked();
            cb_projector.setChecked(checked);
            projectionAdapter.setChooseAll(checked);
        });

        devMemberAdapter = new DevMemberAdapter(presenter.onlineMembers);
        rv_member.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        rv_member.setAdapter(devMemberAdapter);
        devMemberAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                devMemberAdapter.choose(presenter.onlineMembers.get(position).getDeviceDetailInfo().getDevcieid());
                cb_member.setChecked(devMemberAdapter.isChooseAll());
            }
        });
        cb_member.setOnClickListener(v -> {
            boolean checked = cb_member.isChecked();
            cb_member.setChecked(checked);
            devMemberAdapter.setChooseAll(checked);
        });
        inflate.findViewById(R.id.btn_push).setOnClickListener(v -> {
            List<Integer> selectedIds = devMemberAdapter.getDeviceIds();
            selectedIds.addAll(projectionAdapter.getDeviceIds());
            if (selectedIds.isEmpty()) {
                ToastUtils.showShort(R.string.please_choose_push_target);
                return;
            }
            pushFilePop.dismiss();
            jni.mediaPlayOperate(mediaId, selectedIds, 0, Constant.RESOURCE_ID_0,
                    0, InterfaceMacro.Pb_MeetPlayFlag.Pb_MEDIA_PLAYFLAG_ZERO.getNumber());
        });

        inflate.findViewById(R.id.btn_stop_push).setOnClickListener(v -> {
            List<Integer> selectedIds = devMemberAdapter.getDeviceIds();
            selectedIds.addAll(projectionAdapter.getDeviceIds());
            if (selectedIds.isEmpty()) {
                ToastUtils.showShort(R.string.please_choose_stop_target);
                return;
            }
            pushFilePop.dismiss();
            List<Integer> temps = new ArrayList<>();
            temps.add(0);
            jni.stopResourceOperate(temps, selectedIds);
        });
        //默认全选
        cb_member.performClick();
        cb_projector.performClick();
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> {
            pushFilePop.dismiss();
        });
    }

    @Override
    public void updateMeetFunction() {
        if (functionAdapter == null) {
            functionAdapter = new MeetFunctionAdapter(presenter.meetFunctions);
        }
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(presenter.meetFunctions.size() <= 5 ? 1 : 2, StaggeredGridLayoutManager.VERTICAL);
        rvMenu.setLayoutManager(layoutManager);
        rvMenu.setAdapter(functionAdapter);
        functionAdapter.setOnItemClickListener((adapter, view, position) -> {
            int funcode = presenter.meetFunctions.get(position).getFuncode();
            functionAdapter.choose(funcode);
            LogUtils.d(TAG, "选中的功能ID=" + funcode);
            showFragment(funcode);
        });
        defaultMenu();
    }

    private void defaultMenu() {
        if (rvMenu.getVisibility() == View.GONE) {
            return;
        }
        if (presenter.meetFunctions.isEmpty()) {
            showFragment(-1);
            functionAdapter.choose(-1);
            return;
        }
        //判断点击的功能还有没有
        boolean has = false;
        for (int i = 0; i < presenter.meetFunctions.size(); i++) {
            MeetFunctionBean bean = presenter.meetFunctions.get(i);
            int funcode = bean.getFuncode();
            if (funcode == saveFunCode) {
                has = true;
                break;
            }
        }
        LogUtils.e(TAG, "defaultMenu has=" + has + ",saveFunCode=" + saveFunCode);
        if (!has || saveFunCode == -1) {
            int funcode = presenter.meetFunctions.get(0).getFuncode();
            if (funcode != InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_WHITEBOARD_VALUE
                    && funcode != Constant.FUN_CODE) {
                showFragment(funcode);
                functionAdapter.choose(funcode);
            } else {
                if (presenter.meetFunctions.size() > 1) {
                    showFragment(presenter.meetFunctions.get(1).getFuncode());
                    functionAdapter.choose(presenter.meetFunctions.get(1).getFuncode());
                } else {
                    showFragment(-1);
                    functionAdapter.choose(-1);
                }
            }
        } else {
            LogUtils.e(TAG, "点击之前的功能");
            showFragment(saveFunCode);
            functionAdapter.choose(saveFunCode);
        }
    }

    @Override
    public void showSignInDetailsFragment() {
        LogUtils.e(TAG, "showSignInDetailsFragment");
        showFragment(Constant.FUN_CODE_SIGN_IN_DETAILS);
    }

    @Override
    public void showSignInFragment() {
        LogUtils.e(TAG, "showSignInFragment");
        showFragment(InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_SIGNINRESULT_VALUE);
    }

    private void showFragment(int funCode) {
        LogUtils.i(TAG, "showFragment funCode=" + funCode);
        //其它功能
        if (funCode == Constant.FUN_CODE) {
            if (GlobalValue.localRole == InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_compere.getNumber()
                    || GlobalValue.localRole == InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_secretary.getNumber()
                    || GlobalValue.localRole == InterfaceMacro.Pb_MeetMemberRole.Pb_role_admin.getNumber()) {
                showOtherMenu();
            } else {
                ToastUtils.showShort(R.string.no_permission);
            }
            //不能操作 FragmentTransaction
            return;
        }
        if (funCode < Constant.FUN_CODE) {
            if (funCode != InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_WHITEBOARD_VALUE) {
                saveFunCode = funCode;
            }
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        hideFragment(ft);
        switch (funCode) {
            //会议议程
            case InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_AGENDA_BULLETIN_VALUE: {
                if (agendaFragment == null) {
                    agendaFragment = new AgendaFragment();
                    ft.add(R.id.fl_meet, agendaFragment);
                }
                ft.show(agendaFragment);
                break;
            }
            //会议资料
            case InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_MATERIAL_VALUE: {
                if (materialFragment == null) {
                    materialFragment = new MaterialFragment();
                    ft.add(R.id.fl_meet, materialFragment);
                }
                ft.show(materialFragment);
                break;
            }
            //共享文件
            case InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_SHAREDFILE_VALUE: {
                if (shareMaterialFragment == null) {
                    shareMaterialFragment = new ShareMaterialFragment();
                    ft.add(R.id.fl_meet, shareMaterialFragment);
                }
                ft.show(shareMaterialFragment);
                break;
            }
            //批注文件
            case InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_POSTIL_VALUE: {
                if (annotateFragment == null) {
                    annotateFragment = new AnnotateFragment();
                    ft.add(R.id.fl_meet, annotateFragment);
                }
                ft.show(annotateFragment);
                break;
            }
            //会议交流
            case InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_MESSAGE_VALUE: {
                if (meetChatFragment == null) {
                    meetChatFragment = new MeetChatFragment();
                    ft.add(R.id.fl_meet, meetChatFragment);
                }
                ft.show(meetChatFragment);
                break;
            }
            //视屏直播
            case InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_VIDEOSTREAM_VALUE: {
                if (liveVideoFragment == null) {
                    liveVideoFragment = new LiveVideoFragment();
                    ft.add(R.id.fl_meet, liveVideoFragment);
                }
                ft.show(liveVideoFragment);
                break;
            }
            //电子白板
            case InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_WHITEBOARD_VALUE: {
                startActivity(new Intent(this, DrawActivity.class));
                break;
            }
            //网页浏览
            case InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_WEBBROWSER_VALUE: {
                if (webFragment == null) {
                    webFragment = new WebFragment();
                    ft.add(R.id.fl_meet, webFragment);
                }
                ft.show(webFragment);
                break;
            }
            //签到信息
            case InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_SIGNINRESULT_VALUE: {
                if (signInFragment == null) {
                    signInFragment = new SignInFragment();
                    ft.add(R.id.fl_meet, signInFragment);
                }
                ft.show(signInFragment);
                break;
            }
            //签到详情
            case Constant.FUN_CODE_SIGN_IN_DETAILS: {
                Bundle bundle = new Bundle();
                bundle.putBoolean("isFromMeet", true);
                if (adminSignInFragment == null) {
                    adminSignInFragment = new AdminSignInFragment();
                    ft.add(R.id.fl_meet, adminSignInFragment);
                }
                adminSignInFragment.setArguments(bundle);
                ft.show(adminSignInFragment);
                break;
            }
            //设备控制
            case Constant.FUN_CODE_DEVICE: {
                if (deviceControlFragment == null) {
                    deviceControlFragment = new DeviceControlFragment();
                    ft.add(R.id.fl_meet, deviceControlFragment);
                }
                ft.show(deviceControlFragment);
                break;
            }
            //视频管理
            case Constant.FUN_CODE_VIDEO: {
                if (adminCameraControlFragment == null) {
                    adminCameraControlFragment = new AdminCameraControlFragment();
                    ft.add(R.id.fl_meet, adminCameraControlFragment);
                }
                ft.show(adminCameraControlFragment);
                break;
            }
            //投票管理
            case Constant.FUN_CODE_VOTE: {
                Bundle bundle = new Bundle();
                bundle.putBoolean("isVote", true);
                if (voteControlFragment == null) {
                    voteControlFragment = new VoteControlFragment();
                    ft.add(R.id.fl_meet, voteControlFragment);
                }
                voteControlFragment.setArguments(bundle);
                ft.show(voteControlFragment);
                break;
            }
            //选举管理
            case Constant.FUN_CODE_ELECTION: {
                Bundle bundle = new Bundle();
                bundle.putBoolean("isVote", false);
                if (voteControlFragment == null) {
                    voteControlFragment = new VoteControlFragment();
                    ft.add(R.id.fl_meet, voteControlFragment);
                }
                voteControlFragment.setArguments(bundle);
                ft.show(voteControlFragment);
                break;
            }
            //屏幕管理
            case Constant.FUN_CODE_SCREEN: {
                if (screenFragment == null) {
                    screenFragment = new ScreenFragment();
                    ft.add(R.id.fl_meet, screenFragment);
                }
                ft.show(screenFragment);
                break;
            }
            //会议公告
            case Constant.FUN_CODE_BULLETIN: {
                if (noticeFragment == null) {
                    noticeFragment = new BulletFragment();
                    ft.add(R.id.fl_meet, noticeFragment);
                }
                ft.show(noticeFragment);
                break;
            }
        }
        try {
            ft.commitAllowingStateLoss();//允许状态丢失，其他完全一样
            LogUtils.i(TAG, "showFragment 成功 funCode=" + funCode);
//        ft.commit();//出现异常：Can not perform this action after onSaveInstanceState
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideFragment(FragmentTransaction ft) {
        if (agendaFragment != null) ft.hide(agendaFragment);
        if (materialFragment != null) ft.hide(materialFragment);
        if (shareMaterialFragment != null) ft.hide(shareMaterialFragment);
        if (annotateFragment != null) ft.hide(annotateFragment);
        if (meetChatFragment != null) ft.hide(meetChatFragment);
        if (liveVideoFragment != null) ft.hide(liveVideoFragment);
        if (webFragment != null) ft.hide(webFragment);
        if (signInFragment != null) ft.hide(signInFragment);
        if (adminSignInFragment != null) ft.hide(adminSignInFragment);
        if (deviceControlFragment != null) ft.hide(deviceControlFragment);
        if (adminCameraControlFragment != null) ft.hide(adminCameraControlFragment);
        if (voteControlFragment != null) ft.hide(voteControlFragment);
        if (screenFragment != null) ft.hide(screenFragment);
        if (noticeFragment != null) ft.hide(noticeFragment);
    }

    private void clickOtherMenu(int index) {
        LogUtils.e(TAG, "clickOtherMenu index=" + index);
        for (int i = 0; i < otherMenus.size(); i++) {
            boolean selected = index == i;
            otherMenus.get(i).setSelected(selected);
            if (selected) {
                if (index == 0) {
                    hideOtherMenu();
                } else {
                    showFragment(Constant.FUN_CODE + index);
                }
            }
        }

    }

    /**
     * 切换到参会人功能，恢复之前点击的功能项
     */
    private void hideOtherMenu() {
        LogUtils.e(TAG, "hideOtherMenu");
        ll_other_menu.setVisibility(View.GONE);
        rvMenu.setVisibility(View.VISIBLE);
        defaultMenu();
    }

    /**
     * 切换到管理员功能，并显示第一个功能项
     */
    private void showOtherMenu() {
        LogUtils.e(TAG, "showOtherMenu");
        rvMenu.setVisibility(View.GONE);
        ll_other_menu.setVisibility(View.VISIBLE);
        clickOtherMenu(1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_min: {
                LogUtils.e(TAG, "点击最小化图标");
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            }
            case R.id.iv_keyboard: {
                break;
            }
            case R.id.iv_serve: {
                showServePop();
                break;
            }
            case R.id.iv_menu_back: {
                clickOtherMenu(0);
                break;
            }
            case R.id.iv_menu_device: {
                clickOtherMenu(1);
                break;
            }
            case R.id.iv_menu_video: {
                clickOtherMenu(2);
                break;
            }
            case R.id.iv_menu_vote: {
                clickOtherMenu(3);
                break;
            }
            case R.id.iv_menu_election: {
                clickOtherMenu(4);
                break;
            }
            case R.id.iv_menu_screen: {
                clickOtherMenu(5);
                break;
            }
            case R.id.iv_menu_bulletin: {
                clickOtherMenu(6);
                break;
            }
        }
    }

    private void showServePop() {
        ll_bottom_view.setVisibility(View.GONE);
        ll_null.setVisibility(View.VISIBLE);
        View inflate = LayoutInflater.from(this).inflate(R.layout.pop_serve_layout, null, false);
        PopupWindow halfPop = PopupUtil.createHalfPop(inflate, ivMin);
        EditText edt_content = inflate.findViewById(R.id.edt_content);
        inflate.findViewById(R.id.iv_pager).setOnClickListener(v -> edt_content.setText(getString(R.string.server_pager)));
        inflate.findViewById(R.id.iv_pen).setOnClickListener(v -> edt_content.setText(getString(R.string.server_pen)));
        inflate.findViewById(R.id.iv_tea).setOnClickListener(v -> edt_content.setText(getString(R.string.server_tea)));
        inflate.findViewById(R.id.iv_waiter).setOnClickListener(v -> edt_content.setText(getString(R.string.server_waiter)));
        inflate.findViewById(R.id.iv_calculator).setOnClickListener(v -> edt_content.setText(getString(R.string.server_calculator)));
        inflate.findViewById(R.id.iv_clean).setOnClickListener(v -> edt_content.setText(getString(R.string.server_clean)));
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> halfPop.dismiss());
        inflate.findViewById(R.id.btn_send).setOnClickListener(v -> {
            String msg = edt_content.getText().toString().trim();
            if (!msg.isEmpty()) {
                List<Integer> arr = new ArrayList<>();
                arr.add(0);//会议服务类请求则为 0
                jni.sendChatMessage(msg, InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Other_VALUE, arr);
                halfPop.dismiss();
            } else {
                ToastUtils.showShort(R.string.please_enter_content_first);
            }
        });

        halfPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ll_null.setVisibility(View.GONE);
                ll_bottom_view.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initial();
    }

    @Override
    protected void onDestroy() {
        presenter.releaseVideoRes();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        //空实现屏蔽返回键
    }
}