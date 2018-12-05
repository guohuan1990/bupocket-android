package com.bupocket.adaptor;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bupocket.R;
import com.bupocket.enums.CardAdTypeEnum;
import com.bupocket.http.api.dto.resp.GetCardAdDataRespDto;
import com.bupocket.utils.AddressUtil;
import com.bupocket.utils.CommonUtil;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButtonDrawable;

import java.util.List;

public class CardAdDatasAdapter extends BaseAdapter {

    private Integer adType;
    private Context mContext;

    public GetCardAdDataRespDto.PageBean getPage() {
        return page;
    }

    public void setPage(GetCardAdDataRespDto.PageBean page) {
        this.page = page;
    }

    private GetCardAdDataRespDto.PageBean page;
    private List<GetCardAdDataRespDto.AdvertListBean> advertList;

    public CardAdDatasAdapter(List<GetCardAdDataRespDto.AdvertListBean> datas, Context mContext) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
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
            holder.mCardAdPublisherAddressTv = convertView.findViewById(R.id.cardAdPublisherAddressTv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        GetCardAdDataRespDto.AdvertListBean itemData = advertList.get(position);
        holder.mCardAdTitleTv.setText(itemData.getAdvertTitle());
        holder.mCardAdPriceTv.setText(itemData.getPrice());
        holder.mCardAdPriceTv.setText(itemData.getPrice() + " " + itemData.getCoin());
        if ("".equals(itemData.getIssuer().getPhoto())) {
            holder.mCardAdIssuerAuthTv.setVisibility(View.GONE);
            holder.mCardAdIssuerAvatarIv.setImageResource(R.mipmap.avatar);
        } else {
            holder.mCardAdIssuerAuthTv.setVisibility(View.VISIBLE);
            if(CommonUtil.isNull(itemData.getIssuer().getPhoto())){
                holder.mCardAdIssuerAvatarIv.setImageDrawable(convertView.getResources().getDrawable(R.mipmap.avatar));
            }else {
                try {
                    holder.mCardAdIssuerAvatarIv.setImageBitmap(CommonUtil.base64ToBitmap(itemData.getIssuer().getPhoto()));
                } catch (Exception e) {
                    holder.mCardAdIssuerAvatarIv.setImageDrawable(convertView.getResources().getDrawable(R.mipmap.avatar));
                }
            }
        }

        String stockStr = "";
        if(CardAdTypeEnum.BUY.getCode().equals(adType)){
            holder.mCardAdBuyOrSellBtn.setText(R.string.card_package_sell_btn_txt);
            QMUIRoundButtonDrawable adOptBtnDrawable = ((QMUIRoundButtonDrawable)holder.mCardAdBuyOrSellBtn.getBackground());
            adOptBtnDrawable.setBgData(ColorStateList.valueOf(0xFFFF6E3C));
            adOptBtnDrawable.setStrokeData(1, ColorStateList.valueOf(0xFFFF6E3C));
            stockStr = convertView.getResources().getString(R.string.card_package_sell_item_stock_txt);
            stockStr = String.format(stockStr, itemData.getStockQuantity());

        } else if (CardAdTypeEnum.SELL.getCode().equals(adType)) {
            holder.mCardAdBuyOrSellBtn.setText(R.string.card_package_buy_btn_txt);
            QMUIRoundButtonDrawable adOptBtnDrawable = ((QMUIRoundButtonDrawable)holder.mCardAdBuyOrSellBtn.getBackground());
            adOptBtnDrawable.setBgData(ColorStateList.valueOf(0xFF02CA71));
            adOptBtnDrawable.setStrokeData(1, ColorStateList.valueOf(0xFF02CA71));
            stockStr = convertView.getResources().getString(R.string.card_package_buy_item_stock_txt);
            stockStr = String.format(stockStr, itemData.getStockQuantity());
        }
        holder.mCardAdStockQuantityTv.setText(stockStr);
        holder.mCardAdIssuerNameTv.setText(itemData.getIssuer().getName());
        holder.mCardAdPublisherAddressTv.setText(AddressUtil.anonymous(itemData.getPublishAddress()));

        holder.mCardAdBuyOrSellBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemOptBtnListener.onClick(position);
            }
        });
        holder.mCardAdBuyOrSellBtn.setTag(position);

        return convertView;
    }

    public interface OnItemOptBtnListener {
        void onClick(int i);
    }
    private OnItemOptBtnListener onItemOptBtnListener;

    public void setOnItemOptBtnListener(OnItemOptBtnListener onItemOptBtnListener) {
        this.onItemOptBtnListener = onItemOptBtnListener;
    }

    class ViewHolder {
        private TextView mCardAdTitleTv;
        private TextView mCardAdPriceTv;
        private TextView mCardAdStockQuantityTv;
        private QMUIRoundButton mCardAdBuyOrSellBtn;
        private QMUIRadiusImageView mCardAdIssuerAvatarIv;
        private TextView mCardAdIssuerNameTv;
        private TextView mCardAdIssuerAuthTv;
        private TextView mCardAdPublisherAddressTv;
    }

    public Integer getAdType() {
        return adType;
    }

    public void setAdType(Integer adType) {
        this.adType = adType;
    }
}
