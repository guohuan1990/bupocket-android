package com.bupocket.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

public class BPCreateWalletFragment extends BaseFragment {
    @BindView(R.id.create_wallet_btn)
    QMUIRoundButton mCreateWalletBtn;

    @BindView(R.id.toRecoverWalletBtn)
    QMUIRoundButton mToRecoverWalletBtn;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_create_wallet, null);
        ButterKnife.bind(this, root);
        QMUIStatusBarHelper.setStatusBarDarkMode(getBaseFragmentActivity());
        mCreateWalletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new BPUserTermsFragment());
            }
        });

        mToRecoverWalletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new BPRecoverWalletFormFragment());
            }
        });
        
        return root;
    }
    private long exitTime = 0;
    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getContext(), getResources().getText(R.string.next_key_down_err), Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            getActivity().finish();
        }

    }
}
