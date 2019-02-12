package com.bupocket.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.bupocket.R;
import com.bupocket.adaptor.AddressAdapter;
import com.bupocket.base.BaseFragment;
import com.bupocket.http.api.AddressBookService;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.GetAddressBookRespDto;
import com.bupocket.utils.SharedPreferencesHelper;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
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

public class BPAddressBookFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.addressEv)
    QMUIEmptyView mAddressEv;
    @BindView(R.id.recentlyTxRecordEmptyLL)
    LinearLayout mRecentlyTxRecordEmptyLL;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    private SharedPreferencesHelper sharedPreferencesHelper;
    private String identityAddress;
    private String pageSize = "10";
    private Integer pageStart = 1;
    private AddressAdapter addressAdapter;
    private GetAddressBookRespDto.PageBean page;

    private List<GetAddressBookRespDto.AddressBookListBean> addressBookListBeanList = new ArrayList<>();

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_address_book, null);
        ButterKnife.bind(this, root);
        init();
        return root;
    }

    private void init() {
        initData();
        initUI();
    }

    private void initData() {
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        identityAddress = sharedPreferencesHelper.getSharedPreference("identityId","").toString();
    }

    private void initUI() {
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        initTopBar();
        initListView();
    }

    private void initListView() {
        mAddressEv.show(true);
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                refreshlayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshData();
                        refreshlayout.finishRefresh();
                        refreshLayout.setNoMoreData(false);
                        initData();
                    }
                }, 500);

            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(final RefreshLayout refreshlayout) {
                refreshlayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(addressAdapter == null){
                            refreshlayout.finishRefresh();
                            return;
                        }

                        loadMoreData();
                        refreshlayout.finishLoadMore(500);

                        if(!page.isNextFlag()){
                            refreshLayout.finishLoadMoreWithNoMoreData();
                        }
                    }
                }, 500);
            }
        });
    }

    private void refreshData() {
        pageStart = 1;
        addressBookListBeanList.clear();
        loadAddressList();
    }

    private void loadMoreData() {
        if(page.isNextFlag()){
            pageStart ++;
            loadAddressList();
        }
    }

    private void loadAddressList() {
        AddressBookService addressBookService = RetrofitFactory.getInstance().getRetrofit().create(AddressBookService.class);
        Call<ApiResult<GetAddressBookRespDto>> call;
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("identityAddress",identityAddress);
        paramsMap.put("startPage",pageStart);
        paramsMap.put("pageSize",pageSize);
        call = addressBookService.getMyAddressBook(paramsMap);
        call.enqueue(new Callback<ApiResult<GetAddressBookRespDto>>() {
            @Override
            public void onResponse(Call<ApiResult<GetAddressBookRespDto>> call, Response<ApiResult<GetAddressBookRespDto>> response) {
                ApiResult<GetAddressBookRespDto> respDto = response.body();
                if(null != respDto && null != respDto.getData()){
                    mAddressEv.show(null,null);
                    if(isAdded()){
                        handleAddressList(respDto.getData());
                    }
                }else{
                    mAddressEv.show(getResources().getString(R.string.emptyView_mode_desc_fail_title), null);
                }
            }

            @Override
            public void onFailure(Call<ApiResult<GetAddressBookRespDto>> call, Throwable t) {
                t.printStackTrace();
                if(isAdded()){
                    mAddressEv.show(getResources().getString(R.string.emptyView_mode_desc_fail_title), null);
                }
            }
        });
    }

    private void handleAddressList(GetAddressBookRespDto getAddressBookRespDto) {
        page = getAddressBookRespDto.getPage();
        addressBookListBeanList = getAddressBookRespDto.getAddressBookList();


    }

    private void initTopBar() {
        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
        mTopBar.addRightImageButton(R.mipmap.icon_import_wallet,R.id.topbar_right_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new BPAddressAddFragment());
            }
        });
    }
}
