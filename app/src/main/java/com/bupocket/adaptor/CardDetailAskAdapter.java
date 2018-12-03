package com.bupocket.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bupocket.R;
import com.bupocket.http.api.dto.resp.GetCardDetailsDto;
import com.bupocket.utils.CommonUtil;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;

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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.card_details_ask_item, null);
            holder.cardAdTitleTv = convertView.findViewById(R.id.cardAdTitleTv);
            holder.cardAdPriceTv = convertView.findViewById(R.id.cardAdPriceTv);
            holder.cardAdIssuerAvatarIv = convertView.findViewById(R.id.cardAdIssuerAvatarIv);
            holder.cardAdIssuerNameTv = convertView.findViewById(R.id.cardAdIssuerNameTv);
            convertView.setTag(holder);
            if(buyRequest.size() != 0){
                GetCardDetailsDto.BuyRequestBean buyRequestBean = buyRequest.get(position);
                holder.cardAdTitleTv.setText(buyRequestBean.getAdTitle());
                holder.cardAdPriceTv.setText(CommonUtil.addSuffix(buyRequestBean.getPrice(),"BU"));
                holder.cardAdIssuerAvatarIv.setBackgroundResource(R.mipmap.icon_card_details_logo);
                holder.cardAdIssuerNameTv.setText(buyRequestBean.getIssuer().getName());
            }

        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        return convertView;
    }

    class ViewHolder{
        private TextView cardAdTitleTv;
        private TextView cardAdPriceTv;
        private QMUIRadiusImageView cardAdIssuerAvatarIv;
        private TextView cardAdIssuerNameTv;
    }
}
