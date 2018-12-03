package com.bupocket.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bupocket.R;
import com.bupocket.adaptor.CardAdDatasAdapter;
import com.bupocket.adaptor.CardMineAdapter;
import com.bupocket.base.BaseFragment;
import com.bupocket.http.api.dto.resp.GetCardAdDatasRespDto;
import com.bupocket.http.api.dto.resp.GetCardMineDto;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BPCardPackageFragment extends BaseFragment {

    @BindView(R.id.cardContainerMineCardTabTv)
    TextView mCardContainerMineCardTabTv;
    @BindView(R.id.cardContainerBuyTabTv)
    TextView mCardContainerBuyTabTv;
    @BindView(R.id.cardContainerSellTabTv)
    TextView mCardContainerSellTabTv;

    @BindView(R.id.cardContainerTabContentLl)
    LinearLayout mCardContainerTabContentLl;

    private View cardPackageMine;
    private View cardPackageBuyOrSell;
    private QMUIEmptyView mAdEmptyView;
    private SmartRefreshLayout mAdRefreshLayout;
    private ListView mCardAdDataLv;
    private ListView mCardMineLv;

    private Integer adType = 0;
    private GetCardAdDatasRespDto getCardAdDatasRespDto;
    private CardAdDatasAdapter cardAdDatasAdapter;
    private GetCardAdDatasRespDto.PageBean cardPage;
    private List<GetCardAdDatasRespDto.AdvertListBean> cardAdList;
    private GetCardMineDto getCardMineDto;
    private List<GetCardMineDto.MyAssetsBean> myAssetsList;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_card_package, null);
        ButterKnife.bind(this, root);
        init();
        return root;
    }

    private void init() {
        initTabsPages();
        setListeners();
    }

    private void initTabsPages() {

        cardPackageMine = View.inflate(getContext(),R.layout.card_package_mine_layout,null);
        cardPackageBuyOrSell = View.inflate(getContext(),R.layout.card_package_ad_layout,null);
//        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) cardPackageBuyOrSell.getLayoutParams();
//        linearParams.height = 100%;
//        cardPackageBuyOrSell.setLayoutParams(linearParams);
        mAdEmptyView = cardPackageBuyOrSell.findViewById(R.id.emptyView);
        mAdRefreshLayout = cardPackageBuyOrSell.findViewById(R.id.refreshLayout);
        mCardAdDataLv = cardPackageBuyOrSell.findViewById(R.id.cardAdDataLv);
        mCardMineLv = cardPackageMine.findViewById(R.id.cardMineLv);
    }

    private void setListeners() {
        selectTabs();
    }

    private void selectTabs() {
        mCardContainerTabContentLl.addView(cardPackageMine);
        getMyCards();
        mCardContainerMineCardTabTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCardContainerTabContentLl.removeAllViews();
                mCardContainerTabContentLl.addView(cardPackageMine);
                getMyCards();
                setActiveTab("MINE");
            }
        });

        mCardContainerBuyTabTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCardContainerTabContentLl.removeAllViews();
                mCardContainerTabContentLl.addView(cardPackageBuyOrSell);
                adType = 1;
                getCardBuyAd();
                setActiveTab("BUY");
            }
        });

        mCardContainerSellTabTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCardContainerTabContentLl.removeAllViews();
                mCardContainerTabContentLl.addView(cardPackageBuyOrSell);
                adType = 0;
                getCardSellAd();
                setActiveTab("SELL");
            }
        });
    }

    private void getMyCards() {
        String json = "{ \"myAssets\": [{ \"issuer\": { \"name\":\"现牛羊\", \"address\":\"buQZf3Uz8HzjCtZBBwK9ce9gkbj9G4Ew4grT\", \"logo\":\"base64\" }, \"assetInfo\": { \"name\":\"牛肉代金券\", \"code\":\"RNC-1000\", \"issuerAddress\":\"buQZf3Uz8HzjCtZBBwK9ce9gkbj9G4Ew4grT\", \"myAssetQty\":\"3\" } }], \"page\": { \"count\": 1, \"curSize\": 1, \"endOfGroup\": 1, \"firstResultNumber\": 0, \"nextFlag\": false, \"queryTotal\": true, \"size\": 10, \"start\": 1, \"startOfGroup\": 1, \"total\": 1 } }";
        getCardMineDto = JSON.parseObject(json,GetCardMineDto.class);
        myAssetsList = getCardMineDto.getMyAssets();
        loadMineCardsAdapter();
    }

    private void loadMineCardsAdapter() {
        CardMineAdapter cardMineAdapter = new CardMineAdapter(myAssetsList,getContext());
        cardMineAdapter.setPage(getCardMineDto.getPage());
        mCardMineLv.setAdapter(cardMineAdapter);
    }

    private void getCardBuyAd() {
        String json = "{\"advertList\":[{\"advertId\":\"10000020\",\"advertTitle\":\"阳澄湖牌大闸蟹礼券 8只装\",\"price\":\"2\",\"coin\":\"BU\",\"stockQuantity\":\"5\",\"issuer\":{\"name\":\"现牛羊\",\"photo\":\"base64\"}}],\"page\":{\"count\":1,\"curSize\":2,\"endOfGroup\":1,\"firstResultNumber\":0,\"nextFlag\":false,\"queryTotal\":true,\"size\":10,\"start\":1,\"startOfGroup\":1,\"total\":2}}";
        getCardAdDatasRespDto = JSON.parseObject(json,GetCardAdDatasRespDto.class);
        cardAdList = getCardAdDatasRespDto.getAdvertList();
        cardPage = getCardAdDatasRespDto.getPage();
        loadAdDataAdapter();
    }

    private void getCardSellAd() {
        String json = "{\"advertList\":[{\"advertId\":\"10000021\",\"advertTitle\":\"阳澄湖牌大闸蟹礼券 2只装\",\"price\":\"100\",\"coin\":\"BU\",\"stockQuantity\":\"10\",\"issuer\":{\"name\":\"阳澄湖大闸蟹管理中心\",\"photo\":\"\"}}],\"page\":{\"count\":1,\"curSize\":2,\"endOfGroup\":1,\"firstResultNumber\":0,\"nextFlag\":false,\"queryTotal\":true,\"size\":10,\"start\":1,\"startOfGroup\":1,\"total\":2}}";
        getCardAdDatasRespDto = JSON.parseObject(json,GetCardAdDatasRespDto.class);
        cardAdList = getCardAdDatasRespDto.getAdvertList();
        cardPage = getCardAdDatasRespDto.getPage();
        loadAdDataAdapter();
    }

    private void loadAdDataAdapter() {
        cardAdDatasAdapter = new CardAdDatasAdapter(cardAdList, getContext());
        cardAdDatasAdapter.setPage(cardPage);
        cardAdDatasAdapter.setAdType(adType);
        mCardAdDataLv.setAdapter(cardAdDatasAdapter);
    }

    private void setActiveTab(String activeTab) {
        mCardContainerMineCardTabTv.setTextColor(getResources().getColor(R.color.app_color_green));
        mCardContainerBuyTabTv.setTextColor(getResources().getColor(R.color.app_color_green));
        mCardContainerSellTabTv.setTextColor(getResources().getColor(R.color.app_color_green));

        mCardContainerMineCardTabTv.setBackgroundColor(getResources().getColor(R.color.app_color_white));
        mCardContainerBuyTabTv.setBackgroundColor(getResources().getColor(R.color.app_color_white));
        mCardContainerSellTabTv.setBackgroundColor(getResources().getColor(R.color.app_color_white));
        if ("MINE".equals(activeTab)) {
            mCardContainerMineCardTabTv.setTextColor(getResources().getColor(R.color.app_color_white));
            mCardContainerMineCardTabTv.setBackgroundColor(getResources().getColor(R.color.app_color_green));
        } else if ("BUY".equals(activeTab)) {
            mCardContainerBuyTabTv.setTextColor(getResources().getColor(R.color.app_color_white));
            mCardContainerBuyTabTv.setBackgroundColor(getResources().getColor(R.color.app_color_green));
        } else if ("SELL".equals(activeTab)) {
            mCardContainerSellTabTv.setTextColor(getResources().getColor(R.color.app_color_white));
            mCardContainerSellTabTv.setBackgroundColor(getResources().getColor(R.color.app_color_green));
        }
    }
}
