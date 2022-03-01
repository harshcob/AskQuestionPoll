package com.example.askQuestionPoll.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.askQuestionPoll.R;
import com.example.askQuestionPoll.core.pojo.AnalysisMyQuestionsNoneResponseModelDataResult;
import com.example.askQuestionPoll.ui.fragments.AccountFragment;

public class ViewMyQuestionActivity extends AppCompatActivity {

    private ImageView questionImage, option1Image, option2Image;
    private TextView questionDescription, option1Text, option2Text;
    private TextView gender, country, category;
    private Button cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_question);
        questionImage = findViewById(R.id.question_image_view_my_question_activity);
        option1Image = findViewById(R.id.image_option1_view_my_question_activity);
        option2Image = findViewById(R.id.image_option2_view_my_question_activity);
        questionDescription = findViewById(R.id.question_description_view_my_question_activity);
        option1Text = findViewById(R.id.text_option1_view_my_question_activity);
        option2Text = findViewById(R.id.text_option2_view_my_question_activity);
        gender = findViewById(R.id.gender_view_my_question_activity);
        country = findViewById(R.id.country_view_my_question_activity);
        category = findViewById(R.id.category_view_my_question_activity);
        cancel = findViewById(R.id.cancel_view_my_question_activity);

        cancel.setOnClickListener(view -> finish());

        setupDataToUI();
    }

    private void setupDataToUI() {
        AnalysisMyQuestionsNoneResponseModelDataResult singleObject = AccountFragment.myResults.get(AccountFragment.selectedPosition);

        if (!singleObject.getDescription().isEmpty()){
            questionDescription.setVisibility(View.VISIBLE);
            questionDescription.setText(singleObject.getDescription());
        }

        if (!singleObject.getOption1().isEmpty()){
            option1Text.setVisibility(View.VISIBLE);
            option1Text.setText(singleObject.getOption1());
        }

        if (!singleObject.getOption2().isEmpty()){
            option2Text.setVisibility(View.VISIBLE);
            option2Text.setText(singleObject.getOption2());
        }

        if (!singleObject.getOption1_original_image().isEmpty() || !singleObject.getOption1_compress_image().isEmpty()){
            option1Image.setVisibility(View.VISIBLE);
            try {
                Glide.with(this).load(singleObject.getOption1_compress_image()).into(option1Image);
            } catch (Exception e){
                Glide.with(this).load(singleObject.getOption1_original_image()).into(option1Image);
            }
        }

        if (!singleObject.getOption2_original_image().isEmpty() || !singleObject.getOption2_compress_image().isEmpty()){
            option2Image.setVisibility(View.VISIBLE);
            try {
                Glide.with(this).load(singleObject.getOption2_compress_image()).into(option2Image);
            } catch (Exception e){
                Glide.with(this).load(singleObject.getOption2_original_image()).into(option2Image);
            }
        }

        if (!singleObject.getQuestion_original().isEmpty() || !singleObject.getQuestion_compress().isEmpty()){
            questionImage.setVisibility(View.VISIBLE);
            try {
                Glide.with(this).load(singleObject.getQuestion_compress()).into(questionImage);
            } catch (Exception e){
                Glide.with(this).load(singleObject.getQuestion_original()).into(questionImage);
            }
        }

        if (!singleObject.getGender().isEmpty()){
            switch (Integer.parseInt(singleObject.getGender().trim())){
                case 1:
                    gender.setText("male");
                    break;
                case 2:
                    gender.setText("female");
                    break;
                default:
                    gender.setText("all");
                    break;
            }
        }

        if (!singleObject.getCategory_id().isEmpty()){
            category.setText(SplashActivity.categoriesQuestions[Integer.parseInt(singleObject.getCategory_id())]);
        }

        if (!singleObject.getCountry().isEmpty()){
            country.setText(singleObject.getCountry());
        }

    }
}