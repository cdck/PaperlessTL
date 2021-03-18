package com.xlk.paperlesstl.view.admin.fragment.system.device;

import com.mogujie.tt.protobuf.InterfaceDevice;
import com.xlk.paperlesstl.view.admin.BaseInterface;

import java.util.List;


/**
 * @author Created by xlk on 2020/9/18.
 * @desc
 */
public interface AdminDeviceManageInterface extends BaseInterface {

    /**
     * 更新设备列表
     */
    void updateDeviceRv(List<InterfaceDevice.pbui_Item_DeviceDetailInfo> deviceInfos);

    /**
     * 更新会议终端设备列表
     */
    void updateClientRv();
}
