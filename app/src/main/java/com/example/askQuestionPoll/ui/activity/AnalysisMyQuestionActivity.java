package com.example.askQuestionPoll.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.example.askQuestionPoll.R;
import com.example.askQuestionPoll.core.constants.Constants;
import com.example.askQuestionPoll.core.constants.MySingleTon;
import com.example.askQuestionPoll.core.pojo.AnalysisMyQuestionCountryResponseModel;
import com.example.askQuestionPoll.core.pojo.AnalysisMyQuestionGenderResponseModel;
import com.example.askQuestionPoll.core.pojo.AnalysisMyQuestionsNoneResponseModel;
import com.example.askQuestionPoll.core.pojo.UniversalResponseModel;
import com.example.askQuestionPoll.core.pojo.ViewQuestionAddAnswerRequestModel;
import com.example.askQuestionPoll.core.utils.SystemUtils;
import com.example.askQuestionPoll.ui.fragments.AccountFragment;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.optimumbrew.library.core.network.ConnectivityUtils;
import com.optimumbrew.library.core.volley.GsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;

public class AnalysisMyQuestionActivity extends AppCompatActivity {

    private static final String TAG = "AnalysisMyQuestionActivity";

    private ProgressDialog progressDialog;
    private RadioButton country, gender, none;
    private TextView radioButtonSelection;
    private RecyclerView recyclerView;
    private ScrollView parent;

    public static boolean isGenderSelected = false, isNoneSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_question_analysis);

        country = findViewById(R.id.country_my_question_analysis_activity);
        gender = findViewById(R.id.gender_my_question_analysis_activity);
        none = findViewById(R.id.none_my_question_analysis_activity);
        radioButtonSelection = findViewById(R.id.selection_text_my_question_analysis_activity);
        recyclerView = findViewById(R.id.recycler_view_my_question_analysis_activity);
        parent = findViewById(R.id.parent_my_question_analysis_activity);

        progressDialog = new ProgressDialog(AnalysisMyQuestionActivity.this, R.style.ProgressDialog);
        progressDialog.setMessage("Fetching questions for you. Please wait");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        country.setChecked(true);

        getDataFromNetwork();

        setupRadioButtons();

    }

    private void setupRadioButtons() {
        country.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    isNoneSelected = false;
                    isGenderSelected = false;
                    country.setChecked(true);
                    gender.setChecked(false);
                    none.setChecked(false);

                    getDataFromNetwork();
                    progressDialog.show();
                }
            }
        });
        gender.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    isGenderSelected = true;
                    isNoneSelected = false;
                    country.setChecked(false);
                    gender.setChecked(true);
                    none.setChecked(false);

                    getDataFromNetwork();
                    progressDialog.show();
                }
            }
        });
        none.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    isNoneSelected = true;
                    isGenderSelected = false;
                    country.setChecked(false);
                    gender.setChecked(false);
                    none.setChecked(true);

                    getDataFromNetwork();
                    progressDialog.show();
                }
            }
        });
    }

    private void getDataFromNetwork() {
        JSONObject paramBody = new JSONObject();
        try {
              paramBody.put("question_id", AccountFragment.selectedQuestionId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (isNoneSelected){

            GsonRequest<AnalysisMyQuestionsNoneResponseModel> request = new GsonRequest<AnalysisMyQuestionsNoneResponseModel>
                    (1, Constants.API_ANALYSIS_NONE, paramBody.toString(), AnalysisMyQuestionsNoneResponseModel.class, SplashActivity.headerData, new Response.Listener<AnalysisMyQuestionsNoneResponseModel>() {
                        @Override
                        public void onResponse(AnalysisMyQuestionsNoneResponseModel response) {
                            progressDialog.dismiss();
                            if (response.getCode() == 200) {
                                Snackbar.make(getCurrentFocus(), "Successfully res", BaseTransientBottomBar.LENGTH_SHORT).show();
                                Intent intent = new Intent(AnalysisMyQuestionActivity.this, QuestionAnalysisNoneActivity.class);

                                startActivity(intent);
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
                                        SystemUtils.displayTokenExpireDialog(AnalysisMyQuestionActivity.this);
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
                                        SystemUtils.displayTokenExpireDialog(AnalysisMyQuestionActivity.this);
                                    }
                                } else {
                                    Snackbar.make(parent, "This error never happens", BaseTransientBottomBar.LENGTH_SHORT).show();
                                }
                            }

                        }
                    });

            if (!request.shouldCache())
                request.setShouldCache(true);
            MySingleTon.getInstance(AnalysisMyQuestionActivity.this).addGsonRequest(request);

        } else if (isGenderSelected){

            GsonRequest<AnalysisMyQuestionGenderResponseModel> request = new GsonRequest<AnalysisMyQuestionGenderResponseModel>
                    (1, Constants.API_ANALYSIS_GENDER, paramBody.toString(), AnalysisMyQuestionGenderResponseModel.class, SplashActivity.headerData, new Response.Listener<AnalysisMyQuestionGenderResponseModel>() {
                        @Override
                        public void onResponse(AnalysisMyQuestionGenderResponseModel response) {
                            progressDialog.dismiss();
                            if (response.getCode() == 200) {
                                Snackbar.make(getCurrentFocus(), "Successfully res", BaseTransientBottomBar.LENGTH_SHORT).show();
                                Intent intent = new Intent(AnalysisMyQuestionActivity.this, QuestionAnalysisNoneActivity.class);

                                startActivity(intent);
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
                                        SystemUtils.displayTokenExpireDialog(AnalysisMyQuestionActivity.this);
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
                                        SystemUtils.displayTokenExpireDialog(AnalysisMyQuestionActivity.this);
                                    }
                                } else {
                                    Snackbar.make(parent, "This error never happens", BaseTransientBottomBar.LENGTH_SHORT).show();
                                }
                            }

                        }
                    });

            if (!request.shouldCache())
                request.setShouldCache(true);
            MySingleTon.getInstance(AnalysisMyQuestionActivity.this).addGsonRequest(request);

        } else {

            GsonRequest<AnalysisMyQuestionCountryResponseModel> request = new GsonRequest<AnalysisMyQuestionCountryResponseModel>
                    (1, Constants.API_ANALYSIS_COUNTRY, paramBody.toString(), AnalysisMyQuestionCountryResponseModel.class, SplashActivity.headerData, new Response.Listener<AnalysisMyQuestionCountryResponseModel>() {
                        @Override
                        public void onResponse(AnalysisMyQuestionCountryResponseModel response) {
                            progressDialog.dismiss();
                            if (response.getCode() == 200) {
                                Snackbar.make(getCurrentFocus(), "Successfully res", BaseTransientBottomBar.LENGTH_SHORT).show();
                                Intent intent = new Intent(AnalysisMyQuestionActivity.this, QuestionAnalysisNoneActivity.class);

                                startActivity(intent);
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
                                        SystemUtils.displayTokenExpireDialog(AnalysisMyQuestionActivity.this);
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
                                        SystemUtils.displayTokenExpireDialog(AnalysisMyQuestionActivity.this);
                                    }
                                } else {
                                    Snackbar.make(parent, "This error never happens", BaseTransientBottomBar.LENGTH_SHORT).show();
                                }
                            }


                        }
                    });

            if (!request.shouldCache())
                request.setShouldCache(true);
            MySingleTon.getInstance(AnalysisMyQuestionActivity.this).addGsonRequest(request);
        }
    }
}