package com.example.janari.SimpleDailyBudgetApp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.distribute.Distribute;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
import java.util.Locale;


// Activity that checks if user is logged in or not and starts right activity
public class StartActivity extends AppCompatActivity {


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Locale locale = Locale.US;
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
            this.setContentView(R.layout.activity_start);

            AppCenter.start(getApplication(), "{370f7910-3208-45fd-b03e-918e898b1c6e}", Analytics.class, Crashes.class);
            AppCenter.start(getApplication(), "{370f7910-3208-45fd-b03e-918e898b1c6e}", Distribute.class);

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


