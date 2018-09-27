package com.bupocket.fragment;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bupocket.R;
import com.bupocket.adaptor.TxDetailSignatureAdapter;
import com.bupocket.base.BaseFragment;
import com.bupocket.enums.OutinTypeEnum;
import com.bupocket.enums.TxStatusEnum;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.TxService;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.TxDetailRespDto;
import com.bupocket.utils.TimeUtil;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BPAssetsTxDetailFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;

    @BindView(R.id.txStatusIcon)
    QMUIRadiusImageView mTxStatusIcon;
    @BindView(R.id.sendAmountTv)
    TextView mSendAmountTv;
    @BindView(R.id.txStatusTv)
    TextView mTxStatusTv;
    @BindView(R.id.txFromAccAddrTv)
    TextView mTxFromAccAddrTv;
    @BindView(R.id.txToAccAddrTv)
    TextView mTxToAccAddrTv;
    @BindView(R.id.txDetailFeeTv)
    TextView mTxDetailFeeTv;
    @BindView(R.id.txDetailSendDateTv)
    TextView mTxDetailSendDateTv;
    @BindView(R.id.txDetailNoteTv)
    TextView mTxDetailNoteTv;
    @BindView(R.id.txDetailTXHashTv)
    TextView mTxDetailTXHashTv;
    @BindView(R.id.txDetailTxInfoSourceAddressTv)
    TextView mTxDetailTxInfoSourceAddressTv;
    @BindView(R.id.txDetailTxInfoDestAddressTv)
    TextView mTxDetailTxInfoDestAddressTv;
    @BindView(R.id.txDetailTxInfoAmountTv)
    TextView mTxDetailTxInfoAmountTv;
    @BindView(R.id.txDetailTxInfoTXFeeTv)
    TextView mTxDetailTxInfoTXFeeTv;
    @BindView(R.id.txDetailTxInfoNonceTv)
    TextView mTxDetailTxInfoNonceTv;
    @BindView(R.id.txDetailTxInfoLedgerSeqTv)
    TextView mTxDetailTxInfoLedgerSeqTv;
    @BindView(R.id.txDetailTxInfoTxSignaturePkTv)
    TextView mTxDetailTxInfoTxSignaturePkTv;
    @BindView(R.id.txDetailTxInfoTxSignatureSdTv)
    TextView mTxDetailTxInfoTxSignatureSdTv;
    @BindView(R.id.txDetailBlockInfoBlockHeightTv)
    TextView mTxDetailBlockInfoBlockHeightTv;
    @BindView(R.id.txDetailBlockInfoBlockHashTv)
    TextView mTxDetailBlockInfoBlockHashTv;
    @BindView(R.id.txDetailBlockInfoPrevBlockHashTv)
    TextView mTxDetailBlockInfoPrevBlockHashTv;
    @BindView(R.id.txDetailBlockInfoTXCountTv)
    TextView mTxDetailBlockInfoTXCountTv;
    @BindView(R.id.txDetailBlockInfoConsensusTimeTv)
    TextView mTxDetailBlockInfoConsensusTimeTv;
    @BindView(R.id.emptyView)
    QMUIEmptyView mEmptyView;

    @BindView(R.id.txDetailLl)
    LinearLayout mTxDetailLl;

    @BindView(R.id.txDetailSignatureItemLv)
    ListView mTxDetailSignatureItemLv;

    TxDetailSignatureAdapter txDetailSignatureAdapter;

    private String txHash;
    private Integer outinType;
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_tx_detail, null);
        ButterKnife.bind(this, root);
        initTopBar();
        initData();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initTxDetailView();
            }
        },10);

        return root;
    }

    private void initData(){
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        mEmptyView.show(true);
        txHash = getTxHash();
        outinType = getArguments().getInt("outinType");
    }


    private String getTxHash(){
        return getArguments().getString("txHash");
    }

    private void initTxDetailView() {
        TxService txService = RetrofitFactory.getInstance().getRetrofit().create(TxService.class);
        Map<String, Object> parmasMap = new HashMap<>();
        parmasMap.put("hash",txHash);
        Call<ApiResult<TxDetailRespDto>> call = txService.getTxDetail(parmasMap);
        call.enqueue(new Callback<ApiResult<TxDetailRespDto>>() {
            @Override
            public void onResponse(Call<ApiResult<TxDetailRespDto>> call, Response<ApiResult<TxDetailRespDto>> response) {

                ApiResult<TxDetailRespDto> respDto = response.body();

                if(respDto.getData() != null){
                    mTxDetailLl.setVisibility(View.VISIBLE);
                    TxDetailRespDto.TxInfoRespBoBean txInfoRespBoBean = respDto.getData().getTxInfoRespBo();
                    TxDetailRespDto.TxDeatilRespBoBean txDeatilRespBoBean = respDto.getData().getTxDeatilRespBo();
                    TxDetailRespDto.BlockInfoRespBoBean blockInfoRespBoBean = respDto.getData().getBlockInfoRespBo();

                    Drawable txStatusIconDrawable;
                    String txStatusStr;
                    if(txDeatilRespBoBean.getStatus().equals(TxStatusEnum.SUCCESS.getCode())){
                        txStatusIconDrawable = ContextCompat.getDrawable(getContext(),R.mipmap.icon_send_success);
                        txStatusStr = getResources().getString(R.string.tx_status_success_txt1);
                    }else{
                        txStatusIconDrawable = ContextCompat.getDrawable(getContext(),R.mipmap.icon_send_fail);
                        txStatusStr = getResources().getString(R.string.tx_status_fail_txt1);
                    }
                    mTxStatusIcon.setImageDrawable(txStatusIconDrawable);
                    mTxStatusTv.setText(txStatusStr);
                    mSendAmountTv.setText((OutinTypeEnum.IN.getCode().equals(outinType) ? "-" : "+") + txInfoRespBoBean.getAmount());
                    mTxFromAccAddrTv.setText(txDeatilRespBoBean.getSourceAddress());
                    mTxToAccAddrTv.setText(txDeatilRespBoBean.getDestAddress());
                    mTxDetailFeeTv.setText(txDeatilRespBoBean.getFee());
                    mTxDetailSendDateTv.setText(TimeUtil.timeStamp2Date(txDeatilRespBoBean.getApplyTimeDate().toString().substring(0,10),"yyyy.MM.dd HH:mm:ss"));
                    mTxDetailTXHashTv.setText(txInfoRespBoBean.getHash());
                    mTxDetailNoteTv.setText(txDeatilRespBoBean.getOriginalMetadata());

                    mTxDetailTxInfoSourceAddressTv.setText(txInfoRespBoBean.getSourceAddress());
                    mTxDetailTxInfoDestAddressTv.setText(txInfoRespBoBean.getDestAddress());
                    mTxDetailTxInfoAmountTv.setText(txInfoRespBoBean.getAmount() + " BU");
                    mTxDetailTxInfoTXFeeTv.setText(txInfoRespBoBean.getFee() + " BU");
                    mTxDetailTxInfoNonceTv.setText(txInfoRespBoBean.getNonce() + "");
                    mTxDetailTxInfoLedgerSeqTv.setText(txInfoRespBoBean.getLedgerSeq() + "");

                    String signatureStr = txInfoRespBoBean.getSignatureStr();
                    JSONArray signatureArr = JSON.parseArray(signatureStr);
                    JSONObject signatureObj = null;
                    List<TxDetailSignatureAdapter.Signature> signatures = new ArrayList<>();
                    TxDetailSignatureAdapter.Signature signature = null;
                    for (int i = 0; i < signatureArr.size(); i++) {
                        signatureObj = JSON.parseObject(signatureArr.getString(i));
                        signature = new TxDetailSignatureAdapter.Signature();
                        signature.setPublicKey(signatureObj.getString("publicKey"));
                        signature.setSignData(signatureObj.getString("signData"));
                        signatures.add(signature);
                    }
                    txDetailSignatureAdapter = new TxDetailSignatureAdapter(signatures, getContext());
                    mTxDetailSignatureItemLv.setAdapter(txDetailSignatureAdapter);


//                    mTxDetailTxInfoTxSignaturePkTv.setText(signatureObj.getString("publicKey"));
//                    mTxDetailTxInfoTxSignatureSdTv.setText(signatureObj.getString("signData"));

                    mTxDetailBlockInfoBlockHeightTv.setText(blockInfoRespBoBean.getSeq() + "");
                    mTxDetailBlockInfoBlockHashTv.setText(blockInfoRespBoBean.getHash());
                    mTxDetailBlockInfoPrevBlockHashTv.setText(blockInfoRespBoBean.getPreviousHash());
                    mTxDetailBlockInfoTXCountTv.setText(blockInfoRespBoBean.getTxCount() + "");
                    mTxDetailBlockInfoConsensusTimeTv.setText(TimeUtil.timeStamp2Date(blockInfoRespBoBean.getCloseTimeDate().toString().substring(0,10),"yyyy.MM.dd HH:mm:ss"));

                }else{
                    mEmptyView.show(getResources().getString(R.string.emptyView_mode_desc_fail_title), null);
                }
            }

            @Override
            public void onFailure(Call<ApiResult<TxDetailRespDto>> call, Throwable t) {
                t.printStackTrace();
                mEmptyView.show(getResources().getString(R.string.emptyView_mode_desc_fail_title), null);
            }
        });
    }

    private void initTopBar() {
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
        mTopBar.setTitle(getResources().getString(R.string.fragment_title_tx_detail_txt));
    }

    @Override
    public TransitionConfig onFetchTransitionConfig() {
        return SCALE_TRANSITION_CONFIG;
    }
}
