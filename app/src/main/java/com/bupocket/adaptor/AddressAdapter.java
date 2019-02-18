package com.bupocket.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bupocket.R;
import com.bupocket.http.api.dto.resp.GetAddressBookRespDto;
import com.bupocket.utils.AddressUtil;

import java.util.List;

public class AddressAdapter extends BaseAdapter {
    private List<GetAddressBookRespDto.AddressBookListBean> datas;
    private GetAddressBookRespDto.PageBean page;
    private Context mContext;

    public GetAddressBookRespDto.PageBean getPage() {
        return page;
    }

    public void setPage(GetAddressBookRespDto.PageBean page) {
        this.page = page;
    }

    public AddressAdapter(List<GetAddressBookRespDto.AddressBookListBean> datas,Context mContext){
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
        AddressAdapter.ViewHolder holder;
        if(convertView == null){
            holder = new AddressAdapter.ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.view_address_list_item, null);
            holder.addressNameTv = convertView.findViewById(R.id.addressNameTv);
            holder.addressTv = convertView.findViewById(R.id.addressTv);
            holder.addressDescribeTv = convertView.findViewById(R.id.addressDescribeTv);
            convertView.setTag(holder);
        } else {
            holder = (AddressAdapter.ViewHolder) convertView.getTag();
        }

        if(datas.size() != 0){
            holder.addressNameTv.setText(datas.get(position).getNickName());
            holder.addressTv.setText(AddressUtil.anonymous(datas.get(position).getLinkmanAddress()));
            holder.addressDescribeTv.setText(datas.get(position).getRemark());
            if(datas.get(position).getRemark().length() == 0){
                holder.addressDescribeTv.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    class ViewHolder {
        private TextView addressNameTv;
        private TextView addressTv;
        private TextView addressDescribeTv;
    }
}
