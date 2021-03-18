package com.xlk.paperlesstl.view.draw;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.UriUtils;
import com.google.protobuf.ByteString;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.model.Constant;
import com.xlk.paperlesstl.model.GlobalValue;
import com.xlk.paperlesstl.ui.ArtBoard;
import com.xlk.paperlesstl.ui.ColorPickerDialog;
import com.xlk.paperlesstl.util.FileUtil;
import com.xlk.paperlesstl.util.PopupUtil;
import com.xlk.paperlesstl.view.base.BaseActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.ACTION_OPEN_DOCUMENT;
import static com.xlk.paperlesstl.ui.ArtBoard.LocalPathList;
import static com.xlk.paperlesstl.util.ConvertUtil.bmp2bs;
import static com.xlk.paperlesstl.view.draw.DrawPresenter.LocalSharingPathList;
import static com.xlk.paperlesstl.view.draw.DrawPresenter.isSharing;
import static com.xlk.paperlesstl.view.draw.DrawPresenter.launchSrcmemid;
import static com.xlk.paperlesstl.view.draw.DrawPresenter.launchSrcwbid;
import static com.xlk.paperlesstl.view.draw.DrawPresenter.localOperids;

public class DrawActivity extends BaseActivity<DrawPresenter> implements DrawContract.View, View.OnClickListener {

    private Button draw_exit;
    private Button draw_save;
    private Button draw_clear;
    private Button draw_picture;
    private Button draw_color;
    private Button draw_revoke;
    private FrameLayout draw_fl;
    private Button draw_round;
    private Button draw_rect;
    private Button draw_line;
    private Button draw_curve;
    private Button draw_pen;
    private Button draw_text;
    private Button draw_eraser;
    private Button draw_drag;
    private Button draw_launch;
    private Button draw_stop;
    private AppCompatSeekBar draw_seekbar;
    private TextView draw_seekbar_tv;

    List<TextView> selectTvs = new ArrayList<>();
    public static boolean isDrawing;
    private ArtBoard artBoard;
    private DrawMemberAdapter memberAdapter;

    private int IMAGE_CODE = 1;
    private PopupWindow memberPop;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_draw;
    }

    @Override
    protected DrawPresenter initPresenter() {
        return new DrawPresenter(this, this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        initView();
        isDrawing = true;
        draw_fl.post(this::initial);
    }

    private void initial() {
        int width = draw_fl.getWidth();
        int height = draw_fl.getHeight();
        LogUtils.d(TAG, "run -->" + "width: " + width + ", height: " + height);
        artBoard = new ArtBoard(getApplicationContext(), width, height);
        draw_fl.addView(artBoard);
        if (GlobalValue.screenShotBitmap != null) {
            presenter.setIsAddBitmap(true);//设置发起同屏时是否发送图片
            artBoard.drawZoomBmp(GlobalValue.screenShotBitmap);
        }
        presenter.register();
        presenter.queryMember();
        artBoard.setDrawTextListener((x, y) -> {
            LogUtils.d(TAG, "showEdtPop -->" + x + "," + y);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            EditText editText = new EditText(this);
            builder.setView(editText);
            builder.setOnDismissListener(dialog -> {
                String text = editText.getText().toString().trim();
                if (!text.isEmpty()) {
                    artBoard.drawText(x, y, text);
                }
            });
            builder.create().show();
        });
        draw_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                artBoard.setPaintWidth(seekBar.getProgress());
                draw_seekbar_tv.setText(getString(R.string.thickness_, seekBar.getProgress()));
            }
        });
    }

    private void initView() {
        draw_exit = findViewById(R.id.draw_exit);
        draw_save = findViewById(R.id.draw_save);
        draw_clear = findViewById(R.id.draw_clear);
        draw_picture = findViewById(R.id.draw_picture);
//        draw_font = (TextView) findViewById(R.id.draw_font);
        draw_color = findViewById(R.id.draw_color);
        draw_revoke = findViewById(R.id.draw_revoke);
        draw_fl = (FrameLayout) findViewById(R.id.draw_fl);
        draw_round = findViewById(R.id.draw_round);
        draw_rect = findViewById(R.id.draw_rect);
        draw_line = findViewById(R.id.draw_line);
        draw_curve = findViewById(R.id.draw_curve);
        draw_pen = findViewById(R.id.draw_pen);
        draw_text = findViewById(R.id.draw_text);
        draw_eraser = findViewById(R.id.draw_eraser);
        draw_drag = findViewById(R.id.draw_drag);
        draw_launch = findViewById(R.id.draw_launch);
        draw_stop = findViewById(R.id.draw_stop);
        updateBtnEnable();
        draw_seekbar = findViewById(R.id.draw_seekbar);
        draw_seekbar_tv = findViewById(R.id.draw_seekbar_tv);

        draw_exit.setOnClickListener(this);
        draw_save.setOnClickListener(this);
        draw_clear.setOnClickListener(this);
        draw_picture.setOnClickListener(this);
//        draw_font.setOnClickListener(this);
        draw_color.setOnClickListener(this);
        draw_revoke.setOnClickListener(this);
        draw_round.setOnClickListener(this);
        draw_rect.setOnClickListener(this);
        draw_line.setOnClickListener(this);
        draw_curve.setOnClickListener(this);
        draw_pen.setOnClickListener(this);
        draw_text.setOnClickListener(this);
        draw_eraser.setOnClickListener(this);
        draw_drag.setOnClickListener(this);
        draw_launch.setOnClickListener(this);
        draw_stop.setOnClickListener(this);

        selectTvs.add(draw_round);
        selectTvs.add(draw_rect);
        selectTvs.add(draw_line);
        selectTvs.add(draw_curve);
        selectTvs.add(draw_pen);
        selectTvs.add(draw_text);
        selectTvs.add(draw_eraser);
        selectTvs.add(draw_drag);
    }

    @Override
    public void updateBtnEnable() {
        draw_stop.setEnabled(isSharing);
        draw_launch.setEnabled(!isSharing);
        if (isSharing) {
            draw_stop.setBackground(getResources().getDrawable(R.drawable.shape_btn_pressed));
            draw_launch.setBackground(getResources().getDrawable(R.drawable.shape_btn_enable_flase));
        } else {
            draw_stop.setBackground(getResources().getDrawable(R.drawable.shape_btn_enable_flase));
            draw_launch.setBackground(getResources().getDrawable(R.drawable.shape_btn_pressed));
        }
    }

    @Override
    public void initCanvas() {
        artBoard.initCanvas();
    }

    @Override
    public void drawAgain(List<ArtBoard.DrawPath> pathList) {
        artBoard.drawAgain(pathList);
    }

    @Override
    public void drawZoomBmp(Bitmap bitmap) {
        artBoard.drawZoomBmp(bitmap);
    }

    @Override
    public void setCanvasSize(int maxX, int maxY) {
        artBoard.setCanvasSize(maxX, maxY);
    }

    @Override
    public void drawPath(Path path, Paint paint) {
        artBoard.drawPath(path, paint);
    }

    @Override
    public void invalidate() {
        artBoard.invalidate();
    }

    @Override
    public void funDraw(Paint paint, float height, int canSee, float fx, float fy, String text) {
        artBoard.funDraw(paint, height, canSee, fx, fy, text);
    }

    @Override
    public void drawText(String ptext, float lx, float ly, Paint paint) {
        artBoard.drawText(ptext, lx, ly, paint);
    }

    private void setSelect(int index) {
        for (int i = 0; i < selectTvs.size(); i++) {
            TextView tv = selectTvs.get(i);
            if (i == index) {
                tv.setSelected(true);
                artBoard.setDrag(index == 7);
            } else {
                tv.setSelected(false);
            }
        }
    }

    @Override
    public void updateOnlineMembers() {
        if (memberPop != null && memberPop.isShowing()) {
            memberAdapter.notifyDataSetChanged();
            memberAdapter.notifyChecks();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.draw_exit:
                finish();
                break;
            case R.id.draw_save:
                saveDig();
                break;
            case R.id.draw_clear:
                artBoard.clear();
                presenter.setIsAddBitmap(false);//已经清空了就不必发送了
                break;
            case R.id.draw_picture:
                Intent i = new Intent(ACTION_OPEN_DOCUMENT);//打开图片
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                i.setType("image/*");
                startActivityForResult(i, IMAGE_CODE);
                break;
            case R.id.draw_color:
                new ColorPickerDialog(this, new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void colorChanged(int color) {
                        artBoard.setPaintColor(color);
                    }
                }, Color.BLACK).show();
                break;
            case R.id.draw_revoke:
                artBoard.revoke();
                break;
            case R.id.draw_round:
                setSelect(0);
                artBoard.setDrawType(2);
                break;
            case R.id.draw_rect:
                setSelect(1);
                artBoard.setDrawType(3);
                break;
            case R.id.draw_line:
                setSelect(2);
                artBoard.setDrawType(4);
                break;
            case R.id.draw_curve:
                setSelect(3);
                artBoard.setDrawType(1);
                break;
            case R.id.draw_pen:
                setSelect(4);
                artBoard.setDrawType(1);
                break;
            case R.id.draw_text:
                setSelect(5);
                artBoard.setDrawType(5);
                break;
            case R.id.draw_eraser:
                setSelect(6);
                artBoard.setDrawType(6);
                break;
            case R.id.draw_drag:
                setSelect(7);
                break;
            case R.id.draw_launch: {
                presenter.queryMember();
                showOnlineMember();
                break;
            }
            case R.id.draw_stop:
                finish();
                break;
            default:
                break;
        }
    }

    private void showOnlineMember() {
        View inflate = LayoutInflater.from(this).inflate(R.layout.pop_artboard_share, null);
        memberPop = PopupUtil.createHalfPop(inflate, draw_launch);
        CheckBox cb = inflate.findViewById(R.id.pop_artboard_cb);
        RecyclerView rv = inflate.findViewById(R.id.pop_artboard_rv);
        memberAdapter = new DrawMemberAdapter(R.layout.item_single_button, presenter.onLineMembers);
        rv.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        rv.setAdapter(memberAdapter);
        memberAdapter.setOnItemClickListener((ad, view, position) -> {
            memberAdapter.choose(presenter.onLineMembers.get(position).getMemberDetailInfo().getPersonid());
            cb.setChecked(memberAdapter.isChooseAll());
        });
        cb.setOnClickListener(v -> {
            boolean checked = cb.isChecked();
            cb.setChecked(checked);
            memberAdapter.setChooseAll(checked);
        });
        inflate.findViewById(R.id.pop_artboard_launch).setOnClickListener(v -> {
            List<Integer> ids = memberAdapter.getChooseIds();
            if (!ids.isEmpty()) {
                // 发起共享批注  强制：Pb_MEETPOTIL_FLAG_FORCEOPEN  Pb_MEETPOTIL_FLAG_REQUESTOPEN
                if (DrawPresenter.mSrcmemid == 0) {
                    //当前已经在同屏中,并且自己是发起人;则操作ID不需要重新获取
                    launchSrcwbid = System.currentTimeMillis();
                    launchSrcmemid = GlobalValue.localMemberId;
                } else {
                    launchSrcwbid = DrawPresenter.mSrcwbid;
                    launchSrcmemid = DrawPresenter.mSrcmemid;
                }
                LogUtils.d(TAG, "发起同屏：" + ids.toString());
                jni.coerceStartWhiteBoard(InterfaceMacro.Pb_MeetPostilOperType.Pb_MEETPOTIL_FLAG_REQUESTOPEN.getNumber(),
                        GlobalValue.localMemberName, GlobalValue.localMemberId,
                        launchSrcmemid, launchSrcwbid, ids);
                if (DrawPresenter.isAddScreenShot) {//从截图批注端启动的画板
                    DrawPresenter.isAddScreenShot = false;
                    addScreenShot();
                }
                memberPop.dismiss();
            } else {
                ToastUtils.showShort(getString(R.string.please_choose_member));
            }
        });
        inflate.findViewById(R.id.pop_artboard_cancel).setOnClickListener(v -> memberPop.dismiss());
    }

    private void addScreenShot() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GlobalValue.screenShotBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] bytes = baos.toByteArray();
            ByteString picdata = ByteString.copyFrom(bytes);
            long time = System.currentTimeMillis();
            int operid = (int) (time / 10);
            localOperids.add(operid);
            ArtBoard.DrawPath drawPath = new ArtBoard.DrawPath();
            drawPath.picdata = picdata;
            LocalPathList.add(drawPath);
            LocalSharingPathList.add(drawPath);
            LogUtils.d(TAG, "发起同屏时添加截图: LocalPathList.size() : " + LocalPathList.size());
            jni.addPicture(operid, GlobalValue.localMemberId, launchSrcmemid, launchSrcwbid, time,
                    InterfaceMacro.Pb_MeetPostilFigureType.Pb_WB_FIGURETYPE_PICTURE.getNumber(), 0, 0, picdata);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (GlobalValue.screenShotBitmap != null && !GlobalValue.screenShotBitmap.isRecycled()) {
                GlobalValue.screenShotBitmap.recycle();
            }
        }
    }

    private void saveDig() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText edt = new EditText(this);
        builder.setTitle(getResources().getString(R.string.please_enter_file_name));
        edt.setHint(getResources().getString(R.string.please_enter_file_name));
        edt.setText(String.valueOf(System.currentTimeMillis()));
        //编辑光标移动到最后
        edt.setSelection(edt.getText().toString().length());
        builder.setView(edt);
        builder.setPositiveButton(getResources().getString(R.string.save_server), (dialog, which) -> {
            String name = edt.getText().toString().trim();
            if (name.isEmpty()) {
                ToastUtils.showShort(R.string.please_enter_file_name);
            } else if (!FileUtil.isLegalName(name)) {
                ToastUtils.showShort(R.string.tip_file_name_unlawfulness);
            } else {
                presenter.savePicture(name, true, artBoard.getCanvasBmp());
                dialog.dismiss();
            }
        });
        builder.setNeutralButton(getResources().getString(R.string.save_local), (dialog, which) -> {
            final String name = edt.getText().toString().trim();
            if (name.isEmpty()) {
                ToastUtils.showShort(R.string.please_enter_file_name);
            } else if (!FileUtil.isLegalName(name)) {
                ToastUtils.showShort(R.string.tip_file_name_unlawfulness);
            } else {
                presenter.savePicture(name, false, artBoard.getCanvasBmp());
                ToastUtils.showShort(getResources().getString(R.string.tip_save_as, Constant.DIR_PICTURE));
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_CODE && resultCode == Activity.RESULT_OK) {
            // 获取选中文件的uri
            LogUtils.d(TAG, "onActivityResult: data.toString : " + data.toString());
            Uri uri = data.getData();
            String realPath = null;
            File file = UriUtils.uri2File(uri);
            if (file != null) {
                realPath = file.getAbsolutePath();
            }
            LogUtils.e(TAG, "DrawBoardActivity.onActivityResult :  选中的文件路径 --->>> " + realPath);
            if (realPath == null) {
                LogUtils.e(TAG, "onActivityResult: 获取该文件的路径失败....");
                ToastUtils.showShort(R.string.get_file_path_fail);
            } else {
                // 执行操作
                Bitmap dstbmp = BitmapFactory.decodeFile(realPath);
                //将图片绘制到画板中
                Bitmap bitmap = artBoard.drawZoomBmp(dstbmp);
                //保存图片信息
                ArtBoard.DrawPath drawPath = new ArtBoard.DrawPath();
                drawPath.picdata = bmp2bs(bitmap);
                LocalPathList.add(drawPath);
                if (DrawPresenter.isSharing) {
                    long time = System.currentTimeMillis();
                    int operid = (int) (time / 10);
                    localOperids.add(operid);
                    LocalSharingPathList.add(drawPath);
                    presenter.addPicture(operid, GlobalValue.localMemberId, DrawPresenter.mSrcmemid, DrawPresenter.mSrcwbid, time,
                            InterfaceMacro.Pb_MeetPostilFigureType.Pb_WB_FIGURETYPE_PICTURE.getNumber(), 0, 0, bmp2bs(bitmap));
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDrawing = false;
        if (GlobalValue.screenShotBitmap != null) {
            GlobalValue.screenShotBitmap.recycle();
            GlobalValue.screenShotBitmap = null;
        }
        presenter.stopShare();
        presenter.unregister();
        LocalPathList.clear();
        LocalSharingPathList.clear();
        localOperids.clear();
        DrawPresenter.togetherIDs.clear();
        DrawPresenter.pathList.clear();
        DrawPresenter.tempPicData = null;
        DrawPresenter.savePicData = null;
        DrawPresenter.mSrcmemid = 0;
        DrawPresenter.mSrcwbid = 0;
        DrawPresenter.disposePicOpermemberid = 0;
        DrawPresenter.disposePicSrcmemid = 0;
        DrawPresenter.disposePicSrcwbidd = 0;
        ArtBoard.artBoardWidth = 0;
        ArtBoard.artBoardHeight = 0;
        artBoard.destroyDrawingCache();
        artBoard = null;
        presenter.onDestroy();
    }
}