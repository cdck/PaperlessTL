package com.xlk.paperlesstl.view.admin.fragment.pre.file;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.UriUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMeet;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.jni.JniHelper;
import com.xlk.paperlesstl.model.Constant;
import com.xlk.paperlesstl.model.GlobalValue;
import com.xlk.paperlesstl.util.FileUtil;
import com.xlk.paperlesstl.view.admin.BaseFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.xlk.paperlesstl.util.ConvertUtil.s2b;

/**
 * @author Created by xlk on 2020/10/21.
 * @desc
 */
public class AdminFileFragment extends BaseFragment implements AdminFileInterface, View.OnClickListener {

    private RecyclerView rv_dir;
    private RecyclerView rv_file;
    private EditText edt_dir_name, edt_file_name;
    private Button btn_dir_increase;
    private Button btn_dir_modify;
    private Button btn_dir_del;
    private Button btn_dir_permission;
    private Button btn_dir_sort;
    private Button btn_file_increase;
    private Button btn_file_modify;
    private Button btn_file_del;
    private Button btn_file_sort;
    private Button btn_file_history;
    private AdminFilePresenter presenter;
    private DirAdapter dirAdapter;
    private FileAdapter fileAdapter;
    private PopupWindow dirPermissionPop, sortFilePop, sortDirPop;
    private RecyclerView rv_permission_dir, rv_dir_permission_member;
    private DirNameAdapter dirNameAdapter;
    private DirPermissionMemberAdapter dirPermissionMemberAdapter;
    private CheckBox cbAll;
    private final int LOCAL_FILE_REQUEST_CODE = 1;
    private RecyclerView rv_sort_file_dir, rv_sort_file;
    private DirAdapter sortFiledirAdapter;
    private FileSortAdapter fileSortAdapter;
    private DirAdapter sortDirAdapter;
    private PopupWindow historyPop;
    private RecyclerView rv_history_meeting;
    private RecyclerView rv_history_dir;
    private RecyclerView rv_history_file;
    private HistoryMeetAdapter meetAdapter;
    private DirAdapter historyDirAdapter;
    private FileSortAdapter historyFileAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_admin_file, container, false);
        initView(inflate);
        presenter = new AdminFilePresenter(this);
        presenter.queryDir();
        return inflate;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            presenter.queryDir();
        }
    }

    private void initView(View rootView) {
        this.rv_dir = (RecyclerView) rootView.findViewById(R.id.rv_dir);
        this.rv_file = (RecyclerView) rootView.findViewById(R.id.rv_file);
        this.edt_dir_name = (EditText) rootView.findViewById(R.id.edt_dir_name);
        this.edt_file_name = (EditText) rootView.findViewById(R.id.edt_file_name);
        this.btn_dir_increase = (Button) rootView.findViewById(R.id.btn_dir_increase);
        this.btn_dir_modify = (Button) rootView.findViewById(R.id.btn_dir_modify);
        this.btn_dir_del = (Button) rootView.findViewById(R.id.btn_dir_del);
        this.btn_dir_permission = (Button) rootView.findViewById(R.id.btn_dir_permission);
        this.btn_dir_sort = (Button) rootView.findViewById(R.id.btn_dir_sort);
        this.btn_file_increase = (Button) rootView.findViewById(R.id.btn_file_increase);
        this.btn_file_modify = (Button) rootView.findViewById(R.id.btn_file_modify);
        this.btn_file_del = (Button) rootView.findViewById(R.id.btn_file_del);
        this.btn_file_sort = (Button) rootView.findViewById(R.id.btn_file_sort);
        this.btn_file_history = (Button) rootView.findViewById(R.id.btn_file_history);

        this.btn_dir_increase.setOnClickListener(this);
        this.btn_dir_modify.setOnClickListener(this);
        this.btn_dir_del.setOnClickListener(this);
        this.btn_dir_permission.setOnClickListener(this);
        this.btn_dir_sort.setOnClickListener(this);
        this.btn_file_increase.setOnClickListener(this);
        this.btn_file_modify.setOnClickListener(this);
        this.btn_file_del.setOnClickListener(this);
        this.btn_file_sort.setOnClickListener(this);
        this.btn_file_history.setOnClickListener(this);
    }

    @Override
    public void updateDirRv(List<InterfaceFile.pbui_Item_MeetDirDetailInfo> dirInfos) {
        //????????????????????????
        if (dirAdapter == null) {
            dirAdapter = new DirAdapter(R.layout.item_admin_file_dir, dirInfos);
            rv_dir.setLayoutManager(new LinearLayoutManager(getContext()));
            rv_dir.setAdapter(dirAdapter);
            dirAdapter.setOnItemClickListener((adapter, view, position) -> {
                InterfaceFile.pbui_Item_MeetDirDetailInfo dirItem = dirInfos.get(position);
                int dirId = dirItem.getId();
                dirAdapter.setSelected(dirId);
                presenter.setCurrentDirId(dirId);
                presenter.queryFileByDir(dirId);
                edt_dir_name.setText(dirItem.getName().toStringUtf8());
            });
            if (!dirInfos.isEmpty()) {
                int dirId = dirInfos.get(0).getId();
                dirAdapter.setSelected(dirId);
                presenter.setCurrentDirId(dirId);
                presenter.queryFileByDir(dirId);
            }
        } else {
            dirAdapter.notifyDataSetChanged();
        }
        //??????????????????PopupWindow????????????
        if (dirPermissionPop != null && dirPermissionPop.isShowing()) {
            LogUtils.i(TAG, "updateDirRv dirNameAdapter ??????");
            dirNameAdapter.notifyDataSetChanged();
        }
        //?????????????????????????????????
        if (historyPop != null && historyPop.isShowing()) {
            historyDirAdapter = new DirAdapter(R.layout.item_admin_file_dir, dirInfos);
            rv_history_dir.setLayoutManager(new LinearLayoutManager(getContext()));
            rv_history_dir.setAdapter(historyDirAdapter);
            historyDirAdapter.setOnItemClickListener((adapter, view, position) -> {
                int dirId = dirInfos.get(position).getId();
                presenter.setCurrentHistoryDirId(dirId);
                historyDirAdapter.setSelected(dirId);
                presenter.queryFileByDir(dirId);
            });
            if (!dirInfos.isEmpty()) {
                int dirId = dirInfos.get(0).getId();
                historyDirAdapter.setSelected(dirId);
                presenter.setCurrentHistoryDirId(dirId);
                presenter.queryFileByDir(dirId);
            }
        }
    }

    @Override
    public void updateDirFileRv(List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> dirFiles) {
        if (fileAdapter == null) {
            fileAdapter = new FileAdapter(R.layout.item_admin_file, dirFiles);
            rv_file.setLayoutManager(new LinearLayoutManager(getContext()));
            rv_file.setAdapter(fileAdapter);
            fileAdapter.setOnItemClickListener((adapter, view, position) -> {
                int mediaid = dirFiles.get(position).getMediaid();
                fileAdapter.setCheck(mediaid);
                //???????????????????????????
                InterfaceFile.pbui_Item_MeetDirFileDetailInfo lastCheckFile = fileAdapter.getLastCheckFile();
                edt_file_name.setText(lastCheckFile != null ? lastCheckFile.getName().toStringUtf8() : "");
            });
            fileAdapter.addChildClickViewIds(R.id.item_btn_open);
            fileAdapter.setOnItemChildClickListener((adapter, view, position) -> {
                InterfaceFile.pbui_Item_MeetDirFileDetailInfo dirFile = dirFiles.get(position);
                if (FileUtil.isAudio(dirFile.getName().toStringUtf8())) {
                    List<Integer> devIds = new ArrayList<>();
                    devIds.add(GlobalValue.localDeviceId);
                    JniHelper.getInstance().mediaPlayOperate(dirFile.getMediaid(), devIds, 0, Constant.RESOURCE_ID_0, 0, 0);
                } else {
                    FileUtil.openFile(getContext(), Constant.DOWNLOAD_DIR, dirFile.getName().toStringUtf8(), dirFile.getMediaid());
                }
            });
        } else {
            fileAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_dir_increase:
                createDir();
                break;
            case R.id.btn_dir_modify:
                modifyDir();
                break;
            case R.id.btn_dir_del:
                deleteDir();
                break;
            case R.id.btn_dir_permission:
                showDirPermissionPop(presenter.getDirData());
                break;
            case R.id.btn_dir_sort:
                showSortDirPop(presenter.getSortDirData());
                break;
            case R.id.btn_file_increase:
                if (dirAdapter != null && dirAdapter.getSelected() != null) {
                    chooseLocalFile();
                } else {
                    ToastUtils.showShort(R.string.please_choose_dir_first);
                }
                break;
            case R.id.btn_file_modify:
                modifyFile();
                break;
            case R.id.btn_file_del:
                deleteFile();
                break;
            case R.id.btn_file_sort:
                showSortFilePop(presenter.getDirData());
                break;
            case R.id.btn_file_history:
                if (dirAdapter != null && dirAdapter.getSelected() != null) {
                    showOtherMeetFile(dirAdapter.getSelected().getId());
                } else {
                    ToastUtils.showShort(R.string.please_choose_dir_first);
                }
                break;
            default:
                break;
        }
    }

    /**
     * ????????????????????????
     * ????????????????????????????????????????????????
     * ????????????????????????????????????????????????????????????????????????
     * ??????????????????????????????????????????
     * ??????????????????????????????????????????????????????????????????
     */
    private void showOtherMeetFile(int dirId) {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_history_meeting, null);
        historyPop = new PopupWindow(inflate, GlobalValue.screen_width - 100, GlobalValue.screen_height - 100);
        historyPop.setBackgroundDrawable(new BitmapDrawable());
        //??????popWindow?????????????????????????????????????????????????????????true
        historyPop.setTouchable(true);
        //true:???????????????????????????
        historyPop.setOutsideTouchable(true);
        historyPop.setFocusable(true);
//        historyPop.setAnimationStyle(R.style.pop_Animation);
        historyPop.showAtLocation(btn_file_history, Gravity.CENTER, 0, 0);
        rv_history_meeting = inflate.findViewById(R.id.rv_meeting);
        meetAdapter = new HistoryMeetAdapter(R.layout.item_table_2, presenter.meetings);
        rv_history_meeting.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_history_meeting.setAdapter(meetAdapter);
        meetAdapter.setOnItemClickListener((adapter, view, position) -> {
            int id = presenter.meetings.get(position).getId();
            meetAdapter.setSelected(id);
            presenter.setCurrentHistoryDirId(0);
            presenter.switchMeeting(id);
        });
        rv_history_dir = inflate.findViewById(R.id.rv_dir);
        rv_history_file = inflate.findViewById(R.id.rv_file);
        inflate.findViewById(R.id.btn_confirm_import).setOnClickListener(v -> {
            if (historyDirAdapter != null) {
                InterfaceFile.pbui_Item_MeetDirFileDetailInfo selected = historyFileAdapter.getSelected();
                if (selected != null) {
                    presenter.exit();
                    jni.addFile2Dir(dirId, selected);
                    historyPop.dismiss();
                } else {
                    ToastUtils.showShort(R.string.please_choose_file_first);
                }
            }
        });
        inflate.findViewById(R.id.btn_exit).setOnClickListener(v -> {
            presenter.exit();
            historyPop.dismiss();
        });
        historyPop.setOnDismissListener(() -> presenter.setCurrentHistoryDirId(0));
    }

    @Override
    public void updateMeetingRv(List<InterfaceMeet.pbui_Item_MeetMeetInfo> meetings) {
        if (historyPop != null && historyPop.isShowing()) {
            meetAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void updateHistoryDirFileRv(List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> dirFiles) {
        if (historyPop != null && historyPop.isShowing()) {
            historyFileAdapter = new FileSortAdapter(R.layout.item_sort_file, dirFiles);
            rv_history_file.setLayoutManager(new LinearLayoutManager(getContext()));
            rv_history_file.setAdapter(historyFileAdapter);
            historyFileAdapter.setOnItemClickListener((adapter, view, position) -> {
                int mediaid = dirFiles.get(position).getMediaid();
                historyFileAdapter.setSelectedId(mediaid);
            });
        }
    }

    private void showSortDirPop(List<InterfaceFile.pbui_Item_MeetDirDetailInfo> sortDirData) {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_sort_dir, null);
        View admin_fl = getActivity().findViewById(R.id.admin_fl);
        int width = admin_fl.getWidth();
        int height = admin_fl.getHeight();
        LogUtils.i(TAG, "showOtherMeetFile fragment????????? width=" + width + ",height=" + height);
        sortDirPop = new PopupWindow(inflate, width * 2 / 3, height * 2 / 3);
        sortDirPop.setBackgroundDrawable(new BitmapDrawable());
        // ??????popWindow?????????????????????????????????????????????????????????true
        sortDirPop.setTouchable(true);
        // true:???????????????????????????
        sortDirPop.setOutsideTouchable(true);
        sortDirPop.setFocusable(true);
//        sortDirPop.setAnimationStyle(R.style.pop_Animation);
        sortDirPop.showAtLocation(btn_file_history, Gravity.CENTER, 0, 0);
        RecyclerView rv_sort_dir = inflate.findViewById(R.id.rv_sort_dir);
        if (sortDirAdapter == null) {
            sortDirAdapter = new DirAdapter(R.layout.item_admin_file_dir, sortDirData);
            rv_sort_dir.setLayoutManager(new LinearLayoutManager(getContext()));
            rv_sort_dir.setAdapter(sortDirAdapter);
            sortDirAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    sortDirAdapter.setSelected(sortDirData.get(position).getId());
                }
            });
        } else {
            sortDirAdapter.notifyDataSetChanged();
        }
        inflate.findViewById(R.id.btn_move_up).setOnClickListener(v -> {
            InterfaceFile.pbui_Item_MeetDirDetailInfo selected = sortDirAdapter.getSelected();
            if (selected == null) {
                ToastUtils.showShort(R.string.please_choose_dir_first);
                return;
            }
            if (selected.getId() == 1 || selected.getId() == 2) {
                //???????????????????????????????????????????????????
                ToastUtils.showShort(R.string.cannot_move_this_directory);
                return;
            }
            int index = 0;
            for (int i = 0; i < sortDirData.size(); i++) {
                if (selected.getId() == sortDirData.get(i).getId()) {
                    index = i;
                    break;
                }
            }
            if (index == 0) {
                //????????????????????????????????????????????????????????????
                sortDirData.remove(index);
                sortDirData.add(selected);
            } else {
                int preDirId = sortDirData.get(index - 1).getId();
                if (preDirId == 1 || preDirId == 2) {
                    //???????????????????????????????????????????????????????????????????????????
                    ToastUtils.showShort(R.string.cannot_move_up);
                } else {
                    Collections.swap(sortDirData, index, index - 1);
                }
            }
            sortDirAdapter.notifyDataSetChanged();
        });
        inflate.findViewById(R.id.btn_move_down).setOnClickListener(v -> {
            InterfaceFile.pbui_Item_MeetDirDetailInfo selected = sortDirAdapter.getSelected();
            if (selected == null) {
                ToastUtils.showShort(R.string.please_choose_dir_first);
                return;
            }
            if (selected.getId() == 1 || selected.getId() == 2) {
                //???????????????????????????????????????????????????
                ToastUtils.showShort(R.string.cannot_move_this_directory);
                return;
            }
            int index = 0;
            for (int i = 0; i < sortDirData.size(); i++) {
                if (selected.getId() == sortDirData.get(i).getId()) {
                    index = i;
                    break;
                }
            }
            if (index == sortDirData.size() - 1) {
                //?????????????????????????????????????????????????????????????????????
                ToastUtils.showShort(R.string.cannot_move_down);
            } else {
                Collections.swap(sortDirData, index, index + 1);
            }
            sortDirAdapter.notifyDataSetChanged();
        });
        inflate.findViewById(R.id.btn_save).setOnClickListener(v -> {
            List<InterfaceFile.pbui_Item_MeetingDirPosItem> temps = new ArrayList<>();
            for (int i = 0; i < sortDirData.size(); i++) {
                InterfaceFile.pbui_Item_MeetDirDetailInfo item = sortDirData.get(i);
                InterfaceFile.pbui_Item_MeetingDirPosItem build = InterfaceFile.pbui_Item_MeetingDirPosItem.newBuilder()
                        .setDirid(item.getId())
                        .setPos(i)
                        .build();
                temps.add(build);
            }
            jni.modifyMeetDirSort(temps);
            sortDirPop.dismiss();
        });
        inflate.findViewById(R.id.btn_exit).setOnClickListener(v -> {
            sortDirPop.dismiss();
        });
    }

    private void showSortFilePop(List<InterfaceFile.pbui_Item_MeetDirDetailInfo> dirInfos) {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_sort_file, null);
        View admin_fl = getActivity().findViewById(R.id.admin_fl);
        int width = admin_fl.getWidth();
        int height = admin_fl.getHeight();
        LogUtils.i(TAG, "showDirPermissionPop fragment????????? width=" + width + ",height=" + height);
        sortFilePop = new PopupWindow(inflate, width, height);
        sortFilePop.setBackgroundDrawable(new BitmapDrawable());
        // ??????popWindow?????????????????????????????????????????????????????????true
        sortFilePop.setTouchable(true);
        // true:???????????????????????????
        sortFilePop.setOutsideTouchable(true);
        sortFilePop.setFocusable(true);
//        sortFilePop.setAnimationStyle(R.style.pop_Animation);
        sortFilePop.showAtLocation(btn_file_sort, Gravity.END | Gravity.BOTTOM, 0, 0);
        rv_sort_file_dir = inflate.findViewById(R.id.rv_sort_file_dir);
        rv_sort_file = inflate.findViewById(R.id.rv_sort_file);
        sortFiledirAdapter = new DirAdapter(R.layout.item_file_sort_dir, dirInfos);
        rv_sort_file_dir.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_sort_file_dir.setAdapter(sortFiledirAdapter);
        sortFilePop.setOnDismissListener(() -> {
            //PopupWindow??????????????????????????????id
            presenter.setCurrentSortFileDirId(0);
            fileSortAdapter = null;
        });
        sortFiledirAdapter.setOnItemClickListener((adapter, view, position) -> {
            int dirId = dirInfos.get(position).getId();
            sortFiledirAdapter.setSelected(dirId);
            presenter.setCurrentSortFileDirId(dirId);
            presenter.queryFileByDir(dirId);
        });
        inflate.findViewById(R.id.btn_move_up).setOnClickListener(v -> {
            if (fileSortAdapter != null) {
                InterfaceFile.pbui_Item_MeetDirFileDetailInfo selected = fileSortAdapter.getSelected();
                if (selected == null) {
                    ToastUtils.showShort(R.string.please_choose_file_first);
                    return;
                }
                int index = fileSortAdapter.getSelectedIndex();
                if (index == -1) {
                    return;
                }
                List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> sortDirFile = presenter.getSortDirFile();
                if (index == 0) {
                    //??????????????????????????????????????????????????????????????????
                    sortDirFile.remove(index);
                    sortDirFile.add(selected);
                } else {
                    Collections.swap(sortDirFile, index, index - 1);
                }
                fileSortAdapter.notifyDataSetChanged();
            }
        });
        inflate.findViewById(R.id.btn_move_down).setOnClickListener(v -> {
            if (fileSortAdapter != null) {
                InterfaceFile.pbui_Item_MeetDirFileDetailInfo selected = fileSortAdapter.getSelected();
                if (selected == null) {
                    ToastUtils.showShort(R.string.please_choose_file_first);
                    return;
                }
                int index = fileSortAdapter.getSelectedIndex();
                if (index == -1) {
                    return;
                }
                List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> sortDirFile = presenter.getSortDirFile();
                if (index == sortDirFile.size() - 1) {
                    //??????????????????????????????????????????????????????????????????
                    List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> temps = new ArrayList<>();
                    sortDirFile.remove(index);
                    temps.add(selected);
                    temps.addAll(sortDirFile);
                    sortDirFile.clear();
                    sortDirFile.addAll(temps);
                    temps.clear();
                } else {
                    Collections.swap(sortDirFile, index, index + 1);
                }
                fileSortAdapter.notifyDataSetChanged();
            }
        });
        inflate.findViewById(R.id.btn_save).setOnClickListener(v -> {
            if (presenter.modifyMeetDirFileSort()) {
                sortFilePop.dismiss();
            }
        });
        inflate.findViewById(R.id.btn_exit).setOnClickListener(v -> {
            sortFilePop.dismiss();
        });
    }

    @Override
    public void updateSortFileRv(List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> sortDirFiles) {
        if (sortFilePop != null && sortFilePop.isShowing()) {
            LogUtils.i(TAG, "updateSortFileRv ");
            if (fileSortAdapter == null) {
                fileSortAdapter = new FileSortAdapter(R.layout.item_sort_file, sortDirFiles);
                rv_sort_file.setLayoutManager(new LinearLayoutManager(getContext()));
                rv_sort_file.setAdapter(fileSortAdapter);
                fileSortAdapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                        fileSortAdapter.setSelectedId(sortDirFiles.get(position).getMediaid());
                    }
                });
            } else {
                fileSortAdapter.notifyDataSetChanged();
            }
        }
    }

    private void deleteFile() {
        if (fileAdapter != null && fileAdapter.getLastCheckFile() != null) {
            InterfaceFile.pbui_Item_MeetDirFileDetailInfo lastCheckFile = fileAdapter.getLastCheckFile();
            presenter.deleteMeetDirFile(lastCheckFile);
        } else {
            ToastUtils.showShort(R.string.please_choose_file_first);
        }
    }

    private void modifyFile() {
        String fileName = edt_file_name.getText().toString().trim();
        if (fileName.isEmpty()) {
            ToastUtils.showShort(R.string.please_choose_file_first);
            return;
        }
        if (fileAdapter != null && fileAdapter.getLastCheckFile() != null) {
            InterfaceFile.pbui_Item_MeetDirFileDetailInfo lastCheckFile = fileAdapter.getLastCheckFile();
            InterfaceFile.pbui_Item_ModMeetDirFile build = InterfaceFile.pbui_Item_ModMeetDirFile.newBuilder()
                    .setMediaid(lastCheckFile.getMediaid())
                    .setName(s2b(fileName))
                    .setAttrib(lastCheckFile.getAttrib())
                    .build();
            presenter.modifyMeetDirFileName(build);
        } else {
            ToastUtils.showShort(R.string.please_choose_file_first);
        }
    }

    private void chooseLocalFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, LOCAL_FILE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == LOCAL_FILE_REQUEST_CODE) {
            Uri uri = data.getData();
            File file = UriUtils.uri2File(uri);
            if (file != null) {
                int dirId = dirAdapter.getSelected().getId();
                JniHelper.getInstance().uploadFile(InterfaceMacro.Pb_Upload_Flag.Pb_MEET_UPLOADFLAG_ONLYENDCALLBACK_VALUE,
                        dirId, 0, file.getName(), file.getAbsolutePath(),
                        0, Constant.UPLOAD_CHOOSE_FILE);
            }
        }
    }

    /**
     * ????????????
     */
    private void showDirPermissionPop(List<InterfaceFile.pbui_Item_MeetDirDetailInfo> dirInfos) {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_dir_permission, null);
        View admin_fl = getActivity().findViewById(R.id.admin_fl);
        int width = admin_fl.getWidth();
        int height = admin_fl.getHeight();
        LogUtils.i(TAG, "showDirPermissionPop fragment????????? width=" + width + ",height=" + height);
        dirPermissionPop = new PopupWindow(inflate, width, height);
        dirPermissionPop.setBackgroundDrawable(new BitmapDrawable());
        // ??????popWindow?????????????????????????????????????????????????????????true
        dirPermissionPop.setTouchable(true);
        // true:???????????????????????????
        dirPermissionPop.setOutsideTouchable(true);
        dirPermissionPop.setFocusable(true);
//        dirPermissionPop.setAnimationStyle(R.style.pop_Animation);
        dirPermissionPop.showAtLocation(btn_dir_permission, Gravity.END | Gravity.BOTTOM, 0, 0);
        dirPermissionPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                dirPermissionMemberAdapter = null;
            }
        });
        rv_permission_dir = inflate.findViewById(R.id.rv_permission_dir);
        dirNameAdapter = new DirNameAdapter(R.layout.item_dir_permission, dirInfos);
        rv_permission_dir.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_permission_dir.setAdapter(dirNameAdapter);
        dirNameAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                int dirId = dirInfos.get(position).getId();
                dirNameAdapter.setSelected(dirId);
                presenter.queryDirPermission(dirId);
            }
        });
        cbAll = inflate.findViewById(R.id.item_view_1);
        rv_dir_permission_member = inflate.findViewById(R.id.rv_dir_permission_member);
        cbAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cbAll.setChecked(cbAll.isChecked());
                if (dirPermissionMemberAdapter != null) {
                    dirPermissionMemberAdapter.setCheckAll(cbAll.isChecked());
                } else {
                    ToastUtils.showShort(R.string.please_choose_dir_first);
                }
            }
        });
        inflate.findViewById(R.id.btn_save).setOnClickListener(v -> {
            if (dirPermissionMemberAdapter != null) {
                presenter.saveDirPermission(dirPermissionMemberAdapter.getChecks());
            } else {
                ToastUtils.showShort(R.string.please_choose_dir_first);
            }
        });
        inflate.findViewById(R.id.btn_back).setOnClickListener(v -> {
            dirPermissionPop.dismiss();
        });
    }

    @Override
    public void updateMemberPermission(List<MemberDirPermissionBean> memberDirPermissionBeans) {
        if (dirPermissionPop != null && dirPermissionPop.isShowing()) {
            if (dirPermissionMemberAdapter == null) {
                dirPermissionMemberAdapter = new DirPermissionMemberAdapter(R.layout.item_dir_permission_member, memberDirPermissionBeans);
                rv_dir_permission_member.setLayoutManager(new LinearLayoutManager(getContext()));
                rv_dir_permission_member.setAdapter(dirPermissionMemberAdapter);
                dirPermissionMemberAdapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                        dirPermissionMemberAdapter.setCheck(memberDirPermissionBeans.get(position).getMember().getPersonid());
                        cbAll.setChecked(dirPermissionMemberAdapter.isCheckAll());
                    }
                });
            } else {
                dirPermissionMemberAdapter.notifyDataSetChanged();
            }
        }
    }

    private void createDir() {
        String dirName = edt_dir_name.getText().toString().trim();
        if (dirName.isEmpty()) {
            ToastUtils.showShort(R.string.please_enter_dir_name);
            return;
        }
        InterfaceFile.pbui_Item_MeetDirDetailInfo build = InterfaceFile.pbui_Item_MeetDirDetailInfo.newBuilder()
                .setName(s2b(dirName))
                .build();
        presenter.createDir(build);
    }

    private void modifyDir() {
        String dirName = edt_dir_name.getText().toString().trim();
        if (dirName.isEmpty()) {
            ToastUtils.showShort(R.string.please_enter_dir_name);
            return;
        }
        if (dirAdapter != null && dirAdapter.getSelected() != null) {
            InterfaceFile.pbui_Item_MeetDirDetailInfo selected = dirAdapter.getSelected();
            InterfaceFile.pbui_Item_MeetDirDetailInfo build = InterfaceFile.pbui_Item_MeetDirDetailInfo.newBuilder()
                    .setName(s2b(dirName))
                    .setId(selected.getId())
                    .setParentid(selected.getParentid())
                    .setFilenum(selected.getFilenum())
                    .build();
            presenter.modifyDir(build);
        } else {
            ToastUtils.showShort(R.string.please_choose_dir_first);
        }
    }

    private void deleteDir() {
        if (dirAdapter != null && dirAdapter.getSelected() != null) {
            InterfaceFile.pbui_Item_MeetDirDetailInfo selected = dirAdapter.getSelected();
            presenter.deleteDir(selected);
        } else {
            ToastUtils.showShort(R.string.please_choose_dir_first);
        }
    }
}
