package com.xlk.paperlesstl.view.fragment.share;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.UriUtils;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.adapter.FileAdapter;
import com.xlk.paperlesstl.model.Constant;
import com.xlk.paperlesstl.util.FileUtil;
import com.xlk.paperlesstl.view.base.BaseFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Created by xlk on 2021/3/11.
 * @desc
 */
public class ShareMaterialFragment extends BaseFragment<ShareMaterialPresenter> implements ShareMaterialContract.View, View.OnClickListener {

    private Button btn_upload, btn_all, btn_document, btn_picture, btn_audio, btn_other;
    private RecyclerView rv_file;
    List<Button> buttons = new ArrayList<>();
    private int currentIndex;
    List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> currentFiles = new ArrayList<>();
    private FileAdapter fileAdapter;
    private final int REQUEST_CODE_UPLOAD = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_share_material;
    }

    @Override
    protected void initView(View inflate) {
        btn_upload = inflate.findViewById(R.id.btn_upload);
        btn_all = inflate.findViewById(R.id.btn_all);
        btn_document = inflate.findViewById(R.id.btn_document);
        btn_picture = inflate.findViewById(R.id.btn_picture);
        btn_audio = inflate.findViewById(R.id.btn_audio);
        btn_other = inflate.findViewById(R.id.btn_other);
        rv_file = inflate.findViewById(R.id.rv_file);
        btn_upload.setOnClickListener(this);
        btn_all.setOnClickListener(this);
        btn_document.setOnClickListener(this);
        btn_picture.setOnClickListener(this);
        btn_audio.setOnClickListener(this);
        btn_other.setOnClickListener(this);
    }

    @Override
    protected ShareMaterialPresenter initPresenter() {
        return new ShareMaterialPresenter(this);
    }

    @Override
    protected void initial() {
        initButton();
        presenter.queryFile();
        choose(0);
    }

    @Override
    protected void onShow() {
        presenter.queryFile();
    }

    @Override
    public void updateFileRv() {
        currentFiles.clear();
        if (currentIndex == 0) {
            currentFiles.addAll(presenter.allFiles);
        } else {
            for (int i = 0; i < presenter.allFiles.size(); i++) {
                InterfaceFile.pbui_Item_MeetDirFileDetailInfo info = presenter.allFiles.get(i);
                String fileName = info.getName().toStringUtf8();
                if (currentIndex == 1) {
                    if (FileUtil.isDocument(fileName)) {
                        currentFiles.add(info);
                    }
                } else if (currentIndex == 2) {
                    if (FileUtil.isPicture(fileName)) {
                        currentFiles.add(info);
                    }
                } else if (currentIndex == 3) {
                    if (FileUtil.isAudio(fileName)) {
                        currentFiles.add(info);
                    }
                } else if (currentIndex == 4) {
                    if (FileUtil.isOther(fileName)) {
                        currentFiles.add(info);
                    }
                }
            }
        }
        if (fileAdapter == null) {
            fileAdapter = new FileAdapter(true, currentFiles);
            rv_file.setLayoutManager(new LinearLayoutManager(getContext()));
            rv_file.setAdapter(fileAdapter);
        } else {
            fileAdapter.notifyDataSetChanged();
        }
    }

    private void initButton() {
        buttons.add(btn_all);
        buttons.add(btn_document);
        buttons.add(btn_picture);
        buttons.add(btn_audio);
        buttons.add(btn_other);
    }

    private void choose(int index) {
        currentIndex = index;
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setSelected(index == i);
        }
        updateFileRv();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_all: {
                choose(0);
                break;
            }
            case R.id.btn_document: {
                choose(1);
                break;
            }
            case R.id.btn_picture: {
                choose(2);
                break;
            }
            case R.id.btn_audio: {
                choose(3);
                break;
            }
            case R.id.btn_other: {
                choose(4);
                break;
            }
            case R.id.btn_upload: {
                if (Constant.hasPermission(Constant.permission_code_upload)) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");//无类型限制
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(intent, REQUEST_CODE_UPLOAD);
                } else {
                    ToastUtils.showShort(R.string.no_permission);
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_UPLOAD) {
            Uri uri = data.getData();
            File file = UriUtils.uri2File(uri);
            if (file != null) {
                uploadFileDialog(file);
            }
        }
    }

    private void uploadFileDialog(File file) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.modify_file_name);
        EditText editText = new EditText(getContext());
        editText.setText(file.getName());
        builder.setView(editText);
        builder.setPositiveButton(R.string.upload, (dialog, which) -> {
            String fileName = editText.getText().toString().trim();
            if (fileName.isEmpty()) {
                ToastUtils.showShort(R.string.please_enter_file_name);
                return;
            }
            jni.uploadFile(InterfaceMacro.Pb_Upload_Flag.Pb_MEET_UPLOADFLAG_ONLYENDCALLBACK_VALUE,
                    Constant.SHARED_FILE_DIRECTORY_ID, 0, fileName, file.getParentFile().getAbsolutePath() + "/" + fileName, 0, Constant.UPLOAD_CHOOSE_FILE);
            dialog.dismiss();
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}
