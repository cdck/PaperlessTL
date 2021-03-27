package com.xlk.paperlesstl.view.fragment.votecontrol;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.UriUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.google.protobuf.ByteString;
import com.intrusoft.scatter.ChartData;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceVote;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.adapter.VoteAdapter;
import com.xlk.paperlesstl.model.Constant;
import com.xlk.paperlesstl.util.DateUtil;
import com.xlk.paperlesstl.util.DialogUtil;
import com.xlk.paperlesstl.util.JxlUtil;
import com.xlk.paperlesstl.util.PopupUtil;
import com.xlk.paperlesstl.view.admin.adapter.SubmitMemberAdapter;
import com.xlk.paperlesstl.view.admin.adapter.VoteManageMemberAdapter;
import com.xlk.paperlesstl.view.admin.bean.ExportSubmitMember;
import com.xlk.paperlesstl.view.admin.fragment.after.vote.VoteResultFragment;
import com.xlk.paperlesstl.view.base.BaseFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.xlk.paperlesstl.model.Constant.REQUEST_CODE_IMPORT_ELECTION;
import static com.xlk.paperlesstl.util.ConvertUtil.s2b;

/**
 * @author Created by xlk on 2021/3/13.
 * @desc
 */
public class VoteControlFragment extends BaseFragment<VoteControlPresenter> implements VoteControlContract.View, View.OnClickListener {
    private RecyclerView rvVote;
    private EditText edtContent;
    private Button btnAdd;
    private Button btnModify;
    private Button btnDelete;
    private Button btnExportExcel;
    private Button btnImportExcel;
    private Button btnSeeDetails;
    private Button btnSeeChart;
    private VoteAdapter voteAdapter;
    private PopupWindow memberPop;
    private VoteManageMemberAdapter memberAdapter;
    private RecyclerView pop_vote_rv;
    private Spinner sp_countdown, sp_register;
    private boolean isVote;
    private LinearLayout ll_option;
    private EditText edt_option1;
    private EditText edt_option2;
    private EditText edt_option3;
    private EditText edt_option4;
    private EditText edt_option5;
    private TextView tv_type;
    private Spinner sp_type;
    private PopupWindow chartPop;
    private TextView tv_title;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_vote_control;
    }

    @Override
    protected void initView(View inflate) {
        tv_title = (TextView) inflate.findViewById(R.id.tv_title);
        rvVote = (RecyclerView) inflate.findViewById(R.id.rv_vote);
        edtContent = (EditText) inflate.findViewById(R.id.edt_content);
        btnAdd = (Button) inflate.findViewById(R.id.btn_add);
        btnModify = (Button) inflate.findViewById(R.id.btn_modify);
        btnDelete = (Button) inflate.findViewById(R.id.btn_delete);
        btnExportExcel = (Button) inflate.findViewById(R.id.btn_export_excel);
        btnImportExcel = (Button) inflate.findViewById(R.id.btn_import_excel);
        btnSeeDetails = (Button) inflate.findViewById(R.id.btn_see_details);
        btnSeeChart = (Button) inflate.findViewById(R.id.btn_see_chart);
        sp_countdown = inflate.findViewById(R.id.sp_countdown);
        sp_register = inflate.findViewById(R.id.sp_register);
        tv_type = inflate.findViewById(R.id.tv_type);
        sp_type = inflate.findViewById(R.id.sp_type);

        ll_option = inflate.findViewById(R.id.ll_option);
        edt_option1 = inflate.findViewById(R.id.edt_option1);
        edt_option2 = inflate.findViewById(R.id.edt_option2);
        edt_option3 = inflate.findViewById(R.id.edt_option3);
        edt_option4 = inflate.findViewById(R.id.edt_option4);
        edt_option5 = inflate.findViewById(R.id.edt_option5);
        btnAdd.setOnClickListener(this);
        btnModify.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnExportExcel.setOnClickListener(this);
        btnImportExcel.setOnClickListener(this);
        btnSeeDetails.setOnClickListener(this);
        btnSeeChart.setOnClickListener(this);
    }

    @Override
    protected VoteControlPresenter initPresenter() {
        return new VoteControlPresenter(this);
    }

    @Override
    protected void initial() {
        isVote = getArguments().getBoolean("isVote");
        tv_title.setVisibility(isVote ? View.VISIBLE : View.GONE);
        ll_option.setVisibility(isVote ? View.GONE : View.VISIBLE);
        tv_type.setVisibility(isVote ? View.GONE : View.VISIBLE);
        sp_type.setVisibility(isVote ? View.GONE : View.VISIBLE);
        presenter.setVoteMainType(isVote);
        presenter.queryVote();
        presenter.queryMember();
    }

    @Override
    protected void onShow() {
        initial();
    }

    @Override
    public void updateVoteList() {
        if (voteAdapter == null) {
            voteAdapter = new VoteAdapter(presenter.voteInfos);
            rvVote.setLayoutManager(new LinearLayoutManager(getContext()));
            rvVote.setAdapter(voteAdapter);
            voteAdapter.addChildClickViewIds(R.id.btn_launch_vote, R.id.btn_stop_vote);
            voteAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    InterfaceVote.pbui_Item_MeetVoteDetailInfo vote = presenter.voteInfos.get(position);
                    voteAdapter.choose(vote.getVoteid());
                    setVoteInfo(vote);
                }
            });
            voteAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
                @Override
                public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                    InterfaceVote.pbui_Item_MeetVoteDetailInfo vote = presenter.voteInfos.get(position);
                    if (view.getId() == R.id.btn_launch_vote) {
                        for (int i = 0; i < presenter.voteInfos.size(); i++) {
                            if (presenter.voteInfos.get(i).getVotestate() == InterfaceMacro.Pb_MeetVoteStatus.Pb_vote_voteing_VALUE) {
                                ToastUtils.showShort(R.string.please_stop_vote_first);
                                return;
                            }
                        }
                        if (vote.getVotestate() == InterfaceMacro.Pb_MeetVoteStatus.Pb_vote_endvote_VALUE) {
                            String string = (vote.getMaintype() == InterfaceMacro.Pb_MeetVoteType.Pb_VOTE_MAINTYPE_vote_VALUE)
                                    ? getString(R.string.end_vote_tip) : getString(R.string.end_election_tip);
                            DialogUtil.createTipDialog(getContext(), string, getString(R.string.ensure), getString(R.string.cancel),
                                    new DialogUtil.onDialogClickListener() {
                                        @Override
                                        public void positive(DialogInterface dialog) {
                                            dialog.dismiss();
                                            showMemberPop(vote, InterfaceMacro.Pb_VoteStartFlag.Pb_MEET_VOTING_FLAG_REVOTE_VALUE);
                                        }

                                        @Override
                                        public void negative(DialogInterface dialog) {
                                            dialog.dismiss();
                                        }

                                        @Override
                                        public void dismiss(DialogInterface dialog) {

                                        }
                                    });
                        } else {
                            showMemberPop(vote, InterfaceMacro.Pb_VoteStartFlag.Pb_MEET_VOTING_FLAG_AUTOEXIT_VALUE);
                        }
                    } else if (view.getId() == R.id.btn_stop_vote) {
                        jni.stopVote(vote.getVoteid());
                    }
                }
            });
        } else {
            voteAdapter.notifyDataSetChanged();
        }
    }

    private void setVoteInfo(InterfaceVote.pbui_Item_MeetVoteDetailInfo vote) {
        edtContent.setText(vote.getContent().toStringUtf8());
        //如果是选举还需要展示选项
        if (!isVote) {
            edt_option1.setText("");
            edt_option2.setText("");
            edt_option3.setText("");
            edt_option4.setText("");
            edt_option5.setText("");
            List<InterfaceVote.pbui_SubItem_VoteItemInfo> itemList = vote.getItemList();
            for (int i = 0; i < itemList.size(); i++) {
                InterfaceVote.pbui_SubItem_VoteItemInfo info = itemList.get(i);
                if (i == 0) edt_option1.setText(info.getText().toStringUtf8());
                else if (i == 1) edt_option2.setText(info.getText().toStringUtf8());
                else if (i == 2) edt_option3.setText(info.getText().toStringUtf8());
                else if (i == 3) edt_option4.setText(info.getText().toStringUtf8());
                else if (i == 4) edt_option5.setText(info.getText().toStringUtf8());
            }
        }
    }

    @Override
    public void updateMemberList() {
        LogUtils.i(TAG, "updateMemberList");
        if (memberPop != null && memberPop.isShowing()) {
            memberAdapter.notifyDataSetChanged();
            memberAdapter.notifyChoose();
        }
    }

    private void showMemberPop(InterfaceVote.pbui_Item_MeetVoteDetailInfo vote, int voteFlag) {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_vote_member, null);
        memberPop = PopupUtil.createHalfPop(inflate, rvVote);
        CheckBox pop_vote_all = inflate.findViewById(R.id.pop_vote_all);
        pop_vote_rv = inflate.findViewById(R.id.pop_vote_rv);
        memberAdapter = new VoteManageMemberAdapter(R.layout.item_vote_manage_member, presenter.memberInfos);
        pop_vote_rv.setLayoutManager(new LinearLayoutManager(getContext()));
        pop_vote_rv.setAdapter(memberAdapter);
        memberAdapter.setOnItemClickListener((adapter, view, position) -> {
            memberAdapter.setChoose(presenter.memberInfos.get(position).getMemberid());
            pop_vote_all.setChecked(memberAdapter.isChooseAll());
        });
        pop_vote_all.setOnClickListener(v -> {
            boolean checked = pop_vote_all.isChecked();
            pop_vote_all.setChecked(checked);
            memberAdapter.setChooseAll(checked);
        });
        inflate.findViewById(R.id.pop_vote_determine).setOnClickListener(v -> {
            List<Integer> memberIds = memberAdapter.getChoose();
            if (memberIds.isEmpty()) {
                ToastUtils.showShort(R.string.please_choose_member);
                return;
            }
//            if (vote.getVotestate() == InterfaceMacro.Pb_MeetVoteStatus.Pb_vote_notvote_VALUE) {
            int voteid = vote.getVoteid();
            int timeouts = getTimeouts();
            jni.launchVote(memberIds, voteid, timeouts, voteFlag);
//            } else {
//                ToastUtils.showShort(R.string.vote_changed);
//            }
            memberPop.dismiss();
        });
        inflate.findViewById(R.id.pop_vote_cancel).setOnClickListener(v -> {
            memberPop.dismiss();
        });
    }

    private int getTimeouts() {
        int position = sp_countdown.getSelectedItemPosition();
        int timeouts = 0;
        switch (position) {
            case 0:
                timeouts = 10;
                break;
            case 1:
                timeouts = 30;
                break;
            case 2:
                timeouts = 60;
                break;
            case 3:
                timeouts = 120;
                break;
            case 4:
                timeouts = 300;
                break;
            case 5:
                timeouts = 900;
                break;
            case 6:
                timeouts = 1800;
                break;
            case 7:
                timeouts = 36000;
                break;
            default:
                break;
        }
        return timeouts;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add: {
                createVote();
                break;
            }
            case R.id.btn_modify: {
                modifyVote();
                break;
            }
            case R.id.btn_delete: {
                InterfaceVote.pbui_Item_MeetVoteDetailInfo selected = voteAdapter.getSelected();
                if (selected == null) {
                    ToastUtils.showShort(R.string.please_choose_vote);
                    return;
                }
                jni.deleteVote(selected.getVoteid());
                break;
            }
            case R.id.btn_export_excel: {
                if (presenter.voteInfos.isEmpty()) {
                    ToastUtils.showShort(isVote ? R.string.no_vote_info : R.string.no_election_info);
                    return;
                }
                JxlUtil.exportVoteInfo(presenter.voteInfos, isVote ? getString(R.string.vote_fileName) : getString(R.string.election_fileName),
                        isVote ? getString(R.string.vote_content) : getString(R.string.election_content));
                break;
            }
            case R.id.btn_import_excel: {
                chooseLocalFile(REQUEST_CODE_IMPORT_ELECTION);
                break;
            }
            case R.id.btn_see_details: {
                InterfaceVote.pbui_Item_MeetVoteDetailInfo selected = voteAdapter.getSelected();
                if (selected == null) {
                    ToastUtils.showShort(R.string.please_choose_vote);
                    return;
                }
                if (selected.getMode() != InterfaceMacro.Pb_MeetVoteMode.Pb_VOTEMODE_agonymous_VALUE) {
                    if (selected.getVotestate() != InterfaceMacro.Pb_MeetVoteStatus.Pb_vote_notvote_VALUE) {
                        presenter.querySubmittedVoters(selected, true);
                    } else {
                        ToastUtils.showShort(R.string.can_not_choose_notvote);
                    }
                } else {
                    ToastUtils.showShort(R.string.please_choose_registered_vote);
                }
                break;
            }
            case R.id.btn_see_chart: {
                InterfaceVote.pbui_Item_MeetVoteDetailInfo selected = voteAdapter.getSelected();
                if (selected == null) {
                    ToastUtils.showShort(R.string.please_choose_vote);
                    return;
                }
                if (selected.getVotestate() != InterfaceMacro.Pb_MeetVoteStatus.Pb_vote_notvote_VALUE) {
                    presenter.querySubmittedVoters(selected, false);
                } else {
                    ToastUtils.showShort(R.string.can_not_choose_notvote);
                }
                break;
            }
        }
    }

    @Override
    public void showChartPop(InterfaceVote.pbui_Item_MeetVoteDetailInfo vote) {
        View fl_meet = getActivity().findViewById(R.id.fl_meet);
        int width = fl_meet.getWidth();
        int height = fl_meet.getHeight();
        int dp_10 = ConvertUtils.dp2px(10);
        int dp_20 = ConvertUtils.dp2px(20);
        LogUtils.e(TAG, "showSubmittedPop width=" + width + ",height=" + height + ",10dp=" + dp_10 + "px");
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_chart, null);
        chartPop = new PopupWindow(inflate, width, height);
        chartPop.setBackgroundDrawable(new BitmapDrawable());
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        chartPop.setTouchable(true);
        // true:设置触摸外面时消失
        chartPop.setOutsideTouchable(true);
        chartPop.setFocusable(true);
        //添加20dp的原因是pading值
        chartPop.showAtLocation(rvVote, Gravity.END | Gravity.BOTTOM, dp_10, dp_20);
        VoteResultFragment.ChartViewHolder chartViewHolder = new VoteResultFragment.ChartViewHolder(inflate);
        chartViewHolderEvent(chartViewHolder, vote);
    }

    private List<ChartData> chartDatas = new ArrayList<>();
    int countPre = 0;//一共占用的百分比数

    private void chartViewHolderEvent(VoteResultFragment.ChartViewHolder holder, InterfaceVote.pbui_Item_MeetVoteDetailInfo vote) {
        countPre = 0;
        chartDatas.clear();
        /** **** **  先隐藏所有的选项  ** **** **/
        holder.pop_option_a_ll.setVisibility(View.GONE);
        holder.pop_option_b_ll.setVisibility(View.GONE);
        holder.pop_option_c_ll.setVisibility(View.GONE);
        holder.pop_option_d_ll.setVisibility(View.GONE);
        holder.pop_option_e_ll.setVisibility(View.GONE);
        //饼状图形 需要先隐藏
        holder.pop_chart.setVisibility(View.GONE);
        int voteid = vote.getVoteid();
        String[] strings = presenter.queryYd(vote);
        String type = Constant.getVoteType(getContext(), vote.getType());
        String state = Constant.getVoteState(getContext(), vote.getVotestate());
        int maintype = vote.getMaintype();
        boolean isVote = maintype == InterfaceMacro.Pb_MeetVoteType.Pb_VOTE_MAINTYPE_vote_VALUE;
        String mode = vote.getMode() == 0 ? getString(R.string.mode_anonymous) : getString(R.string.mode_register);
        holder.pop_chart_type.setText("（" + type + " " + mode + " " + state + "）" + strings[0] + strings[1] + strings[2] + strings[3]);
        holder.pop_chart_title.setText(vote.getContent().toStringUtf8());
        List<InterfaceVote.pbui_SubItem_VoteItemInfo> optionInfo = vote.getItemList();
        int count = getCount(optionInfo);
        for (int i = 0; i < optionInfo.size(); i++) {
            InterfaceVote.pbui_SubItem_VoteItemInfo info = optionInfo.get(i);
            String text = info.getText().toStringUtf8();
            int selcnt = info.getSelcnt();
            if (!TextUtils.isEmpty(text)) {
                if (i == 0) {
                    holder.pop_option_a_ll.setVisibility(View.VISIBLE);
                    holder.pop_option_a_tv.setText(getString(R.string.vote_count, text, selcnt + ""));
                    int color = isVote ? ContextCompat.getColor(getContext(), R.color.vote_chart_color_green) : ContextCompat.getColor(getContext(), R.color.option_a);
                    holder.iv_option_a_color.setBackgroundColor(color);
                    setChartData(count, selcnt, Color.parseColor("#000000"), color);
                } else if (i == 1) {
                    holder.pop_option_b_ll.setVisibility(View.VISIBLE);
                    holder.pop_option_b_tv.setText(getString(R.string.vote_count, text, selcnt + ""));
                    int color = isVote ? ContextCompat.getColor(getContext(), R.color.vote_chart_color_red) : ContextCompat.getColor(getContext(), R.color.option_b);
                    holder.iv_option_b_color.setBackgroundColor(color);
                    setChartData(count, selcnt, Color.parseColor("#000000"), color);
                } else if (i == 2) {
                    holder.pop_option_c_ll.setVisibility(View.VISIBLE);
                    holder.pop_option_c_tv.setText(getString(R.string.vote_count, text, selcnt + ""));
                    int color = isVote ? ContextCompat.getColor(getContext(), R.color.vote_chart_color_yellow) : ContextCompat.getColor(getContext(), R.color.option_c);
                    holder.iv_option_c_color.setBackgroundColor(color);
                    setChartData(count, selcnt, Color.parseColor("#000000"), color);
                } else if (i == 3) {
                    holder.pop_option_d_ll.setVisibility(View.VISIBLE);
                    holder.pop_option_d_tv.setText(getString(R.string.vote_count, text, selcnt + ""));
                    setChartData(count, selcnt, Color.parseColor("#000000"), ContextCompat.getColor(getContext(), R.color.option_d));
                } else if (i == 4) {
                    holder.pop_option_e_ll.setVisibility(View.VISIBLE);
                    holder.pop_option_e_tv.setText(getString(R.string.vote_count, text, selcnt + ""));
                    setChartData(count, selcnt, Color.parseColor("#000000"), ContextCompat.getColor(getContext(), R.color.option_e));
                }
            }
        }
        if (countPre > 0 && countPre < 100) {//因为没有除尽,有余下的空白区域
            ChartData lastChartData = chartDatas.get(chartDatas.size() - 1);//先获取到最后一条的数据
            chartDatas.remove(chartDatas.size() - 1);//删除掉集合中的最后一个
            //使用原数据重新添加,但是修改所占比例大小,这样就能确保不会出现空白部分
            chartDatas.add(new ChartData(lastChartData.getDisplayText(), lastChartData.getPartInPercent() + (100 - countPre), lastChartData.getTextColor(), lastChartData.getBackgroundColor()));
        }
        //如果没有数据会报错
        if (chartDatas.isEmpty()) {
            chartDatas.add(new ChartData(getResources().getString(R.string.null_str), 100, Color.parseColor("#FFFFFF"), Color.parseColor("#7D7D7D")));
        }
        holder.pop_chart.setChartData(chartDatas);
        holder.pop_chart.setVisibility(View.VISIBLE);
        holder.pop_chart_close.setOnClickListener(v -> chartPop.dismiss());
    }

    private int setChartData(float count, int selcnt, int colora, int colorb) {
        if (selcnt > 0) {
            float element = (float) selcnt / count;
            LogUtils.d(TAG, "setChartData setUplistener :  element --> " + element);
            int v = (int) (element * 100);
            String str = v + "%";
            countPre += v;
            chartDatas.add(new ChartData(str, v, colora, colorb));
        }
        return countPre;
    }

    private int getCount(List<InterfaceVote.pbui_SubItem_VoteItemInfo> itemList) {
        int count = 0;
        for (int i = 0; i < itemList.size(); i++) {
            InterfaceVote.pbui_SubItem_VoteItemInfo info = itemList.get(i);
            count += info.getSelcnt();
        }
        LogUtils.e(TAG, "getCount :  当前投票票数总数 --> " + count);
        return count;
    }

    @Override
    public void showSubmittedPop(InterfaceVote.pbui_Item_MeetVoteDetailInfo vote) {
        View fl_meet = getActivity().findViewById(R.id.fl_meet);
        int width = fl_meet.getWidth();
        int height = fl_meet.getHeight();
        int dp_10 = ConvertUtils.dp2px(10);
        int dp_20 = ConvertUtils.dp2px(20);
        LogUtils.e(TAG, "showSubmittedPop width=" + width + ",height=" + height + ",10dp=" + dp_10 + "px");
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_submitted_member, null);
        PopupWindow popupWindow = new PopupWindow(inflate, width, height);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        popupWindow.setTouchable(true);
        // true:设置触摸外面时消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        //添加20dp的原因是pading值
        popupWindow.showAtLocation(rvVote, Gravity.END | Gravity.BOTTOM, dp_10, dp_20);

//        PopupWindow popupWindow = PopupUtil.createHalfPop(inflate, rvVote);
        SubmitMemberAdapter adapter = new SubmitMemberAdapter(R.layout.item_submit_member, presenter.submitMembers);
        RecyclerView submit_member_rv = inflate.findViewById(R.id.submit_member_rv);
        submit_member_rv.setLayoutManager(new LinearLayoutManager(getContext()));
        submit_member_rv.setAdapter(adapter);
        inflate.findViewById(R.id.submit_member_back).setOnClickListener(v -> popupWindow.dismiss());
        inflate.findViewById(R.id.submit_member_export).setOnClickListener(v -> {
            String[] strings = presenter.queryYd(vote);
            String createTime = DateUtil.nowDate();
            ExportSubmitMember exportSubmitMember = new ExportSubmitMember(vote.getContent().toStringUtf8(), createTime, strings[0], strings[1], strings[2], strings[3], presenter.submitMembers);
            JxlUtil.exportSubmitMember(exportSubmitMember);
        });
    }

    private void modifyVote() {
        InterfaceVote.pbui_Item_MeetVoteDetailInfo selected = voteAdapter.getSelected();
        if (selected == null) {
            ToastUtils.showShort(R.string.please_choose_vote);
            return;
        }
        String content = edtContent.getText().toString().trim();
        if (content.isEmpty()) {
            ToastUtils.showShort(R.string.please_enter_vote_content);
            return;
        }
        List<ByteString> answers = new ArrayList<>();
        if (isVote) {
            answers.add(s2b("赞成"));
            answers.add(s2b("反对"));
            answers.add(s2b("弃权"));
        } else {
            String trim1 = edt_option1.getText().toString().trim();
            String trim2 = edt_option2.getText().toString().trim();
            String trim3 = edt_option3.getText().toString().trim();
            String trim4 = edt_option4.getText().toString().trim();
            String trim5 = edt_option5.getText().toString().trim();
            int position = sp_type.getSelectedItemPosition();
            if (position == InterfaceMacro.Pb_MeetVote_SelType.Pb_VOTE_TYPE_4_5_VALUE
                    || (position == InterfaceMacro.Pb_MeetVote_SelType.Pb_VOTE_TYPE_3_5_VALUE)
                    || (position == InterfaceMacro.Pb_MeetVote_SelType.Pb_VOTE_TYPE_2_5_VALUE)
            ) {
                //5个选项都必须填写
                if (trim1.isEmpty() || trim2.isEmpty() || trim3.isEmpty() || trim4.isEmpty() || trim5.isEmpty()) {
                    ToastUtils.showShort(R.string.please_enter_5_answer);
                    return;
                } else {
                    answers.add(s2b(trim1));
                    answers.add(s2b(trim2));
                    answers.add(s2b(trim3));
                    answers.add(s2b(trim4));
                    answers.add(s2b(trim5));
                }
            } else if (position == InterfaceMacro.Pb_MeetVote_SelType.Pb_VOTE_TYPE_2_3_VALUE) {
                //最少需要填写3个选项
                if (trim1.isEmpty() || trim2.isEmpty() || trim3.isEmpty()) {
                    ToastUtils.showShort(R.string.please_enter_3_answer);
                    return;
                } else {
                    answers.add(s2b(trim1));
                    answers.add(s2b(trim2));
                    answers.add(s2b(trim3));
                }
            } else {
                if (trim1.isEmpty() || trim2.isEmpty()) {
                    ToastUtils.showShort(R.string.min_enter_2_answer);
                    return;
                }
                answers.add(s2b(trim1));
                answers.add(s2b(trim2));
                if (!trim3.isEmpty()) {
                    answers.add(s2b(trim3));
                }
                if (!trim4.isEmpty()) {
                    answers.add(s2b(trim4));
                }
                if (!trim5.isEmpty()) {
                    answers.add(s2b(trim5));
                }
            }
        }

        InterfaceVote.pbui_Item_MeetOnVotingDetailInfo build = InterfaceVote.pbui_Item_MeetOnVotingDetailInfo.newBuilder()
                .setVoteid(selected.getVoteid())
                .setMaintype(isVote ? InterfaceMacro.Pb_MeetVoteType.Pb_VOTE_MAINTYPE_vote_VALUE : InterfaceMacro.Pb_MeetVoteType.Pb_VOTE_MAINTYPE_election_VALUE)
                .setContent(s2b(content))
                .setMode(sp_register.getSelectedItemPosition())
                .setType(isVote ? InterfaceMacro.Pb_MeetVote_SelType.Pb_VOTE_TYPE_SINGLE_VALUE : sp_type.getSelectedItemPosition())
                .addAllText(answers)
                .setSelectcount(answers.size())
                .build();
        jni.modifyVote(build);
    }

    private void createVote() {
        String content = edtContent.getText().toString().trim();
        if (content.isEmpty()) {
            ToastUtils.showShort(R.string.please_enter_vote_content);
            return;
        }
        List<ByteString> answers = new ArrayList<>();
        if (isVote) {
            answers.add(s2b("赞成"));
            answers.add(s2b("反对"));
            answers.add(s2b("弃权"));
        } else {
            String trim1 = edt_option1.getText().toString().trim();
            String trim2 = edt_option2.getText().toString().trim();
            String trim3 = edt_option3.getText().toString().trim();
            String trim4 = edt_option4.getText().toString().trim();
            String trim5 = edt_option5.getText().toString().trim();
            int position = sp_type.getSelectedItemPosition();
            if (position == InterfaceMacro.Pb_MeetVote_SelType.Pb_VOTE_TYPE_4_5_VALUE
                    || (position == InterfaceMacro.Pb_MeetVote_SelType.Pb_VOTE_TYPE_3_5_VALUE)
                    || (position == InterfaceMacro.Pb_MeetVote_SelType.Pb_VOTE_TYPE_2_5_VALUE)
            ) {
                //5个选项都必须填写
                if (trim1.isEmpty() || trim2.isEmpty() || trim3.isEmpty() || trim4.isEmpty() || trim5.isEmpty()) {
                    ToastUtils.showShort(R.string.please_enter_5_answer);
                    return;
                } else {
                    answers.add(s2b(trim1));
                    answers.add(s2b(trim2));
                    answers.add(s2b(trim3));
                    answers.add(s2b(trim4));
                    answers.add(s2b(trim5));
                }
            } else if (position == InterfaceMacro.Pb_MeetVote_SelType.Pb_VOTE_TYPE_2_3_VALUE) {
                //最少需要填写3个选项
                if (trim1.isEmpty() || trim2.isEmpty() || trim3.isEmpty()) {
                    ToastUtils.showShort(R.string.please_enter_3_answer);
                    return;
                } else {
                    answers.add(s2b(trim1));
                    answers.add(s2b(trim2));
                    answers.add(s2b(trim3));
                }
            } else {
                if (trim1.isEmpty() || trim2.isEmpty()) {
                    ToastUtils.showShort(R.string.min_enter_2_answer);
                    return;
                }
                answers.add(s2b(trim1));
                answers.add(s2b(trim2));
                if (!trim3.isEmpty()) {
                    answers.add(s2b(trim3));
                }
                if (!trim4.isEmpty()) {
                    answers.add(s2b(trim4));
                }
                if (!trim5.isEmpty()) {
                    answers.add(s2b(trim5));
                }
            }
        }
        InterfaceVote.pbui_Item_MeetOnVotingDetailInfo build = InterfaceVote.pbui_Item_MeetOnVotingDetailInfo.newBuilder()
                .setContent(s2b(content))
                .setMaintype(isVote ? InterfaceMacro.Pb_MeetVoteType.Pb_VOTE_MAINTYPE_vote_VALUE : InterfaceMacro.Pb_MeetVoteType.Pb_VOTE_MAINTYPE_election_VALUE)
                .setMode(sp_register.getSelectedItemPosition())
                .setType(isVote ? InterfaceMacro.Pb_MeetVote_SelType.Pb_VOTE_TYPE_SINGLE_VALUE : sp_type.getSelectedItemPosition())
                .setSelectcount(answers.size())
                .addAllText(answers)
                .build();
        jni.createVote(build);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_IMPORT_ELECTION) {
            Uri uri = data.getData();
            File file = UriUtils.uri2File(uri);
            if (file != null) {
                String filePath = file.getAbsolutePath();
                LogUtils.i(TAG, "onActivityResult filePath=" + filePath);
                if (filePath.endsWith("xls") || filePath.endsWith("xlsx")) {
                    List<InterfaceVote.pbui_Item_MeetOnVotingDetailInfo> infos = JxlUtil.readVoteXls(filePath,
                            isVote ? InterfaceMacro.Pb_MeetVoteType.Pb_VOTE_MAINTYPE_vote_VALUE
                                    : InterfaceMacro.Pb_MeetVoteType.Pb_VOTE_MAINTYPE_election_VALUE);
                    jni.createMultipleVote(infos);
                } else {
                    ToastUtils.showShort(R.string.please_choose_xls_file);
                }
            }
        }
    }
}
