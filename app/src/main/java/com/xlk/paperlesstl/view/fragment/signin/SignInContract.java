package com.xlk.paperlesstl.view.fragment.signin;

import com.mogujie.tt.protobuf.InterfaceRoom;
import com.xlk.paperlesstl.view.base.IBasePresenter;
import com.xlk.paperlesstl.view.base.IBaseView;

import java.util.List;

/**
 * @author Created by xlk on 2021/3/12.
 * @desc
 */
public interface SignInContract {
    interface View extends IBaseView{

        void updateView(List<InterfaceRoom.pbui_Item_MeetRoomDevSeatDetailInfo> seatDetailInfos, int allMemberCount, int checkedMemberCount);

        void updateRoomBg(String filePath);
    }
    interface Presenter extends IBasePresenter{

        void queryRoomBg();
        void queryPlaceDeviceRankingInfo();

        void queryMember();
    }
}
