package com.xlk.paperlesstl.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.xlk.paperlesstl.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * @author by xlk
 * @date 2020/7/31 18:20
 * @desc 自定义圆形菜单，功能个数是固定8个
 */
public class CircularMenu extends CustomView {
    private final boolean isShowLog = false;
    private final int menuCount=8;
    /**
     * 区域块的颜色
     */
    private int regionColor=0xE61E5888;
    /**
     * 菜单之间的间隔角度
     */
    private final int spacingAngle=1;
    private Matrix mMapMatrix;
    private Path[] paths;
    private Region[] regions;
    private int[] flags;
    private int currentFlag = -1, touchFlag = -1, centerFlag;
    private Path centerPath;
    private Region centerRegion;
    private MenuClickListener mListener;
    private List<Bitmap> unPressedBitmaps = new ArrayList<>();
    private List<Bitmap> pressedBitmaps = new ArrayList<>();

    public CircularMenu(Context context) {
        this(context, null);
    }

    public CircularMenu(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        initBitmap();
        paths = new Path[menuCount];
        regions = new Region[menuCount];
        flags = new int[menuCount];
        for (int i = 0; i < menuCount; i++) {
            paths[i] = new Path();
            regions[i] = new Region();
            flags[i] = i;
        }
        centerPath = new Path();
        centerRegion = new Region();
        centerFlag = menuCount;

        // https://blog.csdn.net/m0_37041332/article/details/80680835
        //初始化绘制区域的画笔
        mDefaultPaint.setColor(regionColor);
        mDefaultPaint.setStrokeCap(Paint.Cap.ROUND);//设置画笔线帽
        mDefaultPaint.setAntiAlias(true);//开启抗锯齿
        mDefaultPaint.setDither(true);//设置防抖动
        mMapMatrix = new Matrix();
    }

    private void initBitmap() {
        unPressedBitmaps.add(drawableToBitmap(mCurrentContext.getResources().getDrawable(R.drawable.stop_projective_n)));
        unPressedBitmaps.add(drawableToBitmap(mCurrentContext.getResources().getDrawable(R.drawable.exit_n)));
        unPressedBitmaps.add(drawableToBitmap(mCurrentContext.getResources().getDrawable(R.drawable.join_screen_n)));
        unPressedBitmaps.add(drawableToBitmap(mCurrentContext.getResources().getDrawable(R.drawable.stop_screen_n)));
        unPressedBitmaps.add(drawableToBitmap(mCurrentContext.getResources().getDrawable(R.drawable.start_screen_n)));
        unPressedBitmaps.add(drawableToBitmap(mCurrentContext.getResources().getDrawable(R.drawable.note_n)));
        unPressedBitmaps.add(drawableToBitmap(mCurrentContext.getResources().getDrawable(R.drawable.vote_result_n)));
        unPressedBitmaps.add(drawableToBitmap(mCurrentContext.getResources().getDrawable(R.drawable.start_projective_n)));
        unPressedBitmaps.add(drawableToBitmap(mCurrentContext.getResources().getDrawable(R.drawable.return_n)));

        pressedBitmaps.add(drawableToBitmap(mCurrentContext.getResources().getDrawable(R.drawable.stop_projective_p)));
        pressedBitmaps.add(drawableToBitmap(mCurrentContext.getResources().getDrawable(R.drawable.exit_p)));
        pressedBitmaps.add(drawableToBitmap(mCurrentContext.getResources().getDrawable(R.drawable.join_screen_p)));
        pressedBitmaps.add(drawableToBitmap(mCurrentContext.getResources().getDrawable(R.drawable.stop_screen_p)));
        pressedBitmaps.add(drawableToBitmap(mCurrentContext.getResources().getDrawable(R.drawable.start_screen_p)));
        pressedBitmaps.add(drawableToBitmap(mCurrentContext.getResources().getDrawable(R.drawable.note_p)));
        pressedBitmaps.add(drawableToBitmap(mCurrentContext.getResources().getDrawable(R.drawable.vote_result_p)));
        pressedBitmaps.add(drawableToBitmap(mCurrentContext.getResources().getDrawable(R.drawable.start_projective_p)));
        pressedBitmaps.add(drawableToBitmap(mCurrentContext.getResources().getDrawable(R.drawable.return_p)));
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        System.out.println("Drawable转Bitmap");
        Bitmap.Config config =
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        //注意，下面三行代码要用到，否在在View或者surfaceview里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mMapMatrix.reset();

        // 这个区域的大小
        Region globalRegion = new Region(-w, -h, w, h);
        int minWidth = Math.min(w, h);
        minWidth *= 0.9;

        //获取外部圆半径
        int br = minWidth / 2;
        RectF bigCircle = new RectF(-br, -br, br, br);

        //获取内部圆半径
        int sr = minWidth / 4;
        RectF smallCircle = new RectF(-sr, -sr, sr, sr);

        // 根据视图大小，初始化 Path 和 Region
        centerPath.addCircle(0, 0, 0.2f * minWidth, Path.Direction.CW);
        centerRegion.setPath(centerPath, globalRegion);

        //正数顺时针
        final float bigSweepAngle = (float) (360 - (spacingAngle * menuCount)) / menuCount;
        //负数逆时针
        final float smallSweepAngle = -(bigSweepAngle - spacingAngle);

        float bigStartAngle = 0;
        for (int i = 0; i < menuCount; i++) {
            Path path = paths[i];
            Region region = regions[i];
            if (i == 0) {
                bigStartAngle = spacingAngle;
            }
            path.addArc(bigCircle, bigStartAngle, bigSweepAngle);
            path.arcTo(smallCircle, bigStartAngle + bigSweepAngle - spacingAngle, smallSweepAngle);
            path.close();
            region.setPath(path, globalRegion);
            bigStartAngle = bigStartAngle + bigSweepAngle + spacingAngle;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float[] pts = new float[2];
        pts[0] = event.getX();
        pts[1] = event.getY();
        mMapMatrix.mapPoints(pts);
        int x = (int) pts[0];
        int y = (int) pts[1];
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                touchFlag = getTouchedPath(x, y);
                currentFlag = touchFlag;
                break;
            case MotionEvent.ACTION_MOVE:
                currentFlag = getTouchedPath(x, y);
                break;
            case MotionEvent.ACTION_UP:
                currentFlag = getTouchedPath(x, y);
                // 如果手指按下区域和抬起区域相同且不为空，则判断点击事件
                if (currentFlag == touchFlag && currentFlag != -1 && mListener != null) {
                    mListener.onClicked(currentFlag);
                }
                touchFlag = currentFlag = -1;
                break;
            case MotionEvent.ACTION_CANCEL:
                touchFlag = currentFlag = -1;
                break;
        }
        invalidate();
        return true;
    }

    private int getTouchedPath(int x, int y) {
        for (int i = 0; i < regions.length; i++) {
            if (regions[i].contains(x, y)) {
                return i;
            }
        }
        if (centerRegion.contains(x, y)) {
            return centerFlag;
        }
        return -1;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int dx = mViewWidth / 2;
        int dy = mViewHeight / 2;
        //移动坐标系到屏幕中心
        canvas.translate(dx, dy);
        // 获取测量矩阵(逆矩阵)
        if (mMapMatrix.isIdentity()) {
            canvas.getMatrix().invert(mMapMatrix);
        }

        for (int i = 0; i < menuCount; i++) {
            canvas.drawPath(paths[i], mDefaultPaint);
            boolean isPressed = currentFlag == flags[i];
            Bitmap bitmap = isPressed ? pressedBitmaps.get(i) : unPressedBitmaps.get(i);
            Rect srcRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            Rect dstRect = regions[i].getBounds();
            if (isShowLog) {
                Log.i("xlklog", "四角位置111=" + dstRect.left + "," + dstRect.top + "," + dstRect.right + "," + dstRect.bottom);
            }
            if (i == 0) {
                int left = dstRect.left + (dstRect.right - dstRect.left) / 3;
                int top = dstRect.top + (dstRect.bottom - dstRect.top) / 6;
                int right = dstRect.right - (dstRect.right - dstRect.left) / 3;
                int bottom = dstRect.bottom - (dstRect.bottom - dstRect.top) / 2;
                dstRect.set(left, top, right, bottom);
            } else if (i == 1) {
                int left = dstRect.left + (dstRect.right - dstRect.left) / 4;
                int top = dstRect.top + (dstRect.bottom - dstRect.top) / 3;
                int right = dstRect.right - (dstRect.right - dstRect.left) / 3 - (dstRect.right - dstRect.left) / 12;
                int bottom = dstRect.bottom - (dstRect.bottom - dstRect.top) / 3;
                dstRect.set(left, top, right, bottom);
            } else if (i == 2) {
                int left = dstRect.left + (Math.abs(dstRect.left) - Math.abs(dstRect.right)) / 3 + (Math.abs(dstRect.left) - Math.abs(dstRect.right)) / 12;
                int top = dstRect.top + (dstRect.bottom - dstRect.top) / 3;
                int right = dstRect.right - (Math.abs(dstRect.left) - Math.abs(dstRect.right)) / 4;
                int bottom = dstRect.bottom - (dstRect.bottom - dstRect.top) / 3;
                dstRect.set(left, top, right, bottom);
            } else if (i == 3) {
                int left = dstRect.left + (Math.abs(dstRect.left) - Math.abs(dstRect.right)) / 3;
                int top = dstRect.top + (dstRect.bottom - dstRect.top) / 6;
                int right = dstRect.right - (Math.abs(dstRect.left) - Math.abs(dstRect.right)) / 3;
                int bottom = dstRect.bottom - (dstRect.bottom - dstRect.top) / 2;
                dstRect.set(left, top, right, bottom);
            } else if (i == 4) {
                int left = dstRect.left + (Math.abs(dstRect.left) - Math.abs(dstRect.right)) / 3;
                int top = dstRect.top + (Math.abs(dstRect.top) - Math.abs(dstRect.bottom)) / 3;
                int right = dstRect.right - (Math.abs(dstRect.left) - Math.abs(dstRect.right)) / 3;
                int bottom = dstRect.bottom - (Math.abs(dstRect.top) - Math.abs(dstRect.bottom)) / 3;
                dstRect.set(left, top, right, bottom);
            } else if (i == 5) {
                int left = dstRect.left + (Math.abs(dstRect.left) - Math.abs(dstRect.right)) / 3 + (Math.abs(dstRect.left) - Math.abs(dstRect.right)) / 12;
                int top = dstRect.top + (Math.abs(dstRect.top) - Math.abs(dstRect.bottom)) / 4;
                int right = dstRect.right - (Math.abs(dstRect.left) - Math.abs(dstRect.right)) / 4;
                int bottom = dstRect.bottom - (Math.abs(dstRect.top) - Math.abs(dstRect.bottom)) / 3 - (Math.abs(dstRect.top) - Math.abs(dstRect.bottom)) / 12;
                dstRect.set(left, top, right, bottom);
            } else if (i == 6) {
                int left = dstRect.left + (Math.abs(dstRect.right) - Math.abs(dstRect.left)) / 4;
                int top = dstRect.top + (Math.abs(dstRect.top) - Math.abs(dstRect.bottom)) / 4;
                int right = dstRect.right - (Math.abs(dstRect.right) - Math.abs(dstRect.left)) / 3 - (Math.abs(dstRect.right) - Math.abs(dstRect.left)) / 12;
                int bottom = dstRect.bottom - (Math.abs(dstRect.top) - Math.abs(dstRect.bottom)) / 3 - (Math.abs(dstRect.top) - Math.abs(dstRect.bottom)) / 12;
                dstRect.set(left, top, right, bottom);
            } else if (i == 7) {
                int left = dstRect.left + (Math.abs(dstRect.right) - Math.abs(dstRect.left)) / 3;
                int top = dstRect.top + (Math.abs(dstRect.top) - Math.abs(dstRect.bottom)) / 3;
                int right = dstRect.right - (Math.abs(dstRect.right) - Math.abs(dstRect.left)) / 3;
                int bottom = dstRect.bottom - (Math.abs(dstRect.top) - Math.abs(dstRect.bottom)) / 3;
                dstRect.set(left, top, right, bottom);
            }
            if (isShowLog) {
                Log.e("xlklog", "四角位置222=" + dstRect.left + "," + dstRect.top + "," + dstRect.right + "," + dstRect.bottom);
            }
            canvas.drawBitmap(bitmap, srcRect, dstRect, mDefaultPaint);
        }
        drawCenter(canvas);
    }

    private void drawCenter(Canvas canvas) {
        canvas.drawPath(centerPath, mDefaultPaint);
        Bitmap bitmap = currentFlag == centerFlag
                ? pressedBitmaps.get(pressedBitmaps.size() - 1)
                : unPressedBitmaps.get(unPressedBitmaps.size() - 1);
        Rect srcRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Rect dstRect = centerRegion.getBounds();
        dstRect.set(dstRect.left / 2, dstRect.top / 2, dstRect.right / 2, dstRect.bottom / 2);
        canvas.drawBitmap(bitmap, srcRect, dstRect, mDefaultPaint);
    }

    public void setListener(MenuClickListener listener) {
        mListener = listener;
    }

    // 点击事件监听器
    public interface MenuClickListener {
        void onClicked(int index);
    }
}
