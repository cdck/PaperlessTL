package com.xlk.paperlesstl.view.fragment.annotate;

import com.xlk.paperlesstl.view.base.IBasePresenter;
import com.xlk.paperlesstl.view.base.IBaseView;

/**
 * @author Created by xlk on 2021/3/9.
 * @desc
 */
public interface AnnotateContract {
    interface View extends IBaseView{

        void updateMember();

        void updateFiles();
    }
    interface Presenter extends IBasePresenter{

        void queryMember();

        void queryFile();

        boolean hasPermission(int deviceId);
    }
}
