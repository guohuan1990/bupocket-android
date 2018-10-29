package com.bupocket.fragment;

import android.view.LayoutInflater;
import android.view.View;

import com.bupocket.BPApplication;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;


import butterknife.BindView;
import butterknife.ButterKnife;

public class BPRegisterTokenStatusFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;

    private io.socket.client.Socket mSocket;
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_register_token_status, null);
        ButterKnife.bind(this, root);
        initTopbar();
        BPApplication application = (BPApplication)getActivity().getApplication();
        mSocket = application.getSocket();
        mSocket.emit("test2","issue success");
        return root;
    }

    private void initTopbar() {
        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
    }
}
