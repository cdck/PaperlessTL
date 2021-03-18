package com.xlk.paperlesstl.view.admin.fragment.mid.screen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.model.Constant;
import com.xlk.paperlesstl.model.GlobalValue;
import com.xlk.paperlesstl.view.admin.BaseFragment;
import com.xlk.paperlesstl.view.admin.adapter.WmProjectorAdapter;
import com.xlk.paperlesstl.view.admin.adapter.WmScreenMemberAdapter;
import com.xlk.paperlesstl.view.admin.bean.DevMember;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * @author Created by xlk on 2021/3/6.
 * @desc
 */
public class ScreenFragment extends BaseFragment implements ScreenInterface, View.OnClickListener {

    private RecyclerView f_screen_rv_target;
    private RecyclerView f_screen_rv_pro;
    private RecyclerView f_screen_rv_source;
    private CheckBox f_screen_pro_cb;
    private CheckBox f_screen_target_cb;
    private Button f_screen_preview;
    private Button f_screen_stop_preview;
    private CheckBox f_screen_mandatory_cb;
    private Button f_screen_launch;
    private Button f_screen_stop;
    private ScreenPresenter presenter;
    private WmScreenMemberAdapter sourceMemberAdapter;
    private WmScreenMemberAdapter targetMemberAdapter;
    private WmProjectorAdapter projectorAdapter;
    public static boolean isAdminPage = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_screen, container, false);
        initView(inflate);
        presenter = new ScreenPresenter(getContext(), this);
        initAdapter();
        presenter.queryDeviceInfo();
        return inflate;
    }

    @Override
    protected void reShow() {
        presenter.queryDeviceInfo();
    }

    private void initView(View inflate) {
        f_screen_rv_target = (RecyclerView) inflate.findViewById(R.id.f_screen_rv_target);
        f_screen_rv_pro = (RecyclerView) inflate.findViewById(R.id.f_screen_rv_pro);
        f_screen_rv_source = (RecyclerView) inflate.findViewById(R.id.f_screen_rv_source);
        f_screen_pro_cb = (CheckBox) inflate.findViewById(R.id.f_screen_pro_cb);
        f_screen_target_cb = (CheckBox) inflate.findViewById(R.id.f_screen_target_cb);
        f_screen_preview = (Button) inflate.findViewById(R.id.f_screen_preview);
        f_screen_stop_preview = (Button) inflate.findViewById(R.id.f_screen_stop_preview);
        f_screen_mandatory_cb = (CheckBox) inflate.findViewById(R.id.f_screen_mandatory_cb);
        f_screen_launch = (Button) inflate.findViewById(R.id.f_screen_launch);
        f_screen_stop = (Button) inflate.findViewById(R.id.f_screen_stop);

        f_screen_preview.setOnClickListener(this);
        f_screen_stop_preview.setOnClickListener(this);
        f_screen_launch.setOnClickListener(this);
        f_screen_stop.setOnClickListener(this);
    }

    private void initAdapter() {
        sourceMemberAdapter = new WmScreenMemberAdapter(R.layout.item_single_button, presenter.sourceMembers);
        f_screen_rv_source.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        f_screen_rv_source.setAdapter(sourceMemberAdapter);
        sourceMemberAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                sourceMemberAdapter.clearChoose();
                sourceMemberAdapter.choose(presenter.sourceMembers.get(position).getDeviceDetailInfo().getDevcieid());
            }
        });

        targetMemberAdapter = new WmScreenMemberAdapter(R.layout.item_single_button, presenter.targetMembers);
        f_screen_rv_target.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        f_screen_rv_target.setAdapter(targetMemberAdapter);
        targetMemberAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                targetMemberAdapter.choose(presenter.targetMembers.get(position).getDeviceDetailInfo().getDevcieid());
                f_screen_target_cb.setChecked(targetMemberAdapter.isChooseAll());
            }
        });
        f_screen_target_cb.setOnClickListener(v -> {
            boolean checked = f_screen_target_cb.isChecked();
            f_screen_target_cb.setChecked(checked);
            targetMemberAdapter.setChooseAll(checked);
        });

        projectorAdapter = new WmProjectorAdapter(R.layout.item_single_button, presenter.onLineProjectors);
        f_screen_rv_pro.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        f_screen_rv_pro.setAdapter(projectorAdapter);
        projectorAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                projectorAdapter.choose(presenter.onLineProjectors.get(position).getDevcieid());
                f_screen_pro_cb.setChecked(projectorAdapter.isChooseAll());
            }
        });
        f_screen_pro_cb.setOnClickListener(v -> {
            boolean checked = f_screen_pro_cb.isChecked();
            f_screen_pro_cb.setChecked(checked);
            projectorAdapter.setChooseAll(checked);
        });
    }

    @Override
    public void notifyOnLineAdapter() {
        if (sourceMemberAdapter != null) {
            sourceMemberAdapter.notifyDataSetChanged();
            sourceMemberAdapter.notifyChecks();
        }
        if (targetMemberAdapter != null) {
            targetMemberAdapter.notifyDataSetChanged();
            targetMemberAdapter.notifyChecks();
        }
        if (projectorAdapter != null) {
            projectorAdapter.notifyDataSetChanged();
            projectorAdapter.notifyChecks();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.f_screen_preview:
                DevMember choose = sourceMemberAdapter.getChoose();
                if (choose != null) {
                    int devcieid = choose.getDeviceDetailInfo().getDevcieid();
                    List<Integer> temps = new ArrayList<>();
                    temps.add(Constant.RESOURCE_ID_0);
                    List<Integer> ids = new ArrayList<>();
                    ids.add(GlobalValue.localDeviceId);
                    jni.streamPlay(devcieid, 2, 0, temps, ids);
                } else {
                    ToastUtils.showShort(R.string.please_choose_source);
                }
                break;
            case R.id.f_screen_stop_preview:

                break;
            case R.id.f_screen_launch:
                startScreen();
                break;
            case R.id.f_screen_stop:
                stopScreen();
                break;
            default:
                break;
        }
    }

    private void stopScreen() {
        List<Integer> ids = targetMemberAdapter.getChooseIds();
        ids.addAll(projectorAdapter.getChooseIds());
        if (!ids.isEmpty()) {
            List<Integer> temps = new ArrayList<>();
            temps.add(0);
            jni.stopResourceOperate(temps, ids);
        } else {
            ToastUtils.showShort(R.string.please_choose_stop_target);
        }
    }

    private void startScreen() {
        DevMember choose = sourceMemberAdapter.getChoose();
        if (choose != null) {
            List<Integer> ids = targetMemberAdapter.getChooseIds();
            ids.addAll(projectorAdapter.getChooseIds());
            if (!ids.isEmpty()) {
                List<Integer> temps = new ArrayList<>();
                temps.add(Constant.RESOURCE_ID_0);
                int devcieid = choose.getDeviceDetailInfo().getDevcieid();
                int triggeruserval = f_screen_mandatory_cb.isChecked() ? InterfaceMacro.Pb_TriggerUsedef.Pb_EXCEC_USERDEF_FLAG_NOCREATEWINOPER_VALUE : 0;
                jni.streamPlay(devcieid, 2, triggeruserval, temps, ids);
            } else {
                ToastUtils.showShort(R.string.please_choose_screen_target);
            }
        } else {
            ToastUtils.showShort(R.string.please_choose_source);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }
}
