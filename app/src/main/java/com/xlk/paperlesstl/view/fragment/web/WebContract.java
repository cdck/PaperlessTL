package com.xlk.paperlesstl.view.fragment.web;

import com.xlk.paperlesstl.view.base.IBasePresenter;
import com.xlk.paperlesstl.view.base.IBaseView;

/**
 * @author Created by xlk on 2021/3/11.
 * @desc
 */
public interface WebContract {
    interface View extends IBaseView{

        void updateUrlList();
    }
    interface Presenter extends IBasePresenter{

        void queryWebUrl();
    }
}
