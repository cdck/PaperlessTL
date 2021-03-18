package com.xlk.paperlesstl.jni;

import android.content.Intent;

import com.blankj.utilcode.util.LogUtils;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.paperlesstl.model.Constant;
import com.xlk.paperlesstl.model.GlobalValue;
import com.xlk.paperlesstl.model.data.EventMessage;
import com.xlk.paperlesstl.model.data.EventType;

import org.greenrobot.eventbus.EventBus;

import static com.xlk.paperlesstl.App.lbm;

/**
 * @author Created by xlk on 2021/3/1.
 * @desc
 */
public class Call {
    private static Call mag;

    private Call() {

    }

    public static Call getInstance() {
        if (mag == null) {
            synchronized (Call.class) {
                if (mag == null) {
                    mag = new Call();
                }
            }
        }
        return mag;
    }

    //初始化无纸化接口
    //data 参考无纸化接口对照表
    //成功返回0 失败返回-1
    public native int Init_walletSys(byte[] data);

    //无纸化功能接口中调用
    //type 功能类型
    //method 功能类型的方法
    //data  方法需要的参数 参考无纸化接口对照表
    //成功返回对应的数组 失败返回null数组
    public native byte[] call_method(int type, int method, byte[] data);

    public native void enablebackgroud(int type);

    /**
     * 初始化桌面、摄像头采集
     *
     * @param type         流类型
     * @param channelindex 流通道索引值
     * @return 成功返回0 失败返回-1
     */
    public native int InitAndCapture(int type, int channelindex);

    //初始化桌面、摄像头采集
    //type 流类型
    //data 采集数据
    //成功返回0 失败返回-1
    public native int call(int type, int iskeyframe, long pts, byte[] data);

    public native byte[] NV21ToI420(byte[] data, int w, int h);

    public native byte[] NV21ToNV12(byte[] data, int w, int h);

    public native byte[] YV12ToNV12(byte[] data, int w, int h);

    //JNI获取桌面、摄像头的参数
    //type 流类型
    //oper 参数标识 用于区分获取的数据类型
    //成功返回操作属性对应的值 失败返回-1
    public static int COLOR_FORMAT = 0;

    public int callback(int type, int oper) {
        LogUtils.i("call", "callback type=" + type + ",oper=" + oper);
        switch (oper) {
            case 1://pixel format
            {
                if (type == 2) return 1;
                return COLOR_FORMAT;
            }
            case 2://width
            {
                switch (type) {
                    case 2:
                        return GlobalValue.screen_width;
                    case 3:
                        return GlobalValue.camera_width;
                }

            }
            case 3://height
            {
                switch (type) {
                    case 2:
                        return GlobalValue.screen_height;
                    case 3:
                        return GlobalValue.camera_height;
                }

            }
            case 4://start capture
            {
                LogUtils.i("Call", "callback -->" + "通知采集流 type= " + type);
                if (type == 2) {
                    Intent intent = new Intent();
                    intent.setAction(Constant.ACTION_START_SCREEN_RECORD);
                    intent.putExtra(Constant.EXTRA_CAPTURE_TYPE, type);
                    lbm.sendBroadcast(intent);
                } else if (type == 3) {
                    EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_COLLECT_CAMERA_START).objects(type).build());
                }
                return 0;
            }
            case 5://releaseMediaCodec capture
            {
                LogUtils.i("Call", "callback -->" + "通知停止采集流 type= " + type);
                if (type == 2) {
                    Intent intent = new Intent();
                    intent.setAction(Constant.ACTION_STOP_SCREEN_RECORD);
                    intent.putExtra(Constant.EXTRA_CAPTURE_TYPE, type);
                    lbm.sendBroadcast(intent);
                } else if (type == 3) {
                    EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_COLLECT_CAMERA_STOP).build());
                }
                return 0;
            }
            default:
                return 0;
        }
    }

    /**
     * 解码后的视频数据,这里对应的是YUV2420每个平面的大小,如{1920, 960, 960}
     *
     * @return
     */
    public int callback_yuvdisplay(int res, int w, int h, byte[] y, byte[] u, byte[] v) {
//        LogUtils.v("videodata", "callback_yuvdisplay -->" + "后台YUV数据");
        EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_YUV_DISPLAY).objects(res, w, h, y, u, v).build());
        return 0;
    }

    /**
     * @param res     播放资源ID
     * @param codecid 解码器类型 (h264=27,h265=173,mpeg4=12,vp8=139,vp9=167)
     * @param w       视频的宽
     * @param h       视频的高
     * @param packet
     */
    public int callback_videodecode(int isKeyframe, int res, int codecid, int w, int h, byte[] packet, long pts, byte[] codecdata) {
        if (packet != null) {
//            LogUtils.v("videodata", "callback_videodecode -->" + "后台videodecode数据");
            EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_VIDEO_DECODE)
                    .objects(isKeyframe, res, codecid, w, h, packet, pts, codecdata).build());
        }else {
//            LogUtils.e("videodata", "callback_videodecode -->" + "packet为空");
        }
        return 0;
    }

    public int callback_startdisplay(int res) {
        return 0;
    }

    public int callback_stopdisplay(int res) {
        return 0;
    }

    public void error_ret(int type, int method, int ret) {
        LogUtils.v("后台错误回调", "error_ret type=" + type + ",method=" + method + ",ret=" + ret);
    }

    //无纸化功能接口回调接口
    //type 功能类型
    //method 功能类型的方法
    //data  方法需要的参数 参考无纸化接口对照表
    //datalen data有数据时 datalen就有长度
    //返回0即可
    public int callback_method(int type, int method, byte[] data, int datalen) {
        if (type != 1) {
            LogUtils.v("后台回调", "callback_method type=" + type + ",method=" + method);
        }
        //升级包下载完成通知
        if (type == InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_UPDATE_VALUE) {
            EventBus.getDefault().postSticky(new EventMessage.Builder().type(type).method(method).objects(data, datalen).build());
        } else {
            EventBus.getDefault().post(new EventMessage.Builder().type(type).method(method).objects(data, datalen).build());
        }
        return 0;
    }
}
