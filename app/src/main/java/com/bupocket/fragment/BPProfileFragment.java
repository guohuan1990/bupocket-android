package com.bupocket.fragment;

import android.view.LayoutInflater;
import android.view.View;
import butterknife.ButterKnife;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;

public class BPProfileFragment extends BaseFragment {
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_profile, null);
        ButterKnife.bind(this, root);
        return root;
    }

}
