package com.bupocket.fragment;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.TxService;
import com.bupocket.http.api.UserService;
import com.bupocket.http.api.dto.resp.ApiResult;
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

public class BPHelpFeedbackFragment extends BaseFragment{
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.feedbackContentET)
    EditText mFeedbackContentET;
    @BindView(R.id.contactET)
    EditText mContactET;
    @BindView(R.id.nextHelpFeedbackBtn)
    Button mNextHelpFeedbackBtn;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_help_feedback, null);
        ButterKnife.bind(this, root);
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        initTopBar();
        buildWatcher();
        mNextHelpFeedbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubmitFeedback();
            }
        });
        return root;
    }

    private void buildWatcher() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mNextHelpFeedbackBtn.setEnabled(false);
                mNextHelpFeedbackBtn.setBackgroundColor(Color.rgb(201, 201, 201));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mNextHelpFeedbackBtn.setEnabled(true);
                mNextHelpFeedbackBtn.setBackgroundColor(Color.rgb(54, 178, 255));
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==0){
                    mNextHelpFeedbackBtn.setEnabled(false);
                    mNextHelpFeedbackBtn.setBackgroundColor(Color.rgb(201, 201, 201));
                }
            }
        };
        mNextHelpFeedbackBtn.addTextChangedListener(watcher);
    }

    private void initTopBar() {
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
