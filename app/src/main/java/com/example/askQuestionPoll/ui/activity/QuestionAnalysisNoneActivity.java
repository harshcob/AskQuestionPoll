package com.example.askQuestionPoll.ui.activity;

import static android.widget.Toast.LENGTH_LONG;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.example.askQuestionPoll.R;
import com.example.askQuestionPoll.core.constants.Constants;
import com.example.askQuestionPoll.core.constants.MySingleTon;
import com.example.askQuestionPoll.core.pojo.QuestionAnalysisResponseModel;
import com.example.askQuestionPoll.core.pojo.UniversalResponseModel;
import com.example.askQuestionPoll.core.utils.SystemUtils;
import com.example.askQuestionPoll.ui.fragments.ViewQuestionFragment;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonSyntaxException;
import com.optimumbrew.library.core.network.ConnectivityUtils;
import com.optimumbrew.library.core.volley.GsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;

import ir.mahozad.android.PieChart;

public class QuestionAnalysisNoneActivity extends AppCompatActivity {

    private TextView option1, option2, option1P, option2P;
    private ImageView optionImg1, optionImg2, report;
    private PieChart pieChart;
    private ProgressDialog progressDialog;
    private RelativeLayout imageLayout, textLayout;
    private LinearLayout back, parent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_analysis_none);

        option1 = findViewById(R.id.text_option1_analysis_question_activity);
        option2 = findViewById(R.id.text_option2_analysis_question_activity);
        optionImg1 = findViewById(R.id.image_option1_analysis_question_activity);
        optionImg2 = findViewById(R.id.image_option2_analysis_question_activity);
        option1P = findViewById(R.id.option1_percentage_analysis_question_activity);
        option2P = findViewById(R.id.option2_percentage_analysis_question_activity);
        imageLayout = findViewById(R.id.option_image_layout_analysis_question_activity);
        textLayout = findViewById(R.id.option_text_layout_analysis_question_activity);
        report = findViewById(R.id.report_question_analysis_question_activity);
        back = findViewById(R.id.exit_analysis_question_activity);
        parent = findViewById(R.id.parent_analysis_question_activity);

        progressDialog = new ProgressDialog(QuestionAnalysisNoneActivity.this, R.style.ProgressDialog);
        progressDialog.setMessage("Getting analysis report for you. Please wait");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        getAnalysisNoneDataFromNetwork();

        Intent intent = getIntent();
        String option1Text = intent.getStringExtra("option1Text");
        String option1Img = intent.getStringExtra("option1Img");

        String option2Text = intent.getStringExtra("option2Text");
        String option2Img = intent.getStringExtra("option2Img");

        if (!option1Text.isEmpty() || !option2Text.isEmpty()) {
            textLayout.setVisibility(View.VISIBLE);
            imageLayout.setVisibility(View.GONE);
            option1.setText(option1Text);
            option2.setText(option2Text);

        } else {
            imageLayout.setVisibility(View.VISIBLE);
            textLayout.setVisibility(View.GONE);
            Glide.with(QuestionAnalysisNoneActivity.this).load(option1Img).placeholder(R.drawable.app_load_image).into(optionImg1);
            Glide.with(QuestionAnalysisNoneActivity.this).load(option2Img).placeholder(R.drawable.app_load_image).into(optionImg2);
        }

        back.setOnClickListener(view -> finish());

        report.setOnClickListener(view -> reportQuestionToNetwork());
    }


    private void reportQuestionToNetwork() {
        JSONObject paramBody = new JSONObject();
        try {
            paramBody.put("question_id", ViewQuestionFragment.currentQuestionId);
        } catch (JSONException e) {

        }

        GsonRequest<UniversalResponseModel> request = new GsonRequest<UniversalResponseModel>
                (1, Constants.API_REPORT_QUESTION, paramBody.toString(), UniversalResponseModel.class, SplashActivity.headerData, new Response.Listener<UniversalResponseModel>() {
                    @Override
                    public void onResponse(UniversalResponseModel response) {
                        progressDialog.dismiss();
                        if (response.getCode() == 200) {
                            Toast.makeText(QuestionAnalysisNoneActivity.this, "Question reported successfully", Toast.LENGTH_SHORT).show();
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
                                    SystemUtils.displayTokenExpireDialog(QuestionAnalysisNoneActivity.this);
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
                                    SystemUtils.displayTokenExpireDialog(QuestionAnalysisNoneActivity.this);
                                }
                            } else {
                                Snackbar.make(parent, "This error never happens", BaseTransientBottomBar.LENGTH_SHORT).show();
                            }
                        }

                    }
                });

        if (!request.shouldCache())
            request.setShouldCache(true);
        MySingleTon.getInstance(QuestionAnalysisNoneActivity.this).addGsonRequest(request);
    }

    private void getAnalysisNoneDataFromNetwork() {
        JSONObject paramBody = new JSONObject();
        try {
            paramBody.put("question_id", ViewQuestionFragment.currentQuestionId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PieChart.Slice slice[] = new PieChart.Slice[2];
        GsonRequest<QuestionAnalysisResponseModel> request = new GsonRequest<QuestionAnalysisResponseModel>
                (1, Constants.API_ANALYSIS_NONE, paramBody.toString(), QuestionAnalysisResponseModel.class, SplashActivity.headerData, new Response.Listener<QuestionAnalysisResponseModel>() {
                    @Override
                    public void onResponse(QuestionAnalysisResponseModel response) {
                        progressDialog.dismiss();
                        Toast.makeText(QuestionAnalysisNoneActivity.this, response.getMessage(), LENGTH_LONG).show();
                        if (response.code == 200) {
                            option1P.setText("Option 1 total selection " + response.getData().getResult().get(0).getOption1());
                            option2P.setText("Option 2 total selection " + response.getData().getResult().get(0).getOption2());
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
                                    SystemUtils.displayTokenExpireDialog(QuestionAnalysisNoneActivity.this);
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
                                    SystemUtils.displayTokenExpireDialog(QuestionAnalysisNoneActivity.this);
                                }
                            } else {
                                Snackbar.make(parent, "This error never happens", BaseTransientBottomBar.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
        MySingleTon.getInstance(QuestionAnalysisNoneActivity.this).addGsonRequest(request);
    }
}