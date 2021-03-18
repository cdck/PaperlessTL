package com.xlk.paperlesstl.view.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.blankj.utilcode.util.LogUtils;
import com.xlk.paperlesstl.jni.JniHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * @author Created by xlk on 2021/3/1.
 * @desc
 */
public abstract class BaseFragment <T extends BasePresenter> extends Fragment implements IBaseView {
    protected String TAG = BaseFragment.class.getSimpleName();
    protected T presenter;
    protected JniHelper jni = JniHelper.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(getLayoutId(), container, false);
        initView(inflate);
        presenter = initPresenter();
        initial();
        return inflate;
    }

    protected abstract int getLayoutId();

    protected abstract void initView(View inflate);

    protected abstract T initPresenter();

    protected abstract void initial();

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            onHide();
        } else {
            onShow();
        }
        super.onHiddenChanged(hidden);
    }

    protected void onHide() {

    }

    protected void onShow() {

    }

    protected void dismissPop(PopupWindow pop) {
        if (pop != null && pop.isShowing()) {
            pop.dismiss();
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

//    protected void showSuccessfulToast(){
//        ToastUtils.make()
//                .setLeftIcon(R.drawable.ic_successful)
//                .setGravity(Gravity.CENTER, 0, 0)
//                .setTextColor(0xFF3475D3)
//                .setTextSize(18)
//                .setBgResource(R.drawable.round_gray_bg)
//                .setDurationIsLong(true)
//                .show(R.string.launch_successful);
//    }

    @Override
    public void onAttach(Context context) {
        LogUtils.i("F_life", this.getClass().getSimpleName() + ".onAttach :   --> ");
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.i("F_life", this.getClass().getSimpleName() + ".onCreate :   --> ");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.i("F_life", this.getClass().getSimpleName() + ".onActivityCreated :   --> ");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        LogUtils.i("F_life", this.getClass().getSimpleName() + ".onStart :   --> ");
        super.onStart();
    }

    @Override
    public void onResume() {
        LogUtils.i("F_life", this.getClass().getSimpleName() + ".onResume :   --> ");
        super.onResume();
    }

    @Override
    public void onPause() {
        LogUtils.i("F_life", this.getClass().getSimpleName() + ".onPause :   --> ");
        super.onPause();
    }

    @Override
    public void onStop() {
        LogUtils.i("F_life", this.getClass().getSimpleName() + ".onStop :   --> ");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        LogUtils.i("F_life", this.getClass().getSimpleName() + ".onDestroyView :   --> ");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        LogUtils.i("F_life", this.getClass().getSimpleName() + ".onDestroy :   --> ");
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    public void onDetach() {
        LogUtils.i("F_life", this.getClass().getSimpleName() + ".onDetach :   --> ");
        super.onDetach();
    }
}

