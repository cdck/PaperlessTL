package com.xlk.paperlesstl.view.fragment.bullet;

import com.xlk.paperlesstl.view.base.IBasePresenter;
import com.xlk.paperlesstl.view.base.IBaseView;

/**
 * @author Created by xlk on 2021/3/13.
 * @desc
 */
public interface BulletContract {
    interface View extends IBaseView{
        void updateNoticeList();
    }
    interface Presenter extends IBasePresenter{
        void queryBullet();
    }
}
