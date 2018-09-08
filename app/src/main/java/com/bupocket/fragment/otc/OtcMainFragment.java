package com.bupocket.fragment.otc;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.alibaba.fastjson.JSON;
import com.bupocket.R;
import com.bupocket.adaptor.OTCAdDataAdapter;
import com.bupocket.base.BaseFragment;
import com.bupocket.fragment.BPAssetsTxDetailFragment;
import com.bupocket.model.AdInfo;
import com.bupocket.model.TokenTxInfo;

import java.util.ArrayList;
import java.util.List;

public class OtcMainFragment extends BaseFragment {
    private List<AdInfo> adInfos;
    private OTCAdDataAdapter otcAdDataAdapter;
    @BindView(R.id.otc_token_ad_item_lv)
    ListView otcAdDatasListView;
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_otc, null);
        ButterKnife.bind(this, root);
        loadOTCAdDatas();
        return root;
    }

    private void loadOTCAdDatas(){
        adInfos = new ArrayList<>();
        adInfos.add(new AdInfo("那时花开", "897", "99%", "100", "2000", "83,323",new String[]{"alipay","bank"}));
        adInfos.add(new AdInfo("捡拾", "8", "100%", "400", "5000", "23,234",new String[]{"bank"}));
        adInfos.add(new AdInfo("小刚卖布", "10", "100%", "100", "2000", "143,398",new String[]{"wechat","bank"}));
        adInfos.add(new AdInfo("武大郎卖布", "10020", "97%", "100", "780", "1,287,331",new String[]{"alipay","wechat","bank"}));
        adInfos.add(new AdInfo("沧海一家", "897", "100%", "20", "300", "3,323",new String[]{"wechat"}));
        adInfos.add(new AdInfo("阿木", "18292", "99%", "1000", "45000", "62,646,513",new String[]{"alipay"}));
        otcAdDataAdapter = new OTCAdDataAdapter(adInfos, getContext());
        otcAdDatasListView.setAdapter(otcAdDataAdapter);
        otcAdDatasListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AdInfo currentItem = (AdInfo) otcAdDataAdapter.getItem(position);
                Toast.makeText(getActivity(), JSON.toJSONString(currentItem), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
