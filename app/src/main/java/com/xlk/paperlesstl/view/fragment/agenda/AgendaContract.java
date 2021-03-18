package com.xlk.paperlesstl.view.fragment.agenda;

import com.xlk.paperlesstl.view.base.IBasePresenter;
import com.xlk.paperlesstl.view.base.IBaseView;

/**
 * @author Created by xlk on 2021/3/8.
 * @desc
 */
public interface AgendaContract {
    interface View extends IBaseView{

        void initDefault();

        void updateAgendaTv(String content);

        void displayFile(String path);
    }
    interface Presenter extends IBasePresenter{

        void queryAgenda();
    }
}
