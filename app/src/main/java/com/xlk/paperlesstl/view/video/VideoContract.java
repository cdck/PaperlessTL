package com.xlk.paperlesstl.view.video;

import android.view.Surface;

import com.xlk.paperlesstl.view.base.IBasePresenter;
import com.xlk.paperlesstl.view.base.IBaseView;

import java.util.List;

/**
 * @author Created by xlk on 2021/3/6.
 * @desc
 */
public interface VideoContract {
    interface View extends IBaseView {

        void updateProgressUi(int per, String currentTime, String totalTime);

        void updateYuv(int w, int h, byte[] y, byte[] u, byte[] v);

        void setCodecType(int type);

        void setCanNotExit();

        void close();

        void notifyOnlineAdapter();

        void updateAnimator(int status);

        void updateTopTitle(String title);
    }

    interface Presenter extends IBasePresenter {

        void queryMember();

        void setSurface(Surface surface);

        void releasePlay();

        String queryDevName(int deivceid);

        void playOrPause();

        void stopPlay();

        void cutVideoImg();

        void setPlayPlace(int progress);

        void mediaPlayOperate(List<Integer> ids, int value);
    }
}
