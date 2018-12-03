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

import java.util.List;

public class CardDetailMySellAdapter extends BaseAdapter {
    private List<GetCardDetailsDto.MySaleBean> mySale;
    private Context mContext;

    public CardDetailMySellAdapter(List<GetCardDetailsDto.MySaleBean> mySale, Context mContext){
        this.mySale = mySale;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mySale.size();
    }

    @Override
    public Object getItem(int position) {
        return mySale.get(position);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.card_details_my_sell_item, null);
            holder.adTitleTv = convertView.findViewById(R.id.adTitleTv);
            holder.priceTv = convertView.findViewById(R.id.priceTv);
            holder.repertoryInfoTv = convertView.findViewById(R.id.repertoryInfoTv);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }

        if(mySale.size() != 0){
            GetCardDetailsDto.MySaleBean mySaleBean = mySale.get(position);
            holder.adTitleTv.setText(mySaleBean.getAdTitle());
            holder.priceTv.setText(CommonUtil.addSuffix(mySaleBean.getPrice(),"BU"));
            String soldAmount = mySaleBean.getSelledAmount();
            String saleTotal = mySaleBean.getSaleTotal();
            holder.repertoryInfoTv.setText(convertView.getContext().getString(R.string.sold_txt) + " " + soldAmount + "/" + saleTotal);

        }
        return convertView;
    }

    class ViewHolder{
        private TextView adTitleTv;
        private TextView priceTv;
        private TextView repertoryInfoTv;
    }
}
