package com.xlk.paperlesstl.view.base;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.UriUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.jni.JniHelper;
import com.xlk.paperlesstl.model.data.EventMessage;
import com.xlk.paperlesstl.model.data.EventType;
import com.xlk.paperlesstl.util.DialogUtil;
import com.xlk.paperlesstl.util.IniUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author Created by xlk on 2021/3/1.
 * @desc
 */
public abstract class BaseActivity<T extends BasePresenter> extends AppCompatActivity implements IBaseView {
    protected String TAG = this.getClass().getSimpleName();
    protected T presenter;
    protected JniHelper jni = JniHelper.getInstance();
    protected IniUtil ini = IniUtil.getInstance();
    private final int REQUEST_CODE_EXPORT_NOTE = 1;
    private AlertDialog tipDialog;
    private AlertDialog disconnectedDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        presenter = initPresenter();
        init(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            case EventType.BUS_CHOOSE_NOTE_FILE: {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, REQUEST_CODE_EXPORT_NOTE);
                break;
            }
//            case EventType.BUS_NETWORK_DISCONNECTED: {
//                showNetWorkTipPop();
//                break;
//            }
            case EventType.BUS_NETWORK_CONNECTED: {
//                NetworkUtils.NetworkType networkType = (NetworkUtils.NetworkType) msg.getObjects()[0];
                if (!jni.isOnline()) {
                    LogUtils.e(TAG, "网络变更 无法连接服务器");
                    showServerDisconnectedDialog();
                } else {
                    LogUtils.e(TAG, "网络变更 连接服务器成功");
                    closeServerDisconnectedDialog();
                }
                break;
            }
            //关闭服务断开提示窗口
            case EventType.BUS_CLOSE_SERVER_DISCONNECTED_DIALOG: {
                closeServerDisconnectedDialog();
                break;
            }
            default:
                break;
        }
    }

    private boolean isShowing(AlertDialog dialog) {
        return dialog != null && dialog.isShowing();
    }

    private void showServerDisconnectedDialog() {
        LogUtils.i(TAG, "disconnectedDialog showServerDisconnectedDialog isShowing=" + isShowing(disconnectedDialog));
        if (isShowing(disconnectedDialog)) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_server_disconnected, null);
        builder.setView(inflate);
        disconnectedDialog = builder.create();
        Window window = disconnectedDialog.getWindow();
        DialogUtil.setParamsType(window);
        window.setGravity(Gravity.TOP | Gravity.CENTER);
        disconnectedDialog.setCanceledOnTouchOutside(false);//点击外部不消失
        disconnectedDialog.setCancelable(false);//用户点击返回键使其无效
        disconnectedDialog.show();//这行代码要在设置宽高的前面，宽高才有用
        WindowManager.LayoutParams layoutparams = window.getAttributes();
        //取消掉dialog弹出后界面阴影效果
        layoutparams.dimAmount = 0f;
        //距离上方的百分比
        layoutparams.verticalMargin = 0.03f;
        layoutparams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        window.setAttributes(layoutparams);
    }

    private void closeServerDisconnectedDialog() {
        LogUtils.i(TAG, "disconnectedDialog closeServerDisconnectedDialog isShowing=" + isShowing(disconnectedDialog));
        if (isShowing(disconnectedDialog)) {
            disconnectedDialog.dismiss();
        }
    }

    private void showNetWorkTipPop() {
        if (tipDialog != null && tipDialog.isShowing()) {
            return;
        }
        tipDialog = DialogUtil.createTipDialog(this, getString(R.string.network_disconnected_tip), getString(R.string.ensure), getString(R.string.cancel), new DialogUtil.onDialogClickListener() {
            @Override
            public void positive(DialogInterface dialog) {
                dialog.dismiss();
                NetworkUtils.openWirelessSettings();
            }

            @Override
            public void negative(DialogInterface dialog) {
                dialog.dismiss();
            }

            @Override
            public void dismiss(DialogInterface dialog) {
                tipDialog = null;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EXPORT_NOTE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            File file = UriUtils.uri2File(uri);
            if (file != null) {
                if (file.getName().endsWith(".txt")) {
                    String content = FileIOUtils.readFile2String(file);
                    EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_EXPORT_NOTE_CONTENT).objects(content).build());
                } else {
                    ToastUtils.showShort(R.string.please_choose_txt_file);
                }
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        LogUtils.i(TAG, "onNewIntent " + this);
        super.onNewIntent(intent);
    }

    protected abstract int getLayoutId();

    protected abstract T initPresenter();

    protected abstract void init(Bundle savedInstanceState);

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    /**
     * 打开选择本地文件
     *
     * @param requestCode 返回码
     */
    protected void chooseLocalFile(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, requestCode);
    }

    public void dismissPopupWindow(PopupWindow pop) {
        if (pop != null && pop.isShowing()) {
            pop.dismiss();
        }
    }

}
