package com.xlk.paperlesstl.view.admin.fragment.after.archive;

/**
 * @author Created by xlk on 2020/10/27.
 * @desc
 */
public class ArchiveInform {
    int type;
    int mediaId;
    String content;
    String result;

    public ArchiveInform(String content, String result) {
        this.content = content;
        this.result = result;
    }

    public ArchiveInform(int type, int mediaId, String content, String result) {
        this.type = type;
        this.mediaId = mediaId;
        this.content = content;
        this.result = result;
    }

    public ArchiveInform(int id, String content, String result) {
        this.mediaId = id;
        this.content = content;
        this.result = result;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getMediaId() {
        return mediaId;
    }

    public void setMediaId(int mediaId) {
        this.mediaId = mediaId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
