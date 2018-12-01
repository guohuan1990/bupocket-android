package com.bupocket.fragment;

import android.view.LayoutInflater;
import android.view.View;

import com.bupocket.R;
import com.bupocket.base.BaseFragment;

import butterknife.ButterKnife;

public class BPCardDetailsFragment extends BaseFragment {
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_card_details, null);
        ButterKnife.bind(this, root);
        init();
        return root;
    }

    private void init() {

    }
}
