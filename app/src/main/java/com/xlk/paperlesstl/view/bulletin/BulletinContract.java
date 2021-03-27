package com.xlk.paperlesstl.view.bulletin;

import android.graphics.drawable.Drawable;

import com.mogujie.tt.protobuf.InterfaceFaceconfig;
import com.xlk.paperlesstl.view.base.IBasePresenter;
import com.xlk.paperlesstl.view.base.IBaseView;

/**
 * @author Created by xlk on 2021/3/23.
 * @desc
 */
interface BulletinContract {
    interface View extends IBaseView{
        void updateBulletinLogo(Drawable drawable);

        void updateBulletinBg(Drawable drawable);

        void updateTitleTextView(InterfaceFaceconfig.pbui_Item_FaceTextItemInfo info);

        void updateContentTextView(InterfaceFaceconfig.pbui_Item_FaceTextItemInfo info);

        void updateCloseButton(InterfaceFaceconfig.pbui_Item_FaceTextItemInfo info);

        void closeBulletin(int bulletid);
    }
    interface Presenter extends IBasePresenter{
        /**
         * 查询公告界面配置
         */
        void queryBulletinInterfaceConfig();
    }
}
