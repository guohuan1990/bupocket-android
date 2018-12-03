package com.bupocket.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bupocket.R;
import com.bupocket.http.api.dto.resp.GetCardMyAssetsRespDto;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;

import java.util.List;

public class CardMyAssetsAdapter extends BaseAdapter {
    private List<GetCardMyAssetsRespDto.MyAssetsBean> myAssetsBeanList;
    private Context mContext;
    private GetCardMyAssetsRespDto.PageBean page;

    public CardMyAssetsAdapter(List<GetCardMyAssetsRespDto.MyAssetsBean> myAssetsBeanList, Context mContext){
        this.myAssetsBeanList = myAssetsBeanList;
        this.mContext = mContext;
    }
    @Override
    public int getCount() {
        return myAssetsBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return myAssetsBeanList.get(position);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.card_mine_item, null);
            holder.myCardIssuerNameTv = convertView.findViewById(R.id.myCardIssuerNameTv);
            holder.myCardIssuerLogoIv = convertView.findViewById(R.id.myCardIssuerLogoIv);
            holder.myCardNameTv = convertView.findViewById(R.id.myCardNameTv);
            holder.myCardAmountTv = convertView.findViewById(R.id.myCardAmountTv);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        if(myAssetsBeanList.size() != 0){
            GetCardMyAssetsRespDto.MyAssetsBean myAssetsBean = myAssetsBeanList.get(position);
            holder.myCardIssuerNameTv.setText(myAssetsBean.getIssuer().getName());
            holder.myCardNameTv.setText(myAssetsBean.getAssetInfo().getName());
            holder.myCardAmountTv.setText(String.format(convertView.getResources().getString(R.string.card_package_mine_card_amount_txt),myAssetsBean.getAssetInfo().getMyAssetQty()));
            holder.myCardIssuerLogoIv.setBackgroundResource(R.mipmap.icon_card_details_logo);
        }
        return convertView;
    }

    public GetCardMyAssetsRespDto.PageBean getPage() {
        return page;
    }

    public void setPage(GetCardMyAssetsRespDto.PageBean page) {
        this.page = page;
    }

    class ViewHolder{
        private QMUIRadiusImageView myCardIssuerLogoIv;
        private TextView myCardIssuerNameTv;
        private TextView myCardNameTv;
        private TextView myCardAmountTv;
    }
}
