package com.example.askQuestionPoll.ui.activity;

import static android.widget.Toast.LENGTH_LONG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.example.askQuestionPoll.R;
import com.example.askQuestionPoll.core.constants.Constants;
import com.example.askQuestionPoll.core.interfaces.ImageSourcePickerInterface;
import com.example.askQuestionPoll.core.pojo.SignUpRequestModel;
import com.example.askQuestionPoll.core.pojo.SignUpResponseModel;
import com.example.askQuestionPoll.core.sharedprefs.CustomSharedPreference;
import com.example.askQuestionPoll.core.utils.SystemUtils;
import com.example.askQuestionPoll.ui.dialog.ImageSourceSelectionDialog;
import com.example.askQuestionPoll.ui.dialog.OtpVerificationDialog;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.kbeanie.multipicker.api.CameraImagePicker;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;
import com.optimumbrew.library.core.network.ConnectivityUtils;
import com.optimumbrew.library.core.volley.MyVolley;
import com.optimumbrew.library.core.volley.PhotoMultipartRequest;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignupActivity extends AppCompatActivity implements ImageSourcePickerInterface {

    private static final String TAG = "SignupActivity";

    private EditText name, email, password, confirmPassword;
    private Spinner country;
    private RadioButton male, female;
    private Button signup;
    private LinearLayout parent;
    private ImageView back, passwordVisible, confirmPasswordVisible;

    public static Boolean isPasswordVisible = false, isConfirmPasswordVisible = false;
    public static String signUpImagePickUrl="";

    public static CircleImageView profileImage;
    private Pattern pattern;
    private Matcher matcher;

    private ImagePicker imagePicker;
    private CameraImagePicker cameraImagePicker;
    private String cameraPickerPath;
    private MyVolley customVolleyQueue;
    private ProgressDialog progressDialog;
    private Typeface face;
    private OtpVerificationDialog otpDialog;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        name = findViewById(R.id.name_signup_activity);
        email = findViewById(R.id.email_signup_activity);
        password = findViewById(R.id.password_signup_activity);
        confirmPassword = findViewById(R.id.confirm_password_signup_activity);
        parent = findViewById(R.id.parent_signup_activity);
        country = findViewById(R.id.country_spinner_signup_activity);
        male = findViewById(R.id.male_signup_activity);
        female = findViewById(R.id.female_signup_activity);
        signup = findViewById(R.id.submit_signup_activity);
        profileImage = findViewById(R.id.profile_image_signup_activity);
        back = findViewById(R.id.back_signup_activity);
        passwordVisible = findViewById(R.id.password_visible_invisible_signup_activity);
        confirmPasswordVisible = findViewById(R.id.confirm_password_visible_invisible_signup_activity);

        face = ResourcesCompat.getFont(SignupActivity.this, R.font.sf_atarian_system);
        customVolleyQueue = MyVolley.getInstance(SignupActivity.this);

        progressDialog = new ProgressDialog(this,R.style.ProgressDialog);
        progressDialog.setMessage("Creating account for you. Please wait");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        otpDialog  = new OtpVerificationDialog(SignupActivity.this);

        if (signUpImagePickUrl!=null && !signUpImagePickUrl.trim().isEmpty()){
            Glide.with(this).load(signUpImagePickUrl).into(profileImage);
        }
        back.setOnClickListener(view -> finish());

        signup.setOnClickListener(view -> {
            SystemUtils.hideSoftKeyboard(SignupActivity.this);
            if (validateData()) {
                progressDialog.show();
                signUpFromNetwork();
            }
        });

        profileImage.setOnClickListener(view -> startImageSelection());
        setupGender();
        setupCountrySpinner();

        passwordVisible.setOnClickListener(view -> {
            setupPasswordVisibleInvisible(password, passwordVisible);
        });

        confirmPasswordVisible.setOnClickListener(view -> {
            setupPasswordVisibleInvisible(confirmPassword, confirmPasswordVisible);
        });


    }

    private void setupPasswordVisibleInvisible(EditText text, ImageView textImage) {
        if (text.getText().toString().trim().isEmpty()) {
            text.requestFocus();
        } else {
            if (isPasswordVisible || isConfirmPasswordVisible) {
                isConfirmPasswordVisible = false;
                isPasswordVisible = false;    //to run else or invisible password
                text.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                textImage.setImageResource(R.drawable.ic_round_eye_hide);
            } else {
                isConfirmPasswordVisible = true;
                isPasswordVisible = true; //to run if or visible password
                text.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                textImage.setImageResource(R.drawable.ic_round_eye);
            }
            text.setSelection(text.getText().length());
            text.setTypeface(face);
        }
    }



    private void signUpFromNetwork() {

        String[] temp = Objects.requireNonNull(name.getText()).toString().trim().split(" ");
        String firstName, lastName;
        if (temp.length >= 2) {
            firstName = temp[0];
            lastName = temp[1];
        } else {
            firstName = name.getText().toString();
            lastName = "";
        }

        int genderTemp;
        if (male.isChecked()) {
            genderTemp = 1;
        } else {
            genderTemp = 2;
        }

        SignUpRequestModel model = new SignUpRequestModel();
        model.setCountry(SplashActivity.countryJson[country.getSelectedItemPosition()]);
        model.setEmail_id(Objects.requireNonNull(email.getText()).toString());
        model.setPassword(Objects.requireNonNull(password.getText()).toString());
        model.setFirst_name(firstName);
        model.setLast_name(lastName);
        model.setGender(genderTemp);

        File imageFile = new File(signUpImagePickUrl);
        PhotoMultipartRequest<SignUpResponseModel> multiPartRequest =
                new PhotoMultipartRequest<SignUpResponseModel>(Constants.API_SIGNUP, "profile_img", imageFile, "request_data",
                        new Gson().toJson(model), SignUpResponseModel.class, null, new Response.Listener<SignUpResponseModel>() {
                    @Override
                    public void onResponse(SignUpResponseModel response) {
                        progressDialog.dismiss();
                        Log.e(TAG, response.toString());
                        if (response.getCode() == 200) {
                            Toast.makeText(SignupActivity.this, "Account created successfully. Verify now", Toast.LENGTH_SHORT).show();
                            CustomSharedPreference.getInstance(SignupActivity.this).setTempUserId(response.getData().getUser_reg_temp_id());
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
                            if (error.getMessage()!=null && !error.getMessage().isEmpty()){
                                if (error.getMessage().contains("expired") || error.getMessage().contains("blacklisted") || error.getMessage().contains("expired.")){
                                    SystemUtils.displayTokenExpireDialog(SignupActivity.this);
                                }
                            }else {
                                Snackbar.make(parent, "Please, restart app", BaseTransientBottomBar.LENGTH_LONG).show();
                            }
                        } else if (error.getCause() instanceof TimeoutError || error.getCause() instanceof NoConnectionError) {
                            Snackbar.make(parent, "Server timeout! Try again", BaseTransientBottomBar.LENGTH_SHORT).show();
                        } else if (ConnectivityUtils.isInternetConnected()) {
                            Snackbar.make(parent, "No internet connection", BaseTransientBottomBar.LENGTH_SHORT).show();
                        } else {
                            if (error.getMessage()!=null && !error.getMessage().isEmpty()){
                                if (error.getMessage().contains("expired") || error.getMessage().contains("blacklisted") || error.getMessage().contains("expired.")){
                                    SystemUtils.displayTokenExpireDialog(SignupActivity.this);
                                } else if (error.getMessage().contains("Email already Exist")){
                                    Snackbar.make(parent, "Email already Exist", BaseTransientBottomBar.LENGTH_SHORT).show();
                                }
                            } else {
                                Snackbar.make(parent, "This error never happens", BaseTransientBottomBar.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
        customVolleyQueue.addToRequestQueue(multiPartRequest);
    }

    private void openOtpVerificationDialog() {

        if (otpDialog != null && !otpDialog.isShowing()) {
            otpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            otpDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
            otpDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            otpDialog.setCanceledOnTouchOutside(false);
            otpDialog.setSignup(true);
            otpDialog.show();
        }
    }


    private boolean validateData() {
        if (validateName()) {
            if (validateEmail()) {
                if (validatePassword()) {
                    if (!signUpImagePickUrl.isEmpty()) {
                        Log.e(TAG, "success validation");
                        return true;
                    } else {
                        Snackbar.make(parent, "Please, choose image then try again", BaseTransientBottomBar.LENGTH_SHORT).show();
                    }
                }
            }
        }
        return false;
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
                            Snackbar.make(parent, "Confirm password should same as password", BaseTransientBottomBar.LENGTH_SHORT).show();
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
                email.requestFocus();
                return false;
            }
        } else {
            Snackbar.make(parent, "Email address should be more than 8 character long", BaseTransientBottomBar.LENGTH_SHORT).show();
            email.requestFocus();
            return false;
        }
    }

    private boolean validateName() {
        name.setText(name.getText().toString().trim());
        if (!name.getText().toString().trim().isEmpty()) {
            if (name.getText().length() > 3) {
                name.setError(null);
                return true;
            } else {
                Snackbar.make(parent, "Name should be more than 3 character long", BaseTransientBottomBar.LENGTH_SHORT).show();
                name.requestFocus();
                return false;
            }
        } else {
            Snackbar.make(parent, "Name can not be empty", BaseTransientBottomBar.LENGTH_SHORT).show();
            name.requestFocus();
            return false;
        }

    }


    private void startImageSelection() {
        ImageSourceSelectionDialog otpDialog = new ImageSourceSelectionDialog(SignupActivity.this, this);
        if (!otpDialog.isShowing()) {
            otpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            otpDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
            otpDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            otpDialog.setCanceledOnTouchOutside(false);
            otpDialog.show();
        }
    }

    private void setupGender() {
        male.setChecked(true);
        male.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                female.setChecked(false);
            }
        });
        female.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                male.setChecked(false);
            }
        });
    }

    private void setupCountrySpinner() {
        if (SplashActivity.countryJson.length > 0) {
            ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, SplashActivity.countryJson);
            countryAdapter.setDropDownViewResource(R.layout.adapter_spinner_layout);
            //Setting the ArrayAdapter data on the Spinner
            country.setAdapter(countryAdapter);
        }
    }


    public void requestPermission(boolean i) {

        if (i) {
            //gallery choose
            Dexter.withContext(SignupActivity.this)
                    .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            pickImageFromGallery();
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            if (response.isPermanentlyDenied()) {
                                openDialogOfSettings();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).check();

        } else {
            //camera choose
            Dexter.withContext(SignupActivity.this)
                    .withPermission(Manifest.permission.CAMERA)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            pickImageFromCamera();
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            if (response.isPermanentlyDenied()) {
                                openDialogOfSettings();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).check();
        }
    }

    private void openDialogOfSettings() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
        builder.setMessage("This App needs permission to enable some functionalities. You can grant them in app setting");
        builder.setTitle("Need Permission");
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.setPositiveButton("Goto Settings", (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
            dialog.dismiss();
        });
        AlertDialog diag = builder.create();
        //Display the message!
        diag.show();
        diag.setCanceledOnTouchOutside(true);
    }


    public void pickImageFromCamera() {
        cameraImagePicker = new CameraImagePicker(SignupActivity.this);
        cameraImagePicker.setImagePickerCallback(new ImagePickerCallback() {
            @Override
            public void onImagesChosen(List<ChosenImage> list) {
                for (ChosenImage image : list) {
                    signUpImagePickUrl = image.getOriginalPath();
                    Log.e(TAG, "Camera Message  " + image.getOriginalPath());
                    Glide.with(parent).load(image.getOriginalPath())
                            .error(R.drawable.app_load_image)
                            .into(profileImage);
                }
            }

            @Override
            public void onError(String s) {
                Snackbar.make(parent, "Something wrong happens in image parsing. Try later", BaseTransientBottomBar.LENGTH_SHORT).show();
                Log.e(TAG, s);
            }
        });
        cameraPickerPath = cameraImagePicker.pickImage();
    }

    public void pickImageFromGallery() {

        imagePicker = new ImagePicker(SignupActivity.this);
        imagePicker.setImagePickerCallback(new ImagePickerCallback() {
            @Override
            public void onImagesChosen(List<ChosenImage> list) {
                for (ChosenImage image : list) {
                    signUpImagePickUrl = image.getOriginalPath();
                    Log.e(TAG, "Gallery Message  " + image.getOriginalPath());
                    Glide.with(parent).load(image.getOriginalPath())
                            .error(R.drawable.app_load_image)
                            .into(profileImage);
                }
            }

            @Override
            public void onError(String s) {
                Snackbar.make(parent, "Something wrong happens in image parsing. Try later", BaseTransientBottomBar.LENGTH_SHORT).show();
                Log.e(TAG, s);
            }
        });
        imagePicker.pickImage();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {    //handling image result after picking image
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Picker.PICK_IMAGE_DEVICE) {
                if (imagePicker == null) {
                    pickImageFromGallery();
                }
                imagePicker.submit(data);
            }
            if (requestCode == Picker.PICK_IMAGE_CAMERA) {
                if (cameraImagePicker == null) {
                    pickImageFromCamera();
                    cameraImagePicker.reinitialize(cameraPickerPath);
                }
                cameraImagePicker.submit(data);
            }
        }
    }
}