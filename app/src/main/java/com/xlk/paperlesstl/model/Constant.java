package com.xlk.paperlesstl.model;

import android.content.Context;
import android.os.Environment;

import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.paperlesstl.App;
import com.xlk.paperlesstl.R;

/**
 * @author Created by xlk on 2021/3/1.
 * @desc 常量类
 */
public class Constant {
    public static final String ROOT_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PaperlessTL/";

    public static final String LOGCAT_DIR = ROOT_DIR + "myLog";
    public static final String FILE_DIR = ROOT_DIR + "File/";
    public static final String DIR_PICTURE = FILE_DIR + "Picture/";
    public static final String DOWNLOAD_DIR = FILE_DIR + "Download File/";
    public static final String EXPORT_DIR = FILE_DIR + "Export File/";
    public static final String DIR_ARCHIVE_TEMP = FILE_DIR + "Conference archive cache directory/";
    public static final String DIR_ARCHIVE_ZIP = FILE_DIR + "Conference archive/";

    public static final String meeting_note_file_path = ROOT_DIR + "会议笔记.txt";

    //主题类型type值

    public static final int THEME_TYPE_DEFAULT = 0;
    public static final int THEME_TYPE_RED = 1;
    public static final int THEME_TYPE_YELLOW = 2;

    //request_code

    /**
     * 管理界面中导入投票
     */
    public static final int REQUEST_CODE_IMPORT_VOTE = 1;
    /**
     * 管理界面导入选举
     */
    public static final int REQUEST_CODE_IMPORT_ELECTION = 2;

    //图片名称和下载标识

    /**
     * 主界面背景图
     */
    public static final String MAIN_BG_PNG_TAG = "main_bg";
    /**
     * 主界面logo图标
     */
    public static final String MAIN_LOGO_PNG_TAG = "main_logo";
    /**
     * 会议界面背景图
     */
    public static final String MEET_BG_PNG_TAG = "meet_bg";
    /**
     * 会议室背景图
     */
    public static final String ROOM_BG_PNG_TAG = "room_bg";
    /**
     * 公告背景图
     */
    public static final String NOTICE_BG_PNG_TAG = "notice_bg";
    /**
     * 公告logo图标
     */
    public static final String NOTICE_LOGO_PNG_TAG = "notice_logo";
    /**
     * 投影界面背景图
     */
    public static final String PROJECTIVE_BG_PNG_TAG = "projective_bg";
    /**
     * 投影界面logo图标
     */
    public static final String PROJECTIVE_LOGO_PNG_TAG = "projective_logo";
    /**
     * 下载桌牌背景图片
     */
    public static final String DOWNLOAD_TABLE_CARD_BG = "download_table_card_bg";
    /**
     * 下载标识：下载无进度通知
     */
    public static final String DOWNLOAD_NO_INFORM = "download_no_inform";

    //下载标识

    /**
     * 下载缓存文件时的标识
     */
    public static final String CACHE_MEETING_FILE = "cache_meeting_file";
    /**
     * 下载会议文件时的标识
     */
    public static final String DOWNLOAD_MEETING_FILE = "download_meeting_file";
    /**
     * 下载批注文件时的标识
     */
    public static final String DOWNLOAD_ANNOTATION_FILE = "download_annotation_file";
    /**
     * 下载完成应该打开的文件标识
     */
    public static final String DOWNLOAD_SHOULD_OPEN_FILE = "download_should_open_file";
    /**
     * 会议议程的文件
     */
    public static final String DOWNLOAD_AGENDA_FILE = "download_agenda_file";
    /**
     * 后台下载批注文件
     */
    public static final String DOWNLOAD_ADMIN_ANNOTATION_FILE = "download_admin_annotation_file";


    /**
     * 归档文件时的下载标识
     */
    public static final String ARCHIVE_DOWNLOAD_FILE = "archive_download_file";
    /**
     * 归档议程文件下载标识
     */
    public static final String ARCHIVE_AGENDA_FILE = "archive_agenda_file";
    /**
     * 归档共享文件下载标识
     */
    public static final String ARCHIVE_SHARE_FILE = "archive_share_file";
    /**
     * 归档批注文件下载标识
     */
    public static final String ARCHIVE_ANNOTATION_FILE = "archive_annotation_file";
    /**
     * 归档会议资料下载标识
     */
    public static final String ARCHIVE_MEET_DATA_FILE = "archive_meet_data_file";


    //固定的会议目录ID
    /**
     * 共享文件目录ID
     */
    public static final int SHARED_FILE_DIRECTORY_ID = 1;
    /**
     * 批注文件目录ID
     */
    public static final int ANNOTATION_FILE_DIRECTORY_ID = 2;

    //上传文件标识
    public static final String UPLOAD_CHOOSE_FILE = "upload_choose_file";
    public static final String UPLOAD_DRAW_PIC = "upload_draw_pic";
    public static final String UPLOAD_WPS_FILE = "upload_wps_file";
    /**
     * 上传背景图片时的标识
     */
    public static final String UPLOAD_BACKGROUND_IMAGE = "upload_background_image";
    /**
     * 上传桌牌背景图片时的标识
     */
    public static final String UPLOAD_TABLE_CARD_BACKGROUND_IMAGE = "upload_table_card_background_image";
    /**
     * 上传会议发布文件
     */
    public static final String UPLOAD_PUBLISH_FILE = "upload_publish_file";
    /**
     * 上传升级文件
     */
    public static final String UPLOAD_UPGRADE_FILE = "upload_upgrade_file";

    /**
     * 下载会议资料文件
     */
    public static final String DOWNLOAD_MATERIAL_FILE = "download_material_file";

    public static final int RESOURCE_ID_0 = 0;
    public static final int RESOURCE_ID_1 = 1;
    public static final int RESOURCE_ID_2 = 2;
    public static final int RESOURCE_ID_3 = 3;
    public static final int RESOURCE_ID_4 = 4;
    public static final int RESOURCE_ID_10 = 10;
    public static final int RESOURCE_ID_11 = 11;

    public static final int SCREEN_SUB_ID = 2;
    public static final int CAMERA_SUB_ID = 3;


    //自定义其它功能的功能码

    public static final int FUN_CODE = 200000;
    public static final int FUN_CODE_DEVICE = FUN_CODE + 1;
    public static final int FUN_CODE_VIDEO = FUN_CODE + 2;
    public static final int FUN_CODE_VOTE = FUN_CODE + 3;
    public static final int FUN_CODE_ELECTION = FUN_CODE + 4;
    public static final int FUN_CODE_SCREEN = FUN_CODE + 5;
    public static final int FUN_CODE_BULLETIN = FUN_CODE + 6;
    public static final int FUN_CODE_SCORE = FUN_CODE + 7;
    /**
     * 后台中的签到信息fragment
     */
    public static final int FUN_CODE_SIGN_IN_DETAILS = FUN_CODE + 8;

    /**
     * 发起播放的类型
     */
    public static final String EXTRA_VIDEO_ACTION = "extra_video_action";
    /**
     * 发起播放的设备ID
     */
    public static final String EXTRA_VIDEO_DEVICE_ID = "extra_video_device_id";
    /**
     * 发起播放的设备id流通道
     */
    public static final String EXTRA_VIDEO_SUB_ID = "extra_video_sub_id";
    /**
     * 发起播放的文件类型
     */
    public static final String EXTRA_VIDEO_SUBTYPE = "extra_video_subtype";
    /**
     * 播放的媒体id
     */
    public static final String EXTRA_VIDEO_MEDIA_ID = "extra_video_media_id";
    /**
     * 设备对讲
     */
    public static final String EXTRA_INVITE_FLAG = "extra_invite_flag";
    public static final String EXTRA_OPERATING_DEVICE_ID = "extra_operating_device_id";
    /**
     * 传入摄像头前置/后置
     */
    public static final String EXTRA_CAMERA_TYPE = "extra_camera_type";
    /**
     * 登录用户id
     */
    public static final String EXTRA_ADMIN_ID = "extra_admin_id";
    /**
     * 登录用户名称
     */
    public static final String EXTRA_ADMIN_NAME = "extra_admin_name";
    /**
     * 登录时的密码
     */
    public static final String EXTRA_ADMIN_PASSWORD = "extra_admin_password";

    /**
     * 投票时提交，用于签到参与投票
     */
    public static final int PB_VOTE_SELFLAG_CHECKIN = 0x80000000;

    //文件类别

    //  大类
    /**
     * 音频
     */
    public static final int MEDIA_FILE_TYPE_AUDIO = 0x00000000;
    /**
     * 视频
     */
    public static final int MEDIA_FILE_TYPE_VIDEO = 0x20000000;
    /**
     * 录制
     */
    public static final int MEDIA_FILE_TYPE_RECORD = 0x40000000;
    /**
     * 图片
     */
    public static final int MEDIA_FILE_TYPE_PICTURE = 0x60000000;
    /**
     * 升级
     */
    public static final int MEDIA_FILE_TYPE_UPDATE = 0xe0000000;
    /**
     * 临时文件
     */
    public static final int MEDIA_FILE_TYPE_TEMP = 0x80000000;
    /**
     * 其它文件
     */
    public static final int MEDIA_FILE_TYPE_OTHER = 0xa0000000;
    public static final int MAIN_TYPE_BITMASK = 0xe0000000;

    //  小类
    /**
     * PCM文件
     */
    public static final int MEDIA_FILE_TYPE_PCM = 0x01000000;
    /**
     * MP3文件
     */
    public static final int MEDIA_FILE_TYPE_MP3 = 0x02000000;
    /**
     * WAV文件
     */
    public static final int MEDIA_FILE_TYPE_ADPCM = 0x03000000;
    /**
     * FLAC文件
     */
    public static final int MEDIA_FILE_TYPE_FLAC = 0x04000000;
    /**
     * MP4文件
     */
    public static final int MEDIA_FILE_TYPE_MP4 = 0x07000000;
    /**
     * MKV文件
     */
    public static final int MEDIA_FILE_TYPE_MKV = 0x08000000;
    /**
     * RMVB文件
     */
    public static final int MEDIA_FILE_TYPE_RMVB = 0x09000000;
    /**
     * RM文件
     */
    public static final int MEDIA_FILE_TYPE_RM = 0x0a000000;
    /**
     * AVI文件
     */
    public static final int MEDIA_FILE_TYPE_AVI = 0x0b000000;
    /**
     * bmp文件
     */
    public static final int MEDIA_FILE_TYPE_BMP = 0x0c000000;
    /**
     * jpeg文件
     */
    public static final int MEDIA_FILE_TYPE_JPEG = 0x0d000000;
    /**
     * png文件
     */
    public static final int MEDIA_FILE_TYPE_PNG = 0x0e000000;
    /**
     * 其它文件
     */
    public static final int MEDIA_FILE_TYPE_OTHER_SUB = 0x10000000;

    public static final int SUB_TYPE_BITMASK = 0x1f000000;


    /* **** **  权限码  ** **** */
    /**
     * 同屏权限
     */
    public static final int permission_code_screen = 1;
    /**
     * 投影权限
     */
    public static final int permission_code_projection = 2;
    /**
     * 上传权限
     */
    public static final int permission_code_upload = 4;
    /**
     * 下载权限
     */
    public static final int permission_code_download = 8;
    /**
     * 投票权限
     */
    public static final int permission_code_vote = 16;
    /**
     * 发送广播时的action和extra
     */
    public static final String ACTION_START_SCREEN_RECORD = "action_start_screen_record";
    public static final String ACTION_STOP_SCREEN_RECORD = "action_stop_screen_record";
    /**
     * 退出应用时发送广播通知停止掉屏幕录制
     */
    public static final String ACTION_STOP_SCREEN_RECORD_WHEN_EXIT_APP = "action_stop_screen_record_when_exit_app";
    /**
     * 要采集的类型，值为2或3，屏幕或摄像头
     */
    public static final String EXTRA_CAPTURE_TYPE = "extra_capture_type";

    /**
     * 判断本机是否拥有某权限
     */
    public static boolean hasPermission(int code) {
        if (GlobalValue.hasAllPermissions) {
            return true;
        }
        return (GlobalValue.localPermission & code) == code;
    }

    /**
     * 判断权限码是否有某一权限
     *
     * @param permission 权限
     * @param code       某一权限的权限码
     */
    public static boolean isHasPermission(int permission, int code) {
        return (permission & code) == code;
    }

    /**
     * 判断设备是否是当前{@param type}类型
     *
     * @param type  类型
     * @param devId 设备ID
     * @see InterfaceMacro.Pb_DeviceIDType
     */
    public static boolean isThisDevType(int type, int devId) {
        return (devId & Constant.DEVICE_MEET_ID_MASK) == type;
    }

    public static final int DEVICE_MEET_ID_MASK = 0xfffc0000;

    /**
     * 获取该设备的类型名称
     *
     * @return 返回空表示是未识别的设备（服务器设备）
     */
    public static String getDeviceTypeName(Context context, int deviceId) {
        if (isThisDevType(InterfaceMacro.Pb_DeviceIDType.Pb_DeviceIDType_MeetDBServer_VALUE, deviceId)) {
            //会议数据库设备
            return context.getString(R.string.database_dev);
        } else if (isThisDevType(InterfaceMacro.Pb_DeviceIDType.Pb_DeviceIDType_MeetService_VALUE, deviceId)) {
            //会议茶水设备
            return context.getString(R.string.tea_dev);
        } else if (isThisDevType(InterfaceMacro.Pb_DeviceIDType.Pb_DeviceIDType_MeetProjective_VALUE, deviceId)) {
            //会议投影设备
            return context.getString(R.string.pro_dev);
        } else if (isThisDevType(InterfaceMacro.Pb_DeviceIDType.Pb_DeviceIDType_MeetCapture_VALUE, deviceId)) {
            //会议流采集设备
            return context.getString(R.string.capture_dev);
        } else if (isThisDevType(InterfaceMacro.Pb_DeviceIDType.Pb_DeviceIDType_MeetClient_VALUE, deviceId)) {
            //会议终端设备
            return context.getString(R.string.client_dev);
        } else if (isThisDevType(InterfaceMacro.Pb_DeviceIDType.Pb_DeviceIDType_MeetVideoClient_VALUE, deviceId)) {
            //会议视屏对讲客户端
            return context.getString(R.string.video_chat_dev);
        } else if (isThisDevType(InterfaceMacro.Pb_DeviceIDType.Pb_DeviceIDType_MeetPublish_VALUE, deviceId)) {
            //会议发布
            return context.getString(R.string.release_dev);
        } else if (isThisDevType(InterfaceMacro.Pb_DeviceIDType.Pb_DEVICE_MEET_PHPCLIENT_VALUE, deviceId)) {
            //PHP中转数据设备
        } else if (isThisDevType(InterfaceMacro.Pb_DeviceIDType.Pb_DeviceIDType_MeetShare_VALUE, deviceId)) {
            //会议一键同屏设备
            return context.getString(R.string.screen_dev);
        }
        return "";
    }

    /**
     * 获取操作界面名称
     */
    public static String getInterfaceStateName(Context context, int state) {
        if (state == InterfaceMacro.Pb_MeetFaceStatus.Pb_MemState_MainFace_VALUE) {
            return context.getString(R.string.face_main);
        } else if (state == InterfaceMacro.Pb_MeetFaceStatus.Pb_MemState_MemFace_VALUE) {
            return context.getString(R.string.face_meet);
        } else if (state == InterfaceMacro.Pb_MeetFaceStatus.Pb_MemState_AdminFace_VALUE) {
            return context.getString(R.string.face_back);
        } else if (state == InterfaceMacro.Pb_MeetFaceStatus.Pb_MemState_VoteFace_VALUE) {
            return context.getString(R.string.face_vote);
        }
        return "";
    }

    /**
     * 获取参会人身份名称
     *
     * @param context 上下文
     * @param role    身份代码  InterfaceMacro.Pb_MeetMemberRole
     * @return 秘书、管理员...
     */
    public static String getMemberRoleName(Context context, int role) {
        switch (role) {
            case InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_normal_VALUE:
                return context.getString(R.string.member_role_ordinary);
            case InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_compere_VALUE:
                return context.getString(R.string.member_role_host);
            case InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_secretary_VALUE:
                return context.getString(R.string.member_role_secretary);
            case InterfaceMacro.Pb_MeetMemberRole.Pb_role_admin_VALUE:
                return context.getString(R.string.member_role_admin);
            default:
                return context.getString(R.string.none);
        }
    }

    public static String getVoteMode(Context context, int mode) {
        if (mode == InterfaceMacro.Pb_MeetVoteMode.Pb_VOTEMODE_agonymous_VALUE) {
            return context.getString(R.string.anonymous);
        } else {
            return context.getString(R.string.notation);
        }
    }

    public static String getVoteType(Context context, int type) {
        switch (type) {
            case InterfaceMacro.Pb_MeetVote_SelType.Pb_VOTE_TYPE_4_5_VALUE:
                return context.getString(R.string.vote_type_4_5);
            case InterfaceMacro.Pb_MeetVote_SelType.Pb_VOTE_TYPE_3_5_VALUE:
                return context.getString(R.string.vote_type_3_5);
            case InterfaceMacro.Pb_MeetVote_SelType.Pb_VOTE_TYPE_2_5_VALUE:
                return context.getString(R.string.vote_type_2_5);
            case InterfaceMacro.Pb_MeetVote_SelType.Pb_VOTE_TYPE_2_3_VALUE:
                return context.getString(R.string.vote_type_2_3);
            default:
                return context.getString(R.string.vote_type_single);
        }
    }

    public static String getVoteState(Context context, int votestate) {
        if (votestate == InterfaceMacro.Pb_MeetVoteStatus.Pb_vote_notvote_VALUE) {
            return context.getString(R.string.vote_state_not_initiated);
        } else if (votestate == InterfaceMacro.Pb_MeetVoteStatus.Pb_vote_voteing_VALUE) {
            return context.getString(R.string.vote_state_ongoing);
        } else {
            return context.getString(R.string.vote_state_has_ended);
        }
    }


    //编码类型
    /**
     * VP8 video (i.e. video in .webm)
     */
    public static final String MIME_VIDEO_VP8 = "video/x-vnd.on2.vp8";
    /**
     * VP9 video (i.e. video in .webm)
     */
    public static final String MIME_VIDEO_VP9 = "video/x-vnd.on2.vp9";
    /**
     * H.263 video
     */
    public static final String MIME_VIDEO_3GPP = "video/3gpp";
    /**
     * SCREEN_HEIGHT.264/AVC video
     */
    public static final String MIME_VIDEO_AVC = "video/avc";

    /**
     * SCREEN_HEIGHT.265/HEVC video
     */
    public static final String MIME_VIDEO_HEVC = "video/hevc";
    /**
     * MPEG4 video
     */
    public static final String MIME_VIDEO_MPEG4 = "video/mp4v-es";

    /**
     * 获取指定的MimeType
     *
     * @param codecId 后台回调ID
     * @return MimeType
     */
    public static String getMimeType(int codecId) {
        switch (codecId) {
            case 12:
            case 13:
                return MIME_VIDEO_MPEG4;
            case 139:
            case 140:
                return MIME_VIDEO_VP8;
            case 167:
            case 168:
                return MIME_VIDEO_VP9;
            case 173:
            case 174:
                return MIME_VIDEO_HEVC;
            default:
                return MIME_VIDEO_AVC;
        }
    }

    /**
     * 获取会议签到类型名称
     *
     * @param type InterfaceMacro.Pb_MeetSignType
     */
    public static String getMeetSignInTypeName(int type) {
        switch (type) {
            case InterfaceMacro.Pb_MeetSignType.Pb_signin_psw_VALUE:
                return App.appContext.getString(R.string.personal_password_signin);
            case InterfaceMacro.Pb_MeetSignType.Pb_signin_photo_VALUE:
                return App.appContext.getString(R.string.handwriting_signin);
            case InterfaceMacro.Pb_MeetSignType.Pb_signin_onepsw_VALUE:
                return App.appContext.getString(R.string.meeting_password_signin);
            default:
                return App.appContext.getString(R.string.direct_signin);
        }
    }

    /**
     * 获取会议功能名称
     *
     * @param context 上下文
     * @param funCode 会议功能码
     */
    public static String getFunctionString(Context context, int funCode) {
        switch (funCode) {
            case InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_AGENDA_BULLETIN_VALUE:
                return context.getString(R.string.meeting_agenda);
            case InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_MATERIAL_VALUE:
                return context.getString(R.string.meeting_data);
//            case InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_SHAREDFILE_VALUE:
//                return context.getString(R.string.meeting_shared_file);
            case InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_POSTIL_VALUE:
                return context.getString(R.string.meeting_annotation_file);
            case InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_MESSAGE_VALUE:
                return context.getString(R.string.meeting_chat);
            case InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_VIDEOSTREAM_VALUE:
                return context.getString(R.string.meeting_live_video);
            case InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_WHITEBOARD_VALUE:
                return context.getString(R.string.meeting_art_board);
            case InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_WEBBROWSER_VALUE:
                return context.getString(R.string.meeting_web_browsing);
//            case InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_VOTERESULT_VALUE:
//                return context.getString(R.string.meeting_questionnaire);
            case InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_SIGNINRESULT_VALUE:
                return context.getString(R.string.meeting_sign_in_information);
            default:
                return context.getString(R.string.unrecognized);
        }
    }


    /**
     * 获取会议状态
     *
     * @param state InterfaceMacro.Pb_MeetVoteStatus
     * @return 未发起、已经结束、正在进行
     */
    public static String getVoteStateName(Context context, int state) {
        switch (state) {
            case InterfaceMacro.Pb_MeetVoteStatus.Pb_vote_voteing_VALUE:
                return context.getString(R.string.state_ongoing);
            case InterfaceMacro.Pb_MeetVoteStatus.Pb_vote_endvote_VALUE:
                return context.getString(R.string.state_has_ended);
            default:
                return context.getString(R.string.state_not_initiated);
        }
    }
}
