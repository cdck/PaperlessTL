package com.xlk.paperlesstl.view.offline.node;

import com.chad.library.adapter.base.entity.node.BaseExpandNode;
import com.chad.library.adapter.base.entity.node.BaseNode;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Created by xlk on 2021/3/26.
 * @desc
 */
public class OfflineDirNode extends BaseExpandNode {
    private List<BaseNode> childNode;
    int dirId;
    String dirName;
    int parentid;
    int filenum;

    public OfflineDirNode(List<BaseNode> childNode, int dirId, String dirName,int parentid,int filenum) {
        this.childNode = childNode;
        this.dirId = dirId;
        this.dirName = dirName;
        this.parentid = parentid;
        this.filenum = filenum;
    }

    public void setChildNode(List<BaseNode> childNode) {
        this.childNode = childNode;
    }

    public int getParentid() {
        return parentid;
    }

    public int getFilenum() {
        return filenum;
    }

    public int getDirId() {
        return dirId;
    }

    public String getDirName() {
        return dirName;
    }

    @Nullable
    @Override
    public List<BaseNode> getChildNode() {
        return childNode;
    }
}
