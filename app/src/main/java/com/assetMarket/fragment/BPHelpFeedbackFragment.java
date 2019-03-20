package com.assetMarket.fragment;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.assetMarket.base.BaseFragment;
import com.assetMarket.common.Constants;
import com.assetMarket.http.api.RetrofitFactory;
import com.assetMarket.http.api.UserService;
import com.assetMarket.http.api.dto.resp.ApiResult;
import com.assetMarket.R;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BPHelpFeedbackFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.feedbackContentET)
    EditText mFeedbackContentET;
    @BindView(R.id.contactET)
    EditText mContactET;
    @BindView(R.id.nextHelpFeedbackBtn)
    Button mNextHelpFeedbackBtn;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_help_feedback, null);
        ButterKnife.bind(this, root);
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        initTopBar();
        mNextHelpFeedbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubmitFeedback();
            }
        });
        buildWatcher();
        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void buildWatcher() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mNextHelpFeedbackBtn.setEnabled(false);
                mNextHelpFeedbackBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_disable_bg));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mNextHelpFeedbackBtn.setEnabled(false);
                mNextHelpFeedbackBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_disable_bg));
            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean signContent = mFeedbackContentET.getText().length() > 0;
                boolean signContact = mContactET.getText().length() > 0;
                if(signContent && signContact){
                    mNextHelpFeedbackBtn.setEnabled(true);
                    mNextHelpFeedbackBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_able_bg));
                }else {
                    mNextHelpFeedbackBtn.setEnabled(false);
                    mNextHelpFeedbackBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_disable_bg));
                }
            }
        };
        mFeedbackContentET.addTextChangedListener(watcher);
        mContactET.addTextChangedListener(watcher);
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

    private void SubmitFeedback(){

        UserService userService = RetrofitFactory.getInstance().getRetrofit().create(UserService.class);
        Map<String, Object> parmasMap = new HashMap<>();
        String content = mFeedbackContentET.getText().toString();
        String contact = mContactET.getText().toString();
        if(content.equals("")||content == null){
            final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                    .setTipWord(getResources().getString(R.string.content_empty_msg))
                    .create();
            tipDialog.show();
            mNextHelpFeedbackBtn.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tipDialog.dismiss();
                }
            }, 1500);
        }else if(contact.equals("")||contact == null){
            final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                    .setTipWord(getResources().getString(R.string.contact_empty_msg))
                    .create();
            tipDialog.show();
            mNextHelpFeedbackBtn.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tipDialog.dismiss();
                }
            }, 1500);
        } else if (content.length() > Constants.HELP_FEEDBACK_CONTENT_LENGTH) {
            final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                    .setTipWord(getResources().getString(R.string.content_too_long_msg))
                    .create();
            tipDialog.show();
            mNextHelpFeedbackBtn.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tipDialog.dismiss();
                }
            }, 1500);
        } else if (contact.length() > Constants.HELP_FEEDBACK_CONTACT_LENGTH) {
            final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                    .setTipWord(getResources().getString(R.string.contact_too_long_msg))
                    .create();
            tipDialog.show();
            mNextHelpFeedbackBtn.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tipDialog.dismiss();
                }
            }, 1500);
        }else {
            parmasMap.put("content",content);
            parmasMap.put("contact",contact);

            Call<ApiResult> call = userService.submitFeedback(parmasMap);
            call.enqueue(new Callback<ApiResult>() {

                @Override
                public void onResponse(Call<ApiResult> call, Response<ApiResult> response) {
                    ApiResult respDto = response.body();
                    final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                            .setTipWord(getResources().getString(R.string.help_feedback_submit_success_msg))
                            .create();
                    tipDialog.show();
                    mNextHelpFeedbackBtn.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tipDialog.dismiss();
                        }
                    }, 1500);
                    mFeedbackContentET.setText("");
                    mContactET.setText("");
                }

                @Override
                public void onFailure(Call<ApiResult> call, Throwable t) {
                    final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                            .setTipWord(getResources().getString(R.string.help_feedback_submit_failed_msg))
                            .create();
                    tipDialog.show();
                    mNextHelpFeedbackBtn.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tipDialog.dismiss();
                        }
                    }, 1500);
                }
            });
        }

    }
}
