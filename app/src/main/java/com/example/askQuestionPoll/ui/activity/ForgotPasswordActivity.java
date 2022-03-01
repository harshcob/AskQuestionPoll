package com.example.askQuestionPoll.ui.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.example.askQuestionPoll.R;
import com.example.askQuestionPoll.core.constants.Constants;
import com.example.askQuestionPoll.core.pojo.ForgotPasswordResponseModel;
import com.example.askQuestionPoll.core.sharedprefs.CustomSharedPreference;
import com.example.askQuestionPoll.core.utils.SystemUtils;
import com.example.askQuestionPoll.ui.dialog.OtpVerificationDialog;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonSyntaxException;
import com.optimumbrew.library.core.network.ConnectivityUtils;
import com.optimumbrew.library.core.volley.GsonRequest;
import com.optimumbrew.library.core.volley.MyVolley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgotPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ForgotPasswordActivity";

    private EditText email;
    private Button forgotPassword;
    private ProgressDialog progressDialog;
    private Pattern pattern;
    private Matcher matcher;
    private MyVolley customVolleyQueue;
    private RelativeLayout parent;

    private OtpVerificationDialog otpDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email = findViewById(R.id.email_forgot_password_activity);
        forgotPassword = findViewById(R.id.submit_forgot_password_activity);
        parent = findViewById(R.id.parent_forgot_password_activity);

        progressDialog = new ProgressDialog(ForgotPasswordActivity.this, R.style.ProgressDialog);
        progressDialog.setMessage("Validating your email. Please wait");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        customVolleyQueue = MyVolley.getInstance(ForgotPasswordActivity.this);
        otpDialog = new OtpVerificationDialog(ForgotPasswordActivity.this);
        forgotPassword.setOnClickListener(view -> {
            SystemUtils.hideSoftKeyboard(ForgotPasswordActivity.this);
            if (validateEmail()) {
                progressDialog.show();
                forgotPasswordFromNetwork();
            }
        });
    }

    private void forgotPasswordFromNetwork() {
        JSONObject paramBody = new JSONObject();

        try {
            paramBody.put("email_id", email.getText().toString());
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
                    CustomSharedPreference.getInstance(ForgotPasswordActivity.this).setEmailID(email.getText().toString());
                    Toast.makeText(ForgotPasswordActivity.this, "Otp has sent to your email", Toast.LENGTH_SHORT).show();
                    openOtpVerificationDialog();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();

                if (error == null || error.getCause() == null) {
                    Snackbar.make(parent, "Server not respond", BaseTransientBottomBar.LENGTH_LONG).show();
                } else if (error.getCause() instanceof JsonSyntaxException || error.getCause() instanceof IllegalStateException) {
                    if (error.getMessage() != null && !error.getMessage().isEmpty()) {
                        if (error.getMessage().contains("New password is same as old password")) {
                            Snackbar.make(parent, "New password is same as old password" + error.getMessage(), BaseTransientBottomBar.LENGTH_SHORT).show();
                        }
                    } else {
                        Snackbar.make(parent, "Please, restart app", BaseTransientBottomBar.LENGTH_LONG).show();
                    }
                } else if (error.getCause() instanceof TimeoutError || error.getCause() instanceof NoConnectionError) {
                    Snackbar.make(parent, "Server timeout! Try again", BaseTransientBottomBar.LENGTH_SHORT).show();
                } else if (ConnectivityUtils.isInternetConnected()) {
                    Snackbar.make(parent, "No internet connection", BaseTransientBottomBar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(parent, "error " + error.getCause(), BaseTransientBottomBar.LENGTH_SHORT).show();
                }

            }
        });
        customVolleyQueue.addToRequestQueue(forgotPasswordRequest);
    }

    private boolean validateEmail() {
        email.setText(email.getText().toString().trim());
        if (Objects.requireNonNull(email.getText()).length() >= 8) {
            pattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
            matcher = pattern.matcher(email.getText());
            if (matcher.matches()) {
                email.setError(null);
                return true;
            } else {
                email.setError("Email address is not valid");
                email.requestFocus();
                return false;
            }
        } else {
            email.setError("Email address should be more than 8 character long");
            email.requestFocus();
            return false;
        }
    }

    private void openOtpVerificationDialog() {

        if (otpDialog != null && !otpDialog.isShowing()) {
            otpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            otpDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
            otpDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            otpDialog.setCanceledOnTouchOutside(false);
            otpDialog.setForgot(true);
            otpDialog.show();
        }
    }
}