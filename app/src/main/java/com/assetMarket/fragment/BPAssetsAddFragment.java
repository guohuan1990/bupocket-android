package com.assetMarket.fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.assetMarket.R;
import com.assetMarket.adaptor.SearchTokenAdapter;
import com.assetMarket.base.BaseFragment;
import com.assetMarket.http.api.RetrofitFactory;
import com.assetMarket.http.api.TokenService;
import com.assetMarket.http.api.dto.resp.ApiResult;
import com.assetMarket.http.api.dto.resp.SearchTokenRespDto;
import com.assetMarket.utils.CommonUtil;
import com.assetMarket.utils.SharedPreferencesHelper;
import com.assetMarket.view.DrawableEditText;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.HashMap;
import java.util.Map;

public class BPAssetsAddFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;

    @BindView(R.id.searchTokenListView)
    ListView mSearchTokenListView;

    @BindView(R.id.emptyView)
    QMUIEmptyView mEmptyView;

    @BindView(R.id.searchTokenEt)
    DrawableEditText mSearchTokenEt;

    SearchTokenAdapter searchTokenAdapter;
    private String currentAccAddress;
    protected SharedPreferencesHelper sharedPreferencesHelper;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_assets_add, null);
        ButterKnife.bind(this, root);
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        initTopBar();
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        currentAccAddress = sharedPreferencesHelper.getSharedPreference("currentAccAddr", "").toString();
        mSearchTokenEt.setOnDrawableClickListener(new DrawableEditText.OnDrawableClickListener() {
            @Override
            public void onDrawableClick() {
                String input = mSearchTokenEt.getText().toString();
                if(CommonUtil.isNull(input)){return;}
                mSearchTokenEt.clearFocus();
                searchToken(input);
            }
        });

        mSearchTokenEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String input = mSearchTokenEt.getText().toString();
                if(CommonUtil.isNull(input)){return false;}
                searchToken(input);
                mSearchTokenEt.clearFocus();
                return true;
            }
        });

        return root;
    }

    private void searchToken(String assetCode){
        TokenService tokenService = RetrofitFactory.getInstance().getRetrofit().create(TokenService.class);
        Map<String, Object> parmasMap = new HashMap<>();
        parmasMap.put("assetCode",assetCode);
        parmasMap.put("address",currentAccAddress);
        parmasMap.put("startPage", 1);
        parmasMap.put("pageSize", 100);
        Call<ApiResult<SearchTokenRespDto>> call = tokenService.queryTokens(parmasMap);
        call.enqueue(new Callback<ApiResult<SearchTokenRespDto>>() {
            @Override
            public void onResponse(Call<ApiResult<SearchTokenRespDto>> call, Response<ApiResult<SearchTokenRespDto>> response) {
                ApiResult<SearchTokenRespDto> respDto = response.body();
                if(respDto != null){
                    handleSearchTokenData(respDto.getData());
                }else {
                    mEmptyView.show(getResources().getString(R.string.emptyView_mode_desc_no_data),null);
                }
            }

            @Override
            public void onFailure(Call<ApiResult<SearchTokenRespDto>> call, Throwable t) {
                t.printStackTrace();
                mEmptyView.show(getResources().getString(R.string.emptyView_mode_desc_fail_title), null);
            }
        });
    }

    private void handleSearchTokenData(SearchTokenRespDto searchTokenRespDto){
        if(searchTokenRespDto != null || searchTokenRespDto.getTokenList() != null){
            if(searchTokenRespDto.getTokenList() == null || searchTokenRespDto.getTokenList().size() == 0) {
                mEmptyView.show(getResources().getString(R.string.emptyView_mode_desc_no_data), null);
            }else{
                mEmptyView.show(null, null);
            }

        }else{
            mEmptyView.show(getResources().getString(R.string.emptyView_mode_desc_fail_title), null);
            return;
        }

        searchTokenAdapter = new SearchTokenAdapter(searchTokenRespDto.getTokenList(), getContext());
        mSearchTokenListView.setAdapter(searchTokenAdapter);

    }

    private void initTopBar() {
        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QMUIStatusBarHelper.setStatusBarDarkMode(getBaseFragmentActivity());
                popBackStack();
            }
        });
    }
}
