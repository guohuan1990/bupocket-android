package com.bupocket.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.bupocket.R;
import com.bupocket.enums.TokenTypeEnum;
import com.bupocket.http.api.dto.resp.SearchTokenRespDto;
import com.bupocket.utils.CommonUtil;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.util.List;

public class SearchTokenAdapter extends BaseAdapter {
    private List<SearchTokenRespDto.TokenListBean> datas;
    private Context mContext;

    public SearchTokenAdapter(List<SearchTokenRespDto.TokenListBean> datas, Context mContext) {
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
    public View getView(int i, View convertView, ViewGroup parent) {
        SearchTokenAdapter.ViewHolder holder;
        if(convertView == null){
            holder = new SearchTokenAdapter.ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.search_token_result_item_layout, null);
            holder.assetIconIv = convertView.findViewById(R.id.assetIconIv);
            holder.assetCodeTv = convertView.findViewById(R.id.assetCodeTv);
            holder.assetNameTv = convertView.findViewById(R.id.assetNameTv);
            holder.assetIssuerTv = convertView.findViewById(R.id.assetIssuerTv);
            holder.tokenOptAddBtn = convertView.findViewById(R.id.tokenOptAddBtn);
            convertView.setTag(holder);
        } else {
            holder = (SearchTokenAdapter.ViewHolder) convertView.getTag();
        }

        if(datas.size() != 0){
            holder.assetCodeTv.setText(datas.get(i).getAssetCode());
            holder.assetNameTv.setText(datas.get(i).getAssetName());
            holder.assetIssuerTv.setText(datas.get(i).getIssuer());
            if(CommonUtil.isNull(datas.get(i).getIcon())){
                holder.assetIconIv.setBackgroundResource(R.mipmap.icon_token_default_icon);
            }else{
                try{
                    holder.assetIconIv.setImageBitmap(CommonUtil.base64ToBitmap(datas.get(i).getIcon()));
                }catch (Exception e){
                    holder.assetIconIv.setBackgroundResource(R.mipmap.icon_token_default_icon);
                }
            }
            String recommend = datas.get(i).getRecommend() + "";
            if("0".equals(recommend)){
                holder.tokenOptAddBtn.setVisibility(View.GONE);
            }else{
            }
        }

        return convertView;
    }
    class ViewHolder {
        private QMUIRadiusImageView assetIconIv;
        private TextView assetCodeTv;
        private TextView assetNameTv;
        private TextView assetIssuerTv;
        private QMUIRoundButton tokenOptAddBtn;
    }
}
