package com.xlk.paperlesstl.model.data;

/**
 * @author Created by xlk on 2021/3/1.
 * @desc
 */
public class EventType {
    private static final int BUS_BASE = 10000;
    /**
     * 自定义EventBus消息type
     * 主界面背景图片下载完成通知
     */
    public static final int BUS_MAIN_BG = BUS_BASE + 1;
    public static final int BUS_MAIN_LOGO = BUS_BASE + 2;
    public static final int BUS_MEET_BG = BUS_BASE + 3;
    public static final int BUS_MEET_LOGO = BUS_BASE + 4;
    public static final int BUS_BULLETIN_BG = BUS_BASE + 5;
    public static final int BUS_BULLETIN_LOGO = BUS_BASE + 6;
    public static final int BUS_ROOM_BG = BUS_BASE + 7;
    public static final int BUS_SHARE_PIC = BUS_BASE + 8;
    public static final int BUS_SCREEN_SHOT = BUS_BASE + 9;
    /**
     * 通知注册WPS操作的广播
     */
    public static final int BUS_WPS_RECEIVER = BUS_BASE + 10;
    /**
     * 通知打开图片文件
     */
    public static final int BUS_PREVIEW_IMAGE = BUS_BASE + 11;
    /**
     * 会议资料文件下载完成
     */
    public static final int BUS_MATERIAL_FILE = BUS_BASE + 12;
    /**
     * 后台播放数据
     */
    public static final int BUS_VIDEO_DECODE = BUS_BASE + 13;
    /**
     * 后台播放YUV数据
     */
    public static final int BUS_YUV_DISPLAY = BUS_BASE + 14;
    /**
     * 议程文件下载完成通知
     */
    public static final int BUS_AGENDA_FILE = BUS_BASE + 15;
    /**
     * x5内核安装完成
     */
    public static final int BUS_X5_INSTALL = BUS_BASE + 16;
    /**
     * 收到强制性播放
     */
    public static final int BUS_MANDATORY_PLAY = BUS_BASE + 17;
    /**
     * 收到开始采集摄像头的通知
     */
    public static final int BUS_COLLECT_CAMERA_START = BUS_BASE + 18;
    /**
     * 收到停止采集摄像头的通知
     */
    public static final int BUS_COLLECT_CAMERA_STOP = BUS_BASE + 19;

    /**
     * 发送视频聊天的状态
     */
    public static final int BUS_CHAT_STATE = BUS_BASE + 20;
    /**
     * 推送文件
     */
    public static final int BUS_PUSH_FILE = BUS_BASE + 21;
    /**
     * 通知导入笔记
     */
    public static final int BUS_CHOOSE_NOTE_FILE = BUS_BASE + 22;
    /**
     * 导入的笔记内容
     */
    public static final int BUS_EXPORT_NOTE_CONTENT = BUS_BASE + 23;
    /**
     * 新的未读消息
     */
    public static final int BUS_UNREAD_MESSAGE_TIP = BUS_BASE + 24;
    public static final int ARCHIVE_BUS_AGENDA_FILE = BUS_BASE + 25;
    public static final int ARCHIVE_BUS_DOWNLOAD_FILE = BUS_BASE + 26;
    public static final int BUS_READ_AGENDA_TXT = BUS_BASE + 27;
    public static final int BUS_TABLE_CARD_BG = BUS_BASE + 28;
    public static final int BUS_UPLOAD_RELEASE_FILE_FINISH = BUS_BASE + 29;
    public static final int BUS_PROJECTIVE_BG = BUS_BASE + 30;
    public static final int BUS_PROJECTIVE_LOGO = BUS_BASE + 31;
    public static final int BUS_NOTICE_BG = BUS_BASE + 32;
    public static final int BUS_NOTICE_LOGO = BUS_BASE + 33;
    public static final int BUS_VIDEO_CHAT = BUS_BASE + 34;
    public static final int BUS_SIGN_IN_DETAILS = BUS_BASE + 35;
    public static final int BUS_SIGN_IN = BUS_BASE + 36;
    /**
     * 网络断开
     */
    public static final int BUS_NETWORK_DISCONNECTED = BUS_BASE + 37;
    /**
     * 网络连接
     */
    public static final int BUS_NETWORK_CONNECTED = BUS_BASE + 38;
    /**
     * 平台初始化完成
     */
    public static final int BUS_INITIALIZED = BUS_BASE + 39;
    /**
     * 通知关闭服务器未连接的提示窗口
     */
    public static final int BUS_CLOSE_SERVER_DISCONNECTED_DIALOG = BUS_BASE + 40;
}
