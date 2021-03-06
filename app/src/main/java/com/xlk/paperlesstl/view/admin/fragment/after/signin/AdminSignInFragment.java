package com.xlk.paperlesstl.view.admin.fragment.after.signin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.google.protobuf.ByteString;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import com.mogujie.tt.protobuf.InterfaceMeet;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.mogujie.tt.protobuf.InterfaceSignin;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xlk.paperlesstl.App;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.model.Constant;
import com.xlk.paperlesstl.model.data.EventMessage;
import com.xlk.paperlesstl.model.data.EventType;
import com.xlk.paperlesstl.util.ConvertUtil;
import com.xlk.paperlesstl.util.DateUtil;
import com.xlk.paperlesstl.view.admin.BaseFragment;
import com.xlk.paperlesstl.view.admin.fragment.after.archive.PdfSignBean;

import org.greenrobot.eventbus.EventBus;

/**
 * @author Created by xlk on 2020/10/26.
 * @desc
 */
public class AdminSignInFragment extends BaseFragment implements AdminSignInInterface, View.OnClickListener {

    private AdminSignInPresenter presenter;
    private AdminSignInAdapter signInAdapter;
    private RecyclerView rv_signIn;
    private TextView tv_yd;
    private TextView tv_yqd;
    private TextView tv_wqd;
    private Button btn_delete;
    private Button btn_export_pdf;
    private boolean isFromMeet;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.admin_fragment_signin, container, false);
        initView(inflate);
        isFromMeet();
        presenter = new AdminSignInPresenter(this);
        presenter.queryAttendPeople();
        return inflate;
    }

    private void isFromMeet() {
        isFromMeet = getArguments().getBoolean("isFromMeet");
        btn_export_pdf.setText(isFromMeet ? getString(R.string.back) : getString(R.string.export_pdf));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    protected void reShow() {
        isFromMeet();
        presenter.queryAttendPeople();
    }


    public void initView(View rootView) {
        this.rv_signIn = (RecyclerView) rootView.findViewById(R.id.rv_signIn);
        this.tv_yd = (TextView) rootView.findViewById(R.id.tv_yd);
        this.tv_yqd = (TextView) rootView.findViewById(R.id.tv_yqd);
        this.tv_wqd = (TextView) rootView.findViewById(R.id.tv_wqd);
        this.btn_delete = (Button) rootView.findViewById(R.id.btn_delete);
        this.btn_export_pdf = (Button) rootView.findViewById(R.id.btn_export_pdf);
        btn_delete.setOnClickListener(this);
        btn_export_pdf.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_delete:
                if (signInAdapter == null || signInAdapter.getChecks().isEmpty()) {
                    ToastUtils.showShort(R.string.please_choose_member);
                    return;
                }
                List<Integer> checks = signInAdapter.getChecks();
                presenter.deleteSignIn(checks);
                break;
            case R.id.btn_export_pdf:
                if (isFromMeet) {
                    EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_SIGN_IN).build());
                } else {
                    exportPdf(presenter.getPdfData());
                }
                break;
            default:
                break;
        }
    }

    /**
     * ????????????????????????PDF???????????????
     *
     * @param pdfSignBean ???????????????????????????
     */
    private void exportPdf(PdfSignBean pdfSignBean) {
        App.threadPool.execute(() -> {
            try {
                long l = System.currentTimeMillis();
                InterfaceMeet.pbui_Item_MeetMeetInfo meetInfo = pdfSignBean.getMeetInfo();
                InterfaceRoom.pbui_Item_MeetRoomDetailInfo roomInfo = pdfSignBean.getRoomInfo();
                List<SignInBean> signInBeans = pdfSignBean.getSignInBeans();
                final int size = signInBeans.size();
                LogUtils.i(TAG, "exportPdf signInBeans.size=" + size);
                int signInCount = pdfSignBean.getSignInCount();
                if (meetInfo == null || roomInfo == null) {
                    return;
                }
                FileUtils.createOrExistsDir(Constant.EXPORT_DIR);
//                File file = new File(Constant.DIR_EXPORT + "????????????.pdf");
//                if (file.exists()) {
//                    boolean delete = file.delete();
//                    LogUtils.i(TAG, "exportPdf ??????????????????=" + delete);
//                }

                Document document = new Document(PageSize.A4);
                PdfWriter.getInstance(document, new FileOutputStream(Constant.EXPORT_DIR + "????????????.pdf"));
                document.open();
                BaseFont bfChinese = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
                Font boldFont14 = new Font(bfChinese, 14, Font.BOLD);
                Font boldFont16 = new Font(bfChinese, 16, Font.BOLD);

                String top = "???????????????" + meetInfo.getName().toStringUtf8()
                        + "\n?????????" + meetInfo.getRoomname().toStringUtf8() + "  ???????????????" + roomInfo.getAddr().toStringUtf8()
                        + "\n???????????????" + (meetInfo.getSecrecy() == 1 ? "???" : "???")
                        + "\n???????????????" + DateUtil.millisecondFormatDetailedTime(meetInfo.getStartTime() * 1000)
                        + "  ???????????????" + DateUtil.millisecondFormatDetailedTime(meetInfo.getEndTime() * 1000)
                        + "\n?????????" + size + "???  ????????????" + signInCount + "???  ????????????" + (size - signInCount) + "???";
                Paragraph title = new Paragraph(top, boldFont16);
                //?????????????????? 0?????? 1????????? 2?????????
                title.setAlignment(1);
                //?????????????????????
                title.setSpacingBefore(5f);
                //?????????????????????
                title.setSpacingAfter(10f);
                document.add(title);

                // ????????????
                Paragraph dottedLine = new Paragraph();
                dottedLine.add(new Chunk(new DottedLineSeparator()));
                //?????????????????????
                dottedLine.setSpacingBefore(10f);
                //?????????????????????
                dottedLine.setSpacingAfter(10f);
                document.add(dottedLine);

                //?????????????????????????????????????????????????????????????????????????????????
                PdfPTable pdfPTable = new PdfPTable(2);
                //???????????????????????????100%
                pdfPTable.setWidthPercentage(100);
                //??????????????????????????????
                pdfPTable.getDefaultCell().setBorder(0);
                //????????????????????????
                final float cellHeight = 100f;
                for (int i = 0; i < size; i++) {
                    SignInBean item = signInBeans.get(i);
                    InterfaceMember.pbui_Item_MemberDetailInfo member = item.getMember();
                    InterfaceSignin.pbui_Item_MeetSignInDetailInfo sign = item.getSign();
                    boolean isNoSign = sign == null;
                    LogUtils.i(TAG, "exportPdf isNoSign=" + isNoSign);
                    String content = "????????????" + member.getName().toStringUtf8()
                            + "\n???????????????" + (isNoSign ? "" : DateUtil.millisecondFormatDetailedTime(sign.getUtcseconds() * 1000))
                            + "\n???????????????" + (isNoSign ? "?????????" : "?????????")
                            + "\n???????????????" + (isNoSign ? "" : Constant.getMeetSignInTypeName(sign.getSigninType()));
                    Paragraph paragraph = new Paragraph(content, boldFont14);
                    PdfPCell cell_1 = new PdfPCell(paragraph);
                    //??????????????????
                    cell_1.setFixedHeight(cellHeight);
                    //??????????????????
                    cell_1.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    pdfPTable.addCell(cell_1);

                    if (!isNoSign) {
                        byte[] bytes = sign.getPsigndata().toByteArray();
                        if (bytes != null && bytes.length > 0) {
                            PdfPCell cell_2 = new PdfPCell();
                            //??????????????????
                            cell_2.setFixedHeight(cellHeight);
                            Image image = Image.getInstance(bytes);
                            image.scaleAbsolute(100, 50);
                            cell_2.setImage(image);
                            pdfPTable.addCell(cell_2);
                            LogUtils.i(TAG, "exportPdf ?????????????????????");
                            continue;
                        }
                    }
                    LogUtils.i(TAG, "exportPdf ??????????????????????????????????????????");
                    //?????????????????????????????????????????????
                    Paragraph a = new Paragraph("");
                    PdfPCell cell_2 = new PdfPCell(a);
                    //??????????????????
                    cell_2.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    //??????????????????
                    cell_2.setHorizontalAlignment(Element.ALIGN_CENTER);
                    //??????????????????
                    cell_2.setFixedHeight(cellHeight);
                    pdfPTable.addCell(cell_2);
                }
                document.add(pdfPTable);
                document.close();
                LogUtils.i(TAG, "exportPdf ??????=" + (System.currentTimeMillis() - l));
                showToast();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void showToast() {
        getActivity().runOnUiThread(() -> ToastUtils.showShort(R.string.export_pdf_successful));
    }

    @Override
    public void update(List<SignInBean> signInBeans, int signInCount) {
        LogUtils.i(TAG, "update signInBeans.size=" + signInBeans.size() + ", signInCount=" + signInCount);
        if (signInAdapter == null) {
            signInAdapter = new AdminSignInAdapter(R.layout.item_admin_signin, signInBeans);
            rv_signIn.setLayoutManager(new LinearLayoutManager(getContext()));
            rv_signIn.setAdapter(signInAdapter);
            signInAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    signInAdapter.setSelected(signInBeans.get(position).getMember().getPersonid());
                }
            });
            signInAdapter.addChildClickViewIds(R.id.item_view_5);
            signInAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
                @Override
                public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                    if (view.getId() == R.id.item_view_5) {
                        LogUtils.i(TAG, "onItemChildClick position=" + position);
                        SignInBean bean = signInBeans.get(position);
                        ByteString picdata = bean.getSign().getPsigndata();
                        showPicDialog(picdata);
                    }
                }
            });
        } else {
            signInAdapter.notifyDataSetChanged();
        }
        tv_yd.setText(getString(R.string.yd_, String.valueOf(signInBeans.size())));
        tv_yqd.setText(getString(R.string.yqd_, String.valueOf(signInCount)));
        tv_wqd.setText(getString(R.string.wqd_, String.valueOf(signInBeans.size() - signInCount)));
    }

    private void showPicDialog(ByteString picdata) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.dialog_show_picture, null);
        ImageView iv = inflate.findViewById(R.id.iv_show_pic);
        iv.setImageBitmap(ConvertUtil.bs2bmp(picdata));
        builder.setView(inflate);
        builder.create().show();
    }
}
