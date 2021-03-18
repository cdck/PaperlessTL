package com.xlk.paperlesstl.view.admin.fragment.mid.camera;


import com.xlk.paperlesstl.view.admin.bean.VideoDev;

import java.util.List;

/**
 * @author Created by xlk on 2020/10/26.
 * @desc
 */
public interface AdminCameraControlInterface {
    /**
     * 更新视频列表
     * @param videoDevs 视频信息
     */
    void updateRv(List<VideoDev> videoDevs);

    void updateDecode(Object[] objs);

    void updateYuv(Object[] objs1);

    void stopResWork(int resid);

    void notifyOnLineAdapter();

}
