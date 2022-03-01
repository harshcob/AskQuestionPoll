package com.example.askQuestionPoll.ui.activity;

import static android.widget.Toast.LENGTH_LONG;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.example.askQuestionPoll.R;
import com.example.askQuestionPoll.core.constants.Constants;
import com.example.askQuestionPoll.core.pojo.ResetPasswordRequestModel;
import com.example.askQuestionPoll.core.pojo.ResetPasswordResponseModel;
import com.example.askQuestionPoll.core.pojo.UniversalResponseModel;
import com.example.askQuestionPoll.core.sharedprefs.CustomSharedPreference;
import com.example.askQuestionPoll.core.utils.SystemUtils;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.optimumbrew.library.core.network.ConnectivityUtils;
import com.optimumbrew.library.core.volley.GsonRequest;
import com.optimumbrew.library.core.volley.MyVolley;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResetPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ResetPasswordActivity";

    private RelativeLayout parent;
    private EditText password, confirmPassword;
    private Button reset;
    private Pattern pattern;
    private Matcher matcher;
    private ProgressDialog progressDialog;
    private MyVolley customVolleyQueue;
    private ImageView passwordVisible, confirmPasswordVisible;
    private boolean isPasswordVisible = false, isConfirmPasswordVisible = false;
    private Typeface face;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        reset = findViewById(R.id.submit_reset_password_activity);
        password = findViewById(R.id.password_reset_password_activity);
        confirmPassword = findViewById(R.id.confirm_password_reset_password_activity);
        parent = findViewById(R.id.parent_password_activity);
        passwordVisible = findViewById(R.id.password_visible_invisible_reset_password_activity);
        confirmPasswordVisible = findViewById(R.id.confirm_password_visible_invisible_reset_password_activity);


        progressDialog = new ProgressDialog(this,R.style.ProgressDialog);
        progressDialog.setMessage("Resetting your Password. Please wait");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        face = ResourcesCompat.getFont(ResetPasswordActivity.this, R.font.sf_atarian_system);
        customVolleyQueue = MyVolley.getInstance(ResetPasswordActivity.this);

        reset.setOnClickListener(view -> {
            SystemUtils.hideSoftKeyboard(ResetPasswordActivity.this);
            if (validatePassword()) {
                progressDialog.show();
                resetPasswordFromNetwork();
            }
        });

        passwordVisible.setOnClickListener(view -> {
            password.requestFocus();
            if (!password.getText().toString().trim().isEmpty()) {
                if (isPasswordVisible) {
                    isPasswordVisible = false;    //to run else or invisible password
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    passwordVisible.setImageResource(R.drawable.ic_round_eye_hide);
                } else {
                    isPasswordVisible = true; //to run if or visible password
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordVisible.setImageResource(R.drawable.ic_round_eye);
                }
                password.setSelection(password.getText().length());
                password.setTypeface(face);
            }
        });

        confirmPasswordVisible.setOnClickListener(view -> {
            confirmPassword.requestFocus();
            if (!confirmPassword.getText().toString().trim().isEmpty()) {
                if (isConfirmPasswordVisible) {
                    isConfirmPasswordVisible = false;    //to run else or invisible password
                    confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    confirmPasswordVisible.setImageResource(R.drawable.ic_round_eye_hide);
                } else {
                    isPasswordVisible = true; //to run if or visible password
                    confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    confirmPasswordVisible.setImageResource(R.drawable.ic_round_eye);
                }
                confirmPassword.setSelection(confirmPassword.getText().length());
                confirmPassword.setTypeface(face);
            }
        });

    }



    private void resetPasswordFromNetwork() {

        ResetPasswordRequestModel model = new ResetPasswordRequestModel();
        model.setEmail_id(CustomSharedPreference.getInstance(ResetPasswordActivity.this).getEmailID());
        model.setToken(CustomSharedPreference.getInstance(ResetPasswordActivity.this).getTempToken());
        model.setPassword(password.getText().toString());

        Log.e(TAG, "resetPasswordFromNetwork: request " + new Gson().toJson(model));
        GsonRequest<UniversalResponseModel> request = new GsonRequest<UniversalResponseModel>(1, Constants.API_RESET_PASSWORD, new Gson().toJson(model),
                UniversalResponseModel.class, null, new Response.Listener<UniversalResponseModel>() {
            @Override
            public void onResponse(UniversalResponseModel response) {
                progressDialog.dismiss();
                Log.e("Response ", "" + response);
                if (response.getCode() == 200) {
                    Toast.makeText(ResetPasswordActivity.this, "Password reset successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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

            }
        });
        customVolleyQueue.addToRequestQueue(request);
    }


    private boolean validatePassword() {
        password.setText(password.getText().toString().trim());
        confirmPassword.setText(confirmPassword.getText().toString().trim());
        if (Objects.requireNonNull(password.getText()).length() >= 8) {
            pattern = Pattern.compile("(.*[0-9].*)", Pattern.CASE_INSENSITIVE);
            matcher = pattern.matcher(password.getText());

            if (matcher.matches()) {

                pattern = Pattern.compile("(.*[A-Z].*)");
                matcher = pattern.matcher(password.getText());

                if (matcher.matches()) {

                    pattern = Pattern.compile("^(?=.*[_.()!@#$%^&*=+]).*$", Pattern.CASE_INSENSITIVE);
                    matcher = pattern.matcher(password.getText());

                    if (matcher.matches()) {
                        password.setError(null);
                        if (password.getText().toString().equals(confirmPassword.getText().toString())) {
                            confirmPassword.setError(null);
                            Log.e("Validation ", "true");
                            return true;
                        } else {
                            confirmPassword.setError("Confirm password should same as password");
                            return false;
                        }
                    } else {
                        Snackbar.make(parent, "Password should have at least one special character", BaseTransientBottomBar.LENGTH_SHORT).show();
                        password.requestFocus();
                        return false;
                    }
                } else {
                    Snackbar.make(parent, "Password should have at least one capital letter", BaseTransientBottomBar.LENGTH_SHORT).show();
                    password.requestFocus();
                    return false;
                }

            } else {
                Snackbar.make(parent, "Password should have at least one number", BaseTransientBottomBar.LENGTH_SHORT).show();
                password.requestFocus();
                return false;
            }
        } else {
            Snackbar.make(parent, "Password should be more than 8 character long", BaseTransientBottomBar.LENGTH_SHORT).show();
            password.requestFocus();
            return false;
        }
    }

}