package com.xlk.paperlesstl.view.bulletin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.mogujie.tt.protobuf.InterfaceFaceconfig;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.model.GlobalValue;
import com.xlk.paperlesstl.view.base.BaseActivity;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

public class BulletinActivity extends BaseActivity<BulletinPresenter> implements BulletinContract.View {

    private androidx.constraintlayout.widget.ConstraintLayout bulletinBgView;
    private android.widget.TextView tvTitle;
    private android.widget.TextView tvContent;
    private android.widget.Button btnClose;
    private android.widget.ImageView ivLogo;
    public static final String EXTRA_BULLETIN_ID = "extra_bulletin_id";
    public static final String EXTRA_BULLETIN_TITLE = "extra_bulletin_title";
    public static final String EXTRA_BULLETIN_CONTENT = "extra_bulletin_content";
    public static final String EXTRA_BULLETIN_TYPE = "extra_bulletin_type";
    public static final String EXTRA_BULLETIN_START_TIME = "extra_bulletin_start_time";
    public static final String EXTRA_BULLETIN_TIMEOUTS = "extra_bulletin_timeouts";
    public static int bulletinId = -1;

    public static void jump(Bundle bundle, Context context) {
        context.startActivity(new Intent(context, BulletinActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtras(bundle));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bulletin;
    }

    @Override
    protected BulletinPresenter initPresenter() {
        return new BulletinPresenter(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        initView();
        presenter.queryBulletinInterfaceConfig();
        updateFromBundle(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtils.e(TAG, "onNewIntent");
        updateFromBundle(intent);
    }

    private void updateFromBundle(Intent intent) {
        Bundle bundle = intent.getExtras();
        bulletinId = bundle.getInt(EXTRA_BULLETIN_ID, 0);
        String title = bundle.getString(EXTRA_BULLETIN_TITLE, "");
        String content = bundle.getString(EXTRA_BULLETIN_CONTENT, "");
        tvTitle.setText(title);
        tvContent.setText(content);
    }

    @Override
    public void closeBulletin(int bulletid) {
        LogUtils.e(TAG, "closeBulletin bulletid=" + bulletid);
//        if (bulletid == bulletinId) {
            finish();
//        }
    }

    @Override
    public void updateBulletinLogo(Drawable drawable) {
        ivLogo.setImageDrawable(drawable);
    }

    @Override
    public void updateBulletinBg(Drawable drawable) {
        bulletinBgView.setBackground(drawable);
    }

    @Override
    public void updateTitleTextView(InterfaceFaceconfig.pbui_Item_FaceTextItemInfo info) {
        int color = info.getColor();
        int fontsize = info.getFontsize();
        int flag = info.getFlag();
        boolean isShow = (InterfaceMacro.Pb_MeetFaceFlag.Pb_MEET_FACEFLAG_SHOW_VALUE == (flag & InterfaceMacro.Pb_MeetFaceFlag.Pb_MEET_FACEFLAG_SHOW_VALUE));
        tvTitle.setVisibility(isShow ? View.VISIBLE : View.GONE);
        int fontflag = info.getFontflag();
        int align = info.getAlign();
        String fontName = info.getFontname().toStringUtf8();
        tvTitle.setTextColor(color);
        tvTitle.setTextSize(fontsize);
        update(R.id.tv_title, info);
        //字体样式
        if (fontflag == InterfaceMacro.Pb_MeetFaceFontFlag.Pb_MEET_FONTFLAG_BOLD.getNumber()) {//加粗
            tvTitle.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        } else if (fontflag == InterfaceMacro.Pb_MeetFaceFontFlag.Pb_MEET_FONTFLAG_LEAN.getNumber()) {//倾斜
            tvTitle.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
        } else if (fontflag == InterfaceMacro.Pb_MeetFaceFontFlag.Pb_MEET_FONTFLAG_UNDERLINE.getNumber()) {//下划线
            tvTitle.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));//暂时用倾斜加粗
        } else {//正常文本
            tvTitle.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }
        //对齐方式
        if (align == InterfaceMacro.Pb_FontAlignFlag.Pb_MEET_FONTALIGNFLAG_LEFT.getNumber()) {//左对齐
            tvTitle.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        } else if (align == InterfaceMacro.Pb_FontAlignFlag.Pb_MEET_FONTALIGNFLAG_RIGHT.getNumber()) {//右对齐
            tvTitle.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        } else if (align == InterfaceMacro.Pb_FontAlignFlag.Pb_MEET_FONTALIGNFLAG_HCENTER.getNumber()) {//水平对齐
            tvTitle.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        } else if (align == InterfaceMacro.Pb_FontAlignFlag.Pb_MEET_FONTALIGNFLAG_TOP.getNumber()) {//上对齐
            tvTitle.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        } else if (align == InterfaceMacro.Pb_FontAlignFlag.Pb_MEET_FONTALIGNFLAG_BOTTOM.getNumber()) {//下对齐
            tvTitle.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        } else if (align == InterfaceMacro.Pb_FontAlignFlag.Pb_MEET_FONTALIGNFLAG_VCENTER.getNumber()) {//垂直对齐
            tvTitle.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        } else {
            tvTitle.setGravity(Gravity.CENTER);
        }
        //字体类型
        Typeface kt_typeface;
        if (!TextUtils.isEmpty(fontName)) {
            switch (fontName) {
                case "楷体":
                    kt_typeface = Typeface.createFromAsset(getAssets(), "kaiti.ttf");
                    break;
                case "隶书":
                    kt_typeface = Typeface.createFromAsset(getAssets(), "lishu.ttf");
                    break;
                case "微软雅黑":
                    kt_typeface = Typeface.createFromAsset(getAssets(), "weiruanyahei.ttf");
                    break;
                case "黑体":
                    kt_typeface = Typeface.createFromAsset(getAssets(), "heiti.ttf");
                    break;
                case "小楷":
                    kt_typeface = Typeface.createFromAsset(getAssets(), "xiaokai.ttf");
                    break;
                default:
                    kt_typeface = Typeface.createFromAsset(getAssets(), "fangsong.ttf");
                    break;
            }
            tvTitle.setTypeface(kt_typeface);
        }
    }

    @Override
    public void updateContentTextView(InterfaceFaceconfig.pbui_Item_FaceTextItemInfo info) {
        int color = info.getColor();
        int fontsize = info.getFontsize();
        int flag = info.getFlag();
        boolean isShow = (InterfaceMacro.Pb_MeetFaceFlag.Pb_MEET_FACEFLAG_SHOW_VALUE == (flag & InterfaceMacro.Pb_MeetFaceFlag.Pb_MEET_FACEFLAG_SHOW_VALUE));
        tvContent.setVisibility(isShow ? View.VISIBLE : View.GONE);
        int fontflag = info.getFontflag();
        int align = info.getAlign();
        String fontName = info.getFontname().toStringUtf8();
        tvContent.setTextColor(color);
        tvContent.setTextSize(fontsize);
        update(R.id.tv_content, info);
        //字体样式
        if (fontflag == InterfaceMacro.Pb_MeetFaceFontFlag.Pb_MEET_FONTFLAG_BOLD.getNumber()) {//加粗
            tvContent.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        } else if (fontflag == InterfaceMacro.Pb_MeetFaceFontFlag.Pb_MEET_FONTFLAG_LEAN.getNumber()) {//倾斜
            tvContent.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
        } else if (fontflag == InterfaceMacro.Pb_MeetFaceFontFlag.Pb_MEET_FONTFLAG_UNDERLINE.getNumber()) {//下划线
            tvContent.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));//暂时用倾斜加粗
        } else {//正常文本
            tvContent.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }
        //对齐方式
        if (align == InterfaceMacro.Pb_FontAlignFlag.Pb_MEET_FONTALIGNFLAG_LEFT.getNumber()) {//左对齐
            tvContent.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        } else if (align == InterfaceMacro.Pb_FontAlignFlag.Pb_MEET_FONTALIGNFLAG_RIGHT.getNumber()) {//右对齐
            tvContent.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        } else if (align == InterfaceMacro.Pb_FontAlignFlag.Pb_MEET_FONTALIGNFLAG_HCENTER.getNumber()) {//水平对齐
            tvContent.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        } else if (align == InterfaceMacro.Pb_FontAlignFlag.Pb_MEET_FONTALIGNFLAG_TOP.getNumber()) {//上对齐
            tvContent.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        } else if (align == InterfaceMacro.Pb_FontAlignFlag.Pb_MEET_FONTALIGNFLAG_BOTTOM.getNumber()) {//下对齐
            tvContent.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        } else if (align == InterfaceMacro.Pb_FontAlignFlag.Pb_MEET_FONTALIGNFLAG_VCENTER.getNumber()) {//垂直对齐
            tvContent.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        } else {
            tvContent.setGravity(Gravity.CENTER);
        }
        //字体类型
        Typeface kt_typeface;
        if (!TextUtils.isEmpty(fontName)) {
            switch (fontName) {
                case "楷体":
                    kt_typeface = Typeface.createFromAsset(getAssets(), "kaiti.ttf");
                    break;
                case "隶书":
                    kt_typeface = Typeface.createFromAsset(getAssets(), "lishu.ttf");
                    break;
                case "微软雅黑":
                    kt_typeface = Typeface.createFromAsset(getAssets(), "weiruanyahei.ttf");
                    break;
                case "黑体":
                    kt_typeface = Typeface.createFromAsset(getAssets(), "heiti.ttf");
                    break;
                case "小楷":
                    kt_typeface = Typeface.createFromAsset(getAssets(), "xiaokai.ttf");
                    break;
                default:
                    kt_typeface = Typeface.createFromAsset(getAssets(), "fangsong.ttf");
                    break;
            }
            tvContent.setTypeface(kt_typeface);
        }
    }

    @Override
    public void updateCloseButton(InterfaceFaceconfig.pbui_Item_FaceTextItemInfo info) {
        String fontName = info.getFontname().toStringUtf8();
        int align = info.getAlign();
        int flag = info.getFlag();
        boolean isShow = (InterfaceMacro.Pb_MeetFaceFlag.Pb_MEET_FACEFLAG_SHOW_VALUE == (flag & InterfaceMacro.Pb_MeetFaceFlag.Pb_MEET_FACEFLAG_SHOW_VALUE));
        btnClose.setVisibility(isShow ? View.VISIBLE : View.GONE);
        int fontflag = info.getFontflag();
        btnClose.setTextColor(info.getColor());
        btnClose.setTextSize(info.getFontsize());
        update(R.id.btn_close, info);
        //字体样式
        if (fontflag == InterfaceMacro.Pb_MeetFaceFontFlag.Pb_MEET_FONTFLAG_BOLD.getNumber()) {//加粗
            btnClose.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        } else if (fontflag == InterfaceMacro.Pb_MeetFaceFontFlag.Pb_MEET_FONTFLAG_LEAN.getNumber()) {//倾斜
            btnClose.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
        } else if (fontflag == InterfaceMacro.Pb_MeetFaceFontFlag.Pb_MEET_FONTFLAG_UNDERLINE.getNumber()) {//下划线
            btnClose.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));//暂时用倾斜加粗
        } else {//正常文本
            btnClose.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }
        //对齐方式
        if (align == InterfaceMacro.Pb_FontAlignFlag.Pb_MEET_FONTALIGNFLAG_LEFT.getNumber()) {//左对齐
            btnClose.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        } else if (align == InterfaceMacro.Pb_FontAlignFlag.Pb_MEET_FONTALIGNFLAG_RIGHT.getNumber()) {//右对齐
            btnClose.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        } else if (align == InterfaceMacro.Pb_FontAlignFlag.Pb_MEET_FONTALIGNFLAG_HCENTER.getNumber()) {//水平对齐
            btnClose.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        } else if (align == InterfaceMacro.Pb_FontAlignFlag.Pb_MEET_FONTALIGNFLAG_TOP.getNumber()) {//上对齐
            btnClose.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        } else if (align == InterfaceMacro.Pb_FontAlignFlag.Pb_MEET_FONTALIGNFLAG_BOTTOM.getNumber()) {//下对齐
            btnClose.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        } else if (align == InterfaceMacro.Pb_FontAlignFlag.Pb_MEET_FONTALIGNFLAG_VCENTER.getNumber()) {//垂直对齐
            btnClose.setGravity(Gravity.CENTER_VERTICAL);
        } else {
            btnClose.setGravity(Gravity.CENTER);
        }
        //字体类型
        Typeface kt_typeface;
        if (!TextUtils.isEmpty(fontName)) {
            switch (fontName) {
                case "楷体":
                    kt_typeface = Typeface.createFromAsset(getAssets(), "kaiti.ttf");
                    break;
                case "隶书":
                    kt_typeface = Typeface.createFromAsset(getAssets(), "lishu.ttf");
                    break;
                case "微软雅黑":
                    kt_typeface = Typeface.createFromAsset(getAssets(), "weiruanyahei.ttf");
                    break;
                case "黑体":
                    kt_typeface = Typeface.createFromAsset(getAssets(), "heiti.ttf");
                    break;
                case "小楷":
                    kt_typeface = Typeface.createFromAsset(getAssets(), "xiaokai.ttf");
                    break;
                default:
                    kt_typeface = Typeface.createFromAsset(getAssets(), "fangsong.ttf");
                    break;
            }
            btnClose.setTypeface(kt_typeface);
        }
    }

    private void update(int resid, InterfaceFaceconfig.pbui_Item_FaceTextItemInfo info) {
        float ly = info.getLy();
        float lx = info.getLx();
        float bx = info.getBx();
        float by = info.getBy();
        ConstraintSet set = new ConstraintSet();
        set.clone(bulletinBgView);
        //设置控件的大小
        float width = (bx - lx) / 100 * GlobalValue.screen_width;
        float height = (by - ly) / 100 * GlobalValue.screen_height;
        set.constrainWidth(resid, (int) width);
        set.constrainHeight(resid, (int) height);
        float biasX, biasY;
        float halfW = (bx - lx) / 2 + lx;
        float halfH = (by - ly) / 2 + ly;

        if (lx == 0) biasX = 0;
        else if (lx > 50) biasX = bx / 100;
        else biasX = halfW / 100;

        if (ly == 0) biasY = 0;
        else if (ly > 50) biasY = by / 100;
        else biasY = halfH / 100;
        set.setHorizontalBias(resid, biasX);
        set.setVerticalBias(resid, biasY);
        set.applyTo(bulletinBgView);
    }

    private void initView() {
        bulletinBgView = (ConstraintLayout) findViewById(R.id.bulletin_bg_view);
        ivLogo = (ImageView) findViewById(R.id.iv_logo);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvContent = (TextView) findViewById(R.id.tv_content);
        btnClose = (Button) findViewById(R.id.btn_close);
        btnClose.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bulletinId = -1;
    }
}