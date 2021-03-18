package com.xlk.paperlesstl.view.fragment.livevideo.node;

import com.chad.library.adapter.base.entity.node.BaseNode;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Created by xlk on 2021/3/11.
 * @desc
 */
public class LevelFileNode extends BaseNode {
    private int dirId;
    private int mediaId;
    private String fileName;

    public LevelFileNode(int dirId, int mediaId, String fileName) {
        this.dirId = dirId;
        this.mediaId = mediaId;
        this.fileName = fileName;
    }

    public int getDirId() {
        return dirId;
    }

    public int getMediaId() {
        return mediaId;
    }

    public String getFileName() {
        return fileName;
    }

    @Nullable
    @Override
    public List<BaseNode> getChildNode() {
        return null;
    }
}
