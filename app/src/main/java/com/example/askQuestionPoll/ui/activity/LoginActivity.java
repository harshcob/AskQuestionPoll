package com.example.askQuestionPoll.ui.activity;

import static android.widget.Toast.LENGTH_LONG;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.bumptech.glide.util.Util;
import com.example.askQuestionPoll.R;
import com.example.askQuestionPoll.core.constants.Constants;
import com.example.askQuestionPoll.core.constants.Extras;
import com.example.askQuestionPoll.core.pojo.LoginRequestModel;
import com.example.askQuestionPoll.core.pojo.LoginResponseModel;
import com.example.askQuestionPoll.core.sharedprefs.CustomSharedPreference;
import com.example.askQuestionPoll.core.utils.SystemUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private TextView forgotPassword, signUpLink;
    private LinearLayout facebook, google;
    private Button login;
    private EditText email, password;
    private ProgressDialog progressDialog;
    private Pattern pattern;
    private Matcher matcher;
    private MyVolley customVolleyQueue;
    private RelativeLayout parent;
    private ImageView passwordVisible;
    private Typeface face;
    private boolean isPasswordVisible = false;
    private Task<GoogleSignInAccount> task;
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        forgotPassword = findViewById(R.id.forgot_password_login_activity);
        signUpLink = findViewById(R.id.signup_link_login_activity);
        login = findViewById(R.id.submit_login_activity);
        facebook = findViewById(R.id.facebook_login_activity);
        google = findViewById(R.id.google_login_activity);
        email = findViewById(R.id.email_login_activity);
        password = findViewById(R.id.password_login_activity);
        parent = findViewById(R.id.parent_login_activity);
        passwordVisible = findViewById(R.id.password_visible_invisible_login_activity);
        //set login layout full height 1.5 out of 3.5 if needed in large device

        face = ResourcesCompat.getFont(LoginActivity.this, R.font.sf_atarian_system);
        customVolleyQueue = MyVolley.getInstance(LoginActivity.this);
        progressDialog = new ProgressDialog(this, R.style.ProgressDialog);
        progressDialog.setMessage("Logging you in. Please wait");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        String tempEmail = CustomSharedPreference.getInstance(LoginActivity.this).getEmailID();
        String tempPassword = CustomSharedPreference.getInstance(LoginActivity.this).getPassword();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        if (!tempPassword.isEmpty() && !tempEmail.isEmpty()) {
            email.setText(tempEmail);
            password.setText(tempPassword);
        }

        signUpLink.setOnClickListener(view -> {
            CustomSharedPreference.getInstance(LoginActivity.this).setEmailID(email.getText().toString().trim());
            CustomSharedPreference.getInstance(LoginActivity.this).setPassword(password.getText().toString().trim());
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        });

        login.setOnClickListener(view -> {
            SystemUtils.hideSoftKeyboard(LoginActivity.this);
            if (validateData()) {
                progressDialog.show();
                loginFromNetwork();
            }
            /*startActivity(new Intent(LoginActivity.this, HomeActivity.class))*/
        });

        facebook.setOnClickListener(view -> signInUsingFacebook());
        google.setOnClickListener(view -> signInUsingGoogle());

        forgotPassword.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class)));

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

    }

    private void signInUsingGoogle() {
        mGoogleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, Extras.GOOGLE_STATUS);
    }

    private void setupGoogleAccountForApplication(GoogleSignInAccount account) {
        CustomSharedPreference.getInstance(LoginActivity.this).setEmailID(account.getEmail());
        CustomSharedPreference.getInstance(LoginActivity.this).setTokenApplication(account.getIdToken());
        Toast.makeText(LoginActivity.this, "id " + account.getId(), Toast.LENGTH_SHORT).show();
        Toast.makeText(LoginActivity.this, "id token " + account.getIdToken(), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
    }

    private void signInUsingFacebook() {
        Toast.makeText(LoginActivity.this, "Sign in using facebook ", Toast.LENGTH_SHORT).show();
    }


    private void loginFromNetwork() {
        LoginRequestModel model = new LoginRequestModel();
        model.setEmail_id(Objects.requireNonNull(email.getText()).toString());
        model.setPassword(Objects.requireNonNull(password.getText()).toString());
        model.getDevice_info().setDevice_udid(CustomSharedPreference.getInstance(LoginActivity.this).getUdid());
        GsonRequest<LoginResponseModel> request = new GsonRequest<LoginResponseModel>
                (1, Constants.API_LOGIN, new Gson().toJson(model), LoginResponseModel.class, null, new Response.Listener<LoginResponseModel>() {
                    @Override
                    public void onResponse(LoginResponseModel response) {
                        progressDialog.dismiss();
                        Log.e(TAG, "response " + response);
                        Toast.makeText(LoginActivity.this, response.getMessage(), LENGTH_LONG).show();
                        if (response.code == 200) {
                            CustomSharedPreference.getInstance(LoginActivity.this).setTokenApplication(response.getData().getToken());
                            CustomSharedPreference.getInstance(LoginActivity.this).setLogin(true);
                            CustomSharedPreference.getInstance(LoginActivity.this).setEmailID(email.getText().toString().trim());
                            CustomSharedPreference.getInstance(LoginActivity.this).setUserId(response.getData().getUser_details().getId());
                            CustomSharedPreference.getInstance(LoginActivity.this).setUserName(response.getData().getUser_details().getFirst_name().trim() + " " + response.getData().getUser_details().getLast_name().trim());
                            CustomSharedPreference.getInstance(LoginActivity.this).setTokenExpire(false);
                            SplashActivity.headerData.put("Authorization", "Bearer " + CustomSharedPreference.getInstance(LoginActivity.this).getTokenApplication());
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
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
                            if (error.getMessage()!=null && !error.getMessage().isEmpty()){
                                Snackbar.make(parent, "Error "+error.getMessage(), BaseTransientBottomBar.LENGTH_SHORT).show();
                            } else {
                                Snackbar.make(parent, "This error never happens", BaseTransientBottomBar.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
        customVolleyQueue.addToRequestQueue(request);
    }

    private boolean validateData() {
        if (validateEmail()) {
            if (validatePassword()) {
                Log.e(TAG, "success ");
                return true;
            }
        }
        return false;
    }


    private boolean validatePassword() {
        password.setText(password.getText().toString().trim());
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
                        Log.e("Validation ", "true");
                        return true;
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

    private boolean validateEmail() {

        email.setText(email.getText().toString().trim());
        if (Objects.requireNonNull(email.getText()).length() >= 8) {
            pattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
            matcher = pattern.matcher(email.getText());
            if (matcher.matches()) {
                email.setError(null);
                return true;
            } else {
                Snackbar.make(parent, "Email address is not valid", BaseTransientBottomBar.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Snackbar.make(parent, "Email address should be more than 8 character long", BaseTransientBottomBar.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult: " + requestCode);

        if (requestCode == Extras.GOOGLE_STATUS) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            //task = GoogleSignIn.getSignedInAccountFromIntent(data);
            // Signed in successfully, show authenticated UI.
            handleSignInResult(data);
        }

    }


    protected void handleSignInResult(Intent data) {
/*
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null && !account.getEmail().isEmpty()) {
                Log.e(TAG, "handleSignInResult: account respond");
                setupGoogleAccountForApplication(account);
            }
        } catch (ApiException exception) {
            Log.w(TAG, "signInResult:failed code=" + exception.getStatusCode());
            exception.printStackTrace();
        }*/

        try {
            GoogleSignIn.getSignedInAccountFromIntent(data).addOnCompleteListener(new OnCompleteListener<GoogleSignInAccount>() {
                @Override
                public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                    if (task.isSuccessful()) {
                        Log.e(TAG, "onComplete: " + task.getResult().getEmail());
                        CustomSharedPreference.getInstance(LoginActivity.this).setEmailID(task.getResult().getEmail());
                        CustomSharedPreference.getInstance(LoginActivity.this).setTokenApplication(task.getResult().getIdToken());
                        Toast.makeText(LoginActivity.this, "id " + task.getResult().getId(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(LoginActivity.this, "id token " + task.getResult().getIdToken(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    }
                }
            });
            // Signed in successfully, show authenticated UI.

        } catch (Exception e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            e.printStackTrace();
        }
    }

}