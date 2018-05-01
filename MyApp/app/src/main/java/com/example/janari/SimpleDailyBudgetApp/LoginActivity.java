package com.example.janari.SimpleDailyBudgetApp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.Locale;


public class LoginActivity extends AppCompatActivity {

    DatabaseHelper myDb;
    EmailHelper emailDB;
    Button LogInButton, RegisterButton;
    EditText Email, Password;
    String EmailHolder, PasswordHolder, id;
    Boolean EditTextEmptyHolder;
    public static final String PREFS_NAME = "MyPrefsFile";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Locale locale = Locale.US;
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        this.setContentView(R.layout.activity_login);

        LogInButton = (Button) findViewById(R.id.buttonLogin);
        RegisterButton = (Button) findViewById(R.id.buttonRegister);
        Email = (EditText) findViewById(R.id.editEmail);
        Password = (EditText) findViewById(R.id.editPassword);

        myDb = new DatabaseHelper(this);
        emailDB = new EmailHelper(this);


        //Log in button.
        LogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Calling EditText is empty or no method.
                CheckEditTextStatus();
                // Calling login method.
                LoginFunction();
                // Adds logged in user email to email database to identify user with ID
                AddEmail();
                // Removes data from EditText fields
                Email.setText(null);
                Password.setText(null);
                // Saving data for logged in session
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0); // 0 - for private mode
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("key", "x");
                editor.commit();

            }
        });

        // Register button.
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Opening new user registration activity using intent on button click.
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

   // Login function starts from here.
    public void LoginFunction(){

        if(EditTextEmptyHolder) {

            boolean recordExists = myDb.hasObject(Email.getText().toString(), Password.getText().toString());

            if(recordExists == true){
                Intent intentSignIn = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
                startActivity(intentSignIn);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "UserName or Password is Wrong, Please Try Again.", Toast.LENGTH_LONG).show();
            }

        }else{
            Toast.makeText(getApplicationContext(), "Enter username and password", Toast.LENGTH_LONG).show();
        }
    }

    // Checking EditText is empty or not.
    public void CheckEditTextStatus() {

        // Getting value from All EditText and storing into String Variables.
        EmailHolder = Email.getText().toString();
        PasswordHolder = Password.getText().toString();

        // Checking EditText is empty or no using TextUtils.
        if (TextUtils.isEmpty(EmailHolder) || TextUtils.isEmpty(PasswordHolder)) {

            EditTextEmptyHolder = false;

        } else {

            EditTextEmptyHolder = true;
        }
    }

    // Refreshes and saves over the user email
    public void RefreshEmail() {

        emailDB.updateEmail("1",
                Email.getText().toString());

    }

    // Saves logged in user email
    public void AddEmail() {

        emailDB.insertEmail("1", Email.getText().toString());
        RefreshEmail();
    }

}