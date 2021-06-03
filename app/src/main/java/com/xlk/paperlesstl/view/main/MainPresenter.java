package com.xlk.paperlesstl.view.main;

import android.graphics.drawable.Drawable;
import android.media.MediaCodecInfo;
import android.text.TextUtils;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceAdmin;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceContext;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceFaceconfig;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.xlk.paperlesstl.App;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.jni.Call;
import com.xlk.paperlesstl.model.Constant;
import com.xlk.paperlesstl.model.GlobalValue;
import com.xlk.paperlesstl.model.data.EventMessage;
import com.xlk.paperlesstl.model.data.EventType;
import com.xlk.paperlesstl.util.CodecUtil;
import com.xlk.paperlesstl.util.DateUtil;
import com.xlk.paperlesstl.view.base.BasePresenter;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.xlk.paperlesstl.App.appContext;

/**
 * @author Created by xlk on 2021/3/1.
 * @desc
 */
class MainPresenter extends BasePresenter<MainContract.View> implements MainContract.Presenter {

    private List<InterfaceMember.pbui_Item_MemberDetailInfo> members = new ArrayList<>();
    public List<InterfaceMember.pbui_Item_MemberDetailInfo> unbindMembers = new ArrayList<>();

    public MainPresenter(MainContract.View view) {
        super(view);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //平台登陆验证返回
            /*case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEVALIDATE_VALUE: {
                byte[] s = (byte[]) msg.getObjects()[0];
                InterfaceBase.pbui_Type_DeviceValidate deviceValidate = InterfaceBase.pbui_Type_DeviceValidate.parseFrom(s);
                int valflag = deviceValidate.getValflag();
                List<Integer> valList = deviceValidate.getValList();
                List<Long> user64BitdefList = deviceValidate.getUser64BitdefList();
                String binaryString = Integer.toBinaryString(valflag);
                LogUtils.i("initFailed valflag=" + valflag + "，二进制：" + binaryString + ", valList=" + valList.toString() + ", user64List=" + user64BitdefList.toString());
                int count = 0, index;
                //  1 1101 1111
                char[] chars = binaryString.toCharArray();
                for (int i = 0; i < chars.length; i++) {
                    if ((chars[chars.length - 1 - i]) == '1') {
                        //有效位个数+1
                        count++;
                        //有效位当前位于valList的索引（跟i是无关的）
                        index = count - 1;
                        int code = valList.get(index);
                        LogUtils.d("initFailed 有效位：" + i + ",当前有效位的个数：" + count);
                        switch (i) {
                            case 0:
                                LogUtils.e("initFailed 区域服务器ID：" + code);
                                break;
                            case 1:
                                LogUtils.e("initFailed 设备ID：" + code);
                                GlobalValue.localDeviceId = code;
                                break;
                            case 2:
                                LogUtils.e("initFailed 状态码：" + code);
                                initializationResult(code);
                                break;
                            case 3:
                                LogUtils.e("initFailed 到期时间：" + code);
                                break;
                            case 4:
                                LogUtils.e("initFailed 企业ID：" + code);
                                break;
                            case 5:
                                LogUtils.e("initFailed 协议版本：" + code);
                                break;
                            case 6:
                                LogUtils.e("initFailed 注册时自定义的32位整数值：" + code);
                                break;
                            case 7:
                                LogUtils.e("initFailed 当前在线设备数：" + code);
                                break;
                            case 8:
                                LogUtils.e("initFailed 最大在线设备数：" + code);
                                break;
                            default:
                                break;
                        }
                    }
                }
                break;
            }*/
            //平台初始化结果
            /*case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_READY_VALUE: {
                int method = msg.getMethod();
                byte[] bytes = (byte[]) msg.getObjects()[0];
                if (method == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_NOTIFY_VALUE) {
                    InterfaceBase.pbui_Ready error = InterfaceBase.pbui_Ready.parseFrom(bytes);
                    int areaid = error.getAreaid();
                    LogUtils.i(TAG, "BusEvent -->" + "平台初始化完毕 连接上的区域服务器ID=" + areaid);
                    initial();
                } else if (method == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_LOGON_VALUE) {
                    InterfaceBase.pbui_Type_LogonError error = InterfaceBase.pbui_Type_LogonError.parseFrom(bytes);
                    int errcode = error.getErrcode();
                    LogUtils.i(TAG, "BusEvent -->" + "平台初登陆失败通知 errcode=" + errcode);
                }
                break;
            }*/
            //平台初始化完成
            case EventType.BUS_INITIALIZED: {
                initial();
                break;
            }
            //时间回调
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_TIME_VALUE: {
                Object[] objs = msg.getObjects();
                byte[] data = (byte[]) objs[0];
                InterfaceBase.pbui_Time pbui_time = InterfaceBase.pbui_Time.parseFrom(data);
                //微秒 转换成毫秒 除以 1000
                String[] gtmDate = DateUtil.getGTMDate(pbui_time.getUsec() / 1000);
                mView.updateTime(gtmDate);
                break;
            }
            //主页logo图标下载完成
            case EventType.BUS_MAIN_LOGO: {
                String filepath = (String) msg.getObjects()[0];
                Drawable drawable = Drawable.createFromPath(filepath);
                mView.updateLogo(drawable);
                break;
            }
            //主页背景图片下载完成
            case EventType.BUS_MAIN_BG: {
                String filepath1 = (String) msg.getObjects()[0];
                Drawable drawable1 = Drawable.createFromPath(filepath1);
                mView.updateBackground(drawable1);
                break;
            }
            //设备会议信息变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEFACESHOW_VALUE: {
                LogUtils.i(TAG, "BusEvent -->" + "设备会议信息变更通知");
                queryDeviceMeetInfo();
                break;
            }
            //设备寄存器变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEINFO_VALUE: {
                byte[] bytes = (byte[]) msg.getObjects()[0];
                InterfaceDevice.pbui_Type_MeetDeviceBaseInfo info = InterfaceDevice.pbui_Type_MeetDeviceBaseInfo.parseFrom(bytes);
                int deviceid = info.getDeviceid();
                int attribid = info.getAttribid();
                LogUtils.i(TAG, "busEvent 设备寄存器变更通知 deviceid=" + deviceid + ",attribid=" + attribid);
                if (deviceid != 0 && deviceid == GlobalValue.localDeviceId) {
                    getSeatName();
                }
                break;
            }
            //参会人员变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER_VALUE: {
                byte[] o = (byte[]) msg.getObjects()[0];
                InterfaceBase.pbui_MeetNotifyMsg pbui_meetNotifyMsg = InterfaceBase.pbui_MeetNotifyMsg.parseFrom(o);
                int id = pbui_meetNotifyMsg.getId();
                int opermethod = pbui_meetNotifyMsg.getOpermethod();
                LogUtils.i(TAG, "BusEvent -->" + "参会人员变更通知 id= " + id + ", opermethod= " + opermethod);
                queryMember();
                break;
            }
            //界面配置变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETFACECONFIG_VALUE: {
                LogUtils.i(TAG, "BusEvent -->" + "界面配置变更通知");
                queryInterFaceConfiguration();
                break;
            }
            //会议排位变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSEAT_VALUE: {
                byte[] datas = (byte[]) msg.getObjects()[0];
                InterfaceBase.pbui_MeetNotifyMsg inform = InterfaceBase.pbui_MeetNotifyMsg.parseFrom(datas);
                LogUtils.d(TAG, "BusEvent -->" + "会议排位变更通知 id=" + inform.getId() + ",operMethod=" + inform.getOpermethod());
                if (inform.getId() != 0 && inform.getId() == GlobalValue.localDeviceId) {
                    queryLocalRole();
                }
                break;
            }
            //数据后台回复的错误信息
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DBSERVERERROR_VALUE: {
                byte[] bytes = (byte[]) msg.getObjects()[0];
                InterfaceBase.pbui_Type_MeetDBServerOperError info = InterfaceBase.pbui_Type_MeetDBServerOperError.parseFrom(bytes);
                if (info != null) {
                    int type = info.getType();
                    int method = info.getMethod();
                    int status = info.getStatus();
                    LogUtils.e(TAG, "数据后台回复的错误信息 type=" + type + ",method=" + method + ",status=" + status);
                    if (type == 8 && method == 10) {
                        //管理员登录
                        mView.updateLoginStatus(status);
                    }
                }
                break;
            }
            //管理员登录返回
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ADMIN_VALUE: {
                if (msg.getMethod() == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_LOGON_VALUE) {
                    LogUtils.i(TAG, "BusEvent 管理员登录返回");
                    byte[] bytes = (byte[]) msg.getObjects()[0];
                    InterfaceAdmin.pbui_Type_AdminLogonStatus info = InterfaceAdmin.pbui_Type_AdminLogonStatus.parseFrom(bytes);
                    if (info != null) {
                        mView.loginStatus(info);
                    }
                }
                break;
            }
            //会议签到结果返回
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSIGN_VALUE: {
                if (msg.getMethod() == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_LOGON_VALUE) {
                    //签到密码返回
                    byte[] datas = (byte[]) msg.getObjects()[0];
                    InterfaceBase.pbui_Type_MeetDBServerOperError dbServerOperError = InterfaceBase.pbui_Type_MeetDBServerOperError.parseFrom(datas);
                    int type = dbServerOperError.getType();
                    int method1 = dbServerOperError.getMethod();
                    int status = dbServerOperError.getStatus();
                    if (status == InterfaceMacro.Pb_DB_StatusCode.Pb_STATUS_DONE_VALUE) {
                        LogUtils.i(TAG, "BusEvent -->" + "签到成功，进入会议");
                        ToastUtils.showShort(R.string.sign_in_successfully);
                        mView.jump2meet();
                    } else if (status == InterfaceMacro.Pb_DB_StatusCode.Pb_STATUS_PSWFAILED_VALUE) {
                        LogUtils.i(TAG, "BusEvent -->" + "签到密码错误");
                        ToastUtils.showShort(R.string.sign_in_password_error);
                        mView.readySignIn();
                    }
                }
                break;
            }
            //设备交互信息
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEOPER_VALUE: {
                if (msg.getMethod() == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ENTER_VALUE) {
                    LogUtils.i(TAG, "BusEvent -->" + "辅助签到进入会议");
                    mView.jump2meet();
                }
                break;
            }
            //升级包下载完成通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_UPDATE_VALUE: {
                byte[] bytes = (byte[]) msg.getObjects()[0];
                InterfaceBase.pbui_Type_MeetUpdateNotify info = InterfaceBase.pbui_Type_MeetUpdateNotify.parseFrom(bytes);
                int localhardver = info.getLocalhardver();
                int localsoftver = info.getLocalsoftver();
                int newhardver = info.getNewhardver();
                int newsoftver = info.getNewsoftver();
                int newminhardver = info.getNewminhardver();
                int newminsoftver = info.getNewminsoftver();
                // InterfaceMacro.Pb_ProgramType.Pb_MEET_PROGRAM_TYPE_MEETCLIENT
                int deviceidtype = info.getDeviceidtype();
                int devicemask = info.getDevicemask();
                String updatepath = info.getUpdatepath().toStringUtf8();
                LogUtils.i(TAG, "busEvent 升级包下载完成通知：\nupdatepath=" + updatepath
                        + "\n当前版本=" + localhardver + "." + localsoftver
                        + "\n新版本=" + newhardver + "." + newsoftver
                        + "\n最小版本号=" + newminhardver + "." + newminsoftver
                        + "\ndeviceidtype=" + deviceidtype + "，devicemask=" + devicemask
                );
                if (newhardver > localhardver || newsoftver > localsoftver) {
                    LogUtils.d(TAG, "busEvent 有新版本更新");
//                    File apkFile = new File(updatepath + "/update.apk");
                    File docFile = new File(updatepath + "/无纸化标准版更新说明.txt");
                    String content = FileIOUtils.readFile2String(docFile);
                    new Thread(() -> {
                        try {
                            //粘性信息的情况下：可能Activity还没有完全创建
                            Thread.sleep(1000);
                            if (mView != null) {
                                mView.showUpgradeDialog(content, info);
                                EventBus.getDefault().removeStickyEvent(msg);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void initialization(String uuid) {
        if (GlobalValue.initializationIsOver) {
            initial();
        } else {
            App.threadPool.execute(() -> jni.javaInitSys(uuid));
        }
    }

    private void initializationResult(int code) {
        String msg;
        switch (code) {
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_NONE_VALUE:
                msg = appContext.getString(R.string.error_0);
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_EXPIRATION_VALUE:
                msg = appContext.getString(R.string.error_1);
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_OPER_VALUE:
                msg = appContext.getString(R.string.error_2);
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_ENTERPRISE_VALUE:
                msg = appContext.getString(R.string.error_3);
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_NODEVICEID_VALUE:
                msg = appContext.getString(R.string.error_4);
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_NOALLOWIN_VALUE:
                msg = appContext.getString(R.string.error_5);
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_FILEERROR_VALUE:
                msg = appContext.getString(R.string.error_6);
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_INVALID_VALUE:
                msg = appContext.getString(R.string.error_7);
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_IDOCCUPY_VALUE:
                msg = appContext.getString(R.string.error_8);
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_NOTBEING_VALUE:
                msg = appContext.getString(R.string.error_9);
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_ONLYDEVICEID_VALUE:
                msg = appContext.getString(R.string.error_10);
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_DEVICETYPENOMATCH_VALUE:
                msg = appContext.getString(R.string.error_11);
                break;
            default:
                msg = "";
                break;
        }
        if (!msg.isEmpty()) {
            ToastUtils.showShort(msg);
        }
        //平台初始化成功
        if (code == InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_NONE_VALUE) {
            GlobalValue.initializationIsOver = true;
            initial();
        }
    }

    private void initial() {
        LogUtils.i(TAG, "initial ");
        setInterfaceStatus();
        initStream();
        cacheData();
        getSeatName();
        queryDeviceMeetInfo();
        queryMember();
        queryInterFaceConfiguration();
    }

    private void cacheData() {
        InterfaceContext.pbui_MeetContextInfo info = jni.queryContextProperty(InterfaceMacro.Pb_ContextPropertyID.Pb_MEETCONTEXT_PROPERTY_SELFID_VALUE);
        if (info != null) {
            int propertyval = info.getPropertyval();
            LogUtils.e("查询本机设备id=" + propertyval);
        }
        //缓存参会人信息(不然收不到参会人变更通知)
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER_VALUE);
        //缓存会议信息
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETINFO_VALUE);
        //缓存排位信息(不然收不到排位变更通知)
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSEAT_VALUE);
        //缓存界面配置信息
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETFACECONFIG_VALUE);
    }

    @Override
    public void setInterfaceStatus() {
        //  修改本机界面状态
        jni.modifyContextProperties(InterfaceMacro.Pb_ContextPropertyID.Pb_MEETCONTEXT_PROPERTY_ROLE_VALUE,
                InterfaceMacro.Pb_MeetFaceStatus.Pb_MemState_MainFace_VALUE);
    }

    @Override
    public void initStream() {
        int format = CodecUtil.selectColorFormat(Objects.requireNonNull(CodecUtil.selectCodec("video/avc")), "video/avc");
        switch (format) {
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar:
                Call.COLOR_FORMAT = 0;
                break;
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar:
                Call.COLOR_FORMAT = 1;
                break;
            default:
                break;
        }
        jni.InitAndCapture(0, 2);
        jni.InitAndCapture(0, 3);
    }

    @Override
    public void getSeatName() {
        if (GlobalValue.localDeviceId != 0) {
            InterfaceDevice.pbui_Item_DeviceDetailInfo dev = jni.queryDevInfoById(GlobalValue.localDeviceId);
            if (dev != null) {
                mView.updateSeatName(appContext.getString(R.string.seat_) + dev.getDevname().toStringUtf8());
            }
        }
    }

    @Override
    public void queryInterFaceConfiguration() {
        InterfaceFaceconfig.pbui_Type_FaceConfigInfo pbui_type_faceConfigInfo = jni.queryInterFaceConfiguration();
        if (pbui_type_faceConfigInfo == null) return;
        List<InterfaceFaceconfig.pbui_Item_FaceOnlyTextItemInfo> onlytextList = pbui_type_faceConfigInfo.getOnlytextList();
        List<InterfaceFaceconfig.pbui_Item_FacePictureItemInfo> pictureList = pbui_type_faceConfigInfo.getPictureList();
        List<InterfaceFaceconfig.pbui_Item_FaceTextItemInfo> textList = pbui_type_faceConfigInfo.getTextList();
        for (int i = 0; i < pictureList.size(); i++) {
            InterfaceFaceconfig.pbui_Item_FacePictureItemInfo pic = pictureList.get(i);
            int faceid = pic.getFaceid();
            int mediaid = pic.getMediaid();
            LogUtils.i("queryInterFaceConfiguration pictureList faceid=" + faceid);
            String userStr = "";
            //主界面背景
            if (faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_MAINBG_VALUE) {
                userStr = Constant.MAIN_BG_PNG_TAG;
                //logo图标
            } else if (faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_LOGO_VALUE) {
                userStr = Constant.MAIN_LOGO_PNG_TAG;
            }
            if (!TextUtils.isEmpty(userStr)) {
                FileUtils.createOrExistsDir(Constant.DIR_PICTURE);
                jni.creationFileDownload(Constant.DIR_PICTURE + userStr + ".png", mediaid, 1, 0, userStr);
            }
        }
        for (int i = 0; i < onlytextList.size(); i++) {
            InterfaceFaceconfig.pbui_Item_FaceOnlyTextItemInfo itemInfo = onlytextList.get(i);
            int faceid = itemInfo.getFaceid();
            LogUtils.i(TAG, "queryInterFaceConfiguration onlytextList faceid=" + faceid);
            String text = itemInfo.getText().toStringUtf8();
            //公司名称
            if (faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_COLTDTEXT_VALUE) {
                mView.updateCompany(text);
                break;
            }
        }
        for (int i = 0; i < textList.size(); i++) {
            InterfaceFaceconfig.pbui_Item_FaceTextItemInfo itemInfo = textList.get(i);
            int faceid = itemInfo.getFaceid();
            LogUtils.i(TAG, "queryInterFaceConfiguration textList faceid=" + faceid);
            if (faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_MEETNAME_VALUE) {//会议名称
                mView.updateTv(R.id.meet_tv_main, itemInfo);
            } else if (faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_MEMBERCOMPANY_VALUE) {//参会人单位
                mView.updateTv(R.id.unit_tv_main, itemInfo);
            } else if (faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_MEMBERNAME_VALUE) {//参会人名称
                mView.updateTv(R.id.member_tv_main, itemInfo);
            } else if (faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_MEMBERJOB_VALUE) {//参会人职业
                mView.updateTv(R.id.post_tv_main, itemInfo);
            } else if (faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_SEATNAME_VALUE) {//座席名称
                mView.updateTv(R.id.seat_tv_main, itemInfo);
            } else if (faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_TIMER_VALUE) {//日期时间
                mView.updateDate(R.id.date_relative_main, itemInfo);
            } else if (faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_COMPANY_VALUE) {//单位名称
                mView.updateTv(R.id.company_tv_main, itemInfo);
            } else if (faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACE_LOGO_GEO_VALUE) {//Logo图标,只需要更新位置坐标
                mView.update(R.id.logo_iv_main, itemInfo);
                boolean isShow = (InterfaceMacro.Pb_MeetFaceFlag.Pb_MEET_FACEFLAG_SHOW_VALUE ==
                        (itemInfo.getFlag() & InterfaceMacro.Pb_MeetFaceFlag.Pb_MEET_FACEFLAG_SHOW_VALUE));
                mView.isShowLogo(isShow);
            } else if (faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACE_checkin_GEO_VALUE) {//进入会议按钮 text
//                    view.updateEnterView(R.id.slideview_main, itemInfo);
                mView.updateBtn(R.id.enter_btn_main, itemInfo);
            } else if (faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACE_manage_GEO_VALUE) {//进入后台 text
//                    mView.updateBtn(R.id.set_btn_main, itemInfo);
            } else if (faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACE_topstatus_GEO_VALUE) {//会议状态
//                mView.updateTv(R.id.meet_state, itemInfo);
            } else if (faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACE_remark_GEO_VALUE) {//备注信息
//                mView.updateTv(R.id.note_info, itemInfo);
            } else if (faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACE_ver_GEO_VALUE) {//软件版本
                mView.updateTv(R.id.app_version, itemInfo);
            } else if (faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACE_role_GEO_VALUE) {//角色
//                mView.updateTv(R.id.member_role, itemInfo);
            }
        }
    }

    @Override
    public void queryDeviceMeetInfo() {
        InterfaceDevice.pbui_Type_DeviceFaceShowDetail devMeetInfo = jni.queryDeviceMeetInfo();
        if (devMeetInfo == null) {
            return;
        }
        LogUtils.e(devMeetInfo.getDeviceid() + ",会议ID=" + devMeetInfo.getMeetingid() + ",人员ID=" + devMeetInfo.getMemberid() + ",会场ID=" + devMeetInfo.getRoomid()
                + "\n会议名称=" + devMeetInfo.getMeetingname().toStringUtf8() + ",人员名称=" + devMeetInfo.getMembername().toStringUtf8() + ",公司名称=" + devMeetInfo.getCompany().toStringUtf8() +
                "职位名称=" + devMeetInfo.getJob().toStringUtf8());
        mView.updateUI(devMeetInfo);
        GlobalValue.localMeetingId = devMeetInfo.getMeetingid();
        GlobalValue.localMeetingName = devMeetInfo.getMeetingname().toStringUtf8();
        GlobalValue.localMemberId = devMeetInfo.getMemberid();
        GlobalValue.localMemberName = devMeetInfo.getMembername().toStringUtf8();
        GlobalValue.localRoomId = devMeetInfo.getRoomid();
        GlobalValue.signinType = devMeetInfo.getSigninType();
        cacheData();
        if (GlobalValue.localMeetingId != 0) {
            queryMeetingState();
        } else {
            mView.updateMeetingState(-1);
        }
        if (GlobalValue.localMemberId != 0) {
            //查询参会人单位
            InterfaceMember.pbui_Type_MeetMembeProperty info = jni.queryMemberProperty(InterfaceMacro.Pb_MemberPropertyID.Pb_MEETMEMBER_PROPERTY_COMPANY_VALUE, 0);
            if (info != null) {
                String unit = info.getPropertytext().toStringUtf8();
                LogUtils.d(TAG, "queryDevMeetInfo --> 单位：" + unit);
                mView.updateUnit(unit);
            }
            //查询备注
            InterfaceMember.pbui_Type_MeetMembeProperty info1 = jni.queryMemberProperty(InterfaceMacro.Pb_MemberPropertyID.Pb_MEETMEMBER_PROPERTY_COMMENT_VALUE, 0);
            if (info1 != null) {
                String noteinfo = info1.getPropertytext().toStringUtf8();
                LogUtils.d(TAG, "queryDevMeetInfo --> 备注：" + noteinfo);
                mView.updateNote(appContext.getString(R.string.note_info_, noteinfo));
            }
            queryLocalRole();
        } else {
            mView.updateMemberRole("");
            mView.updateNote("");
            mView.updateUnit("");
        }
    }

    public void queryMeetingState() {
        byte[] bytes = jni.queryMeetingProperty(InterfaceMacro.Pb_MeetPropertyID.Pb_MEET_PROPERTY_STATUS_VALUE, GlobalValue.localMeetingId, 0);
        if (bytes == null) {
            mView.updateMeetingState(-1);
            return;
        }
        try {
            InterfaceBase.pbui_CommonInt32uProperty info = InterfaceBase.pbui_CommonInt32uProperty.parseFrom(bytes);
            int propertyval = info.getPropertyval();
            LogUtils.i(TAG, "queryMeetingState -->" + "会议状态：" + propertyval);
            mView.updateMeetingState(propertyval);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    private void queryLocalRole() {
//            InterfaceRoom.pbui_Type_MeetSeatDetailInfo info = jni.queryMeetRanking();
//            if (info != null) {
//                List<InterfaceRoom.pbui_Item_MeetSeatDetailInfo> itemList = info.getItemList();
//                for (int i = 0; i < itemList.size(); i++) {
//                    InterfaceRoom.pbui_Item_MeetSeatDetailInfo pbui_item_meetSeatDetailInfo = itemList.get(i);
//                    int seatid = pbui_item_meetSeatDetailInfo.getSeatid();
//                    int nameId = pbui_item_meetSeatDetailInfo.getNameId();
//                    int role = pbui_item_meetSeatDetailInfo.getRole();
//                    if (seatid == localDeviceId && nameId == localMemberId) {
//                        Values.localRole = role;
//                        LogUtils.i(TAG, "queryLocalRole 本机参会人角色 " + role);
//                        if (role == InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_compere_VALUE
//                                || role == InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_secretary_VALUE
//                                || role == InterfaceMacro.Pb_MeetMemberRole.Pb_role_admin_VALUE) {
//                            //当前是主持人或秘书或管理员，设置拥有所有权限
//                            Values.hasAllPermissions = true;
//                            if (role == InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_compere_VALUE) {
//                                mView.updateMemberRole(appContext.getString(R.string.member_role_host));
//                            } else if (role == InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_secretary_VALUE) {
//                                mView.updateMemberRole(appContext.getString(R.string.member_role_secretary));
//                            } else {
//                                mView.updateMemberRole(appContext.getString(R.string.member_role_admin));
//                            }
//                        } else {
//                            Values.hasAllPermissions = false;
//                            mView.updateMemberRole(appContext.getString(R.string.member_role_ordinary));
//                        }
//                    }
//                }
//            }
        InterfaceBase.pbui_CommonInt32uProperty property = jni.queryMeetRankingProperty(
                InterfaceMacro.Pb_MeetSeatPropertyID.Pb_MEETSEAT_PROPERTY_ROLEBYMEMBERID_VALUE);
        if (property == null) {
            return;
        }
        int propertyval = property.getPropertyval();
        GlobalValue.localRole = propertyval;
        LogUtils.i(TAG, "queryLocalRole 本机参会人角色 " + propertyval);
        if (propertyval == InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_compere_VALUE
                || propertyval == InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_secretary_VALUE
                || propertyval == InterfaceMacro.Pb_MeetMemberRole.Pb_role_admin_VALUE) {
            //当前是主持人或秘书或管理员，设置拥有所有权限
            GlobalValue.hasAllPermissions = true;
            if (propertyval == InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_compere_VALUE) {
                mView.updateMemberRole(appContext.getString(R.string.member_role_host));
            } else if (propertyval == InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_secretary_VALUE) {
                mView.updateMemberRole(appContext.getString(R.string.member_role_secretary));
            } else {
                mView.updateMemberRole(appContext.getString(R.string.member_role_admin));
            }
        } else {
            GlobalValue.hasAllPermissions = false;
            mView.updateMemberRole(appContext.getString(R.string.member_role_ordinary));
        }
    }

    @Override
    public void queryMember() {
        InterfaceMember.pbui_Type_MemberDetailInfo attendPeople = jni.queryMember();
        members.clear();
        if (attendPeople != null) {
            members.addAll(attendPeople.getItemList());
        }
        queryMeetRanking();
    }

    public void queryMeetRanking() {
        InterfaceRoom.pbui_Type_MeetSeatDetailInfo seatDetailInfo = jni.queryMeetRanking();
        unbindMembers.clear();
        if (seatDetailInfo != null) {
            List<Integer> ids = new ArrayList<>();
            List<InterfaceRoom.pbui_Item_MeetSeatDetailInfo> itemList = seatDetailInfo.getItemList();
            for (int i = 0; i < itemList.size(); i++) {
                InterfaceRoom.pbui_Item_MeetSeatDetailInfo item = itemList.get(i);
                if (item.getNameId() != 0) {
                    ids.add(item.getNameId());
                }
            }
            for (int i = 0; i < members.size(); i++) {
                InterfaceMember.pbui_Item_MemberDetailInfo item = members.get(i);
                if (!ids.contains(item.getPersonid())) {
                    unbindMembers.add(item);
                }
            }
        }
        mView.updateUnBindMember();
    }
}
