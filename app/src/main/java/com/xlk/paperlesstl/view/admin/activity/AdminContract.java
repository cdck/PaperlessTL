package com.xlk.paperlesstl.view.admin.activity;

import com.mogujie.tt.protobuf.InterfaceMeet;
import com.xlk.paperlesstl.view.base.IBasePresenter;
import com.xlk.paperlesstl.view.base.IBaseView;

/**
 * @author Created by xlk on 2021/3/5.
 * @desc
 */
public interface AdminContract {
     interface View extends IBaseView{

         /**
          * 更新在线离线UI
          */
         void updateOnlineStatus(boolean onLine);

         /**
          * 更新席位名称
          */
         void updateDeviceName(String devName);

         /**
          * 更新会议状态
          * @param item 当前的会议
          *  =0未开始会议，=1已开始会议，=2已结束会议
          */
         void updateMeetStatus(InterfaceMeet.pbui_Item_MeetMeetInfo item);
     }
     interface Presenter extends IBasePresenter{

     }

}
