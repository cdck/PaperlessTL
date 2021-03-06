package com.xlk.paperlesstl.view.videochat;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.mogujie.tt.protobuf.InterfaceStop;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.jni.JniHelper;
import com.xlk.paperlesstl.model.Constant;
import com.xlk.paperlesstl.model.GlobalValue;
import com.xlk.paperlesstl.model.data.EventMessage;
import com.xlk.paperlesstl.model.data.EventType;
import com.xlk.paperlesstl.ui.video.VideoChatView;
import com.xlk.paperlesstl.view.admin.adapter.MeetChatMemberAdapter;
import com.xlk.paperlesstl.view.admin.bean.DevMember;
import com.xlk.paperlesstl.view.base.BaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.xlk.paperlesstl.model.Constant.RESOURCE_ID_10;
import static com.xlk.paperlesstl.model.Constant.RESOURCE_ID_11;

public class ChatVideoActivity extends BaseActivity<ChatVideoPresenter> implements View.OnClickListener, ChatVideoContract.View {

    private final String TAG = "ChatVideoActivity-->";
    private CheckBox pop_video_chat_all;
    private RecyclerView pop_video_chat_rv;
    private RadioGroup pop_video_chat_radio;
    private RadioButton pop_video_chat_paging;
    private RadioButton pop_video_chat_intercom;
    private ImageView pop_video_chat_close;
    private CheckBox video_chat_ask_cb;
    private Button pop_video_chat_launch;
    private Button pop_video_chat_stop;
    private VideoChatView video_chat_view;
    private JniHelper jni = JniHelper.getInstance();
    private List<Integer> ids = new ArrayList<>();
    private List<InterfaceMember.pbui_Item_MemberDetailInfo> memberInfos = new ArrayList<>();
    private List<DevMember> onlineMembers = new ArrayList<>();
    private MeetChatMemberAdapter memberAdapter;
    private int work_state = 0;//=0??????,=1????????????=2?????????
    public static boolean isChatingOpened = false;//??????????????????????????????
    private int mInviteflag, mOperdeviceid;
    private LinearLayoutManager layoutManager;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat_video;
    }

    @Override
    protected ChatVideoPresenter initPresenter() {
        return new ChatVideoPresenter(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        initView();
        isChatingOpened = true;
        mInviteflag = getIntent().getIntExtra(Constant.EXTRA_INVITE_FLAG, -1);
        mOperdeviceid = getIntent().getIntExtra(Constant.EXTRA_OPERATING_DEVICE_ID, -1);
        LogUtils.d(TAG, "onCreate --> ???????????????ID???????????????= " + mOperdeviceid);
        initial();
        EventBus.getDefault().register(this);
        queryAttendPeople();
    }

    private void queryAttendPeople() {
        InterfaceMember.pbui_Type_MemberDetailInfo attendPeople = jni.queryMember();
        if (attendPeople == null) {
            return;
        }
        memberInfos.clear();
        memberInfos.addAll(attendPeople.getItemList());
        queryDeviceInfo();
    }

    private void queryDeviceInfo() {
        InterfaceDevice.pbui_Type_DeviceDetailInfo deviceDetailInfo = jni.queryDeviceInfo();
        if (deviceDetailInfo == null) {
            return;
        }
        List<InterfaceDevice.pbui_Item_DeviceDetailInfo> deviceDetailInfos = deviceDetailInfo.getPdevList();
        onlineMembers.clear();
        for (int i = 0; i < deviceDetailInfos.size(); i++) {
            InterfaceDevice.pbui_Item_DeviceDetailInfo detailInfo = deviceDetailInfos.get(i);
            int devcieid = detailInfo.getDevcieid();
            int memberid = detailInfo.getMemberid();
            int facestate = detailInfo.getFacestate();
            int netstate = detailInfo.getNetstate();
            if (facestate == 1 && netstate == 1 && devcieid != GlobalValue.localDeviceId) {
                for (int j = 0; j < memberInfos.size(); j++) {
                    InterfaceMember.pbui_Item_MemberDetailInfo memberDetailInfo = memberInfos.get(j);
                    int personid = memberDetailInfo.getPersonid();
                    if (personid == memberid) {
                        onlineMembers.add(new DevMember(detailInfo, memberDetailInfo));
                    }
                }
            }
        }
        updateRv();
    }

    private void setRvLayoutManager(boolean canScroll) {
        layoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return canScroll;
            }
        };
        pop_video_chat_rv.setLayoutManager(layoutManager);
    }

    private void updateRv() {
        if (memberAdapter == null) {
            memberAdapter = new MeetChatMemberAdapter(R.layout.item_chat_member, onlineMembers);
            if (layoutManager == null) {
                setRvLayoutManager(true);
            }
            pop_video_chat_rv.setLayoutManager(layoutManager);
            pop_video_chat_rv.setAdapter(memberAdapter);
            memberAdapter.setOnItemClickListener((adapter, view, position) -> {
                memberAdapter.setCheck(onlineMembers.get(position).getMemberDetailInfo().getPersonid());
                pop_video_chat_all.setChecked(memberAdapter.isCheckAll());
            });
            pop_video_chat_all.setOnClickListener(v -> {
                boolean checked = pop_video_chat_all.isChecked();
                pop_video_chat_all.setChecked(checked);
                memberAdapter.setCheckAll(checked);
            });
        } else {
            memberAdapter.notifyDataSetChanged();
            memberAdapter.notifyCheck();
            pop_video_chat_all.setChecked(memberAdapter.isCheckAll());
        }
    }

    private void initial() {
        pop_video_chat_paging.setOnClickListener(v -> {
            LogUtils.i(TAG, "initial -->" + "????????????");
            video_chat_view.createDefaultView(1);
        });
        pop_video_chat_intercom.setOnClickListener(v -> {
            LogUtils.i(TAG, "initial -->" + "????????????");
            video_chat_view.createDefaultView(2);
        });
        if (mOperdeviceid == -1) {
            work_state = 0;
            setEnable();
            video_chat_view.createDefaultView(1);
            pop_video_chat_paging.setChecked(true);
        } else {
            if ((mInviteflag & InterfaceDevice.Pb_DeviceInviteFlag.Pb_DEVICE_INVITECHAT_FLAG_SIMPLEX_VALUE) ==
                    InterfaceDevice.Pb_DeviceInviteFlag.Pb_DEVICE_INVITECHAT_FLAG_SIMPLEX_VALUE) {
                createPaging();
            } else {
                createIntercom();
            }
        }
    }

    private void createIntercom() {
        work_state = 2;
        ids.clear();
        ids.add(10);
        ids.add(11);
        video_chat_view.createPlayView(ids);
        pop_video_chat_intercom.setChecked(true);
        setEnable();
    }

    private void createPaging() {
        work_state = 1;
        ids.clear();
        ids.add(10);
        video_chat_view.createPlayView(ids);
        pop_video_chat_paging.setChecked(true);
        setEnable();
    }

    private void setEnable() {
        boolean enabled = work_state == 0;
        video_chat_ask_cb.setEnabled(enabled);
        pop_video_chat_paging.setEnabled(enabled);
        pop_video_chat_intercom.setEnabled(enabled);
        pop_video_chat_all.setEnabled(enabled);
        setRvLayoutManager(enabled);
        pop_video_chat_launch.setEnabled(enabled);
        pop_video_chat_stop.setEnabled(!enabled);
//        pop_video_chat_launch.setBackground(getResources().getDrawable(R.drawable.shape_btn_status_bg));
//        pop_video_chat_stop.setBackground(getResources().getDrawable(R.drawable.shape_btn_status_bg));
//        if (enabled) {
//            pop_video_chat_launch.setBackground(getResources().getDrawable(R.drawable.shape_btn_status_bg));
//        } else {
//            pop_video_chat_launch.setBackground(getResources().getDrawable(R.drawable.shape_btn_enable_flase));
//        }
//        if (enabled) {
//            pop_video_chat_stop.setBackground(getResources().getDrawable(R.drawable.shape_btn_enable_flase));
//        } else {
//            pop_video_chat_stop.setBackground(getResources().getDrawable(R.drawable.shape_btn_status_bg));
//        }
    }

    private void initView() {
        pop_video_chat_all = findViewById(R.id.pop_video_chat_all);
        pop_video_chat_rv = findViewById(R.id.pop_video_chat_rv);
        pop_video_chat_radio = findViewById(R.id.pop_video_chat_radio);
        pop_video_chat_paging = findViewById(R.id.pop_video_chat_paging);
        pop_video_chat_intercom = findViewById(R.id.pop_video_chat_intercom);
        pop_video_chat_close = findViewById(R.id.pop_video_chat_close);
        video_chat_ask_cb = findViewById(R.id.video_chat_ask_cb);
        pop_video_chat_launch = findViewById(R.id.pop_video_chat_launch);
        pop_video_chat_stop = findViewById(R.id.pop_video_chat_stop);
        video_chat_view = findViewById(R.id.video_chat_view);

        pop_video_chat_close.setOnClickListener(this);
        pop_video_chat_launch.setOnClickListener(this);
        pop_video_chat_stop.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        video_chat_view.clearAll();
        stopAll();
        isChatingOpened = false;
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        stopAll();
////        startActivity(new Intent(this, MeetingActivity.class));
        finish();
//        super.onBackPressed();
    }

    private void stopAll() {
        LogUtils.d(TAG, "stopAll -->" + "?????????????????????");
        EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_COLLECT_CAMERA_STOP).build());
        List<Integer> resids = new ArrayList<>();
        resids.add(RESOURCE_ID_10);
        resids.add(RESOURCE_ID_11);
        List<Integer> devids = new ArrayList<>();
        devids.add(GlobalValue.localDeviceId);
        jni.stopResourceOperate(resids, devids);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void BusEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            case EventType.BUS_VIDEO_DECODE://?????????????????? DECODE
                Object[] objs = msg.getObjects();
                int obj = (int) objs[1];
                LogUtils.v(TAG, "BusEvent ???????????? --> resid = " + obj);
                video_chat_view.setVideoDecode(objs);
                break;
            case EventType.BUS_YUV_DISPLAY://?????????????????? YUV
                Object[] objs1 = msg.getObjects();
                int o3 = (int) objs1[0];
                LogUtils.v(TAG, "BusEvent ???????????? --> resid = " + o3);
                video_chat_view.setYuv(objs1);
                break;
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEMEETSTATUS_VALUE://????????????????????????
                int o = (int) msg.getObjects()[1];
                if (o > 0) {
                    queryAttendPeople();
                }
                break;
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER_VALUE://?????????????????????
                queryAttendPeople();
                break;
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEINFO_VALUE://???????????????????????????
                int o1 = (int) msg.getObjects()[1];
                if (msg.getMethod() == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_NOTIFY_VALUE
                        && o1 > 0) {
                    queryAttendPeople();
                }
                break;
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEOPER_VALUE://????????????????????????
                if (msg.getMethod() == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_EXITCHAT_VALUE) {
                    stopDeviceIntercomInform(msg);
                } else if (msg.getMethod() == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_RESPONSEINVITE_VALUE) {
                    replyDeviceIntercomInform(msg);
                }
                break;
            case EventType.BUS_CHAT_STATE://?????????????????????????????????
                Object[] objs2 = msg.getObjects();
                int inviteflag = (int) objs2[0];
                int operdeviceid = (int) objs2[1];
                LogUtils.i(TAG, "BusEvent -->" + "????????????????????????????????? inviteflag= " + inviteflag + ", operdeviceid= " + operdeviceid);
                mOperdeviceid = operdeviceid;
                if ((inviteflag & InterfaceDevice.Pb_DeviceInviteFlag.Pb_DEVICE_INVITECHAT_FLAG_SIMPLEX_VALUE)
                        == InterfaceDevice.Pb_DeviceInviteFlag.Pb_DEVICE_INVITECHAT_FLAG_SIMPLEX_VALUE) {
                    LogUtils.i(TAG, "BusEvent -->" + "?????????????????????");
                    createPaging();
                } else {
                    LogUtils.i(TAG, "BusEvent -->" + "?????????????????????");
                    createIntercom();
                }
                break;
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_STOPPLAY_VALUE://??????????????????
                byte[] o2 = (byte[]) msg.getObjects()[0];
                if (msg.getMethod() == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_CLOSE_VALUE) {
                    //??????????????????
                    InterfaceStop.pbui_Type_MeetStopResWork stopResWork = InterfaceStop.pbui_Type_MeetStopResWork.parseFrom(o2);
                    List<Integer> resList = stopResWork.getResList();
                    for (int resid : resList) {
                        LogUtils.i(TAG, "BusEvent -->" + "?????????????????? resid: " + resid);
                        if ((resid == RESOURCE_ID_10 || resid == RESOURCE_ID_11) && mOperdeviceid == GlobalValue.localDeviceId) {
                            LogUtils.i(TAG, "BusEvent -->" + "????????????????????????????????????????????????????????????????????????");
                            if (work_state != 0) {
                                LogUtils.i(TAG, "BusEvent -->" + "??????????????????");
                                jni.stopDeviceIntercom(mOperdeviceid);
                            }
                        }
                    }
                } else if (msg.getMethod() == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_NOTIFY_VALUE) {
                    //??????????????????
                    InterfaceStop.pbui_Type_MeetStopPlay stopPlay = InterfaceStop.pbui_Type_MeetStopPlay.parseFrom(o2);
                    int resid = stopPlay.getRes();
                    int createdeviceid = stopPlay.getCreatedeviceid();
                    LogUtils.i(TAG, "BusEvent -->" + "?????????????????? resid= " + resid + ", createdeviceid= " + createdeviceid);
                    if ((resid == RESOURCE_ID_10 || resid == RESOURCE_ID_11) && mOperdeviceid == GlobalValue.localDeviceId) {
                        LogUtils.i(TAG, "BusEvent -->" + "????????????????????????????????????????????????????????????????????????");
                        if (work_state != 0) {
                            LogUtils.i(TAG, "BusEvent -->" + "??????????????????");
                            jni.stopDeviceIntercom(mOperdeviceid);
                        }
                    }
                }
                break;
        }
    }

    //?????????????????????????????????
    private void replyDeviceIntercomInform(EventMessage msg) throws InvalidProtocolBufferException {
        byte[] bytes = (byte[]) msg.getObjects()[0];
        InterfaceDevice.pbui_Type_DeviceChat info = InterfaceDevice.pbui_Type_DeviceChat.parseFrom(bytes);
        int inviteflag = info.getInviteflag();
        int operdeviceid = info.getOperdeviceid();
        LogUtils.i(TAG, "????????????????????????????????? inviteflag = " + inviteflag + ", operdeviceid= " + operdeviceid);
        if ((inviteflag & InterfaceDevice.Pb_DeviceInviteFlag.Pb_DEVICE_INVITECHAT_FLAG_DEAL_VALUE) ==
                InterfaceDevice.Pb_DeviceInviteFlag.Pb_DEVICE_INVITECHAT_FLAG_DEAL_VALUE) {
            if ((inviteflag & InterfaceDevice.Pb_DeviceInviteFlag.Pb_DEVICE_INVITECHAT_FLAG_SIMPLEX_VALUE) ==
                    InterfaceDevice.Pb_DeviceInviteFlag.Pb_DEVICE_INVITECHAT_FLAG_SIMPLEX_VALUE) {
                LogUtils.i(TAG, "????????????????????????????????? -->" + "??????????????????");
                ToastUtils.showShort(getString(R.string.agree_device_paging, getMemberName()));
                if (work_state != 1) {
                    createPaging();
                }
            } else {
                LogUtils.i(TAG, "????????????????????????????????? -->" + "??????????????????");
                ToastUtils.showShort(getString(R.string.agree_device_intercom, getMemberName()));
                createIntercom();
            }
        } else {
            work_state = 0;
            if ((inviteflag & InterfaceDevice.Pb_DeviceInviteFlag.Pb_DEVICE_INVITECHAT_FLAG_SIMPLEX_VALUE) == InterfaceDevice.Pb_DeviceInviteFlag.Pb_DEVICE_INVITECHAT_FLAG_SIMPLEX_VALUE) {
                LogUtils.i(TAG, "????????????????????????????????? -->" + "??????????????????");
                ToastUtils.showShort(getString(R.string.reject_device_paging, getMemberName()));
                video_chat_view.createDefaultView(1);
            } else {
                LogUtils.i(TAG, "????????????????????????????????? -->" + "??????????????????");
                ToastUtils.showShort(getString(R.string.reject_device_intercom, getMemberName()));
                video_chat_view.createDefaultView(2);
            }
            setEnable();
        }
    }

    private String getMemberName() {
        for (int i = 0; i < onlineMembers.size(); i++) {
            DevMember devMember = onlineMembers.get(i);
            if (devMember.getDeviceDetailInfo().getDevcieid() == mOperdeviceid) {
                return devMember.getMemberDetailInfo().getName().toStringUtf8();
            }
        }
        return "";
    }

    //??????????????????????????????
    private void stopDeviceIntercomInform(EventMessage msg) throws InvalidProtocolBufferException {
        byte[] bytes = (byte[]) msg.getObjects()[0];
        InterfaceDevice.pbui_Type_ExitDeviceChat info = InterfaceDevice.pbui_Type_ExitDeviceChat.parseFrom(bytes);
        int exitdeviceid = info.getExitdeviceid();
        int operdeviceid = info.getOperdeviceid();
        LogUtils.i(TAG, "?????????????????????????????? -->" + " exitdeviceid= " + exitdeviceid + ", operdeviceid= " + operdeviceid);
        if (work_state == 1) {//?????????
            if (exitdeviceid == operdeviceid || exitdeviceid == GlobalValue.localDeviceId) {//??????????????????,???????????????
                LogUtils.i(TAG, "?????????????????????????????? -->" + "????????????????????????????????????");
                stopAll();
                video_chat_view.createDefaultView(1);
                work_state = 0;
                setEnable();
            }
        } else if (work_state == 2) {//?????????
            LogUtils.i(TAG, "?????????????????????????????? -->" + "?????????????????????");
            stopAll();
            video_chat_view.createDefaultView(2);
            work_state = 0;
            setEnable();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pop_video_chat_launch: {
                if (memberAdapter != null && !memberAdapter.getChooseDevid().isEmpty()) {
                    List<Integer> chooseDevids = memberAdapter.getChooseDevid();
                    if (pop_video_chat_paging.isChecked()) {
                        //????????????
                        int flag;
                        if (video_chat_ask_cb.isChecked()) {
                            flag = InterfaceDevice.Pb_DeviceInviteFlag.Pb_DEVICE_INVITECHAT_FLAG_SIMPLEX_VALUE |//??????
                                    InterfaceDevice.Pb_DeviceInviteFlag.Pb_DEVICE_INVITECHAT_FLAG_VIDEO_VALUE |//??????
                                    InterfaceDevice.Pb_DeviceInviteFlag.Pb_DEVICE_INVITECHAT_FLAG_AUDIO_VALUE |//??????
                                    InterfaceDevice.Pb_DeviceInviteFlag.Pb_DEVICE_INVITECHAT_FLAG_ASK_VALUE;//??????
                        } else {
                            flag = InterfaceDevice.Pb_DeviceInviteFlag.Pb_DEVICE_INVITECHAT_FLAG_SIMPLEX_VALUE |//??????
                                    InterfaceDevice.Pb_DeviceInviteFlag.Pb_DEVICE_INVITECHAT_FLAG_VIDEO_VALUE |//??????
                                    InterfaceDevice.Pb_DeviceInviteFlag.Pb_DEVICE_INVITECHAT_FLAG_AUDIO_VALUE;//??????
                        }
                        LogUtils.i(TAG, "???????????? -->???????????????ID= " + chooseDevids.toString());
                        mOperdeviceid = GlobalValue.localDeviceId;
                        jni.deviceIntercom(chooseDevids, flag);
                        createPaging();
                    } else {
                        //????????????
                        if (chooseDevids.size() > 1) {
                            ToastUtils.showShort(R.string.can_only_choose_one);
                        } else {
                            LogUtils.i(TAG, "???????????? -->???????????????ID= " + chooseDevids.toString());
                            int flag;
                            if (video_chat_ask_cb.isChecked()) {
                                flag = InterfaceDevice.Pb_DeviceInviteFlag.Pb_DEVICE_INVITECHAT_FLAG_VIDEO_VALUE |//??????
                                        InterfaceDevice.Pb_DeviceInviteFlag.Pb_DEVICE_INVITECHAT_FLAG_AUDIO_VALUE |//??????
                                        InterfaceDevice.Pb_DeviceInviteFlag.Pb_DEVICE_INVITECHAT_FLAG_ASK_VALUE;//??????
                            } else {
                                flag = InterfaceDevice.Pb_DeviceInviteFlag.Pb_DEVICE_INVITECHAT_FLAG_VIDEO_VALUE |//??????
                                        InterfaceDevice.Pb_DeviceInviteFlag.Pb_DEVICE_INVITECHAT_FLAG_AUDIO_VALUE;//??????
                            }
                            mOperdeviceid = GlobalValue.localDeviceId;
                            jni.deviceIntercom(chooseDevids, flag);
                            createIntercom();
                        }
                    }
                } else {
                    ToastUtils.showShort(R.string.please_choose_member);
                }
                break;
            }
            case R.id.pop_video_chat_stop:
                if (work_state != 0) {
                    jni.stopDeviceIntercom(mOperdeviceid);
                }
                break;
            case R.id.pop_video_chat_close:
                onBackPressed();
                break;
        }
    }
}