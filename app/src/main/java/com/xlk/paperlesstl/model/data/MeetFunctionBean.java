package com.xlk.paperlesstl.model.data;

import com.blankj.utilcode.util.LogUtils;

/**
 * @author Created by xlk on 2021/3/8.
 * @desc
 */
public class MeetFunctionBean {
    int funcode;
    int position;

    public MeetFunctionBean(int funcode, int position) {
        LogUtils.i("xlklog", "功能 funcode=" + funcode + ",position=" + position);
        this.funcode = funcode;
        this.position = position;
    }

    public int getFuncode() {
        return funcode;
    }

    public int getPosition() {
        return position;
    }
}
