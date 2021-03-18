package com.xlk.paperlesstl.helper;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.intrusoft.scatter.PieChart;
import com.xlk.paperlesstl.R;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author xlk
 * @date 2020/4/24
 * @desc
 */
public class CustomBaseViewHolder {

    public static class ScreenViewHolder {
        public View rootView;
        public CheckBox wm_screen_mandatory;
        public TextView wm_screen_title;
        public TextView textView2;
        public CheckBox wm_screen_cb_attendee;
        public Button wm_screen_launch;
        public Button wm_screen_cancel;
        public CheckBox wm_screen_cb_projector;
        public RecyclerView wm_screen_rv_attendee;
        public RecyclerView wm_screen_rv_projector;

        public ScreenViewHolder(View rootView) {
            this.rootView = rootView;
            this.wm_screen_mandatory = (CheckBox) rootView.findViewById(R.id.wm_screen_mandatory);
            this.wm_screen_title = (TextView) rootView.findViewById(R.id.wm_screen_title);
            this.textView2 = (TextView) rootView.findViewById(R.id.textView2);
            this.wm_screen_cb_attendee = (CheckBox) rootView.findViewById(R.id.wm_screen_cb_attendee);
            this.wm_screen_launch = (Button) rootView.findViewById(R.id.wm_screen_launch);
            this.wm_screen_cancel = (Button) rootView.findViewById(R.id.wm_screen_cancel);
            this.wm_screen_cb_projector = (CheckBox) rootView.findViewById(R.id.wm_screen_cb_projector);
            this.wm_screen_rv_attendee = (RecyclerView) rootView.findViewById(R.id.wm_screen_rv_attendee);
            this.wm_screen_rv_projector = (RecyclerView) rootView.findViewById(R.id.wm_screen_rv_projector);
        }

    }

    public static class ProViewHolder {
        public View rootView;
        public CheckBox wm_pro_mandatory;
        public TextView wm_pro_title;
        public CheckBox wm_pro_all;
        public RecyclerView wm_pro_rv;
        public CheckBox wm_pro_full;
        public CheckBox wm_pro_flow1;
        public CheckBox wm_pro_flow2;
        public CheckBox wm_pro_flow3;
        public CheckBox wm_pro_flow4;
        public Button wm_pro_launch_pro;
        public Button wm_pro_cancel;

        public ProViewHolder(View rootView) {
            this.rootView = rootView;
            this.wm_pro_mandatory = (CheckBox) rootView.findViewById(R.id.wm_pro_mandatory);
            this.wm_pro_title = (TextView) rootView.findViewById(R.id.wm_pro_title);
            this.wm_pro_all = (CheckBox) rootView.findViewById(R.id.wm_pro_all);
            this.wm_pro_rv = (RecyclerView) rootView.findViewById(R.id.wm_pro_rv);
            this.wm_pro_full = (CheckBox) rootView.findViewById(R.id.wm_pro_full);
            this.wm_pro_flow1 = (CheckBox) rootView.findViewById(R.id.wm_pro_flow1);
            this.wm_pro_flow2 = (CheckBox) rootView.findViewById(R.id.wm_pro_flow2);
            this.wm_pro_flow3 = (CheckBox) rootView.findViewById(R.id.wm_pro_flow3);
            this.wm_pro_flow4 = (CheckBox) rootView.findViewById(R.id.wm_pro_flow4);
            this.wm_pro_launch_pro = (Button) rootView.findViewById(R.id.wm_pro_launch_pro);
            this.wm_pro_cancel = (Button) rootView.findViewById(R.id.wm_pro_cancel);
        }

    }

    public static class WmScreenViewHolder {
        public View rootView;
        public CheckBox cb_mandatory;
        public TextView tv_title;
        public CheckBox cb_member;
        public RecyclerView rv_member;
        public CheckBox cb_projector;
        public RecyclerView rv_projector;
        public Button btn_ensure;
        public Button btn_cancel;

        public WmScreenViewHolder(View rootView) {
            this.rootView = rootView;
            this.cb_mandatory = (CheckBox) rootView.findViewById(R.id.cb_mandatory);
            this.tv_title = (TextView) rootView.findViewById(R.id.tv_title);
            this.cb_member = (CheckBox) rootView.findViewById(R.id.cb_member);
            this.rv_member = (RecyclerView) rootView.findViewById(R.id.rv_member);
            this.cb_projector = (CheckBox) rootView.findViewById(R.id.cb_projector);
            this.rv_projector = (RecyclerView) rootView.findViewById(R.id.rv_projector);
            this.btn_ensure = (Button) rootView.findViewById(R.id.btn_ensure);
            this.btn_cancel = (Button) rootView.findViewById(R.id.btn_cancel);
        }

    }

    public static class WmProViewHolder {
        public View rootView;
        public CheckBox cb_mandatory;
        public TextView tv_title;
        public CheckBox cb_all;
        public RecyclerView rv_projector;
        public CheckBox cb_full;
        public CheckBox cb_flow_1;
        public CheckBox cb_flow_2;
        public CheckBox cb_flow_3;
        public CheckBox cb_flow_4;
        public Button btn_launch_pro;
        public Button btn_cancel;

        public WmProViewHolder(View rootView) {
            this.rootView = rootView;
            this.cb_mandatory = (CheckBox) rootView.findViewById(R.id.cb_mandatory);
            this.tv_title = (TextView) rootView.findViewById(R.id.tv_title);
            this.cb_all = (CheckBox) rootView.findViewById(R.id.cb_all);
            this.rv_projector = (RecyclerView) rootView.findViewById(R.id.rv_projector);
            this.cb_full = (CheckBox) rootView.findViewById(R.id.cb_full);
            this.cb_flow_1 = (CheckBox) rootView.findViewById(R.id.cb_flow_1);
            this.cb_flow_2 = (CheckBox) rootView.findViewById(R.id.cb_flow_2);
            this.cb_flow_3 = (CheckBox) rootView.findViewById(R.id.cb_flow_3);
            this.cb_flow_4 = (CheckBox) rootView.findViewById(R.id.cb_flow_4);
            this.btn_launch_pro = (Button) rootView.findViewById(R.id.btn_launch_pro);
            this.btn_cancel = (Button) rootView.findViewById(R.id.btn_cancel);
        }

    }

    /**
     * 会前设置-参会人员-参会人权限popupView
     */
    public static class PermissionViewHolder {
        public View rootView;
        public CheckBox item_tv_1;
        public RecyclerView rv_member_permission;
        public Button btn_add_screen;
        public Button btn_add_projection;
        public Button btn_add_upload;
        public Button btn_add_download;
        public Button btn_add_vote;
        public Button btn_save;
        public Button btn_del_screen;
        public Button btn_del_projection;
        public Button btn_del_upload;
        public Button btn_del_download;
        public Button btn_del_vote;
        public Button btn_back;

        public PermissionViewHolder(View rootView) {
            this.rootView = rootView;
            this.item_tv_1 = (CheckBox) rootView.findViewById(R.id.item_tv_1);
            this.rv_member_permission = (RecyclerView) rootView.findViewById(R.id.rv_member_permission);
            this.btn_add_screen = (Button) rootView.findViewById(R.id.btn_add_screen);
            this.btn_add_projection = (Button) rootView.findViewById(R.id.btn_add_projection);
            this.btn_add_upload = (Button) rootView.findViewById(R.id.btn_add_upload);
            this.btn_add_download = (Button) rootView.findViewById(R.id.btn_add_download);
            this.btn_add_vote = (Button) rootView.findViewById(R.id.btn_add_vote);
            this.btn_save = (Button) rootView.findViewById(R.id.btn_save);
            this.btn_del_screen = (Button) rootView.findViewById(R.id.btn_del_screen);
            this.btn_del_projection = (Button) rootView.findViewById(R.id.btn_del_projection);
            this.btn_del_upload = (Button) rootView.findViewById(R.id.btn_del_upload);
            this.btn_del_download = (Button) rootView.findViewById(R.id.btn_del_download);
            this.btn_del_vote = (Button) rootView.findViewById(R.id.btn_del_vote);
            this.btn_back = (Button) rootView.findViewById(R.id.btn_back);
        }

    }

    public static class NoteViewHolder {
        public View rootView;
        public ImageView iv_close;
        public EditText edt_note;
        public Button btn_export_note;
        public Button btn_save_local;
        public Button btn_back;
        public Button btn_cache;

        public NoteViewHolder(View rootView) {
            this.rootView = rootView;
            this.iv_close = (ImageView) rootView.findViewById(R.id.iv_close);
            this.edt_note = (EditText) rootView.findViewById(R.id.edt_note);
            this.btn_export_note = (Button) rootView.findViewById(R.id.btn_export_note);
            this.btn_save_local = (Button) rootView.findViewById(R.id.btn_save_local);
            this.btn_back = (Button) rootView.findViewById(R.id.btn_back);
            this.btn_cache = (Button) rootView.findViewById(R.id.btn_cache);
        }
    }

    public static class VoteResultViewHolder {
        public Button btn_vote;
        public Button btn_election;
        public ImageView closeIv;
        public RecyclerView voteTitleRv;
        public PieChart pieChart;
        public TextView optionA;
        public LinearLayout voteOptionA;
        public TextView optionB;
        public LinearLayout voteOptionB;
        public TextView optionC;
        public LinearLayout voteOptionC;
        public TextView optionD;
        public LinearLayout voteOptionD;
        public TextView optionE;
        public LinearLayout voteOptionE;
        public RecyclerView optionRv;
        public TextView vote_type_tv;
        public TextView member_count_tv;
        public TextView vote_title_tv;
        public ImageView vote_option_color_a;
        public ImageView vote_option_color_b;
        public ImageView vote_option_color_c;
        public ImageView vote_option_color_d;
        public ImageView vote_option_color_e;
        public LinearLayout vote_type_top_ll;
        public TextView vote_member_count_tv;

        public VoteResultViewHolder(View view) {
            btn_vote = view.findViewById(R.id.btn_vote);
            btn_election = view.findViewById(R.id.btn_election);
            closeIv = view.findViewById(R.id.close_iv);
            voteTitleRv = view.findViewById(R.id.vote_title_rv);
            pieChart = view.findViewById(R.id.pie_chart);
            optionA = view.findViewById(R.id.option_a);
            voteOptionA = view.findViewById(R.id.vote_option_a);
            optionB = view.findViewById(R.id.option_b);
            voteOptionB = view.findViewById(R.id.vote_option_b);
            optionC = view.findViewById(R.id.option_c);
            voteOptionC = view.findViewById(R.id.vote_option_c);
            optionD = view.findViewById(R.id.option_d);
            voteOptionD = view.findViewById(R.id.vote_option_d);
            optionE = view.findViewById(R.id.option_e);
            voteOptionE = view.findViewById(R.id.vote_option_e);
            optionRv = view.findViewById(R.id.option_rv);
            vote_type_tv = view.findViewById(R.id.vote_type_tv);
            member_count_tv = view.findViewById(R.id.member_count_tv);
            vote_title_tv = view.findViewById(R.id.vote_title_tv);
            vote_option_color_a = view.findViewById(R.id.vote_option_color_a);
            vote_option_color_b = view.findViewById(R.id.vote_option_color_b);
            vote_option_color_c = view.findViewById(R.id.vote_option_color_c);
            vote_option_color_d = view.findViewById(R.id.vote_option_color_d);
            vote_option_color_e = view.findViewById(R.id.vote_option_color_e);
            vote_type_top_ll = view.findViewById(R.id.vote_type_top_ll);
            vote_member_count_tv = view.findViewById(R.id.vote_member_count_tv);
        }
    }

}
