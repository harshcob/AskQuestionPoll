package com.example.askQuestionPoll.ui.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.example.askQuestionPoll.R;
import com.example.askQuestionPoll.core.constants.Constants;
import com.example.askQuestionPoll.core.constants.MySingleTon;
import com.example.askQuestionPoll.core.interfaces.AccountQuestionSelectionInterface;
import com.example.askQuestionPoll.core.pojo.AnalysisMyQuestionsNoneResponseModel;
import com.example.askQuestionPoll.core.pojo.AnalysisMyQuestionsNoneResponseModelDataResult;
import com.example.askQuestionPoll.core.pojo.LoginResponseModelAlternativeDataResult;
import com.example.askQuestionPoll.core.sharedprefs.CustomSharedPreference;
import com.example.askQuestionPoll.core.utils.SystemUtils;
import com.example.askQuestionPoll.ui.activity.AnalysisMyQuestionActivity;
import com.example.askQuestionPoll.ui.activity.SplashActivity;
import com.example.askQuestionPoll.ui.activity.ViewMyQuestionActivity;
import com.example.askQuestionPoll.ui.adapters.AccountRecyclerViewAdapter;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonSyntaxException;
import com.optimumbrew.library.core.network.ConnectivityUtils;
import com.optimumbrew.library.core.volley.GsonRequest;

import java.net.ConnectException;
import java.util.ArrayList;

public class AccountFragment extends Fragment implements AccountQuestionSelectionInterface {

    private static final String TAG = "AccountFragment";

    private RecyclerView recyclerView;
    private LinearLayout parent;
    private ProgressDialog progressDialog;

    public static int selectedPosition = -1, selectedQuestionId = -1;
    public static ArrayList<AnalysisMyQuestionsNoneResponseModelDataResult> myResults;
    private ArrayList<Integer> myQuestionId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_account, container, false);
        recyclerView = root.findViewById(R.id.recycler_view_setting_fragment);
        parent = root.findViewById(R.id.parent_view_setting_fragment);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Fetching your questions for you. Please wait");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        getMyQuestionsFromNetwork();

        if (!ViewQuestionFragment.isAlternative)
            getMyQuestionIdsAlternative();
        else
            getMyQuestionIds();

        return root;
    }

    private void getMyQuestionIdsAlternative() {
        LoginResponseModelAlternativeDataResult model = null;

        try {
            int tempCount = 0;
            //case of arraylist
            if (ViewQuestionFragment.resultAlternative!=null && ViewQuestionFragment.resultAlternative.size() != 0) {
                for (int loop = 0; loop < ViewQuestionFragment.resultAlternative.size(); loop++) {
                    if (CustomSharedPreference.getInstance(requireContext()).getUserId() == ViewQuestionFragment.resultAlternative.get(loop).getUser_id()) {
                        myQuestionId.add(ViewQuestionFragment.resultAlternative.get(loop).getQuestion_id());
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
    }

    private void getMyQuestionIds() {
        try {
            int tempCount = 0;
            //case of array of arraylist
            if (ViewQuestionFragment.result!=null && ViewQuestionFragment.result.length != 0) {
                for (int loop = 0; loop < ViewQuestionFragment.result.length; loop++) {
                    if (!ViewQuestionFragment.result[loop].isEmpty()) {

                        for (int inLoop = 0; inLoop < ViewQuestionFragment.result[loop].size(); inLoop++) {
                            if (CustomSharedPreference.getInstance(requireContext()).getUserId() == ViewQuestionFragment.result[loop].get(inLoop).getQuestion_id()) {
                                myQuestionId.add(ViewQuestionFragment.result[loop].get(inLoop).getQuestion_id());
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
    }

    private void getMyQuestionsFromNetwork() {
        GsonRequest<AnalysisMyQuestionsNoneResponseModel> request = new GsonRequest<AnalysisMyQuestionsNoneResponseModel>
                (1, Constants.API_MY_QUESTIONS, null, AnalysisMyQuestionsNoneResponseModel.class, SplashActivity.headerData, new Response.Listener<AnalysisMyQuestionsNoneResponseModel>() {
                    @Override
                    public void onResponse(AnalysisMyQuestionsNoneResponseModel response) {
                        progressDialog.dismiss();
                        if (response.getCode() == 200) {
                            myResults = response.getData().getResult();
                            setupRecyclerView(myResults);
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
        MySingleTon.getInstance(getContext()).addGsonRequest(request);
    }

    private void setupRecyclerView(ArrayList<AnalysisMyQuestionsNoneResponseModelDataResult> myResults) {
        AccountRecyclerViewAdapter adapter = new AccountRecyclerViewAdapter(myResults, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void startActivityCustomized(boolean sourceType, int position, int questionId) {
        selectedPosition = position;
        selectedQuestionId = questionId;

        if (sourceType) {
            startActivity(new Intent(getContext(), ViewMyQuestionActivity.class));
        } else {
            startActivity(new Intent(getContext(), AnalysisMyQuestionActivity.class));
        }
    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        try {
            getMyQuestionsFromNetwork();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}