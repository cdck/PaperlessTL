package com.xlk.paperlesstl.view.videochat;

import com.google.protobuf.InvalidProtocolBufferException;
import com.xlk.paperlesstl.model.data.EventMessage;
import com.xlk.paperlesstl.view.base.BasePresenter;


/**
 * @author Created by xlk on 2020/12/18.
 * @desc
 */
public class ChatVideoPresenter extends BasePresenter<ChatVideoContract.View> implements ChatVideoContract.Presenter {
    public ChatVideoPresenter(ChatVideoContract.View view) {
        super(view);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {

    }
}
