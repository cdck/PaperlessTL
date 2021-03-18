package com.xlk.paperlesstl.view.meet;

import com.xlk.paperlesstl.view.base.IBasePresenter;
import com.xlk.paperlesstl.view.base.IBaseView;

/**
 * @author Created by xlk on 2021/3/8.
 * @desc
 */
public interface MeetingContract {
    interface View extends IBaseView {

        void updateMeetName(String meetingName);

        void hasOtherFunction(boolean has);

        void updateMemberRole(String string);

        void updateMeetFunction();

        void showPushPop(int mediaId);

        void updateMemberAndProjectorAdapter();

        void updateTime(String time, String day, String week);

        void updateMemberName(String memberName);

        void showSignInDetailsFragment();

        void showSignInFragment();
    }

    interface Presenter extends IBasePresenter {

        void initial();

        void initVideoRes();

        void releaseVideoRes();

        void queryDeviceMeetInfo();

        void queryMeetFunction();

        void queryPermission();

        void queryMember();
    }
}
