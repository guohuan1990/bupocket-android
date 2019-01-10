package com.bupocket.fragment;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUITabSegment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BPWalletImportFragment extends BaseFragment {

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.tabSegment)
    QMUITabSegment mTabSegment;
    @BindView(R.id.contentViewPager)
    ViewPager mContentViewPager;

    private Map<ContentPage, View> mPageMap = new HashMap<>();
    private ContentPage mDestPage = ContentPage.MnemonicWord;
    private int mCurrentItemCount = 3;
    private PagerAdapter mPagerAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return mCurrentItemCount;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(final ViewGroup container, int position) {
            ContentPage page = ContentPage.getPage(position);
            View view = getPageView(page);
            view.setTag(page);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(view, params);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            View view = (View) object;
            Object page = view.getTag();
            if (page instanceof ContentPage) {
                int pos = ((ContentPage) page).getPosition();
                if (pos >= mCurrentItemCount) {
                    return POSITION_NONE;
                }
                return POSITION_UNCHANGED;
            }
            return POSITION_NONE;
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected View onCreateView() {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_wallet_import, null);
        ButterKnife.bind(this, rootView);
        init();
        return rootView;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void init() {
        initData();
        initUI();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initUI() {
        initTopBar();
        initTabAndPager();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initTabAndPager() {
        mContentViewPager.setAdapter(mPagerAdapter);
        mContentViewPager.setCurrentItem(mDestPage.getPosition(), false);
        mTabSegment.setHasIndicator(true);
        mTabSegment.setIndicatorPosition(false);
        mTabSegment.setIndicatorWidthAdjustContent(true);
        mTabSegment.addTab(new QMUITabSegment.Tab(getString(R.string.mnemonic_word_txt)));
        mTabSegment.addTab(new QMUITabSegment.Tab(getString(R.string.keystore_txt)));
        mTabSegment.addTab(new QMUITabSegment.Tab(getString(R.string.private_key_txt)));
        mTabSegment.setDefaultSelectedColor(getContext().getColor(R.color.app_color_main));
        mTabSegment.setupWithViewPager(mContentViewPager, false);
        mTabSegment.setMode(QMUITabSegment.MODE_FIXED);
        mTabSegment.addOnTabSelectedListener(new QMUITabSegment.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int index) {
                mTabSegment.hideSignCountView(index);
            }

            @Override
            public void onTabUnselected(int index) {

            }

            @Override
            public void onTabReselected(int index) {
                mTabSegment.hideSignCountView(index);
            }

            @Override
            public void onDoubleTap(int index) {

            }
        });
    }

    private void initData() {

    }

    private void initTopBar() {
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });

        mTopBar.setTitle(getString(R.string.import_wallet_title_txt));
    }

    public enum ContentPage {
        MnemonicWord(0),
        Keystore(1),
        PrivateKey(2);
        private final int position;

        ContentPage(int pos) {
            position = pos;
        }

        public static ContentPage getPage(int position) {
            switch (position) {
                case 0:
                    return MnemonicWord;
                case 1:
                    return Keystore;
                case 2:
                    return PrivateKey;
                default:
                    return MnemonicWord;
            }
        }
        public int getPosition() {
            return position;
        }
    }

    private View getPageView(ContentPage page) {
        View view = mPageMap.get(page);
        if (view == null) {

            mPageMap.put(page, view);
        }
        return view;
    }
}
