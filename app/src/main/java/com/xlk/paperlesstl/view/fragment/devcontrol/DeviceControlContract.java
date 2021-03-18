package com.xlk.paperlesstl.view.fragment.devcontrol;

import com.xlk.paperlesstl.view.base.IBasePresenter;
import com.xlk.paperlesstl.view.base.IBaseView;

import java.util.List;

/**
 * @author Created by xlk on 2021/3/12.
 * @desc
 */
public interface DeviceControlContract {
    interface View extends IBaseView {
        void updateDeviceList();

        void updateMemberRoleList();
    }

    interface Presenter extends IBasePresenter {
        void queryPlaceDeviceRankingInfo();

        void queryMember();

        void modifyDeviceFlag(List<Integer> deviceIds);
    }
}
