package com.bupocket.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.bupocket.R;
import com.bupocket.dto.resp.GetMyTxsRespDto;
import com.bupocket.model.TokenTxInfo;

import java.util.Collection;
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
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.userAccAddrTV.setText(datas.get(i).getTxAccountAddress());
        holder.txAmountTV.setText(datas.get(i).getTxAmount());
        holder.txDateTV.setText(datas.get(i).getTxDate());
        holder.txStatusTV.setText(datas.get(i).getTxStatus());
        return convertView;
    }

    class ViewHolder {
        private TextView userAccAddrTV;
        private TextView txDateTV;
        private TextView txAmountTV;
        private TextView txStatusTV;
    }

    public GetMyTxsRespDto.PageBean getPage() {
        return page;
    }

    public void setPage(GetMyTxsRespDto.PageBean page) {
        this.page = page;
    }
}
