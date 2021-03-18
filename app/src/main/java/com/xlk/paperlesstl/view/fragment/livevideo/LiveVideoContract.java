package com.xlk.paperlesstl.view.fragment.livevideo;

import com.xlk.paperlesstl.view.base.IBasePresenter;
import com.xlk.paperlesstl.view.base.IBaseView;

import java.util.List;

/**
 * @author Created by xlk on 2021/3/11.
 * @desc
 */
public interface LiveVideoContract {
    interface View extends IBaseView {

        void updateMeetVideo();

        void updateFiles();

        void updateDecode(Object[] objs);

        void updateYuv(Object[] objs);

        void stopResWork(int resid);
    }

    interface Presenter extends IBasePresenter {

        void initVideoRes();

        void releaseVideoRes();

        void queryMeetVideo();

        void queryFile();

        void stopResource(List<Integer> resIds);
    }
}
