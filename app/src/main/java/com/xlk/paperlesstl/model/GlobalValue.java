package com.xlk.paperlesstl.model;

import android.graphics.Bitmap;

import com.mogujie.tt.protobuf.InterfaceMember;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/3/1.
 * @desc
 */
public class GlobalValue {

    public static int screen_width, screen_height, half_width, half_height;
    public static int camera_width = 1280, camera_height = 720;
    /**
     * 是否初始化完成
     */
    public static boolean initializationIsOver;
    public static int localMeetingId = 0;
    public static int localDeviceId = 0;
    public static int localMemberId = 0;
    public static int localRoomId;
    public static String localMemberName = "";
    public static String localMeetingName = "";
    public static String localDeviceName = "";
    public static String localRoomName = "";
    /**
     * 存放正在下载中的媒体ID，下载退出后进行删除
     */
    public static List<Integer> downloadingFiles = new ArrayList<>();

    /**
     * 本机参会人角色
     */
    public static int localRole;

    /**
     * 是否加载完成（不等于成功加载X5，也可能加载的是系统内核）
     */
    public static boolean initX5Finished = false;
    /**
     * 是否正在播放
     */
    public static boolean isVideoPlaying;
    /**
     * 是否正在被强制性播放中
     */
    public static boolean isMandatoryPlaying;
    /**
     * 是否有新的播放
     */
    public static boolean haveNewPlayInform;
    /**
     * 本机是否拥有全部权限
     */
    public static boolean hasAllPermissions;
    /**
     * 存放所有参会人的权限
     */
    public static List<InterfaceMember.pbui_Item_MemberPermission> allPermissions;
    /**
     * 本机的权限
     */
    public static int localPermission;
    /**
     * 画板的操作ID
     */
    public static int operid;
    public static int signinType;
    /**
     * 用来记录是否从后台打开文档文件时打开的WPS
     * =0会议，=1后台管理，=2离线会议
     */
    public static int PAGE_MODE = 0;

    /**
     * 截图时获取的bitmap对象
     */
    public static Bitmap screenShotBitmap = null;

    public static int theme_type = 0;
}
