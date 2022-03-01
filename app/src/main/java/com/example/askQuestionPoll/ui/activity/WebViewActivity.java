package com.example.askQuestionPoll.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.askQuestionPoll.R;

public class WebViewActivity extends AppCompatActivity {

    WebView webView;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        webView = findViewById(R.id.sliding_web_view_activity);

        progressDialog = new ProgressDialog(this,R.style.ProgressDialog);
        progressDialog.setMessage("Loading. Please wait");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        if (webView.getVisibility() == View.GONE) {
            setupWebView();
        }
    }

    private void setupWebView() {
        webView.loadUrl("https://stackoverflow.com");
        webView.requestFocus();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setVisibility(View.VISIBLE);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressDialog.dismiss();
            }
        });
    }
}