package com.bupocket.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.bupocket.http.api.dto.resp.GetCardAdBlobRespDto;
import com.bupocket.http.api.dto.resp.GetCardAdDataRespDto;
import com.bupocket.http.api.dto.resp.GetCardMyAssetsRespDto;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.DecimalCalculate;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.wallet.Wallet;
import com.bupocket.wallet.exception.WalletException;
import com.bupocket.wallet.model.WalletSignData;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
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
    private String adId;
    private String adPrice;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private String totalQuantityTxt = "";
    private Integer buyOrSellQuantity;
    private Integer stockQuantity;
    private double totalAmount;
    private boolean subFlag;
    private boolean addFlag;
    private String txBlob;
    private String txHash;
    private String blobId;
    private String tokenBalance;
    private String currentAccAddress;
    private String availableTokenBalance;

    private View cardPackageMine;
    private QMUIEmptyView mCardMyAssetsEmptyView;
    private SmartRefreshLayout mCardMyAssetsRefreshLayout;
    private ListView mCardMyAssetsLv;
    private LinearLayout mCardMyAssetsEmptyLl;
    private LinearLayout mCardMyAssetsBuyRequestLl;
    private QMUITipDialog txSendingTipDialog;
    private QMUITipDialog loadGetBalanceTipDialog;
    private QMUIDialog pwdConfirmDialog;
    private QMUIBottomSheet confirmOperationBtmSheet;

    private GetCardMyAssetsRespDto getCardMyAssetsRespDto;
    private CardMyAssetsAdapter cardMyAssetsAdapter;
    private GetCardMyAssetsRespDto.PageBean myAssetsPage;
    private List<GetCardMyAssetsRespDto.MyAssetsBean> myAssetsList = new ArrayList<>();
    private Integer myAssetsPageStart = 1;
    private String myAssetsPageSize = "10";
    private boolean myAssetsRefreshFlag = true;


    private View cardPackageBuyOrSell;
    private QMUIEmptyView mAdEmptyView;
    private SmartRefreshLayout mAdRefreshLayout;
    private ListView mCardAdDataLv;
    private LinearLayout mCardAdListEmptyLl;

    private GetCardAdDataRespDto getCardAdDataRespDto;
    private CardAdDatasAdapter cardAdDatasAdapter;
    private GetCardAdDataRespDto.PageBean cardAdPage;
    private List<GetCardAdDataRespDto.AdvertListBean> cardAdList = new ArrayList<>();
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

    @Override
    public void onStart() {
        super.onStart();
        activeTab = "MINE";
        cardAdPageStart = 1;
        cardAdPageSize = "10";
        setListeners();
    }


    private void init() {
        initData();
        initUI();
    }

    private void initUI() {
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
    }

    private void initData() {
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), Constants.LOCAL_SHARED_FILE_NAME);
        currentAccAddress = sharedPreferencesHelper.getSharedPreference("currentAccAddr", "").toString();
    }

    private void addTabsPages() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        if ("MINE".equals(activeTab)) {
            cardPackageMine = inflater.inflate(R.layout.card_package_mine_layout, mCardContainerTabContentLl,true);
            mCardMyAssetsEmptyView = cardPackageMine.findViewById(R.id.cardMineEmptyView);
            mCardMyAssetsRefreshLayout = cardPackageMine.findViewById(R.id.refreshLayout);
            mCardMyAssetsLv = cardPackageMine.findViewById(R.id.cardMineLv);
            mCardMyAssetsEmptyLl = cardPackageMine.findViewById(R.id.cardMyAssetsListEmptyLl);
//            mCardMyAssetsBuyRequestLl = cardPackageMine.findViewById(R.id.cardMyAssetsBuyRequestLl);
//            mCardMyAssetsBuyRequestLl.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    startFragment(new BPBuyRequestFragment());
//                }
//            });
            loadMyAssetsDatasViews();
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
        getMyCardAssetsDatas();
        mCardContainerMineCardTabTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("MINE".equals(activeTab)) {
                    return;
                }
                selectMineTab();
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
                cardAdPageStart = 1;
                cardAdList.clear();
                addTabsPages();
                loadAdDataAdapter();
                mAdRefreshLayout.setNoMoreData(false);
                showOrHideEmptyPage(false);
                mAdEmptyView.show(true);
                getAdDatas(activeTab);
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
                cardAdPageStart = 1;
                cardAdList.clear();
                addTabsPages();
                loadAdDataAdapter();
                mAdRefreshLayout.setNoMoreData(false);
                showOrHideEmptyPage(false);
                mAdEmptyView.show(true);
                getAdDatas(activeTab);
                setActiveTab();
            }
        });
    }

    private void selectMineTab() {
        mCardContainerTabContentLl.removeAllViews();
        activeTab = "MINE";
        addTabsPages();
        getMyCardAssetsDatas();
        setActiveTab();
    }

    // My assets part start  ++++++++
    private void getMyCardAssetsDatas() {
        mCardMyAssetsEmptyView.show(true);
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("userToken", sharedPreferencesHelper.getSharedPreference("userToken","").toString());
        paramsMap.put("startPage",myAssetsPageStart.toString());
        paramsMap.put("pageSize",myAssetsPageSize);
        AssetService assetService = RetrofitFactory.getInstance().getRetrofit(getActivity()).create(AssetService.class);
        retrofit2.Call<ApiResult<GetCardMyAssetsRespDto>> call = assetService.getMyCardMine(paramsMap);
        call.enqueue(new Callback<ApiResult<GetCardMyAssetsRespDto>>() {
            @Override
            public void onResponse(Call<ApiResult<GetCardMyAssetsRespDto>> call, Response<ApiResult<GetCardMyAssetsRespDto>> response) {
                ApiResult<GetCardMyAssetsRespDto> respDto = response.body();
                if(respDto == null){
                    return;
                }
                if (ExceptionEnum.SUCCESS.getCode().equals(respDto.getErrCode())) {
                    mCardMyAssetsEmptyView.show(false);
                    getCardMyAssetsRespDto = respDto.getData();
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
                    showOrHideEmptyPage(false);
                    mCardMyAssetsEmptyView.show(getResources().getString(R.string.emptyView_mode_desc_fail_title), null);
                    Toast.makeText(getContext(),getString(R.string.err_code_txt) +
                            respDto.getErrCode(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResult<GetCardMyAssetsRespDto>> call, Throwable t) {
                showOrHideEmptyPage(false);
                mCardMyAssetsEmptyView.show(getResources().getString(R.string.emptyView_mode_desc_fail_title), null);
                if (getActivity() != null) {
                    Toast.makeText(getContext(),getString(R.string.network_error_msg),Toast.LENGTH_LONG).show();
                }
                t.printStackTrace();
            }
        });
    }

    private void loadCardMyAssetsAdapter() {
        cardMyAssetsAdapter = new CardMyAssetsAdapter(myAssetsList,getContext());
        cardMyAssetsAdapter.setPage(myAssetsPage);
        mCardMyAssetsLv.setAdapter(cardMyAssetsAdapter);
        mCardMyAssetsLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GetCardMyAssetsRespDto.MyAssetsBean itemInfo = myAssetsList.get(position);
                Bundle argz = new Bundle();
                argz.putString("issuerAddress", itemInfo.getAssetInfo().getIssuerAddress());
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
        loadCardMyAssetsAdapter();
    }

    private void loadMoreMyAssetsData(){
        myAssetsPageStart ++;
        myAssetsRefreshFlag = false;
        getMyCardAssetsDatas();
        loadCardMyAssetsAdapter();
    }
    // My assets part end  --------

    // AD part start ++++++++
    private void getAdDatas(final String currentTab) {
        mAdEmptyView.show(true);
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("advertType", adType.toString());
        paramsMap.put("startPage", cardAdPageStart.toString());
        paramsMap.put("pageSize", cardAdPageSize);
        paramsMap.put("userToken", sharedPreferencesHelper.getSharedPreference("userToken","").toString());
        AssetService assetService = RetrofitFactory.getInstance().getRetrofit(getActivity()).create(AssetService.class);
        retrofit2.Call<ApiResult<GetCardAdDataRespDto>> call = assetService.getCardAdData(paramsMap);
        call.enqueue(new Callback<ApiResult<GetCardAdDataRespDto>>() {
            @Override
            public void onResponse(retrofit2.Call<ApiResult<GetCardAdDataRespDto>> call, Response<ApiResult<GetCardAdDataRespDto>> response) {
                mAdEmptyView.show(false);
                ApiResult<GetCardAdDataRespDto> respDto = response.body();
                if (respDto == null) {
                    return;
                }
                if (ExceptionEnum.SUCCESS.getCode().equals(respDto.getErrCode())) {
                    if (currentTab.equals(activeTab)) {


                        if (cardAdRefreshFlag) {
                            cardAdList = respDto.getData().getAdvertList();
                        } else {
                            cardAdList.addAll(respDto.getData().getAdvertList());
                        }
                        if (cardAdList.size() > 0) {
                            loadAdDataAdapter();
                            showOrHideEmptyPage(false);
                        } else {
                            showOrHideEmptyPage(true);
                        }
                        cardAdPage = respDto.getData().getPage();
                        if (cardAdPage.isNextFlag()) {
                            mAdRefreshLayout.setEnableLoadMore(true);
                        } else {
                            mAdRefreshLayout.setEnableLoadMore(false);
                        }
                        loadAdDataAdapter();
                    } else {
                        cardAdList.clear();
                    }
                } else {
                    showOrHideEmptyPage(false);
                    mAdEmptyView.show(getResources().getString(R.string.emptyView_mode_desc_fail_title), null);
                    Toast.makeText(getContext(),getString(R.string.err_code_txt) +
                            respDto.getErrCode(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ApiResult<GetCardAdDataRespDto>> call, Throwable t) {
                showOrHideEmptyPage(false);
                mAdEmptyView.show(getResources().getString(R.string.emptyView_mode_desc_fail_title), null);
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
                adId = currentItem.getAdvertId();
                adPrice = currentItem.getPrice();
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
                        getAdDatas(activeTab);
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
        confirmOperationBtmSheet = new QMUIBottomSheet(getContext());
        buyOrSellQuantity = 1;
        stockQuantity = Integer.parseInt(itemInfo.getStockQuantity());
        subFlag = true;
        addFlag = true;

        confirmOperationBtmSheet.setContentView(R.layout.card_ad_confirm_layout);
        TextView mAssetName = confirmOperationBtmSheet.findViewById(R.id.cardAdConfirmAssetNameTv);
        TextView mAdPrice = confirmOperationBtmSheet.findViewById(R.id.cardAdConfirmPriceTv);
        TextView mAdAssetId = confirmOperationBtmSheet.findViewById(R.id.cardAdConfirmAssetIdTv);
        TextView mAdAssetIssuerName = confirmOperationBtmSheet.findViewById(R.id.cardAdConfirmAssetIssuerNameTv);
        final TextView mAdTotalQuantity = confirmOperationBtmSheet.findViewById(R.id.cardAdConfirmQuantityTv);
        final TextView mAdTotalQuantitySub = confirmOperationBtmSheet.findViewById(R.id.cardAdConfirmSubQuantityTv);
        final TextView mAdTotalQuantityAdd = confirmOperationBtmSheet.findViewById(R.id.cardAdConfirmAddQuantityTv);
        final TextView mAdTotalQuantityCompute = confirmOperationBtmSheet.findViewById(R.id.cardAdConfirmComputeQuantityTv);
        TextView mAdSellFee = confirmOperationBtmSheet.findViewById(R.id.cardAdConfirmSellFeeTv);
        TextView mAdTotalAmountLabel = confirmOperationBtmSheet.findViewById(R.id.cardAdConfirmTotalAmountLabelTv);
        final TextView mAdTotalAmountValue = confirmOperationBtmSheet.findViewById(R.id.cardAdConfirmTotalAmountValueTv);

        QMUIRoundButton mConfirmBtn = confirmOperationBtmSheet.findViewById(R.id.cardAdConfirmBuyOrSellBtn);
        mAdTotalQuantitySub.setTextColor(getResources().getColor(R.color.app_txt_color_gray));
        subFlag = false;

//        fill detail
        mAssetName.setText(itemInfo.getAssetName());
        String priceTxt = itemInfo.getPrice() + " " + itemInfo.getCoin() + " " + getString(R.string.card_package_card_ad_confirm_unit_txt);
        mAdPrice.setText(priceTxt);
        String assetIdTxt = getString(R.string.card_package_card_ad_confirm_asset_id_txt);
        assetIdTxt = String.format(assetIdTxt, itemInfo.getAssetCode());
        mAdAssetId.setText(assetIdTxt);
        mAdAssetIssuerName.setText(itemInfo.getIssuer().getName());
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

        confirmOperationBtmSheet.show();
        confirmOperationBtmSheet.findViewById(R.id.cardAdConfirmCloseBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmOperationBtmSheet.dismiss();
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
        pwdConfirmDialog = new QMUIDialog(getContext());
        pwdConfirmDialog.setCanceledOnTouchOutside(true);
        pwdConfirmDialog.setContentView(R.layout.password_comfirm_layout);
        pwdConfirmDialog.show();

        QMUIRoundButton mPasswordConfirmBtn = pwdConfirmDialog.findViewById(R.id.passwordConfirmBtn);
        ImageView mPasswordConfirmCloseBtn = pwdConfirmDialog.findViewById(R.id.passwordConfirmCloseBtn);
        TextView mPasswordConfirmNotice = pwdConfirmDialog.findViewById(R.id.passwordConfirmNotice);
        mPasswordConfirmNotice.setText(R.string.card_package_pwd_dialog_message_txt);

        mPasswordConfirmCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pwdConfirmDialog.dismiss();
            }
        });
        mPasswordConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 检查合法性
                EditText mPasswordConfirmEt = pwdConfirmDialog.findViewById(R.id.passwordConfirmEt);
                final String password = mPasswordConfirmEt.getText().toString().trim();
                txSendingTipDialog = new QMUITipDialog.Builder(getContext())
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                        .setTipWord(getResources().getString(R.string.send_tx_handleing_txt))
                        .create();
                txSendingTipDialog.show();
                txSendingTipDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
                            return true;
                        }
                        return false;
                    }
                });
                if (CardAdTypeEnum.BUY.getCode().equals(adType)) {
                    getSellAdBlob(password);
                } else if (CardAdTypeEnum.SELL.getCode().equals(adType)) {
                    getBuyAdBlob(password);
                }
            }
        });
    }

    private void getSellAdBlob(final String password) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("advertId",adId);
        paramsMap.put("price",adPrice);
        paramsMap.put("quantity",buyOrSellQuantity.toString());
        paramsMap.put("userToken", sharedPreferencesHelper.getSharedPreference("userToken","").toString());
        AssetService assetService = RetrofitFactory.getInstance().getRetrofit(getActivity()).create(AssetService.class);
        Call<ApiResult<GetCardAdBlobRespDto>> call = assetService.getCardSellAdBlob(paramsMap);
        call.enqueue(new Callback<ApiResult<GetCardAdBlobRespDto>>() {
            @Override
            public void onResponse(Call<ApiResult<GetCardAdBlobRespDto>> call, Response<ApiResult<GetCardAdBlobRespDto>> response) {
                ApiResult<GetCardAdBlobRespDto> respDto = response.body();
                if(ExceptionEnum.SUCCESS.getCode().equals(respDto.getErrCode())){
                    txBlob = respDto.getData().getTxBlob();
                    txHash = respDto.getData().getTxHash();
                    blobId = respDto.getData().getBlobId();
                    signBlob(password);
                } else if(ExceptionEnum.ASSET_BALANCE_ERROR.getCode().equals(respDto.getErrCode())) {
                    txSendingTipDialog.dismiss();
                    Toast.makeText(getContext(),getString(R.string.err_account_asset_balance_not_enough),Toast.LENGTH_LONG).show();
                } else if(ExceptionEnum.ADVERT_ID_EXIST_ERROR.getCode().equals(respDto.getErrCode())) {
                    txSendingTipDialog.dismiss();
                    Toast.makeText(getContext(),getString(R.string.err_ad_not_exist),Toast.LENGTH_LONG).show();
                } else if(ExceptionEnum.ADVERT_PRICE_ERROR1.getCode().equals(respDto.getErrCode())) {
                    txSendingTipDialog.dismiss();
                    Toast.makeText(getContext(),getString(R.string.err_ad_price_refresh),Toast.LENGTH_LONG).show();
                } else if(ExceptionEnum.ORDER_PLACE_ONESELF_ERROR.getCode().equals(respDto.getErrCode())) {
                    txSendingTipDialog.dismiss();
                    Toast.makeText(getContext(),getString(R.string.err_cannot_tx_with_self),Toast.LENGTH_LONG).show();
                } else if(ExceptionEnum.ADVERT_STOCK_QUANTITY_ERROR.getCode().equals(respDto.getErrCode())) {
                    txSendingTipDialog.dismiss();
                    Toast.makeText(getContext(),getString(R.string.err_ad_stock_quantity_not_enough),Toast.LENGTH_LONG).show();
                } else {
                    txSendingTipDialog.dismiss();
                    Toast.makeText(getContext(),getString(R.string.err_cannot_order),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResult<GetCardAdBlobRespDto>> call, Throwable t) {
                txSendingTipDialog.dismiss();
                if (getActivity() != null) {
                    Toast.makeText(getContext(),getString(R.string.network_error_msg),Toast.LENGTH_LONG).show();
                }
                t.printStackTrace();
            }
        });
    }

    private void getBuyAdBlob(final String password) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("advertId",adId);
        paramsMap.put("price",adPrice);
        paramsMap.put("quantity",buyOrSellQuantity.toString());
        paramsMap.put("userToken", sharedPreferencesHelper.getSharedPreference("userToken","").toString());
        AssetService assetService = RetrofitFactory.getInstance().getRetrofit(getActivity()).create(AssetService.class);
        Call<ApiResult<GetCardAdBlobRespDto>> call = assetService.getCardBuyAdBlob(paramsMap);
        call.enqueue(new Callback<ApiResult<GetCardAdBlobRespDto>>() {
            @Override
            public void onResponse(Call<ApiResult<GetCardAdBlobRespDto>> call, Response<ApiResult<GetCardAdBlobRespDto>> response) {
                ApiResult<GetCardAdBlobRespDto> respDto = response.body();
                if(ExceptionEnum.SUCCESS.getCode().equals(respDto.getErrCode())){
                    txBlob = respDto.getData().getTxBlob();
                    txHash = respDto.getData().getTxHash();
                    blobId = respDto.getData().getBlobId();
                    signBlob(password);
                } else if(ExceptionEnum.BU_BALANCE_ERROR.getCode().equals(respDto.getErrCode())) {
                    txSendingTipDialog.dismiss();
                    Toast.makeText(getContext(),getString(R.string.err_account_bu_balance_not_enough),Toast.LENGTH_LONG).show();
                } else if(ExceptionEnum.ADVERT_ID_EXIST_ERROR.getCode().equals(respDto.getErrCode())) {
                    txSendingTipDialog.dismiss();
                    Toast.makeText(getContext(),getString(R.string.err_ad_not_exist),Toast.LENGTH_LONG).show();
                } else if(ExceptionEnum.ADVERT_PRICE_ERROR1.getCode().equals(respDto.getErrCode())) {
                    txSendingTipDialog.dismiss();
                    Toast.makeText(getContext(),getString(R.string.err_ad_price_refresh),Toast.LENGTH_LONG).show();
                } else if(ExceptionEnum.ORDER_PLACE_ONESELF_ERROR.getCode().equals(respDto.getErrCode())) {
                    txSendingTipDialog.dismiss();
                    Toast.makeText(getContext(),getString(R.string.err_cannot_tx_with_self),Toast.LENGTH_LONG).show();
                } else if(ExceptionEnum.ADVERT_STOCK_QUANTITY_ERROR.getCode().equals(respDto.getErrCode())) {
                    txSendingTipDialog.dismiss();
                    Toast.makeText(getContext(),getString(R.string.err_ad_stock_quantity_not_enough),Toast.LENGTH_LONG).show();
                } else {
                    txSendingTipDialog.dismiss();
                    Toast.makeText(getContext(),getString(R.string.err_cannot_order),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResult<GetCardAdBlobRespDto>> call, Throwable t) {
                txSendingTipDialog.dismiss();
                if (getActivity() != null) {
                    Toast.makeText(getContext(),getString(R.string.network_error_msg),Toast.LENGTH_LONG).show();
                }
                t.printStackTrace();
            }
        });
    }

    private void signBlob(final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String bpData = getAccountBPData();
                    final WalletSignData walletSignData = Wallet.getInstance().signTxBlob(password, txBlob, bpData);

                    if (walletSignData != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (CardAdTypeEnum.BUY.getCode().equals(adType)) {
                                    submitSell(walletSignData.getSignData(), walletSignData.getPublicKey());
                                } else if (CardAdTypeEnum.SELL.getCode().equals(adType)) {
                                    submitBuy(walletSignData.getSignData(), walletSignData.getPublicKey());
                                }
                            }
                        });
                    }
                }catch (WalletException e){
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(getActivity(), R.string.network_error_msg, Toast.LENGTH_SHORT).show();
                    txSendingTipDialog.dismiss();
                    Looper.loop();
                } catch (Exception e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(getActivity(), R.string.checking_password_error, Toast.LENGTH_SHORT).show();
                    txSendingTipDialog.dismiss();
                    Looper.loop();
                }finally {

                }
            }
        }).start();
    }

    private void submitSell(String txBlobSign, String publicKey) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("txBlob", txBlob);
        paramsMap.put("txHash", txHash);
        paramsMap.put("blobId", blobId);
        paramsMap.put("txBlobSign", txBlobSign);
        paramsMap.put("publicKey", publicKey);
        paramsMap.put("userToken", sharedPreferencesHelper.getSharedPreference("userToken", "").toString());
        AssetService assetService = RetrofitFactory.getInstance().getRetrofit(getActivity()).create(AssetService.class);
        Call<ApiResult> call = assetService.submitSellAd(paramsMap);
        call.enqueue(new Callback<ApiResult>() {
            @Override
            public void onResponse(Call<ApiResult> call, Response<ApiResult> response) {
                ApiResult respDto = response.body();
                if(ExceptionEnum.SUCCESS.getCode().equals(respDto.getErrCode())){
                    pwdConfirmDialog.dismiss();
                    txSendingTipDialog.dismiss();
                    confirmOperationBtmSheet.dismiss();
                    selectMineTab();
                    Toast.makeText(getContext(),getString(R.string.card_package_card_ad_confirm_sell_success_txt),Toast.LENGTH_LONG).show();
                } else if(ExceptionEnum.ADVERT_STOCK_QUANTITY_ERROR.getCode().equals(respDto.getErrCode())) {
                    txSendingTipDialog.dismiss();
                    Toast.makeText(getContext(),getString(R.string.err_ad_stock_quantity_not_enough),Toast.LENGTH_LONG).show();
                } else {
                    txSendingTipDialog.dismiss();
                    Toast.makeText(getContext(),getString(R.string.err_cannot_order),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResult> call, Throwable t) {
                txSendingTipDialog.dismiss();
                if (getActivity() != null) {
                    Toast.makeText(getContext(),getString(R.string.network_error_msg),Toast.LENGTH_LONG).show();
                }
                t.printStackTrace();
            }
        });
    }

    private void submitBuy(String txBlobSign, String publicKey) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("txBlob", txBlob);
        paramsMap.put("txHash", txHash);
        paramsMap.put("blobId", blobId);
        paramsMap.put("txBlobSign", txBlobSign);
        paramsMap.put("publicKey", publicKey);
        paramsMap.put("userToken", sharedPreferencesHelper.getSharedPreference("userToken", "").toString());
        AssetService assetService = RetrofitFactory.getInstance().getRetrofit(getActivity()).create(AssetService.class);
        Call<ApiResult> call = assetService.submitBuyAd(paramsMap);
        call.enqueue(new Callback<ApiResult>() {
            @Override
            public void onResponse(Call<ApiResult> call, Response<ApiResult> response) {
                ApiResult respDto = response.body();
                if(ExceptionEnum.SUCCESS.getCode().equals(respDto.getErrCode())){
                    pwdConfirmDialog.dismiss();
                    txSendingTipDialog.dismiss();
                    confirmOperationBtmSheet.dismiss();
                    selectMineTab();
                    Toast.makeText(getContext(),getString(R.string.card_package_card_ad_confirm_buy_success_txt),Toast.LENGTH_LONG).show();
                } else if(ExceptionEnum.ADVERT_STOCK_QUANTITY_ERROR.getCode().equals(respDto.getErrCode())) {
                    txSendingTipDialog.dismiss();
                    Toast.makeText(getContext(),getString(R.string.err_ad_stock_quantity_not_enough),Toast.LENGTH_LONG).show();
                } else {
                    txSendingTipDialog.dismiss();
                    Toast.makeText(getContext(),getString(R.string.err_cannot_order),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResult> call, Throwable t) {
                txSendingTipDialog.dismiss();
                if (getActivity() != null) {
                    Toast.makeText(getContext(),getString(R.string.network_error_msg),Toast.LENGTH_LONG).show();
                }
                t.printStackTrace();
            }
        });
    }

    private String getAccountBPData(){
        String data = sharedPreferencesHelper.getSharedPreference("BPData", "").toString();
        return data;
    }

    private String getSkeyStr(){
        return sharedPreferencesHelper.getSharedPreference("skey","").toString();
    }
}
