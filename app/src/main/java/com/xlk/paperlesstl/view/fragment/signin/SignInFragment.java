package com.xlk.paperlesstl.view.fragment.signin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.xlk.paperlesstl.App;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.model.data.EventMessage;
import com.xlk.paperlesstl.model.data.EventType;
import com.xlk.paperlesstl.ui.CustomSeatView;
import com.xlk.paperlesstl.view.base.BaseFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * @author Created by xlk on 2021/3/12.
 * @desc
 */
public class SignInFragment extends BaseFragment<SignInPresenter> implements SignInContract.View {

    private TextView tv_sign_in_status;
    private LinearLayout seat_root_ll;
    private CustomSeatView seat_absolute;
    private int width, height;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_meet_signin;
    }

    @Override
    protected void initView(View inflate) {
        tv_sign_in_status = inflate.findViewById(R.id.tv_sign_in_status);
        seat_root_ll = inflate.findViewById(R.id.seat_root_ll);
        seat_absolute = inflate.findViewById(R.id.seat_absolute);
        inflate.findViewById(R.id.btn_see_details).setOnClickListener(v -> {
            EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_SIGN_IN_DETAILS).build());
        });
    }

    @Override
    protected SignInPresenter initPresenter() {
        return new SignInPresenter(this);
    }

    @Override
    protected void initial() {
        seat_root_ll.post(() -> {
            seat_absolute.setViewSize(seat_root_ll.getWidth(), seat_root_ll.getHeight());
            presenter.queryRoomBg();
            presenter.queryMember();
        });
    }

    @Override
    protected void onShow() {
        presenter.queryRoomBg();
    }

    @Override
    public void updateRoomBg(String filepath) {
        Drawable drawable = Drawable.createFromPath(filepath);
        seat_absolute.setBackground(drawable);
        Bitmap bitmap = BitmapFactory.decodeFile(filepath);
        if (bitmap != null) {
            width = bitmap.getWidth();
            height = bitmap.getHeight();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
            seat_absolute.setLayoutParams(params);
            LogUtils.e(TAG, "updateRoomBg 图片宽高 -->" + width + ", " + height);
            presenter.queryPlaceDeviceRankingInfo();
            bitmap.recycle();
        }
    }

    @Override
    public void updateView(List<InterfaceRoom.pbui_Item_MeetRoomDevSeatDetailInfo> seatDetailInfos, int allMemberCount, int checkedMemberCount) {
        getActivity().runOnUiThread(() -> {
            tv_sign_in_status.setText(getString(R.string.sign_in_status, allMemberCount, checkedMemberCount, allMemberCount - checkedMemberCount));
            seat_absolute.removeAllViews();
            for (InterfaceRoom.pbui_Item_MeetRoomDevSeatDetailInfo info : seatDetailInfos) {
                LogUtils.d(TAG, "updateView -->左上角坐标：（" + info.getX() + "," + info.getY() + "）, 设备= " + info.getDevname().toStringUtf8());
                addSeat(info);
            }
        });
    }

    private void addSeat(InterfaceRoom.pbui_Item_MeetRoomDevSeatDetailInfo item) {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.item_seat, null);
        RelativeLayout.LayoutParams ivParams = new RelativeLayout.LayoutParams(
                30, 30);
//                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams seatLinearParams = new RelativeLayout.LayoutParams(120, 40);
        ImageView item_seat_iv = inflate.findViewById(R.id.item_seat_iv);
        LinearLayout item_seat_ll = inflate.findViewById(R.id.item_seat_ll);
        TextView item_seat_device = inflate.findViewById(R.id.item_seat_device);
        TextView item_seat_member = inflate.findViewById(R.id.item_seat_member);
        boolean isChecked = item.getIssignin() == 1;
        item_seat_iv.setSelected(isChecked);

        switch (item.getDirection()) {
            //上
            case 0:
                item_seat_iv.setImageResource(isChecked ? R.drawable.seat_p_t : R.drawable.seat_n_t);
                ivParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                ivParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                seatLinearParams.addRule(RelativeLayout.BELOW, item_seat_iv.getId());
                seatLinearParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                break;
            //下
            case 1:
                item_seat_iv.setImageResource(isChecked ? R.drawable.seat_p_b : R.drawable.seat_n_b);
                seatLinearParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                seatLinearParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                ivParams.addRule(RelativeLayout.BELOW, item_seat_ll.getId());
                ivParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                break;
            //左
            case 2:
                item_seat_iv.setImageResource(isChecked ? R.drawable.seat_p_l : R.drawable.seat_n_l);
                ivParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                ivParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                seatLinearParams.addRule(RelativeLayout.BELOW, item_seat_iv.getId());
                seatLinearParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                break;
            //右
            case 3:
                item_seat_iv.setImageResource(isChecked ? R.drawable.seat_p_r : R.drawable.seat_n_r);
                ivParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                ivParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                seatLinearParams.addRule(RelativeLayout.BELOW, item_seat_iv.getId());
                seatLinearParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                break;
            default:
                break;
        }
//        item_seat_iv.setVisibility(isShow ? View.VISIBLE : View.GONE);

        String devName = item.getDevname().toStringUtf8();
        if (!TextUtils.isEmpty(devName)) {
            item_seat_device.setText(devName);
        } else {
            item_seat_device.setVisibility(View.GONE);
        }

        String memberName = item.getMembername().toStringUtf8();
        if (!TextUtils.isEmpty(memberName)) {
            item_seat_member.setText(memberName);
            item_seat_member.setTextColor(isChecked
                    ? App.appContext.getColor(R.color.signed_text_color)
                    : App.appContext.getColor(R.color.unsigned_text_color)
            );
        } else {
            item_seat_member.setVisibility(View.GONE);
        }

        item_seat_iv.setLayoutParams(ivParams);
        item_seat_ll.setLayoutParams(seatLinearParams);
        //左上角x坐标
        float x1 = item.getX();
        //左上角y坐标
        float y1 = item.getY();
        if (x1 > 1) {
            x1 = 1;
        } else if (x1 < 0) {
            x1 = 0;
        }
        if (y1 > 1) {
            y1 = 1;
        } else if (y1 < 0) {
            y1 = 0;
        }
        int x = (int) (x1 * width);
        int y = (int) (y1 * height);

        AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,
                120, 70,
                x, y);
        inflate.setLayoutParams(params);
        seat_absolute.addView(inflate);
    }
}
