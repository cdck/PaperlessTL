package com.xlk.paperlesstl.view.fragment.agenda;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.tencent.smtt.sdk.TbsDownloader;
import com.tencent.smtt.sdk.TbsReaderView;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.view.base.BaseFragment;

import java.util.Objects;

import static com.xlk.paperlesstl.App.appContext;
import static com.xlk.paperlesstl.model.GlobalValue.initX5Finished;

/**
 * @author Created by xlk on 2021/3/8.
 * @desc
 */
public class AgendaFragment extends BaseFragment<AgendaPresenter> implements AgendaContract.View, TbsReaderView.ReaderCallback {
    /**
     * =true 加载的是系统内核（默认），=false 加载的是X5内核
     */
    public static boolean isNeedRestart;
    private LinearLayout agenda_root;
    private ProgressBar progress_bar;
    private ScrollView agenda_sv;
    private TextView agenda_tv;
    private TbsReaderView tbsReaderView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_meet_agenda;
    }

    @Override
    protected void initView(View inflate) {
        agenda_root = inflate.findViewById(R.id.agenda_root);
        progress_bar = inflate.findViewById(R.id.progress_bar);
        agenda_sv = inflate.findViewById(R.id.agenda_sv);
        agenda_tv = inflate.findViewById(R.id.agenda_tv);
    }

    @Override
    protected AgendaPresenter initPresenter() {
        return new AgendaPresenter(this);
    }

    @Override
    protected void initial() {
        presenter.queryAgenda();
    }

    @Override
    protected void onShow() {
        presenter.queryAgenda();
    }

    @Override
    public void initDefault() {
        agenda_tv.setText("");
        if (tbsReaderView != null) {
            agenda_root.removeView(tbsReaderView);
            tbsReaderView.onStop();
            tbsReaderView = null;
        }
    }

    @Override
    public void updateAgendaTv(String content) {
        //也有可能下载X5内核完成，但是议程变成文本类
        progress_bar.setVisibility(View.GONE);
        agenda_sv.setVisibility(View.VISIBLE);
        agenda_tv.setText(content);
    }

    @Override
    public void displayFile(String path) {
        agenda_sv.setVisibility(View.GONE);
        if (initX5Finished) {
            //加载完成
            if (isNeedRestart) {
                //加载的是系统内核
                progress_bar.setVisibility(View.GONE);
                agenda_sv.setVisibility(View.VISIBLE);
                agenda_tv.setText(getString(R.string.init_x5_failure));
                return;
            }
        } else {
            //没有加载完成
            progress_bar.setVisibility(View.VISIBLE);
            TbsDownloader.startDownload(appContext);
            return;
        }
        /* **** **  加载完成，并且加载的是X5内核  ** **** */
        progress_bar.setVisibility(View.GONE);
        String tempPath = Environment.getExternalStorageDirectory().getPath();
        tbsReaderView = new TbsReaderView(Objects.requireNonNull(getContext()), this);
        agenda_root.addView(tbsReaderView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        Bundle bundle = new Bundle();
        bundle.putString("filePath", path);
        bundle.putString("tempPath", tempPath);//bsReaderTemp
        String suffix = path.substring(path.lastIndexOf(".") + 1);
        LogUtils.i(TAG, "displayFile 打开文件 -->" + path + "， 后缀： " + suffix + ", tempPath= " + tempPath);
        try {
            boolean result = tbsReaderView.preOpen(suffix, false);
            LogUtils.e(TAG, "displayFile :  result --> " + result);
            if (result) {
                tbsReaderView.openFile(bundle);
            } else {
                ToastUtils.showShort(R.string.not_supported);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //不停止掉，下次进入是打开文件会卡在加载中状态
        if (tbsReaderView != null) {
            tbsReaderView.onStop();
            tbsReaderView = null;
        }
    }

    @Override
    public void onCallBackAction(Integer integer, Object o, Object o1) {

    }
}
