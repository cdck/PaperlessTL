package com.xlk.paperlesstl.view.fragment.bullet;

import android.view.View;
import android.widget.EditText;

import com.blankj.utilcode.util.ToastUtils;
import com.mogujie.tt.protobuf.InterfaceBullet;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.adapter.NoticeAdapter;
import com.xlk.paperlesstl.view.base.BaseFragment;

import java.util.ArrayList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.xlk.paperlesstl.util.ConvertUtil.s2b;

/**
 * @author Created by xlk on 2021/3/13.
 * @desc
 */
public class BulletFragment extends BaseFragment<BulletPresenter> implements BulletContract.View {
    private RecyclerView rvNotice;
    private EditText edtTitle;
    private EditText edtContent;
    private NoticeAdapter noticeAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_notice;
    }

    @Override
    protected void initView(View inflate) {
        rvNotice = (RecyclerView) inflate.findViewById(R.id.rv_notice);
        edtTitle = (EditText) inflate.findViewById(R.id.edt_title);
        edtContent = (EditText) inflate.findViewById(R.id.edt_content);
        inflate.findViewById(R.id.btn_add).setOnClickListener(v -> {
            String title = edtTitle.getText().toString().trim();
            String content = edtContent.getText().toString().trim();
            if(title.isEmpty() && content.isEmpty()){
                ToastUtils.showShort(R.string.please_enter_title_or_content);
                return;
            }
//            if (title.isEmpty()) {
//                ToastUtils.showShort(R.string.please_enter_title_first);
//                return;
//            }
//            if (content.isEmpty()) {
//                ToastUtils.showShort(R.string.please_enter_content_first);
//                return;
//            }
            InterfaceBullet.pbui_Item_BulletDetailInfo build = InterfaceBullet.pbui_Item_BulletDetailInfo.newBuilder()
                    .setTitle(s2b(title))
                    .setContent(s2b(content))
                    .build();
            jni.addBullet(build);
        });
        inflate.findViewById(R.id.btn_modify).setOnClickListener(v -> {
            InterfaceBullet.pbui_Item_BulletDetailInfo selected = noticeAdapter.getSelected();
            if (selected == null) {
                ToastUtils.showShort(R.string.please_choose_notice_first);
                return;
            }
            String title = edtTitle.getText().toString().trim();
            String content = edtContent.getText().toString().trim();
            if (title.isEmpty()) {
                ToastUtils.showShort(R.string.please_enter_title_first);
                return;
            }
            if (content.isEmpty()) {
                ToastUtils.showShort(R.string.please_enter_content_first);
                return;
            }
            InterfaceBullet.pbui_Item_BulletDetailInfo build = InterfaceBullet.pbui_Item_BulletDetailInfo.newBuilder()
                    .setBulletid(selected.getBulletid())
                    .setTimeouts(selected.getTimeouts())
                    .setStarttime(selected.getStarttime())
                    .setType(selected.getType())
                    .setTitle(s2b(title))
                    .setContent(s2b(content))
                    .build();
            jni.modifyBullet(build);
        });
        inflate.findViewById(R.id.btn_delete).setOnClickListener(v -> {
            InterfaceBullet.pbui_Item_BulletDetailInfo selected = noticeAdapter.getSelected();
            if (selected == null) {
                ToastUtils.showShort(R.string.please_choose_notice_first);
                return;
            }
            jni.deleteBullet(selected);
        });
    }

    @Override
    protected BulletPresenter initPresenter() {
        return new BulletPresenter(this);
    }

    @Override
    protected void initial() {
        presenter.queryBullet();
    }

    @Override
    protected void onShow() {
        initial();
    }

    @Override
    public void updateNoticeList() {
        if (noticeAdapter == null) {
            noticeAdapter = new NoticeAdapter(presenter.noticeLists);
            rvNotice.setLayoutManager(new LinearLayoutManager(getContext()));
            rvNotice.setAdapter(noticeAdapter);
            noticeAdapter.setOnItemClickListener((adapter, view, position) -> {
                InterfaceBullet.pbui_Item_BulletDetailInfo bullet = presenter.noticeLists.get(position);
                noticeAdapter.choose(bullet.getBulletid());
                edtTitle.setText(bullet.getTitle().toStringUtf8());
                edtContent.setText(bullet.getContent().toStringUtf8());
            });
            noticeAdapter.addChildClickViewIds(R.id.btn_release, R.id.btn_close);
            noticeAdapter.setOnItemChildClickListener((adapter, view, position) -> {
                InterfaceBullet.pbui_Item_BulletDetailInfo bullet = presenter.noticeLists.get(position);
                if (view.getId() == R.id.btn_release) {
                    jni.launchBullet(bullet);
                } else {
                    jni.stopBullect(bullet.getBulletid(), new ArrayList<>());
                }
            });
        } else {
            noticeAdapter.notifyDataSetChanged();
        }
    }
}
