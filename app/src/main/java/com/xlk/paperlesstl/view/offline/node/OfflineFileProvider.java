package com.xlk.paperlesstl.view.offline.node;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.entity.node.BaseNode;
import com.chad.library.adapter.base.provider.BaseNodeProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.jni.JniHelper;
import com.xlk.paperlesstl.model.Constant;
import com.xlk.paperlesstl.util.FileUtil;
import com.xlk.paperlesstl.view.offline.LocalPlayActivity;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import static com.xlk.paperlesstl.util.ConvertUtil.s2b;
import static com.xlk.paperlesstl.view.offline.OfflineActivity.currentMeetingName;

/**
 * @author Created by xlk on 2021/3/26.
 * @desc
 */
class OfflineFileProvider extends BaseNodeProvider {
    private final String TAG = "LevelFileProvider-->";

    @Override
    public int getItemViewType() {
        return OfflineFileNodeAdapter.NODE_TYPE_FILE;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_expandable_offline_file;
    }

    @Override
    public void convert(@NotNull BaseViewHolder helper, BaseNode node) {
        OfflineFileNode fileNode = (OfflineFileNode) node;
        ImageView item_file_iv = helper.getView(R.id.iv_file_type);
        String fileName = fileNode.getName();
        long fileSize = fileNode.getSize();
        String fileSizeStr = FileUtil.formatFileSize(fileSize);

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
                .setText(R.id.tv_file_size, fileSizeStr);

        helper.getView(R.id.iv_preview).setOnClickListener(v -> {
            LogUtils.i(TAG, "onChildClick fileName=" + fileName);
            String filePath = Constant.ROOT_DIR + "meetcache/" + currentMeetingName + "/" + fileNode.getDirName() + "/" + fileName;
            File file = new File(filePath);
            if (file.exists()) {
                if (FileUtil.isAudio(fileName)) {
                    //如果是音频或视频则在线播放
                    /*List<Integer> devIds = new ArrayList<>();
                devIds.add(GlobalValue.localDeviceId);
                JniHelper.getInstance().mediaPlayOperate(fileNode.getMediaId(),
                        devIds, 0, 0, 0,
                        InterfaceMacro.Pb_MeetPlayFlag.Pb_MEDIA_PLAYFLAG_ZERO_VALUE);*/
                    Bundle bundle = new Bundle();
                    bundle.putString("filePath", filePath);
                    bundle.putString("fileName", fileName);
                    context.startActivity(new Intent(context, LocalPlayActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .putExtras(bundle));
                } else if (FileUtil.isPicture(fileName)) {
                    OfflineFileNodeAdapter adapter = (OfflineFileNodeAdapter) getAdapter();
                    adapter.previewPicture(filePath);
                } else {
                    FileUtil.openFile(getContext(), file);
                }
            }
        });
        ImageView iv_delete_file = helper.getView(R.id.iv_delete_file);
        iv_delete_file.setOnClickListener(v -> {
            InterfaceFile.pbui_Item_MeetDirFileDetailInfo build = InterfaceFile.pbui_Item_MeetDirFileDetailInfo.newBuilder()
                    .setAttrib(fileNode.getAttrib())
                    .setFilepos(fileNode.getFilepos())
                    .setMediaid(fileNode.getMediaId())
                    .setMstime(fileNode.getMstime())
                    .setName(s2b(fileNode.getName()))
                    .setSize(fileNode.getSize())
                    .setUploaderid(fileNode.getUploaderId())
                    .setUploaderName(s2b(fileNode.getUploaderName()))
                    .setUploaderRole(fileNode.getUploaderRole())
                    .build();
            JniHelper.getInstance().deleteMeetDirFile(fileNode.getDirId(), build);
            OfflineFileNodeAdapter adapter = (OfflineFileNodeAdapter) getAdapter();
            adapter.delete(fileNode);
        });
    }

}

