package com.bupocket.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bupocket.R;
import com.bupocket.adaptor.CardDetailAskAdapter;
import com.bupocket.adaptor.CardDetailMySellAdapter;
import com.bupocket.base.BaseFragment;
import com.bupocket.http.api.dto.resp.GetCardDetailsDto;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BPCardDetailsFragment extends BaseFragment {

    @BindView(R.id.issueOrganizationNameTv)
    TextView mIssueOrganizationNameTv;
    @BindView(R.id.cardNameTv)
    TextView mCardNameTv;
    @BindView(R.id.numberRemainingTv)
    TextView mNumberRemainingTv;
    @BindView(R.id.mySellLv)
    ListView mMySellLv;
    @BindView(R.id.purchasingInfoLv)
    ListView mPurchasingInfoLv;

    private GetCardDetailsDto cardDetailsDto;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_card_details, null);
        ButterKnife.bind(this, root);
        init();
        return root;
    }

    private void init() {
        initData();
        initUI();
    }

    private void initData() {
        getCardDetails();
    }

    private void getCardDetails() {
        String json = "{ \"assetInfo\":{ \"name\":\"牛肉代金券\", \"code\":\"RNC-1000\", \"issuerAddress\":\"buQZf3Uz8HzjCtZBBwK9ce9gkbj9G4Ew4grT\", \"issuerName\":\"现牛羊\", \"issuerLogo\":\"base64\", \"myAssetQty\":\"3\" }, \"mySale\":[ { \"adTitle\":\"如康牛腩块1kg生牛肉 整肉原切生鲜 生鲜 清真食品咖哩牛肉 牛腩肉\", \"price\":\"60\", \"saleTotal\":\"10\", \"selledAmount\":\"5\" },{ \"adTitle\":\"如康牛腩块1kg生牛肉 整肉原切生鲜 生鲜 清真食品咖哩牛肉 牛腩肉\", \"price\":\"60\", \"saleTotal\":\"10\", \"selledAmount\":\"5\" },{ \"adTitle\":\"如康牛腩块1kg生牛肉 整肉原切生鲜 生鲜 清真食品咖哩牛肉 牛腩肉\", \"price\":\"60\", \"saleTotal\":\"10\", \"selledAmount\":\"5\" } ], \"buyRequest\":[ { \"adTitle\":\"如康牛腩块1kg生牛肉 整肉原切生鲜 生鲜 清真食品咖哩牛肉 牛腩肉\", \"price\":\"60\", \"adId\":\"10000000\", \"issuer\":{ \"name\":\"现牛羊\", \"logo\":\"base64\" } } ] }";
        cardDetailsDto = JSON.parseObject(json,GetCardDetailsDto.class);

    }

    private void initUI() {
        String issueOrganizationName = cardDetailsDto.getAssetInfo().getIssuerName();
        String cardName = cardDetailsDto.getAssetInfo().getName();
        String numberRemaining = cardDetailsDto.getAssetInfo().getMyAssetQty();
        mIssueOrganizationNameTv.setText(issueOrganizationName);
        mCardNameTv.setText(cardName);
        mNumberRemainingTv.setText(getString(R.string.number_remaining_txt) + numberRemaining);

        CardDetailMySellAdapter cardMySellAdapter = new CardDetailMySellAdapter(cardDetailsDto.getMySale(), getContext());
        mMySellLv.setAdapter(cardMySellAdapter);
        CardDetailAskAdapter cardDetailAskAdapter = new CardDetailAskAdapter(cardDetailsDto.getBuyRequest(),getContext());
        mPurchasingInfoLv.setAdapter(cardDetailAskAdapter);
    }
}
