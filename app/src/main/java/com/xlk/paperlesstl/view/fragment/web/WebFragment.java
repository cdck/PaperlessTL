package com.xlk.paperlesstl.view.fragment.web;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.google.protobuf.ByteString;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.WebView;
import com.wang.avi.AVLoadingIndicatorView;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.adapter.UrlAdapter;
import com.xlk.paperlesstl.ui.X5WebView;
import com.xlk.paperlesstl.view.base.BaseFragment;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * @author Created by xlk on 2021/3/11.
 * @desc
 */
public class WebFragment extends BaseFragment<WebPresenter> implements WebContract.View {

    private EditText edt_url;
    private RecyclerView rv_url;
    private X5WebView web_view;
    private AVLoadingIndicatorView web_loading;
    private UrlAdapter urlAdapter;
    private LinearLayout ll_web_view;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_meet_web;
    }

    @Override
    protected void initView(View inflate) {
        ll_web_view = inflate.findViewById(R.id.ll_web_view);
        edt_url = inflate.findViewById(R.id.edt_url);
        rv_url = inflate.findViewById(R.id.rv_url);
        web_view = inflate.findViewById(R.id.web_view);
        web_loading = inflate.findViewById(R.id.web_loading);
        inflate.findViewById(R.id.iv_go).setOnClickListener(v -> {
            String url = edt_url.getText().toString();
            if (!url.isEmpty()) {
                rv_url.setVisibility(View.GONE);
                ll_web_view.setVisibility(View.VISIBLE);
                web_view.loadUrl(uriHttpFirst(url));
            }
        });
        inflate.findViewById(R.id.iv_home).setOnClickListener(v -> {
            rv_url.setVisibility(View.VISIBLE);
            ll_web_view.setVisibility(View.GONE);
        });
    }

    //地址HTTP协议判断，无HTTP打头的，增加http://，并返回。
    private String uriHttpFirst(String strUri) {
        if (strUri.indexOf("http://", 0) != 0 && strUri.indexOf("https://", 0) != 0) {
            strUri = "http://" + strUri;
        }
        return strUri;
    }

    @Override
    protected WebPresenter initPresenter() {
        return new WebPresenter(this);
    }

    @Override
    protected void initial() {
        web_view.setWebViewClient(new com.tencent.smtt.sdk.WebViewClient() {
            @Override
            public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
                web_loading.setVisibility(View.VISIBLE);
                super.onPageStarted(webView, s, bitmap);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest webResourceRequest) {
                return super.shouldOverrideUrlLoading(webView, webResourceRequest);
            }

            @Override
            public void onPageFinished(com.tencent.smtt.sdk.WebView webView, String s) {
                LogUtils.e(TAG, "onPageFinished : 加载结束 url --> " + s);
                edt_url.setText(s != null ? s : "");
                web_loading.setVisibility(View.GONE);
                super.onPageFinished(webView, s);
            }
        });
        presenter.queryWebUrl();
    }

    @Override
    public void updateUrlList() {
        if (urlAdapter == null) {
            urlAdapter = new UrlAdapter(presenter.urlLists);
            rv_url.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
            rv_url.setAdapter(urlAdapter);
            urlAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    rv_url.setVisibility(View.GONE);
                    ll_web_view.setVisibility(View.VISIBLE);
                    String addr = presenter.urlLists.get(position).getAddr().toStringUtf8();
                    web_view.loadUrl(uriHttpFirst(addr));
                }
            });
        } else {
            urlAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onHide() {
        web_view.setAlpha(0);
        onPause();
    }

    @Override
    protected void onShow() {
        web_view.setAlpha(1);
        onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        web_view.onPause();
        if (web_view != null) {
            String videoJs = "javascript: var v = document.getElementsByTagName('video'); for(var i=0;i<v.length;i++){v[i].pause();} ";
            web_view.loadUrl(videoJs);//遍历所有的Vedio标签，主动调用暂停方法
            web_view.onPause();
            web_view.pauseTimers();
//            ll_web_view.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (web_view != null) {
            web_view.onResume();
            web_view.resumeTimers();
//            ll_web_view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (web_view != null) {
            web_view.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            //webview停止加载
            web_view.stopLoading();
            //webview销毁
            web_view.destroy();
            //webview清理内存
            web_view.clearCache(true);
            //webview清理历史记录
            web_view.clearHistory();
        }
    }
}
