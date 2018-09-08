package com.bupocket.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.fragment.otc.OtcMainFragment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BPDiscoveryFragment extends BaseFragment {

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.listview)
    ListView mListView;
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_discovery, null);
        ButterKnife.bind(this, root);
        initTopBar();
        initListView();
        return root;
    }
    private void initListView() {
        String[] listItems = new String[]{
                "创建钱包-生成助记词",
                "OTC"
        };
        List<String> data = new ArrayList<>();

        Collections.addAll(data, listItems);

        mListView.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.simple_list_item, data));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        startFragment(new BPCreateWalletStepOne());
                        break;
                    case 1:
                        startFragment(new OtcMainFragment());
                        break;
                }
            }
        });
    }
    private void initTopBar() {
        mTopBar.setTitle(getResources().getString(R.string.tabbar_discovery_txt));
    }

}
