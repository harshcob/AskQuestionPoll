package com.example.askQuestionPoll.core.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.example.askQuestionPoll.core.sharedprefs.CustomSharedPreference;
import com.example.askQuestionPoll.ui.dialog.TokenExpireDialog;

public class SystemUtils {

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);

    }


    public static void displayTokenExpireDialog(Activity activity) {
        TokenExpireDialog tokenExpireDialog = new TokenExpireDialog(activity);
        CustomSharedPreference.getInstance(activity).setTokenApplication("");
        CustomSharedPreference.getInstance(activity).setLogin(false);
        tokenExpireDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        tokenExpireDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
        tokenExpireDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tokenExpireDialog.setCanceledOnTouchOutside(false);
        tokenExpireDialog.show();
    }
}
