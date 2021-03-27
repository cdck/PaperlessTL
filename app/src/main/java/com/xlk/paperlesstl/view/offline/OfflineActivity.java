package com.xlk.paperlesstl.view.offline;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.node.BaseNode;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMeet;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.model.Constant;
import com.xlk.paperlesstl.model.GlobalValue;
import com.xlk.paperlesstl.view.admin.fragment.pre.meetingManage.MeetingAdapter;
import com.xlk.paperlesstl.view.base.BaseActivity;
import com.xlk.paperlesstl.view.offline.node.OfflineDirNode;
import com.xlk.paperlesstl.view.offline.node.OfflineFileNodeAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cc.shinichi.library.ImagePreview;

public class OfflineActivity extends BaseActivity<OfflinePresenter> implements OfflineContract.View {

    private android.widget.RelativeLayout rlTopView;
    private ImageView ivClose;
    private LinearLayout llView;
    private RecyclerView rvMeeting;
    private LinearLayout llBottomView;
    private android.widget.Button btnSwitchMeeting;
    private android.widget.Button btnDeleteMeeting;
    private MeetingAdapter meetingAdapter;

    private RecyclerView rv_offline_file;
    private LinearLayout ll_offline_meeting;
    private OfflineFileNodeAdapter fileNodeAdapter;
    private TextView tv_title;
    public static String currentMeetingName;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_offline;
    }

    @Override
    protected OfflinePresenter initPresenter() {
        return new OfflinePresenter(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        GlobalValue.PAGE_MODE = 2;
        initView();
        jni.initVideoRes(Constant.RESOURCE_ID_0, GlobalValue.screen_width, GlobalValue.screen_height);
        presenter.queryMeeting();
    }

    @Override
    public void updateMeetingList() {
        if (meetingAdapter == null) {
            meetingAdapter = new MeetingAdapter(R.layout.item_admin_meeting, presenter.meetLists);
            rvMeeting.setLayoutManager(new LinearLayoutManager(this));
            rvMeeting.setAdapter(meetingAdapter);
            meetingAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    meetingAdapter.setSelected(presenter.meetLists.get(position).getId());
                }
            });
        } else {
            meetingAdapter.notifyDataSetChanged();
        }
    }

    private void initView() {
        rv_offline_file = (RecyclerView) findViewById(R.id.rv_offline_file);
        ll_offline_meeting = (LinearLayout) findViewById(R.id.ll_offline_meeting);

        rlTopView = (RelativeLayout) findViewById(R.id.rl_top_view);
        tv_title = (TextView) findViewById(R.id.tv_title);
        ivClose = (ImageView) findViewById(R.id.iv_close);
        llView = (LinearLayout) findViewById(R.id.ll_view);
        rvMeeting = (RecyclerView) findViewById(R.id.rv_meeting);
        llBottomView = (LinearLayout) findViewById(R.id.ll_bottom_view);
        btnSwitchMeeting = (Button) findViewById(R.id.btn_switch_meeting);
        btnDeleteMeeting = (Button) findViewById(R.id.btn_delete_meeting);
        ivClose.setOnClickListener(v -> {
            onBackPressed();
        });
        btnSwitchMeeting.setOnClickListener(v -> {
            InterfaceMeet.pbui_Item_MeetMeetInfo selectedMeeting = meetingAdapter.getSelectedMeeting();
            if (selectedMeeting == null) {
                ToastUtils.showShort(R.string.please_choose_meeting_first);
                return;
            }
            LogUtils.e(TAG, "进入离线会议");
            jni.modifyContextProperties(InterfaceMacro.Pb_ContextPropertyID.Pb_MEETCONTEXT_PROPERTY_CURMEETINGID.getNumber(), selectedMeeting.getId());
            presenter.queryFile();
            currentMeetingName = selectedMeeting.getName().toStringUtf8();
            tv_title.setText(currentMeetingName);
//            btnSwitchMeeting.setText(getString(R.string.delete_dir));
//            btnDeleteMeeting.setText(getString(R.string.delete_file));
            llBottomView.setVisibility(View.GONE);
            ll_offline_meeting.setVisibility(View.GONE);
            rv_offline_file.setVisibility(View.VISIBLE);
        });
        btnDeleteMeeting.setOnClickListener(v -> {
            InterfaceMeet.pbui_Item_MeetMeetInfo selectedMeeting = meetingAdapter.getSelectedMeeting();
            if (selectedMeeting == null) {
                ToastUtils.showShort(R.string.please_choose_meeting_first);
                return;
            }
            jni.deleteMeeting(selectedMeeting);
            presenter.queryMeeting();
        });
    }

    @Override
    public void showFiles() {
        if (fileNodeAdapter == null) {
            if (!presenter.showFiles.isEmpty()) {
                BaseNode baseNode = presenter.showFiles.get(0);
                if (baseNode instanceof OfflineDirNode) {
                    OfflineDirNode dirNode = (OfflineDirNode) baseNode;
                    dirNode.setExpanded(true);
                }
            } else {
                LogUtils.e(TAG, "会议目录和文件是空的");
            }
            fileNodeAdapter = new OfflineFileNodeAdapter(presenter.showFiles);
            rv_offline_file.setLayoutManager(new LinearLayoutManager(this));
            rv_offline_file.setAdapter(fileNodeAdapter);
            fileNodeAdapter.setOnDeleteListener(new OfflineFileNodeAdapter.DeleteListener() {
                @Override
                public void onDelete(BaseNode node) {
                    presenter.queryFile();
                }

                @Override
                public void previewPicture(String filePath) {
                    int index = 0;
                    if (!picPath.contains(filePath)) {
                        picPath.add(filePath);
                        index = picPath.size() - 1;
                    } else {
                        for (int i = 0; i < picPath.size(); i++) {
                            if (picPath.get(i).equals(filePath)) {
                                index = i;
                                break;
                            }
                        }
                    }
                    previewImage(index);
                }
            });
        } else {
            fileNodeAdapter.setList(presenter.showFiles);
            fileNodeAdapter.notifyDataSetChanged();
        }
    }

    List<String> picPath = new ArrayList<>();

    private void previewImage(int index) {
        if (picPath.isEmpty()) {
            return;
        }
        ImagePreview.getInstance()
                .setContext(this)
                //设置图片地址集合
                .setImageList(picPath)
                //设置开始的索引
                .setIndex(index)
                //设置是否显示下载按钮
                .setShowDownButton(false)
                //设置是否显示关闭按钮
                .setShowCloseButton(false)
                //设置是否开启下拉图片退出
                .setEnableDragClose(true)
                //设置是否开启上拉图片退出
                .setEnableUpDragClose(true)
                //设置是否开启点击图片退出
                .setEnableClickClose(true)
                .setShowErrorToast(true)
                .start();
    }

    @Override
    public void onBackPressed() {
        if (rv_offline_file.getVisibility() == View.VISIBLE) {
            tv_title.setText(getString(R.string.offline_meeting));
//            btnSwitchMeeting.setText(getString(R.string.switch_meeting));
//            btnDeleteMeeting.setText(getString(R.string.delete));
            rv_offline_file.setVisibility(View.GONE);
            llBottomView.setVisibility(View.VISIBLE);
            ll_offline_meeting.setVisibility(View.VISIBLE);
        } else {
            exit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalValue.PAGE_MODE = 0;
        jni.releaseVideoRes(Constant.RESOURCE_ID_0);
    }

    private void exit() {
        //正常退出还是会在离线会议的模式下
//        finish();
//        startActivity(new Intent(this, MainActivity.class));
        AppUtils.relaunchApp(true);
    }
}