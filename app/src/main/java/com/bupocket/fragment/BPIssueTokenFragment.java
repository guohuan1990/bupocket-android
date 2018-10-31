package com.bupocket.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bupocket.BPApplication;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.enums.AssetTypeEnum;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.TokenService;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.GetTokenDetailRespDto;
import com.bupocket.http.api.dto.resp.GetTokensRespDto;
import com.bupocket.model.IssueTokenInfo;
import com.bupocket.utils.SharedPreferencesHelper;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BPIssueTokenFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.tokenCodeTv)
    TextView mTokenCodeTv;
    @BindView(R.id.issueTypeTv)
    TextView mIssueTypeTv;
    @BindView(R.id.thisTimeIssueAmountTv)
    TextView mThisTimeIssueAmountTv;
    @BindView(R.id.totalIssueAmountTv)
    TextView mTotalIssueAmountTv;
    @BindView(R.id.issueFeeTv)
    TextView mIssueFeeTv;
    @BindView(R.id.accumulativeIssueAmountTv)
    TextView mAccumulativeIssueAmountTv;

    private Socket mSocket;
    protected SharedPreferencesHelper sharedPreferencesHelper;
    String assetCode;
    String issueAddress;
    String issueAmount;
    String actualSupply;
    String assetName;
    String decimals;
    String tokenDescription;
    String tokenType;
    String totalSupply;
    String version;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_issue_token_confirm, null);
        ButterKnife.bind(this, root);
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        initTopbar();
        initData();
        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        final String uuID = bundle.getString("uuID");

        BPApplication application = (BPApplication)getActivity().getApplication();
        mSocket = application.getSocket();
        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                System.out.println("socket id :" + mSocket.id());
                mSocket.emit("token.register.join",uuID);
//                mSocket.disconnect();
            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {}

        });
        mSocket.emit("token.register.scanSuccess","");
        mSocket.connect();
    }

    private void initData() {
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        Bundle bundle = getArguments();
        String tokenData = bundle.getString("tokenData");
        IssueTokenInfo issueTokenInfo = IssueTokenInfo.objectFromData(tokenData);
        assetCode = issueTokenInfo.getCode();
        issueAmount = issueTokenInfo.getAmount();
        issueAddress = sharedPreferencesHelper.getSharedPreference("currentAccAddr", "").toString();

        TokenService tokenService = RetrofitFactory.getInstance().getRetrofit().create(TokenService.class);
        Map<String, Object> parmasMap = new HashMap<>();
        parmasMap.put("assetCode",assetCode);
        parmasMap.put("issueAddress",issueAddress);
        Call<ApiResult<GetTokenDetailRespDto>> call = tokenService.getTokenDetail(parmasMap);
        call.enqueue(new Callback<ApiResult<GetTokenDetailRespDto>>() {
            @Override
            public void onResponse(Call<ApiResult<GetTokenDetailRespDto>> call, Response<ApiResult<GetTokenDetailRespDto>> response) {
                ApiResult<GetTokenDetailRespDto> respDto = response.body();
                String errorCode = respDto.getErrCode();
                if(errorCode.equals("500004")){

                }else{
                    GetTokenDetailRespDto.DataBean tokenDetail = respDto.getData().getData();
                    tokenType = tokenDetail.getTokenType();

                    actualSupply = tokenDetail.getActualSupply();
                    assetName = tokenDetail.getAssetName();
                    decimals = tokenDetail.getDecimals();
                    tokenDescription = tokenDetail.getTokenDescription();
                    totalSupply = tokenDetail.getTotalSupply();
                    version = tokenDetail.getVersion();

                    mTokenCodeTv.setText(assetCode);
                    mThisTimeIssueAmountTv.setText(issueAmount);
                    mIssueFeeTv.setText("51 BU");

                    if(tokenType.equals(AssetTypeEnum.ATP_FIXED.getCode())){
                        mIssueTypeTv.setText(getString(R.string.issue_type_disposable_txt));
                        mTotalIssueAmountTv.setText(totalSupply);
                    }else if(tokenType.equals(AssetTypeEnum.ATP_ADD.getCode())){
                        mIssueTypeTv.setText(getString(R.string.issue_type_increment_txt));
                        mTotalIssueAmountTv.setText(totalSupply);
                        mAccumulativeIssueAmountTv.setText(actualSupply);
                    }else if(tokenType.equals(AssetTypeEnum.ATP_INFINITE.getCode())){
                        mIssueTypeTv.setText(getString(R.string.issue_type_unlimited_txt));
                        mAccumulativeIssueAmountTv.setText(actualSupply);
                    }

                }
            }

            @Override
            public void onFailure(Call<ApiResult<GetTokenDetailRespDto>> call, Throwable t) {
                Toast.makeText(getActivity(), R.string.network_error_msg, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initTopbar() {
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QMUIStatusBarHelper.setStatusBarDarkMode(getBaseFragmentActivity());
                popBackStack();
            }
        });
        mTopBar.setTitle(getString(R.string.issue_token_page_title));
    }
}
