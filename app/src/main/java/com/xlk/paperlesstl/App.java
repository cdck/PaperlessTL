package com.xlk.paperlesstl;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsDownloader;
import com.tencent.smtt.sdk.TbsListener;
import com.xlk.paperlesstl.helper.CrashHandler;
import com.xlk.paperlesstl.helper.MyRejectedExecutionHandler;
import com.xlk.paperlesstl.helper.NamingThreadFactory;
import com.xlk.paperlesstl.helper.ScreenRecorder;
import com.xlk.paperlesstl.helper.SharedPreferenceHelper;
import com.xlk.paperlesstl.model.Constant;
import com.xlk.paperlesstl.model.GlobalValue;
import com.xlk.paperlesstl.model.data.EventMessage;
import com.xlk.paperlesstl.model.data.EventType;
import com.xlk.paperlesstl.service.BackService;
import com.xlk.paperlesstl.service.FabService;
import com.xlk.paperlesstl.view.main.MainActivity;
import com.xlk.paperlesstl.view.meet.MeetingActivity;
import com.xlk.paperlesstl.view.fragment.agenda.AgendaFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import skin.support.SkinCompatManager;
import skin.support.app.SkinAppCompatViewInflater;
import skin.support.app.SkinCardViewInflater;
import skin.support.constraint.app.SkinConstraintViewInflater;
import skin.support.design.app.SkinMaterialViewInflater;

import static com.xlk.paperlesstl.model.Constant.THEME_TYPE_DEFAULT;
import static com.xlk.paperlesstl.model.Constant.THEME_TYPE_RED;

/**
 * @author Created by xlk on 2021/3/1.
 * @desc
 */
public class App extends Application {
    static {
        System.loadLibrary("avcodec-57");
        System.loadLibrary("avdevice-57");
        System.loadLibrary("avfilter-6");
        System.loadLibrary("avformat-57");
        System.loadLibrary("avutil-55");
        System.loadLibrary("postproc-54");
        System.loadLibrary("swresample-2");
        System.loadLibrary("swscale-4");
        System.loadLibrary("SDL2");
        System.loadLibrary("main");
        System.loadLibrary("NetClient");
        System.loadLibrary("Codec");
        System.loadLibrary("ExecProc");
        System.loadLibrary("Device-OpenSles");
        System.loadLibrary("meetcoreAnd");
        System.loadLibrary("PBmeetcoreAnd");
        System.loadLibrary("meetAnd");
        System.loadLibrary("native-lib");
        System.loadLibrary("z");
    }

    private final String TAG = "App-->";
    public static final boolean read2file = false;
    public static Context appContext;
    public static LocalBroadcastManager lbm;
    public static MediaProjection mMediaProjection;
    public static MediaProjectionManager mMediaProjectionManager;
    public static int mResult;
    public static Intent mIntent;
    public static int width, height, dpi, maxBitRate = 1000 * 1000;
    private ScreenRecorder recorder;
    public static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            1,
            Runtime.getRuntime().availableProcessors() + 1,
            10L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(100),
            new NamingThreadFactory("paperlessTL-threadPool-"),
            new MyRejectedExecutionHandler()
    );
    public static List<Activity> activities = new ArrayList<>();
    public static Activity currentActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("???????????????????????????");
        appContext = this;
        LogUtils.Config config = LogUtils.getConfig();
        config.setLogSwitch(true);
        config.setDir(Constant.LOGCAT_DIR);
        config.setSaveDays(7);
        CrashHandler.getInstance().init(this);
        initScreenParam();
        lbm = LocalBroadcastManager.getInstance(getApplicationContext());
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.ACTION_START_SCREEN_RECORD);
        filter.addAction(Constant.ACTION_STOP_SCREEN_RECORD);
        filter.addAction(Constant.ACTION_STOP_SCREEN_RECORD_WHEN_EXIT_APP);
        lbm.registerReceiver(receiver, filter);

        ActivityUtils.addActivityLifecycleCallbacks(new Utils.ActivityLifecycleCallbacks());

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                activities.add(activity);
                LogUtils.d("activityLife", "onActivityCreated " + activity + ",Activity??????=" + activities.size() + logAxt());
                if (activity.getClass().getName().equals(MeetingActivity.class.getName())) {
                    openFabService(true);
                }
                if (activity.getClass().getName().equals(MainActivity.class.getName())) {
                    //?????????MainActivity??????????????????????????????
                    loadX5();
                    openBackService(true);
                }
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                LogUtils.i("activityLife", "onActivityStarted " + activity);
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                LogUtils.i("activityLife", "onActivityResumed " + activity);
                currentActivity=activity;
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                LogUtils.i("activityLife", "onActivityPaused " + activity);
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                LogUtils.i("activityLife", "onActivityStopped " + activity);
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
                LogUtils.i("activityLife", "onActivitySaveInstanceState " + activity);
            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                activities.remove(activity);
                LogUtils.e("activityLife", "onActivityDestroyed " + activity + ",Activity??????=" + activities.size() + logAxt());
                if (activity.getClass().getName().equals(MeetingActivity.class.getName())) {
                    openFabService(false);
                }
                if (activities.isEmpty()) {
                    AppUtils.exitApp();
//                    openBackService(false);
//                    System.exit(0);
                }
            }
        });
        initSkin();
    }

    private void initSkin() {
        //Android-skin-support:https://www.jianshu.com/p/6c4b7fd66223
        SkinCompatManager.withoutActivity(this)
                .addInflater(new SkinAppCompatViewInflater())           // ???????????????????????????
                .addInflater(new SkinMaterialViewInflater())            // material design ?????????????????????[??????]
                .addInflater(new SkinConstraintViewInflater())          // ConstraintLayout ?????????????????????[??????]
                .addInflater(new SkinCardViewInflater())                // CardView v7 ?????????????????????[??????]
//                .setSkinStatusBarColorEnable(false)                     // ????????????????????????????????????[??????]
//                .setSkinWindowBackgroundEnable(false)                   // ??????windowBackground?????????????????????[??????]
                .loadSkin();
        GlobalValue.theme_type = (int) SharedPreferenceHelper.getData(this, SharedPreferenceHelper.key_theme_type, 0);
        LogUtils.i("initSkin ??????????????????=" + GlobalValue.theme_type);
        if (GlobalValue.theme_type == Constant.THEME_TYPE_DEFAULT) {
            SkinCompatManager.getInstance().restoreDefaultTheme();
        } else if (GlobalValue.theme_type == Constant.THEME_TYPE_RED) {
            SkinCompatManager.getInstance().loadSkin("red", SkinCompatManager.SKIN_LOADER_STRATEGY_PREFIX_BUILD_IN); // ????????????
        } else if (GlobalValue.theme_type == Constant.THEME_TYPE_YELLOW) {
            SkinCompatManager.getInstance().loadSkin("yellow", SkinCompatManager.SKIN_LOADER_STRATEGY_PREFIX_BUILD_IN); // ????????????
        }
    }


    private Intent fabService;
    private boolean fabServiceIsOpen;

    private void openFabService(boolean open) {
        if (open && !fabServiceIsOpen) {
            if (fabService == null) {
                fabService = new Intent(this, FabService.class);
            }
            startService(fabService);
            fabServiceIsOpen = true;
            LogUtils.d(TAG, "openFabService --> ?????????????????????");
        } else if (!open && fabServiceIsOpen) {
            if (fabService != null) {
                stopService(fabService);
                fabServiceIsOpen = false;
                LogUtils.d(TAG, "openFabService --> ?????????????????????");
            } else {
                LogUtils.d(TAG, "openFabService --> fabService????????????????????????");
            }
        }
    }

    private Intent backService;
    private boolean backServiceIsOpen;

    private void openBackService(boolean open) {
        if (open && !backServiceIsOpen) {
            if (backService == null) {
                backService = new Intent(this, BackService.class);
            }
            startService(backService);
            backServiceIsOpen = true;
            LogUtils.d(TAG, "openBackService --> ??????????????????");
        } else if (!open && backServiceIsOpen) {
            if (backService != null) {
                stopService(backService);
                backServiceIsOpen = false;
                LogUtils.d(TAG, "openBackService --> ??????????????????");
            } else {
                LogUtils.d(TAG, "openBackService --> backService????????????????????????");
            }
        }
    }

    private void loadX5() {
        boolean canLoadX5 = QbSdk.canLoadX5(appContext);
        LogUtils.i(TAG, "x5??????  ??????????????????X5?????? -->" + canLoadX5);
        if (canLoadX5) {
            initX5();
        } else {
            QbSdk.setDownloadWithoutWifi(true);
            QbSdk.setTbsListener(new TbsListener() {
                @Override
                public void onDownloadFinish(int i) {
                    LogUtils.i(TAG, "x5?????? onDownloadFinish -->??????X5?????????" + i);
                }

                @Override
                public void onInstallFinish(int i) {
                    LogUtils.i(TAG, "x5?????? onInstallFinish -->??????X5?????????" + i);
                    if (i == TbsListener.ErrorCode.INSTALL_SUCCESS_AND_RELEASE_LOCK) {
                        initX5();
                    }
                }

                @Override
                public void onDownloadProgress(int i) {
                    LogUtils.i(TAG, "x5?????? onDownloadProgress -->??????X5?????????" + i);
                }
            });
            App.threadPool.execute(() -> {
                //?????????????????????????????????
//                boolean needDownload = TbsDownloader.needDownload(mContext, TbsDownloader.DOWNLOAD_OVERSEA_TBS);
//                LogUtils.i(TAG, "loadX5 ????????????????????????X5??????" + needDownload);
//                if (needDownload) {
//                    // ?????????????????????????????????????????????????????????????????????
//                    // ??????: ?????????wifi?????????????????????????????????????????????
//                    // ????????????
//                    TbsDownloader.startDownload(mContext);
//                }
                LogUtils.i(TAG, "loadX5 ????????????X5??????");
                TbsDownloader.startDownload(appContext);
            });
        }
    }

    public void initX5() {
        LogUtils.i(TAG, "initX5 ");
        //????????????sdk?????????????????????initX5Enviroment?????????????????????????????????????????????????????????????????????x5?????????
//        QbSdk.initX5Environment(appContext, cb);
        //?????????????????????????????????????????????????????????QbSdk.preinit????????????
        QbSdk.preInit(appContext, cb);
    }

    public QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
        @Override
        public void onCoreInitFinished() {
            //x5?????????????????????????????????????????????????????????????????????????????????x5???????????????????????????x5?????????????????????????????????????????????
            LogUtils.i(TAG, "x5?????? onCoreInitFinished-->");
        }

        @Override
        public void onViewInitFinished(boolean b) {
            GlobalValue.initX5Finished = true;
            //ToastUtils.showShortToast(usedX5 ? R.string.tencent_x5_load_successfully : R.string.tencent_x5_load_failed);
            //x5????????????????????????????????????true??????x5?????????????????????????????????x5??????????????????????????????????????????????????????
            LogUtils.i(TAG, "x5?????? onViewInitFinished: ??????X5??????????????????: " + b);
            AgendaFragment.isNeedRestart = !b;
            EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_X5_INSTALL).build());
        }
    };

    private String logAxt() {
        String ret = "???????????????Activity:\n";
        for (Activity activity : activities) {
            String a = activity.getCallingPackage() + "  #  " + activity + "\n";
            ret += a;
        }
        return ret;
    }

    private void initScreenParam() {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager window = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        window.getDefaultDisplay().getMetrics(metric);
        GlobalValue.screen_width = metric.widthPixels;
        GlobalValue.screen_height = metric.heightPixels;
        GlobalValue.half_width = metric.widthPixels / 2;
        GlobalValue.half_height = metric.heightPixels / 2;
        width = metric.widthPixels;
        height = metric.heightPixels;
        dpi = metric.densityDpi;
        LogUtils.e(TAG, "initScreenParam :  dpi --> " + dpi);
        if (dpi > 320) {
            dpi = 320;
        }
        LogUtils.i(TAG, "initScreenParam ????????????=" + GlobalValue.screen_width + "," + GlobalValue.screen_height);
    }


    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int type = intent.getIntExtra(Constant.EXTRA_CAPTURE_TYPE, 0);
            LogUtils.e(TAG, "onReceive :   --> type= " + type + " , action = " + action);
            if (action.equals(Constant.ACTION_START_SCREEN_RECORD)) {
                LogUtils.e(TAG, "screen_shot --> ");
                startScreenRecord();
            } else if (action.equals(Constant.ACTION_STOP_SCREEN_RECORD)) {
                LogUtils.e(TAG, "stop_screen_shot --> ");
                if (stopScreenRecord()) {
                    LogUtils.i(TAG, "stopStreamInform: ?????????????????????..");
                } else {
                    LogUtils.e(TAG, "stopStreamInform: ???????????????????????? ");
                }
            } else if (action.equals(Constant.ACTION_STOP_SCREEN_RECORD_WHEN_EXIT_APP)) {
                LogUtils.i(TAG, "onReceive ??????APP?????????????????????----");
                stopScreenRecord();
                killAllActivity();
            }
        }
    };

    private void killAllActivity() {
        for (Activity activity : activities) {
            activity.finish();
        }
    }

    private boolean stopScreenRecord() {
        if (recorder != null) {
            recorder.quit();
            recorder = null;
            return true;
        } else {
            return false;
        }
    }

    private void startScreenRecord() {
        if (stopScreenRecord()) {
            LogUtils.i(TAG, "startScreenRecord: ?????????????????????");
        } else {
            if (mMediaProjection == null) {
                return;
            }
            if (recorder != null) {
                recorder.quit();
            }
            if (recorder == null) {
                recorder = new ScreenRecorder(width, height, maxBitRate, dpi, mMediaProjection, "");
            }
            recorder.start();//??????????????????
            LogUtils.i(TAG, "startScreenRecord: ??????????????????");
        }
    }
}
