package com.example.janari.SimpleDailyBudgetApp;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.janari.SimpleDailyBudgetApp.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class FamilyLoginActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    String ID;
    private EditText emailField, passwordField;
    private Button signInButton, signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Locale locale = Locale.US;
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        this.setContentView(R.layout.activity_family_login);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // Fields
        emailField = findViewById(R.id.field_email);
        passwordField = findViewById(R.id.field_password);
        signInButton = findViewById(R.id.button_sign_in);
        signUpButton = findViewById(R.id.button_sign_up);

        // Click listeners
        signInButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);

        // Intents from NavigationDrawerActivity
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        ID = extras.getString("id");

        // Set data from intent to fields
        TextView userID = (TextView) findViewById(R.id.id);
        userID.setText(ID);
    }
    @Override
    public void onStart() {
        super.onStart();

        // Check auth on Activity start
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }
    }

    private void signIn() {
        if (!validateForm()) {
            return;
        }
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(FamilyLoginActivity.this, "Sign In Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signUp() {

        if (!validateForm()) {
            return;
        }

        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(FamilyLoginActivity.this, "Sign Up Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void onAuthSuccess(FirebaseUser user) {

        // Write new user
        writeNewUser(user.getUid(), user.getEmail(), user.getEmail());

        // When login is successful, all important data are sent with intent to MessageActivity class
        TextView userId = (TextView) findViewById(R.id.id);
        String string = userId.getText().toString();
        TextView email = (TextView) findViewById(R.id.field_email);
        String string4 = email.getText().toString();
        TextView password = (TextView) findViewById(R.id.field_password);
        String string5 = password.getText().toString();
        Intent intent = new Intent(FamilyLoginActivity.this, MessageActivity.class);
        Bundle extras = new Bundle();
        extras.putString("id", string);
        extras.putString("email", string4);
        extras.putString("password", string5);
        intent.putExtras(extras);
        startActivity(intent);
        finish();
    }

    // Method to check that fields are filled
    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(emailField.getText().toString())) {
            emailField.setError("Required");
            result = false;
        } else {
            emailField.setError(null);
        }

        if (TextUtils.isEmpty(passwordField.getText().toString())) {
            passwordField.setError("Required");
            result = false;
        } else {
            passwordField.setError(null);
        }

        return result;
    }

    // Method to write new user to Firebase database
    private void writeNewUser(String userId, String email, String password) {
        User user = new User(email, password);

        mDatabase.child("users").child(userId).setValue(user);
    }

    // Sign in and Sign up buttons click
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_sign_in) {
           signIn();
        } else if (i == R.id.button_sign_up) {
            signUp();
        }
    }
}