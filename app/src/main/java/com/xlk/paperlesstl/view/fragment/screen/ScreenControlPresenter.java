package com.xlk.paperlesstl.view.fragment.screen;

import com.google.protobuf.InvalidProtocolBufferException;
import com.xlk.paperlesstl.model.data.EventMessage;
import com.xlk.paperlesstl.view.base.BasePresenter;

/**
 * @author Created by xlk on 2021/3/13.
 * @desc
 */
public class ScreenControlPresenter extends BasePresenter<ScreenControlContract.View> implements ScreenControlContract.Presenter {
    public ScreenControlPresenter(ScreenControlContract.View view) {
        super(view);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {

    }
}
