package com.xlk.paperlesstl.view.offline.node;

import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.entity.node.BaseNode;
import com.chad.library.adapter.base.provider.BaseNodeProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.jni.JniHelper;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.core.view.ViewCompat;

import static com.xlk.paperlesstl.util.ConvertUtil.s2b;

/**
 * @author Created by xlk on 2021/3/26.
 * @desc
 */
public class OfflineDirProvider extends BaseNodeProvider {
    @Override
    public int getItemViewType() {
        return OfflineFileNodeAdapter.NODE_TYPE_DIR;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_expandable_dir;
    }

    @Override
    public void convert(@NotNull BaseViewHolder helper, BaseNode baseNode) {
        OfflineDirNode dirNode = (OfflineDirNode) baseNode;
        helper.setText(R.id.item_dir_tv, dirNode.getDirName());
        setArrowSpin(helper, baseNode, false);
        RelativeLayout rl_delete_dir = helper.getView(R.id.rl_delete_dir);
        View temp_view = helper.getView(R.id.temp_view);
        rl_delete_dir.setVisibility(View.VISIBLE);
        temp_view.setVisibility(View.VISIBLE);
        ImageView iv_delete_dir = helper.getView(R.id.iv_delete_dir);
        iv_delete_dir.setOnClickListener(v -> {
            InterfaceFile.pbui_Item_MeetDirDetailInfo build = InterfaceFile.pbui_Item_MeetDirDetailInfo.newBuilder()
                    .setId(dirNode.getDirId())
                    .setFilenum(dirNode.getFilenum())
                    .setParentid(dirNode.getParentid())
                    .setName(s2b(dirNode.getDirName()))
                    .build();
            JniHelper.getInstance().deleteMeetDir(build);
            OfflineFileNodeAdapter adapter = (OfflineFileNodeAdapter) getAdapter();
            adapter.delete(dirNode);
        });
    }

    @Override
    public void convert(@NotNull BaseViewHolder helper, @NotNull BaseNode data, @NotNull List<?> payloads) {
        for (Object payload : payloads) {
            if (payload instanceof Integer && (int) payload == OfflineFileNodeAdapter.EXPAND_COLLAPSE_PAYLOAD) {
                // 增量刷新，使用动画变化箭头
                setArrowSpin(helper, data, true);
            }
        }
    }

    private void setArrowSpin(BaseViewHolder helper, BaseNode data, boolean isAnimate) {
        OfflineDirNode entity = (OfflineDirNode) data;
        ImageView imageView = helper.getView(R.id.item_dir_iv);
        if (entity.isExpanded()) {
            if (isAnimate) {
                ViewCompat.animate(imageView).setDuration(200)
                        .setInterpolator(new DecelerateInterpolator())
                        .rotation(90f)
                        .start();
            } else {
                imageView.setRotation(90f);
            }
        } else {
            if (isAnimate) {
                ViewCompat.animate(imageView).setDuration(200)
                        .setInterpolator(new DecelerateInterpolator())
                        .rotation(0f)
                        .start();
            } else {
                imageView.setRotation(0f);
            }
        }
    }

    @Override
    public void onClick(@NotNull BaseViewHolder helper, @NotNull View view, BaseNode data, int position) {
        // 这里使用payload进行增量刷新（避免整个item刷新导致的闪烁，不自然）
        if (getAdapter() != null) {
            getAdapter().expandOrCollapse(position, true, true, OfflineFileNodeAdapter.EXPAND_COLLAPSE_PAYLOAD);
        }
    }
}
