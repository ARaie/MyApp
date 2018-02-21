package com.example.janari.SimpleDailyBudgetApp;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    DatabaseHelper myDb;
    EditText Name, Email, Password;
    Button btnAddData;
    String EmailHolder, PasswordHolder, NameHolder, id;
    Boolean EditTextEmptyHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Local database
        myDb = new DatabaseHelper(this);

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
                    UpdateData();

                    // Starts login activity
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);

                }else{
                    // When fields are not filled
                    Toast.makeText(getApplicationContext(), "Enter email and password", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    // Adds and updates data in user info database
    public void UpdateData() {

        long i = myDb.insertData(Name.getText().toString(),
                Email.getText().toString(),
                Password.getText().toString());
        id = String.valueOf(i);
        boolean isUpdate = myDb.updateData(id,
                Name.getText().toString(),
                Email.getText().toString(),Password.getText().toString());
        if(isUpdate != true)
            Toast.makeText(RegisterActivity.this,"Data not Updated",Toast.LENGTH_LONG).show();
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
}