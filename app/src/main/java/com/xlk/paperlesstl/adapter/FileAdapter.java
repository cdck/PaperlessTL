package com.xlk.paperlesstl.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.jni.JniHelper;
import com.xlk.paperlesstl.model.Constant;
import com.xlk.paperlesstl.model.GlobalValue;
import com.xlk.paperlesstl.model.data.EventMessage;
import com.xlk.paperlesstl.model.data.EventType;
import com.xlk.paperlesstl.util.FileUtil;
import com.xlk.paperlesstl.util.PopupUtil;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/3/9.
 * @desc
 */
public class FileAdapter extends BaseQuickAdapter<InterfaceFile.pbui_Item_MeetDirFileDetailInfo, BaseViewHolder> {
    private final String TAG = "FileAdapter-->";
    private final boolean need_uploader;

    public FileAdapter(boolean need_uploader, @Nullable List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> data) {
        super(R.layout.item_expandable_file, data);
        this.need_uploader = need_uploader;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder helper, InterfaceFile.pbui_Item_MeetDirFileDetailInfo item) {
        ImageView item_file_iv = helper.getView(R.id.iv_file_type);
        String fileName = item.getName().toStringUtf8();

        if (FileUtil.isPicture(fileName)) {
            item_file_iv.setImageResource(R.drawable.ic_file_type_picture);
        } else if (FileUtil.isAudio(fileName)) {
            item_file_iv.setImageResource(R.drawable.ic_file_type_video);
        } else if (FileUtil.isPPT(fileName)) {
            item_file_iv.setImageResource(R.drawable.ic_file_type_ppt);
        } else if (FileUtil.isXLS(fileName)) {
            item_file_iv.setImageResource(R.drawable.ic_file_type_wps);
        } else if (FileUtil.isDocument(fileName)) {
            item_file_iv.setImageResource(R.drawable.ic_file_type_word);
        } else {
            item_file_iv.setImageResource(R.drawable.ic_file_type_other);
        }
        helper.setText(R.id.tv_file_name, fileName)
                .setText(R.id.tv_file_size, FileUtil.formatFileSize(item.getSize()));
        if (need_uploader) {
            helper.setText(R.id.tv_uploader, item.getUploaderName().toStringUtf8());
        }
        helper.getView(R.id.iv_preview).setOnClickListener(v -> {
            LogUtils.i(TAG, "onChildClick fileName=" + fileName);
            if (FileUtil.isAudio(fileName)) {
                //如果是音频或视频则在线播放
                List<Integer> devIds = new ArrayList<>();
                devIds.add(GlobalValue.localDeviceId);
                JniHelper.getInstance().mediaPlayOperate(item.getMediaid(),
                        devIds, 0, 0, 0,
                        InterfaceMacro.Pb_MeetPlayFlag.Pb_MEDIA_PLAYFLAG_ZERO_VALUE);
            } else {
                FileUtil.openFile(getContext(), Constant.DOWNLOAD_DIR, fileName, item.getMediaid());
            }
        });
        ImageView iv_more = helper.getView(R.id.iv_more);
        iv_more.setOnClickListener(view -> {
            LogUtils.i(TAG, "点击更多= " + fileName);
            View inflate = LayoutInflater.from(getContext()).inflate(R.layout.item_file_more_oper, null, false);
            int dp2px = ConvertUtils.dp2px(150);
            PopupWindow popupWindow = PopupUtil.createAs(inflate, iv_more, iv_more.getWidth(), -dp2px);
            inflate.findViewById(R.id.tv_push).setOnClickListener(v -> {
                EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_PUSH_FILE).objects(item.getMediaid()).build());
                popupWindow.dismiss();
            });
            inflate.findViewById(R.id.tv_download).setOnClickListener(v -> {
                FileUtils.createOrExistsDir(Constant.DOWNLOAD_DIR);
                JniHelper.getInstance().creationFileDownload(Constant.DOWNLOAD_DIR + fileName, item.getMediaid(),
                        1, 0, Constant.DOWNLOAD_MEETING_FILE);
                popupWindow.dismiss();
            });
            inflate.findViewById(R.id.tv_cache).setOnClickListener(v -> {
                FileUtils.createOrExistsDir(Constant.CACHE_DIR);
                JniHelper.getInstance().creationFileDownload(Constant.CACHE_DIR + fileName, item.getMediaid(),
                        1, 0, Constant.CACHE_MEETING_FILE);
                popupWindow.dismiss();
            });
        });
    }
}
