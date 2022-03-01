package com.example.askQuestionPoll.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.example.askQuestionPoll.R;
import com.example.askQuestionPoll.core.sharedprefs.CustomSharedPreference;
import com.example.askQuestionPoll.ui.activity.LoginActivity;

public class TokenExpireDialog extends Dialog {
    Context context;
    Button loginLink;

    public TokenExpireDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_token_expire);

        CustomSharedPreference.getInstance(context).setTokenExpire(true);
        loginLink = findViewById(R.id.submit_token_expire_dialog);
        loginLink.setOnClickListener(view -> context.startActivity(new Intent(getContext(), LoginActivity.class)));
    }

    @Override
    public void onBackPressed() {

    }
}
