package com.xlk.paperlesstl.view.offline;

import com.xlk.paperlesstl.view.base.IBasePresenter;
import com.xlk.paperlesstl.view.base.IBaseView;

/**
 * @author Created by xlk on 2021/3/25.
 * @desc
 */
interface OfflineContract {
    interface View extends IBaseView{
        void updateMeetingList();

        void showFiles();
    }
    interface Presenter extends IBasePresenter{
        void queryMeeting();

        void queryFile();
    }
}
