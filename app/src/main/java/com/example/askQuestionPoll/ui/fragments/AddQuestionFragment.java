package com.example.askQuestionPoll.ui.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.example.askQuestionPoll.R;
import com.example.askQuestionPoll.core.constants.Constants;
import com.example.askQuestionPoll.core.constants.MySingleTon;
import com.example.askQuestionPoll.core.interfaces.CountrySelectionInterface;
import com.example.askQuestionPoll.core.interfaces.ImageSourcePickerInterface;
import com.example.askQuestionPoll.core.pojo.AddQuestionResponseModel;
import com.example.askQuestionPoll.core.pojo.AddTextQuestionRequestModel;
import com.example.askQuestionPoll.core.utils.SystemUtils;
import com.example.askQuestionPoll.ui.activity.SplashActivity;
import com.example.askQuestionPoll.ui.dialog.CountryPickupDialog;
import com.example.askQuestionPoll.ui.dialog.ImageSourceSelectionDialog;
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
import com.optimumbrew.library.core.volley.MyVolley;

import java.net.ConnectException;
import java.util.List;

public class AddQuestionFragment extends Fragment implements ImageSourcePickerInterface, CountrySelectionInterface {

    private static final String TAG = "AddQuestionFragment";

    private Spinner categories, gender;
    private ImageView option1Image, option2Image;
    private ImageButton questionImage;
    private EditText questionDescription, option1Text, option2Text;
    private TextView questionDescriptionTextView, country;
    private Button preview, submit;
    private RelativeLayout imageLayout, textLayout;
    private RadioButton text, image;
    private View root;
    private ScrollView parent;

    private int whichImageView = -1;    //question image 0, option1 image 1, option2 image 2
    private String option1ImageUrl = "", option2ImageUrl = "", questionImageUrl = "", selectedCountry = "";
    private boolean isImageQuestion = false;

    private CameraImagePicker cameraImagePicker;
    private ImagePicker imagePicker;
    private String cameraPickerPath;
    private ProgressDialog progressDialog;

    public static CountryPickupDialog countryDialog;
    public static ImageSourceSelectionDialog imageDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_add_question, container, false);

        categories = root.findViewById(R.id.category_spinner_add_question_fragment);
        country = root.findViewById(R.id.country_add_question_fragment);
        gender = root.findViewById(R.id.gender_spinner_add_question_fragment);
        questionImage = root.findViewById(R.id.question_image_add_question_fragment);
        option1Image = root.findViewById(R.id.image_option1_add_question_fragment);
        option2Image = root.findViewById(R.id.image_option2_add_question_fragment);
        questionDescription = root.findViewById(R.id.question_description_add_question_fragment);
        option1Text = root.findViewById(R.id.text_option1_add_question_fragment);
        option2Text = root.findViewById(R.id.text_option2_add_question_fragment);
        preview = root.findViewById(R.id.preview_add_question_fragment);
        submit = root.findViewById(R.id.submit_add_question_fragment);
        text = root.findViewById(R.id.text_radio_add_question_fragment);
        image = root.findViewById(R.id.image_radio_add_question_fragment);
        imageLayout = root.findViewById(R.id.option_image_layout_add_question_fragment);
        textLayout = root.findViewById(R.id.option_text_layout_add_question_fragment);
        questionDescriptionTextView = root.findViewById(R.id.question_description_count_add_question_fragment);
        parent = root.findViewById(R.id.parent_add_question_fragment);

        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Fetching questions for you. Please wait");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        countryDialog = new CountryPickupDialog(requireContext(), this);
        imageDialog = new ImageSourceSelectionDialog(requireContext(), this);

        setupRadioButtons();
        setupDescription();

        setupSpinner(gender, SplashActivity.genderJson);
        setupSpinner(categories, SplashActivity.categoriesQuestions);

        country.setOnClickListener(view -> setupCountry());
        questionImage.setOnClickListener(view -> startImageSelection(0));
        option1Image.setOnClickListener(view -> startImageSelection(1));
        option2Image.setOnClickListener(view -> startImageSelection(2));

        submit.setOnClickListener(view -> {
            if (isImageQuestion) {
                setupImageQuestionsToNetwork();
            } else {
                setupTextQuestionsToNetwork();
            }
        });
        return root;
    }

    private void setupCountry() {
        if (countryDialog != null && !countryDialog.isShowing()) {
            countryDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            countryDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
            countryDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            countryDialog.setCanceledOnTouchOutside(false);
            countryDialog.show();
        }
    }

    private void setupImageQuestionsToNetwork() {

    }

    private void setupTextQuestionsToNetwork() {
        SystemUtils.hideSoftKeyboard(requireActivity());
        if (validateData()) {
            progressDialog.show();
            AddTextQuestionRequestModel model = new AddTextQuestionRequestModel();

            switch (gender.getSelectedItemPosition()) {
                case 0:
                    model.getRequest_data().setGender(3);
                    break;
                case 1:
                    model.getRequest_data().setGender(2);
                    break;
                case 2:
                    model.getRequest_data().setGender(1);
                    break;
            }
            model.getRequest_data().setOption_type(1);

            if (!country.getText().toString().trim().isEmpty())
                model.getRequest_data().setCountry(country.getText().toString());
            model.getRequest_data().setCategory_id(categories.getSelectedItemPosition());
            model.getRequest_data().setDescription(questionDescription.getText().toString());
            model.getRequest_data().setOption1(option1Text.getText().toString());
            model.getRequest_data().setOption2(option2Text.getText().toString());

            Log.e(TAG, "setupTextQuestionsToNetwork: " + new Gson().toJson(model));
            GsonRequest<AddQuestionResponseModel> request = new GsonRequest<AddQuestionResponseModel>
                    (1, Constants.API_ADD_QUESTION, new Gson().toJson(model), AddQuestionResponseModel.class, SplashActivity.headerData, new Response.Listener<AddQuestionResponseModel>() {
                        @Override
                        public void onResponse(AddQuestionResponseModel response) {
                            progressDialog.dismiss();
                            if (response.getCode() == 200) {
                                Toast.makeText(requireContext(), "Question added successfully", Toast.LENGTH_SHORT).show();
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
                                        SystemUtils.displayTokenExpireDialog(requireActivity());
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
            MySingleTon.getInstance(requireContext()).addGsonRequest(request);
        }
    }

    private boolean validateData() {
        if (!questionDescription.getText().toString().trim().isEmpty()) {
            if (isImageQuestion) {  //image api request validation
                if (!option1ImageUrl.trim().isEmpty()) {
                    if (option2ImageUrl.trim().isEmpty()) {
                        return true;
                    } else {
                        Snackbar.make(root, "Please, choose image for option 2", BaseTransientBottomBar.LENGTH_SHORT).show();
                        option2Image.requestFocus();
                        return false;
                    }
                } else {
                    Snackbar.make(root, "Please, choose image for option 1", BaseTransientBottomBar.LENGTH_SHORT).show();
                    option1Image.requestFocus();
                    return false;
                }
            } else {    //text api request validation
                if (!option1Text.getText().toString().trim().isEmpty()) {
                    if (!option2Text.getText().toString().trim().isEmpty()) {
                        return true;
                    } else {
                        Snackbar.make(root, "Option 2 can not be empty", BaseTransientBottomBar.LENGTH_SHORT).show();
                        option2Text.requestFocus();
                        return false;
                    }
                } else {
                    Snackbar.make(root, "Option 1 can not be empty", BaseTransientBottomBar.LENGTH_SHORT).show();
                    option1Text.requestFocus();
                    return false;
                }
            }
        } else {
            Snackbar.make(root, "Question description can not be empty.", BaseTransientBottomBar.LENGTH_SHORT).show();
            questionDescription.requestFocus();
            return false;
        }
    }


    private void setupDescription() {
        questionDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    int temp = charSequence.length();

                    if (temp >= 0 && temp <= 150) {
                        questionDescriptionTextView.setText(temp + "/200");
                        questionDescriptionTextView.setTextColor(Color.rgb(0, 0, 0));
                    }
                    if (temp > 150 && temp <= 200) {
                        questionDescriptionTextView.setText(temp + "/200");
                        questionDescriptionTextView.setTextColor(Color.rgb(255, 0, 0));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    private void startImageSelection(int i) {
        if (i > -1 && i < 3) {
            if (i == 0)
                whichImageView = 0;
            else if (i == 1)
                whichImageView = 1;
            else
                whichImageView = 2;
        }

        if (imageDialog != null && !imageDialog.isShowing()) {
            countryDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            countryDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
            countryDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            countryDialog.setCanceledOnTouchOutside(false);
            countryDialog.show();
        }
    }

    private void setupSpinner(Spinner spinner, String[] json) {
        if (json!=null && json.length > 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, json);
            adapter.setDropDownViewResource(R.layout.adapter_spinner_layout);
            //Setting the ArrayAdapter data on the Spinner
            spinner.setAdapter(adapter);
        }
    }

    private void setupRadioButtons() {
        text.setChecked(true);
        text.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    text.setChecked(true);
                    image.setChecked(false);
                    imageLayout.setVisibility(View.GONE);
                    textLayout.setVisibility(View.VISIBLE);
                    isImageQuestion = false;
                }
            }
        });

        image.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    image.setChecked(true);
                    text.setChecked(false);
                    textLayout.setVisibility(View.GONE);
                    imageLayout.setVisibility(View.VISIBLE);
                    isImageQuestion = true;
                }
            }
        });
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
                    ImageView temp = null;
                    switch (whichImageView) {
                        case 0:
                            temp = questionImage;
                            questionImageUrl = image.getOriginalPath();
                            break;
                        case 1:
                            temp = option1Image;
                            option1ImageUrl = image.getOriginalPath();
                            break;
                        case 2:
                            temp = option2Image;
                            option2ImageUrl = image.getOriginalPath();
                            break;
                        default:
                            break;
                    }
                    if (temp != null) {
                        Glide.with(requireContext()).load(image.getOriginalPath())
                                .error(R.drawable.app_load_image)
                                .into(temp);
                    }
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
                    ImageView temp = null;
                    switch (whichImageView) {
                        case 0:
                            temp = questionImage;
                            questionImageUrl = image.getOriginalPath();
                            break;
                        case 1:
                            temp = option1Image;
                            option1ImageUrl = image.getOriginalPath();
                            break;
                        case 2:
                            temp = option2Image;
                            option2ImageUrl = image.getOriginalPath();
                            break;
                        default:
                            break;
                    }
                    if (temp != null) {
                        Glide.with(requireContext()).load(image.getOriginalPath())
                                .error(R.drawable.app_load_image)
                                .into(temp);
                    }
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

    @Override
    public void getCountryData(String selectedCountry) {
        this.selectedCountry = selectedCountry;
        Toast.makeText(getContext(), "" + selectedCountry, Toast.LENGTH_SHORT).show();
        country.setText(selectedCountry);
    }

}