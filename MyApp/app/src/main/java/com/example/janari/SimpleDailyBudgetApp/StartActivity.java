package com.example.janari.SimpleDailyBudgetApp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


// Activity that checks if user is logged in or not and starts right activity
public class StartActivity extends AppCompatActivity {


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_start);
            SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);

            String string = settings.getString("key", null);

            if(string == null) {
                Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(intent);
                this.finish();
            }else{
                Intent intent = new Intent(StartActivity.this, NavigationDrawerActivity.class);
                startActivity(intent);
                this.finish();
            }

        }

    }


