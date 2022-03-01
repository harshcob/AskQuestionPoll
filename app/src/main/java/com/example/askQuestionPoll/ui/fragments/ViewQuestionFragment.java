package com.example.askQuestionPoll.ui.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.askQuestionPoll.R;
import com.example.askQuestionPoll.core.constants.Constants;
import com.example.askQuestionPoll.core.pojo.LoginResponseModelAlternativeDataResult;
import com.example.askQuestionPoll.core.pojo.UniversalResponseModel;
import com.example.askQuestionPoll.core.pojo.ViewQuestionAddAnswerRequestModel;
import com.example.askQuestionPoll.core.pojo.ViewQuestionResponseModel;
import com.example.askQuestionPoll.core.pojo.ViewQuestionResponseModelAlternative;
import com.example.askQuestionPoll.core.pojo.ViewQuestionResponseModelResult;
import com.example.askQuestionPoll.core.utils.SystemUtils;
import com.example.askQuestionPoll.ui.activity.QuestionAnalysisNoneActivity;
import com.example.askQuestionPoll.ui.activity.SplashActivity;
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
import java.util.ArrayList;

public class ViewQuestionFragment extends Fragment {

    private static final String TAG = "ViewQuestionFragment";

    private View root;
    private ImageView report, questionImage, option1Img, option2Img;
    private TextView option1Text, option2Text, questionDescription, noData;
    private LinearLayout skip, questionLayout, parent;
    private RelativeLayout imageLayout, textLayout;

    private MyVolley customVolleyQueue;
    private ProgressDialog progressDialog;
    private AlertDialog.Builder alertDialog;

    private int totalCount = 0;
    private boolean alertDialogClicked = false;

    public static boolean isAlternative = false;
    public static int currentPosition = -1, currentQuestionId;
    public static ArrayList<ViewQuestionResponseModelResult>[] result;
    public static ArrayList<LoginResponseModelAlternativeDataResult> resultAlternative;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_view_questions, container, false);
        if (container != null)
            customVolleyQueue = MyVolley.getInstance(container.getContext());

        report = root.findViewById(R.id.report_question_view_question_fragment);
        questionImage = root.findViewById(R.id.question_image_view_question_fragment);
        option1Img = root.findViewById(R.id.image_option1_view_question_fragment);
        option2Img = root.findViewById(R.id.image_option2_view_question_fragment);
        option1Text = root.findViewById(R.id.text_option1_view_question_fragment);
        option2Text = root.findViewById(R.id.text_option2_view_question_fragment);
        questionDescription = root.findViewById(R.id.question_description_view_question_fragment);
        skip = root.findViewById(R.id.skip_question_view_question_fragment);
        imageLayout = root.findViewById(R.id.option_image_layout_view_question_fragment);
        textLayout = root.findViewById(R.id.option_text_layout_view_question_fragment);
        questionLayout = root.findViewById(R.id.question_layout_view_question_fragment);
        noData = root.findViewById(R.id.no_data_view_question_fragment);
        parent = root.findViewById(R.id.parent_view_question_fragment);

        alertDialog = new AlertDialog.Builder(requireContext());
        progressDialog = new ProgressDialog(requireContext(), R.style.ProgressDialog);
        progressDialog.setMessage("Fetching questions for you. Please wait");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();


        getQuestionsFromNetwork();


        report.setOnClickListener(view -> {
            progressDialog.setMessage("Reporting this question");
            progressDialog.show();
            reportQuestionToNetwork();
        });

        option1Img.setOnClickListener(view -> {
            progressDialog.show();
            setupAlertDialog(1);
        });
        option2Img.setOnClickListener(view -> {
            progressDialog.show();
            setupAlertDialog(2);
        });
        option1Text.setOnClickListener(view -> {
            progressDialog.show();
            setupAlertDialog(1);
        });
        option2Text.setOnClickListener(view -> {
            progressDialog.show();
            setupAlertDialog(2);
        });

        skip.setOnClickListener(view -> {
            /*Log.e(TAG, "skipQuestion count: "+count +" size "+result.size());
            if (count == result.size()-1){
                Snackbar.make(parent,"You are on last question",BaseTransientBottomBar.LENGTH_SHORT).show();
                skip.setVisibility(View.GONE);
            } else if (count == result.size()-1){
                Snackbar.make(parent,"You are on second last question",BaseTransientBottomBar.LENGTH_SHORT).show();
            }*/
            currentPosition++;
            if (isAlternative){
                skipQuestionAlternative();
            } else{
                skipQuestion();
            }

        });

        return root;
    }


    private void setupAlertDialog(int i) {
        if (alertDialog != null && !alertDialogClicked) {
            alertDialogClicked = true;
            alertDialog.setTitle("ARE YOU SURE ?");
            alertDialog.setMessage("You want to submit ?");

            boolean tempReturn[] = new boolean[1];
            alertDialog.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            addQuestionAnswerToNetwork(i);
                            dialog.dismiss();
                        }
                    });

            alertDialog.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            progressDialog.dismiss();
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
    }

    private void addQuestionAnswerToNetwork(int i) {

        ViewQuestionAddAnswerRequestModel model = new ViewQuestionAddAnswerRequestModel();
        model.setQuestion_id(currentQuestionId);
        model.setSelected_answer(i);

        GsonRequest<UniversalResponseModel> request = new GsonRequest<UniversalResponseModel>
                (1, Constants.API_ADD_ANSWER, new Gson().toJson(model), UniversalResponseModel.class, SplashActivity.headerData, new Response.Listener<UniversalResponseModel>() {
                    @Override
                    public void onResponse(UniversalResponseModel response) {
                        progressDialog.dismiss();
                        if (response.getCode() == 200) {
                            Toast.makeText(getContext(), "Answer added successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getContext(), QuestionAnalysisNoneActivity.class);
                            if (result.length != 0) {
                                intent.putExtra("option1Text", getModelFromArrayOfArrayList().getOption1());
                                intent.putExtra("option2Text", getModelFromArrayOfArrayList().getOption2());
                                intent.putExtra("option1Img", getModelFromArrayOfArrayList().getOption1_original_image());
                                intent.putExtra("option2Img", getModelFromArrayOfArrayList().getOption2_original_image());
                            } else {
                                intent.putExtra("option1Text", resultAlternative.get(currentPosition).getOption1());
                                intent.putExtra("option2Text", resultAlternative.get(currentPosition).getOption2());
                                intent.putExtra("option1Img", resultAlternative.get(currentPosition).getOption1_original_image());
                                intent.putExtra("option2Img", resultAlternative.get(currentPosition).getOption2_original_image());
                            }
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
        customVolleyQueue.addToRequestQueue(request);
    }

    private void reportQuestionToNetwork() {
        JSONObject paramBody = new JSONObject();
        try {
            paramBody.put("question_id", currentQuestionId);
        } catch (JSONException e) {

        }

        GsonRequest<UniversalResponseModel> request = new GsonRequest<UniversalResponseModel>
                (1, Constants.API_REPORT_QUESTION, paramBody.toString(), UniversalResponseModel.class, SplashActivity.headerData, new Response.Listener<UniversalResponseModel>() {
                    @Override
                    public void onResponse(UniversalResponseModel response) {
                        progressDialog.dismiss();
                        if (response.getCode() == 200) {
                            Toast.makeText(getContext(), "Question reported successfully", Toast.LENGTH_SHORT).show();
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
        customVolleyQueue.addToRequestQueue(request);
    }

    private void skipQuestionAlternative() {
        if ((currentPosition + 1) == totalCount) {
            currentPosition = 0;
        }
        LoginResponseModelAlternativeDataResult model = null;
        Log.e(TAG, "skipQuestion: current position at function start " + currentPosition);
        try {
            int tempCount = 0;
            //case of arraylist
            if (resultAlternative.size() != 0) {
                for (int loop = 0; loop < resultAlternative.size(); loop++) {
                    if (currentPosition == tempCount) {
                        model = resultAlternative.get(tempCount);
                        currentQuestionId = model.getQuestion_id();
                        Log.e(TAG, "skipQuestion: current position found" + currentPosition);
                        break;
                    }
                    tempCount++;
                    if (loop == 0)
                        tempCount++;
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        if (model != null) {
            Log.e(TAG, "skipQuestion: model current position " + currentPosition);
            setupQuestion(model.getDescription(), questionImage, model.getQuestion_compress(), model.getQuestion_original());
            setupOptions(model.getOption1(), model.getOption2(), model.getOption1_compress_image(), model.getOption1_original_image(), model.getOption2_compress_image(), model.getOption2_original_image());
        }

        if ((currentPosition + 2) == totalCount) {
            Toast.makeText(getContext(), "You are on second last position", Toast.LENGTH_SHORT).show();
        }

    }

    private void skipQuestion() {

        if ((currentPosition + 1) == totalCount) {
            currentPosition = 0;
        }
        ViewQuestionResponseModelResult model = null;
        Log.e(TAG, "skipQuestion: current position at function start " + currentPosition);

        try {
            int tempCount = 0;
            //case of array of arraylist
            if (result.length != 0) {
                for (int loop = 0; loop < result.length; loop++) {
                    if (!result[loop].isEmpty()) {

                        for (int inLoop = 0; inLoop < result[loop].size(); inLoop++) {
                            if (currentPosition == tempCount) {
                                model = result[loop].get(inLoop);
                                currentQuestionId = model.getQuestion_id();
                                Log.e(TAG, "skipQuestion: current position found" + currentPosition);
                                break;
                            }
                            tempCount++;
                        }
                        if (loop == 0) {
                            tempCount++;
                        }
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        if (model != null) {
            Log.e(TAG, "skipQuestion: model current position " + currentPosition);
            setupQuestion(model.getDescription(), questionImage, model.getQuestion_compress(), model.getQuestion_original());
            setupOptions(model.getOption1(), model.getOption2(), model.getOption1_compress_image(), model.getOption1_original_image(), model.getOption2_compress_image(), model.getOption2_original_image());
        }

        if ((currentPosition + 2) == totalCount) {
            Toast.makeText(getContext(), "You are on second last position", Toast.LENGTH_SHORT).show();
        }


    }

    private ViewQuestionResponseModelResult getModelFromArrayOfArrayList() {
        ViewQuestionResponseModelResult modelInFunction = null;
        try {
            int tempCount = 0;
            //case of array of arraylist
            if (result.length != 0) {
                for (int loop = 0; loop < result.length; loop++) {
                    if (!result[loop].isEmpty()) {

                        for (int inLoop = 0; inLoop < result[loop].size(); inLoop++) {
                            if (currentPosition == tempCount) {
                                modelInFunction = result[loop].get(inLoop);
                                currentQuestionId = modelInFunction.getQuestion_id();
                                Log.e(TAG, "skipQuestion: current position found" + currentPosition);
                                break;
                            }
                            tempCount++;
                        }
                        if (loop == 0) {
                            tempCount++;
                        }
                    }
                }
            }
        } catch (IndexOutOfBoundsException exception) {
            exception.printStackTrace();
        }
        return modelInFunction;
    }

    private void setupQuestion(String description, ImageView questionImage, String
            question_compress, String question_original) {
        if (!description.isEmpty()) {
            questionDescription.setText(description);
            questionDescription.setVisibility(View.VISIBLE);
        } else {
            questionDescription.setVisibility(View.GONE);
        }

        if (!question_compress.isEmpty() || !question_original.isEmpty()) {
            Log.e(TAG, "setupQuestion: image setup original " + question_original);
            setupImage(questionImage, question_compress, question_original);
            questionImage.setVisibility(View.VISIBLE);
        } else {
            questionImage.setVisibility(View.GONE);
        }

    }


    private void setupOptions(String option1, String option2, String
            option1_compress_image, String option1_original_image, String option2_compress_image, String
                                      option2_original_image) {
        if ((!option1.trim().isEmpty()) || (!option2.trim().isEmpty())) {
            Log.e(TAG, "setupOptions: op1 " + option1 + " op2 " + option2);
            imageLayout.setVisibility(View.GONE);
            option1Text.setText(option1);
            option2Text.setText(option2);
            textLayout.setVisibility(View.VISIBLE);
        }
        if (!(option1_compress_image.isEmpty() || option1_original_image.isEmpty()) && !(option2_compress_image.isEmpty() || option2_original_image.isEmpty())) {
            Log.e(TAG, "setupOptions: img op1 " + option1 + " op2 " + option2);
            textLayout.setVisibility(View.GONE);
            setupImage(option1Img, option1_compress_image, option1_original_image);
            setupImage(option2Img, option2_compress_image, option2_original_image);
            imageLayout.setVisibility(View.VISIBLE);
        }

    }


    private void setupImage(ImageView imageView, String compress, String original) {
        try {
            Glide.with(root).load(Uri.parse(compress)).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    imageView.setImageResource(R.drawable.app_load_image);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            }).into(imageView);
        } catch (Exception e) {
            Glide.with(root).load(Uri.parse(original)).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    imageView.setImageResource(R.drawable.app_load_image);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            }).into(imageView);
            Log.e(TAG, "setupImage: " + e.getCause());
        }

    }

    private void getQuestionsFromNetwork() {    //medical question 1
        GsonRequest<ViewQuestionResponseModel> request = new GsonRequest<ViewQuestionResponseModel>
                (1, Constants.API_VIEW_QUESTION, null, ViewQuestionResponseModel.class, SplashActivity.headerData, new Response.Listener<ViewQuestionResponseModel>() {
                    @Override
                    public void onResponse(ViewQuestionResponseModel response) {
                        progressDialog.dismiss();
                        if (response.getCode() == 200) {
                            questionLayout.setVisibility(View.VISIBLE);
                            skip.setVisibility(View.VISIBLE);
                            noData.setVisibility(View.GONE);
                            result = response.getData().getResult();
                            setTotalCount();
                            if (currentPosition == -1)
                                currentPosition = 0;

                            skipQuestion();
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
                                getQuestionsFromNetworkAlternative();   //case of arraylist
                                Snackbar.make(parent, "We are trying different path for you. please wait", BaseTransientBottomBar.LENGTH_LONG).show();
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
        customVolleyQueue.addToRequestQueue(request);
    }

    private void getQuestionsFromNetworkAlternative() {
        GsonRequest<ViewQuestionResponseModelAlternative> request = new GsonRequest<ViewQuestionResponseModelAlternative>
                (1, Constants.API_VIEW_QUESTION, null, ViewQuestionResponseModelAlternative.class, SplashActivity.headerData, new Response.Listener<ViewQuestionResponseModelAlternative>() {
                    @Override
                    public void onResponse(ViewQuestionResponseModelAlternative response) {
                        progressDialog.dismiss();
                        if (response.getCode() == 200) {
                            questionLayout.setVisibility(View.VISIBLE);
                            skip.setVisibility(View.VISIBLE);
                            noData.setVisibility(View.GONE);
                            isAlternative = true;
                            resultAlternative = response.getData().getResult();
                            setTotalCount();
                            if (currentPosition == -1)
                                currentPosition = 0;

                            skipQuestionAlternative();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        skip.setVisibility(View.GONE);
                        noData.setVisibility(View.VISIBLE);
                        questionLayout.setVisibility(View.GONE);
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
        customVolleyQueue.addToRequestQueue(request);
    }

    private void setTotalCount() {
        totalCount = 0;
        if (result != null) {
            for (int loop = 0; loop < result.length; loop++) {

                totalCount += result[loop].size();
            }
            Log.e(TAG, "onResponse: total count " + totalCount);
        } else {
            totalCount = resultAlternative.size();
        }
    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        try {
            getQuestionsFromNetwork();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            getQuestionsFromNetworkAlternative();
        }
    }

}