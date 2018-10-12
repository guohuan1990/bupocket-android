package com.bupocket.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bupocket.R;
import com.bupocket.model.TokenInfo;
import com.bupocket.utils.CommonUtil;

import java.util.List;

import butterknife.BindView;
import io.bumo.crypto.protobuf.Common;

public class TokensAdapter extends BaseAdapter {
    private List<TokenInfo> datas;
    private Context mContext;
    String mPrefixTokenPrice = "≈￥";

    public TokensAdapter(List<TokenInfo> datas, Context mContext) {
        this.datas = datas;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.my_token_item_layout, null);
            holder.assetCodeTv = convertView.findViewById(R.id.assetCodeTv);
            holder.amountTv = convertView.findViewById(R.id.amountTv);
            holder.assetIconIv = convertView.findViewById(R.id.assetIconIv);
            holder.priceTv = convertView.findViewById(R.id.priceTv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.assetCodeTv.setText(datas.get(position).getAssetCode());
        if(!datas.get(position).getPrice().equals("~")){
            holder.priceTv.setText(mPrefixTokenPrice + CommonUtil.formatDouble(datas.get(position).getPrice()));
        }else{
            holder.priceTv.setText(datas.get(position).getPrice());
        }
        holder.amountTv.setText(CommonUtil.formatDouble(datas.get(position).getAmount()));
        if(datas.get(position).getIcon() == null){
            holder.assetIconIv.setBackgroundResource(R.mipmap.icon_token_default_icon);
        }else{
            holder.assetIconIv.setImageBitmap(CommonUtil.base64ToBitmap(datas.get(position).getIcon()));
        }
        return convertView;
    }

    class ViewHolder{
        private TextView assetCodeTv;
        private ImageView assetIconIv;
        private TextView amountTv;
        private TextView priceTv;
    }
}
