package com.example.askQuestionPoll.ui.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.askQuestionPoll.R;
import com.example.askQuestionPoll.ui.dialog.CountryPickupDialog;

public class CountryRecyclerViewAdapter extends RecyclerView.Adapter<CountryRecyclerViewHolder> {

    private static final String TAG = "CountryRecyclerViewAdapter";

    String[] countryList;

    private int selectedPosition = -1;  //to hold position

    public CountryRecyclerViewAdapter(String[] countryList) {
        this.countryList = countryList;
        CountryPickupDialog.selectedCountryList = new boolean[countryList.length];
    }

    @NonNull
    @Override
    public CountryRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_country_layout, parent, false);
        return new CountryRecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CountryRecyclerViewHolder holder, int position) {

        String text = countryList[position];
        holder.checkBox.setText(text);
        holder.checkBox.setOnCheckedChangeListener(null);


        if (CountryPickupDialog.selectedCountryList[position]) {
            Log.e(TAG, "onBindViewHolder: true positions " + position);
            holder.checkBox.setChecked(true);
        } else
            holder.checkBox.setChecked(false);


        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                selectedPosition = holder.getAdapterPosition();
                CountryPickupDialog.selectedCountryList[selectedPosition] = true;

                if (selectedPosition == position) {
                    holder.checkBox.setChecked(true);
                } else {
                    holder.checkBox.setChecked(false);
                }
                Log.e(TAG, "onBindViewHolder: position " + position);
                Log.e(TAG, "onBindViewHolder: selectedposition " + selectedPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return countryList.length;
    }
}

class CountryRecyclerViewHolder extends RecyclerView.ViewHolder {
    CheckBox checkBox;

    public CountryRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        checkBox = itemView.findViewById(R.id.check_box_recycler_item_layout);
    }
}
