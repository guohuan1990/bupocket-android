package com.assetMarket.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.assetMarket.R;
import com.assetMarket.http.api.dto.resp.GetCardDetailsDto;
import com.assetMarket.utils.CommonUtil;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.util.List;

public class CardDetailAskAdapter extends BaseAdapter {
    private List<GetCardDetailsDto.BuyRequestBean> buyRequest;
    private Context mContext;

    public CardDetailAskAdapter(List<GetCardDetailsDto.BuyRequestBean> buyRequest,Context mContext){
        this.buyRequest = buyRequest;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return buyRequest.size();
    }

    @Override
    public Object getItem(int position) {
        return buyRequest.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.card_details_ask_item, null);
            holder.cardAdTitleTv = convertView.findViewById(R.id.cardAdTitleTv);
            holder.cardAdPriceTv = convertView.findViewById(R.id.cardAdPriceTv);
            holder.cardAdIssuerAvatarIv = convertView.findViewById(R.id.cardAdIssuerAvatarIv);
            holder.cardAdIssuerNameTv = convertView.findViewById(R.id.cardAdIssuerNameTv);
            holder.cardAdSellBtn = convertView.findViewById(R.id.cardAdSellBtn);
            convertView.setTag(holder);

        }else {
            holder = (ViewHolder)convertView.getTag();
        }

        if(buyRequest.size() != 0){
            GetCardDetailsDto.BuyRequestBean buyRequestBean = buyRequest.get(position);
            holder.cardAdTitleTv.setText(buyRequestBean.getAdTitle());
            holder.cardAdPriceTv.setText(CommonUtil.addSuffix(buyRequestBean.getPrice(),"BU"));
            holder.cardAdIssuerNameTv.setText(buyRequestBean.getIssuer().getName());
            if(CommonUtil.isNull(buyRequestBean.getIssuer().getLogo())){
                holder.cardAdIssuerAvatarIv.setImageDrawable(convertView.getResources().getDrawable(R.mipmap.avatar));
            }else {
                try {
                    holder.cardAdIssuerAvatarIv.setImageBitmap(CommonUtil.base64ToBitmap(buyRequestBean.getIssuer().getLogo()));
                } catch (Exception e) {
                    holder.cardAdIssuerAvatarIv.setImageDrawable(convertView.getResources().getDrawable(R.mipmap.avatar));
                }
            }
        }
        holder.cardAdSellBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemOptBtnListener.onClick(position);
            }
        });
        holder.cardAdSellBtn.setTag(position);

        return convertView;
    }

    public interface OnItemOptBtnListener {
        void onClick(int i);
    }
    private OnItemOptBtnListener onItemOptBtnListener;

    public void setOnItemOptBtnListener(OnItemOptBtnListener onItemOptBtnListener) {
        this.onItemOptBtnListener = onItemOptBtnListener;
    }

    class ViewHolder{
        private TextView cardAdTitleTv;
        private TextView cardAdPriceTv;
        private QMUIRadiusImageView cardAdIssuerAvatarIv;
        private TextView cardAdIssuerNameTv;
        private QMUIRoundButton cardAdSellBtn;
    }
}
