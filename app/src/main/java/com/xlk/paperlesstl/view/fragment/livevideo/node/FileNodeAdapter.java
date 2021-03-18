package com.xlk.paperlesstl.view.fragment.livevideo.node;

import com.chad.library.adapter.base.BaseNodeAdapter;
import com.chad.library.adapter.base.entity.node.BaseNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Created by xlk on 2021/3/11.
 * @desc
 */
public class FileNodeAdapter extends BaseNodeAdapter {
    public static final int NODE_TYPE_DIR = 0;
    public static final int NODE_TYPE_FILE = 1;
    public static final int EXPAND_COLLAPSE_PAYLOAD = 110;
    private final LevelDirProvider levelDirProvider;
    private final LevelFileProvider levelFileProvider;

    public FileNodeAdapter(@Nullable List<BaseNode> nodeList) {
        super(nodeList);
        levelDirProvider = new LevelDirProvider();
        addNodeProvider(levelDirProvider);
        levelFileProvider = new LevelFileProvider();
        addNodeProvider(levelFileProvider);
    }
    public int getSelectedFile(){
        return levelFileProvider.getSelectedId();
    }

    @Override
    protected int getItemType(@NotNull List<? extends BaseNode> list, int position) {
        BaseNode baseNode = list.get(position);
        if (baseNode instanceof LevelDirNode) {
            return NODE_TYPE_DIR;
        } else if (baseNode instanceof LevelFileNode) {
            return NODE_TYPE_FILE;
        }
        return -1;
    }
}
