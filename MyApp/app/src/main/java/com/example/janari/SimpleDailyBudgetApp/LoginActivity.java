package com.example.janari.SimpleDailyBudgetApp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
    Button LogInButton, RegisterButton ;
    EditText Email, Password;
    String EmailHolder, PasswordHolder;
    Boolean EditTextEmptyHolder;
    SQLiteDatabase sqLiteDatabaseObj;
    Cursor cursor;
    String TempPassword = "NOT_FOUND", email ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LogInButton = (Button)findViewById(R.id.buttonLogin);
        RegisterButton = (Button)findViewById(R.id.buttonRegister);
        Email = (EditText)findViewById(R.id.editEmail);
        Password = (EditText)findViewById(R.id.editPassword);

        myDb = new DatabaseHelper(this);

        //Adding click listener to log in button.
        LogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Calling EditText is empty or no method.
                CheckEditTextStatus();
                // Calling login method.
                LoginFunction();
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

                viewAll();

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

            // Opening SQLite database write permission.
            sqLiteDatabaseObj = myDb.getWritableDatabase();

            // Adding search email query to cursor.
            cursor = sqLiteDatabaseObj.query(DatabaseHelper.TABLE_NAME, null, " " + DatabaseHelper.COL_3 + "=?", new String[]{Email.toString()}, null, null, null);

            while (cursor.moveToNext()) {

                if (cursor.isFirst()) {

                    cursor.moveToFirst();

                    // Storing Password associated with entered email.
                    TempPassword = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_4));
                    //email = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_3));

                    // Closing cursor.
                    cursor.close();
                }
            }

            // Calling method to check final result
            CheckFinalResult();

        }
        else {

            //If any of login EditText empty then this block will be executed.
            Toast.makeText(LoginActivity.this,"Please Enter UserName or Password.",Toast.LENGTH_LONG).show();

        }

    }

    // Checking EditText is empty or not.
    public void CheckEditTextStatus(){

        // Getting value from All EditText and storing into String Variables.
        EmailHolder = Email.getText().toString();
        PasswordHolder = Password.getText().toString();

        // Checking EditText is empty or no using TextUtils.
        if( TextUtils.isEmpty(EmailHolder) || TextUtils.isEmpty(PasswordHolder)){

            EditTextEmptyHolder = false ;

        }
        else {

            EditTextEmptyHolder = true ;
        }
    }

    // Checking entered password from SQLite database email associated password.
    public void CheckFinalResult(){

        if(TempPassword.equalsIgnoreCase(PasswordHolder))
        {

            Toast.makeText(LoginActivity.this,"Login Successfully",Toast.LENGTH_LONG).show();

            // Going to NavigationDrawerActivity after login success message.
            Intent intent = new Intent(LoginActivity.this, NavigationDrawerActivity.class);
            startActivity(intent);
        }
        else {

            //TODO tegelt peaks salasõna vale olema. Ehk on asi et kuskil peaks passwordi stringiks teisendama, sest emaili tundis ka alles siis ära
            Toast.makeText(LoginActivity.this,"UserName or Password is Wrong, Please Try Again.",Toast.LENGTH_LONG).show();

            Intent intent = new Intent(LoginActivity.this, NavigationDrawerActivity.class);

            // Passing user name and email to navigation drawer header.
            viewName();
            Email = (EditText)findViewById(R.id.editEmail);
            EditText userName = (EditText)findViewById(R.id.name);
            String stringEmail = Email.getText().toString();
            String stringName = userName.getText().toString();
            intent.putExtra("userName", stringName);
            intent.putExtra("userEmail", stringEmail);
            startActivity(intent);

        }
        TempPassword = "NOT_FOUND" ;

    }

    // Method fo getting user name from database to pass it to navigation drawer header view
    public void viewName() {

        Cursor res = myDb.getAllData();

        EditText userName = (EditText)findViewById(R.id.name);
        while (res.moveToNext()) {
            userName.setText(res.getString(1));
        }

    }

}