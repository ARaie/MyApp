package com.example.janari.SimpleDailyBudgetApp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class StartActivity extends AppCompatActivity {


//TODO võiks ju olla aga miks see i tööta emulaatoril juba nii nagu andmebaasid. kui on mälus siis ei näita mulle enne
    //välja login'i kui ma pole teinud logout'i
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_start);
            SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
//Get "hasLoggedIn" value. If the value doesn't exist yet false is returned
            boolean hasLoggedIn = settings.getBoolean("hasLoggedIn", false);

            if(hasLoggedIn)
            {
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


