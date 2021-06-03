package com.xlk.paperlesstl.view.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import cc.shinichi.library.tool.ui.ToastUtil;
import skin.support.SkinCompatManager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.mogujie.tt.protobuf.InterfaceAdmin;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceFaceconfig;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.xlk.paperlesstl.App;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.adapter.MemberAdapter;
import com.xlk.paperlesstl.helper.SharedPreferenceHelper;
import com.xlk.paperlesstl.model.Constant;
import com.xlk.paperlesstl.model.GlobalValue;
import com.xlk.paperlesstl.ui.ArtBoard;
import com.xlk.paperlesstl.ui.RvItemDecoration;
import com.xlk.paperlesstl.util.AppUtil;
import com.xlk.paperlesstl.util.ConvertUtil;
import com.xlk.paperlesstl.util.PopupUtil;
import com.xlk.paperlesstl.view.admin.activity.AdminActivity;
import com.xlk.paperlesstl.view.admin.fragment.system.device.LocalFileAdapter;
import com.xlk.paperlesstl.view.base.BaseActivity;
import com.xlk.paperlesstl.view.meet.MeetingActivity;
import com.xlk.paperlesstl.view.offline.OfflineActivity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.xlk.paperlesstl.model.Constant.EXTRA_ADMIN_ID;
import static com.xlk.paperlesstl.model.Constant.EXTRA_ADMIN_NAME;
import static com.xlk.paperlesstl.model.Constant.THEME_TYPE_DEFAULT;
import static com.xlk.paperlesstl.model.Constant.THEME_TYPE_RED;
import static com.xlk.paperlesstl.model.Constant.THEME_TYPE_YELLOW;
import static com.xlk.paperlesstl.model.GlobalValue.camera_height;
import static com.xlk.paperlesstl.model.GlobalValue.camera_width;
import static com.xlk.paperlesstl.util.ConvertUtil.s2b;

public class MainActivity extends BaseActivity<MainPresenter> implements MainContract.View {

    private androidx.constraintlayout.widget.ConstraintLayout mainRootLayout;
    private android.widget.TextView seatTvMain;
    private android.widget.TextView postTvMain;
    private android.widget.TextView unitTvMain;
    private android.widget.TextView meetTvMain;
    private android.widget.TextView memberTvMain;
    private android.widget.ImageView logoIvMain;
    private android.widget.TextView companyTvMain;
    private android.widget.TextView memberRole;
    private android.widget.TextView meetState;
    private android.widget.TextView appVersion;
    private android.widget.TextView noteInfo;
    private android.widget.RelativeLayout dateRelativeMain;
    private android.widget.TextView timeTvMain;
    private android.widget.LinearLayout dateLinearMain;
    private android.widget.TextView dateTvMain;
    private android.widget.TextView weekTvMain;
    private android.widget.LinearLayout iconLlMain;
    private android.widget.ImageView ivSetMain;
    private android.widget.ImageView ivCloseMain;
    private android.widget.Button enterBtnMain;
    private PopupWindow unbindMemberPop, createMemberPop, mainLoginPop;
    private MemberAdapter unbindMemberAdapter;
    private PopupWindow configPop;
    private PopupWindow cacheDirPop;
    private PopupWindow signInPop;
    private TextView tvLoginStatus;
    private boolean isLogin2Offline;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected MainPresenter initPresenter() {
        return new MainPresenter(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        initView();
        applyPermissions(new String[]{
                Permission.WRITE_EXTERNAL_STORAGE,
                Permission.READ_EXTERNAL_STORAGE,
                Permission.RECORD_AUDIO,
                Permission.CAMERA,
                Permission.READ_PHONE_STATE
        });
    }


    private void initView() {
        mainRootLayout = (ConstraintLayout) findViewById(R.id.main_root_layout);
        seatTvMain = (TextView) findViewById(R.id.seat_tv_main);
        postTvMain = (TextView) findViewById(R.id.post_tv_main);
        unitTvMain = (TextView) findViewById(R.id.unit_tv_main);
        meetTvMain = (TextView) findViewById(R.id.meet_tv_main);
        memberTvMain = (TextView) findViewById(R.id.member_tv_main);
        logoIvMain = (ImageView) findViewById(R.id.logo_iv_main);
        companyTvMain = (TextView) findViewById(R.id.company_tv_main);
        memberRole = (TextView) findViewById(R.id.member_role);
        meetState = (TextView) findViewById(R.id.meet_state);
        appVersion = (TextView) findViewById(R.id.app_version);
        noteInfo = (TextView) findViewById(R.id.note_info);
        dateRelativeMain = (RelativeLayout) findViewById(R.id.date_relative_main);
        timeTvMain = (TextView) findViewById(R.id.time_tv_main);
        dateLinearMain = (LinearLayout) findViewById(R.id.date_linear_main);
        dateTvMain = (TextView) findViewById(R.id.date_tv_main);
        weekTvMain = (TextView) findViewById(R.id.week_tv_main);
        iconLlMain = (LinearLayout) findViewById(R.id.icon_ll_main);
        ivSetMain = (ImageView) findViewById(R.id.iv_set_main);
        ivCloseMain = (ImageView) findViewById(R.id.iv_close_main);
        enterBtnMain = (Button) findViewById(R.id.enter_btn_main);
    }

    private void applyPermissions(String[] pers) {
        XXPermissions.with(this).constantRequest()
                .permission(pers).request(new OnPermission() {
            @Override
            public void hasPermission(List<String> granted, boolean all) {
                if (all) {
                    start();
                }
            }

            @Override
            public void noPermission(List<String> denied, boolean quick) {

            }
        });
    }

    private void start() {
        initConfigFile();
        updateAppVersion();
        try {
            initCameraSize();
        } catch (Exception e) {
            LogUtils.e(TAG, "start --> 相机使用失败：" + e.toString());
            e.printStackTrace();
        }
        applyReadFrameBufferPermission();
    }

    private void updateAppVersion() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            appVersion.setText(getString(R.string.version_, packageInfo.versionName));
            String hardver = "";
            String softver = "";
            if (packageInfo.versionName.contains(".")) {
                hardver = packageInfo.versionName.substring(0, packageInfo.versionName.indexOf("."));
                softver = packageInfo.versionName.substring(packageInfo.versionName.indexOf(".") + 1);
            }
            if (ini.loadFile()) {
                String iniHardver = ini.get("selfinfo", "hardver");
                String iniSoftver = ini.get("selfinfo", "softver");
                String lastTime = ini.get("other", "lastTime");
                if (!iniHardver.equals(hardver) || !iniSoftver.equals(softver)) {
                    LogUtils.i(TAG, "setVersion 设置到ini文件中");
                    ini.put("selfinfo", "hardver", hardver);
                    ini.put("selfinfo", "softver", softver);
                    ini.put("other", "lastTime", System.currentTimeMillis());
                    ini.store();
                }
            }
            LogUtils.e(TAG, "packageInfo.versionCode=" + packageInfo.versionCode + ",packageInfo.lastUpdateTime=" + packageInfo.lastUpdateTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initConfigFile() {
        long l = System.currentTimeMillis();
        LogUtils.d("进入initConfigFile");
        FileUtils.createOrExistsDir(Constant.ROOT_DIR);
        boolean exists = FileUtils.isFileExists(Constant.ROOT_DIR + "client.ini");
        if (!exists) {
            copyTo("client.ini", Constant.ROOT_DIR, "client.ini");
        }
        File file = new File(Constant.ROOT_DIR + "client.dev");
        if (file.exists()) {
            file.delete();
        }
        copyTo("client.dev", Constant.ROOT_DIR, "client.dev");
        LogUtils.i(TAG, "initConfigFile 用时=" + (System.currentTimeMillis() - l));

    }

    /**
     * 复制文件
     *
     * @param fromPath
     * @param toPath
     * @param fileName
     */
    private void copyTo(String fromPath, String toPath, String fileName) {
        // 复制位置
        File toFile = new File(toPath);
        FileUtils.createOrExistsFile(toFile);
        try {
            // 根据文件名获取assets文件夹下的该文件的inputstream
            InputStream fromFileIs = getResources().getAssets().open(fromPath);
            // 获取文件的字节数
            int length = fromFileIs.available();
            // 创建byte数组
            byte[] buffer = new byte[length];
            FileOutputStream fileOutputStream = new FileOutputStream(toFile
                    + "/" + fileName); // 字节输入流
            BufferedInputStream bufferedInputStream = new BufferedInputStream(
                    fromFileIs);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                    fileOutputStream);
            int len = bufferedInputStream.read(buffer);
            while (len != -1) {
                bufferedOutputStream.write(buffer, 0, len);
                len = bufferedInputStream.read(buffer);
            }
            bufferedInputStream.close();
            bufferedOutputStream.close();
            fromFileIs.close();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initCameraSize() {
        int type = 1;
        LogUtils.d(TAG, "initCameraSize :   --> ");
        //获取摄像机的个数 一般是前/后置两个
        int numberOfCameras = Camera.getNumberOfCameras();
        if (numberOfCameras < 2) {
            LogUtils.d(TAG, "initCameraSize: 该设备只有后置像头");
            //如果没有2个则说明只有后置像头
            type = 0;
        }
        ArrayList<Integer> supportW = new ArrayList<>();
        ArrayList<Integer> supportH = new ArrayList<>();
        int largestW = 0, largestH = 0;
        Camera c = Camera.open(type);
        Camera.Parameters param = null;
        if (c != null) {
            param = c.getParameters();
        }
        if (param == null) {
            return;
        }
        for (int i = 0; i < param.getSupportedPreviewSizes().size(); i++) {
            int w = param.getSupportedPreviewSizes().get(i).width, h = param.getSupportedPreviewSizes().get(i).height;
//            LogUtils.d(TAG, "initCameraSize: w=" + w + " h=" + h);
            supportW.add(w);
            supportH.add(h);
        }
        for (int i = 0; i < supportH.size(); i++) {
            try {
                largestW = supportW.get(i);
                largestH = supportH.get(i);
                LogUtils.d(TAG, "initCameraSize :   --> largestW= " + largestW + " , largestH=" + largestH);
                MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/avc", largestW, largestH);
                if (MediaCodec.createEncoderByType("video/avc").getCodecInfo().getCapabilitiesForType("video/avc").isFormatSupported(mediaFormat)) {
                    if (largestW * largestH > camera_width * camera_height) {
                        camera_width = largestW;
                        camera_height = largestH;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (c != null) {
                    c.setPreviewCallback(null);
                    c.stopPreview();
                    c.release();
                    c = null;
                }
            }
        }
        if (camera_width * camera_height > 1280 * 720) {
            camera_width = 1280;
            camera_height = 720;
        }
        LogUtils.d(TAG, "initCameraSize -->" + "前置像素：" + camera_width + " X " + camera_height);
    }

    private void applyReadFrameBufferPermission() {
        try {
            MediaProjectionManager manager = (MediaProjectionManager) getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            App.mMediaProjectionManager = manager;
            if (App.mIntent != null && App.mResult != 0) {
                initial();
            } else {
                /** **** **  第一次时保存 manager  ** **** **/
                startActivityForResult(manager.createScreenCaptureIntent(), REQUEST_CODE_READ_FRAME_BUFFER);
            }
        } catch (Exception e) {
            e.printStackTrace();
            initial();
        }
    }

    private final int REQUEST_CODE_READ_FRAME_BUFFER = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_READ_FRAME_BUFFER) {
            if (resultCode == Activity.RESULT_OK) {
                App.mResult = resultCode;
                App.mIntent = data;
                App.mMediaProjection = App.mMediaProjectionManager.getMediaProjection(resultCode, data);
                initial();
            } else {
                applyReadFrameBufferPermission();
            }
        }
    }

    /**
     * 权限获取成功后开始初始化
     */
    private void initial() {
        LogUtils.i(TAG, "initial");
        presenter.initialization(AppUtil.getUniqueId(this));
    }

    @Override
    public void updateTime(String[] date) {
        timeTvMain.setText(date[0]);
        dateTvMain.setText(date[1]);
        weekTvMain.setText(date[2]);
    }

    @Override
    public void updateSeatName(String name) {
        LogUtils.d(TAG, "updateSeatName 席位：" + name);
        seatTvMain.setText(name);
    }

    @Override
    public void showUpgradeDialog(String content, InterfaceBase.pbui_Type_MeetUpdateNotify info) {
        runOnUiThread(() -> {
            File file = new File(info.getUpdatepath().toStringUtf8() + "/update.apk");
            LogUtils.i(TAG, "showUpgradeDialog 更新内容=" + content);
            View inflate = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_upgrade, null);
            PopupWindow upgradePop = PopupUtil.createHalfPop(inflate, enterBtnMain, false);
            TextView tv_new_version = inflate.findViewById(R.id.tv_new_version);
            TextView tv_old_version = inflate.findViewById(R.id.tv_old_version);
            TextView tv_content = inflate.findViewById(R.id.tv_content);
            tv_new_version.setText(info.getNewhardver() + "." + info.getNewsoftver());
            tv_old_version.setText(info.getLocalhardver() + "." + info.getLocalsoftver());
            tv_content.setText(content);
            //下次更新
            inflate.findViewById(R.id.btn_next_time).setOnClickListener(v -> {
                upgradePop.dismiss();
            });
            //立即更新
            inflate.findViewById(R.id.btn_upgrade).setOnClickListener(v -> {
                AppUtils.installApp(file);
                upgradePop.dismiss();
            });
        });
    }

    @Override
    public void jump2meet() {
        presenter.unregister();
        startActivity(new Intent(MainActivity.this, MeetingActivity.class));
        finish();
    }

    /**
     * 申请悬浮窗权限
     */
    private void applyAlertWindowPermission() {
        XXPermissions.with(this).constantRequest()
                .permission(Manifest.permission.SYSTEM_ALERT_WINDOW)
                .request(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> granted, boolean all) {
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {
                    }
                });
    }

    @Override
    public void readySignIn() {
        LogUtils.i(TAG, "开始签到 readySignIn");
        if (!XXPermissions.hasPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW)) {
            applyAlertWindowPermission();
        } else {
            if (GlobalValue.signinType == InterfaceMacro.Pb_MeetSignType.Pb_signin_direct_VALUE) {
                jni.sendSign(0, GlobalValue.signinType, "", s2b(""));
            } else {
                signin(GlobalValue.signinType);
            }
        }
    }

    private void signin(final int type) {
        View inflate = LayoutInflater.from(this).inflate(R.layout.pop_main_sign_in, null, false);
        signInPop = PopupUtil.createHalfPop(inflate, enterBtnMain);
        TextView tv_title = inflate.findViewById(R.id.tv_title);
        LinearLayout ll_draw_view = inflate.findViewById(R.id.ll_draw_view);
        LinearLayout ll_draw_pwd_view = inflate.findViewById(R.id.ll_draw_pwd_view);
        LinearLayout ll_pwd_view = inflate.findViewById(R.id.ll_pwd_view);
        EditText edt_pwd = inflate.findViewById(R.id.edt_pwd);
        EditText edt_pwd_only = inflate.findViewById(R.id.edt_pwd_only);
        ArtBoard art_board = inflate.findViewById(R.id.art_board);
        String signInType = "";
        switch (type) {
            case InterfaceMacro.Pb_MeetSignType.Pb_signin_psw_VALUE: {
                signInType = getString(R.string.personal_password_signin);
                ll_pwd_view.setVisibility(View.VISIBLE);
                break;
            }
            case InterfaceMacro.Pb_MeetSignType.Pb_signin_photo_VALUE: {
                signInType = getString(R.string.handwriting_signin);
                ll_draw_view.setVisibility(View.VISIBLE);
                break;
            }
            case InterfaceMacro.Pb_MeetSignType.Pb_signin_onepsw_VALUE: {
                signInType = getString(R.string.meeting_password_signin);
                ll_pwd_view.setVisibility(View.VISIBLE);
                break;
            }
            case InterfaceMacro.Pb_MeetSignType.Pb_signin_onepsw_photo_VALUE: {
                signInType = getString(R.string.meeting_password_and_handwriting_signin);
                ll_draw_view.setVisibility(View.VISIBLE);
                ll_draw_pwd_view.setVisibility(View.VISIBLE);
                break;
            }
            case InterfaceMacro.Pb_MeetSignType.Pb_signin_psw_photo_VALUE: {
                signInType = getString(R.string.personal_password_and_handwriting_signin);
                ll_draw_view.setVisibility(View.VISIBLE);
                ll_draw_pwd_view.setVisibility(View.VISIBLE);
                break;
            }
        }
        tv_title.setText(signInType);
        inflate.findViewById(R.id.pwd_draw_revoke).setOnClickListener(v -> {
            art_board.revoke();
        });
        inflate.findViewById(R.id.pwd_draw_clear).setOnClickListener(v -> {
            art_board.clear();
        });
        inflate.findViewById(R.id.pwd_draw_determine).setOnClickListener(v -> {
            String pwd = edt_pwd.getText().toString().trim();
            if (ll_draw_pwd_view.getVisibility() == View.VISIBLE) {
                if (pwd.isEmpty()) {
                    ToastUtils.showShort(R.string.please_enter_password);
                    return;
                }
            }
            if (art_board.isNotEmpty()) {
                Bitmap canvasBmp = art_board.getCanvasBmp();
                signInPop.dismiss();
                jni.sendSign(0, type, pwd, ConvertUtil.bmp2bs(canvasBmp));
                art_board.clear();//不清理在画板界面就会存在于 LocalPathList集合中
                canvasBmp.recycle();
                signInPop.dismiss();
            } else {
                ToastUtils.showShort(R.string.please_sign_first);
            }
        });
        inflate.findViewById(R.id.pwd_draw_cancel).setOnClickListener(v -> {
            signInPop.dismiss();
        });
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> signInPop.dismiss());
        inflate.findViewById(R.id.btn_ensure).setOnClickListener(v -> {
            String pwd = edt_pwd_only.getText().toString().trim();
            if (pwd.isEmpty()) {
                ToastUtils.showShort(R.string.please_enter_password);
                return;
            }
            jni.sendSign(0, type, pwd, s2b(""));
            signInPop.dismiss();
        });
        signInPop.setOnDismissListener(() -> {
            if (art_board != null && art_board.isNotEmpty()) {
                art_board.clear();
            }
        });
    }

    @Override
    public void updateBackground(Drawable drawable) {
        mainRootLayout.setBackground(drawable);
    }

    @Override
    public void updateLogo(Drawable drawable) {
        LogUtils.i(TAG, "updateLogo");
        logoIvMain.setImageDrawable(drawable);
    }

    @Override
    public void updateMemberRole(String role) {
        memberRole.setText(role);
    }

    @Override
    public void updateMeetingState(int state) {
        LogUtils.i(TAG, "updateMeetingState state=" + state);
        //会议状态，0为未开始会议，1为已开始会议，2为已结束会议，其它表示未加入会议无状态
        switch (state) {
            case 0:
                meetState.setText(getString(R.string.state_meet_not));
                break;
            case 1:
                meetState.setText(getString(R.string.state_meet_start));
                break;
            case 2:
                meetState.setText(getString(R.string.state_meet_end));
                break;
            default:
                meetState.setText("");
                break;
        }
    }

    @Override
    public void updateCompany(String text) {
        LogUtils.i(TAG, "updateCompany 公司=" + text);
        companyTvMain.setText(text);
    }

    @Override
    public void updateTv(int resId, InterfaceFaceconfig.pbui_Item_FaceTextItemInfo itemInfo) {
        update(resId, itemInfo);
        int faceid = itemInfo.getFaceid();
        int flag = itemInfo.getFlag();
        boolean isShow = (InterfaceMacro.Pb_MeetFaceFlag.Pb_MEET_FACEFLAG_SHOW_VALUE == (flag & InterfaceMacro.Pb_MeetFaceFlag.Pb_MEET_FACEFLAG_SHOW_VALUE));
        int fontsize = itemInfo.getFontsize();
        int color = itemInfo.getColor();
        int align = itemInfo.getAlign();
        int fontflag = itemInfo.getFontflag();
        String fontName = itemInfo.getFontname().toStringUtf8();
        TextView tv = findViewById(resId);
        tv.setTextColor(color);
        tv.setTextSize(fontsize);
        tv.setVisibility(isShow ? View.VISIBLE : View.GONE);
        //字体样式
        if (fontflag == InterfaceMacro.Pb_MeetFaceFontFlag.Pb_MEET_FONTFLAG_BOLD.getNumber()) {//加粗
            tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        } else if (fontflag == InterfaceMacro.Pb_MeetFaceFontFlag.Pb_MEET_FONTFLAG_LEAN.getNumber()) {//倾斜
            tv.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
        } else if (fontflag == InterfaceMacro.Pb_MeetFaceFontFlag.Pb_MEET_FONTFLAG_UNDERLINE.getNumber()) {//下划线
            tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));//暂时用倾斜加粗
        } else {//正常文本
            tv.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }
        //对齐方式
        if (align == InterfaceMacro.Pb_FontAlignFlag.Pb_MEET_FONTALIGNFLAG_LEFT.getNumber()) {//左对齐
            tv.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        } else if (align == InterfaceMacro.Pb_FontAlignFlag.Pb_MEET_FONTALIGNFLAG_RIGHT.getNumber()) {//右对齐
            tv.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        } else if (align == InterfaceMacro.Pb_FontAlignFlag.Pb_MEET_FONTALIGNFLAG_HCENTER.getNumber()) {//水平对齐
            tv.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        } else if (align == InterfaceMacro.Pb_FontAlignFlag.Pb_MEET_FONTALIGNFLAG_TOP.getNumber()) {//上对齐
            tv.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        } else if (align == InterfaceMacro.Pb_FontAlignFlag.Pb_MEET_FONTALIGNFLAG_BOTTOM.getNumber()) {//下对齐
            tv.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        } else if (align == InterfaceMacro.Pb_FontAlignFlag.Pb_MEET_FONTALIGNFLAG_VCENTER.getNumber()) {//垂直对齐
            tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        } else {
            tv.setGravity(Gravity.CENTER);
        }
        //字体类型
        Typeface kt_typeface;
        if (!TextUtils.isEmpty(fontName)) {
            switch (fontName) {
                case "楷体":
                    kt_typeface = Typeface.createFromAsset(getAssets(), "kaiti.ttf");
                    break;
                case "隶书":
                    kt_typeface = Typeface.createFromAsset(getAssets(), "lishu.ttf");
                    break;
                case "微软雅黑":
                    kt_typeface = Typeface.createFromAsset(getAssets(), "weiruanyahei.ttf");
                    break;
                case "黑体":
                    kt_typeface = Typeface.createFromAsset(getAssets(), "heiti.ttf");
                    break;
                case "小楷":
                    kt_typeface = Typeface.createFromAsset(getAssets(), "xiaokai.ttf");
                    break;
                default:
                    kt_typeface = Typeface.createFromAsset(getAssets(), "fangsong.ttf");
                    break;
            }
            tv.setTypeface(kt_typeface);
        }
    }

    @Override
    public void updateDate(int resId, InterfaceFaceconfig.pbui_Item_FaceTextItemInfo itemInfo) {
        update(resId, itemInfo);
        int faceid = itemInfo.getFaceid();
        int flag = itemInfo.getFlag();
        boolean isShow = (InterfaceMacro.Pb_MeetFaceFlag.Pb_MEET_FACEFLAG_SHOW_VALUE == (flag & InterfaceMacro.Pb_MeetFaceFlag.Pb_MEET_FACEFLAG_SHOW_VALUE));
        dateRelativeMain.setVisibility(isShow ? View.VISIBLE : View.GONE);
        int fontsize = itemInfo.getFontsize();
        int color = itemInfo.getColor();
        int align = itemInfo.getAlign();
        int fontflag = itemInfo.getFontflag();
        String fontName = itemInfo.getFontname().toStringUtf8();
        timeTvMain.setTextColor(color);
        timeTvMain.setTextSize(fontsize);
        dateTvMain.setTextColor(color);
        dateTvMain.setTextSize(fontsize);
        weekTvMain.setTextColor(color);
        weekTvMain.setTextSize(fontsize);
        Typeface typeface = null;
        //字体样式
        if (fontflag == InterfaceMacro.Pb_MeetFaceFontFlag.Pb_MEET_FONTFLAG_BOLD.getNumber()) {//加粗
            typeface = Typeface.defaultFromStyle(Typeface.BOLD);
        } else if (fontflag == InterfaceMacro.Pb_MeetFaceFontFlag.Pb_MEET_FONTFLAG_LEAN.getNumber()) {//倾斜
            typeface = Typeface.defaultFromStyle(Typeface.ITALIC);
        } else if (fontflag == InterfaceMacro.Pb_MeetFaceFontFlag.Pb_MEET_FONTFLAG_UNDERLINE.getNumber()) {//下划线
            typeface = Typeface.defaultFromStyle(Typeface.BOLD_ITALIC);
        } else {//正常文本
            typeface = Typeface.defaultFromStyle(Typeface.NORMAL);
        }
        timeTvMain.setTypeface(typeface);
        dateTvMain.setTypeface(typeface);
        weekTvMain.setTypeface(typeface);
        //对齐方式
        if (align == InterfaceMacro.Pb_FontAlignFlag.Pb_MEET_FONTALIGNFLAG_LEFT.getNumber()) {//左对齐
            dateRelativeMain.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            timeTvMain.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            dateTvMain.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            weekTvMain.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        } else if (align == InterfaceMacro.Pb_FontAlignFlag.Pb_MEET_FONTALIGNFLAG_RIGHT.getNumber()) {//右对齐
            dateRelativeMain.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
            timeTvMain.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
            dateTvMain.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
            weekTvMain.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        } else if (align == InterfaceMacro.Pb_FontAlignFlag.Pb_MEET_FONTALIGNFLAG_HCENTER.getNumber()) {//水平对齐
            dateRelativeMain.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
            timeTvMain.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
            dateTvMain.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
            weekTvMain.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        } else if (align == InterfaceMacro.Pb_FontAlignFlag.Pb_MEET_FONTALIGNFLAG_TOP.getNumber()) {//上对齐
            dateRelativeMain.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
            timeTvMain.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
            dateTvMain.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
            weekTvMain.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        } else if (align == InterfaceMacro.Pb_FontAlignFlag.Pb_MEET_FONTALIGNFLAG_BOTTOM.getNumber()) {//下对齐
            dateRelativeMain.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            timeTvMain.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            dateTvMain.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            weekTvMain.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        } else if (align == InterfaceMacro.Pb_FontAlignFlag.Pb_MEET_FONTALIGNFLAG_VCENTER.getNumber()) {//垂直对齐
            dateRelativeMain.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            timeTvMain.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            dateTvMain.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            weekTvMain.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        } else {
            dateRelativeMain.setGravity(Gravity.CENTER);
            timeTvMain.setGravity(Gravity.CENTER);
            dateTvMain.setGravity(Gravity.CENTER);
            weekTvMain.setGravity(Gravity.CENTER);
        }
        //字体类型
        Typeface kt_typeface;
        if (!TextUtils.isEmpty(fontName)) {
            switch (fontName) {
                case "楷体":
                    kt_typeface = Typeface.createFromAsset(getAssets(), "kaiti.ttf");
                    break;
                case "黑体":
                    kt_typeface = Typeface.createFromAsset(getAssets(), "heiti.ttf");
                    break;
                case "隶书":
                    kt_typeface = Typeface.createFromAsset(getAssets(), "lishu.ttf");
                    break;
                case "微软雅黑":
                    kt_typeface = Typeface.createFromAsset(getAssets(), "weiruanyahei.ttf");
                    break;
                case "小楷":
                    kt_typeface = Typeface.createFromAsset(getAssets(), "xiaokai.ttf");
                    break;
                default:
                    kt_typeface = Typeface.createFromAsset(getAssets(), "fangsong.ttf");
                    break;
            }
            timeTvMain.setTypeface(kt_typeface);
            dateTvMain.setTypeface(kt_typeface);
            weekTvMain.setTypeface(kt_typeface);
        }
    }

    @Override
    public void update(int resId, InterfaceFaceconfig.pbui_Item_FaceTextItemInfo itemInfo) {
        float lx = itemInfo.getLx();
        float ly = itemInfo.getLy();
        float bx = itemInfo.getBx();
        float by = itemInfo.getBy();
        ConstraintSet set = new ConstraintSet();
        set.clone(mainRootLayout);
        //设置控件的大小
        float width = (bx - lx) / 100 * GlobalValue.screen_width;
        float height = (by - ly) / 100 * GlobalValue.screen_height;
        set.constrainWidth(resId, (int) width);
        set.constrainHeight(resId, (int) height);
//        LogUtils.d(TAG, "update: 控件大小 当前控件宽= " + width + ", 当前控件高= " + height);
        float biasX, biasY;
        float halfW = (bx - lx) / 2 + lx;
        float halfH = (by - ly) / 2 + ly;

        if (lx == 0) biasX = 0;
        else if (lx > 50) biasX = bx / 100;
        else biasX = halfW / 100;

        if (ly == 0) biasY = 0;
        else if (ly > 50) biasY = by / 100;
        else biasY = halfH / 100;
//        LogUtils.d(TAG, "update: biasX= " + biasX + ",biasY= " + biasY);
        set.setHorizontalBias(resId, biasX);
        set.setVerticalBias(resId, biasY);
        set.applyTo(mainRootLayout);
    }

    @Override
    public void isShowLogo(boolean isShow) {
        logoIvMain.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @Override
    public void updateBtn(int resId, InterfaceFaceconfig.pbui_Item_FaceTextItemInfo itemInfo) {
//        update(resId, itemInfo);
        int faceid = itemInfo.getFaceid();
        int flag = itemInfo.getFlag();
        boolean isShow = (InterfaceMacro.Pb_MeetFaceFlag.Pb_MEET_FACEFLAG_SHOW_VALUE == (flag & InterfaceMacro.Pb_MeetFaceFlag.Pb_MEET_FACEFLAG_SHOW_VALUE));
        int fontsize = itemInfo.getFontsize();
        int color = itemInfo.getColor();
        int align = itemInfo.getAlign();
        int fontflag = itemInfo.getFontflag();
        String fontName = itemInfo.getFontname().toStringUtf8();
        Button btn = findViewById(resId);
        btn.setTextColor(color);
        btn.setTextSize(fontsize);
        btn.setVisibility(isShow ? View.VISIBLE : View.GONE);
        //字体样式
        if (fontflag == InterfaceMacro.Pb_MeetFaceFontFlag.Pb_MEET_FONTFLAG_BOLD.getNumber()) {//加粗
            btn.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        } else if (fontflag == InterfaceMacro.Pb_MeetFaceFontFlag.Pb_MEET_FONTFLAG_LEAN.getNumber()) {//倾斜
            btn.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
        } else if (fontflag == InterfaceMacro.Pb_MeetFaceFontFlag.Pb_MEET_FONTFLAG_UNDERLINE.getNumber()) {//下划线
            btn.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));//暂时用倾斜加粗
        } else {//正常文本
            btn.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }
        //对齐方式
        if (align == InterfaceMacro.Pb_FontAlignFlag.Pb_MEET_FONTALIGNFLAG_LEFT.getNumber()) {//左对齐
            btn.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        } else if (align == InterfaceMacro.Pb_FontAlignFlag.Pb_MEET_FONTALIGNFLAG_RIGHT.getNumber()) {//右对齐
            btn.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        } else if (align == InterfaceMacro.Pb_FontAlignFlag.Pb_MEET_FONTALIGNFLAG_HCENTER.getNumber()) {//水平对齐
            btn.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        } else if (align == InterfaceMacro.Pb_FontAlignFlag.Pb_MEET_FONTALIGNFLAG_TOP.getNumber()) {//上对齐
            btn.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        } else if (align == InterfaceMacro.Pb_FontAlignFlag.Pb_MEET_FONTALIGNFLAG_BOTTOM.getNumber()) {//下对齐
            btn.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        } else if (align == InterfaceMacro.Pb_FontAlignFlag.Pb_MEET_FONTALIGNFLAG_VCENTER.getNumber()) {//垂直对齐
            btn.setGravity(Gravity.CENTER_VERTICAL);
        } else {
            btn.setGravity(Gravity.CENTER);
        }
        //字体类型
        Typeface kt_typeface;
        if (!TextUtils.isEmpty(fontName)) {
            switch (fontName) {
                case "楷体":
                    kt_typeface = Typeface.createFromAsset(getAssets(), "kaiti.ttf");
                    break;
                case "隶书":
                    kt_typeface = Typeface.createFromAsset(getAssets(), "lishu.ttf");
                    break;
                case "微软雅黑":
                    kt_typeface = Typeface.createFromAsset(getAssets(), "weiruanyahei.ttf");
                    break;
                case "黑体":
                    kt_typeface = Typeface.createFromAsset(getAssets(), "heiti.ttf");
                    break;
                case "小楷":
                    kt_typeface = Typeface.createFromAsset(getAssets(), "xiaokai.ttf");
                    break;
                default:
                    kt_typeface = Typeface.createFromAsset(getAssets(), "fangsong.ttf");
                    break;
            }
            btn.setTypeface(kt_typeface);
        }
    }

    @Override
    public void updateUI(InterfaceDevice.pbui_Type_DeviceFaceShowDetail devMeetInfo) {
        memberTvMain.setText(devMeetInfo.getMembername().toStringUtf8());
        String meetingName = devMeetInfo.getMeetingname().toStringUtf8();
        meetTvMain.setText(meetingName);
        unitTvMain.setText(getString(R.string.unit_name_, devMeetInfo.getCompany().toStringUtf8()));
        postTvMain.setText(getString(R.string.job_name_, devMeetInfo.getJob().toStringUtf8()));
        if (GlobalValue.localMeetingId == 0) {
            memberTvMain.setText("");
            meetTvMain.setText(getString(R.string.please_join_meeting_first));
            unitTvMain.setText("");
            postTvMain.setText("");
            noteInfo.setText("");
            meetState.setText("");
            memberRole.setText("");
        }
    }

    @Override
    public void updateUnit(String unit) {
        LogUtils.d(TAG, "updateUnit -->" + "单位：" + unit);
        unitTvMain.setText(getString(R.string.unit_name_, unit));
    }

    @Override
    public void updateNote(String string) {
        noteInfo.setText(string);
    }

    /**
     * 进入会议按钮点击事件
     *
     * @param view
     */
    public void enterMeeting(View view) {
        if (GlobalValue.localMeetingId != 0) {
            if (GlobalValue.localMemberId != 0) {
                readySignIn();
            } else {
                ToastUtils.showShort(R.string.please_bind_member_first);
                presenter.queryMember();
                showUnBindMembers();
            }
        } else {
            ToastUtils.showShort(R.string.please_join_meeting_first);
        }
    }

    /**
     * 展示所有未绑定设备的参会人弹框
     */
    private void showUnBindMembers() {
        View inflate = LayoutInflater.from(this).inflate(R.layout.pop_unbind_member, null, false);
        unbindMemberPop = PopupUtil.createHalfPop(inflate, enterBtnMain);
        RecyclerView rv_unbind_member = inflate.findViewById(R.id.rv_unbind_member);
        unbindMemberAdapter = new MemberAdapter(presenter.unbindMembers);
        rv_unbind_member.setAdapter(unbindMemberAdapter);
        rv_unbind_member.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        unbindMemberAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                unbindMemberAdapter.setSelect(presenter.unbindMembers.get(position).getPersonid());
            }
        });
        inflate.findViewById(R.id.btn_ensure).setOnClickListener(v -> {
            int memberId = unbindMemberAdapter.getSelectedId();
            if (memberId != -1) {
                jni.modifyMeetRanking(memberId, 0, GlobalValue.localDeviceId);
                unbindMemberPop.dismiss();
            } else {
                ToastUtils.showShort(R.string.please_choose_member);
            }
        });
        inflate.findViewById(R.id.btn_create).setOnClickListener(v -> {
            unbindMemberPop.dismiss();
            showCreateMemberPop();
        });
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> {
            unbindMemberPop.dismiss();
        });
    }

    @Override
    public void updateUnBindMember() {
        if (unbindMemberPop != null && unbindMemberPop.isShowing()) {
            unbindMemberAdapter.notifyDataSetChanged();
        }
    }

    private void showCreateMemberPop() {
        View inflate = LayoutInflater.from(this).inflate(R.layout.pop_create_member, null, false);
        createMemberPop = PopupUtil.createHalfPop(inflate, enterBtnMain);
        EditText edt_company = inflate.findViewById(R.id.edt_company);
        EditText edt_name = inflate.findViewById(R.id.edt_name);
        EditText edt_position = inflate.findViewById(R.id.edt_position);
        EditText edt_phone = inflate.findViewById(R.id.edt_phone);
        EditText edt_email = inflate.findViewById(R.id.edt_email);
        EditText edt_password = inflate.findViewById(R.id.edt_password);
        EditText edt_note = inflate.findViewById(R.id.edt_note);
        inflate.findViewById(R.id.btn_ensure).setOnClickListener(v -> {
            String name = edt_name.getText().toString().trim();
            if (name.isEmpty()) {
                ToastUtils.showShort(R.string.please_enter_member_name);
                return;
            }
            String company = edt_company.getText().toString().trim();
            String position = edt_position.getText().toString().trim();
            String phone = edt_phone.getText().toString().trim();
            String email = edt_email.getText().toString().trim();
            String password = edt_password.getText().toString().trim();
            String note = edt_note.getText().toString().trim();
            if (!email.isEmpty() && !RegexUtils.isEmail(email)) {
                ToastUtils.showShort(R.string.email_format_error);
                return;
            }
            if (!phone.isEmpty() && !RegexUtils.isMobileSimple(phone)) {
                ToastUtils.showShort(R.string.phone_format_error);
                return;
            }
            InterfaceMember.pbui_Item_MemberDetailInfo build = InterfaceMember.pbui_Item_MemberDetailInfo.newBuilder()
                    .setCompany(s2b(company))
                    .setName(s2b(name))
                    .setJob(s2b(position))
                    .setEmail(s2b(email))
                    .setPhone(s2b(phone))
                    .setPassword(s2b(password))
                    .setComment(s2b(note)).build();
            jni.addAttendPeople(build);
            createMemberPop.dismiss();
        });
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> createMemberPop.dismiss());
        createMemberPop.setOnDismissListener(this::showUnBindMembers);
    }

    private long lastClickTime;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - lastClickTime > 2000) {
            lastClickTime = System.currentTimeMillis();
            ToastUtils.showShort(R.string.click_again_exit);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        dismissPopupWindow(unbindMemberPop);
        dismissPopupWindow(createMemberPop);
        dismissPopupWindow(mainLoginPop);
        dismissPopupWindow(configPop);
        dismissPopupWindow(cacheDirPop);
        dismissPopupWindow(signInPop);
        FileUtils.deleteAllInDir(Constant.FILE_DIR);
        super.onDestroy();
    }

    @Override
    public void updateLoginStatus(int status) {
        LogUtils.e(TAG, "updateLoginStatus status=" + status);
        if (mainLoginPop != null && mainLoginPop.isShowing()) {
            switch (status) {
                case InterfaceMacro.Pb_DB_StatusCode.Pb_STATUS_DONE_VALUE: {
                    break;
                }
                case InterfaceMacro.Pb_DB_StatusCode.Pb_STATUS_FAIL_VALUE: {
                    tvLoginStatus.setText(getString(R.string.login_fail_tip));
                    break;
                }
                case InterfaceMacro.Pb_DB_StatusCode.Pb_STATUS_EXCPT_DB_VALUE: {
                    tvLoginStatus.setText(getString(R.string.database_exception));
                    break;
                }
                case InterfaceMacro.Pb_DB_StatusCode.Pb_STATUS_EXCPT_SV_VALUE: {
                    tvLoginStatus.setText(getString(R.string.server_exception));
                    break;
                }
                case InterfaceMacro.Pb_DB_StatusCode.Pb_STATUS_PSWFAILED_VALUE: {
                    tvLoginStatus.setText(getString(R.string.wrong_password));
                    break;
                }
                case InterfaceMacro.Pb_DB_StatusCode.Pb_STATUS_ACCESSDENIED_VALUE: {
                    tvLoginStatus.setText(getString(R.string.no_permission));
                    break;
                }
            }
        }
    }

    @Override
    public void loginStatus(InterfaceAdmin.pbui_Type_AdminLogonStatus info) {
        //管理员登陆状态
        int err = info.getErr();
        int adminid = info.getAdminid();
        String adminname = info.getAdminname().toStringUtf8();
        int sessionid = info.getSessionid();
        LogUtils.i(TAG, "loginStatus adminid:" + adminid + ",adminname:" + adminname + ",sessionid:" + sessionid + ",err:" + err);
        switch (err) {
            //登陆成功
            case InterfaceMacro.Pb_AdminLogonStatus.Pb_ADMINLOGON_ERR_NONE_VALUE:
                tvLoginStatus.setText(getString(R.string.login_successful));
                Intent intent;
                if (isLogin2Offline) {
                    intent = new Intent(MainActivity.this, OfflineActivity.class);
                } else {
                    intent = new Intent(MainActivity.this, AdminActivity.class);
                }
                intent.putExtra(EXTRA_ADMIN_ID, adminid);
                intent.putExtra(EXTRA_ADMIN_NAME, adminname);
                if (GlobalValue.localMeetingId != 0) {
                    jni.modifyContextProperties(InterfaceMacro.Pb_ContextPropertyID.Pb_MEETCONTEXT_PROPERTY_CURMEETINGID_VALUE
                            , GlobalValue.localMeetingId);
                }
                presenter.unregister();
                startActivity(intent);
                finish();
                break;
            //密码错误
            case InterfaceMacro.Pb_AdminLogonStatus.Pb_ADMINLOGON_ERR_PSW_VALUE:
                tvLoginStatus.setText(getString(R.string.wrong_password));
                break;
            //服务器异常
            case InterfaceMacro.Pb_AdminLogonStatus.Pb_ADMINLOGON_ERR_EXCPT_SV_VALUE:
                tvLoginStatus.setText(getString(R.string.server_exception));
                break;
            //数据库异常
            case InterfaceMacro.Pb_AdminLogonStatus.Pb_ADMINLOGON_ERR_EXCPT_DB_VALUE:
                tvLoginStatus.setText(getString(R.string.database_exception));
                break;
            default:
                break;
        }
    }

    /**
     * 设置按钮点击事件
     *
     * @param view
     */
    public void openSet(View view) {
        boolean spIsRemember = (boolean) SharedPreferenceHelper.getData(this, SharedPreferenceHelper.key_remember, false);
        boolean isAdministratorLogin = (boolean) SharedPreferenceHelper.getData(this, SharedPreferenceHelper.key_administrator_login, true);
        int themeType = (int) SharedPreferenceHelper.getData(this, SharedPreferenceHelper.key_theme_type, 0);
        String spUser = (String) SharedPreferenceHelper.getData(this, SharedPreferenceHelper.key_user, "");
        String spPwd = (String) SharedPreferenceHelper.getData(this, SharedPreferenceHelper.key_password, "");
        View inflate = LayoutInflater.from(this).inflate(R.layout.pop_main_set, null, false);
        mainLoginPop = PopupUtil.createCenter(inflate, ScreenUtils.getScreenWidth() / 3 * 2, ScreenUtils.getScreenHeight() / 3 * 2, enterBtnMain);
        EditText edt_user_name = inflate.findViewById(R.id.edt_user_name);
        EditText edt_user_pwd = inflate.findViewById(R.id.edt_user_pwd);
        CheckBox cb_manage_mode = inflate.findViewById(R.id.cb_manage_mode);
        CheckBox cb_offline_mode = inflate.findViewById(R.id.cb_offline_mode);
        CheckBox cb_remember_pwd = inflate.findViewById(R.id.cb_remember_pwd);
        tvLoginStatus = inflate.findViewById(R.id.tv_login_status);

        RadioGroup rg_theme = inflate.findViewById(R.id.rg_theme);
        RadioButton rb_theme_blue = inflate.findViewById(R.id.rb_theme_blue);
        RadioButton rb_theme_red = inflate.findViewById(R.id.rb_theme_red);
        RadioButton rb_theme_yellow = inflate.findViewById(R.id.rb_theme_yellow);
        rb_theme_blue.setChecked(themeType == THEME_TYPE_DEFAULT);
        rb_theme_red.setChecked(themeType == THEME_TYPE_RED);
        rb_theme_yellow.setChecked(themeType == THEME_TYPE_YELLOW);
        rg_theme.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_theme_blue:
                        ToastUtils.showShort(R.string.theme_blue);
                        SkinCompatManager.getInstance().restoreDefaultTheme();
                        SharedPreferenceHelper.setData(MainActivity.this, SharedPreferenceHelper.key_theme_type, THEME_TYPE_DEFAULT);
                        GlobalValue.theme_type = THEME_TYPE_DEFAULT;
                        break;
                    case R.id.rb_theme_red:
                        ToastUtils.showShort(R.string.theme_red);
                        SkinCompatManager.getInstance().loadSkin("red", SkinCompatManager.SKIN_LOADER_STRATEGY_PREFIX_BUILD_IN); // 前缀加载
                        SharedPreferenceHelper.setData(MainActivity.this, SharedPreferenceHelper.key_theme_type, THEME_TYPE_RED);
                        GlobalValue.theme_type = THEME_TYPE_RED;
                        break;
                    case R.id.rb_theme_yellow:
                        ToastUtils.showShort(R.string.theme_yellow);
                        SkinCompatManager.getInstance().loadSkin("yellow", SkinCompatManager.SKIN_LOADER_STRATEGY_PREFIX_BUILD_IN); // 前缀加载
                        SharedPreferenceHelper.setData(MainActivity.this, SharedPreferenceHelper.key_theme_type, THEME_TYPE_YELLOW);
                        GlobalValue.theme_type = THEME_TYPE_YELLOW;
                        break;
                }
            }
        });

        cb_remember_pwd.setChecked(spIsRemember);
        cb_manage_mode.setChecked(isAdministratorLogin);
        cb_offline_mode.setChecked(!isAdministratorLogin);
        edt_user_name.setText(spUser);
        if (spIsRemember) {
            edt_user_pwd.setText(spPwd);
        }
        cb_manage_mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cb_offline_mode.setChecked(!isChecked);
            }
        });
        cb_offline_mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cb_manage_mode.setChecked(!isChecked);
            }
        });
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> mainLoginPop.dismiss());
        inflate.findViewById(R.id.btn_login).setOnClickListener(v -> {
            String userName = edt_user_name.getText().toString();
            String userPwd = edt_user_pwd.getText().toString();
            if (userName.isEmpty() || userPwd.isEmpty()) {
                ToastUtils.showShort(R.string.please_enter_name_and_password);
                return;
            }
            SharedPreferenceHelper.setData(MainActivity.this, SharedPreferenceHelper.key_remember, cb_remember_pwd.isChecked());
            SharedPreferenceHelper.setData(MainActivity.this, SharedPreferenceHelper.key_administrator_login, cb_manage_mode.isChecked());
            if (cb_remember_pwd.isChecked()) {
                SharedPreferenceHelper.setData(MainActivity.this, SharedPreferenceHelper.key_user, userName);
                SharedPreferenceHelper.setData(MainActivity.this, SharedPreferenceHelper.key_password, userPwd);
            }
            isLogin2Offline = cb_offline_mode.isChecked();
            jni.login(userName, userPwd, 1, isLogin2Offline ? 2 : 0);
        });
        inflate.findViewById(R.id.btn_system_settings).setOnClickListener(v -> {
            showConfigPop();
            mainLoginPop.dismiss();
        });
    }

    private void showConfigPop() {
        View inflate = LayoutInflater.from(this).inflate(R.layout.pop_main_config, null, false);
        configPop = PopupUtil.createHalfPop(inflate, enterBtnMain);
        EditText edt_ip = inflate.findViewById(R.id.edt_ip);
        EditText edt_port = inflate.findViewById(R.id.edt_port);
        EditText edt_bitrate = inflate.findViewById(R.id.edt_bitrate);
        EditText edt_cache_location = inflate.findViewById(R.id.edt_cache_location);
        EditText edt_cache_size = inflate.findViewById(R.id.edt_cache_size);
        CheckBox cb_encode_filter = inflate.findViewById(R.id.cb_encode_filter);
        CheckBox cb_open_microphone = inflate.findViewById(R.id.cb_open_microphone);
        CheckBox cb_disable_multicast = inflate.findViewById(R.id.cb_disable_multicast);
        CheckBox cb_tcp_mode = inflate.findViewById(R.id.cb_tcp_mode);
        edt_cache_location.setKeyListener(null);

        edt_cache_location.setText(ini.get("Buffer Dir", "mediadir"));
        edt_cache_size.setText(ini.get("Buffer Dir", "mediadirsize"));

        edt_ip.setText(ini.get("areaaddr", "area0ip"));
        edt_port.setText(ini.get("areaaddr", "area0port"));
        edt_bitrate.setText(ini.get("other", "maxBitRate"));

        //编码过滤
        cb_encode_filter.setChecked(ini.get("nosdl", "disablebsf").equals("0"));
        //打开麦克风 将音频附加到视频通道中
        cb_open_microphone.setChecked(ini.get("debug", "videoaudio").equals("1"));
        //禁用组播 等于1表示禁用组播
        cb_disable_multicast.setChecked(ini.get("debug", "disablemulticast").equals("1"));
        //等于1表示使用TCP模式
        cb_tcp_mode.setChecked(ini.get("selfinfo", "streamprotol").equals("1"));
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> configPop.dismiss());
        inflate.findViewById(R.id.iv_more).setOnClickListener(v -> {
            showDirPop(edt_cache_location);
        });
        inflate.findViewById(R.id.btn_save).setOnClickListener(v -> {
            String ip = edt_ip.getText().toString().trim();
            String port = edt_port.getText().toString().trim();
            String bitrate = edt_bitrate.getText().toString().trim();
            String mediadir = edt_cache_location.getText().toString().trim();
            String mediadirsize = edt_cache_size.getText().toString().trim();
            if (ip.isEmpty() || port.isEmpty() || bitrate.isEmpty()) {
                ToastUtils.showShort(R.string.tip_content_empty);
                return;
            }
            if (!RegexUtils.isIP(ip)) {
                ToastUtils.showShort(R.string.ip_format_err);
                return;
            }
            int i = Integer.parseInt(bitrate);
            if (i < 500 || i > 10000) {
                ToastUtils.showShort(R.string.tip_bitrate_scope);
                return;
            }
            ini.put("areaaddr", "area0ip", ip);
            ini.put("areaaddr", "area0port", port);
            ini.put("Buffer Dir", "mediadir", mediadir);
            ini.put("Buffer Dir", "mediadirsize", mediadirsize);
            ini.put("other", "maxBitRate", bitrate);
            ini.put("nosdl", "disablebsf", cb_encode_filter.isChecked() ? "0" : "1");
            ini.put("debug", "videoaudio", cb_open_microphone.isChecked() ? "1" : "0");
            ini.put("debug", "disablemulticast", cb_disable_multicast.isChecked() ? "1" : "0");
            ini.put("selfinfo", "streamprotol", cb_tcp_mode.isChecked() ? "1" : "0");
            ini.store();
            AppUtils.relaunchApp(true);
            configPop.dismiss();
        });
    }

    private FileFilter dirFilter = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            return pathname.isDirectory() && !pathname.getName().startsWith(".");
        }
    };

    private List<File> currentFiles = new ArrayList<>();
    private LocalFileAdapter localFileAdapter;

    private void showDirPop(EditText edt) {
        String rootDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        currentFiles.clear();
        currentFiles.addAll(FileUtils.listFilesInDirWithFilter(rootDir, dirFilter));
        View inflate = LayoutInflater.from(this).inflate(R.layout.pop_local_file, null);
        cacheDirPop = PopupUtil.createHalfPop(inflate, enterBtnMain);
        EditText edt_current_dir = inflate.findViewById(R.id.edt_current_dir);
        edt_current_dir.setKeyListener(null);
        edt_current_dir.setText(rootDir);

        RecyclerView rv_current_file = inflate.findViewById(R.id.rv_current_file);
        localFileAdapter = new LocalFileAdapter(R.layout.item_local_file, currentFiles);
        rv_current_file.setLayoutManager(new LinearLayoutManager(this));
        rv_current_file.addItemDecoration(new RvItemDecoration(this));
        rv_current_file.setAdapter(localFileAdapter);
        localFileAdapter.setOnItemClickListener((adapter, view, position) -> {
            File file = currentFiles.get(position);
            edt_current_dir.setText(file.getAbsolutePath());
            edt_current_dir.setSelection(edt_current_dir.getText().toString().length());
            List<File> files = FileUtils.listFilesInDirWithFilter(file, dirFilter);
            currentFiles.clear();
            currentFiles.addAll(files);
            localFileAdapter.notifyDataSetChanged();
        });
        inflate.findViewById(R.id.iv_back).setOnClickListener(v -> {
            String dirPath = edt_current_dir.getText().toString().trim();
            if (dirPath.equals(rootDir)) {
                ToastUtils.showShort(R.string.current_dir_root);
                return;
            }
            File file = new File(dirPath);
            File parentFile = file.getParentFile();
            edt_current_dir.setText(parentFile.getAbsolutePath());
            LogUtils.i(TAG, "showChooseDir 上一级的目录=" + parentFile.getAbsolutePath());
            List<File> files = FileUtils.listFilesInDirWithFilter(parentFile, dirFilter);
            currentFiles.clear();
            currentFiles.addAll(files);
            localFileAdapter.notifyDataSetChanged();
        });
        inflate.findViewById(R.id.btn_determine).setOnClickListener(v -> {
            String text = edt_current_dir.getText().toString();
            edt.setText(text);
            edt.setSelection(text.length());
            cacheDirPop.dismiss();
        });
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> {
            cacheDirPop.dismiss();
        });
    }

    /**
     * 退出应用按钮点击事件
     *
     * @param view
     */
    public void exitApp(View view) {
        finish();
//        AppUtils.exitApp();
    }
}