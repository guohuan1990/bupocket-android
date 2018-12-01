package com.bupocket.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bupocket.R;
import com.bupocket.base.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BPCardContainerFragment extends BaseFragment {

    @BindView(R.id.cardContainerMineCardTabTv)
    TextView mCardContainerMineCardTabTv;
    @BindView(R.id.cardContainerBuyTabTv)
    TextView mCardContainerBuyTabTv;
    @BindView(R.id.cardContainerSellTabTv)
    TextView mCardContainerSellTabTv;

    @BindView(R.id.cardContainerTabContentLl)
    LinearLayout mCardContainerTabContentLl;

    private View cardPackageMine;
    private View cardPackageBuyOrSell;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_card_package, null);
        ButterKnife.bind(this, root);
        init();
        return root;
    }

    private void init() {
        setListeners();
    }

    private void setListeners() {
        selectTabs();
    }

    private void selectTabs() {
        cardPackageMine = View.inflate(getContext(),R.layout.fragment_card_package_mine,null);
        cardPackageBuyOrSell = View.inflate(getContext(),R.layout.fragment_card_package_buy_or_sell,null);
        mCardContainerTabContentLl.addView(cardPackageMine);
        mCardContainerMineCardTabTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCardContainerTabContentLl.removeAllViews();
                mCardContainerTabContentLl.addView(cardPackageMine);
                setActiveTab("MINE");
            }
        });

        mCardContainerBuyTabTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCardContainerTabContentLl.removeAllViews();
                mCardContainerTabContentLl.addView(cardPackageBuyOrSell);
                setActiveTab("BUY");
            }
        });

        mCardContainerSellTabTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCardContainerTabContentLl.removeAllViews();
                mCardContainerTabContentLl.addView(cardPackageBuyOrSell);
                setActiveTab("SELL");
            }
        });
    }

    private void setActiveTab(String activeTab) {
        mCardContainerMineCardTabTv.setTextColor(getResources().getColor(R.color.app_color_green));
        mCardContainerBuyTabTv.setTextColor(getResources().getColor(R.color.app_color_green));
        mCardContainerSellTabTv.setTextColor(getResources().getColor(R.color.app_color_green));

        mCardContainerMineCardTabTv.setBackgroundColor(getResources().getColor(R.color.app_color_white));
        mCardContainerBuyTabTv.setBackgroundColor(getResources().getColor(R.color.app_color_white));
        mCardContainerSellTabTv.setBackgroundColor(getResources().getColor(R.color.app_color_white));
        if ("MINE".equals(activeTab)) {
            mCardContainerMineCardTabTv.setTextColor(getResources().getColor(R.color.app_color_white));
            mCardContainerMineCardTabTv.setBackgroundColor(getResources().getColor(R.color.app_color_green));
        } else if ("BUY".equals(activeTab)) {
            mCardContainerBuyTabTv.setTextColor(getResources().getColor(R.color.app_color_white));
            mCardContainerBuyTabTv.setBackgroundColor(getResources().getColor(R.color.app_color_green));
        } else if ("SELL".equals(activeTab)) {
            mCardContainerSellTabTv.setTextColor(getResources().getColor(R.color.app_color_white));
            mCardContainerSellTabTv.setBackgroundColor(getResources().getColor(R.color.app_color_green));
        }
    }
}
