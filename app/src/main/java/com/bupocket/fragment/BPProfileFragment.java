package com.bupocket.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.utils.AddressUtil;
import com.bupocket.utils.QRCodeUtil;
import com.bupocket.utils.SharedPreferencesHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BPProfileFragment extends BaseFragment{
    private SharedPreferencesHelper sharedPreferencesHelper;
    private String currentAccAddress;
    private String currentAccNick;

    @BindView(R.id.showMyAddressLv)
    LinearLayout mShowMyaddressL;
    @BindString(R.string.qr_copy_success_message)
    String copySuccessMessage;
    @BindView(R.id.userNick)
    TextView userNickTx;
    @BindView(R.id.userBcAddress)
    TextView addressTv;
    @BindView(R.id.changePwdItem)
    RelativeLayout mChangePwdItem;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_profile, null);
        ButterKnife.bind(this, root);
        initData(root);
        showMyAddress(currentAccAddress);
        mChangePwdItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoChangePwdFragment();
            }
        });
        return root;
    }

    private void initData(View view){
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        currentAccNick = sharedPreferencesHelper.getSharedPreference("currentAccNick", "").toString();
        currentAccAddress = sharedPreferencesHelper.getSharedPreference("currentAccAddr", "").toString();

        userNickTx.setText(currentAccNick);

        String hideCurrentAccAddress = AddressUtil.anonymous(currentAccAddress);
        addressTv.setText(hideCurrentAccAddress);
    }

    private void showMyAddress(final String currentAccAddress) {
        mShowMyaddressL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final QMUIBottomSheet sheet = new QMUIBottomSheet(getContext());
                sheet.setContentView(R.layout.show_address_layout);


                TextView addressTxt = sheet.findViewById(R.id.printAccAddressTv);
                addressTxt.setText(currentAccAddress);

                Bitmap mBitmap = QRCodeUtil.createQRCodeBitmap(currentAccAddress, 356, 356);
                ImageView mImageView = sheet.findViewById(R.id.qr_pocket_address_image);
                mImageView.setImageBitmap(mBitmap);

                sheet.findViewById(R.id.addressCopyBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData mClipData = ClipData.newPlainText("Label", currentAccAddress);
                        cm.setPrimaryClip(mClipData);
                        final QMUITipDialog copySuccessDiglog = new QMUITipDialog.Builder(getContext())
                                .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                                .setTipWord(copySuccessMessage)
                                .create();
                        copySuccessDiglog.show();
                        getView().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                copySuccessDiglog.dismiss();
                            }
                        }, 1500);
                    }
                });

                sheet.findViewById(R.id.closeBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sheet.dismiss();
                    }
                });
                sheet.show();

            }
        });
    }

    private void gotoChangePwdFragment(){
        startFragment(new BPChangePwdFragment());
    }
}
