package com.bupocket.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.bupocket.BPApplication;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.enums.BumoNodeEnum;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.utils.SocketUtil;
import com.bupocket.wallet.Wallet;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

public class BPSettingFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.settingLv)
    QMUIGroupListView mSettingLv;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_setting, null);
        ButterKnife.bind(this, root);
        init();
        return root;
    }

    private void init() {
        initUI();
    }

    private void initUI() {
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        initTopBar();
        initGroupListView();
    }

    private void initGroupListView() {

        // switch node item
        QMUICommonListItemView switchNode = mSettingLv.createItemView(getString(R.string.switch_node_title_txt));
        switchNode.getTextView().setTextColor(getResources().getColor(R.color.app_txt_color_gray_2));
        switchNode.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);
        switchNode.setImageDrawable(getResources().getDrawable(R.mipmap.icon_switch_node));
        // get bumoNode and set checked
        System.out.print(SharedPreferencesHelper.getInstance().getInt("bumoNode",Constants.DEFAULT_BUMO_NODE));
        if(SharedPreferencesHelper.getInstance().getInt("bumoNode",Constants.DEFAULT_BUMO_NODE) == BumoNodeEnum.TEST.getCode()){
            switchNode.getSwitch().setChecked(true);
        }else {
            switchNode.getSwitch().setChecked(false);
        }
        switchNode.getSwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                RetrofitFactory.getInstance().setNull4Retrofit();
                Wallet.getInstance().setNull4Wallet();
                SocketUtil.getInstance().SetNull4SocketUtil();
                if(!isChecked){
                    SharedPreferencesHelper.getInstance().save("bumoNode", BumoNodeEnum.MAIN.getCode());
                    BPApplication.switchNetConfig(BumoNodeEnum.MAIN.getName());
                }else {
                    SharedPreferencesHelper.getInstance().save("bumoNode", BumoNodeEnum.TEST.getCode());
                    BPApplication.switchNetConfig(BumoNodeEnum.TEST.getName());
                }
            }
        });

        // multi-language item
        QMUICommonListItemView language = mSettingLv.createItemView(getString(R.string.language_txt));
        language.getTextView().setTextColor(getResources().getColor(R.color.app_txt_color_gray_2));
        language.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM);
        language.setImageDrawable(getResources().getDrawable(R.mipmap.icon_profile_item_language));
        ImageView languageRightArrow = new ImageView(getContext());
        languageRightArrow.setImageDrawable(getResources().getDrawable(R.mipmap.icon_right_arrow));
        language.addAccessoryCustomView(languageRightArrow);
        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new BPLanguageFragment());
            }
        });

        // monetary unit item
        QMUICommonListItemView monetary = mSettingLv.createItemView(getString(R.string.monetary_title_txt));
        monetary.getTextView().setTextColor(getResources().getColor(R.color.app_txt_color_gray_2));
        monetary.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM);
        monetary.setImageDrawable(getResources().getDrawable(R.mipmap.icon_monetary_unit));
        ImageView monetaryRightArrow = new ImageView(getContext());
        monetaryRightArrow.setImageDrawable(getResources().getDrawable(R.mipmap.icon_right_arrow));
        monetary.addAccessoryCustomView(monetaryRightArrow);
        monetary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new BPCurrencyFragment());
            }
        });

        QMUIGroupListView.newSection(getContext())
                .setSeparatorDrawableRes(R.color.app_color_white)
                .setSeparatorDrawableRes(R.color.app_color_white,R.color.app_color_white,R.color.app_color_white,R.color.app_color_white)
                .addItemView(monetary,null)
                .addItemView(language,null)
                .addItemView(switchNode,null)
                .addTo(mSettingLv);
    }

    private void initTopBar() {
        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
    }
}
