package com.xlk.paperlesstl.view.fragment.web;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.paperlesstl.model.data.EventMessage;
import com.xlk.paperlesstl.view.base.BasePresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/3/11.
 * @desc
 */
public class WebPresenter extends BasePresenter<WebContract.View> implements WebContract.Presenter {

    public List<InterfaceBase.pbui_Item_UrlDetailInfo> urlLists = new ArrayList<>();

    public WebPresenter(WebContract.View view) {
        super(view);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEFAULTURL_VALUE: {
                queryWebUrl();
                break;
            }
        }
    }

    @Override
    public void queryWebUrl() {
        InterfaceBase.pbui_meetUrl object = jni.queryWebUrl();
        urlLists.clear();
        if (object != null) {
            urlLists.addAll(object.getItemList());
        }
        mView.updateUrlList();
    }
}
