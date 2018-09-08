package com.bupocket.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bupocket.R;
import com.bupocket.adaptor.MyMnemonicCodeAdapter;
import com.bupocket.base.BaseFragment;
import com.bupocket.model.MnemonicCodeInfo;
import com.bupocket.utils.SecureRandomUtils;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import org.bitcoinj.crypto.MnemonicCode;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class BPCreateWalletStepOne extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.cw_n_tv1)
    TextView cwnTv1;
    @BindView(R.id.cw_n_tv2)
    TextView cwnTv2;
    @BindView(R.id.cw_n_tv3)
    TextView cwnTv3;
    @BindView(R.id.cw_n_tv4)
    TextView cwnTv4;
    @BindView(R.id.cw_n_tv5)
    TextView cwnTv5;
    @BindView(R.id.cw_n_tv6)
    TextView cwnTv6;
    @BindView(R.id.cw_n_tv7)
    TextView cwnTv7;
    @BindView(R.id.cw_n_tv8)
    TextView cwnTv8;
    @BindView(R.id.cw_n_tv9)
    TextView cwnTv9;
    @BindView(R.id.cw_n_tv10)
    TextView cwnTv10;
    @BindView(R.id.cw_n_tv11)
    TextView cwnTv11;
    @BindView(R.id.cw_n_tv12)
    TextView cwnTv12;
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_create_wallet_step_one, null);
        ButterKnife.bind(this, root);
        initTopBar();
        buildMnemonicCode();
        printMnemonicCode();
        return root;
    }



    private List<MnemonicCodeInfo> buildMnemonicCode(){
        List<MnemonicCodeInfo> listDatas = null;
        try {
            MnemonicCode mnemonicCode  = new MnemonicCode(getContext().getAssets().open("english.txt"), null);
            SecureRandom secureRandom = SecureRandomUtils.secureRandom();
            byte[] initialEntropy = new byte[16];
            secureRandom.nextBytes(initialEntropy);
            List<String> wds = mnemonicCode.toMnemonic(initialEntropy);

            listDatas = new ArrayList<>();
            MnemonicCodeInfo mCodeInfo = null;
            for (String code: wds
                 ) {
                mCodeInfo = new MnemonicCodeInfo();
                mCodeInfo.setCode(code);
                listDatas.add(mCodeInfo);
            }
            return listDatas;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listDatas;
    }


    private void printMnemonicCode() {
        List<MnemonicCodeInfo> mnemonicCodeInfos = buildMnemonicCode();
        cwnTv1.setText(mnemonicCodeInfos.get(0).getCode());
        cwnTv2.setText(mnemonicCodeInfos.get(1).getCode());
        cwnTv3.setText(mnemonicCodeInfos.get(2).getCode());
        cwnTv4.setText(mnemonicCodeInfos.get(3).getCode());
        cwnTv5.setText(mnemonicCodeInfos.get(4).getCode());
        cwnTv6.setText(mnemonicCodeInfos.get(5).getCode());
        cwnTv7.setText(mnemonicCodeInfos.get(6).getCode());
        cwnTv8.setText(mnemonicCodeInfos.get(7).getCode());
        cwnTv9.setText(mnemonicCodeInfos.get(8).getCode());
        cwnTv10.setText(mnemonicCodeInfos.get(9).getCode());
        cwnTv11.setText(mnemonicCodeInfos.get(10).getCode());
        cwnTv12.setText(mnemonicCodeInfos.get(11).getCode());

    }

//    private void showLongMessageDialog() {
//            new QMUIDialog.MessageDialogBuilder(getActivity())
//                    .setTitle("标题")
//                    .setMessage(buildMnemonicCode().toString())
//                    .addAction("取消", new QMUIDialogAction.ActionListener() {
//                        @Override
//                        public void onClick(QMUIDialog dialog, int index) {
//                            dialog.dismiss();
//                        }
//                    })
//                    .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();
//    }
    private void initTopBar() {
        mTopBar.setTitle(getResources().getString(R.string.create_wallet_txt));
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
    }

}
