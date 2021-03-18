package com.xlk.paperlesstl.view.fragment.chat;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.mogujie.tt.protobuf.InterfaceIM;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.adapter.ChatMemberAdapter;
import com.xlk.paperlesstl.model.GlobalValue;
import com.xlk.paperlesstl.ui.RvItemDecoration;
import com.xlk.paperlesstl.view.admin.adapter.MulitpleItemAdapter;
import com.xlk.paperlesstl.view.admin.bean.ChatMessage;
import com.xlk.paperlesstl.view.base.BaseFragment;
import com.xlk.paperlesstl.view.videochat.ChatVideoActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.xlk.paperlesstl.util.ConvertUtil.s2b;

/**
 * @author Created by xlk on 2021/3/9.
 * @desc
 */
public class MeetChatFragment extends BaseFragment<MeetChatPresenter> implements MeetChatContract.View, View.OnClickListener {
    private CheckBox cbAll;
    private RecyclerView rvMember;
    private RecyclerView rvMessage;
    private EditText edtContent;
    private Button btnSend;
    private Button btnVideoChat;
    private ChatMemberAdapter chatMemberAdapter;
    private List<ChatMessage> chatMessages = new ArrayList<>();
    private MulitpleItemAdapter messageAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_meet_chat;
    }

    @Override
    protected void initView(View inflate) {
        cbAll = (CheckBox) inflate.findViewById(R.id.cb_all);
        rvMember = (RecyclerView) inflate.findViewById(R.id.rv_member);
        rvMessage = (RecyclerView) inflate.findViewById(R.id.rv_message);
        edtContent = (EditText) inflate.findViewById(R.id.edt_content);
        btnSend = (Button) inflate.findViewById(R.id.btn_send);
        btnVideoChat = (Button) inflate.findViewById(R.id.btn_video_chat);
        cbAll.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        btnVideoChat.setOnClickListener(this);
    }

    @Override
    protected MeetChatPresenter initPresenter() {
        return new MeetChatPresenter(this);
    }

    @Override
    protected void onShow() {
        initial();
    }

    @Override
    protected void initial() {
        presenter.queryMember();
    }

    @Override
    public void updateMembers() {
        if (chatMemberAdapter == null) {
            chatMemberAdapter = new ChatMemberAdapter(presenter.onlineMembers);
            rvMember.setLayoutManager(new LinearLayoutManager(getContext()));
            rvMember.setAdapter(chatMemberAdapter);
            rvMember.addItemDecoration(new RvItemDecoration(getContext()));
            chatMemberAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    chatMemberAdapter.choose(presenter.onlineMembers.get(position).getMemberDetailInfo().getPersonid());
                    cbAll.setChecked(chatMemberAdapter.isCheckAll());
                }
            });
        } else {
            chatMemberAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void addChatMessage(com.xlk.paperlesstl.view.admin.bean.ChatMessage chatMessage) {
        chatMessages.add(chatMessage);
        if (messageAdapter == null) {
            messageAdapter = new MulitpleItemAdapter(getContext(), chatMessages);
            rvMessage.setLayoutManager(new LinearLayoutManager(getContext()));
            rvMessage.setAdapter(messageAdapter);
        } else {
            messageAdapter.notifyDataSetChanged();
        }
        //移动到最后一条记录
        if (!chatMessages.isEmpty()) {
            rvMessage.smoothScrollToPosition(chatMessages.size() - 1);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cb_all: {
                boolean checked = cbAll.isChecked();
                cbAll.setChecked(checked);
                if (chatMemberAdapter != null) {
                    chatMemberAdapter.setCheckAll(checked);
                }
                break;
            }
            case R.id.btn_send: {
                String message = edtContent.getText().toString().trim();
                if (message.isEmpty()) {
                    ToastUtils.showShort(R.string.please_enter_content_first);
                    return;
                }
                List<Integer> selectedIds = chatMemberAdapter.getSelectedIds();
                if (selectedIds.isEmpty()) {
                    ToastUtils.showShort(R.string.please_choose_member);
                    return;
                }
                jni.sendChatMessage(message, InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Message.getNumber(), selectedIds);
                edtContent.setText("");
                InterfaceIM.pbui_Type_MeetIM build = InterfaceIM.pbui_Type_MeetIM.newBuilder()
                        .setMsgtype(0)
                        .setRole(GlobalValue.localRole)
                        .setMemberid(GlobalValue.localMemberId)
                        .setMsg(s2b(message))
                        .setUtcsecond(System.currentTimeMillis() / 1000)//需要换算成秒单位
                        .setMeetname(s2b(GlobalValue.localMeetingName))
                        .setRoomname(s2b(GlobalValue.localRoomName))
                        .setMembername(s2b(GlobalValue.localMemberName))
                        .setSeatename(s2b(GlobalValue.localDeviceName))
                        .addAllUserids(selectedIds)
                        .build();
                addChatMessage(new ChatMessage(1, build));
                break;
            }
            case R.id.btn_video_chat: {
                startActivity(new Intent(getContext(), ChatVideoActivity.class));
                break;
            }
        }
    }
}
