package com.xlk.paperlesstl.view.draw;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Path;

import com.xlk.paperlesstl.ui.ArtBoard;
import com.xlk.paperlesstl.view.base.IBasePresenter;
import com.xlk.paperlesstl.view.base.IBaseView;

import java.util.List;

/**
 * @author Created by xlk on 2021/3/6.
 * @desc
 */
public interface DrawContract {
    interface View extends IBaseView {

        void updateOnlineMembers();

        void initCanvas();

        void drawAgain(List<ArtBoard.DrawPath> pathList);

        void drawZoomBmp(Bitmap bitmap);

        void setCanvasSize(int maxX, int maxY);

        void drawPath(Path path, Paint paint);

        void invalidate();

        void funDraw(Paint paint, float height, int canSee, float fx, float fy, String text);

        void drawText(String ptext, float lx, float ly, Paint paint);

        void updateBtnEnable();
    }

    interface Presenter extends IBasePresenter {

        void setIsAddBitmap(boolean b);

        void queryMember();
    }
}
