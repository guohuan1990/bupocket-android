package com.bupocket.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bupocket.R;
import com.bupocket.adaptor.CardAdDatasAdapter;
import com.bupocket.adaptor.CardMyAssetsAdapter;
import com.bupocket.base.BaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.enums.CardAdTypeEnum;
import com.bupocket.enums.ExceptionEnum;
import com.bupocket.http.api.AssetService;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.GetCardAdDataRespDto;
import com.bupocket.http.api.dto.resp.GetCardMyAssetsRespDto;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.DecimalCalculate;
import com.bupocket.utils.SharedPreferencesHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
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

    private String activeTab = "MINE";
    private Integer adType = CardAdTypeEnum.BUY.getCode();
    private SharedPreferencesHelper sharedPreferencesHelper;
    private String totalQuantityTxt = "";
    private Integer buyOrSellQuantity;
    private Integer stockQuantity;
    private double totalAmount;
    private boolean subFlag;
    private boolean addFlag;

    private View cardPackageMine;
    private QMUIEmptyView mCardMyAssetsEmptyView;
    private SmartRefreshLayout mCardMyAssetsRefreshLayout;
    private ListView mCardMyAssetsLv;
    private LinearLayout mCardMyAssetsEmptyLl;
    private LinearLayout mCardMyAssetsBuyRequestLl;

    private GetCardMyAssetsRespDto getCardMyAssetsRespDto;
    private CardMyAssetsAdapter cardMyAssetsAdapter;
    private GetCardMyAssetsRespDto.PageBean myAssetsPage;
    private List<GetCardMyAssetsRespDto.MyAssetsBean> myAssetsList;
    private Integer myAssetsPageStart = 1;
    private String myAssetsPageSize = "50";
    private boolean myAssetsRefreshFlag = true;


    private View cardPackageBuyOrSell;
    private QMUIEmptyView mAdEmptyView;
    private SmartRefreshLayout mAdRefreshLayout;
    private ListView mCardAdDataLv;
    private LinearLayout mCardAdListEmptyLl;

    private GetCardAdDataRespDto getCardAdDataRespDto;
    private CardAdDatasAdapter cardAdDatasAdapter;
    private GetCardAdDataRespDto.PageBean cardAdPage;
    private List<GetCardAdDataRespDto.AdvertListBean> cardAdList;
    private Integer cardAdPageStart = 1;
    private String cardAdPageSize = "50";
    private boolean cardAdRefreshFlag = true;
    private boolean cardAdClickFlag = false;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_card_package, null);
        ButterKnife.bind(this, root);
        init();
        return root;
    }

    private void init() {
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buBox");
        setListeners();
    }

    private void addTabsPages() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        if ("MINE".equals(activeTab)) {
            cardPackageMine = inflater.inflate(R.layout.card_package_mine_layout, mCardContainerTabContentLl,true);
            mCardMyAssetsEmptyView = cardPackageMine.findViewById(R.id.emptyView);
            mCardMyAssetsRefreshLayout = cardPackageMine.findViewById(R.id.refreshLayout);
            mCardMyAssetsLv = cardPackageMine.findViewById(R.id.cardMineLv);
            mCardMyAssetsEmptyLl = cardPackageMine.findViewById(R.id.cardMyAssetsListEmptyLl);
            mCardMyAssetsBuyRequestLl = cardPackageMine.findViewById(R.id.cardMyAssetsBuyRequestLl);
            mCardMyAssetsBuyRequestLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startFragment(new BPBuyRequestFragment());
                }
            });
        } else if ("BUY".equals(activeTab) || "SELL".equals(activeTab)) {
            cardPackageBuyOrSell = inflater.inflate(R.layout.card_package_ad_layout, mCardContainerTabContentLl,true);
            mAdEmptyView = cardPackageBuyOrSell.findViewById(R.id.emptyView);
            mAdRefreshLayout = cardPackageBuyOrSell.findViewById(R.id.refreshLayout);
            mCardAdDataLv = cardPackageBuyOrSell.findViewById(R.id.cardAdDataLv);
            mCardAdListEmptyLl = cardPackageBuyOrSell.findViewById(R.id.cardAdListEmptyLl);
            loadAdDatasViews();
        }
    }

    private void setListeners() {
        selectTabs();
    }

    private void selectTabs() {
        addTabsPages();
        getMyCards();
        mCardContainerMineCardTabTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("MINE".equals(activeTab)) {
                    return;
                }
                mCardContainerTabContentLl.removeAllViews();
                activeTab = "MINE";
                addTabsPages();
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
                adType = CardAdTypeEnum.SELL.getCode();
                activeTab = "BUY";
                addTabsPages();
                getCardBuyAd();
//                getAdDatas();
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
                adType = CardAdTypeEnum.BUY.getCode();
                activeTab = "SELL";
                addTabsPages();
                getCardSellAd();
//                getAdDatas();
                setActiveTab();
            }
        });
    }

    // My assets part start  ++++++++
    private void getMyCards() {
        String json = "{ \"myAssets\": [{ \"issuer\": { \"name\":\"现牛羊\", \"address\":\"buQZf3Uz8HzjCtZBBwK9ce9gkbj9G4Ew4grT\", \"logo\":\"base64\" }, \"assetInfo\": { \"name\":\"牛肉代金券\", \"code\":\"RNC-1000\", \"issuerAddress\":\"buQZf3Uz8HzjCtZBBwK9ce9gkbj9G4Ew4grT\", \"myAssetQty\":\"3\" } }], \"page\": { \"count\": 1, \"curSize\": 1, \"endOfGroup\": 1, \"firstResultNumber\": 0, \"nextFlag\": false, \"queryTotal\": true, \"size\": 10, \"start\": 1, \"startOfGroup\": 1, \"total\": 1 } }";
//        String json = "{ \"myAssets\": [], \"page\": { \"count\": 1, \"curSize\": 1, \"endOfGroup\": 1, \"firstResultNumber\": 0, \"nextFlag\": false, \"queryTotal\": true, \"size\": 10, \"start\": 1, \"startOfGroup\": 1, \"total\": 1 } }";
        getCardMyAssetsRespDto = JSON.parseObject(json,GetCardMyAssetsRespDto.class);
        myAssetsList = getCardMyAssetsRespDto.getMyAssets();
        if(myAssetsList.size() > 0) {
            loadCardMyAssetsAdapter();
        } else {
            showOrHideEmptyPage(true);
        }
        myAssetsPage = getCardMyAssetsRespDto.getPage();
        if (myAssetsPage.isNextFlag()) {
            mCardMyAssetsRefreshLayout.setEnableLoadMore(true);
        } else {
            mCardMyAssetsRefreshLayout.setEnableLoadMore(false);
        }
    }
    private void getMyCardAssetsDatas() {
        mAdEmptyView.show(true);
        Map<String, Object> paramsMap = new HashMap<>();
//        paramsMap.put("userToken", sharedPreferencesHelper.getSharedPreference("userToken","").toString());
        AssetService assetService = RetrofitFactory.getInstance().getRetrofit().create(AssetService.class);
        retrofit2.Call<ApiResult<GetCardMyAssetsRespDto>> call = assetService.getMyCardMine(paramsMap);
        call.enqueue(new Callback<ApiResult<GetCardMyAssetsRespDto>>() {
            @Override
            public void onResponse(Call<ApiResult<GetCardMyAssetsRespDto>> call, Response<ApiResult<GetCardMyAssetsRespDto>> response) {
                ApiResult<GetCardMyAssetsRespDto> respDto = response.body();
                if (ExceptionEnum.SUCCESS.getCode().equals(respDto.getErrCode())) {
                    mAdEmptyView.show(false);
                    if (respDto.getData().getMyAssets().size() > 0) {
                        if (myAssetsRefreshFlag) {
                            myAssetsList = respDto.getData().getMyAssets();
                        } else {
                            myAssetsList.addAll(respDto.getData().getMyAssets());
                        }
                        myAssetsPage = respDto.getData().getPage();
                        if (myAssetsPage.isNextFlag()) {
                            mCardMyAssetsRefreshLayout.setEnableLoadMore(true);
                        } else {
                            mCardMyAssetsRefreshLayout.setEnableLoadMore(false);
                        }
                        loadCardMyAssetsAdapter();
                    } else {
                        showOrHideEmptyPage(true);
                    }
                } else {
                    Toast.makeText(getContext(),getString(R.string.err_code_txt) +
                            respDto.getErrCode(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResult<GetCardMyAssetsRespDto>> call, Throwable t) {
                mAdEmptyView.show(false);
                if (getActivity() != null) {
                    Toast.makeText(getContext(),getString(R.string.network_error_msg),Toast.LENGTH_LONG).show();
                }
                t.printStackTrace();
            }
        });
    }

    private void loadCardMyAssetsAdapter() {
        cardMyAssetsAdapter = new CardMyAssetsAdapter(myAssetsList,getContext());
        cardMyAssetsAdapter.setPage(getCardMyAssetsRespDto.getPage());
        mCardMyAssetsLv.setAdapter(cardMyAssetsAdapter);
        mCardMyAssetsLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GetCardMyAssetsRespDto.MyAssetsBean itemInfo = myAssetsList.get(position);
                Bundle argz = new Bundle();
//                argz.putString("address", itemInfo.get);
                argz.putString("issuerAddress", itemInfo.getIssuer().getAddress());
                argz.putString("assetCode", itemInfo.getAssetInfo().getCode());
                BPCardDetailsFragment fragment = new BPCardDetailsFragment();
                fragment.setArguments(argz);
                startFragment(fragment);
            }
        });
    }

    private void loadMyAssetsDatasViews(){
        mCardMyAssetsRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                refreshlayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshMyAssetsData();
                        mCardMyAssetsRefreshLayout.finishRefresh();
                        mCardMyAssetsRefreshLayout.setNoMoreData(false);
                        getMyCardAssetsDatas();
                    }
                }, 500);

            }
        });
        mCardMyAssetsRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(final RefreshLayout refreshlayout) {
                mCardMyAssetsRefreshLayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(cardAdDatasAdapter == null){
                            mCardMyAssetsRefreshLayout.finishRefresh();
                            return;
                        }
                        mCardMyAssetsRefreshLayout.finishLoadMore(500);
                        loadMoreMyAssetsData();
                    }
                }, 500);
            }
        });
    }

    private void refreshMyAssetsData(){
        myAssetsRefreshFlag = true;
        myAssetsPageStart = 1;
        myAssetsList.clear();
        loadAdDataAdapter();
    }

    private void loadMoreMyAssetsData(){
        myAssetsPageStart ++;
        myAssetsRefreshFlag = false;
        getMyCardAssetsDatas();
        loadAdDataAdapter();
    }
    // My assets part end  --------

    // AD part start ++++++++
    private void getCardBuyAd() {
        String json = "{\"advertList\":[{\"advertId\":\"10000020\",\"advertTitle\":\"阳澄湖牌大闸蟹礼券 8只装\",\"price\":\"2\",\"coin\":\"BU\",\"stockQuantity\":\"5\",\"issuer\":{\"name\":\"现牛羊\",\"photo\":\"base64\"}}],\"page\":{\"count\":1,\"curSize\":2,\"endOfGroup\":1,\"firstResultNumber\":0,\"nextFlag\":false,\"queryTotal\":true,\"size\":10,\"start\":1,\"startOfGroup\":1,\"total\":2}}";
        getCardAdDataRespDto = JSON.parseObject(json,GetCardAdDataRespDto.class);
        cardAdList = getCardAdDataRespDto.getAdvertList();
        if (cardAdList.size() > 0) {
            loadAdDataAdapter();
        } else {
            showOrHideEmptyPage(true);
        }
        cardAdPage = getCardAdDataRespDto.getPage();
        if (cardAdPage.isNextFlag()) {
            mAdRefreshLayout.setEnableLoadMore(true);
        } else {
            mAdRefreshLayout.setEnableLoadMore(false);
        }
    }

    private void getCardSellAd() {
//        String json = "{\"advertList\":[],\"page\":{\"count\":1,\"curSize\":2,\"endOfGroup\":1,\"firstResultNumber\":0,\"nextFlag\":false,\"queryTotal\":true,\"size\":10,\"start\":1,\"startOfGroup\":1,\"total\":2}}";
        String json = "{\"advertList\":[{\"advertId\":\"10000021\",\"advertTitle\":\"阳澄湖牌大闸蟹礼券 2只装\",\"price\":\"100\",\"coin\":\"BU\",\"stockQuantity\":\"10\",\"issuer\":{\"name\":\"阳澄湖大闸蟹管理中心\",\"photo\":\"\"}}],\"page\":{\"count\":1,\"curSize\":2,\"endOfGroup\":1,\"firstResultNumber\":0,\"nextFlag\":false,\"queryTotal\":true,\"size\":10,\"start\":1,\"startOfGroup\":1,\"total\":2}}";
        getCardAdDataRespDto = JSON.parseObject(json,GetCardAdDataRespDto.class);
        cardAdList = getCardAdDataRespDto.getAdvertList();
        cardAdPage = getCardAdDataRespDto.getPage();
        if (cardAdList.size() > 0) {
            loadAdDataAdapter();
        } else {
            showOrHideEmptyPage(true);
        }
        if (cardAdPage.isNextFlag()) {
            mAdRefreshLayout.setEnableLoadMore(true);
        } else {
            mAdRefreshLayout.setEnableLoadMore(false);
        }
    }

    private void getAdDatas() {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("advertType", adType.toString());
        paramsMap.put("startPage", cardAdPageStart.toString());
        paramsMap.put("pageSize", cardAdPageSize);
//        paramsMap.put("userToken", sharedPreferencesHelper.getSharedPreference("userToken","").toString());
        AssetService assetService = RetrofitFactory.getInstance().getRetrofit().create(AssetService.class);
        retrofit2.Call<ApiResult<GetCardAdDataRespDto>> call = assetService.getCardAdData(paramsMap);
        call.enqueue(new Callback<ApiResult<GetCardAdDataRespDto>>() {
            @Override
            public void onResponse(retrofit2.Call<ApiResult<GetCardAdDataRespDto>> call, Response<ApiResult<GetCardAdDataRespDto>> response) {
                ApiResult<GetCardAdDataRespDto> respDto = response.body();
                if (ExceptionEnum.SUCCESS.getCode().equals(respDto.getErrCode())) {
                    if (cardAdRefreshFlag) {
                        cardAdList = respDto.getData().getAdvertList();
                    } else {
                        cardAdList.addAll(respDto.getData().getAdvertList());
                    }
                    cardAdPage = respDto.getData().getPage();
                    if (cardAdPage.isNextFlag()) {
                        mAdRefreshLayout.setEnableLoadMore(true);
                    } else {
                        mAdRefreshLayout.setEnableLoadMore(false);
                    }
                    loadAdDataAdapter();
                } else {
                    Toast.makeText(getContext(),getString(R.string.err_code_txt) +
                            respDto.getErrCode(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ApiResult<GetCardAdDataRespDto>> call, Throwable t) {
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
        cardAdDatasAdapter.setOnItemOptBtnListener(new CardAdDatasAdapter.OnItemOptBtnListener() {
            @Override
            public void onClick(int i) {
//                if (cardAdClickFlag) {
//                    return;
//                }
//                cardAdClickFlag = true;
                GetCardAdDataRespDto.AdvertListBean currentItem = (GetCardAdDataRespDto.AdvertListBean) cardAdDatasAdapter.getItem(i);
                showConfirmOperationBottomSheet(currentItem);
            }
        });
    }

    private void loadAdDatasViews(){
        mAdRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                refreshlayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshCardAdData();
                        mAdRefreshLayout.finishRefresh();
                        mAdRefreshLayout.setNoMoreData(false);
                        getAdDatas();
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
        cardAdList.clear();
        loadAdDataAdapter();
    }

    private void loadMoreCardAdData(){
        cardAdPageStart ++;
        cardAdRefreshFlag = false;
        loadAdDataAdapter();
    }
    // AD part end --------

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

    private  void showOrHideEmptyPage(boolean showFlag) {
        if ("MINE".equals(activeTab)) {
            if (showFlag) {
                mCardMyAssetsEmptyLl.setVisibility(View.VISIBLE);
                mCardMyAssetsLv.setVisibility(View.GONE);
            } else {
                mCardMyAssetsEmptyLl.setVisibility(View.GONE);
                mCardMyAssetsLv.setVisibility(View.VISIBLE);
            }
        } else if ("BUY".equals(activeTab) || "SELL".equals(activeTab)) {
            if (showFlag) {
                mCardAdListEmptyLl.setVisibility(View.VISIBLE);
                mCardAdDataLv.setVisibility(View.GONE);
            } else {
                mCardAdListEmptyLl.setVisibility(View.GONE);
                mCardAdDataLv.setVisibility(View.VISIBLE);
            }
        }
    }

    private void showConfirmOperationBottomSheet(final GetCardAdDataRespDto.AdvertListBean itemInfo) {
        final QMUIBottomSheet sheet = new QMUIBottomSheet(getContext());
        buyOrSellQuantity = 1;
        stockQuantity = Integer.parseInt(itemInfo.getStockQuantity());
        subFlag = true;
        addFlag = true;

        sheet.setContentView(R.layout.card_ad_confirm_layout);
        TextView mAdTitle = sheet.findViewById(R.id.cardAdConfirmTitleTv);
        TextView mAdPrice = sheet.findViewById(R.id.cardAdConfirmPriceTv);
        TextView mAdAssetId = sheet.findViewById(R.id.cardAdConfirmAssetIdTv);
        final TextView mAdTotalQuantity = sheet.findViewById(R.id.cardAdConfirmQuantityTv);
        final TextView mAdTotalQuantitySub = sheet.findViewById(R.id.cardAdConfirmSubQuantityTv);
        final TextView mAdTotalQuantityAdd = sheet.findViewById(R.id.cardAdConfirmAddQuantityTv);
        final TextView mAdTotalQuantityCompute = sheet.findViewById(R.id.cardAdConfirmComputeQuantityTv);
        TextView mAdSellFee = sheet.findViewById(R.id.cardAdConfirmSellFeeTv);
        TextView mAdTotalAmountLabel = sheet.findViewById(R.id.cardAdConfirmTotalAmountLabelTv);
        final TextView mAdTotalAmountValue = sheet.findViewById(R.id.cardAdConfirmTotalAmountValueTv);

        QMUIRoundButton mConfirmBtn = sheet.findViewById(R.id.cardAdConfirmBuyOrSellBtn);
        mAdTotalQuantitySub.setTextColor(getResources().getColor(R.color.app_txt_color_gray));

//        fill detail
        mAdTitle.setText(itemInfo.getAdvertTitle());
        String priceTxt = itemInfo.getPrice() + " " + itemInfo.getCoin() + " " + getString(R.string.card_package_card_ad_confirm_unit_txt);
        mAdPrice.setText(priceTxt);
        String assetIdTxt = getString(R.string.card_package_card_ad_confirm_asset_id_txt);
//        assetIdTxt = String.format(assetIdTxt, itemInfo.getAdvertId());
        mAdAssetId.setText(assetIdTxt);
        String feeTxt = getString(R.string.card_package_card_ad_sell_fee_txt);
        feeTxt = String.format(feeTxt, String.valueOf(Constants.CARD_TX_FEE)) + " " + itemInfo.getCoin();
        mAdSellFee.setText(feeTxt);

        if (CardAdTypeEnum.BUY.getCode().equals(adType)) {
            totalQuantityTxt = getString(R.string.card_package_card_ad_sell_amount_txt);
            mAdTotalAmountLabel.setText(R.string.card_package_card_ad_confirm_total_amount_sell_txt);
            totalAmount = DecimalCalculate.sub(DecimalCalculate.mul(Double.parseDouble(String.valueOf(buyOrSellQuantity)), Double.parseDouble(itemInfo.getPrice())), Constants.CARD_TX_FEE);

            mConfirmBtn.setText(R.string.card_package_card_ad_confirm_sell_txt);
        } else if (CardAdTypeEnum.SELL.getCode().equals(adType)) {
            totalQuantityTxt = getString(R.string.card_package_card_ad_buy_amount_txt);
            mAdTotalAmountLabel.setText(R.string.card_package_card_ad_confirm_total_amount_buy_txt);
            totalAmount = DecimalCalculate.add(DecimalCalculate.mul(Double.parseDouble(String.valueOf(buyOrSellQuantity)), Double.parseDouble(itemInfo.getPrice())), Constants.CARD_TX_FEE);

            mConfirmBtn.setText(R.string.card_package_card_ad_confirm_buy_txt);
        }
        mAdTotalQuantity.setText(totalQuantityTxt);
        mAdTotalQuantityCompute.setText(buyOrSellQuantity.toString());
        mAdTotalAmountValue.setText(CommonUtil.rvZeroAndDot(String.valueOf(totalAmount)) + itemInfo.getCoin());

        sheet.show();
        sheet.findViewById(R.id.cardAdConfirmCloseBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheet.dismiss();
                cardAdClickFlag = false;
            }
        });
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPasswordConfirmDialog();
            }
        });
        mAdTotalQuantitySub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!subFlag) {
                    return;
                }
                buyOrSellQuantity--;
                addFlag = true;
                mAdTotalQuantityAdd.setTextColor(getResources().getColor(R.color.app_color_green));
                if (buyOrSellQuantity == 1) {
                    subFlag = false;
                    mAdTotalQuantitySub.setTextColor(getResources().getColor(R.color.app_txt_color_gray));
                }
                if (CardAdTypeEnum.BUY.getCode().equals(adType)) {
                    totalAmount = DecimalCalculate.sub(DecimalCalculate.mul(Double.parseDouble(String.valueOf(buyOrSellQuantity)), Double.parseDouble(itemInfo.getPrice())), Constants.CARD_TX_FEE);
                } else if (CardAdTypeEnum.SELL.getCode().equals(adType)) {
                    totalAmount = DecimalCalculate.add(DecimalCalculate.mul(Double.parseDouble(String.valueOf(buyOrSellQuantity)), Double.parseDouble(itemInfo.getPrice())), Constants.CARD_TX_FEE);
                }

                mAdTotalQuantity.setText(totalQuantityTxt);
                mAdTotalQuantityCompute.setText(buyOrSellQuantity.toString());
                mAdTotalAmountValue.setText(CommonUtil.rvZeroAndDot(String.valueOf(totalAmount)) + itemInfo.getCoin());
            }
        });
        mAdTotalQuantityAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!addFlag) {
                    return;
                }
                buyOrSellQuantity++;
                subFlag = true;
                mAdTotalQuantitySub.setTextColor(getResources().getColor(R.color.app_color_green));
                if (buyOrSellQuantity.equals(stockQuantity)) {
                    addFlag = false;
                    mAdTotalQuantityAdd.setTextColor(getResources().getColor(R.color.app_txt_color_gray));
                }
                if (CardAdTypeEnum.BUY.getCode().equals(adType)) {
                    totalAmount = DecimalCalculate.sub(DecimalCalculate.mul(Double.parseDouble(String.valueOf(buyOrSellQuantity)), Double.parseDouble(itemInfo.getPrice())), Constants.CARD_TX_FEE);
                } else if (CardAdTypeEnum.SELL.getCode().equals(adType)) {
                    totalAmount = DecimalCalculate.add(DecimalCalculate.mul(Double.parseDouble(String.valueOf(buyOrSellQuantity)), Double.parseDouble(itemInfo.getPrice())), Constants.CARD_TX_FEE);
                }

                mAdTotalQuantity.setText(totalQuantityTxt);
                mAdTotalQuantityCompute.setText(buyOrSellQuantity.toString());
                mAdTotalAmountValue.setText(CommonUtil.rvZeroAndDot(String.valueOf(totalAmount)) + itemInfo.getCoin());
            }
        });
    }

    private void showPasswordConfirmDialog() {
        final QMUIDialog qmuiDialog = new QMUIDialog(getContext());
        qmuiDialog.setCanceledOnTouchOutside(false);
        qmuiDialog.setContentView(R.layout.password_comfirm_layout);
        qmuiDialog.show();
    }

    private String getAccountBPData(){
        String data = sharedPreferencesHelper.getSharedPreference("BPData", "").toString();
        return data;
    }
}
