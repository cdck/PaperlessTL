package com.xlk.paperlesstl.util;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.model.GlobalValue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

/**
 * @author Created by xlk on 2021/3/9.
 * @desc
 */
public class DialogUtil {


    /**
     * 设置dialog在窗口的最上层
     * 即使dialog的上下文对象不是activity
     */
    public static void setParamsType(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0新特性
            window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.setType(WindowManager.LayoutParams.TYPE_PHONE);
        } else {
            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
    }

    public interface onDialogClickListener {
        void positive(DialogInterface dialog);

        void negative(DialogInterface dialog);

        void dismiss(DialogInterface dialog);

    }


    /**
     * 展示提示弹框
     *
     * @param cxt      上下文
     * @param message  提示内容
     * @param positive 确定按钮文本
     * @param negative 取消按钮文本
     * @param listener 弹框操作回调
     * @return AlertDialog
     */
    public static AlertDialog createTipDialog(Context cxt, String message, String positive, String negative, @NonNull onDialogClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(cxt);
        View inflate = LayoutInflater.from(cxt).inflate(R.layout.dialog_operation_tip_view, null);
        TextView tv_message = inflate.findViewById(R.id.tv_message);
        Button btn_ensure = inflate.findViewById(R.id.btn_ensure);
        Button btn_cancel = inflate.findViewById(R.id.btn_cancel);
        tv_message.setText(message);
        btn_ensure.setText(positive);
        btn_cancel.setText(negative);
        builder.setView(inflate);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                listener.dismiss(dialog);
            }
        });
        AlertDialog dialog = builder.create();
        setParamsType(dialog.getWindow());
        dialog.setCanceledOnTouchOutside(false);//点击外部不消失
        dialog.setCancelable(false);//用户点击返回键使其无效
        dialog.show();//这行代码要在设置宽高的前面，宽高才有用
        //宽高必须要在show之后设置
        WindowManager.LayoutParams attributes = dialog.getWindow().getAttributes();
        attributes.width = GlobalValue.screen_width / 2;
        attributes.height = GlobalValue.screen_height / 2;
        dialog.getWindow().setAttributes(attributes);
        inflate.findViewById(R.id.btn_ensure).setOnClickListener(v -> listener.positive(dialog));
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> listener.negative(dialog));
        return dialog;
    }


    /**
     * 创建一个宽高为屏幕一半且居中的dialog
     *
     * @param context  上下文对象
     * @param layoutId 布局id
     * @param outside  点击外部是否隐藏窗口
     * @return AlertDialog对象
     */
    public static AlertDialog createTipDialog(Context context, int layoutId, boolean outside) {
        return createTipDialog(context, layoutId, outside, GlobalValue.half_width, GlobalValue.half_height);
    }

    /**
     * 创建一个指定大小且居中的dialog
     * @param context  上下文对象
     * @param layoutId xml布局
     * @param outside  是否点击外部隐藏dialog
     * @param width    宽
     * @param height   高
     * @return AlertDialog，用于查找控件
     */
    public static AlertDialog createTipDialog(Context context, int layoutId, boolean outside, int width, int height) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View inflate = LayoutInflater.from(context).inflate(layoutId, null);
        builder.setView(inflate);
        AlertDialog dialog = builder.create();
        setParamsType(dialog.getWindow());
        //=false 点击外部不消失
        dialog.setCanceledOnTouchOutside(outside);
        //=false 用户点击返回键使其无效
        dialog.setCancelable(outside);
        dialog.show();
        //宽高必须要在show之后设置
        WindowManager.LayoutParams attributes = dialog.getWindow().getAttributes();
        attributes.width = width;
        attributes.height = height;
        dialog.getWindow().setAttributes(attributes);
        return dialog;
    }
}
