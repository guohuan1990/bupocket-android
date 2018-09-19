package com.bupocket.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.QMUIWindowInsetLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BPUserInfoFragment extends BaseFragment {

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;

    @BindView(R.id.userInfoBackupWalletTv)
    TextView mUserInfoBackupWalletTv;

    @BindView(R.id.userInfoLogoutWalletTv)
    TextView mUserInfoLogoutWalletTv;

    @BindView(R.id.userInfoAccNameTv)
    TextView mUserInfoAccNameTv;

    @BindView(R.id.userInfoAccAddressTv)
    TextView mUserInfoAccAddressTv;

    @BindView(R.id.testLayout)
    QMUIWindowInsetLayout mTestLayout;

    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_user_info, null);
        ButterKnife.bind(this, root);
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        initTopBar();
        initData();
        eventListeners();
        return root;
    }

    private void initData() {
        String accAddress = getArguments().getString("accAddress");
        String accName = getArguments().getString("accName");
        mUserInfoAccNameTv.setText(accName);
        mUserInfoAccAddressTv.setText(accAddress);
    }

    private void eventListeners() {
        mUserInfoBackupWalletTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startFragment(new BPBackupWalletFragment());
            }
        });

        mUserInfoLogoutWalletTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "点击退出身份",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initTopBar() {
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QMUIStatusBarHelper.setStatusBarDarkMode(getBaseFragmentActivity());
                popBackStack();
            }
        });
        mTopBar.setTitle(R.string.user_info_title);
    }

    private void showMessagePositiveDialog() {
        new QMUIDialog.MessageDialogBuilder(getActivity())
                .setTitle("标题")
                .setMessage("确定要发送吗？")
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        Toast.makeText(getActivity(), "发送成功", Toast.LENGTH_SHORT).show();
                    }
                })
                .create(mCurrentDialogStyle).show();
    }
}
