package com.bupocket.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bupocket.R;
import com.bupocket.adaptor.MyTokenTxAdapter;
import com.bupocket.base.BaseFragment;
import com.bupocket.model.TokenTxInfo;

import java.util.ArrayList;
import java.util.List;

public class BPAssetsFragment extends BaseFragment {
    @BindView(R.id.user_nick)
    TextView mUserNickTextView;
    @BindView(R.id.user_bc_address)
    TextView mUserBcAddressTextView;

    private List<TokenTxInfo> tokenTxInfoList;
    private MyTokenTxAdapter myTokenTxAdapter;
    @BindView(R.id.my_token_tx_lv)
    ListView tokenTxsListView;
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_assets, null);
        ButterKnife.bind(this, root);
        mUserNickTextView.setText("阿木");
        mUserBcAddressTextView.setText("buQevL***WPng8P");

        initAssetsGroupListView();
        return root;
    }
    private void initAssetsGroupListView(){
        tokenTxInfoList = new ArrayList<>();
        tokenTxInfoList.add(new TokenTxInfo("buQYaQ***HkKH5Z", "1分前","+893,325.93828","成功"));
        tokenTxInfoList.add(new TokenTxInfo("buQVsU***KDf2gL", "3天前","-1000","成功"));
        tokenTxInfoList.add(new TokenTxInfo("buQhCP***KXRaYD", "28/08/2018","+0.0093","成功"));
        tokenTxInfoList.add(new TokenTxInfo("buQYaQ***HkKH5Z", "17/08/2018","+800","成功"));
        tokenTxInfoList.add(new TokenTxInfo("buQVsU***KDf2gL", "14/08/2018","+98,382","成功"));
        tokenTxInfoList.add(new TokenTxInfo("buQYaQ***HkKH5Z", "09/08/2018","+30","成功"));
        tokenTxInfoList.add(new TokenTxInfo("buQYaQ***HkKH5Z", "02/07/2018","+30","成功"));
        tokenTxInfoList.add(new TokenTxInfo("buQYaQ***HkKH5Z", "28/07/2018","+56","成功"));
        tokenTxInfoList.add(new TokenTxInfo("buQi27***Scb8oG", "23/07/2018","+24","成功"));
        tokenTxInfoList.add(new TokenTxInfo("buQYaQ***HkKH5Z", "13/07/2018","+190","成功"));
        tokenTxInfoList.add(new TokenTxInfo("buQYaQ***HkKH5Z", "10/07/2018","+1","成功"));
        tokenTxInfoList.add(new TokenTxInfo("buQYaQ***HkKH5Z", "03/07/2018","+5","成功"));

        myTokenTxAdapter = new MyTokenTxAdapter(tokenTxInfoList, getContext());
        tokenTxsListView.setAdapter(myTokenTxAdapter);
        tokenTxsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TokenTxInfo currentItem = (TokenTxInfo) myTokenTxAdapter.getItem(position);
                Toast.makeText(getActivity(), currentItem.getTxAmount() + " is Clicked", Toast.LENGTH_SHORT).show();
                startFragment(new BPAssetsTxDetailFragment());
            }
        });
    }
}
