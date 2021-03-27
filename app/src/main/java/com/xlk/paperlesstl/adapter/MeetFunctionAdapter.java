package com.xlk.paperlesstl.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.model.Constant;
import com.xlk.paperlesstl.model.data.MeetFunctionBean;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Created by xlk on 2021/3/8.
 * @desc 会议界面 会议功能适配器
 */
public class MeetFunctionAdapter extends BaseQuickAdapter<MeetFunctionBean, BaseViewHolder> {
    private int selectedId = -1;

    public MeetFunctionAdapter(@Nullable List<MeetFunctionBean> data) {
        super(R.layout.item_meet_function, data);
    }

    public void choose(int id) {
        selectedId = id;
        notifyDataSetChanged();
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, MeetFunctionBean item) {
        ImageView iv = holder.getView(R.id.item_view_1);
        switch (item.getFuncode()) {
            //会议议程
            case InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_AGENDA_BULLETIN_VALUE: {
                iv.setBackground(getContext().getDrawable(R.drawable.menu_agenda_s));
                break;
            }
            //会议资料
            case InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_MATERIAL_VALUE: {
                iv.setBackground(getContext().getDrawable(R.drawable.menu_file_s));
                break;
            }
            //共享文件
            case InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_SHAREDFILE_VALUE: {
                iv.setBackground(getContext().getDrawable(R.drawable.menu_sharefile_s));
                break;
            }
            //批注文件
            case InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_POSTIL_VALUE: {
                iv.setBackground(getContext().getDrawable(R.drawable.menu_note_s));
                break;
            }
            //会议交流
            case InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_MESSAGE_VALUE: {
                iv.setBackground(getContext().getDrawable(R.drawable.menu_message_s));
                break;
            }
            //视屏直播
            case InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_VIDEOSTREAM_VALUE: {
                iv.setBackground(getContext().getDrawable(R.drawable.menu_video_s));
                break;
            }
            //电子白板
            case InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_WHITEBOARD_VALUE: {
                iv.setBackground(getContext().getDrawable(R.drawable.menu_whiteboard_s));
                break;
            }
            //网页浏览
            case InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_WEBBROWSER_VALUE: {
                iv.setBackground(getContext().getDrawable(R.drawable.menu_web_s));
                break;
            }
            //签到信息
            case InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_SIGNINRESULT_VALUE: {
                iv.setBackground(getContext().getDrawable(R.drawable.menu_checkin_s));
                break;
            }
            //其它功能模块
            case Constant.FUN_CODE: {
                iv.setBackground(getContext().getDrawable(R.drawable.menu_other_s));
                break;
            }
        }
        iv.setSelected(selectedId == item.getFuncode());
    }
}
