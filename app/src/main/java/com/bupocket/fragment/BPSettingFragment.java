package com.bupocket.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.bupocket.BPApplication;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.enums.BumoNodeEnum;
import com.bupocket.enums.HiddenFunctionStatusEnum;
import com.bupocket.fragment.home.HomeFragment;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.utils.SocketUtil;
import com.bupocket.wallet.Wallet;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

public class BPSettingFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.settingLv)
    QMUIGroupListView mSettingLv;

    protected SharedPreferencesHelper sharedPreferencesHelper;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_setting, null);
        ButterKnife.bind(this, root);
        init();
        return root;
    }

    private void init() {
        initData();
        initUI();
    }

    private void initData() {
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
    }

    private void initUI() {
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        initTopBar();
        initGroupListView();
    }

    private void initGroupListView() {

        // switch node item
        final QMUICommonListItemView switchNode = mSettingLv.createItemView(getString(R.string.switch_node_title_txt));
        switchNode.getTextView().setTextColor(getResources().getColor(R.color.app_txt_color_gray_2));
        switchNode.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);
        switchNode.setImageDrawable(getResources().getDrawable(R.mipmap.icon_switch_node));
        // get bumoNode and set checked change listener
        if(SharedPreferencesHelper.getInstance().getInt("bumoNode",Constants.DEFAULT_BUMO_NODE) == BumoNodeEnum.TEST.getCode()){
            switchNode.getSwitch().setChecked(true);
        }else {
            switchNode.getSwitch().setChecked(false);
        }
        switchNode.getSwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(!isChecked){
                    SharedPreferencesHelper.getInstance().save("bumoNode", BumoNodeEnum.MAIN.getCode());
                    BPApplication.switchNetConfig(BumoNodeEnum.MAIN.getName());
                    showSwitchMainNetDialog();
                }else {
                    ShowSwitchTestNetConfirmDialog();
                }
            }

            private void ShowSwitchTestNetConfirmDialog() {
                new QMUIDialog.MessageDialogBuilder(getActivity())
                        .setMessage(getString(R.string.switch_test_net_message_txt))
                        .addAction(getString(R.string.no_txt), new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                mSettingLv.removeAllViews();
                                initGroupListView();
                                dialog.dismiss();
                            }
                        })
                        .addAction(getString(R.string.yes_txt), new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                SharedPreferencesHelper.getInstance().save("bumoNode", BumoNodeEnum.TEST.getCode());
                                BPApplication.switchNetConfig(BumoNodeEnum.TEST.getName());
                                dialog.dismiss();
                                startFragment(new HomeFragment());
                            }
                        })
                        .setCanceledOnTouchOutside(false)
                        .create().show();
            }

            private void showSwitchMainNetDialog() {
                new QMUIDialog.MessageDialogBuilder(getActivity())
                        .setMessage(getString(R.string.switch_main_net_message_txt))
                        .addAction(getString(R.string.i_knew_btn_txt), new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                dialog.dismiss();
                                startFragment(new HomeFragment());
                            }
                        }).setCanceledOnTouchOutside(false).create().show();
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

        QMUIGroupListView.Section section = QMUIGroupListView.newSection(getContext())
                .setSeparatorDrawableRes(R.color.app_color_white)
                .setSeparatorDrawableRes(R.color.app_color_white,R.color.app_color_white,R.color.app_color_white,R.color.app_color_white)
                .addItemView(monetary,null)
                .addItemView(language,null);

        int hiddenFunctionStatus = sharedPreferencesHelper.getInt("hiddenFunctionStatus",HiddenFunctionStatusEnum.DISABLE.getCode());
        if(HiddenFunctionStatusEnum.ENABLE.getCode() == hiddenFunctionStatus){
            section.addItemView(switchNode,null);
        }
        section.addTo(mSettingLv);
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
