package com.assetMarket.fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.assetMarket.R;
import com.assetMarket.base.BaseFragment;
import com.assetMarket.http.api.AssetService;
import com.assetMarket.http.api.RetrofitFactory;
import com.assetMarket.http.api.dto.resp.ApiResult;
import com.assetMarket.http.api.dto.resp.SearchCardRespDto;
import com.assetMarket.utils.CommonUtil;
import com.assetMarket.view.DrawableEditText;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BPBuyRequestFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.searchCardEt)
    DrawableEditText mSearchCardEt;
    @BindView(R.id.emptyView)
    QMUIEmptyView mEmptyView;
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_buy_request, null);
        ButterKnife.bind(this, root);
        init();
        return root;
    }

    private void init() {
        initUI();
        setListener();
    }

    private void setListener() {
        mSearchCardEt.setOnDrawableClickListener(new DrawableEditText.OnDrawableClickListener() {
            @Override
            public void onDrawableClick() {
                String input = mSearchCardEt.getText().toString();
                if(CommonUtil.isNull(input)){return;}
                mSearchCardEt.clearFocus();
                searchCard(input);
            }
        });

        mSearchCardEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String input = mSearchCardEt.getText().toString();
                if(CommonUtil.isNull(input)){return false;}
                searchCard(input);
                mSearchCardEt.clearFocus();
                return true;
            }
        });

    }

    private void searchCard(String input) {
        AssetService assetService = RetrofitFactory.getInstance().getRetrofit().create(AssetService.class);
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("assetName",input);
        paramsMap.put("startPage", "1");
        paramsMap.put("pageSize", "100");
        Call<ApiResult<SearchCardRespDto>> call = assetService.searchCard(paramsMap);
        call.enqueue(new Callback<ApiResult<SearchCardRespDto>>() {
            @Override
            public void onResponse(Call<ApiResult<SearchCardRespDto>> call, Response<ApiResult<SearchCardRespDto>> response) {
                ApiResult<SearchCardRespDto> respDto = response.body();
                if(respDto != null){
                    handleSearchCardData(respDto.getData());
                }else {
                    mEmptyView.show(getResources().getString(R.string.emptyView_mode_desc_no_data),null);
                }
            }

            @Override
            public void onFailure(Call<ApiResult<SearchCardRespDto>> call, Throwable t) {
                t.printStackTrace();
                mEmptyView.show(getResources().getString(R.string.emptyView_mode_desc_fail_title), null);
            }
        });
    }

    private void handleSearchCardData(SearchCardRespDto data) {
        if(data != null){
            if(data.getAssets() == null || data.getAssets().size() == 0){
                mEmptyView.show(getResources().getString(R.string.emptyView_mode_desc_no_data), null);
            }else {
                mEmptyView.show(null, null);
            }
        }else {
            mEmptyView.show(getResources().getString(R.string.emptyView_mode_desc_fail_title), null);
            return;
        }

    }

    private void initUI() {
        intiTopBar();
    }

    private void intiTopBar() {
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
