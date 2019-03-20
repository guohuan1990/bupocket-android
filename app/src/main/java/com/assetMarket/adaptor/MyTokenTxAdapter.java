package com.assetMarket.adaptor;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.assetMarket.enums.OutinTypeEnum;
import com.assetMarket.enums.TxStatusEnum;
import com.assetMarket.http.api.dto.resp.GetMyTxsRespDto;
import com.assetMarket.model.TokenTxInfo;
import com.assetMarket.R;

import java.util.List;

public class MyTokenTxAdapter extends BaseAdapter {
    private List<TokenTxInfo> datas;
    private GetMyTxsRespDto.PageBean page;
    private Context mContext;

    public MyTokenTxAdapter(List<TokenTxInfo> datas, Context mContext) {
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.my_token_tx_item_layout, null);
            holder.userAccAddrTV = convertView.findViewById(R.id.tx_account_address);
            holder.txAmountTV = convertView.findViewById(R.id.tx_amount);
            holder.txDateTV = convertView.findViewById(R.id.tx_date);
            holder.txStatusTV = convertView.findViewById(R.id.tx_status);
            holder.outInTypeIconIv = convertView.findViewById(R.id.outInTypeIconIv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(datas.size() != 0){
            holder.userAccAddrTV.setText(datas.get(i).getTxAccountAddress());
            holder.txAmountTV.setText(datas.get(i).getTxAmount());
            holder.txDateTV.setText(datas.get(i).getTxDate());
            String status = datas.get(i).getTxStatus();
            if(TxStatusEnum.SUCCESS.getName().equals(status)){
                holder.txStatusTV.setText(R.string.tx_status_success_txt);
                holder.txStatusTV.setTextColor(convertView.getResources().getColor(R.color.tx_status_success_txt_color));
            }else {
                holder.txStatusTV.setText(R.string.tx_status_fail_txt);
                holder.txStatusTV.setTextColor(convertView.getResources().getColor(R.color.tx_status_failed_txt_color));
            }
            String outInType = datas.get(i).getOutinType();
            if(OutinTypeEnum.IN.getCode().equals(outInType)){
                holder.outInTypeIconIv.setImageDrawable(convertView.getResources().getDrawable(R.mipmap.icon_collection));
            }else{
                holder.outInTypeIconIv.setImageDrawable(convertView.getResources().getDrawable(R.mipmap.icon_payment));
            }
        }
        return convertView;
    }

    class ViewHolder {
        private TextView userAccAddrTV;
        private TextView txDateTV;
        private TextView txAmountTV;
        private TextView txStatusTV;
        private ImageView outInTypeIconIv;
    }

    public GetMyTxsRespDto.PageBean getPage() {
        return page;
    }

    public void setPage(GetMyTxsRespDto.PageBean page) {
        this.page = page;
    }
}
