package com.xlk.paperlesstl.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.google.protobuf.ByteString;
import com.intrusoft.scatter.ChartData;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceBullet;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceFilescorevote;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceVote;
import com.mogujie.tt.protobuf.InterfaceWhiteboard;
import com.xlk.paperlesstl.App;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.adapter.CanJoinMemberAdapter;
import com.xlk.paperlesstl.adapter.CanJoinProjectorAdapter;
import com.xlk.paperlesstl.adapter.VoteSubmitMemberAdapter;
import com.xlk.paperlesstl.adapter.VoteTitleAdapter;
import com.xlk.paperlesstl.adapter.WmMemberAdapter;
import com.xlk.paperlesstl.adapter.WmProjectorAdapter;
import com.xlk.paperlesstl.helper.CustomBaseViewHolder;
import com.xlk.paperlesstl.helper.SharedPreferenceHelper;
import com.xlk.paperlesstl.jni.JniHelper;
import com.xlk.paperlesstl.model.Constant;
import com.xlk.paperlesstl.model.GlobalValue;
import com.xlk.paperlesstl.model.data.EventMessage;
import com.xlk.paperlesstl.model.data.EventType;
import com.xlk.paperlesstl.model.data.VoteResultSubmitMember;
import com.xlk.paperlesstl.ui.ArtBoard;
import com.xlk.paperlesstl.util.AppUtil;
import com.xlk.paperlesstl.util.DateUtil;
import com.xlk.paperlesstl.util.DialogUtil;
import com.xlk.paperlesstl.view.draw.DrawActivity;
import com.xlk.paperlesstl.view.draw.DrawPresenter;
import com.xlk.paperlesstl.view.main.MainActivity;
import com.xlk.paperlesstl.view.videochat.ChatVideoActivity;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
//import androidx.appcompat.app.AlertDialog;
import android.app.AlertDialog;

import androidx.appcompat.graphics.drawable.DrawableWrapper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import cc.shinichi.library.tool.file.FileUtil;
import skin.support.SkinCompatManager;
import skin.support.widget.SkinCompatImageView;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.xlk.paperlesstl.model.Constant.RESOURCE_ID_0;
import static com.xlk.paperlesstl.model.Constant.RESOURCE_ID_1;
import static com.xlk.paperlesstl.model.Constant.RESOURCE_ID_2;
import static com.xlk.paperlesstl.model.Constant.RESOURCE_ID_3;
import static com.xlk.paperlesstl.model.Constant.RESOURCE_ID_4;
import static com.xlk.paperlesstl.model.Constant.permission_code_projection;
import static com.xlk.paperlesstl.model.Constant.permission_code_screen;
import static com.xlk.paperlesstl.view.draw.DrawPresenter.disposePicSrcmemid;
import static com.xlk.paperlesstl.view.draw.DrawPresenter.disposePicSrcwbidd;
import static com.xlk.paperlesstl.view.draw.DrawPresenter.savePicData;
import static com.xlk.paperlesstl.view.draw.DrawPresenter.tempPicData;
import static com.xlk.paperlesstl.view.videochat.ChatVideoActivity.isChatingOpened;

/**
 * @author Created by xlk on 2021/3/1.
 * @desc
 */
public class FabService extends Service implements FabContract.View {

    private final String TAG = "FabService-->";
    private JniHelper jni = JniHelper.getInstance();
    private FabPresenter presenter;
    private Context context;
    private long downTime, upTime;
    private int mTouchStartX, mTouchStartY;
    private WindowManager wm;
    private int windowWidth, windowHeight;
    private int mScreenDensity;
    private ImageReader mImageReader;
    private SkinCompatImageView hoverButton;
    private boolean hoverButtonIsShowing;
    private View menuView, projectionView, screenView, noteView, voteResultView, canJoinView;
    private boolean menuViewIsShowing, projectionViewIsShowing, screenViewIsShowing, noteViewIsShowing, voteResultViewIsShowing, canJoinViewIsShowing;
    private WindowManager.LayoutParams mParams, defaultParams, fullParams, wrapParams;
    private WmProjectorAdapter wmProjectorAdapter, wmScreenProjectorAdapter;
    private WmMemberAdapter wmMemberAdapter;
    private VoteTitleAdapter voteTitleAdapter;
    public List<InterfaceVote.pbui_Item_MeetVoteDetailInfo> currentVoteInfos = new ArrayList<>();
    private int currentVoteType = InterfaceMacro.Pb_MeetVoteType.Pb_VOTE_MAINTYPE_vote_VALUE;
    private List<VoteResultSubmitMember> submitMemberData = new ArrayList<>();
    private int countPre;
    private List<ChartData> chartDatas;
    /**
     * ????????????-???????????????Adapter
     */
    private VoteSubmitMemberAdapter voteSubmitMemberAdapter;

    private AlertDialog voteDialog;
    private int currentVoteId;
    private int currentChooseCount;
    private int voteTimeouts;
    private int maxChooseCount = 1;//?????????????????????????????????????????????
    private AlertDialog bulletDialog;
    private TextView bullet_title, bullet_content;
    private CanJoinMemberAdapter canJoinMemberAdapter;
    private CanJoinProjectorAdapter canJoinProjectorAdapter;
    private int currentBulletId;

    public static String saveNoteContent = "";
    private View recordingTimeView;
    private Button timeButton;
    private WindowManager.LayoutParams timeParams;
    private boolean timeButtonIsShow;
    private Timer timeTimer;
    private int nowTime;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                String p = (String) msg.obj;
                timeButton.setText(p);
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initTheme();
        context = getApplicationContext();
        presenter = new FabPresenter(context, this);
        initial();
    }

    private void initTheme() {
//        if (GlobalValue.theme_type == 0) {
//            SkinCompatManager.getInstance().restoreDefaultTheme();
//        } else if (GlobalValue.theme_type == 1) {
//            SkinCompatManager.getInstance().loadSkin("red", SkinCompatManager.SKIN_LOADER_STRATEGY_PREFIX_BUILD_IN); // ????????????
//        } else if (GlobalValue.theme_type == 2) {
//            SkinCompatManager.getInstance().loadSkin("yellow", SkinCompatManager.SKIN_LOADER_STRATEGY_PREFIX_BUILD_IN); // ????????????
//        }
    }

    @SuppressLint({"ClickableViewAccessibility", "WrongConstant"})
    private void initial() {
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowWidth = wm.getDefaultDisplay().getWidth();
        windowHeight = wm.getDefaultDisplay().getHeight();
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;
        mImageReader = ImageReader.newInstance(GlobalValue.screen_width, GlobalValue.screen_height, 0x1, 2);
        initAdapter();

        /** **** **  ????????????  ** **** **/
        timeButton = new Button(context);
        timeButton.setTag("timeButton");
        timeButton.setTextColor(Color.RED);
        //?????????????????????????????????????????????
        timeButton.getBackground().setAlpha(50);

        hoverButton = new SkinCompatImageView(App.currentActivity);
//        hoverButton = new ImageView(App.currentActivity);
        hoverButton.setTag("hoverButton");
//        Drawable drawable = context.getResources().getDrawable(R.drawable.sided);
//        hoverButton.setImageDrawable(drawable);
        hoverButton.setImageResource(R.drawable.sided);
        hoverButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downTime = System.currentTimeMillis();
                    mTouchStartX = (int) event.getRawX();
                    mTouchStartY = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int rawX = (int) event.getRawX();
                    int rawY = (int) event.getRawY();
                    int mx = rawX - mTouchStartX;
                    int my = rawY - mTouchStartY;
                    mParams.x += mx;
                    mParams.y += my;//?????????????????????????????????
                    wm.updateViewLayout(hoverButton, mParams);
                    mTouchStartX = rawX;
                    mTouchStartY = rawY;
                    break;
                case MotionEvent.ACTION_UP:
                    upTime = System.currentTimeMillis();
                    if (upTime - downTime > 150) {
                        wm.updateViewLayout(hoverButton, mParams);
                    } else {
                        showMenuView();
                    }
                    break;
            }
            return true;
        });
        initParams();
        hoverButtonIsShowing = true;
        wm.addView(hoverButton, mParams);

        File file = new File(Constant.meeting_note_file_path);
        if (FileUtils.isFileExists(file)) {
            saveNoteContent = FileIOUtils.readFile2String(file);
            LogUtils.e("???????????????????????????=" + saveNoteContent);
        }
        presenter.queryMember();
        presenter.queryVote();
    }

    private void initAdapter() {
        wmProjectorAdapter = new WmProjectorAdapter(presenter.onLineProjectors);
        wmScreenProjectorAdapter = new WmProjectorAdapter(presenter.onLineProjectors);
        wmMemberAdapter = new WmMemberAdapter(presenter.onLineMember);
        voteTitleAdapter = new VoteTitleAdapter(currentVoteInfos);
        voteSubmitMemberAdapter = new VoteSubmitMemberAdapter(submitMemberData);
    }

    @Override
    public void notifyOnLineAdapter() {
        wmProjectorAdapter.notifyDataSetChanged();
        wmScreenProjectorAdapter.notifyDataSetChanged();
        wmMemberAdapter.notifyDataSetChanged();
    }

    /**
     * ???????????????
     */
    private void showMenuView() {
        menuView = LayoutInflater.from(App.currentActivity).inflate(R.layout.fab_menu_test, null);
        menuView.setFocusable(true);
        menuView.setFocusableInTouchMode(true);
        menuView.setTag("menuView");
        //????????????
        menuView.findViewById(R.id.iv_view1).setOnClickListener(v -> {
            if (Constant.hasPermission(permission_code_projection)) {
                presenter.queryMember();
                showProjectionWindow(2);
            } else {
                ToastUtils.showShort(R.string.err_NoPermission);
            }
        });
        //????????????
        menuView.findViewById(R.id.iv_view2).setOnClickListener(v -> {
//            delAllView();
            menuViewIsShowing = false;
            wm.removeView(menuView);
            ActivityUtils.startActivity(MainActivity.class);
            ActivityUtils.finishOtherActivities(MainActivity.class);
//            AppUtils.relaunchApp(true);
        });
        //????????????
        menuView.findViewById(R.id.iv_view3).setOnClickListener(v -> {
            presenter.queryCanJoinScreen();
            showCanJoinWindow();
        });
        //????????????
        menuView.findViewById(R.id.iv_view4).setOnClickListener(v -> {
            if (Constant.hasPermission(permission_code_screen)) {
                presenter.queryMember();
                showScreenWindow(2);
            } else {
                ToastUtils.showShort(R.string.err_NoPermission);
            }
        });
        //????????????
        menuView.findViewById(R.id.iv_view5).setOnClickListener(v -> {
            if (Constant.hasPermission(permission_code_screen)) {
                presenter.queryMember();
                showScreenWindow(1);
            } else {
                ToastUtils.showShort(R.string.err_NoPermission);
            }
        });
        //????????????
        menuView.findViewById(R.id.iv_view6).setOnClickListener(v -> {
            showNoteWindow(menuView, saveNoteContent);
        });
        //????????????
        menuView.findViewById(R.id.iv_view7).setOnClickListener(v -> {
            presenter.queryVote();
            showVoteResultsWindow();
        });
        //????????????
        menuView.findViewById(R.id.iv_view8).setOnClickListener(v -> {
            if (Constant.hasPermission(permission_code_projection)) {
                presenter.queryMember();
                showProjectionWindow(1);
            } else {
                ToastUtils.showShort(R.string.err_NoPermission);
            }
        });
        //??????
        menuView.findViewById(R.id.iv_view9).setOnClickListener(v -> {
            showPop(menuView, hoverButton, mParams);
        });
        showPop(hoverButton, menuView, wrapParams);
    }

    @Override
    public void showRecordingTimeWindow(boolean isStart) {
        LogUtils.i("showRecordingTimeWindow isStart=" + isStart);
        if (isStart) {
            if (!timeButtonIsShow) {
                nowTime = 0;
                timeTimer = new Timer();
                timeTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        nowTime++;
                        String time = DateUtil.intTotime(nowTime);
                        Message message = new Message();
                        message.what = 1;
                        message.obj = time;
                        handler.sendMessage(message);
                    }
                }, 0, 1000);
                /** **** **  ????????????  ** **** **/
                timeParams = new WindowManager.LayoutParams();
                //??????view???????????????????????????????????????->???????????????view???????????????
                timeParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
                setParamsType(timeParams);
                timeParams.format = PixelFormat.RGBA_8888;
                timeParams.gravity = Gravity.START | Gravity.TOP;
                timeParams.width = FrameLayout.LayoutParams.WRAP_CONTENT;
                timeParams.height = FrameLayout.LayoutParams.WRAP_CONTENT;
                timeParams.x = 0;
                timeParams.y = 0;
                wm.addView(timeButton, timeParams);
                timeButtonIsShow = true;
            }
        } else {
            exitTiming();
        }
    }

    //????????????????????????
    private void exitTiming() {
        LogUtils.i("exitTiming-----------------------");
        if (timeTimer != null) {
            timeTimer.cancel();
            timeTimer = null;
        }
        if (timeButtonIsShow) {
            wm.removeView(timeButton);
            timeButtonIsShow = false;
        }
        handler.removeCallbacksAndMessages(null);
        nowTime = 0;
    }

    @Override
    public void updateCanJoinList() {
        LogUtils.e(TAG, "updateCanJoinList");
        if (canJoinMemberAdapter != null) {
            canJoinMemberAdapter.notifyDataSetChanged();
        }
        if (canJoinProjectorAdapter != null) {
            canJoinProjectorAdapter.notifyDataSetChanged();
        }
    }

    /**
     * ????????????????????????
     */
    private void showCanJoinWindow() {
        canJoinView = LayoutInflater.from(App.currentActivity).inflate(R.layout.fab_join_screen, null);
        canJoinView.setTag("canJoinView");
        canJoinViewEvent(canJoinView);
        showPop(menuView, canJoinView);
    }

    private void canJoinViewEvent(View holder) {
        RecyclerView rv_member = holder.findViewById(R.id.rv_member);
        RecyclerView rv_projector = holder.findViewById(R.id.rv_projector);
        canJoinMemberAdapter = new CanJoinMemberAdapter(presenter.canJoinMembers);
        rv_member.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        rv_member.setAdapter(canJoinMemberAdapter);
        canJoinMemberAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                canJoinMemberAdapter.choose(presenter.canJoinMembers.get(position).getDevceid());
                canJoinProjectorAdapter.clearSelect();
            }
        });
        canJoinProjectorAdapter = new CanJoinProjectorAdapter(presenter.canJoinPros);
        rv_projector.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        rv_projector.setAdapter(canJoinProjectorAdapter);
        canJoinProjectorAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                canJoinProjectorAdapter.choose(presenter.canJoinPros.get(position).getDevcieid());
                canJoinMemberAdapter.clearSelect();
            }
        });
        holder.findViewById(R.id.btn_ensure).setOnClickListener(v -> {
            int deviceId = canJoinMemberAdapter.getSelectedId();
            if (deviceId == -1) {
                deviceId = canJoinProjectorAdapter.getSelectedId();
            }
            if (deviceId == -1) {
                ToastUtils.showShort(R.string.please_choose_join_screen_targeten);
                return;
            }
            jni.streamPlay(deviceId, 2, 0, RESOURCE_ID_0, GlobalValue.localDeviceId);
            showPop(canJoinView, hoverButton, mParams);
        });
        holder.findViewById(R.id.btn_cancel).setOnClickListener(v -> {
            showPop(canJoinView, hoverButton, mParams);
        });
    }

    @Override
    public void updateVoteRv() {
        currentVoteInfos.clear();
        for (int i = 0; i < presenter.allVoteInfos.size(); i++) {
            InterfaceVote.pbui_Item_MeetVoteDetailInfo item = presenter.allVoteInfos.get(i);
            if (item.getMaintype() == currentVoteType) {
                currentVoteInfos.add(item);
            }
        }
        voteTitleAdapter.notifyDataSetChanged();
    }

    /**
     * ????????????????????????
     */
    private void showVoteResultsWindow() {
        voteResultView = LayoutInflater.from(App.currentActivity).inflate(R.layout.fab_vote_result_view, null);
        voteResultView.setTag("voteResultView");
        CustomBaseViewHolder.VoteResultViewHolder holder = new CustomBaseViewHolder.VoteResultViewHolder(voteResultView);
        voteResultHolderEvent(holder);
        showPop(menuView, voteResultView, fullParams);
    }

    private void voteResultHolderEvent(CustomBaseViewHolder.VoteResultViewHolder holder) {
        holder.pieChart.setVisibility(View.GONE);
        holder.closeIv.setOnClickListener(v -> showPop(voteResultView, hoverButton, mParams));
        holder.btn_vote.setOnClickListener(v -> {
            holder.btn_vote.setSelected(true);
            holder.btn_election.setSelected(false);
            currentVoteType = InterfaceMacro.Pb_MeetVoteType.Pb_VOTE_MAINTYPE_vote_VALUE;
            updateVoteRv();
        });
        holder.btn_election.setOnClickListener(v -> {
            holder.btn_election.setSelected(true);
            holder.btn_vote.setSelected(false);
            currentVoteType = InterfaceMacro.Pb_MeetVoteType.Pb_VOTE_MAINTYPE_election_VALUE;
            updateVoteRv();
        });
        holder.voteTitleRv.setLayoutManager(new LinearLayoutManager(context));
        holder.voteTitleRv.setAdapter(voteTitleAdapter);
        holder.optionRv.setLayoutManager(new LinearLayoutManager(context));
        holder.optionRv.setAdapter(voteSubmitMemberAdapter);
        voteTitleAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                voteTitleAdapter.choose(currentVoteInfos.get(position).getVoteid());
                showVote(holder, position);
            }
        });
        if (!currentVoteInfos.isEmpty()) {
            showVote(holder, 0);
        }
        holder.btn_vote.performClick();
    }

    /**
     * ???????????????????????????
     *
     * @param holder
     * @param position
     */
    private void showVote(CustomBaseViewHolder.VoteResultViewHolder holder, int position) {
        submitMemberData.clear();
        voteSubmitMemberAdapter.notifyDataSetChanged();
        holder.voteOptionA.setVisibility(View.GONE);
        holder.voteOptionB.setVisibility(View.GONE);
        holder.voteOptionC.setVisibility(View.GONE);
        holder.voteOptionD.setVisibility(View.GONE);
        holder.voteOptionE.setVisibility(View.GONE);
        holder.pieChart.setVisibility(View.GONE);
        countPre = 0;//???????????????????????????
        InterfaceVote.pbui_Item_MeetVoteDetailInfo currentVoteInfo = currentVoteInfos.get(position);
        int voteid = currentVoteInfo.getVoteid();
        int mode = currentVoteInfo.getMode();
        int type = currentVoteInfo.getType();
        String content = currentVoteInfo.getContent().toStringUtf8();
        int votestate = currentVoteInfo.getVotestate();
        int maintype = currentVoteInfo.getMaintype();
        boolean isVote = maintype == InterfaceMacro.Pb_MeetVoteType.Pb_VOTE_MAINTYPE_vote.getNumber();
        String modestr = mode == 0 ? getString(R.string.anonymous) : getString(R.string.notation);
        String typestr = Constant.getVoteType(context, type);
        String votestatestr = Constant.getVoteState(context, votestate);
        holder.vote_type_tv.setText("( " + typestr + "  " + modestr + "  " + votestatestr + " )");
        holder.vote_title_tv.setText(content);
        if (mode == 1) {
            //??????
            InterfaceVote.pbui_Type_MeetVoteSignInDetailInfo object = jni.querySubmitterByVoteId(voteid);
            submitMemberData.clear();
            if (object != null) {
                List<Integer> ids = new ArrayList<>();
                int selectedItem = 0;
                List<InterfaceVote.pbui_Item_MeetVoteSignInDetailInfo> itemList = object.getItemList();
                for (int i = 0; i < itemList.size(); i++) {
                    InterfaceVote.pbui_Item_MeetVoteSignInDetailInfo info = itemList.get(i);
                    String chooseText = "";
                    String name = "";
                    int shidao = 0;
                    int selcnt1 = info.getSelcnt();
                    int i1 = selcnt1 & Constant.PB_VOTE_SELFLAG_CHECKIN;
                    LogUtils.e(TAG, "FabService.fun_queryOneVoteSubmitter :  selcnt1 --> " + selcnt1 + "??? ???????????????= " + i1);
                    if (i1 == Constant.PB_VOTE_SELFLAG_CHECKIN) {
                        shidao++;
                    }
                    LogUtils.e(TAG, "FabService.fun_queryOneVoteSubmitter :  ???????????? --> " + shidao);
                    int id1 = info.getId();
                    ids.add(id1);
                    for (int k = 0; k < presenter.memberDetailInfos.size(); k++) {
                        if (presenter.memberDetailInfos.get(k).getPersonid() == id1) {
                            name = presenter.memberDetailInfos.get(k).getName().toStringUtf8();
                            break;//????????????,??????????????????for??????
                        }
                    }
                    int selcnt = info.getSelcnt();
                    //int????????????????????????????????????
                    String string = Integer.toBinaryString(selcnt);
                    //?????????????????????1???????????????
                    int length = string.length();
                    for (int j = 0; j < length; j++) {
                        char c = string.charAt(j);
                        //??? char ?????????int?????????
                        int a = c - '0';
                        if (a == 1) {
                            selectedItem = length - j - 1;//?????????0??????
                            LogUtils.e(TAG, "FabService.fun_queryOneVoteSubmitter :  ???????????? " + selectedItem + " ???");
                            for (int k = 0; k < currentVoteInfo.getItemList().size(); k++) {
                                if (k == selectedItem) {
                                    InterfaceVote.pbui_SubItem_VoteItemInfo pbui_subItem_voteItemInfo = currentVoteInfo.getItemList().get(k);
                                    String text = pbui_subItem_voteItemInfo.getText().toStringUtf8();
                                    if (chooseText.length() == 0) chooseText = text;
                                    else chooseText += " | " + text;
                                }
                            }
                        }
                    }
                    submitMemberData.add(new VoteResultSubmitMember(id1, name, chooseText));
                }
            }
            voteSubmitMemberAdapter.notifyDataSetChanged();
        }

        holder.member_count_tv.setVisibility(View.GONE);
        holder.vote_member_count_tv.setText(getString(R.string.vote_member_count, 0));
        /** **** **  ????????????????????????  ** **** **/
        if (votestate != InterfaceMacro.Pb_MeetVoteStatus.Pb_vote_notvote.getNumber()) {
            holder.member_count_tv.setVisibility(View.VISIBLE);
            String yingDaoStr = "";//??????
            String shiDaoStr = "";//??????
            String yiTouStr = "";//??????
            String weiTouStr = "";//??????
            InterfaceBase.pbui_CommonInt32uProperty yingDaoInfo = jni.queryVoteSubmitterProperty(voteid, 0, InterfaceMacro.Pb_MeetVotePropertyID.Pb_MEETVOTE_PROPERTY_ATTENDNUM.getNumber());
            InterfaceBase.pbui_CommonInt32uProperty yiTouInfo = jni.queryVoteSubmitterProperty(voteid, 0, InterfaceMacro.Pb_MeetVotePropertyID.Pb_MEETVOTE_PROPERTY_VOTEDNUM.getNumber());
            InterfaceBase.pbui_CommonInt32uProperty shiDaoInfo = jni.queryVoteSubmitterProperty(voteid, 0, InterfaceMacro.Pb_MeetVotePropertyID.Pb_MEETVOTE_PROPERTY_CHECKINNUM.getNumber());
            int yingDao = yingDaoInfo == null ? 0 : yingDaoInfo.getPropertyval();
            int yiTou = yiTouInfo == null ? 0 : yiTouInfo.getPropertyval();
            int shiDao = shiDaoInfo == null ? 0 : shiDaoInfo.getPropertyval();
            yingDaoStr = yingDao + "";
            yiTouStr = yiTou + "";
            shiDaoStr = shiDao + "";
            weiTouStr = (yingDao - yiTou) + "";
            LogUtils.e(TAG, "FabService.holder_event :  ???????????? --> " + yingDao + ", ????????????= " + yiTou);
            holder.member_count_tv.setText(getString(R.string.vote_result_count, yingDaoStr, shiDaoStr, yiTouStr, weiTouStr));
            if (isVote) {
                holder.vote_type_top_ll.setVisibility(View.GONE);
                holder.vote_member_count_tv.setVisibility(View.VISIBLE);
                holder.vote_member_count_tv.setText(getString(R.string.vote_member_count, yiTou));
            } else {
                holder.vote_type_top_ll.setVisibility(View.VISIBLE);
                holder.vote_member_count_tv.setVisibility(View.GONE);
            }
        }

        /** **** **  itemList???item??????????????????????????????????????????  ** **** **/
        List<InterfaceVote.pbui_SubItem_VoteItemInfo> itemList = currentVoteInfo.getItemList();
        int count = getCount(itemList);
        if (chartDatas == null) chartDatas = new ArrayList<>();
        else chartDatas.clear();
        holder.vote_option_color_a.setBackgroundColor(isVote ? getColor(R.color.chart_color_green) : getColor(R.color.chart_color_red));
        holder.vote_option_color_b.setBackgroundColor(isVote ? getColor(R.color.chart_color_red) : getColor(R.color.chart_color_green));
        holder.vote_option_color_c.setBackgroundColor(isVote ? getColor(R.color.chart_color_yellow) : getColor(R.color.chart_color_blue));
        for (int i = 0; i < itemList.size(); i++) {
            InterfaceVote.pbui_SubItem_VoteItemInfo itemInfo = itemList.get(i);
            String option = itemInfo.getText().toStringUtf8();//??????????????????
            int selcnt = itemInfo.getSelcnt();//???????????????????????????
            if (!TextUtils.isEmpty(option)) {
                if (i == 0) {
                    holder.voteOptionA.setVisibility(View.VISIBLE);
                    holder.optionA.setText(getString(R.string.vote_count, option, selcnt + ""));
                    setChartData(count, selcnt, getColor(R.color.black), isVote ? getColor(R.color.chart_color_green) : getColor(R.color.chart_color_red));
                } else if (i == 1) {
                    holder.voteOptionB.setVisibility(View.VISIBLE);
                    holder.optionB.setText(getString(R.string.vote_count, option, selcnt + ""));
                    setChartData(count, selcnt, getColor(R.color.black), isVote ? getColor(R.color.chart_color_red) : getColor(R.color.chart_color_green));
                } else if (i == 2) {
                    holder.voteOptionC.setVisibility(View.VISIBLE);
                    holder.optionC.setText(getString(R.string.vote_count, option, selcnt + ""));
                    setChartData(count, selcnt, getColor(R.color.black), isVote ? getColor(R.color.chart_color_yellow) : getColor(R.color.chart_color_blue));
                } else if (i == 3) {
                    holder.voteOptionD.setVisibility(View.VISIBLE);
                    holder.optionD.setText(getString(R.string.vote_count, option, selcnt + ""));
                    setChartData(count, selcnt, getColor(R.color.black), getColor(R.color.chart_color_aqua));
                } else if (i == 4) {
                    holder.voteOptionE.setVisibility(View.VISIBLE);
                    holder.optionE.setText(getString(R.string.vote_count, option, selcnt + ""));
                    setChartData(count, selcnt, getColor(R.color.black), getColor(R.color.chart_color_pink));
                }
            }
        }
        if (countPre > 0 && countPre < 100) {//??????????????????,????????????????????????
            ChartData lastChartData = chartDatas.get(chartDatas.size() - 1);//?????????????????????????????????
            chartDatas.remove(chartDatas.size() - 1);//?????????????????????????????????
            //???????????????????????????,??????????????????????????????,??????????????????????????????????????????
            chartDatas.add(new ChartData(lastChartData.getDisplayText(),
                    lastChartData.getPartInPercent() + (100 - countPre),
                    lastChartData.getTextColor(), lastChartData.getBackgroundColor()));
        }
        //???????????????????????????
        if (chartDatas.isEmpty()) {
            chartDatas.add(new ChartData(getResources().getString(R.string.null_str), 100, Color.parseColor("#FFFFFF"), Color.parseColor("#676767")));
        }
        holder.pieChart.setChartData(chartDatas);
        holder.pieChart.setVisibility(View.VISIBLE);
        voteTitleAdapter.choose(currentVoteInfo.getVoteid());
    }

    /**
     * @param count  ???????????????????????????
     * @param selcnt ???????????????????????????
     * @param colora
     * @param colorb ???????????????
     * @return ???????????????????????????????????????????????????????????????????????????100%???
     */
    private int setChartData(float count, int selcnt, int colora, int colorb) {
        if (selcnt > 0) {
            float element = (float) selcnt / count;
            LogUtils.d(TAG, "FabService.setUplistener :  element --> " + element);
            int v = (int) (element * 100);
            String str = v + "%";
            countPre += v;
            chartDatas.add(new ChartData(str, v, colora, colorb));
        }
        return countPre;
    }

    private int getCount(List<InterfaceVote.pbui_SubItem_VoteItemInfo> itemList) {
        int count = 0;
        for (int i = 0; i < itemList.size(); i++) {
            InterfaceVote.pbui_SubItem_VoteItemInfo info = itemList.get(i);
            count += info.getSelcnt();
        }
        LogUtils.e(TAG, "FabService.getCount :  ???????????????????????? --> " + count);
        return count;
    }

    /**
     * ????????????????????????
     *
     * @param type =1?????????=2??????
     */
    private void showScreenWindow(int type) {
        screenView = LayoutInflater.from(App.currentActivity).inflate(R.layout.fab_screen_view, null);
        screenView.setTag("screenView");
        CustomBaseViewHolder.WmScreenViewHolder holder = new CustomBaseViewHolder.WmScreenViewHolder(screenView);
        ScreenViewHolderEvent(holder, type);
        showPop(menuView, screenView);
    }

    private void ScreenViewHolderEvent(CustomBaseViewHolder.WmScreenViewHolder holder, int type) {
        holder.cb_mandatory.setVisibility(type == 1 ? View.VISIBLE : View.INVISIBLE);
        holder.tv_title.setText(type == 1 ? context.getString(R.string.launch_screen_title) : context.getString(R.string.stop_screen_title));
        holder.btn_ensure.setText(type == 1 ? context.getString(R.string.launch_screen) : context.getString(R.string.stop_screen));
        holder.rv_member.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        holder.rv_member.setAdapter(wmMemberAdapter);
        wmMemberAdapter.setOnItemClickListener((adapter, view, position) -> {
            wmMemberAdapter.choose(presenter.onLineMember.get(position).getDeviceDetailInfo().getDevcieid());
            holder.cb_member.setChecked(wmMemberAdapter.isCheckAll());
        });
        holder.cb_member.setOnClickListener(v -> {
            boolean checked = holder.cb_member.isChecked();
            holder.cb_member.setChecked(checked);
            wmMemberAdapter.setCheckAll(checked);
        });
        holder.rv_projector.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        holder.rv_projector.setAdapter(wmScreenProjectorAdapter);
        wmScreenProjectorAdapter.setOnItemClickListener((adapter, view, position) -> {
            wmScreenProjectorAdapter.choose(presenter.onLineProjectors.get(position).getDevcieid());
            holder.cb_projector.setChecked(wmScreenProjectorAdapter.isCheckAll());
        });
        holder.cb_projector.setOnClickListener(v -> {
            boolean checked = holder.cb_projector.isChecked();
            holder.cb_projector.setChecked(checked);
            wmScreenProjectorAdapter.setCheckAll(checked);
        });
        holder.btn_ensure.setOnClickListener(v -> {
            List<Integer> selectedIds = wmMemberAdapter.getSelectedIds();
            selectedIds.addAll(wmScreenProjectorAdapter.getSelectedIds());
            if (selectedIds.isEmpty()) {
                ToastUtils.showShort(R.string.please_choose_member_or_projection);
                return;
            }
            if (type == 1) {
                int triggeruserval = holder.cb_mandatory.isChecked() ? InterfaceMacro.Pb_TriggerUsedef.Pb_EXCEC_USERDEF_FLAG_NOCREATEWINOPER_VALUE : 0;
                jni.streamPlay(GlobalValue.localDeviceId, 2, triggeruserval, RESOURCE_ID_0, selectedIds);
            } else {
                jni.stopResourceOperate(RESOURCE_ID_0, selectedIds);
            }
            showPop(screenView, hoverButton, mParams);
        });
        holder.btn_cancel.setOnClickListener(v -> showPop(screenView, hoverButton, mParams));
        holder.cb_member.performClick();
        holder.cb_projector.performClick();
    }

    /**
     * ???????????????????????????
     *
     * @param type =1?????????=2??????
     */
    private void showProjectionWindow(int type) {
        projectionView = LayoutInflater.from(App.currentActivity).inflate(R.layout.fab_projection_view, null);
        projectionView.setTag("projectionView");
        CustomBaseViewHolder.WmProViewHolder holder = new CustomBaseViewHolder.WmProViewHolder(projectionView);
        proViewHolderEvent(holder, type);
        showPop(menuView, projectionView);
    }

    private void proViewHolderEvent(CustomBaseViewHolder.WmProViewHolder holder, int type) {
        holder.cb_mandatory.setVisibility(type == 1 ? View.VISIBLE : View.INVISIBLE);
        holder.tv_title.setText(type == 1 ? context.getString(R.string.launch_pro_title) : context.getString(R.string.stop_pro_title));
        holder.btn_launch_pro.setText(type == 1 ? context.getString(R.string.launch_pro) : context.getString(R.string.stop_pro));
        holder.rv_projector.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        holder.rv_projector.setAdapter(wmProjectorAdapter);
        wmProjectorAdapter.setOnItemClickListener((adapter, view, position) -> {
            wmProjectorAdapter.choose(presenter.onLineProjectors.get(position).getDevcieid());
            holder.cb_all.setChecked(wmProjectorAdapter.isCheckAll());
        });
        holder.cb_all.setOnClickListener(v -> {
            boolean checked = holder.cb_all.isChecked();
            holder.cb_all.setChecked(checked);
            wmProjectorAdapter.setCheckAll(checked);
        });
        holder.cb_full.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    holder.cb_flow_1.setChecked(false);
                    holder.cb_flow_2.setChecked(false);
                    holder.cb_flow_3.setChecked(false);
                    holder.cb_flow_4.setChecked(false);
                }
            }
        });
        CompoundButton.OnCheckedChangeListener lll = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    holder.cb_full.setChecked(false);
                }
            }
        };
        holder.cb_flow_1.setOnCheckedChangeListener(lll);
        holder.cb_flow_2.setOnCheckedChangeListener(lll);
        holder.cb_flow_3.setOnCheckedChangeListener(lll);
        holder.cb_flow_4.setOnCheckedChangeListener(lll);
        holder.btn_launch_pro.setOnClickListener(v -> {
            List<Integer> selectedIds = wmProjectorAdapter.getSelectedIds();
            if (selectedIds.isEmpty()) {
                ToastUtils.showShort(R.string.please_choose_projector_first);
                return;
            }
            List<Integer> res = new ArrayList<>();
            if (holder.cb_full.isChecked()) {
                res.add(RESOURCE_ID_0);
            } else {
                if (holder.cb_flow_1.isChecked()) res.add(RESOURCE_ID_1);
                if (holder.cb_flow_2.isChecked()) res.add(RESOURCE_ID_2);
                if (holder.cb_flow_3.isChecked()) res.add(RESOURCE_ID_3);
                if (holder.cb_flow_4.isChecked()) res.add(RESOURCE_ID_4);
            }
            if (res.isEmpty()) {
                ToastUtils.showShort(context.getString(R.string.please_choose_res_first));
                return;
            }
            if (type == 1) {//????????????
                boolean isMandatory = holder.cb_mandatory.isChecked();
                int triggeruserval = isMandatory ? InterfaceMacro.Pb_TriggerUsedef.Pb_EXCEC_USERDEF_FLAG_NOCREATEWINOPER_VALUE
                        : InterfaceMacro.Pb_TriggerUsedef.Pb_EXCEC_USERDEF_FLAG_ZERO_VALUE;
                jni.streamPlay(GlobalValue.localDeviceId, 2, triggeruserval, res, selectedIds);
            } else {//????????????
                jni.stopResourceOperate(res, selectedIds);
            }
            showPop(projectionView, hoverButton, mParams);
        });
        holder.btn_cancel.setOnClickListener(v -> {
            showPop(projectionView, hoverButton, mParams);
        });
        holder.cb_all.performClick();
    }

    @Override
    public void showVideoChatWindow(int inviteflag, int operdeviceid) {
        //???????????????
        boolean isAsk = (inviteflag & InterfaceDevice.Pb_DeviceInviteFlag.Pb_DEVICE_INVITECHAT_FLAG_ASK_VALUE)
                == InterfaceDevice.Pb_DeviceInviteFlag.Pb_DEVICE_INVITECHAT_FLAG_ASK_VALUE;
        if (!isAsk) {//???????????????????????????????????????
            if (!isChatingOpened) {
                startActivity(new Intent(this, ChatVideoActivity.class)
                        .putExtra(Constant.EXTRA_INVITE_FLAG, inviteflag)
                        .putExtra(Constant.EXTRA_OPERATING_DEVICE_ID, operdeviceid)
                        .setFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            } else {
                EventBus.getDefault().post(new EventMessage.Builder()
                        .type(EventType.BUS_CHAT_STATE)
                        .objects(inviteflag, operdeviceid)
                        .build());
            }
//            showOpenCamera(inviteflag, operdeviceid);
            //?????????
            int flag = inviteflag | InterfaceDevice.Pb_DeviceInviteFlag.Pb_DEVICE_INVITECHAT_FLAG_DEAL_VALUE;
            LogUtils.d(TAG, "???????????????????????????" + flag);
            jni.replyDeviceIntercom(operdeviceid, flag);
            return;
        }
        //???????????????
        DialogUtil.createTipDialog(context, getString(R.string.deviceIntercom_Inform_title, presenter.getMemberNameByDeviceId(operdeviceid)),
                getString(R.string.agree), getString(R.string.reject), new DialogUtil.onDialogClickListener() {
                    @Override
                    public void positive(DialogInterface dialog) {
                        if (!isChatingOpened) {
                            startActivity(new Intent(context, ChatVideoActivity.class)
                                    .putExtra(Constant.EXTRA_INVITE_FLAG, inviteflag)
                                    .putExtra(Constant.EXTRA_OPERATING_DEVICE_ID, operdeviceid)
                                    .setFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                        } else {
                            EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_CHAT_STATE).objects(inviteflag, operdeviceid).build());
                        }
//                        showOpenCamera(inviteflag, operdeviceid);
                        int flag = inviteflag | InterfaceDevice.Pb_DeviceInviteFlag.Pb_DEVICE_INVITECHAT_FLAG_DEAL_VALUE;
                        LogUtils.d(TAG, "showView -->" + "?????????" + flag);
                        jni.replyDeviceIntercom(operdeviceid, flag);
                        dialog.dismiss();
                    }

                    @Override
                    public void negative(DialogInterface dialog) {
                        LogUtils.d(TAG, "showView -->" + "?????????" + inviteflag);
                        jni.replyDeviceIntercom(operdeviceid, inviteflag);
                        dialog.dismiss();
                    }

                    @Override
                    public void dismiss(DialogInterface dialog) {

                    }
                });
    }

    @Override
    public void showOpenCamera() {
//        AppUtil.checkCamera()
        DialogUtil.createTipDialog(context, getString(R.string.please_choose_camera),
                getString(R.string.front_camera), getString(R.string.rear_camera),
                new DialogUtil.onDialogClickListener() {
                    @Override
                    public void positive(DialogInterface dialog) {

                    }

                    @Override
                    public void negative(DialogInterface dialog) {

                    }

                    @Override
                    public void dismiss(DialogInterface dialog) {

                    }
                });
    }

    /**
     * ????????????????????????
     *
     * @param msg
     */
    @Override
    public void openArtBoardInform(EventMessage msg) {
        try {
            LogUtils.i(TAG, "openArtBoardInform ????????????????????????");
            byte[] o = (byte[]) msg.getObjects()[0];
            InterfaceWhiteboard.pbui_Type_MeetStartWhiteBoard object = InterfaceWhiteboard.pbui_Type_MeetStartWhiteBoard.parseFrom(o);
            int operflag = object.getOperflag();//?????????????????? ??????Pb_MeetPostilOperType
            String medianame = object.getMedianame().toStringUtf8();//??????????????????
            DrawPresenter.disposePicOpermemberid = object.getOpermemberid();//????????????????????????ID
            disposePicSrcmemid = object.getSrcmemid();//??????????????????ID ??????????????????
            disposePicSrcwbidd = object.getSrcwbid();//???????????????????????? ?????????????????????????????? ??????????????????
            if (operflag == InterfaceMacro.Pb_MeetPostilOperType.Pb_MEETPOTIL_FLAG_FORCEOPEN.getNumber()) {
                LogUtils.i(TAG, "openArtBoardInform: ??????????????????  ????????????????????????..");
                jni.agreeJoin(GlobalValue.localMemberId, disposePicSrcmemid, disposePicSrcwbidd);
                DrawPresenter.isSharing = true;//?????????????????????????????????????????????
                DrawPresenter.mSrcmemid = disposePicSrcmemid;//?????????????????????ID
                DrawPresenter.mSrcwbid = disposePicSrcwbidd;//??????????????????
                Intent intent = new Intent(context, DrawActivity.class);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else if (operflag == InterfaceMacro.Pb_MeetPostilOperType.Pb_MEETPOTIL_FLAG_REQUESTOPEN.getNumber()) {
                LogUtils.i(TAG, "openArtBoardInform: ??????????????????..");
                whetherOpen(disposePicSrcmemid, disposePicSrcwbidd, medianame, DrawPresenter.disposePicOpermemberid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void whetherOpen(final int srcmemid, final long srcwbidd, String medianame, final int opermemberid) {
        DialogUtil.createTipDialog(context, context.getString(R.string.title_whether_agree_join, medianame),
                context.getString(R.string.agree), context.getString(R.string.reject), new DialogUtil.onDialogClickListener() {
                    @Override
                    public void positive(DialogInterface dialog) {
                        //????????????
                        jni.agreeJoin(GlobalValue.localMemberId, srcmemid, srcwbidd);
                        DrawPresenter.isSharing = true;//?????????????????????????????????????????????
                        DrawPresenter.mSrcmemid = srcmemid;//?????????????????????ID
                        DrawPresenter.mSrcwbid = srcwbidd;
                        Intent intent1 = new Intent(context, DrawActivity.class);
                        if (tempPicData != null) {
                            savePicData = tempPicData;
                            /** **** **  ?????????????????????  ** **** **/
                            ArtBoard.DrawPath drawPath = new ArtBoard.DrawPath();
                            drawPath.operid = GlobalValue.operid;
                            drawPath.srcwbid = srcwbidd;
                            drawPath.srcmemid = srcmemid;
                            drawPath.opermemberid = opermemberid;
                            drawPath.picdata = savePicData;
                            GlobalValue.operid = 0;
                            tempPicData = null;
                            //???????????????????????????????????????
                            DrawPresenter.pathList.add(drawPath);
                        }
                        intent1.addFlags(FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent1);
                        //??????????????????????????????,??????????????????????????????????????????????????????????????????????????????
                        //?????????????????????????????????,????????????????????????
                        if (!DrawPresenter.togetherIDs.contains(opermemberid)) {
                            DrawPresenter.togetherIDs.add(opermemberid);
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void negative(DialogInterface dialog) {
                        jni.rejectJoin(GlobalValue.localMemberId, srcmemid, srcwbidd);
                        dialog.dismiss();
                    }

                    @Override
                    public void dismiss(DialogInterface dialog) {

                    }
                });
    }

    /**
     * ??????????????????
     *
     * @param info
     */
    @Override
    public void showVoteWindow(InterfaceVote.pbui_Item_MeetOnVotingDetailInfo info) {
        if (voteDialog != null && voteDialog.isShowing()) {
            LogUtils.e(TAG, "showVoteWindow --> ???????????????????????????");
            return;
        }
        currentVoteId = info.getVoteid();
        voteDialog = DialogUtil.createTipDialog(App.currentActivity, R.layout.dialog_receive_vote, false, GlobalValue.screen_width, GlobalValue.screen_height);
        VoteViewHolder voteViewHolder = new VoteViewHolder(voteDialog);
        voteViewHolderEvent(voteViewHolder, info);
        voteDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                LogUtils.i(TAG, "onDismiss ??????????????????");
            }
        });
    }

    //??????????????????
    private void voteViewHolderEvent(VoteViewHolder holder, InterfaceVote.pbui_Item_MeetOnVotingDetailInfo info) {
        voteTimeouts = info.getTimeouts();
        String voteMode = getVoteMode(info);
//        String voteType = Constant.getVoteType(context, info.getType());
        holder.tv_title.setText(info.getContent().toStringUtf8() + voteMode);
        int maintype = info.getMaintype();
        int selectItem = 0 | Constant.PB_VOTE_SELFLAG_CHECKIN;
        jni.submitVoteResult(1, currentVoteId, selectItem);
        LogUtils.d(TAG, "??????????????? -->" + voteTimeouts);
        boolean isVote = maintype == InterfaceMacro.Pb_MeetVoteType.Pb_VOTE_MAINTYPE_vote_VALUE;
        int selectcount = info.getSelectcount();
        if (isVote) {
            holder.sv_election.setVisibility(View.GONE);
            holder.rl_ensure.setVisibility(View.GONE);
            holder.ll_vote_view.setVisibility(View.VISIBLE);
            //??????
            holder.iv_favour.setOnClickListener(v -> {
                submitVote(selectcount, 1);
            });
            //??????
            holder.iv_against.setOnClickListener(v -> {
                submitVote(selectcount, 2);
            });
            //??????
            holder.iv_waiver.setOnClickListener(v -> {
                submitVote(selectcount, 4);
            });
        } else {
            holder.sv_election.setVisibility(View.VISIBLE);
            holder.rl_ensure.setVisibility(View.VISIBLE);
            holder.ll_vote_view.setVisibility(View.GONE);
            initCheckBox(holder, info);
            chooseEvent(holder.cb_a);
            chooseEvent(holder.cb_b);
            chooseEvent(holder.cb_c);
            chooseEvent(holder.cb_d);
            chooseEvent(holder.cb_e);
            holder.btn_ensure.setOnClickListener(v -> {
                int answer = 0;
                if (holder.cb_a.isChecked()) answer += 1;
                if (holder.cb_b.isChecked()) answer += 2;
                if (holder.cb_c.isChecked()) answer += 4;
                if (holder.cb_d.isChecked()) answer += 8;
                if (holder.cb_e.isChecked()) answer += 16;
                if (answer != 0) {
                    jni.submitVoteResult(info.getSelectcount(), currentVoteId, answer);
                    closeVoteView();
                } else {
                    ToastUtils.showShort(R.string.please_choose_answer_first);
                }
            });
        }
        holder.iv_close.setOnClickListener(v -> closeVoteView());
        if (voteTimeouts <= 0) {
            holder.countdown_view.setVisibility(View.INVISIBLE);
        } else {
            holder.countdown_view.setVisibility(View.VISIBLE);
            holder.chronometer.setBase(SystemClock.elapsedRealtime());
            holder.chronometer.start();
            holder.chronometer.setOnChronometerTickListener(c -> {
                voteTimeouts--;
                if (voteTimeouts <= 0) {
                    if (isVote) {
                        //????????????????????????????????????????????????????????????
                        jni.submitVoteResult(info.getSelectcount(), currentVoteId, 4);
                    }
                    c.stop();
                    closeVoteView();
                } else {
                    String countdown = DateUtil.countdown(voteTimeouts);
                    c.setText(countdown);
                }
            });
        }
    }

    private void submitVote(int selectcount, int answer) {
        DialogUtil.createTipDialog(this, getString(R.string.sure_you_want_to_submit), getString(R.string.ensure), getString(R.string.cancel), new DialogUtil.onDialogClickListener() {
            @Override
            public void positive(DialogInterface dialog) {
                dialog.dismiss();
                jni.submitVoteResult(selectcount, currentVoteId, answer);
                closeVoteView();
            }

            @Override
            public void negative(DialogInterface dialog) {
                dialog.dismiss();
            }

            @Override
            public void dismiss(DialogInterface dialog) {

            }
        });
    }

    private String getVoteMode(InterfaceVote.pbui_Item_MeetOnVotingDetailInfo info) {
        String voteMode = "(";
        switch (info.getType()) {
            case InterfaceMacro.Pb_MeetVote_SelType.Pb_VOTE_TYPE_SINGLE_VALUE://??????
                voteMode += context.getString(R.string.type_single) + "???";
                maxChooseCount = 1;
                break;
            case InterfaceMacro.Pb_MeetVote_SelType.Pb_VOTE_TYPE_4_5_VALUE://5???4
                voteMode += context.getString(R.string.type_4_5) + "???";
                maxChooseCount = 4;
                break;
            case InterfaceMacro.Pb_MeetVote_SelType.Pb_VOTE_TYPE_3_5_VALUE:
                voteMode += context.getString(R.string.type_3_5) + "???";
                maxChooseCount = 3;
                break;
            case InterfaceMacro.Pb_MeetVote_SelType.Pb_VOTE_TYPE_2_5_VALUE:
                voteMode += context.getString(R.string.type_2_5) + "???";
                maxChooseCount = 2;
                break;
            case InterfaceMacro.Pb_MeetVote_SelType.Pb_VOTE_TYPE_2_3_VALUE:
                voteMode += context.getString(R.string.type_2_3) + "???";
                maxChooseCount = 2;
                break;
            case InterfaceMacro.Pb_MeetVote_SelType.Pb_VOTE_TYPE_MANY_VALUE:
                voteMode += context.getString(R.string.type_multi) + "???";
                maxChooseCount = info.getSelectcount() - 1;
                break;
        }
        if (info.getMode() == InterfaceMacro.Pb_MeetVoteMode.Pb_VOTEMODE_agonymous_VALUE) {//??????
            voteMode += context.getString(R.string.mode_anonymous);
        } else {
            voteMode += context.getString(R.string.mode_register);
        }
        voteMode += "???";
        return voteMode;
    }

    private void initCheckBox(VoteViewHolder holder, InterfaceVote.pbui_Item_MeetOnVotingDetailInfo info) {
        holder.cb_a.setVisibility(View.VISIBLE);
        holder.cb_b.setVisibility(View.VISIBLE);
        holder.cb_c.setVisibility(View.VISIBLE);
        holder.cb_d.setVisibility(View.VISIBLE);
        holder.cb_e.setVisibility(View.VISIBLE);
        int selectcount = info.getSelectcount();//????????????
        switch (selectcount) {
            case 2:
                holder.cb_c.setVisibility(View.GONE);
                holder.cb_d.setVisibility(View.GONE);
                holder.cb_e.setVisibility(View.GONE);
                break;
            case 3:
                holder.cb_d.setVisibility(View.GONE);
                holder.cb_e.setVisibility(View.GONE);
                break;
            case 4:
                holder.cb_e.setVisibility(View.GONE);
                break;
        }
        List<ByteString> textList = info.getTextList();
        for (int i = 0; i < textList.size(); i++) {
            String s = textList.get(i).toStringUtf8();
            switch (i) {
                case 0:
                    holder.cb_a.setText(s);
                    break;
                case 1:
                    holder.cb_b.setText(s);
                    break;
                case 2:
                    holder.cb_c.setText(s);
                    break;
                case 3:
                    holder.cb_d.setText(s);
                    break;
                case 4:
                    holder.cb_e.setText(s);
                    break;
            }
        }
    }

    private void chooseEvent(CheckBox checkBox) {
        checkBox.setOnClickListener(v -> {
            boolean checked = checkBox.isChecked();
            if (checked) {//?????????????????????
                if (currentChooseCount < maxChooseCount) {
                    currentChooseCount++;
                    checkBox.setChecked(true);
                } else {
                    checkBox.setChecked(false);
                    ToastUtils.showShort(context.getString(R.string.max_choose_, maxChooseCount));
                }
            } else {
                currentChooseCount--;
                checkBox.setChecked(false);
            }
        });
    }

    public static class VoteViewHolder {
        public ImageView iv_close;
        public RelativeLayout top_layout;
        public TextView tv_title;
        public TextView tv_time;
        public Chronometer chronometer;
        public ScrollView sv_election;
        public LinearLayout countdown_view;
        public CheckBox cb_a;
        public CheckBox cb_b;
        public CheckBox cb_c;
        public CheckBox cb_d;
        public CheckBox cb_e;
        public RelativeLayout rl_ensure;
        public Button btn_ensure;
        public LinearLayout ll_vote_view;
        public ImageView iv_favour;
        public ImageView iv_against;
        public ImageView iv_waiver;

        public VoteViewHolder(AlertDialog rootView) {
            this.top_layout = (RelativeLayout) rootView.findViewById(R.id.top_layout);
            this.iv_close = (ImageView) rootView.findViewById(R.id.iv_close);
            this.tv_title = (TextView) rootView.findViewById(R.id.tv_title);
            this.countdown_view = (LinearLayout) rootView.findViewById(R.id.countdown_view);
            this.tv_time = (TextView) rootView.findViewById(R.id.tv_time);
            this.chronometer = (Chronometer) rootView.findViewById(R.id.chronometer);
            this.sv_election = (ScrollView) rootView.findViewById(R.id.sv_election);
            this.cb_a = (CheckBox) rootView.findViewById(R.id.cb_a);
            this.cb_b = (CheckBox) rootView.findViewById(R.id.cb_b);
            this.cb_c = (CheckBox) rootView.findViewById(R.id.cb_c);
            this.cb_d = (CheckBox) rootView.findViewById(R.id.cb_d);
            this.cb_e = (CheckBox) rootView.findViewById(R.id.cb_e);
            this.rl_ensure = (RelativeLayout) rootView.findViewById(R.id.rl_ensure);
            this.btn_ensure = (Button) rootView.findViewById(R.id.btn_ensure);
            this.ll_vote_view = (LinearLayout) rootView.findViewById(R.id.ll_vote_view);
            this.iv_favour = (ImageView) rootView.findViewById(R.id.iv_favour);
            this.iv_against = (ImageView) rootView.findViewById(R.id.iv_against);
            this.iv_waiver = (ImageView) rootView.findViewById(R.id.iv_waiver);
        }
    }

    @Override
    public void closeVoteView() {
        if (voteDialog != null && voteDialog.isShowing()) {
            voteDialog.dismiss();
            currentVoteId = -1;
            currentChooseCount = 0;
        }
    }

    boolean dialogIsShowing = false;
    private LinkedList<InterfaceDevice.pbui_Type_MeetRequestPrivilegeNotify> permissionsRequests = new LinkedList<>();//?????????????????????????????????

    @Override
    public void applyPermissionsInform(InterfaceDevice.pbui_Type_MeetRequestPrivilegeNotify info) {
        if (dialogIsShowing) {
            permissionsRequests.addLast(info);
            return;
        }
        dialogIsShowing = true;
        DialogUtil.createTipDialog(App.currentActivity, getString(R.string.apply_permissions, presenter.getMemberNameByDeviceId(info.getDeviceid())),
                getString(R.string.agree), getString(R.string.reject), new DialogUtil.onDialogClickListener() {
                    @Override
                    public void positive(DialogInterface dialog) {
                        jni.revertAttendPermissionsRequest(info.getDeviceid(), 1);
                        dialog.dismiss();
                    }

                    @Override
                    public void negative(DialogInterface dialog) {
                        jni.revertAttendPermissionsRequest(info.getDeviceid(), 0);
                        dialog.dismiss();
                    }

                    @Override
                    public void dismiss(DialogInterface dialog) {
                        dialogIsShowing = false;
                        if (!permissionsRequests.isEmpty()) {
                            InterfaceDevice.pbui_Type_MeetRequestPrivilegeNotify item = permissionsRequests.removeFirst();
                            if (item != null) {
                                LogUtils.d(TAG, "dismiss -->" + "?????????????????????????????????????????????");
                                applyPermissionsInform(item);
                            }
                        }
                    }
                });
    }

    /**
     * ??????????????????
     *
     * @param bullet
     */
    @Override
    public void showBulletWindow(InterfaceBullet.pbui_Item_BulletDetailInfo bullet) {
        if (bulletDialog != null && bulletDialog.isShowing()) {
            currentBulletId = bullet.getBulletid();
            bullet_title.setText(bullet.getTitle().toStringUtf8());
            bullet_content.setText(bullet.getContent().toStringUtf8());
            return;
        }
        currentBulletId = bullet.getBulletid();
        bulletDialog = DialogUtil.createTipDialog(App.currentActivity, R.layout.pop_receive_bullet, false, GlobalValue.screen_width, GlobalValue.screen_height);
        bullet_title = bulletDialog.findViewById(R.id.bullet_title);
        bullet_content = bulletDialog.findViewById(R.id.bullet_content);
        bullet_title.setText(bullet.getTitle().toStringUtf8());
        bullet_content.setText(bullet.getContent().toStringUtf8());
        bulletDialog.findViewById(R.id.btn_close_bullet).setOnClickListener(v -> {
            LogUtils.e(TAG, "btn_close_bullet ???????????????");
            bulletDialog.dismiss();
        });
        bulletDialog.findViewById(R.id.iv_close).setOnClickListener(v -> {
            LogUtils.e(TAG, "iv_close ???????????????");
            bulletDialog.dismiss();
        });
        bulletDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                currentBulletId = -1;
            }
        });
    }

    @Override
    public void closeBulletWindow(int bulletid) {
        if (currentBulletId == bulletid) {
            if (bulletDialog != null && bulletDialog.isShowing()) {
                bulletDialog.dismiss();
            }
        }
    }

    @Override
    public void showNoteView(String content) {
        showNoteWindow(hoverButton, content);
    }

    /**
     * ??????????????????
     *
     * @param removeView
     * @param content
     */
    public void showNoteWindow(View removeView, String content) {
        saveNoteContent = content;
        noteView = LayoutInflater.from(App.currentActivity).inflate(R.layout.fab_note_view, null);
        noteView.setTag("noteView");
        CustomBaseViewHolder.NoteViewHolder holder = new CustomBaseViewHolder.NoteViewHolder(noteView);
        noteViewHolderEvent(holder);
        showPop(removeView, noteView);
    }

    private void noteViewHolderEvent(CustomBaseViewHolder.NoteViewHolder holder) {
        holder.edt_note.setText(saveNoteContent);
        holder.edt_note.setSelection(saveNoteContent.length());
        holder.btn_back.setOnClickListener(v -> {
            saveNoteContent = holder.edt_note.getText().toString();
            showPop(noteView, hoverButton, mParams);
        });
        holder.iv_close.setOnClickListener(v -> {
            saveNoteContent = "";
            showPop(noteView, hoverButton, mParams);
        });
        holder.btn_export_note.setOnClickListener(v -> {
            saveNoteContent = holder.edt_note.getText().toString();
            showPop(noteView, hoverButton, mParams);
            EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_CHOOSE_NOTE_FILE).build());
        });
        holder.btn_save_local.setOnClickListener(v -> {
            String content = holder.edt_note.getText().toString();
            File file = new File(Constant.meeting_note_file_path);
            FileUtils.createOrExistsFile(file);
            if (FileUtil.writeFileFromString(file, content)) {
                ToastUtils.showLong(R.string.save_note_at, file.getAbsolutePath());
            }
        });
        holder.btn_cache.setOnClickListener(v -> {
            String content = holder.edt_note.getText().toString();
            File file = new File(Constant.meeting_note_file_path);
            FileUtils.createOrExistsFile(file);
            if (FileUtil.writeFileFromString(file, content)) {
                ToastUtils.showLong(R.string.save_note_at, file.getAbsolutePath());
            }
        });
    }

    @Override
    public void showScoreView(InterfaceFilescorevote.pbui_Type_StartUserDefineFileScoreNotify info) {

    }

    @Override
    public void closeScoreView() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            delAllView();
        } catch (Exception e) {
            e.printStackTrace();
        }
        presenter.onDestroy();
    }

    private void setParamsType(WindowManager.LayoutParams params) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0?????????
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            params.type = WindowManager.LayoutParams.TYPE_PHONE;//???????????????????????????????????????
        } else {
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;//???????????????????????????????????????
        }
    }

    private void initParams() {
        /** **** **  ????????????  ** **** **/
        mParams = new WindowManager.LayoutParams();
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        setParamsType(mParams);
        mParams.format = PixelFormat.RGBA_8888;
        mParams.gravity = Gravity.START | Gravity.TOP;
        mParams.width = FrameLayout.LayoutParams.WRAP_CONTENT;
        mParams.height = FrameLayout.LayoutParams.WRAP_CONTENT;
        mParams.x = windowWidth / 3;
        mParams.y = 0;//??????windowHeight????????????????????????????????????
//        mParams.windowAnimations = R.style.pop_animation_t_b;
        /** **** **  ??????  ** **** **/
        defaultParams = new WindowManager.LayoutParams();
        setParamsType(defaultParams);
        defaultParams.format = PixelFormat.RGBA_8888;
        defaultParams.gravity = Gravity.CENTER;
        defaultParams.width = GlobalValue.half_width;
        defaultParams.height = GlobalValue.half_height;
//        defaultParams.windowAnimations = R.style.pop_animation_t_b;
        /** **** **  ????????????  ** **** **/
        fullParams = new WindowManager.LayoutParams();
        setParamsType(fullParams);
        fullParams.format = PixelFormat.RGBA_8888;
        fullParams.gravity = Gravity.CENTER;
        fullParams.width = FrameLayout.LayoutParams.MATCH_PARENT;
        fullParams.height = FrameLayout.LayoutParams.MATCH_PARENT;
//        fullParams.windowAnimations = R.style.pop_animation_t_b;
        /** **** **  ??????????????????  ** **** **/
        wrapParams = new WindowManager.LayoutParams();
        setParamsType(wrapParams);
        wrapParams.format = PixelFormat.RGBA_8888;
        wrapParams.gravity = Gravity.CENTER;
        wrapParams.width = FrameLayout.LayoutParams.WRAP_CONTENT;
        wrapParams.height = FrameLayout.LayoutParams.WRAP_CONTENT;
//        wrapParams.windowAnimations = R.style.pop_animation_t_b;
    }

    /**
     * ??????????????????
     *
     * @param removeView ???????????????view
     * @param addView    ???????????????view
     * @param params     params??????
     */
    private void showPop(View removeView, View addView, WindowManager.LayoutParams params) {
        wm.removeView(removeView);
        wm.addView(addView, params);
        setIsShowing(removeView, addView);
    }

    private void showPop(View removeView, View addView) {
        wm.removeView(removeView);
        wm.addView(addView, defaultParams);
        setIsShowing(removeView, addView);
    }

    private void setIsShowing(View remove, View add) {
        String removeTag = (String) remove.getTag();
        String addTag = (String) add.getTag();
        switch (removeTag) {
            case "hoverButton":
                hoverButtonIsShowing = false;
                break;
            case "menuView":
                menuViewIsShowing = false;
                break;
            case "projectionView":
                projectionViewIsShowing = false;
                break;
            case "screenView":
                screenViewIsShowing = false;
                break;
            case "noteView":
                noteViewIsShowing = false;
                break;
            case "canJoinView":
                canJoinViewIsShowing = false;
                break;
            case "voteResultView":
                voteResultViewIsShowing = false;
                break;
        }
        switch (addTag) {
            case "hoverButton":
                hoverButtonIsShowing = true;
                break;
            case "menuView":
                menuViewIsShowing = true;
                break;
            case "projectionView":
                projectionViewIsShowing = true;
                break;
            case "screenView":
                screenViewIsShowing = true;
                break;
            case "noteView":
                noteViewIsShowing = true;
                break;
            case "canJoinView":
                canJoinViewIsShowing = true;
                break;
            case "voteResultView":
                voteResultViewIsShowing = true;
                break;
        }
    }

    private void delAllView() {
        if (hoverButtonIsShowing) wm.removeView(hoverButton);
        if (menuViewIsShowing) wm.removeView(menuView);
        if (projectionViewIsShowing) wm.removeView(projectionView);
        if (screenViewIsShowing) wm.removeView(screenView);
        if (noteViewIsShowing) wm.removeView(noteView);
        if (canJoinViewIsShowing) wm.removeView(canJoinView);
        if (voteResultViewIsShowing) wm.removeView(voteResultView);
    }
}
