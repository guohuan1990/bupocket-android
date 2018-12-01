package com.bupocket.adaptor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bupocket.R;
import com.bupocket.enums.AdTypeEnum;
import com.bupocket.http.api.dto.resp.GetAdDatasRespDto;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.util.List;

public class CardAdDatasAdapter extends BaseAdapter {

    private Integer adType;
    private Context mContext;
    private GetAdDatasRespDto.PageBean page;
    private List<GetAdDatasRespDto.AdvertListBean> advertList;

    public CardAdDatasAdapter(List<GetAdDatasRespDto.AdvertListBean> datas, Context mContext) {
        this.advertList = datas;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return advertList.size();
    }

    @Override
    public Object getItem(int position) {
        return advertList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.card_ad_item_layout, null);
            holder.mCardAdTitleTv = convertView.findViewById(R.id.cardAdTitleTv);
            holder.mCardAdPriceTv = convertView.findViewById(R.id.cardAdPriceTv);
            holder.mCardAdStockQuantityTv = convertView.findViewById(R.id.cardAdStockQuantityTv);
            holder.mCardAdBuyOrSellBtn = convertView.findViewById(R.id.cardAdBuyOrSellBtn);
            holder.mCardAdIssuerAvatarIv = convertView.findViewById(R.id.cardAdIssuerAvatarIv);
            holder.mCardAdIssuerNameTv = convertView.findViewById(R.id.cardAdIssuerNameTv);
            holder.mCardAdIssuerAuthTv = convertView.findViewById(R.id.cardAdIssuerAuthTv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        GetAdDatasRespDto.AdvertListBean itemData = advertList.get(position);
        holder.mCardAdTitleTv.setText(itemData.getAdvertTitle());
        holder.mCardAdPriceTv.setText(itemData.getPrice());
        holder.mCardAdPriceTv.setText(itemData.getPrice());
        if ("".equals(itemData.getIssuer().getPhoto())) {
            holder.mCardAdIssuerAuthTv.setVisibility(View.GONE);
            holder.mCardAdIssuerAvatarIv.setImageResource(R.mipmap.avatar);
        } else {
            holder.mCardAdIssuerAuthTv.setVisibility(View.VISIBLE);
            byte[] decodedString = Base64.decode(itemData.getIssuer().getPhoto(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.mCardAdIssuerAvatarIv.setImageBitmap(decodedByte);
        }

        String stockStr = "";
        if(AdTypeEnum.BUY.getCode().equals(adType)){
            holder.mCardAdBuyOrSellBtn.setText(R.string.card_package_sell_btn_txt);
            holder.mCardAdBuyOrSellBtn.setBackgroundColor(convertView.getResources().getColor(R.color.card_ad_sell_btn_bg));
            stockStr = convertView.getResources().getString(R.string.card_package_sell_item_stock_txt);
            stockStr = String.format(stockStr, Integer.parseInt(itemData.getStockQuantity()));

        } else if (AdTypeEnum.SELL.getCode().equals(adType)) {
            holder.mCardAdBuyOrSellBtn.setText(R.string.card_package_buy_btn_txt);
            holder.mCardAdBuyOrSellBtn.setBackgroundColor(convertView.getResources().getColor(R.color.app_color_green));
            stockStr = convertView.getResources().getString(R.string.card_package_buy_item_stock_txt);
            stockStr = String.format(stockStr, Integer.parseInt(itemData.getStockQuantity()));
        }
        holder.mCardAdStockQuantityTv.setText(stockStr);
        holder.mCardAdIssuerNameTv.setText(itemData.getIssuer().getName());
        return convertView;
    }

    class ViewHolder {
        private TextView mCardAdTitleTv;
        private TextView mCardAdPriceTv;
        private TextView mCardAdStockQuantityTv;
        private QMUIRoundButton mCardAdBuyOrSellBtn;
        private QMUIRadiusImageView mCardAdIssuerAvatarIv;
        private TextView mCardAdIssuerNameTv;
        private TextView mCardAdIssuerAuthTv;
    }

    public Integer getAdType() {
        return adType;
    }

    public void setAdType(Integer adType) {
        this.adType = adType;
    }
}
