package com.xlk.paperlesstl.view.fragment.livevideo;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceVideo;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.adapter.MeetVideoAdapter;
import com.xlk.paperlesstl.model.Constant;
import com.xlk.paperlesstl.model.GlobalValue;
import com.xlk.paperlesstl.model.data.VideoDev;
import com.xlk.paperlesstl.ui.RvItemDecoration;
import com.xlk.paperlesstl.ui.video.CustomVideoView;
import com.xlk.paperlesstl.ui.video.ViewClickListener;
import com.xlk.paperlesstl.view.base.BaseFragment;
import com.xlk.paperlesstl.view.fragment.livevideo.node.FileNodeAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.xlk.paperlesstl.model.Constant.RESOURCE_ID_1;
import static com.xlk.paperlesstl.model.Constant.RESOURCE_ID_2;
import static com.xlk.paperlesstl.model.Constant.RESOURCE_ID_3;
import static com.xlk.paperlesstl.model.Constant.RESOURCE_ID_4;

/**
 * @author Created by xlk on 2021/3/11.
 * @desc
 */
public class LiveVideoFragment extends BaseFragment<LiveVideoPresenter> implements LiveVideoContract.View, View.OnClickListener, CompoundButton.OnCheckedChangeListener, ViewClickListener {

    private CheckBox cb_video, cb_file;
    private CustomVideoView custom_video_view;
    private RecyclerView rv_video, rv_file;
    private Button btn_watch_video, btn_stop_watch;
    List<Integer> ids = new ArrayList<>();
    private MeetVideoAdapter meetVideoAdapter;
    private FileNodeAdapter fileNodeAdapter;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_live_video;
    }

    @Override
    protected void initView(View inflate) {
        cb_video = inflate.findViewById(R.id.cb_video);
        cb_file = inflate.findViewById(R.id.cb_file);
        custom_video_view = inflate.findViewById(R.id.custom_video_view);
        rv_video = inflate.findViewById(R.id.rv_video);
        rv_file = inflate.findViewById(R.id.rv_file);
        btn_watch_video = inflate.findViewById(R.id.btn_watch_video);
        btn_stop_watch = inflate.findViewById(R.id.btn_stop_watch);
        cb_video.setOnCheckedChangeListener(this);
        cb_file.setOnCheckedChangeListener(this);
        btn_watch_video.setOnClickListener(this);
        btn_stop_watch.setOnClickListener(this);

        custom_video_view.setViewClickListener(this);
    }

    @Override
    protected LiveVideoPresenter initPresenter() {
        return new LiveVideoPresenter(this);
    }

    @Override
    protected void initial() {
        ids.add(RESOURCE_ID_1);
        ids.add(RESOURCE_ID_2);
        ids.add(RESOURCE_ID_3);
        ids.add(RESOURCE_ID_4);
        onShow();
    }

    @Override
    protected void onHide() {
        presenter.stopResource(ids);
        custom_video_view.clearAll();
        presenter.releaseVideoRes();
        presenter.unregister();
    }

    @Override
    protected void onShow() {
        presenter.initVideoRes();
        custom_video_view.createView(ids);
        presenter.register();
        presenter.queryMeetVideo();
        presenter.queryFile();
    }

    @Override
    public void updateMeetVideo() {
        if (meetVideoAdapter == null) {
            meetVideoAdapter = new MeetVideoAdapter(presenter.videoDevs);
            rv_video.setLayoutManager(new LinearLayoutManager(getContext()));
            rv_video.addItemDecoration(new RvItemDecoration(getContext()));
            rv_video.setAdapter(meetVideoAdapter);
            meetVideoAdapter.setOnItemClickListener((adapter, view, position) -> {
                VideoDev videoDev = presenter.videoDevs.get(position);
                InterfaceDevice.pbui_Item_DeviceDetailInfo dev = videoDev.getDeviceDetailInfo();
                if (dev.getNetstate() != 1) {
                    return;
                }
                InterfaceVideo.pbui_Item_MeetVideoDetailInfo item = videoDev.getVideoDetailInfo();
                meetVideoAdapter.choose(item.getDeviceid(), item.getId());
            });
        } else {
            meetVideoAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void updateFiles() {
        if (fileNodeAdapter == null) {
            fileNodeAdapter = new FileNodeAdapter(presenter.allData);
            rv_file.setLayoutManager(new LinearLayoutManager(getContext()));
            rv_file.setAdapter(fileNodeAdapter);
        } else {
            fileNodeAdapter.setList(presenter.allData);
            fileNodeAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void updateDecode(Object[] objs) {
        custom_video_view.setVideoDecode(objs);
    }

    @Override
    public void updateYuv(Object[] objs) {
        custom_video_view.setYuv(objs);
    }

    @Override
    public void stopResWork(int resid) {
        custom_video_view.stopResWork(resid);
    }

    @Override
    public void onClick(View v) {
        int resId = custom_video_view.getSelectResId();
        if (resId == -1) {
            ToastUtils.showShort(R.string.please_choose_play_window);
            return;
        }
        List<Integer> temps = new ArrayList<>();
        temps.add(GlobalValue.localDeviceId);
        switch (v.getId()) {
            case R.id.btn_watch_video: {
                if (cb_video.isChecked()) {
                    VideoDev selected = meetVideoAdapter.getSelected();
                    if (selected == null) {
                        ToastUtils.showShort(R.string.please_choose_meet_video);
                        return;
                    }
                    jni.streamPlay(selected.getVideoDetailInfo().getDeviceid(), selected.getVideoDetailInfo().getSubid(), 0, resId, temps);
                } else {
                    int mediaId = fileNodeAdapter.getSelectedFile();
                    if (mediaId == -1) {
                        ToastUtils.showShort(R.string.please_choose_file_first);
                        return;
                    }
                    jni.mediaPlayOperate(mediaId, temps, 0, resId, 0, 0);
                }
                break;
            }
            case R.id.btn_stop_watch: {
                jni.stopResourceOperate(resId, GlobalValue.localDeviceId);
                break;
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.cb_video) {
            cb_file.setChecked(!isChecked);
            if (isChecked) {
                rv_video.setVisibility(View.VISIBLE);
                rv_file.setVisibility(View.GONE);
            }
        } else {
            cb_video.setChecked(!isChecked);
            if (isChecked) {
                rv_file.setVisibility(View.VISIBLE);
                rv_video.setVisibility(View.GONE);
            }
        }
    }

    long oneTime, twoTime, threeTime, fourTime;

    @Override
    public void click(int res) {
        switch (res) {
            case Constant.RESOURCE_ID_1:
                custom_video_view.setSelectResId(res);
                if (System.currentTimeMillis() - oneTime < 500) {
                    custom_video_view.zoom(res);
                } else {
                    oneTime = System.currentTimeMillis();
                }
                break;
            case Constant.RESOURCE_ID_2:
                custom_video_view.setSelectResId(res);
                if (System.currentTimeMillis() - twoTime < 500) {
                    custom_video_view.zoom(res);
                } else {
                    twoTime = System.currentTimeMillis();
                }
                break;
            case Constant.RESOURCE_ID_3:
                custom_video_view.setSelectResId(res);
                if (System.currentTimeMillis() - threeTime < 500) {
                    custom_video_view.zoom(res);
                } else {
                    threeTime = System.currentTimeMillis();
                }
                break;
            case Constant.RESOURCE_ID_4:
                custom_video_view.setSelectResId(res);
                if (System.currentTimeMillis() - fourTime < 500) {
                    custom_video_view.zoom(res);
                } else {
                    fourTime = System.currentTimeMillis();
                }
                break;
            default:
                break;
        }
    }
}
