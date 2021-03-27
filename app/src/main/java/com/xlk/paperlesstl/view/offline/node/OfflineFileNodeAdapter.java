package com.xlk.paperlesstl.view.offline.node;

import com.chad.library.adapter.base.BaseNodeAdapter;
import com.chad.library.adapter.base.entity.node.BaseNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Created by xlk on 2021/3/26.
 * @desc
 */
public class OfflineFileNodeAdapter extends BaseNodeAdapter {
    public static final int NODE_TYPE_DIR = 0;
    public static final int NODE_TYPE_FILE = 1;
    public static final int EXPAND_COLLAPSE_PAYLOAD = 110;

    private final OfflineDirProvider levelDirProvider;
    private final OfflineFileProvider levelFileProvider;
    private DeleteListener listener;

    public interface DeleteListener {
        void onDelete(BaseNode node);
        void previewPicture(String filePath);
    }

    public void setOnDeleteListener(DeleteListener listener) {
        this.listener = listener;
    }

    public void previewPicture(String filePath) {
        if (listener != null) {
            listener.previewPicture(filePath);
        }
    }

    public void delete(BaseNode node) {
        if (listener != null) {
            listener.onDelete(node);
        }
    }

    public OfflineFileNodeAdapter(@Nullable List<BaseNode> nodeList) {
        super(nodeList);
        levelDirProvider = new OfflineDirProvider();
        addNodeProvider(levelDirProvider);
        levelFileProvider = new OfflineFileProvider();
        addNodeProvider(levelFileProvider);
    }

    @Override
    protected int getItemType(@NotNull List<? extends BaseNode> list, int position) {
        BaseNode baseNode = list.get(position);
        if (baseNode instanceof OfflineDirNode) {
            return NODE_TYPE_DIR;
        } else if (baseNode instanceof OfflineFileNode) {
            return NODE_TYPE_FILE;
        }
        return -1;
    }
}
