package com.bupocket.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bupocket.R;
import com.bupocket.adaptor.CardAdDatasAdapter;
import com.bupocket.adaptor.CardMineAdapter;
import com.bupocket.base.BaseFragment;
import com.bupocket.enums.CardAdTypeEnum;
import com.bupocket.enums.ExceptionEnum;
import com.bupocket.http.api.AssetService;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.GetCardAdDatasRespDto;
import com.bupocket.http.api.dto.resp.GetCardMineDto;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private QMUIEmptyView mCardMineEmptyView;
    private SmartRefreshLayout mCardMineRefreshLayout;
    private ListView mCardMineLv;

    private View cardPackageBuyOrSell;
    private QMUIEmptyView mAdEmptyView;
    private SmartRefreshLayout mAdRefreshLayout;
    private ListView mCardAdDataLv;

    private String activeTab = "MINE";
    private Integer adType = CardAdTypeEnum.BUY.getCode();

    private GetCardAdDatasRespDto getCardAdDatasRespDto;
    private CardAdDatasAdapter cardAdDatasAdapter;
    private GetCardAdDatasRespDto.PageBean cardAdPage;
    private List<GetCardAdDatasRespDto.AdvertListBean> cardAdList;
    private Integer cardAdPageStart = 1;
    private String cardAdPageSize = "50";
    private boolean cardAdRefreshFlag = true;
    private boolean cardAdClickFlag = false;

    private GetCardMineDto getCardMineDto;
    private List<GetCardMineDto.MyAssetsBean> myAssetsList;
    private Integer myAssetsPageStart = 1;
    private String myAssetsPageSize = "50";
    private boolean myAssetsRefreshFlag = true;
    private boolean myAssetsClickFlag = false;

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
        mCardMineEmptyView = cardPackageMine.findViewById(R.id.emptyView);
        mCardMineRefreshLayout = cardPackageMine.findViewById(R.id.refreshLayout);
        mCardMineLv = cardPackageMine.findViewById(R.id.cardMineLv);

        cardPackageBuyOrSell = View.inflate(getContext(),R.layout.card_package_ad_layout,null);
        mAdEmptyView = cardPackageBuyOrSell.findViewById(R.id.emptyView);
        mAdRefreshLayout = cardPackageBuyOrSell.findViewById(R.id.refreshLayout);
        mCardAdDataLv = cardPackageBuyOrSell.findViewById(R.id.cardAdDataLv);
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
                if ("MINE".equals(activeTab)) {
                    return;
                }
                mCardContainerTabContentLl.removeAllViews();
                mCardContainerTabContentLl.addView(cardPackageMine);
                activeTab = "MINE";
                getMyCards();
                setActiveTab();
            }
        });

        mCardContainerBuyTabTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("BUY".equals(activeTab)) {
                    return;
                }
                mCardContainerTabContentLl.removeAllViews();
                mCardContainerTabContentLl.addView(cardPackageBuyOrSell);
                adType = CardAdTypeEnum.SELL.getCode();
                activeTab = "BUY";
                getCardBuyAd();
                setActiveTab();
            }
        });

        mCardContainerSellTabTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("SELL".equals(activeTab)) {
                    return;
                }
                mCardContainerTabContentLl.removeAllViews();
                mCardContainerTabContentLl.addView(cardPackageBuyOrSell);
                adType = CardAdTypeEnum.BUY.getCode();
                activeTab = "SELL";
                getCardSellAd();
                setActiveTab();
            }
        });
    }

    private void getMyCards() {
        String json = "{ \"myAssets\": [{ \"issuer\": { \"name\":\"现牛羊\", \"address\":\"buQZf3Uz8HzjCtZBBwK9ce9gkbj9G4Ew4grT\", \"logo\":\"base64\" }, \"assetInfo\": { \"name\":\"牛肉代金券\", \"code\":\"RNC-1000\", \"issuerAddress\":\"buQZf3Uz8HzjCtZBBwK9ce9gkbj9G4Ew4grT\", \"myAssetQty\":\"3\" } }], \"page\": { \"count\": 1, \"curSize\": 1, \"endOfGroup\": 1, \"firstResultNumber\": 0, \"nextFlag\": false, \"queryTotal\": true, \"size\": 10, \"start\": 1, \"startOfGroup\": 1, \"total\": 1 } }";
        getCardMineDto = JSON.parseObject(json,GetCardMineDto.class);
        myAssetsList = getCardMineDto.getMyAssets();
        loadMineCardsAdapter();
    }
    private void getMyCardDatas() {
        Map<String, Object> paramsMap = new HashMap<>();
//        paramsMap.put("userToken", sharedPreferencesHelper.getSharedPreference("userToken","").toString());
        AssetService assetService = RetrofitFactory.getInstance().getRetrofit().create(AssetService.class);
        retrofit2.Call<ApiResult<GetCardMineDto>> call = assetService.getMyCardMine(paramsMap);
        call.enqueue(new Callback<ApiResult<GetCardMineDto>>() {
            @Override
            public void onResponse(Call<ApiResult<GetCardMineDto>> call, Response<ApiResult<GetCardMineDto>> response) {
                ApiResult<GetCardMineDto> respDto = response.body();
                if (ExceptionEnum.SUCCESS.getCode().equals(respDto.getErrCode())) {
                    if (cardAdRefreshFlag) {
//                        cardAdList = respDto.getData().getMyAssets();
                    } else {
//                        cardAdList.addAll(respDto.getData().getMyAssets());
                    }
//                    cardAdPage = respDto.getData().getPage();
//                    loadAdDataAdapter();
                } else {
                    Toast.makeText(getContext(),getString(R.string.err_code_txt) +
                            respDto.getErrCode(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResult<GetCardMineDto>> call, Throwable t) {
                if (getActivity() != null) {
                    Toast.makeText(getContext(),getString(R.string.network_error_msg),Toast.LENGTH_LONG).show();
                }
                t.printStackTrace();
            }
        });
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
        cardAdPage = getCardAdDatasRespDto.getPage();
        loadAdDataAdapter();
    }

    private void getCardSellAd() {
        String json = "{\"advertList\":[{\"advertId\":\"10000021\",\"advertTitle\":\"阳澄湖牌大闸蟹礼券 2只装\",\"price\":\"100\",\"coin\":\"BU\",\"stockQuantity\":\"10\",\"issuer\":{\"name\":\"阳澄湖大闸蟹管理中心\",\"photo\":\"\"}}],\"page\":{\"count\":1,\"curSize\":2,\"endOfGroup\":1,\"firstResultNumber\":0,\"nextFlag\":false,\"queryTotal\":true,\"size\":10,\"start\":1,\"startOfGroup\":1,\"total\":2}}";
        getCardAdDatasRespDto = JSON.parseObject(json,GetCardAdDatasRespDto.class);
        cardAdList = getCardAdDatasRespDto.getAdvertList();
        cardAdPage = getCardAdDatasRespDto.getPage();
        loadAdDataAdapter();
    }

    private void getAdDatas() {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("advertType", adType);
        paramsMap.put("startPage", cardAdPageStart);
        paramsMap.put("pageSize", cardAdPageSize);
//        paramsMap.put("userToken", sharedPreferencesHelper.getSharedPreference("userToken","").toString());
        AssetService assetService = RetrofitFactory.getInstance().getRetrofit().create(AssetService.class);
        retrofit2.Call<ApiResult<GetCardAdDatasRespDto>> call = assetService.getCardAdDatas(paramsMap);
        call.enqueue(new Callback<ApiResult<GetCardAdDatasRespDto>>() {
            @Override
            public void onResponse(retrofit2.Call<ApiResult<GetCardAdDatasRespDto>> call, Response<ApiResult<GetCardAdDatasRespDto>> response) {
                ApiResult<GetCardAdDatasRespDto> respDto = response.body();
                if (ExceptionEnum.SUCCESS.getCode().equals(respDto.getErrCode())) {
                    if (cardAdRefreshFlag) {
                        cardAdList = respDto.getData().getAdvertList();
                    } else {
                        cardAdList.addAll(respDto.getData().getAdvertList());
                    }
                    cardAdPage = respDto.getData().getPage();
//                    loadAdDataAdapter();
                } else {
                    Toast.makeText(getContext(),getString(R.string.err_code_txt) +
                            respDto.getErrCode(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ApiResult<GetCardAdDatasRespDto>> call, Throwable t) {
                if (getActivity() != null) {
                    Toast.makeText(getContext(),getString(R.string.network_error_msg),Toast.LENGTH_LONG).show();
                }
                t.printStackTrace();
            }
        });
    }

    private void loadAdDataAdapter() {
        cardAdDatasAdapter = new CardAdDatasAdapter(cardAdList, getContext());
        cardAdDatasAdapter.setPage(cardAdPage);
        cardAdDatasAdapter.setAdType(adType);
        mCardAdDataLv.setAdapter(cardAdDatasAdapter);
    }

    private void loadAdDatasViews(){
        mAdRefreshLayout.setEnableLoadMore(true);
        mAdRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                refreshlayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshCardAdData();
                        mAdRefreshLayout.finishRefresh();
                        mAdRefreshLayout.setNoMoreData(false);
//                        initData();
                    }
                }, 500);

            }
        });
        mAdRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(final RefreshLayout refreshlayout) {
                mAdRefreshLayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(cardAdDatasAdapter == null){
                            mAdRefreshLayout.finishRefresh();
                            return;
                        }
                        mAdRefreshLayout.finishLoadMore(500);
                        loadMoreCardAdData();
                    }
                }, 500);
            }
        });
    }

    private void refreshCardAdData(){
        cardAdRefreshFlag = true;
        cardAdPageStart = 1;
//        adInfos.clear();
//        loadOTCAdDatas();
    }

    private void loadMoreCardAdData(){
        cardAdPageStart ++;
        cardAdRefreshFlag = false;
//        loadOTCAdDatas();
    }

    private void setActiveTab() {
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
