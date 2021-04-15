package com.xlk.paperlesstl.view.fragment.annotate;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.adapter.FileAdapter;
import com.xlk.paperlesstl.adapter.SeatMemberAdapter;
import com.xlk.paperlesstl.ui.RvItemDecoration;
import com.xlk.paperlesstl.view.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Created by xlk on 2021/3/9.
 * @desc 查看批注
 */
public class AnnotateFragment extends BaseFragment<AnnotatePresenter> implements AnnotateContract.View {

    private RecyclerView rv_member, rv_file;
    private SeatMemberAdapter seatMemberAdapter;
    private FileAdapter fileAdapter;
    public List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> currentFiles = new ArrayList<>();
    private int currentDeviceId;
    /**
     * 如果 currentMemberId=0 则会显示出管理员
     */
    private int currentMemberId = -1;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_meet_annotate;
    }

    @Override
    protected void initView(View inflate) {
        rv_member = inflate.findViewById(R.id.rv_member);
        rv_file = inflate.findViewById(R.id.rv_file);
    }

    @Override
    protected AnnotatePresenter initPresenter() {
        return new AnnotatePresenter(this);
    }

    @Override
    protected void initial() {
        presenter.queryMember();
        presenter.queryFile();
    }

    @Override
    protected void onShow() {
        initial();
    }

    @Override
    public void updateMember() {
        if (seatMemberAdapter == null) {
            seatMemberAdapter = new SeatMemberAdapter(presenter.seatMembers);
            rv_member.addItemDecoration(new RvItemDecoration(getContext()));
            rv_member.setLayoutManager(new LinearLayoutManager(getContext()));
            rv_member.setAdapter(seatMemberAdapter);
            seatMemberAdapter.setOnItemClickListener((adapter, view, position) -> {
                currentDeviceId = presenter.seatMembers.get(position).getSeatDetailInfo().getSeatid();
                currentMemberId = presenter.seatMembers.get(position).getMemberDetailInfo().getPersonid();
                seatMemberAdapter.setSelectedId(currentDeviceId);
                if (presenter.hasPermission(currentDeviceId)) {
                    updateFiles();
                } else {
                    cleanFile();
                    jni.applyPermission(currentDeviceId,
                            InterfaceMacro.Pb_MemberPermissionPropertyID.Pb_memperm_postilview_VALUE);
                }
            });
        } else {
            seatMemberAdapter.notifyDataSetChanged();
        }
    }

    private void cleanFile() {
        currentFiles.clear();
        if (fileAdapter == null) {
            fileAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void updateFiles() {
        currentFiles.clear();
        for (int i = 0; i < presenter.annotateFiles.size(); i++) {
            InterfaceFile.pbui_Item_MeetDirFileDetailInfo item = presenter.annotateFiles.get(i);
            int uploaderid = item.getUploaderid();
            if (uploaderid == currentMemberId) {
                currentFiles.add(item);
            }
        }
        if (fileAdapter == null) {
            fileAdapter = new FileAdapter(false, currentFiles);
            rv_file.setLayoutManager(new LinearLayoutManager(getContext()));
            rv_file.setAdapter(fileAdapter);
        } else {
            fileAdapter.notifyDataSetChanged();
        }
    }
}
