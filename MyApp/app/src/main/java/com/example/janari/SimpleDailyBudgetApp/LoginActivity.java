package com.example.janari.SimpleDailyBudgetApp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class LoginActivity extends AppCompatActivity {

    Button btnviewAll;
    DatabaseHelper myDb;
    Button LogInButton, RegisterButton, emails;
    EditText Email, Password;
    String EmailHolder, PasswordHolder, id;
    Boolean EditTextEmptyHolder;
    public static final String PREFS_NAME = "MyPrefsFile";
    EmailHelper emailDB;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LogInButton = (Button) findViewById(R.id.buttonLogin);
        RegisterButton = (Button) findViewById(R.id.buttonRegister);
        Email = (EditText) findViewById(R.id.editEmail);
        Password = (EditText) findViewById(R.id.editPassword);

        myDb = new DatabaseHelper(this);
        emailDB = new EmailHelper(this);


        //Adding click listener to log in button.
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
                editor.putString("key", "olemas");
                editor.commit();


            }
        });

        // Adding click listener to register button.
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Opening new user registration activity using intent on button click.
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // TODO Temporary button for checking data in user info database
        btnviewAll = (Button) findViewById(R.id.view_data);
        btnviewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //myDb.delete();
                viewAll();

            }
        });

        //TODO Temporary button for check logged in user email
        emails = (Button) findViewById(R.id.aa);
        emails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                viewEmails();

            }
        });
    }

    // TODO Temporary two functions to check data in database
    public void viewAll() {

        Cursor res = myDb.getAllData();
        if (res.getCount() == 0) {
            // show message
            showMessage("Error", "Nothing found");
            return;
        }

        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()) {
            buffer.append("Id :" + res.getString(0) + "\n");
            buffer.append("Name :" + res.getString(1) + "\n");
            buffer.append("Email :" + res.getString(2) + "\n");
            buffer.append("Password :" + res.getString(3) + "\n\n");

        }

        // Show all data
        showMessage("Data", buffer.toString());
    }
    // TODO Temporary two functions to check data in database
    public void viewEmails() {

        Cursor res = emailDB.getAllData();
        if (res.getCount() == 0) {
            // show message
            showMessage("Error", "Nothing found");
            return;
        }

        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()) {
            buffer.append("Id :" + res.getString(0) + "\n");
            buffer.append("Name :" + res.getString(1) + "\n");

        }

        // Show all data
        showMessage("Data", buffer.toString());
    }

    public void showMessage(String title, String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

   // Login function starts from here.
    public void LoginFunction(){

        if(EditTextEmptyHolder) {

            boolean recordExists = myDb.hasObject(Email.getText().toString(), Password.getText().toString());

            if(recordExists == true){
                Intent intentSignIn = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
                Toast.makeText(getApplicationContext(), "Login successful.", Toast.LENGTH_LONG).show();
                startActivity(intentSignIn);
            } else {
                Toast.makeText(getApplicationContext(), "UserName or Password is Wrong, Please Try Again.", Toast.LENGTH_LONG).show();
            }

        }else{
            Toast.makeText(getApplicationContext(), "Enter email and password", Toast.LENGTH_LONG).show();
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


        boolean isUpdate = emailDB.updateEmail("1",
                Email.getText().toString());

    }

    // Saves logged in user email
    public void AddEmail() {


        boolean isInserted = emailDB.insertEmail("1", Email.getText().toString());
        RefreshEmail();
    }

}