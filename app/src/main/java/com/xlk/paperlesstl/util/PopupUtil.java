package com.xlk.paperlesstl.util;

import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.blankj.utilcode.util.LogUtils;
import com.xlk.paperlesstl.model.GlobalValue;

/**
 * @author Created by xlk on 2021/3/4.
 * @desc
 */
public class PopupUtil {
    /**
     * @param contentView 弹框布局
     * @param w           宽
     * @param h           高
     * @param parent      父控件
     * @return PopupWindow
     */
    public static PopupWindow createCenter(View contentView, int w, int h, View parent, boolean outside) {
        PopupWindow popupWindow = new PopupWindow(contentView, w, h);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        popupWindow.setTouchable(true);
        // true:设置触摸外面时消失
        popupWindow.setOutsideTouchable(outside);
        popupWindow.setFocusable(outside);
        popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
        return popupWindow;
    }

    /**
     * @param contentView 弹框布局
     * @param w           宽
     * @param h           高
     * @param parent      父控件
     * @return PopupWindow
     */
    public static PopupWindow createCenter(View contentView, int w, int h, View parent) {
        PopupWindow popupWindow = new PopupWindow(contentView, w, h);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        popupWindow.setTouchable(true);
        // true:设置触摸外面时消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
        return popupWindow;
    }

    /**
     * 创建一个大小为屏幕一半且居中的弹窗
     *
     * @param contentView
     * @param parent
     * @return
     */
    public static PopupWindow createHalfPop(View contentView, View parent) {
        PopupWindow popupWindow = new PopupWindow(contentView, GlobalValue.half_width, GlobalValue.half_height);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        popupWindow.setTouchable(true);
        // true:设置触摸外面时消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
        return popupWindow;
    }

    /**
     * 创建一个大小为屏幕一半且居中的弹窗
     *
     * @param contentView
     * @param parent
     * @param outside     =false 点击外部不会消失
     * @return
     */
    public static PopupWindow createHalfPop(View contentView, View parent, boolean outside) {
        PopupWindow popupWindow = new PopupWindow(contentView, GlobalValue.half_width, GlobalValue.half_height);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        popupWindow.setTouchable(true);
        // true:设置触摸外面时消失
        popupWindow.setOutsideTouchable(outside);
        popupWindow.setFocusable(outside);
        popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
        return popupWindow;
    }

    public static PopupWindow createAs(View contentView, ImageView parent, int xoff, int yoff) {
        PopupWindow popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        popupWindow.setTouchable(true);
        // true:设置触摸外面时消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAsDropDown(parent, xoff, yoff);
        LogUtils.d("xlklog", "PopupWindow的偏移量： xoff=" + xoff + ",yoff=" + yoff);
        return popupWindow;
    }
}
