package com.bupocket.adaptor;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.bupocket.R;
import com.bupocket.enums.PaymentTypeEnum;
import com.bupocket.model.AdInfo;

import java.util.Arrays;
import java.util.List;

public class OTCAdDataAdapter extends BaseAdapter {
    private List<AdInfo> datas;
    private Context mContext;

    public OTCAdDataAdapter(List<AdInfo> datas, Context mContext) {
        this.datas = datas;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int i) {
        return datas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        OTCAdDataAdapter.ViewHolder holder;
        if(convertView == null){
            holder = new OTCAdDataAdapter.ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.otc_token_ad_item_list_layout, null);
            holder.userNickTV = convertView.findViewById(R.id.otc_ad_user_nick);
            holder.maxValueTV = convertView.findViewById(R.id.otc_ad_max_value);
            holder.minValueTV = convertView.findViewById(R.id.otc_ad_min_value);
            holder.txSizeTV = convertView.findViewById(R.id.otc_ad_transaction_size);
            holder.soldValueTV = convertView.findViewById(R.id.otc_ad_sold_value);
            holder.amountTV = convertView.findViewById(R.id.otc_ad_sold_amount);
            holder.paymentTypeAlipayTV = convertView.findViewById(R.id.otc_payment_type_alipay);
            holder.paymentTypeWechatTV = convertView.findViewById(R.id.otc_payment_type_wechat);
            holder.paymentTypeBankCardTV = convertView.findViewById(R.id.otc_payment_type_bankcard);
            convertView.setTag(holder);
        } else {
            holder = (OTCAdDataAdapter.ViewHolder) convertView.getTag();
        }
        holder.userNickTV.setText(datas.get(i).getUserNick());
        holder.maxValueTV.setText(datas.get(i).getMaxValue());
        holder.minValueTV.setText(datas.get(i).getMinValue());
        holder.txSizeTV.setText(datas.get(i).getTransactionSize());
        holder.soldValueTV.setText(datas.get(i).getSoldValue());
        holder.amountTV.setText(datas.get(i).getAmount());
        String []paymentTypeArr = datas.get(i).getPaymentTypeArr();
        if(isContains(PaymentTypeEnum.AILPAY.getCode(), paymentTypeArr)){
            holder.paymentTypeAlipayTV.setVisibility(View.VISIBLE);
        }
        if(isContains(PaymentTypeEnum.WECHAT.getCode(), paymentTypeArr)){
            holder.paymentTypeWechatTV.setVisibility(View.VISIBLE);
        }
        if(isContains(PaymentTypeEnum.BANKCARD.getCode(), paymentTypeArr)){
            holder.paymentTypeBankCardTV.setVisibility(View.VISIBLE);
        }
        return convertView;
    }


    private Boolean isContains(String str,String []srcArr){
        List<String> datas = Arrays.asList(srcArr);
        if(datas.contains(str)){
            return true;
        }else {
            return false;
        }
    }

    class ViewHolder {
        private TextView userNickTV;
        private TextView minValueTV;
        private TextView maxValueTV;
        private TextView txSizeTV;
        private TextView soldValueTV;
        private TextView amountTV;
        private TextView paymentTypeAlipayTV;
        private TextView paymentTypeWechatTV;
        private TextView paymentTypeBankCardTV;
    }

    @Nullable
    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }
}
