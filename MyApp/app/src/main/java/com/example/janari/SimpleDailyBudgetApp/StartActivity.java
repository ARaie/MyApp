package com.example.janari.SimpleDailyBudgetApp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.distribute.Distribute;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;


// Activity that checks if user is logged in or not and starts right activity
public class StartActivity extends AppCompatActivity {


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_start);

            AppCenter.start(getApplication(), "{26b6dcaf-b14d-43a5-866e-ae2a994c4d8f}", Analytics.class, Crashes.class);
            AppCenter.start(getApplication(), "{26b6dcaf-b14d-43a5-866e-ae2a994c4d8f}", Distribute.class);

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


