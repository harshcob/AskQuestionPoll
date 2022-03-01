package com.example.askQuestionPoll.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.askQuestionPoll.R;
import com.example.askQuestionPoll.core.interfaces.AccountQuestionSelectionInterface;
import com.example.askQuestionPoll.core.pojo.AnalysisMyQuestionsNoneResponseModelDataResult;
import com.example.askQuestionPoll.ui.activity.ViewMyQuestionActivity;

import java.util.ArrayList;

public class AccountRecyclerViewAdapter extends RecyclerView.Adapter<AccountRecyclerViewHolder> {
    private ArrayList<AnalysisMyQuestionsNoneResponseModelDataResult> myQuestionsList;
    AccountQuestionSelectionInterface selectionInterface;

    public AccountRecyclerViewAdapter(ArrayList<AnalysisMyQuestionsNoneResponseModelDataResult> myQuestionsList, AccountQuestionSelectionInterface selectionInterface) {
        this.myQuestionsList = myQuestionsList;
        this.selectionInterface = selectionInterface;
    }

    @NonNull
    @Override
    public AccountRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_account_layout, parent, false);
        return new AccountRecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountRecyclerViewHolder holder, int position) {
        AnalysisMyQuestionsNoneResponseModelDataResult singleObject = myQuestionsList.get(position);
        holder.questionDescription.setText(singleObject.getDescription());
        holder.viewQuestion.setOnClickListener(view -> {
            holder.context.startActivity(new Intent(holder.context, ViewMyQuestionActivity.class));
        });

        holder.viewAnalysis.setOnClickListener(view -> {
            holder.context.startActivity(new Intent(holder.context, ViewMyQuestionActivity.class));
        });
    }

    @Override
    public int getItemCount() {
        return myQuestionsList.size();
    }
}

class AccountRecyclerViewHolder extends RecyclerView.ViewHolder {

    TextView questionDescription;
    Button viewQuestion, viewAnalysis;
    Context context;

    public AccountRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        context = itemView.getContext();
        questionDescription = itemView.findViewById(R.id.question_description_recycler_item_layout);
        viewQuestion = itemView.findViewById(R.id.view_questions_recycler_item_layout);
        viewAnalysis = itemView.findViewById(R.id.view_analysis_recycler_item_layout);
    }
}
