package com.xlk.paperlesstl.view.admin.fragment.after.archive;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ZipUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceAdmin;
import com.mogujie.tt.protobuf.InterfaceAgenda;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceBullet;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMeet;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.mogujie.tt.protobuf.InterfaceSignin;
import com.mogujie.tt.protobuf.InterfaceVote;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.xlk.paperlesstl.App;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.model.Constant;
import com.xlk.paperlesstl.model.GlobalValue;
import com.xlk.paperlesstl.model.data.EventMessage;
import com.xlk.paperlesstl.model.data.EventType;
import com.xlk.paperlesstl.util.DateUtil;
import com.xlk.paperlesstl.util.FileUtil;
import com.xlk.paperlesstl.util.JxlUtil;
import com.xlk.paperlesstl.view.admin.BasePresenter;
import com.xlk.paperlesstl.view.admin.fragment.after.signin.SignInBean;
import com.xlk.paperlesstl.view.admin.fragment.pre.member.MemberRoleBean;

import static com.xlk.paperlesstl.model.Constant.ANNOTATION_FILE_DIRECTORY_ID;
import static com.xlk.paperlesstl.model.Constant.SHARED_FILE_DIRECTORY_ID;

/**
 * @author Created by xlk on 2020/10/27.
 * @desc
 */
public class ArchivePresenter extends BasePresenter {
    private final ArchiveInterface view;
    /**
     * ????????????
     */
    private List<InterfaceBullet.pbui_Item_BulletDetailInfo> noticeData = new ArrayList<>();
    /**
     * ?????????????????????
     */
    private String agendaContent;
    /**
     * ???????????????id
     */
    private int agendaMediaId;
    /**
     * ?????????????????????
     */
    private int agendaType;
    /**
     * ?????????????????????
     */
    private InterfaceMeet.pbui_Item_MeetMeetInfo currentMeetInfo;
    /**
     * ????????????????????????????????????
     */
    private InterfaceRoom.pbui_Item_MeetRoomDetailInfo currentRoomInfo;
    /**
     * ??????????????????????????????
     */
    private InterfaceAdmin.pbui_Item_AdminDetailInfo currentAdminInfo;
    /**
     * ?????????????????????????????????????????????
     */
    private List<MemberRoleBean> devSeatInfos = new ArrayList<>();
    /**
     * ????????????
     */
    private List<SignInBean> signInData = new ArrayList<>();
    /**
     * ????????????
     */
    private List<InterfaceVote.pbui_Item_MeetVoteDetailInfo> voteData = new ArrayList<>();
    /**
     * ????????????
     */
    private List<InterfaceVote.pbui_Item_MeetVoteDetailInfo> electionData = new ArrayList<>();
    /**
     * ??????????????????
     */
    private List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> shareFileData = new ArrayList<>();
    /**
     * ??????????????????
     */
    private List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> annotationFileData = new ArrayList<>();
    /**
     * ????????????????????????????????????????????????
     */
    private List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> otherFileData = new ArrayList<>();
    /**
     * ????????????
     */
    private List<ArchiveInform> archiveInforms = new ArrayList<>();
    /**
     * ????????????????????????tag,?????????????????????????????????????????????????????????
     */
    private List<String> archiveTasks = new ArrayList<>();
    /**
     * =true??????????????????????????????
     */
    private boolean isCompressing;
    /**
     * =true?????????????????????????????????
     */
    private boolean isEncryption;


    public ArchivePresenter(ArchiveInterface view) {
        super();
        this.view = view;
    }

    public void queryAll() {
        queryNotice();
        queryAgenda();
        queryMeetById();
        queryRoom();
        queryAdmin();
        queryMember();
        querySignin();
        queryVote();
        queryDir();
    }

    /**
     * ????????????
     *
     * @param tag ??????tag
     */
    private void addTask(String tag) {
        if (!archiveTasks.contains(tag)) {
            LogUtils.i(TAG, "addTask ????????????=" + tag);
            archiveTasks.add(tag);
        }
    }

    /**
     * ????????????
     *
     * @param tag ??????tag
     */
    private void removeTask(String tag) {
        if (archiveTasks.contains(tag)) {
            LogUtils.d(TAG, "removeTask ????????????=" + tag);
            archiveTasks.remove(tag);
            if (archiveTasks.isEmpty()) {
                zipArchiveDir();
            }
        }
    }


    public boolean hasStarted() {
        return isCompressing || !archiveTasks.isEmpty();
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //??????????????????
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETBULLET_VALUE: {
                queryNotice();
                break;
            }
            //??????????????????
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETAGENDA_VALUE: {
                queryAgenda();
                break;
            }
            //????????????????????????
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETINFO_VALUE: {
                queryMeetById();
                break;
            }
            //????????????????????????
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOM_VALUE: {
                LogUtils.i(TAG, "BusEvent ????????????????????????");
                queryRoom();
                break;
            }
            //?????????????????????
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ADMIN_VALUE: {
                LogUtils.i(TAG, "busEvent ?????????????????????");
                queryAdmin();
                break;
            }
            //????????????????????????
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSEAT_VALUE: {
                LogUtils.i(TAG, "busEvent " + "????????????????????????");
                queryPlaceRanking();
                break;
            }
            //????????????????????????
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER_VALUE: {
                LogUtils.i(TAG, "BusEvent -->" + "????????????????????????");
                queryMember();
                break;
            }
            //??????????????????
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSIGN_VALUE: {
                LogUtils.i(TAG, "busEvent ??????????????????");
                querySignin();
                break;
            }
            //??????????????????
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETVOTEINFO_VALUE: {
                LogUtils.d(TAG, "BusEvent -->" + "??????????????????");
                queryVote();
                break;
            }
            //??????????????????????????????
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORYFILE_VALUE: {
                byte[] bytes = (byte[]) msg.getObjects()[0];
                InterfaceBase.pbui_MeetNotifyMsgForDouble info = InterfaceBase.pbui_MeetNotifyMsgForDouble.parseFrom(bytes);
                int opermethod = info.getOpermethod();
                int id = info.getId();
                int subid = info.getSubid();
                LogUtils.i(TAG, "busEvent ?????????????????????????????? id=" + id + ",subid=" + subid + ",opermethod=" + opermethod);
                queryDirFile(id);
                break;
            }
            //????????????????????????
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORY_VALUE: {
                LogUtils.i(TAG, "busEvent ????????????????????????");
                queryDir();
                break;
            }
            //??????????????????????????????
            case EventType.ARCHIVE_BUS_AGENDA_FILE: {
                archiveInforms.add(new ArchiveInform("??????????????????????????????", "100%"));
                view.updateArchiveInform(archiveInforms);
                break;
            }
            //?????????????????????????????????
            case EventType.ARCHIVE_BUS_DOWNLOAD_FILE: {
                Object[] objects = msg.getObjects();
                int mediaId = (int) objects[0];
                String fileName = (String) objects[1];
                int progress = (int) objects[2];
                for (int i = 0; i < archiveInforms.size(); i++) {
                    ArchiveInform archiveInform = archiveInforms.get(i);
                    if (archiveInform.getMediaId() == mediaId) {
                        archiveInform.setContent("?????????????????????" + fileName);
                        archiveInform.setResult("???????????????" + progress + "%");
                        break;
                    }
                }
                view.updateArchiveInform(archiveInforms);
                if (progress == 100) {
                    removeTask(String.valueOf(mediaId));
                } else {
                    addTask(String.valueOf(mediaId));
                }
                break;
            }
            default:
                break;
        }
    }


    public void zipArchiveDir() {
        App.threadPool.execute(() -> {
            try {
                Thread.sleep(500);
                if (!archiveTasks.isEmpty()) {
                    LogUtils.i(TAG, "run ???????????????????????????");
                    return;
                }
                if (isCompressing) {
                    LogUtils.i(TAG, "zipArchiveDir ??????????????????...");
                    return;
                }
                File srcFile = new File(Constant.DIR_ARCHIVE_TEMP);
                if (!srcFile.exists()) {
                    LogUtils.e(TAG, "zipArchiveDir ????????????????????????=" + Constant.DIR_ARCHIVE_TEMP);
                    return;
                }
                isCompressing = true;
                LogUtils.i(TAG, "run ???????????? ????????????=" + Thread.currentThread().getId());
                archiveInforms.add(new ArchiveInform("????????????", "?????????..."));
                view.updateArchiveInform(archiveInforms);
                FileUtils.createOrExistsDir(Constant.DIR_ARCHIVE_ZIP);
                String zipFilePath = Constant.DIR_ARCHIVE_ZIP + "????????????.zip";
                File zipFile = new File(zipFilePath);
                if (zipFile.exists()) {
                    zipFilePath = Constant.DIR_ARCHIVE_ZIP + "????????????-" + DateUtil.nowDate() + ".zip";
                }
//                System.out.println("??????????????????????????????" + getEncoding(zipFilePath));
//                Properties initProp = new Properties(System.getProperties());
//                Charset charset = Charset.defaultCharset();
//                System.out.println("charset:" + charset.name() + ",toString=" + charset.toString());
//                System.out.println("??????????????????:" + initProp.getProperty("file.encoding"));
//                System.out.println("??????????????????:" + initProp.getProperty("user.language"));

//                if (isEncryption) {
//                    File file = new File(Constant.DIR_ARCHIVE_TEMP);
//                    ZipUtil.doZipFilesWithPassword(file, zipFilePath, "123456");
//                } else {
                ZipUtils.zipFile(Constant.DIR_ARCHIVE_TEMP, zipFilePath);
//                }
                for (int i = 0; i < archiveInforms.size(); i++) {
                    ArchiveInform archiveInform = archiveInforms.get(i);
                    if (archiveInform.getContent().equals("????????????")) {
                        archiveInform.setContent("????????????");
                        archiveInform.setResult("100%");
                        break;
                    }
                }
                view.updateArchiveInform(archiveInforms);
                LogUtils.i(TAG, "run ????????????");
                FileUtil.delDirFile(Constant.DIR_ARCHIVE_TEMP);
//                FileUtils.deleteAllInDir(Constant.DIR_ARCHIVE_TEMP);

                isCompressing = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static String getEncoding(String str) {
        String encode = "GB2312";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) { //???????????????GB2312
                String s = encode;
                return s; //?????????????????????GB2312????????????????????????
            }
        } catch (Exception exception) {
        }
        encode = "ISO-8859-1";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) { //???????????????ISO-8859-1
                String s1 = encode;
                return s1;
            }
        } catch (Exception exception1) {
        }
        encode = "UTF-8";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) { //???????????????UTF-8
                String s2 = encode;
                return s2;
            }
        } catch (Exception exception2) {
        }
        encode = "GBK";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) { //???????????????GBK
                String s3 = encode;
                return s3;
            }
        } catch (Exception exception3) {
        }
        return "";
    }

    private void zip(ZipOutputStream zout, File target, String name, BufferedOutputStream bos) throws IOException {
        //?????????????????????
        if (target.isDirectory()) {
            File[] files = target.listFiles();
            //?????????
            if (files.length == 0) {
                zout.putNextEntry(new ZipEntry(name + "/"));
            /*  ??????????????????ZIP?????????????????????????????????????????????????????????
              ?????????????????????????????????????????? ??????????????????????????????????????????
              ?????????????????????????????????????????????????????????????????????????????????????????????*/
            }
            for (File f : files) {
                //????????????
                zip(zout, f, name + "/" + f.getName(), bos);
            }
        } else {
            zout.putNextEntry(new ZipEntry(name));
            InputStream inputStream = new FileInputStream(target);
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            byte[] bytes = new byte[1024];
            int len = -1;
            while ((len = bis.read(bytes)) != -1) {
                bos.write(bytes, 0, len);
            }
            bis.close();
        }
    }

    private void queryDir() {
        InterfaceFile.pbui_Type_MeetDirDetailInfo dir = jni.queryMeetDir();
        if (dir != null) {
            for (int i = 0; i < dir.getItemList().size(); i++) {
                InterfaceFile.pbui_Item_MeetDirDetailInfo item = dir.getItemList().get(i);
                queryDirFile(item.getId());
            }
        }
    }

    private void queryDirFile(int dirId) {
        InterfaceFile.pbui_Type_MeetDirFileDetailInfo pbui_type_meetDirFileDetailInfo = jni.queryMeetDirFile(dirId);
        if (dirId == ANNOTATION_FILE_DIRECTORY_ID) {
            annotationFileData.clear();
            if (pbui_type_meetDirFileDetailInfo != null) {
                annotationFileData.addAll(pbui_type_meetDirFileDetailInfo.getItemList());
            }
        } else if (dirId == SHARED_FILE_DIRECTORY_ID) {
            shareFileData.clear();
            if (pbui_type_meetDirFileDetailInfo != null) {
                shareFileData.addAll(pbui_type_meetDirFileDetailInfo.getItemList());
            }
        } else {
            otherFileData.clear();
            if (pbui_type_meetDirFileDetailInfo != null) {
                otherFileData.addAll(pbui_type_meetDirFileDetailInfo.getItemList());
            }
        }
    }

    private void queryVote() {
        InterfaceVote.pbui_Type_MeetVoteDetailInfo pbui_type_meetVoteDetailInfo = jni.queryVote();
        if (pbui_type_meetVoteDetailInfo != null) {
            List<InterfaceVote.pbui_Item_MeetVoteDetailInfo> itemList = pbui_type_meetVoteDetailInfo.getItemList();
            for (int i = 0; i < itemList.size(); i++) {
                InterfaceVote.pbui_Item_MeetVoteDetailInfo item = pbui_type_meetVoteDetailInfo.getItem(i);
                if (item.getMaintype() == InterfaceMacro.Pb_MeetVoteType.Pb_VOTE_MAINTYPE_vote_VALUE) {
                    voteData.add(item);
                } else if (item.getMaintype() == InterfaceMacro.Pb_MeetVoteType.Pb_VOTE_MAINTYPE_election_VALUE) {
                    electionData.add(item);
                }
            }
        }
    }

    private void queryMember() {
        InterfaceMember.pbui_Type_MemberDetailInfo pbui_type_memberDetailInfo = jni.queryMember();
        devSeatInfos.clear();
        signInData.clear();
        if (pbui_type_memberDetailInfo != null) {
            List<InterfaceMember.pbui_Item_MemberDetailInfo> itemList = pbui_type_memberDetailInfo.getItemList();
            for (int i = 0; i < itemList.size(); i++) {
                devSeatInfos.add(new MemberRoleBean(itemList.get(i)));
                signInData.add(new SignInBean(itemList.get(i)));
            }
        }
        querySignin();
        queryPlaceRanking();
    }

    private void queryPlaceRanking() {
        InterfaceRoom.pbui_Type_MeetRoomDevSeatDetailInfo info = jni.placeDeviceRankingInfo(queryCurrentRoomId());
        if (info != null) {
            for (int i = 0; i < devSeatInfos.size(); i++) {
                MemberRoleBean bean = devSeatInfos.get(i);
                for (int j = 0; j < info.getItemList().size(); j++) {
                    InterfaceRoom.pbui_Item_MeetRoomDevSeatDetailInfo item = info.getItemList().get(j);
                    if (item.getMemberid() == bean.getMember().getPersonid()) {
                        bean.setSeat(item);
                        break;
                    }
                }
            }
        }
    }

    private void querySignin() {
        try {
            InterfaceSignin.pbui_Type_MeetSignInDetailInfo pbui_type_meetSignInDetailInfo = jni.querySignin();
            if (pbui_type_meetSignInDetailInfo != null) {
                List<InterfaceSignin.pbui_Item_MeetSignInDetailInfo> itemList = pbui_type_meetSignInDetailInfo.getItemList();
                for (int i = 0; i < signInData.size(); i++) {
                    SignInBean signInBean = signInData.get(i);
                    for (int j = 0; j < itemList.size(); j++) {
                        InterfaceSignin.pbui_Item_MeetSignInDetailInfo item = itemList.get(j);
                        if (item.getNameId() == signInBean.getMember().getPersonid()) {
                            signInBean.setSign(item);
                            break;
                        }
                    }
                }
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    private void queryAdmin() {
        int currentAdminId = queryCurrentAdminId();
        InterfaceAdmin.pbui_TypeAdminDetailInfo pbui_typeAdminDetailInfo = jni.queryAdmin();
        if (pbui_typeAdminDetailInfo != null) {
            List<InterfaceAdmin.pbui_Item_AdminDetailInfo> itemList = pbui_typeAdminDetailInfo.getItemList();
            for (int i = 0; i < itemList.size(); i++) {
                InterfaceAdmin.pbui_Item_AdminDetailInfo pbui_item_adminDetailInfo = itemList.get(i);
                if (pbui_item_adminDetailInfo.getAdminid() == currentAdminId) {
                    currentAdminInfo = pbui_item_adminDetailInfo;
                    break;
                }
            }
        }
    }

    private void queryAgenda() {
        InterfaceAgenda.pbui_meetAgenda meetAgenda = jni.queryAgenda();
        if (meetAgenda != null) {
            agendaType = meetAgenda.getAgendatype();
            agendaContent = meetAgenda.getText().toStringUtf8();
            agendaMediaId = meetAgenda.getMediaid();
            LogUtils.i(TAG, "queryAgenda agendaMediaId=" + agendaMediaId + ",agendaContent=" + agendaContent.length());
        }
    }

    private void queryNotice() {
        InterfaceBullet.pbui_BulletDetailInfo pbui_bulletDetailInfo = jni.queryNotice();
        noticeData.clear();
        if (pbui_bulletDetailInfo != null) {
            noticeData.addAll(pbui_bulletDetailInfo.getItemList());
        }
    }

    private void queryMeetById() {
        try {
            InterfaceMeet.pbui_Item_MeetMeetInfo info = jni.queryMeetFromId(queryCurrentMeetId());
            currentMeetInfo = info;
            queryRoom();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    private void queryRoom() {
        InterfaceRoom.pbui_Item_MeetRoomDetailInfo room = jni.queryRoomById(queryCurrentRoomId());
        currentRoomInfo = room;
    }

    /**
     * ????????????????????????
     *
     * @param isEncryption =true ????????????
     */
    public void setEncryption(boolean isEncryption) {
        if (isCompressing || !archiveTasks.isEmpty()) {
            return;
        }
        this.isEncryption = isEncryption;
        LogUtils.i(TAG, "setEncryption ????????????=" + isEncryption);
    }

    /**
     * ??????????????????
     */
    public void archiveAll() {
//        if (isCompressing || !archiveTasks.isEmpty()) {
//            view.showToast(R.string.please_wait_archive_complete_first);
//            return;
//        }
        archiveInforms.clear();
        archiveTasks.clear();
        long l = System.currentTimeMillis();
        archiveMeetInfo();
        archiveMemberInfo();
        archiveSignInfo();
        archiveVoteInfo();
        archiveShareInfo();
        archiveAnnotationInfo();
        archiveMeetData();
        LogUtils.i(TAG, "archiveAll ??????????????????" + (System.currentTimeMillis() - l));
    }

    /**
     * ??????????????????
     *
     * @param meetInfo       ??????????????????
     * @param memberInfo     ??????????????????
     * @param signInfo       ??????????????????
     * @param voteInfo       ??????????????????
     * @param shareInfo      ??????????????????
     * @param annotationInfo ??????????????????
     * @param meetData       ????????????
     */
    public void archiveSelected(boolean meetInfo, boolean memberInfo, boolean signInfo, boolean voteInfo,
                                boolean shareInfo, boolean annotationInfo, boolean meetData) {
//        if (isCompressing || !archiveTasks.isEmpty()) {
//            view.showToast(R.string.please_wait_archive_complete_first);
//            return;
//        }
        archiveInforms.clear();
        archiveTasks.clear();
        if (meetInfo) {
            archiveMeetInfo();
        }
        if (memberInfo) {
            archiveMemberInfo();
        }
        if (signInfo) {
            archiveSignInfo();
        }
        if (voteInfo) {
            archiveVoteInfo();
        }
        if (shareInfo) {
            archiveShareInfo();
        }
        if (annotationInfo) {
            archiveAnnotationInfo();
        }
        if (meetData) {
            archiveMeetData();
        }
    }

    /**
     * ????????????????????????
     */
    private void archiveMeetInfo() {
        addTask("????????????????????????");
        long l = System.currentTimeMillis();
        //??????????????????
        meetInfo2file();
        // ??????????????????
        if (agendaType == InterfaceMacro.Pb_AgendaType.Pb_MEET_AGENDA_TYPE_TEXT_VALUE) {
            read2file("??????????????????.txt", agendaContent);
        } else if (agendaType == InterfaceMacro.Pb_AgendaType.Pb_MEET_AGENDA_TYPE_FILE_VALUE) {
            downloadAgendaFile();
        }
        // ??????????????????
        notice2file();
        LogUtils.i(TAG, "???????????????????????? ?????????" + (System.currentTimeMillis() - l));
        removeTask("????????????????????????");
    }

    /**
     * ?????????????????????
     */
    private void archiveMemberInfo() {
        if (devSeatInfos.isEmpty()) {
            return;
        }
        long l = System.currentTimeMillis();
        addTask("?????????????????????");
        if (JxlUtil.exportMemberInfo(devSeatInfos)) {
            LogUtils.i(TAG, "????????????????????? ??????=" + (System.currentTimeMillis() - l));
            archiveInforms.add(new ArchiveInform("??????????????????????????????", "100%"));
            view.updateArchiveInform(archiveInforms);
            removeTask("?????????????????????");
        }
    }

    /**
     * ??????????????????
     */
    private void archiveSignInfo() {
        if (signInData.isEmpty()) {
            return;
        }
        addTask("??????????????????");
        long l = System.currentTimeMillis();
        JxlUtil.exportArchiveSignIn(signInData);
        LogUtils.i(TAG, "?????????????????? ??????=" + (System.currentTimeMillis() - l));
        archiveInforms.add(new ArchiveInform("????????????????????????", "100%"));
        view.updateArchiveInform(archiveInforms);
        removeTask("??????????????????");
    }

    /**
     * ??????????????????
     */
    private void archiveVoteInfo() {
        addTask("??????????????????");
        long l = System.currentTimeMillis();
        if (!voteData.isEmpty()) {
            JxlUtil.exportArchiveVote(voteData, devSeatInfos.size(), true);
        }
        if (!electionData.isEmpty()) {
            JxlUtil.exportArchiveVote(electionData, devSeatInfos.size(), false);
        }
        LogUtils.i(TAG, "?????????????????? ?????????" + (System.currentTimeMillis() - l));
        archiveInforms.add(new ArchiveInform("????????????????????????", "100%"));
        view.updateArchiveInform(archiveInforms);
        removeTask("??????????????????");
    }

    /**
     * ??????????????????
     */
    private void archiveShareInfo() {
        if (shareFileData.isEmpty()) {
            return;
        }
        FileUtils.createOrExistsDir(Constant.DIR_ARCHIVE_TEMP + "????????????/");
        for (int i = 0; i < shareFileData.size(); i++) {
            InterfaceFile.pbui_Item_MeetDirFileDetailInfo item = shareFileData.get(i);
            String fileName = item.getName().toStringUtf8();
            archiveInforms.add(new ArchiveInform(0, item.getMediaid(), "?????????????????????" + fileName, "0%"));
            view.updateArchiveInform(archiveInforms);
            addTask(String.valueOf(item.getMediaid()));
            jni.creationFileDownload(Constant.DIR_ARCHIVE_TEMP + "????????????/" + fileName, item.getMediaid(), 1, 0, Constant.ARCHIVE_DOWNLOAD_FILE);
        }
    }

    /**
     * ??????????????????
     */
    private void archiveAnnotationInfo() {
        if (annotationFileData.isEmpty()) {
            return;
        }
        FileUtils.createOrExistsDir(Constant.DIR_ARCHIVE_TEMP + "????????????/");
        for (int i = 0; i < annotationFileData.size(); i++) {
            InterfaceFile.pbui_Item_MeetDirFileDetailInfo item = annotationFileData.get(i);
            String fileName = item.getName().toStringUtf8();
            archiveInforms.add(new ArchiveInform(1, item.getMediaid(), "?????????????????????" + fileName, "0%"));
            view.updateArchiveInform(archiveInforms);
            addTask(String.valueOf(item.getMediaid()));
            jni.creationFileDownload(Constant.DIR_ARCHIVE_TEMP + "????????????/" + fileName, item.getMediaid(), 1, 0, Constant.ARCHIVE_DOWNLOAD_FILE);
        }
    }

    /**
     * ??????????????????
     */
    private void archiveMeetData() {
        if (otherFileData.isEmpty()) {
            return;
        }
        FileUtils.createOrExistsDir(Constant.DIR_ARCHIVE_TEMP + "????????????/");
        for (int i = 0; i < otherFileData.size(); i++) {
            InterfaceFile.pbui_Item_MeetDirFileDetailInfo item = otherFileData.get(i);
            String fileName = item.getName().toStringUtf8();
            archiveInforms.add(new ArchiveInform(2, item.getMediaid(), "?????????????????????" + fileName, "0%"));
            view.updateArchiveInform(archiveInforms);
            addTask(String.valueOf(item.getMediaid()));
            jni.creationFileDownload(Constant.DIR_ARCHIVE_TEMP + "????????????/" + fileName, item.getMediaid(), 1, 0, Constant.ARCHIVE_DOWNLOAD_FILE);
        }
    }

    /**
     * ??????????????????
     */
    private void downloadAgendaFile() {
        byte[] bytes = jni.queryFileProperty(InterfaceMacro.Pb_MeetFilePropertyID.Pb_MEETFILE_PROPERTY_NAME.getNumber(), agendaMediaId);
        InterfaceBase.pbui_CommonTextProperty textProperty = null;
        try {
            textProperty = InterfaceBase.pbui_CommonTextProperty.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        String fileName = textProperty.getPropertyval().toStringUtf8();
        LogUtils.i(TAG, "downloadAgendaFile ????????????????????? -->??????id=" + agendaMediaId + ", ?????????=" + fileName);
        FileUtils.createOrExistsDir(Constant.DIR_ARCHIVE_TEMP);
        File file = new File(Constant.DIR_ARCHIVE_TEMP + fileName);
        if (file.exists()) {
            if (GlobalValue.downloadingFiles.contains(agendaMediaId)) {
                view.showToast(R.string.currently_downloading);
            }
        } else {
            addTask(String.valueOf(agendaMediaId));
            jni.creationFileDownload(Constant.DIR_ARCHIVE_TEMP + fileName, agendaMediaId, 1, 0,
                    Constant.ARCHIVE_AGENDA_FILE);
        }
    }

    /**
     * ??????????????????????????????
     */
    private void meetInfo2file() {
        if (currentMeetInfo != null) {
            String content = "";
            content += "???????????????" + currentMeetInfo.getName().toStringUtf8()
                    + "\n???????????????" + currentRoomInfo.getName().toStringUtf8()
                    + "\n???????????????" + currentRoomInfo.getAddr().toStringUtf8()
                    + "\n???????????????" + (currentMeetInfo.getSecrecy() == 1 ? "???" : "???")
                    + "\n?????????????????????" + DateUtil.millisecondFormatDetailedTime(currentMeetInfo.getStartTime() * 1000)
                    + "\n?????????????????????" + DateUtil.millisecondFormatDetailedTime(currentMeetInfo.getEndTime() * 1000)
                    + "\n???????????????" + Constant.getMeetSignInTypeName(currentMeetInfo.getSigninType())
                    + "\n??????????????????" + (currentAdminInfo != null ? currentAdminInfo.getAdminname().toStringUtf8() : queryCurrentAdminName())
                    + "\n??????????????????" + (currentAdminInfo != null ? currentAdminInfo.getComment().toStringUtf8() : "")
            ;
            read2file("??????????????????.txt", content);
        }
    }

    /**
     * ????????????????????????
     */
    private void notice2file() {
        if (noticeData.isEmpty()) {
            return;
        }
        String content = "";
        for (int i = 0; i < noticeData.size(); i++) {
            InterfaceBullet.pbui_Item_BulletDetailInfo item = noticeData.get(i);
            content += "?????????" + item.getTitle().toStringUtf8() + "\n" + "?????????" + item.getContent().toStringUtf8() + "\n\n";
        }
        read2file("??????????????????.txt", content);
    }

    /**
     * ?????????????????????????????????
     *
     * @param fileName ??????????????????????????????
     * @param content  ????????????
     */
    private void read2file(String fileName, String content) {
        try {
            File file = new File(Constant.DIR_ARCHIVE_TEMP + fileName);
            FileUtils.createOrExistsDir(Constant.DIR_ARCHIVE_TEMP);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fos));
            bufferedWriter.write(content);
            bufferedWriter.close();
            if ("??????????????????.txt".equals(fileName)) {
                archiveInforms.add(new ArchiveInform("??????????????????????????????", "100%"));
            } else if ("??????????????????.txt".equals(fileName)) {
                archiveInforms.add(new ArchiveInform("??????????????????????????????", "100%"));
            } else if ("??????????????????.txt".equals(fileName)) {
                archiveInforms.add(new ArchiveInform("??????????????????????????????", "100%"));
            }
            view.updateArchiveInform(archiveInforms);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
