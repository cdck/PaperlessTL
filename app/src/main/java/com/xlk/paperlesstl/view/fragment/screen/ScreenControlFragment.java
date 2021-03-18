package com.xlk.paperlesstl.view.fragment.screen;

import android.view.View;

import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.view.base.BaseFragment;

/**
 * @author Created by xlk on 2021/3/13.
 * @desc
 */
public class ScreenControlFragment extends BaseFragment<ScreenControlPresenter> implements ScreenControlContract.View {
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_screen_control;
    }

    @Override
    protected void initView(View inflate) {

    }

    @Override
    protected ScreenControlPresenter initPresenter() {
        return null;
    }

    @Override
    protected void initial() {

    }
}
