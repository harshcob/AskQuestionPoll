package com.example.askQuestionPoll.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.askQuestionPoll.R;
import com.example.askQuestionPoll.core.interfaces.CountrySelectionInterface;
import com.example.askQuestionPoll.ui.activity.SplashActivity;
import com.example.askQuestionPoll.ui.adapters.CountryRecyclerViewAdapter;

public class CountryPickupDialog extends Dialog {

    private Context context;
    private RecyclerView recyclerview;
    private EditText search;
    private TextView cancel, submit;
    private CheckBox all;
    private CountryRecyclerViewAdapter adapter;
    private CountrySelectionInterface selectionInterface;

    public static boolean[] selectedCountryList;


    public CountryPickupDialog(@NonNull Context context, CountrySelectionInterface selectionInterface) {
        super(context);
        this.context = context;
        this.selectionInterface = selectionInterface;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_country_pickup);

        recyclerview = findViewById(R.id.recycler_view_country_dialog);
        search = findViewById(R.id.search_country_dialog);
        all = findViewById(R.id.all_selection_country_dialog);
        cancel = findViewById(R.id.cancel_country_dialog);
        submit = findViewById(R.id.select_country_dialog);

        cancel.setOnClickListener(view -> dismiss());
        submit.setOnClickListener(view -> submitCountry());
        setupRecyclerView(SplashActivity.countryJson);

        all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                for (int loop = 0; loop < selectedCountryList.length; loop++)
                    selectedCountryList[loop] = b;
                adapter.notifyDataSetChanged();
            }
        });
    }


    private void submitCountry() {
        String temp = "";
        for (int loop = 0; loop < SplashActivity.countryJson.length; loop++) {
            if (selectedCountryList[loop]) {
                if (temp.isEmpty())
                    temp += SplashActivity.countryJson[loop];
                else
                    temp += ", "+SplashActivity.countryJson[loop];
            }
        }

        selectionInterface.getCountryData(temp);
        dismiss();
    }

    private void setupRecyclerView(String[] myResults) {
        adapter = new CountryRecyclerViewAdapter(myResults);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setAdapter(adapter);
    }

}
