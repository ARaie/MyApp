package com.example.janari.SimpleDailyBudgetApp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.janari.SimpleDailyBudgetApp.Models.User;
import com.google.firebase.database.ValueEventListener;

public class FamilyLoginActivity extends AppCompatActivity implements
        View.OnClickListener {

    private static final String TAG = "LoginActivity";

    private TextView txtStatus;
    private TextView txtDetail;
    private EditText edtEmail;
    private EditText edtPassword;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_login);

        txtStatus = (TextView) findViewById(R.id.status);
        txtDetail = (TextView) findViewById(R.id.detail);
        edtEmail = (EditText) findViewById(R.id.edt_email);
        edtPassword = (EditText) findViewById(R.id.edt_password);

        findViewById(R.id.btn_email_sign_in).setOnClickListener(this);
        findViewById(R.id.btn_email_create_account).setOnClickListener(this);
        findViewById(R.id.btn_sign_out).setOnClickListener(this);
        findViewById(R.id.btn_test_message).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();

        if (i == R.id.btn_email_create_account) {
            createAccount(edtEmail.getText().toString(), edtPassword.getText().toString());
        } else if (i == R.id.btn_email_sign_in) {
            signIn(edtEmail.getText().toString(), edtPassword.getText().toString());
        } else if (i == R.id.btn_sign_out) {
            signOut();
        } else if (i == R.id.btn_test_message) {
            testMessage();
        }
    }

    private void createAccount(String email, String password) {

        if (!validateForm(email, password)) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            //writeNewUser(user.getUid(), edtEmail.getText().toString(), edtPassword.getText().toString());
                        } else {
                            FirebaseUser user = mAuth.getCurrentUser();
                            //zwriteNewUser(user.getUid(), edtEmail.getText().toString(), edtPassword.getText().toString());

                        }
                    }
                });
    }

    private void signIn(String email, String password) {
        Log.e(TAG, "signIn:" + email);
        if (!validateForm(email, password)) {
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.e(TAG, "signIn: Success!");

                            // update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Log.e(TAG, "signIn: Fail!", task.getException());
                            Toast.makeText(FamilyLoginActivity.this, "Authentication failed!", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        if (!task.isSuccessful()) {
                            txtStatus.setText("Authentication failed!");
                        }
                    }
                });
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);

    }

    private boolean validateForm(String email, String password) {

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(FamilyLoginActivity.this, "Enter email address!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(FamilyLoginActivity.this, "Enter password!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            txtStatus.setText("User Email: " + user.getEmail());
            txtDetail.setText("Firebase User ID: " + user.getUid());

            findViewById(R.id.email_password_buttons).setVisibility(View.GONE);
            findViewById(R.id.email_password_fields).setVisibility(View.GONE);
            findViewById(R.id.layout_signed_in_control).setVisibility(View.VISIBLE);

        } else {
            txtStatus.setText("Signed Out");
            txtDetail.setText(null);

            findViewById(R.id.email_password_buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.email_password_fields).setVisibility(View.VISIBLE);
            findViewById(R.id.layout_signed_in_control).setVisibility(View.GONE);
        }
    }

    private String getUsernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    private void testMessage() {
        startActivity(new Intent(this, MessageActivity.class));
    }
}