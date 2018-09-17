package com.bupocket.fragment;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.fragment.home.HomeFragment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

public class BPRecoverWalletFormFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;

    @BindView(R.id.recoverShowPwdIv)
    ImageView mPwdShow;

    @BindView(R.id.recoverShowConfirmPwdIv)
    ImageView mConfirmPwdShow;

    @BindView(R.id.recoverMneonicCodeEt)
    EditText mMneonicCode;

    @BindView(R.id.recoverWalletNameEt)
    EditText mWalletName;

    @BindView(R.id.recoverPwdEt)
    EditText mPwd;

    @BindView(R.id.recoverConfirmPwdEt)
    EditText mConfirmPwd;

    @BindView(R.id.recoverWalletSubmitBtn)
    QMUIRoundButton recoverSubmit;

    private boolean isPwdHideFirst = true;
    private boolean isConfirmPwdHideFirst = true;
    @Override
    protected View onCreateView() {

        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_recover_wallet_form, null);
        ButterKnife.bind(this, root);
        initTopBar();
        eventListeners();
        return root;
    }


    private void initTopBar() {

        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragmentAndDestroyCurrent(new HomeFragment());
            }
        });
    }

    private boolean validateData(){
        if ("".equals(mMneonicCode.getText().toString().trim())) {
            Toast.makeText(getActivity(), R.string.recover_edit_mneonic_code_hint,Toast.LENGTH_SHORT).show();
            return false;
        } else if ("".equals(mWalletName.getText().toString().trim())){
            Toast.makeText(getActivity(), R.string.recover_edit_new_wallet_name_hint,Toast.LENGTH_SHORT).show();
            return false;
        }else if("".equals(mPwd.getText().toString().trim())){
            Toast.makeText(getActivity(), R.string.recover_set_pwd_hint,Toast.LENGTH_SHORT).show();
            return false;
        }else if("".equals(mConfirmPwd.getText().toString().trim())){
            Toast.makeText(getActivity(), R.string.recover_confirm_pwd_hint,Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void eventListeners() {
        mPwdShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPwdHideFirst) {
                    mPwdShow.setImageDrawable(ContextCompat.getDrawable(getContext(),R.mipmap.icon_open_eye));
                    mPwd.setInputType(InputType.TYPE_CLASS_TEXT);
                    mPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance ());
                    isPwdHideFirst = true;
                } else {
                    mPwdShow.setImageDrawable(ContextCompat.getDrawable(getContext(),R.mipmap.icon_close_eye));
                    mPwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mPwd.setTransformationMethod(PasswordTransformationMethod.getInstance ());
                    isPwdHideFirst = false;
                }
            }
        });
        mConfirmPwdShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isConfirmPwdHideFirst) {
                    mConfirmPwdShow.setImageDrawable(ContextCompat.getDrawable(getContext(),R.mipmap.icon_open_eye));
                    mConfirmPwd.setInputType(InputType.TYPE_CLASS_TEXT);
                    mConfirmPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance ());
                    isConfirmPwdHideFirst = true;
                } else {
                    mConfirmPwdShow.setImageDrawable(ContextCompat.getDrawable(getContext(),R.mipmap.icon_close_eye));
                    mConfirmPwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mConfirmPwd.setTransformationMethod(PasswordTransformationMethod.getInstance ());
                    isConfirmPwdHideFirst = false;
                }
            }
        });

        recoverSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean flag = validateData();
                if(!flag){
                    return;
                }
                validateData();
            }
        });

    }

}
