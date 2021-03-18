package com.xlk.paperlesstl.view.fragment.share;

import com.xlk.paperlesstl.view.base.IBasePresenter;
import com.xlk.paperlesstl.view.base.IBaseView;

/**
 * @author Created by xlk on 2021/3/11.
 * @desc
 */
public interface ShareMaterialContract {
    interface View extends IBaseView{

        void updateFileRv();
    }
    interface Presenter extends IBasePresenter{

        void queryFile();
    }
}
