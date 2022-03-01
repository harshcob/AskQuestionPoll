package com.example.askQuestionPoll.ui.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.example.askQuestionPoll.R;
import com.example.askQuestionPoll.core.constants.Constants;
import com.example.askQuestionPoll.core.pojo.ForgotOtpVerifyRequestModel;
import com.example.askQuestionPoll.core.pojo.ForgotOtpVerifyResponseModel;
import com.example.askQuestionPoll.core.pojo.ForgotPasswordResponseModel;
import com.example.askQuestionPoll.core.pojo.SignUpOtpResendResponseModel;
import com.example.askQuestionPoll.core.pojo.SignUpOtpVerifyRequestModel;
import com.example.askQuestionPoll.core.pojo.SignUpOtpVerifyResponseModel;
import com.example.askQuestionPoll.core.sharedprefs.CustomSharedPreference;
import com.example.askQuestionPoll.core.utils.SystemUtils;
import com.example.askQuestionPoll.ui.activity.LoginActivity;
import com.example.askQuestionPoll.ui.activity.ResetPasswordActivity;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.optimumbrew.library.core.network.ConnectivityUtils;
import com.optimumbrew.library.core.volley.GsonRequest;
import com.optimumbrew.library.core.volley.MyVolley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;

public class OtpVerificationDialog extends Dialog {
    private static final String TAG = "OtpVerificationDialog";

    private RelativeLayout parent;
    private Context context;
    private EditText otp1, otp2, otp3, otp4;
    private TextView resend;
    private Button reset, submit;
    private ProgressDialog progressDialog;
    private MyVolley customVolleyQueue;
    private boolean isSignup = false;
    private boolean isForgot = false;
    ConnectivityManager connectivityManager;


    public OtpVerificationDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_otp_verification);

        otp1 = findViewById(R.id.otp1_otp_dialog);
        otp2 = findViewById(R.id.otp2_otp_dialog);
        otp3 = findViewById(R.id.otp3_otp_dialog);
        otp4 = findViewById(R.id.otp4_otp_dialog);
        resend = findViewById(R.id.resend_otp_dialog);
        reset = findViewById(R.id.reset_otp_dialog);
        submit = findViewById(R.id.submit_otp_dialog);
        parent = findViewById(R.id.parent_otp_dialog);

        customVolleyQueue = MyVolley.getInstance(getOwnerActivity());

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Verifying your otp. Please wait");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        reset.setOnClickListener(view -> {
            otp1.setText("");
            otp2.setText("");
            otp3.setText("");
            otp4.setText("");
            otp1.requestFocus();
        });

        resend.setOnClickListener(view -> {
            SystemUtils.hideSoftKeyboard(getOwnerActivity());
            progressDialog.show();
            if (isSignup)
                signUpResendOtpFromNetwork();
            else if (isForgot)
                forgotResendGivenOtpFromNetwork();
        });

        submit.setOnClickListener(view -> {
            if (validateOtp()) {
                progressDialog.show();
                if (isSignup)
                    signUpVerifyGivenOtpFromNetwork();
                else if (isForgot) {
                    forgotVerifyGivenOtpFromNetwork();
                }
            }
        });
        setupEditTexts();
    }

    private void forgotResendGivenOtpFromNetwork() {
        JSONObject paramBody = new JSONObject();
        try {
            paramBody.put("email_id", CustomSharedPreference.getInstance(context).getEmailID());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        GsonRequest<ForgotPasswordResponseModel> forgotPasswordRequest = new GsonRequest<ForgotPasswordResponseModel>(1, Constants.API_FORGOT_PASSWORD, paramBody.toString(),
                ForgotPasswordResponseModel.class, null, new Response.Listener<ForgotPasswordResponseModel>() {
            @Override
            public void onResponse(ForgotPasswordResponseModel response) {
                progressDialog.dismiss();
                Log.e("Otp dialog error ", response.getMessage());
                if (response.getCode() == 200) {
                    Toast.makeText(context, "Otp has sent to your email", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();

                if (error == null || error.getCause() == null) {
                    Toast.makeText(getContext(), "Server not responds", Toast.LENGTH_SHORT).show();
                } else if (error.getCause() instanceof JsonSyntaxException || error.getCause() instanceof IllegalStateException) {
                    if (error.getMessage() !=null && !error.getMessage().isEmpty()){
                        Toast.makeText(getContext(), "OTP does not match", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getContext(), "Please, restart app", Toast.LENGTH_SHORT).show();
                    }
                } else if (error.getCause() instanceof TimeoutError || error.getCause() instanceof NoConnectionError) {
                    Toast.makeText(getContext(), "Server timeout. Try again", Toast.LENGTH_SHORT).show();
                } else if (ConnectivityUtils.isInternetConnected()) {
                    Snackbar.make(parent, "No internet connection", BaseTransientBottomBar.LENGTH_SHORT).show();
                } else {
                    if (error.getMessage() != null && !error.getMessage().isEmpty()) {
                        Toast.makeText(getContext(), "error "+error.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "this error never happens", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        customVolleyQueue.addToRequestQueue(forgotPasswordRequest);
    }

    private void forgotVerifyGivenOtpFromNetwork() {
        int otpMain = ((1000) * Integer.parseInt(otp1.getText().toString())) + ((100) * Integer.parseInt(otp2.getText().toString())) + ((10) * Integer.parseInt(otp3.getText().toString())) + Integer.parseInt(otp4.getText().toString());

        ForgotOtpVerifyRequestModel model = new ForgotOtpVerifyRequestModel();
        model.setToken(otpMain);
        model.setEmail_id(CustomSharedPreference.getInstance(getOwnerActivity()).getEmailID());
        //verify given otp api call
        Log.e(TAG, "Request" + new Gson().toJson(model));
        GsonRequest<ForgotOtpVerifyResponseModel> request = new GsonRequest<ForgotOtpVerifyResponseModel>(1, Constants.API_FORGOT_OTP_VERIFY, new Gson().toJson(model),
                ForgotOtpVerifyResponseModel.class, null, response -> {
            progressDialog.dismiss();
            Log.e(TAG, "Response " + response.toString());
            if (response.getCode() == 200) {
                CustomSharedPreference.getInstance(getOwnerActivity()).setTempToken(response.getData().getToken());
                Toast.makeText(context, response.getMessage(), Toast.LENGTH_SHORT).show();
                context.startActivity(new Intent(context, ResetPasswordActivity.class));
                dismiss();
            }
        }, error -> {
            progressDialog.dismiss();

            if (error == null || error.getCause() == null) {
                Snackbar.make(parent, "Server not respond", BaseTransientBottomBar.LENGTH_LONG).show();
            } else if (error.getCause() instanceof JsonSyntaxException || error.getCause() instanceof IllegalStateException) {
                Snackbar.make(parent, "Please, restart app", BaseTransientBottomBar.LENGTH_LONG).show();
            } else if (error.getCause() instanceof TimeoutError || error.getCause() instanceof NoConnectionError) {
                Snackbar.make(parent, "Server timeout! Try again", BaseTransientBottomBar.LENGTH_SHORT).show();
            } else if (ConnectivityUtils.isInternetConnected()) {
                Snackbar.make(parent, "No internet connection", BaseTransientBottomBar.LENGTH_SHORT).show();
            } else {
                if (error.getMessage() != null && !error.getMessage().isEmpty()) {
                    Snackbar.make(parent, "Error " + error.getMessage(), BaseTransientBottomBar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(parent, "This error never happens", BaseTransientBottomBar.LENGTH_SHORT).show();
                }
            }


        });
        customVolleyQueue.addToRequestQueue(request);
    }

    private void signUpVerifyGivenOtpFromNetwork() {

        int otpMain = ((1000) * Integer.parseInt(otp1.getText().toString())) + ((100) * Integer.parseInt(otp2.getText().toString())) + ((10) * Integer.parseInt(otp3.getText().toString())) + Integer.parseInt(otp4.getText().toString());

        SignUpOtpVerifyRequestModel model = new SignUpOtpVerifyRequestModel();
        model.setToken(otpMain);
        model.setUser_reg_temp_id(CustomSharedPreference.getInstance(getOwnerActivity()).getTempUserId());
        model.getDevice_info().setDevice_udid(CustomSharedPreference.getInstance(getOwnerActivity()).getUdid());

        //verify given otp api call
        Log.e(TAG, "Request" + new Gson().toJson(model));
        GsonRequest<SignUpOtpVerifyResponseModel> request = new GsonRequest<SignUpOtpVerifyResponseModel>(1, Constants.API_SIGNUP_OTP_VERIFY, new Gson().toJson(model),
                SignUpOtpVerifyResponseModel.class, null, response -> {
            progressDialog.dismiss();
            Log.e(TAG, "Response " + response.toString());
            if (response.getCode() == 200) {
                Toast.makeText(context, response.getMessage(), Toast.LENGTH_SHORT).show();
                context.startActivity(new Intent(context, LoginActivity.class));
                dismiss();
            }
        }, error -> {
            progressDialog.dismiss();

            if (error == null || error.getCause() == null) {
                Snackbar.make(parent, "Server not respond", BaseTransientBottomBar.LENGTH_LONG).show();
            } else if (error.getCause() instanceof JsonSyntaxException || error.getCause() instanceof IllegalStateException) {
                Snackbar.make(parent, "Please, restart app", BaseTransientBottomBar.LENGTH_LONG).show();
            } else if (error.getCause() instanceof TimeoutError || error.getCause() instanceof NoConnectionError) {
                Snackbar.make(parent, "Server timeout! Try again", BaseTransientBottomBar.LENGTH_SHORT).show();
            } else if (ConnectivityUtils.isInternetConnected()) {
                Snackbar.make(parent, "No internet connection", BaseTransientBottomBar.LENGTH_SHORT).show();
            } else {
                if (error.getMessage() != null && !error.getMessage().isEmpty()) {
                    Snackbar.make(parent, "Error " + error.getMessage(), BaseTransientBottomBar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(parent, "This error never happens", BaseTransientBottomBar.LENGTH_SHORT).show();
                }
            }

        });
        customVolleyQueue.addToRequestQueue(request);
    }

    private void signUpResendOtpFromNetwork() {
        JSONObject paramBody = new JSONObject();
        try {
            paramBody.put("user_reg_temp_id", CustomSharedPreference.getInstance(getOwnerActivity()).getTempUserId());
            Log.e("Json sent to api ", paramBody.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // resend otp api call
        GsonRequest<SignUpOtpResendResponseModel> request = new GsonRequest<SignUpOtpResendResponseModel>(1, Constants.API_SIGNUP_OTP_RESEND, paramBody.toString(),
                SignUpOtpResendResponseModel.class, null, response -> {
            progressDialog.dismiss();
            if (response.getCode() == 200) {
                Log.e("Response ", response.toString());
                Toast.makeText(context, "Otp has sent to your mail successfully again", Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            progressDialog.dismiss();

            if (error == null || error.getCause() == null) {
                Snackbar.make(parent, "Server not respond", BaseTransientBottomBar.LENGTH_LONG).show();
            } else if (error.getCause() instanceof JsonSyntaxException || error.getCause() instanceof IllegalStateException) {
                Snackbar.make(parent, "Please, restart app", BaseTransientBottomBar.LENGTH_LONG).show();
            } else if (error.getCause() instanceof TimeoutError || error.getCause() instanceof NoConnectionError) {
                Snackbar.make(parent, "Server timeout! Try again", BaseTransientBottomBar.LENGTH_SHORT).show();
            } else if (ConnectivityUtils.isInternetConnected()) {
                Snackbar.make(parent, "No internet connection", BaseTransientBottomBar.LENGTH_SHORT).show();
            } else {
                if (error.getMessage() != null && !error.getMessage().isEmpty()) {
                    Snackbar.make(parent, "Error " + error.getMessage(), BaseTransientBottomBar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(parent, "This error never happens", BaseTransientBottomBar.LENGTH_SHORT).show();
                }
            }

        });
        customVolleyQueue.addToRequestQueue(request);

    }


    private void setupEditTexts() {
        otp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() >= 1) {
                    otp2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        otp2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() >= 1) {
                    otp3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        otp3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() >= 1) {
                    otp4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        otp4.setOnKeyListener((view, i, keyEvent) -> {
            if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                if (otp4.getText().toString().isEmpty()) {
                    otp3.requestFocus();
                }
            }
            return false;
        });
        otp3.setOnKeyListener((view, i, keyEvent) -> {
            if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                if (otp4.getText().toString().isEmpty() && otp3.getText().toString().isEmpty()) {
                    otp2.requestFocus();
                }
            }
            return false;
        });
        otp2.setOnKeyListener((view, i, keyEvent) -> {
            if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                if (otp4.getText().toString().isEmpty() && otp3.getText().toString().isEmpty() && otp2.getText().toString().isEmpty()) {
                    otp1.requestFocus();
                }
            }
            return false;
        });
    }

    private boolean validateOtp() {
        if (otp1.getText().length() >= 1 && otp2.getText().length() >= 1 && otp3.getText().length() >= 1 && otp4.getText().length() >= 1)
            return true;
        return false;
    }

    public boolean isSignup() {
        return isSignup;
    }

    public void setSignup(boolean signup) {
        isSignup = signup;
    }

    public boolean isForgot() {
        return isForgot;
    }

    public void setForgot(boolean forgot) {
        isForgot = forgot;
    }
}
