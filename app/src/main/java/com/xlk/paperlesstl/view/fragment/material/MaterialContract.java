package com.xlk.paperlesstl.view.fragment.material;

import com.xlk.paperlesstl.view.base.IBasePresenter;
import com.xlk.paperlesstl.view.base.IBaseView;

/**
 * @author Created by xlk on 2021/3/8.
 * @desc
 */
public interface MaterialContract {
    interface View extends IBaseView {

        void showFiles();
    }

    interface Presenter extends IBasePresenter {

        void queryDir();
    }
}
