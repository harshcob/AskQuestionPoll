package com.example.askQuestionPoll.ui.fragments;

import static android.widget.Toast.LENGTH_LONG;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.example.askQuestionPoll.R;
import com.example.askQuestionPoll.core.constants.Constants;
import com.example.askQuestionPoll.core.constants.Extras;
import com.example.askQuestionPoll.core.constants.MySingleTon;
import com.example.askQuestionPoll.core.interfaces.ImageSourcePickerInterface;
import com.example.askQuestionPoll.core.pojo.LogoutResponseModel;
import com.example.askQuestionPoll.core.pojo.SettingResetPasswordRequestModel;
import com.example.askQuestionPoll.core.pojo.SettingResetPasswordResponseModel;
import com.example.askQuestionPoll.core.pojo.SettingUpdateProfileResponseModel;
import com.example.askQuestionPoll.core.sharedprefs.CustomSharedPreference;
import com.example.askQuestionPoll.core.utils.SystemUtils;
import com.example.askQuestionPoll.ui.activity.LoginActivity;
import com.example.askQuestionPoll.ui.activity.SplashActivity;
import com.example.askQuestionPoll.ui.activity.WebViewActivity;
import com.example.askQuestionPoll.ui.dialog.AppRatingDialog;
import com.example.askQuestionPoll.ui.dialog.ImageSourceSelectionDialog;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
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
import com.optimumbrew.library.core.volley.GsonRequest;
import com.optimumbrew.library.core.volley.PhotoMultipartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingsFragment extends Fragment implements ImageSourcePickerInterface {

    private static final String TAG = "SettingFragment";

    private EditText oldPassword, password, confirmPassword, name;
    private Spinner category;
    private ImageView logout, clickToAds, oldPasswordVisibleInVisible, newPasswordVisibleInVisible, confirmNewPasswordVisibleInVisible, profileImage;
    private TextView displayName, adsRewards;
    private Button saveProfile, submitPassword;
    private LinearLayout rating, privacyPolicy, termsCondition;
    private ScrollView parent;

    private CameraImagePicker cameraImagePicker;
    private ImagePicker imagePicker;
    private Typeface face;

    private ProgressDialog progressDialog;
    private AppRatingDialog ratingDialog;
    private ImageSourceSelectionDialog imageDialog;
    private AlertDialog.Builder alertDialog;

    private RewardedAd mRewardedAd;

    private boolean isPasswordVisible, isConfirmPasswordVisible;
    private String profileImagePickUrl = "", cameraPickerPath = "";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_setting, container, false);

        oldPassword = root.findViewById(R.id.old_password_setting_fragment);
        password = root.findViewById(R.id.new_password_setting_fragment);
        confirmPassword = root.findViewById(R.id.confirm_new_password_setting_fragment);
        name = root.findViewById(R.id.name_setting_fragment);
        category = root.findViewById(R.id.category_spinner_setting_fragment);
        clickToAds = root.findViewById(R.id.ads_show_setting_fragment);
        adsRewards = root.findViewById(R.id.ads_reward_setting_fragment);
        oldPasswordVisibleInVisible = root.findViewById(R.id.old_password_visible_invisible_setting_fragment);
        newPasswordVisibleInVisible = root.findViewById(R.id.new_password_visible_invisible_setting_fragment);
        confirmNewPasswordVisibleInVisible = root.findViewById(R.id.confirm_new_password_visible_invisible_setting_fragment);
        displayName = root.findViewById(R.id.user_name_setting_fragment);
        saveProfile = root.findViewById(R.id.save_user_profile_setting_fragment);
        submitPassword = root.findViewById(R.id.submit_password_setting_fragment);
        rating = root.findViewById(R.id.rating_button_setting_fragment);
        privacyPolicy = root.findViewById(R.id.privacy_policy_setting_fragment);
        termsCondition = root.findViewById(R.id.terms_condition_setting_fragment);
        logout = root.findViewById(R.id.logout_setting_fragment);
        profileImage = root.findViewById(R.id.profile_image_setting_fragment);
        parent = root.findViewById(R.id.parent_setting_fragment);


        adsRewards.setText(new StringBuilder().append("Coins").append(CustomSharedPreference.getInstance(requireContext()).getRewards()));
        face = ResourcesCompat.getFont(requireContext(), R.font.sf_atarian_system);
        progressDialog = new ProgressDialog(requireContext(), R.style.ProgressDialog);
        progressDialog.setMessage("Please wait");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        String nameTemp = CustomSharedPreference.getInstance(requireContext()).getUserName();
        displayName.setText(nameTemp);
        name.setHint(nameTemp);

        setupCategorySpinner();

        ratingDialog = new AppRatingDialog(requireContext());
        imageDialog = new ImageSourceSelectionDialog(requireContext(), this);

        oldPassword.setOnClickListener(view -> {
            setupPasswordVisibleInvisible(oldPassword, oldPasswordVisibleInVisible);
        });
        password.setOnClickListener(view -> {
            setupPasswordVisibleInvisible(password, newPasswordVisibleInVisible);
        });
        confirmPassword.setOnClickListener(view -> {
            setupPasswordVisibleInvisible(confirmPassword, confirmNewPasswordVisibleInVisible);
        });

        logout.setOnClickListener(view -> {
            alertDialog = new AlertDialog.Builder(requireContext());
            alertDialog.setTitle("ARE YOU SURE ?");
            alertDialog.setMessage("You want to logout ?");

            alertDialog.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            progressDialog.show();
                            logoutFromNetwork();
                        }
                    });

            alertDialog.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        });

        clickToAds.setOnClickListener(view -> {
            setupAdsFromNetwork();
        });

        profileImage.setOnClickListener(view -> {
            startImageSelection();
        });

        saveProfile.setOnClickListener(view -> {
            saveUserProfileToNetwork();
        });

        submitPassword.setOnClickListener(view -> {
            submitNewPasswordToNetwork();
        });

        rating.setOnClickListener(view -> {
            openRatingDialog();
        });

        privacyPolicy.setOnClickListener(view -> {
            openWebViewActivity();
        });

        termsCondition.setOnClickListener(view -> {
            openWebViewActivity();
        });

        return root;
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

    private void openWebViewActivity() {
        startActivity(new Intent(getContext(), WebViewActivity.class));
    }

    private void openRatingDialog() {

        if (ratingDialog != null && !ratingDialog.isShowing()) {
            ratingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            ratingDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
            ratingDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            ratingDialog.setCanceledOnTouchOutside(false);
            ratingDialog.show();
        }
    }

    private void submitNewPasswordToNetwork() {
        if (validatePassword()) {
            SettingResetPasswordRequestModel model = new SettingResetPasswordRequestModel();
            model.setEmail_id(CustomSharedPreference.getInstance(getContext()).getEmailID());
            model.setOld_password(oldPassword.getText().toString().trim());
            model.setNew_password(password.getText().toString().trim());

            GsonRequest<SettingResetPasswordResponseModel> request = new GsonRequest<SettingResetPasswordResponseModel>
                    (1, Constants.API_RESET_PASSWORD_USER, new Gson().toJson(model), SettingResetPasswordResponseModel.class,
                            SplashActivity.headerData, new Response.Listener<SettingResetPasswordResponseModel>() {
                        @Override
                        public void onResponse(SettingResetPasswordResponseModel response) {
                            progressDialog.dismiss();
                            Log.e(TAG, "response " + response);
                            oldPassword.setText("");
                            password.setText("");
                            confirmPassword.setText("");
                            Toast.makeText(getContext(), "Password reset successfully", LENGTH_LONG).show();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            progressDialog.dismiss();

                            if (error == null || error.getCause() == null) {
                                Snackbar.make(parent, "Server not respond", BaseTransientBottomBar.LENGTH_LONG).show();
                            } else if (error.getCause() instanceof JsonSyntaxException || error.getCause() instanceof IllegalStateException) {
                                if (error.getMessage() != null && !error.getMessage().isEmpty()) {
                                    if (error.getMessage().contains("expired") || error.getMessage().contains("blacklisted") || error.getMessage().contains("expired.")) {
                                        SystemUtils.displayTokenExpireDialog(requireActivity());
                                    }
                                } else {
                                    Snackbar.make(parent, "Please, restart app", BaseTransientBottomBar.LENGTH_LONG).show();
                                }
                            } else if (error.getCause() instanceof TimeoutError || error.getCause() instanceof NoConnectionError) {
                                Snackbar.make(parent, "Server timeout! Try again", BaseTransientBottomBar.LENGTH_SHORT).show();
                            } else if (ConnectivityUtils.isInternetConnected()) {
                                Snackbar.make(parent, "No internet connection", BaseTransientBottomBar.LENGTH_SHORT).show();
                            } else {
                                if (error.getMessage() != null && !error.getMessage().isEmpty()) {
                                    if (error.getMessage().contains("expired") || error.getMessage().contains("blacklisted") || error.getMessage().contains("expired.")) {
                                        SystemUtils.displayTokenExpireDialog(requireActivity());
                                    }
                                } else {
                                    Snackbar.make(parent, "This error never happens", BaseTransientBottomBar.LENGTH_SHORT).show();
                                }
                            }

                        }
                    });

            MySingleTon.getInstance(getActivity()).addGsonRequest(request);
        }
    }

    private void saveUserProfileToNetwork() {
        if (validateName()) {
            if (!profileImagePickUrl.isEmpty()) {

                JSONObject paramBody = new JSONObject();
                File tempFile = null;
                try {
                    paramBody.put("email_id", name.getText().toString());
                    if (profileImagePickUrl != null || !profileImagePickUrl.isEmpty())
                        tempFile = new File(profileImagePickUrl);
                    else
                        tempFile = new File("");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.e(TAG, "saveUserProfileToNetwork: " + tempFile.exists());
                Log.e(TAG, "saveUserProfileToNetwork: " + profileImagePickUrl);

                PhotoMultipartRequest<SettingUpdateProfileResponseModel> request = new PhotoMultipartRequest<SettingUpdateProfileResponseModel>
                        (Constants.API_EDIT_PROFILE_USER, "profile_img", tempFile, "name", paramBody.toString(), SettingUpdateProfileResponseModel.class,
                                SplashActivity.headerData, new Response.Listener<SettingUpdateProfileResponseModel>() {
                            @Override
                            public void onResponse(SettingUpdateProfileResponseModel response) {
                                progressDialog.dismiss();
                                if (response.getCode() == 200) {
                                    CustomSharedPreference.getInstance(getContext()).setUserName(name.getText().toString().trim());
                                    name.setHint(name.getText().toString());
                                    displayName.setText(name.getText().toString());
                                    name.setText("");

                                    Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
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
                                        if (error.getMessage().contains("expired") || error.getMessage().contains("blacklisted") || error.getMessage().contains("expired.")) {
                                            SystemUtils.displayTokenExpireDialog(requireActivity());
                                        }
                                    } else {
                                        Snackbar.make(parent, "Please, restart app", BaseTransientBottomBar.LENGTH_LONG).show();
                                    }
                                } else if (error.getCause() instanceof TimeoutError || error.getCause() instanceof NoConnectionError) {
                                    Snackbar.make(parent, "Server timeout! Try again", BaseTransientBottomBar.LENGTH_SHORT).show();
                                } else if (ConnectivityUtils.isInternetConnected()) {
                                    Snackbar.make(parent, "No internet connection", BaseTransientBottomBar.LENGTH_SHORT).show();
                                } else {
                                    if (error.getMessage() != null && !error.getMessage().isEmpty()) {
                                        if (error.getMessage().contains("expired") || error.getMessage().contains("blacklisted") || error.getMessage().contains("expired.")) {
                                            SystemUtils.displayTokenExpireDialog(requireActivity());
                                        }
                                    } else {
                                        Snackbar.make(parent, "This error never happens", BaseTransientBottomBar.LENGTH_SHORT).show();
                                    }
                                }


                            }
                        });
                if (!request.shouldCache())
                    request.setShouldCache(true);
                MySingleTon.getInstance(getContext()).addPhotoMultiPartRequest(request);
            } else {
                Snackbar.make(parent, "Please, select profile image", BaseTransientBottomBar.LENGTH_SHORT).show();
            }
        }
    }


    private void setupAdsFromNetwork() {

        MobileAds.initialize(requireContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                if (!initializationStatus.getAdapterStatusMap().isEmpty()) {
                    AdRequest adRequest = new AdRequest.Builder().build();
                    adRequest.isTestDevice(requireContext());
                    RewardedAd.load(requireContext(), Extras.AD_MOB, adRequest, new RewardedAdLoadCallback() {

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            super.onAdLoaded(rewardedAd);
                            Log.e(TAG, "onAdFailedToLoad: load " + rewardedAd.getAdUnitId());
                            Log.e(TAG, "onAdFailedToLoad:  load " + rewardedAd.getResponseInfo());
                            Log.e(TAG, "onAdFailedToLoad:  load " + rewardedAd.getRewardItem());

                            mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdShowedFullScreenContent() {
                                    // Called when ad is shown.
                                    Log.d(TAG, "Ad was shown.");
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(AdError adError) {
                                    // Called when ad fails to show.
                                    Log.d(TAG, "Ad failed to show.");
                                }

                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    // Called when ad is dismissed.
                                    // Set the ad reference to null so you don't show the ad a second time.
                                    Log.d(TAG, "Ad was dismissed.");
                                    mRewardedAd = null;
                                }
                            });

                            mRewardedAd.show(requireActivity(), new OnUserEarnedRewardListener() {
                                @Override
                                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                    Log.d(TAG, "The user earned the reward.");
                                    CustomSharedPreference.getInstance(requireContext()).setRewards(CustomSharedPreference.getInstance(requireContext()).getRewards() + rewardItem.getAmount());
                                    adsRewards.setText(CustomSharedPreference.getInstance(requireContext()).getRewards());
                                }

                            });

                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            mRewardedAd = null;
                        }

                    });
                }
            }
        });


    }

    private void setupCategorySpinner() {
        if (SplashActivity.categoriesQuestions.length > 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, SplashActivity.categoriesQuestions);
            adapter.setDropDownViewResource(R.layout.adapter_spinner_layout);
            //Setting the ArrayAdapter data on the Spinner
            category.setAdapter(adapter);
        }
    }

    private void logoutFromNetwork() {
        GsonRequest<LogoutResponseModel> request = new GsonRequest<LogoutResponseModel>
                (1, Constants.API_LOGOUT, null, LogoutResponseModel.class, SplashActivity.headerData, new Response.Listener<LogoutResponseModel>() {
                    @Override
                    public void onResponse(LogoutResponseModel response) {
                        progressDialog.dismiss();
                        Log.e(TAG, "response " + response);
                        Toast.makeText(getContext(), response.getMessage(), LENGTH_LONG).show();
                        if (response.code == 200) {
                            CustomSharedPreference.getInstance(getContext()).setTokenApplication("");
                            CustomSharedPreference.getInstance(getContext()).setLogin(false);
                            requireActivity().startActivity(new Intent(requireActivity().getApplicationContext(), LoginActivity.class));
                            requireActivity().finish();
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
                                if (error.getMessage().contains("expired") || error.getMessage().contains("blacklisted") || error.getMessage().contains("expired.")) {
                                    SystemUtils.displayTokenExpireDialog(requireActivity());
                                }
                            } else {
                                Snackbar.make(parent, "Please, restart app", BaseTransientBottomBar.LENGTH_LONG).show();
                            }
                        } else if (error.getCause() instanceof TimeoutError || error.getCause() instanceof NoConnectionError) {
                            Snackbar.make(parent, "Server timeout! Try again", BaseTransientBottomBar.LENGTH_SHORT).show();
                        } else if (ConnectivityUtils.isInternetConnected()) {
                            Snackbar.make(parent, "No internet connection", BaseTransientBottomBar.LENGTH_SHORT).show();
                        } else {
                            if (error.getMessage() != null && !error.getMessage().isEmpty()) {
                                if (error.getMessage().contains("expired") || error.getMessage().contains("blacklisted") || error.getMessage().contains("expired.")) {
                                    SystemUtils.displayTokenExpireDialog(requireActivity());
                                }
                            } else {
                                Snackbar.make(parent, "This error never happens", BaseTransientBottomBar.LENGTH_SHORT).show();
                            }
                        }

                    }
                });

        MySingleTon.getInstance(getActivity()).addGsonRequest(request);
    }


    private void startImageSelection() {
        if (imageDialog != null && !imageDialog.isShowing()) {
            imageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            imageDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
            imageDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            imageDialog.setCanceledOnTouchOutside(false);
            imageDialog.show();
        }
    }


    private boolean validatePassword() {
        password.setText(password.getText().toString().trim());
        confirmPassword.setText(confirmPassword.getText().toString().trim());
        if (Objects.requireNonNull(password.getText()).length() >= 8) {
            Pattern pattern = Pattern.compile("(.*[0-9].*)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(password.getText());

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


    public void requestPermission(boolean i) {
        if (i) {
            //gallery choose
            Dexter.withContext(requireContext())
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
            Dexter.withContext(requireContext())
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
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("This App needs permission to enable some functionalities. You can grant them in app setting");
        builder.setTitle("Need Permission");
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.setPositiveButton("Goto Settings", (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", requireContext().getPackageName(), null);
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
        cameraImagePicker = new CameraImagePicker(this);
        cameraImagePicker.setImagePickerCallback(new ImagePickerCallback() {
            @Override
            public void onImagesChosen(List<ChosenImage> list) {
                for (ChosenImage image : list) {
                    Log.e(TAG, "Camera Message  " + image.getOriginalPath());
                    profileImagePickUrl = image.getOriginalPath();
                    Glide.with(requireContext()).load(image.getOriginalPath())
                            .error(R.drawable.app_load_image)
                            .into(profileImage);
                }
            }

            @Override
            public void onError(String s) {
                Toast.makeText(requireContext(), "Something wrong happens in image parsing. Try later", Toast.LENGTH_SHORT).show();
                Log.e(TAG, s);
            }
        });
        cameraPickerPath = cameraImagePicker.pickImage();
    }

    public void pickImageFromGallery() {

        imagePicker = new ImagePicker(this);
        imagePicker.setImagePickerCallback(new ImagePickerCallback() {
            @Override
            public void onImagesChosen(List<ChosenImage> list) {
                for (ChosenImage image : list) {
                    Log.e(TAG, "Gallery Message  " + image.getOriginalPath());
                    profileImagePickUrl = image.getOriginalPath();
                    Glide.with(requireContext()).load(image.getOriginalPath())
                            .error(R.drawable.app_load_image)
                            .into(profileImage);

                }
            }

            @Override
            public void onError(String s) {
                Toast.makeText(requireContext(), "Something wrong happens in image parsing. Try later", Toast.LENGTH_SHORT).show();
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