package com.example.askQuestionPoll.ui.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.askQuestionPoll.core.sharedprefs.CustomSharedPreference;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.askQuestionPoll.R;

import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {
    private long timeBack=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        BottomNavigationView navigationView = findViewById(R.id.bottom_nav_view_home_activity);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_home_activity);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - timeBack > 1000) {
            timeBack = System.currentTimeMillis();
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}