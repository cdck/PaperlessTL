package com.xlk.paperlesstl.ui.video;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Range;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.blankj.utilcode.util.LogUtils;
import com.xlk.paperlesstl.App;
import com.xlk.paperlesstl.model.data.EventMessage;
import com.xlk.paperlesstl.model.data.EventType;
import com.xlk.paperlesstl.view.admin.bean.MediaBean;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * @author xlk
 * @date 2019/7/1
 */
public class MyGLSurfaceView extends GLSurfaceView {

    private final String TAG = "MyGLSurfaceView-->";
    private String saveMimeType = "";
    private MediaCodec mediaCodec;
    private MediaFormat mediaFormat;
    private MediaCodec.BufferInfo info;
    private Surface surface;
    private int initW;
    private int initH;
    private MyWlGlRender glRender;
    private int resId;
    private long lastPushTime;
    private boolean isStop = false;
    private releaseThread timeThread;

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        super.surfaceChanged(holder, format, w, h);
    }

    public MyGLSurfaceView(Context context) {
        this(context, null);
    }

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
        initRender();
    }

    private void initRender() {
        LogUtils.i(TAG, "initRender -->");
        glRender = new MyWlGlRender(getContext());
        setRenderer(glRender);
        //???????????????????????????  RENDERMODE_CONTINUOUSLY ?????????????????????
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        glRender.setWlOnRenderRefreshListener(new WlOnRenderRefreshListener() {
            @Override
            public void onRefresh() {
                requestRender();
            }
        });
    }

    public void setCodecType(int codecType) {
        if (glRender == null) {
            LogUtils.i(TAG, "setCodecType -->glRender???null");
            initRender();
        }
        glRender.setCodecType(codecType);
        requestRender();
    }

    public void setFrameData(int w, int h, byte[] y, byte[] u, byte[] v) {
        glRender.setFrameData(w, h, y, u, v);
        requestRender();
    }

    public void setOnGlSurfaceViewOncreateListener(WlOnGlSurfaceViewOncreateListener onGlSurfaceViewOncreateListener) {
        if (glRender != null) {
            glRender.setWlOnGlSurfaceViewOncreateListener(onGlSurfaceViewOncreateListener);
        }
    }

    public Surface getSurface() {
        return this.surface;
    }

    public void setSurface(Surface surface) {
        this.surface = surface;
    }

    public void cutVideoImg() {
        if (glRender != null) {
            glRender.cutVideoImg();
            requestRender();
        }
    }

    public void initCodec(String mimeType, int w, int h, byte[] codecdata) {
        if (!saveMimeType.equals(mimeType) || initW != w || initH != h || mediaCodec == null) {
            if (mediaCodec != null) {
                //??????stop?????????????????? uninitialzed ????????????????????????????????????MediaCodec
                mediaCodec.stop();
            }
            saveMimeType = mimeType;
            try {
                //1.???????????????????????????????????????????????????????????????????????????Uninitialized???
                mediaCodec = MediaCodec.createDecoderByType(saveMimeType);
                /**  ???????????????????????????????????????????????????  */
                MediaCodecInfo.VideoCapabilities videoCapabilities = mediaCodec.getCodecInfo().getCapabilitiesForType(saveMimeType).getVideoCapabilities();
                Range<Integer> supportedWidths = videoCapabilities.getSupportedWidths();
                Integer upper = supportedWidths.getUpper();
                Integer lower = supportedWidths.getLower();
                Range<Integer> supportedHeights = videoCapabilities.getSupportedHeights();
                Integer upper1 = supportedHeights.getUpper();
                Integer lower1 = supportedHeights.getLower();
                initW = w;
                initH = h;
                LogUtils.i(TAG, "initCodec -->" + "w= " + w + ", h= " + h);
                if (w > upper) {
                    w = upper;
                } else if (w < lower) {
                    w = lower;
                }
                if (h > upper1) {
                    h = upper1;
                } else if (h < lower1) {
                    h = lower1;
                }
                initMediaFormat(w, h, codecdata);
                info = new MediaCodec.BufferInfo();
                LogUtils.i(TAG, "initCodec -->configure surface????????????: " + (surface == null));
                //2.????????????????????????????????????????????????????????????????????????Configured???
                mediaCodec.configure(mediaFormat, surface, null, 0);
                //3.??????start()?????????????????????????????????Executing???
                mediaCodec.start();
                //?????????????????????????????????????????????configure???start?????????????????????
                mediaCodec.setVideoScalingMode(MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initMediaFormat(int w, int h, byte[] codecdata) {
        LogUtils.d(TAG, "initMediaFormat :   -->mediaFormat??????????????? " + (mediaFormat == null) + ", w= " + w + ", h= " + h);
        mediaFormat = MediaFormat.createVideoFormat(saveMimeType, w, h);
        mediaFormat.setInteger(MediaFormat.KEY_WIDTH, w);
        mediaFormat.setInteger(MediaFormat.KEY_HEIGHT, h);
        //????????????????????????
        mediaFormat.setLong(MediaFormat.KEY_MAX_INPUT_SIZE, w * h);
        if (codecdata != null) {
            mediaFormat.setByteBuffer("csd-0", ByteBuffer.wrap(codecdata));
            mediaFormat.setByteBuffer("csd-1", ByteBuffer.wrap(codecdata));
        }
    }

    LinkedBlockingQueue<MediaBean> queue = new LinkedBlockingQueue<>();

    public void mediaCodecDecode(byte[] bytes, long pts, int iskeyframe) {
        if (isStop) return;
        if (bytes != null && bytes.length > 0) {
            //??????????????????????????????????????????????????????
            queue.offer(new MediaBean(bytes, bytes.length, pts, iskeyframe));
        } else {
            //bytes???null????????????????????????????????????????????????????????????????????????buffer ??? ???????????????????????????
        }
        int queuesize = queue.size();
//        LogUtils.i(TAG, " mediaCodecDecode -->queuesize: " + queuesize);
        if (queuesize > 500) {
            //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            //??????????????????I???P????????????????????????????????????????????????
            int keyframenum = 0;
            MediaBean poll;
            //???????????????????????????????????????
            for (int ni = 0; ni < queuesize; ++ni) {
                poll = queue.peek();
                if (poll.getIskeyframe() == 1)
                    keyframenum++;
            }
            for (int ni = 0; ni < queuesize; ++ni) {
                poll = queue.peek();
                if (poll.getIskeyframe() == 1) {
                    keyframenum--;
                    if (keyframenum < 2) {
                        //???????????????????????????????????????????????????????????????????????????????????????????????????
                        break;
                    }
                }
//                LogUtils.e(TAG, "mediaCodecDecode ????????????????????? -->");
                //?????????????????????,?????????
                queue.poll();
            }
            //????????????????????????
            queuesize = queue.size();
        }
        //????????????????????????????????????
        if (mediaCodec == null)
            return;
        //?????????????????????????????????????????????????????????????????????buffer????????????????????????????????????
        if (queuesize > 0) {
            int inputBufferIndex = -1;
            try {
                inputBufferIndex = mediaCodec.dequeueInputBuffer(0);
                if (inputBufferIndex >= 0) {
                    //????????????????????????buffer
                    ByteBuffer byteBuffer = mediaCodec.getInputBuffer(inputBufferIndex);
                    byteBuffer.clear();
                    //???????????????????????????????????????????????????
                    MediaBean poll = queue.poll();
                    byteBuffer.put(poll.getBytes());
                    mediaCodec.queueInputBuffer(inputBufferIndex, 0, poll.getSize(), poll.getPts(), 0);
                }
            } catch (IllegalStateException e) {
                //??????????????????????????????????????????????????????????????????????????????
                mediaCodec = null;
                return;
            }
        }
        //??????????????????buffer?????????????????????
        if (info == null)
            return;
        try {
            int index = mediaCodec.dequeueOutputBuffer(info, 0);
            if (index >= 0) {
                ByteBuffer outputBuffer = mediaCodec.getOutputBuffer(index);
                outputBuffer.position(info.offset);
                outputBuffer.limit(info.offset + info.size);
                //mediaCodec.releaseOutputBuffer(index, info.presentationTimeUs);
                //??????????????????????????????????????????surface??????true??????????????????????????????surface
                mediaCodec.releaseOutputBuffer(index, true);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        isStop = true;
        timeThread = null;
        releaseMediaCodec();
        this.surfaceDestroyed(this.getHolder());
        this.destroyDrawingCache();
        if (glRender != null) {
            glRender.glClear();
            glRender.destory();
            glRender.setWlOnGlSurfaceViewOncreateListener(null);
            glRender.setWlOnRenderRefreshListener(null);
            glRender = null;
        }
    }

    /**
     * ????????????
     */
    private void releaseMediaCodec() {
        App.threadPool.execute(()->{
            if (mediaCodec != null) {
                try {
                    LogUtils.e(TAG, "releaseMediaCodec :   --> ");
                    mediaCodec.reset();
                    //??????stop()???????????????????????????????????????????????????Uninitialized??????????????????????????????????????????????????????
                    mediaCodec.stop();
                    //??????flush()??????????????????????????????????????????????????????Flushed???
                    mediaCodec.flush();
                    //??????????????????????????????????????????release()?????????????????????
                    mediaCodec.release();
                } catch (MediaCodec.CodecException e) {
                    LogUtils.e(TAG, "run :  CodecException --> " + e.getMessage());
                } catch (IllegalStateException e) {
                    LogUtils.e(TAG, "run :  IllegalStateException --> " + e.getMessage());
                } catch (Exception e) {
                    LogUtils.e(TAG, "run :  Exception --> " + e.getMessage());
                }
            }
            mediaCodec = null;
            mediaFormat = null;
        });
    }

    public void setResId(int resid) {
        this.resId = resid;
    }

    public int getResId() {
        return resId;
    }

    public void setLastPushTime(long timeMillis) {
        this.lastPushTime = timeMillis;
    }

    public void startTimeThread() {
        isStop = false;
        if (timeThread == null && !isStop) {
            timeThread = new releaseThread();
            timeThread.start();
        }
    }

    public void stopTimeThread() {
        isStop = true;
        if (timeThread != null) {
            timeThread = null;
        }
        releaseMediaCodec();
    }

    long framepersecond = 80;//??????????????????????????? ???????????????

    class releaseThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!isStop) {
                if (System.currentTimeMillis() - lastPushTime >= framepersecond) {
                    LogUtils.v(TAG, "releaseThread ????????????????????? -->");
                    EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_VIDEO_DECODE).objects(0, resId, 0, 0, 0, null, 1L, null).build());
                    try {
                        sleep(25);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
