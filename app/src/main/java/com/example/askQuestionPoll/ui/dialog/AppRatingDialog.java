package com.example.askQuestionPoll.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.askQuestionPoll.R;
import com.example.askQuestionPoll.core.sharedprefs.CustomSharedPreference;

public class AppRatingDialog extends Dialog {

    private Context context;
    private TextView notNow;
    private RatingBar ratingBar;

    public AppRatingDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_app_rating);

        notNow = findViewById(R.id.not_now_rating_dialog);
        ratingBar = findViewById(R.id.rating_bar_rating_dialog);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                CustomSharedPreference.getInstance(context).setRating(v);
                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp"));
                context.startActivity(i);
                dismiss();
            }
        });
        notNow.setOnClickListener(view -> dismissDialog());

    }

    private void dismissDialog() {
        CustomSharedPreference.getInstance(context).setRating(0);
        dismiss();
    }
}
