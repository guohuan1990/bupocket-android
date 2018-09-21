package com.bupocket.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.utils.LocaleUtil;
import com.bupocket.utils.SpUtil;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BPLanguageFragment extends BaseFragment{
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.languageCNRL)
    RelativeLayout mLanguageCNRL;
    @BindView(R.id.languageENRL)
    RelativeLayout mLanguageENRL;
    @BindView(R.id.cnSelectedIV)
    ImageView mCnSelectedIV;
    @BindView(R.id.enSelectedIV)
    ImageView mEnSelectedIV;

    private int currentLanguage = 0;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_language, null);
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        ButterKnife.bind(this, root);
        getLanguage();
        initTopBar();
        mLanguageCNRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentLanguage = 0;
                LocaleUtil.changeAppLanguage(getContext(), currentLanguage);
                mCnSelectedIV.setBackgroundResource(R.mipmap.icon_language_selected);
                mEnSelectedIV.setBackgroundResource(R.color.app_color_white);
            }
        });
        mLanguageENRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentLanguage = 1;
                LocaleUtil.changeAppLanguage(getContext(), currentLanguage);
                mCnSelectedIV.setBackgroundResource(R.color.app_color_white);
                mEnSelectedIV.setBackgroundResource(R.mipmap.icon_language_selected);
            }
        });
        return root;
    }

    private void getLanguage() {
        int language = SpUtil.getInstance().getInt("currentLanguage", currentLanguage);
        switch (language) {
            case 0:
                mCnSelectedIV.setBackgroundResource(R.mipmap.icon_language_selected);
                break;
            case 1:
                mEnSelectedIV.setBackgroundResource(R.mipmap.icon_language_selected);
                break;
        }
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
