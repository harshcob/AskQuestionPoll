package com.example.askQuestionPoll.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.example.askQuestionPoll.R;
import com.example.askQuestionPoll.core.constants.Constants;
import com.example.askQuestionPoll.core.pojo.ViewCategoriesAddQuestionResponseModel;
import com.example.askQuestionPoll.core.sharedprefs.CustomSharedPreference;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonSyntaxException;
import com.optimumbrew.library.core.network.ConnectivityUtils;
import com.optimumbrew.library.core.volley.GsonRequest;
import com.optimumbrew.library.core.volley.MyVolley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";

    private MyVolley customVolleyQueue;
    private RelativeLayout parent;

    public static String[] categoriesQuestions, countryJson, genderJson;
    public static HashMap<String, String> headerData = new HashMap<>();

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        parent = findViewById(R.id.parent_splash_activity);

        try {
            customVolleyQueue = MyVolley.getInstance(SplashActivity.this);
            CustomSharedPreference.getInstance(this).setUdid(Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID));
        } catch (Exception e) {
            Log.e(TAG, "onCreate: ");
        } finally {
            parseJsonFromAssets();
            noNeedOfAuthenticationAPICallFromNetwork();
        }

        new Handler().postDelayed(() -> {
            if (CustomSharedPreference.getInstance(SplashActivity.this).getLogin()) {
                headerData.put("Authorization", "Bearer " + CustomSharedPreference.getInstance(SplashActivity.this).getTokenApplication());
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            } else {
                clearApplicationData();
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
            finish();

        }, 1000);
    }

    private void parseJsonFromAssets() {

        //for country.json
        try {
            JSONArray arrayCountry = new JSONArray(LoadJsonFromAsset("country.json"));
            countryJson = new String[arrayCountry.length()];
            for (int i = 0; i < arrayCountry.length(); i++) {
                JSONObject singleItemObject = (JSONObject) arrayCountry.get(i);
                countryJson[i] = singleItemObject.getString("country");
            }
        } catch (Exception e) {
            Log.e("Exception", "displaySimpleJsonData: " + e.getLocalizedMessage());
        }

        //for gender.json
        try {
            JSONArray arrayCountry = new JSONArray(LoadJsonFromAsset("gender.json"));
            genderJson = new String[arrayCountry.length()];
            for (int i = 0; i < arrayCountry.length(); i++) {
                JSONObject singleItemObject = (JSONObject) arrayCountry.get(i);
                genderJson[i] = singleItemObject.getString("gender");
            }
        } catch (Exception e) {
            Log.e("Exception", "displaySimpleJsonData: " + e.getLocalizedMessage());
        }

    }


    public void clearApplicationData() {
        File cache = getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                    Log.i("EEEEEERRRRRRROOOOOOORRRR", "**************** File /data/data/APP_PACKAGE/" + s + " DELETED *******************");
                }
            }
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }

    private void noNeedOfAuthenticationAPICallFromNetwork() {

        //view categories setup at add question
        viewCategoriesForAddQuestionFromNetwork();
    }

    private void viewCategoriesForAddQuestionFromNetwork() {

        GsonRequest<ViewCategoriesAddQuestionResponseModel> viewQuestionResponseModelGsonRequest = new GsonRequest<ViewCategoriesAddQuestionResponseModel>
                (1, Constants.API_VIEW_CATEGORIES, null, ViewCategoriesAddQuestionResponseModel.class, null, new Response.Listener<ViewCategoriesAddQuestionResponseModel>() {
                    @Override
                    public void onResponse(ViewCategoriesAddQuestionResponseModel response) {
                        if (response.getCode() == 200) {
                            categoriesQuestions = new String[response.getData().getResult().size()];
                            for (int loop = 0; loop < response.getData().getResult().size(); loop++) {
                                categoriesQuestions[loop] = response.getData().getResult().get(loop).getCategory_name();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


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

        if (!viewQuestionResponseModelGsonRequest.shouldCache())
            viewQuestionResponseModelGsonRequest.setShouldCache(true);
        customVolleyQueue.addToRequestQueue(viewQuestionResponseModelGsonRequest);
    }

    public String LoadJsonFromAsset(String fileName) {
        byte[] formArray = null;
        try {
            AssetManager manager = getAssets();
            InputStream file = manager.open(fileName);
            formArray = new byte[file.available()];
            file.read(formArray);
            file.close();
        } catch (Exception e) {
            Snackbar.make(getCurrentFocus(), "Something wrong happens in json parsing. Try later", BaseTransientBottomBar.LENGTH_SHORT).show();
            Log.e(TAG, "LoadJsonFromAsset: " + e.getLocalizedMessage());
        }
        return new String(formArray);
    }

}