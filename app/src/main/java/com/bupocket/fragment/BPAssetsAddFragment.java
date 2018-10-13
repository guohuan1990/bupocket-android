package com.bupocket.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.alibaba.fastjson.JSON;
import com.bupocket.R;
import com.bupocket.adaptor.SearchTokenAdapter;
import com.bupocket.base.BaseFragment;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.TokenService;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.SearchTokenRespDto;
import com.bupocket.utils.CommonUtil;
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
    EditText mSearchTokenEt;

    @BindView(R.id.searchTokenBtn)
    ImageView mSearchTokenBtn;


    SearchTokenAdapter searchTokenAdapter;



    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_assets_add, null);
        ButterKnife.bind(this, root);
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        initTopBar();

        mSearchTokenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = mSearchTokenEt.getText().toString();
                if(CommonUtil.isNull(input)){return;}
                searchToken(input);
            }
        });

        return root;
    }


    private void searchToken(String assetCode){
        TokenService tokenService = RetrofitFactory.getInstance().getRetrofit().create(TokenService.class);
        Map<String, Object> parmasMap = new HashMap<>();
        parmasMap.put("assetCode",assetCode);
        parmasMap.put("startPage", 1);
        parmasMap.put("pageSize", 100);
        Call<ApiResult<SearchTokenRespDto>> call = tokenService.queryTokens(parmasMap);
        call.enqueue(new Callback<ApiResult<SearchTokenRespDto>>() {
            @Override
            public void onResponse(Call<ApiResult<SearchTokenRespDto>> call, Response<ApiResult<SearchTokenRespDto>> response) {
                ApiResult<SearchTokenRespDto> respDto = response.body();
                handleSearchTokenData(respDto.getData());
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
        mSearchTokenListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            SearchTokenRespDto.TokenListBean currentItem = (SearchTokenRespDto.TokenListBean) searchTokenAdapter.getItem(position);
            Toast.makeText(getContext(),JSON.toJSONString(currentItem),Toast.LENGTH_LONG).show();
            }
        });
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
