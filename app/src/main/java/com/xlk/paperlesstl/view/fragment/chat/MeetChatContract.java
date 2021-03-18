package com.xlk.paperlesstl.view.fragment.chat;

import com.xlk.paperlesstl.view.admin.bean.ChatMessage;
import com.xlk.paperlesstl.view.base.IBasePresenter;
import com.xlk.paperlesstl.view.base.IBaseView;

/**
 * @author Created by xlk on 2021/3/9.
 * @desc
 */
public interface MeetChatContract {
    interface View extends IBaseView{

        void updateMembers();

        void addChatMessage(ChatMessage chatMessage);
    }
    interface Presenter extends IBasePresenter{

        void queryMember();
    }
}
