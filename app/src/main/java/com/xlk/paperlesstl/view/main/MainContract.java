package com.xlk.paperlesstl.view.main;

import android.graphics.drawable.Drawable;

import com.mogujie.tt.protobuf.InterfaceAdmin;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceFaceconfig;
import com.xlk.paperlesstl.view.base.IBasePresenter;
import com.xlk.paperlesstl.view.base.IBaseView;


/**
 * @author Created by xlk on 2020/11/28.
 * @desc
 */
public interface MainContract {
    interface View extends IBaseView {
        /**
         * 更新时间
         *
         * @param date
         */
        void updateTime(String[] date);

        /**
         * 更新席位名称
         *
         * @param name
         */
        void updateSeatName(String name);

        /**
         * 升级包下载完成打开升级弹框
         *
         * @param content 升级内容
         * @param info    版本信息
         */
        void showUpgradeDialog(String content, InterfaceBase.pbui_Type_MeetUpdateNotify info);

        /**
         * 进入会议
         */
        void jump2meet();

        /**
         * 签到进入会议
         */
        void readySignIn();

        /**
         * 更新主页背景图
         *
         * @param drawable 背景图片
         */
        void updateBackground(Drawable drawable);

        /**
         * 更新主页logo图标
         *
         * @param drawable logo图
         */
        void updateLogo(Drawable drawable);


        /**
         * 更新绑定未参会人列表
         */
        void updateUnBindMember();

        /**
         * 更新参会人角色
         *
         * @param string
         */
        void updateMemberRole(String string);

        /**
         * 更新当前会议的状态
         *
         * @param propertyval
         */
        void updateMeetingState(int propertyval);

        /**
         * 更新单位
         *
         * @param unit
         */
        void updateUnit(String unit);

        /**
         * 更新卑职
         *
         * @param string
         */
        void updateNote(String string);

        /**
         * 更新公司名称
         *
         * @param text
         */
        void updateCompany(String text);

        void updateTv(int resid, InterfaceFaceconfig.pbui_Item_FaceTextItemInfo itemInfo);

        void updateDate(int date_relative_main, InterfaceFaceconfig.pbui_Item_FaceTextItemInfo itemInfo);

        void update(int logo_iv_main, InterfaceFaceconfig.pbui_Item_FaceTextItemInfo itemInfo);

        void isShowLogo(boolean isShow);

        void updateBtn(int enter_btn_main, InterfaceFaceconfig.pbui_Item_FaceTextItemInfo itemInfo);

        void updateUI(InterfaceDevice.pbui_Type_DeviceFaceShowDetail devMeetInfo);

        void loginStatus(InterfaceAdmin.pbui_Type_AdminLogonStatus info);
    }

    interface Presenter extends IBasePresenter {
        /**
         * 平台初始化
         *
         * @param uuid 唯一值
         */
        void initialization(String uuid);

        /**
         * 设置界面状态
         */
        void setInterfaceStatus();

        /**
         * 初始化流通道
         */
        void initStream();

        /**
         * 获取席位名称
         */
        void getSeatName();

        /**
         * 查询界面配置
         */
        void queryInterFaceConfiguration();

        /**
         * 查询本机加入的会议
         */
        void queryDeviceMeetInfo();

        /**
         * 查询参会人
         */
        void queryMember();
    }
}
