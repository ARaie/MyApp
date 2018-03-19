package com.example.janari.SimpleDailyBudgetApp;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    DatabaseHelper myDb;
    EmailHelper emailDB;
    EditText Name, Email, Password;
    Button btnAddData;
    String EmailHolder, PasswordHolder, NameHolder, id;
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
        this.setContentView(R.layout.activity_register);


        // Local database
        myDb = new DatabaseHelper(this);
        emailDB = new EmailHelper(this);

        // Fields
        Name = (EditText) findViewById(R.id.enter_name);
        Email = (EditText) findViewById(R.id.enter_email);
        Password = (EditText) findViewById(R.id.enter_password);

        // Button
        btnAddData = (Button) findViewById(R.id.button_add);
        btnAddData.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                // Check that EditText fields are not empty
                CheckEditTextStatus();
                // If fields are not empty
                if(EditTextEmptyHolder) {

                    // Add new user to database
                    UpdateData(Email.getText().toString());


                }else{
                    // When fields are not filled
                    Toast.makeText(getApplicationContext(), "Enter username and password", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
  // Checks that fields are not empty
    public void CheckEditTextStatus() {

        // Getting value from All EditText and storing into String Variables.
        NameHolder = Name.getText().toString();
        EmailHolder = Email.getText().toString();
        PasswordHolder = Password.getText().toString();

        // Checking EditText is empty or no using TextUtils.
        if (TextUtils.isEmpty(EmailHolder) || TextUtils.isEmpty(EmailHolder) || TextUtils.isEmpty(PasswordHolder)) {

            EditTextEmptyHolder = false;

        } else {

            EditTextEmptyHolder = true;
        }
    }
    // Login function starts from here.
    public void LoginFunction(){

        if(EditTextEmptyHolder) {

            boolean recordExists = myDb.hasObject(Email.getText().toString(), Password.getText().toString());

            if(recordExists == true){
                Intent intentSignIn = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
                startActivity(intentSignIn);
            } else {
                Toast.makeText(getApplicationContext(), "UserName or Password is Wrong, Please Try Again.", Toast.LENGTH_LONG).show();
            }

        }else{
            Toast.makeText(getApplicationContext(), "Enter username and password", Toast.LENGTH_LONG).show();
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

    // Adds and updates data in user info database
    public void UpdateData(String userName) {

        Cursor res = myDb.AllName(userName);
        if (res.getCount() == 0) {

            long i = myDb.insertData(Name.getText().toString(),
                    Email.getText().toString(),
                    Password.getText().toString());
            id = String.valueOf(i);
            boolean isUpdate = myDb.updateData(id,
                    Name.getText().toString(),
                    Email.getText().toString(),Password.getText().toString());
            if(isUpdate != true)
                Toast.makeText(RegisterActivity.this,"Data not Updated",Toast.LENGTH_LONG).show();

            // Calling login method.
            LoginFunction();
            // Adds logged in user email to email database to identify user with ID
            AddEmail();
            // Removes data from EditText fields
            Email.setText(null);
            Password.setText(null);
            Name.setText(null);
            // Saving data for logged in session
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0); // 0 - for private mode
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("key", "olemas"); //TODO kuhu see läheb?
            editor.commit();

            // Starts login activity
            Intent intent = new Intent(RegisterActivity.this, NavigationDrawerActivity.class);
            startActivity(intent);
        }else{

            Toast.makeText(getApplicationContext(), "This username is already taken, please choose another one", Toast.LENGTH_LONG).show();

        }
    }
}