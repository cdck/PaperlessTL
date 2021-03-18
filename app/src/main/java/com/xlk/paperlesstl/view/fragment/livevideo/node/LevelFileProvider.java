package com.xlk.paperlesstl.view.fragment.livevideo.node;

import android.view.View;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.entity.node.BaseNode;
import com.chad.library.adapter.base.provider.BaseNodeProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.ui.MarqueeTextView;

import org.jetbrains.annotations.NotNull;

/**
 * @author Created by xlk on 2021/3/11.
 * @desc
 */
public class LevelFileProvider extends BaseNodeProvider {
    private int selectedId = -1;

    @Override
    public int getItemViewType() {
        return FileNodeAdapter.NODE_TYPE_FILE;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_video_file;
    }

    @Override
    public void onClick(@NotNull BaseViewHolder helper, @NotNull View view, BaseNode data, int position) {
        LevelFileNode fileNode = (LevelFileNode) data;
        selectedId = fileNode.getMediaId();
        if (getAdapter() != null) {
            getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void convert(@NotNull BaseViewHolder helper, BaseNode node) {
        LevelFileNode fileNode = (LevelFileNode) node;
        MarqueeTextView textView = helper.getView(R.id.item_view_1);
        textView.setText(fileNode.getFileName());
        boolean isSelected = selectedId == fileNode.getMediaId();
        textView.setTextColor(isSelected ? getContext().getColor(R.color.white) : getContext().getColor(R.color.black));
        LinearLayout item_root_view = helper.getView(R.id.item_root_view);
        item_root_view.setBackgroundColor(isSelected ? getContext().getColor(R.color.light_blue) : getContext().getColor(R.color.white));
    }

    public int getSelectedId() {
        return selectedId;
    }
}
