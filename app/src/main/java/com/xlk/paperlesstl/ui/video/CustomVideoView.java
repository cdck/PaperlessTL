package com.xlk.paperlesstl.ui.video;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.LogUtils;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.model.Constant;

import java.util.ArrayList;
import java.util.List;


/**
 * @author xlk
 * @date 2020/3/19
 * @desc
 */
public class CustomVideoView extends ViewGroup {
    private final String TAG = "CustomVideoView-->";
    private final Context cxt;
    private int widthMeasureSpec;
    private int heightMeasureSpec;
    private ViewClickListener mListener;
    private boolean isEnlarge;//当前是否是最大的界面
    private List<MyGLSurfaceView> shrinkSfvs = new ArrayList<>();
    List<Integer> resids = new ArrayList<>();
    private int largeResId=-1;

    public CustomVideoView(Context context) {
        this(context, null);
    }

    public CustomVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.cxt = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.widthMeasureSpec = widthMeasureSpec;
        this.heightMeasureSpec = heightMeasureSpec;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        measureChild(getWidth(), getHeight());
    }

    private void measureChild(int parentWidth, int parentHeight) {
        LogUtils.i(TAG, "measureChild -->viewGroup的宽高：" + parentWidth + ", " + parentHeight);
        View videoView1 = getChildAt(0);
        View videoView2 = getChildAt(1);
        View videoView3 = getChildAt(2);
        View videoView4 = getChildAt(3);
        LayoutParams params = new LayoutParams(parentWidth, parentHeight);
        LayoutParams params2_1 = new LayoutParams(parentWidth / 2, parentHeight / 2);
        LayoutParams params1_1 = new LayoutParams(1, 1);
        switch (getChildCount()) {
            case 1:
                videoView1.setLayoutParams(params);
                break;
            case 4:
                if (largeResId == -1) {
                    videoView1.setLayoutParams(params2_1);
                    videoView2.setLayoutParams(params2_1);
                    videoView3.setLayoutParams(params2_1);
                    videoView4.setLayoutParams(params2_1);
                } else if (largeResId == 1) {
                    videoView1.setLayoutParams(params);
                    videoView2.setLayoutParams(params1_1);
                    videoView3.setLayoutParams(params1_1);
                    videoView4.setLayoutParams(params1_1);
                } else if (largeResId == 2) {
                    videoView1.setLayoutParams(params1_1);
                    videoView2.setLayoutParams(params);
                    videoView3.setLayoutParams(params1_1);
                    videoView4.setLayoutParams(params1_1);
                } else if (largeResId == 3) {
                    videoView1.setLayoutParams(params1_1);
                    videoView2.setLayoutParams(params1_1);
                    videoView3.setLayoutParams(params);
                    videoView4.setLayoutParams(params1_1);
                } else if (largeResId == 4) {
                    videoView1.setLayoutParams(params1_1);
                    videoView2.setLayoutParams(params1_1);
                    videoView3.setLayoutParams(params1_1);
                    videoView4.setLayoutParams(params);
                }
//                videoView1.setLayoutParams(params2_1);
//                videoView2.setLayoutParams(params2_1);
//                videoView3.setLayoutParams(params2_1);
//                videoView4.setLayoutParams(params2_1);
                break;
            default:
        }
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //注意：左、上、右、下  表示的是与原点（0,0）的距离不是与该方向的距离
        LogUtils.i(TAG, "onLayout :   --> l=" + l + ", t=" + t + ", r=" + r + ", b=" + b
                + ", getChildCount= " + getChildCount() + ", 宽高：" + getWidth() + "," + getHeight());
        switch (getChildCount()) {
            case 1:
                layout1();
                break;
            case 4:
                layout4();
                break;
        }
    }

    private void layout1() {
        MyGLSurfaceView glSurfaceView = (MyGLSurfaceView) getChildAt(0);
        int measuredWidth = glSurfaceView.getMeasuredWidth();
        int measuredHeight = glSurfaceView.getMeasuredHeight();
        LogUtils.d(TAG, "layout1 :   --> " + measuredWidth + "," + measuredHeight);
        glSurfaceView.layout(0, 0, measuredWidth, measuredHeight);
        glSurfaceView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.click(glSurfaceView.getResId());
                }
            }
        });
    }

    private void layout4() {
        MyGLSurfaceView glSurfaceView1 = (MyGLSurfaceView) getChildAt(0);
        int measuredWidth = glSurfaceView1.getMeasuredWidth();
        int measuredHeight = glSurfaceView1.getMeasuredHeight();
        LogUtils.d(TAG, "layout4 0:   --> " + measuredWidth + "," + measuredHeight);
        glSurfaceView1.layout(0, 0, measuredWidth, measuredHeight);
        glSurfaceView1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.click(glSurfaceView1.getResId());
                }
            }
        });

        MyGLSurfaceView glSurfaceView2 = (MyGLSurfaceView) getChildAt(1);
        int measuredWidth1 = glSurfaceView2.getMeasuredWidth();
        int measuredHeight1 = glSurfaceView2.getMeasuredHeight();
        LogUtils.d(TAG, "layout4 1:   --> " + measuredWidth + "," + measuredHeight);
        glSurfaceView2.layout(measuredWidth, 0, measuredWidth + measuredWidth1, measuredHeight1);
        glSurfaceView2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.click(glSurfaceView2.getResId());
                }
            }
        });

        MyGLSurfaceView glSurfaceView3 = (MyGLSurfaceView) getChildAt(2);
        int measuredWidth2 = glSurfaceView3.getMeasuredWidth();
        int measuredHeight2 = glSurfaceView3.getMeasuredHeight();
        LogUtils.d(TAG, "layout4 2:   --> " + measuredWidth + "," + measuredHeight);
        glSurfaceView3.layout(0, measuredHeight, measuredWidth2, measuredHeight + measuredHeight2);
        glSurfaceView3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.click(glSurfaceView3.getResId());
                }
            }
        });

        MyGLSurfaceView glSurfaceView4 = (MyGLSurfaceView) getChildAt(3);
        int measuredWidth3 = glSurfaceView4.getMeasuredWidth();
        int measuredHeight3 = glSurfaceView4.getMeasuredHeight();
        LogUtils.d(TAG, "layout4 3:   --> " + measuredWidth + "," + measuredHeight);
        glSurfaceView4.layout(measuredWidth2, measuredHeight1, measuredWidth2 + measuredWidth3, measuredHeight1 + measuredHeight3);
        glSurfaceView4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.click(glSurfaceView4.getResId());
                }
            }
        });
    }

    private void reDraw() {
        LogUtils.i(TAG, "reDraw --> 重新绘制---");
        invalidate();
        measureChild(getWidth(), getHeight());
        requestLayout();
    }

    public void createView(List<Integer> resIds) {
        resids.addAll(resIds);
        for (int i = 0; i < resIds.size(); i++) {
            final MyGLSurfaceView surfaceView = new MyGLSurfaceView(getContext(), null);
            surfaceView.setResId(resIds.get(i));
            surfaceView.setBackground(cxt.getDrawable(R.drawable.video_res_s));
            surfaceView.setClickable(true);
            surfaceView.setOnGlSurfaceViewOncreateListener(new WlOnGlSurfaceViewOncreateListener() {
                @Override
                public void onGlSurfaceViewOncreate(Surface surface) {
                    LogUtils.e(TAG, "createPlayView onGlSurfaceViewOncreate :   --> ");
                    surfaceView.setSurface(surface);
                }

                @Override
                public void onCutVideoImg(Bitmap bitmap) {
                    LogUtils.e(TAG, "createPlayView onCutVideoImg :   --> ");
                }
            });
            addView(surfaceView);
        }
        reDraw();
    }

    //设置是否选中view
    public void setSelectResId(int resId) {
        for (int i = 0; i < getChildCount(); i++) {
            MyGLSurfaceView childAt = (MyGLSurfaceView) getChildAt(i);
            if (childAt.getResId() == resId) {
                boolean selected = childAt.isSelected();
                childAt.setSelected(!selected);
            } else {//无论当前view是否选中，其它的view都必须设置为未选中
                childAt.setSelected(false);
            }
        }
    }

    public int getSelectResId() {
        for (int i = 0; i < getChildCount(); i++) {
            MyGLSurfaceView childAt = (MyGLSurfaceView) getChildAt(i);
            if (childAt.isSelected()) {
                return childAt.getResId();
            }
        }
        return -1;
    }

    public void setYuv(Object[] yuvdisplay) {
        int resid = (int) yuvdisplay[0];
        if (!resids.contains(resid)) {
            //处理已经停止播放的资源，后台还有一两个通知现在才发过来的问题
            return;
        }
        int w = (int) yuvdisplay[1];
        int h = (int) yuvdisplay[2];
        byte[] y = (byte[]) yuvdisplay[3];
        byte[] u = (byte[]) yuvdisplay[4];
        byte[] v = (byte[]) yuvdisplay[5];
        for (int i = 0; i < getChildCount(); i++) {
            MyGLSurfaceView surfaceView = (MyGLSurfaceView) getChildAt(i);
            if (surfaceView != null) {
                if (surfaceView.getResId() == resid) {
                    surfaceView.stopTimeThread();
                    surfaceView.setCodecType(0);
                    surfaceView.setFrameData(w, h, y, u, v);
                    break;
                }
            }
        }
    }

    public void setVideoDecode(Object[] videoDecode) {
        int resid = (int) videoDecode[1];
        if (!resids.contains(resid)) {
            //处理已经停止播放的资源，后台还有一两个通知现在才发过来的问题
            return;
        }
        int isKeyframe = (int) videoDecode[0];
        int codecid = (int) videoDecode[2];
        int w = (int) videoDecode[3];
        int h = (int) videoDecode[4];
        byte[] packet = (byte[]) videoDecode[5];
        long pts = (long) videoDecode[6];
        byte[] codecdata = (byte[]) videoDecode[7];
        String mimeType = Constant.getMimeType(codecid);
        for (int i = 0; i < getChildCount(); i++) {
            MyGLSurfaceView surfaceView = (MyGLSurfaceView) getChildAt(i);
            if (surfaceView.getResId() == resid) {
                //设置播放类型为decode
                surfaceView.setCodecType(1);
                //收到通知后，可能surface还是空
                if (surfaceView.getSurface() != null) {
                    if (packet != null) {
                        surfaceView.setLastPushTime(System.currentTimeMillis());
                        surfaceView.initCodec(mimeType, w, h, codecdata);
                    }
                    surfaceView.mediaCodecDecode(packet, pts, isKeyframe);
                    surfaceView.startTimeThread();
                }
                break;
            }
        }
    }

    public void stopResWork(int resId) {
        for (int i = 0; i < getChildCount(); i++) {
            MyGLSurfaceView glSurfaceView = (MyGLSurfaceView) getChildAt(i);
            if (glSurfaceView.getResId() == resId) {
                LogUtils.d(TAG, "stopResWork -->" + "停止播放：" + resId);
                glSurfaceView.stopTimeThread();
                glSurfaceView.setCodecType(2);
                break;
            }
        }
    }

    //放大或缩小
    public void zoom(int resId) {
        LogUtils.d(TAG, "zoom -->" + "isEnlarge = " + isEnlarge + ", resId= " + resId);
        largeResId = isEnlarge ? -1 : resId;
        if (isEnlarge) {
            isEnlarge = false;
            shrink(resId);
        } else {
            isEnlarge = true;
            enlarge(resId);
        }
        reDraw();
    }

    //缩小
    private void shrink(int resId) {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setVisibility(VISIBLE);
        }
//        resids.clear();
//        long l = System.currentTimeMillis();
//        MyGLSurfaceView childAt = (MyGLSurfaceView) getChildAt(0);
//        shrinkSfvs.add(childAt);
////        removeView(childAt);
//        List<MyGLSurfaceView> temps = new ArrayList<>(shrinkSfvs);
//        for (int i = 0; i < shrinkSfvs.size(); i++) {
//            MyGLSurfaceView child = shrinkSfvs.get(i);
//            int resId1 = child.getResId();
//            resids.add(resId1);
//            temps.set(resId1 - 1, child);
//        }
//        for (MyGLSurfaceView view : temps) {
//            if (indexOfChild(view) == -1) {
//                LogUtils.i("没有该子view才进行添加");
//                addView(view);
//            }
//        }
//        LogUtils.d(TAG, "shrink -->" + "用时：" + (System.currentTimeMillis() - l));
//        reDraw();
    }

    //放大
    private void enlarge(int resId) {
//        resids.clear();
//        resids.add(resId);
//        shrinkSfvs.clear();
//        for (int i = 0; i < getChildCount(); i++) {
//            MyGLSurfaceView childAt = (MyGLSurfaceView) getChildAt(i);
//            if (childAt.getResId() != resId) {
////                childAt.destroy();
////                childAt.setCodecType(2);
//                shrinkSfvs.add(childAt);
//            }
//        }
//        for (int i = 0; i < shrinkSfvs.size(); i++) {
//            removeView(shrinkSfvs.get(i));
//        }
    }

    public void setViewClickListener(ViewClickListener listener) {
        mListener = listener;
    }

    public void clearAll() {
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof MyGLSurfaceView) {
                MyGLSurfaceView v = (MyGLSurfaceView) childAt;
                v.destroy();
            }
        }
        removeAllViews();
    }

}
