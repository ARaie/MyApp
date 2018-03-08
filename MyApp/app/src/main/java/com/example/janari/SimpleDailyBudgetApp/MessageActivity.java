package com.example.janari.SimpleDailyBudgetApp;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.janari.SimpleDailyBudgetApp.Models.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class  MessageActivity extends AppCompatActivity {

    private Button btnSend, btnLogout, btnRefresh, btnBack;
    private TextView updateTime, email, password, familyBudget;
    private DatabaseReference mDatabase;
    private DatabaseReference mMessageReference;
    private FirebaseAuth mAuth;
    DBHelper budgetDB;
    DataHelper InputDB;
    String ID, emailCopy, passwordCopy, familys;
    double sum = 0.00, Family, exp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Locale locale = Locale.US;
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        this.setContentView(R.layout.activity_message);


        // Buttons
        btnSend = (Button) findViewById(R.id.btn_send);
        btnLogout = (Button) findViewById(R.id.log_out);
        btnRefresh = (Button) findViewById(R.id.refresh);
        btnBack = (Button) findViewById(R.id.btn_back);

        // Fields
        familyBudget = (TextView) findViewById(R.id.budget);
        updateTime = (TextView) findViewById(R.id.time);
        email = (TextView) findViewById(R.id.email_refresh);
        password = (TextView) findViewById(R.id.password_refresh);

        // Get data from Intent
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        ID = extras.getString("id");
        emailCopy = extras.getString("email");
        passwordCopy =extras.getString("password");

        // Set email and password to fields for refreshing user
        email.setText(emailCopy);
        password.setText(passwordCopy);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mMessageReference = FirebaseDatabase.getInstance().getReference("user");
        mAuth = FirebaseAuth.getInstance();

        budgetDB = new DBHelper(this);
        InputDB = new DataHelper(this);

        // Set current date
        TextView dateView = (TextView) findViewById(R.id.date);
        setDate(dateView);


        // Get data from Firebase DB and display it
        mDatabase.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                        String budget = dataSnapshot.child("userBudget").getValue().toString();
                        double Budget = Double.parseDouble(budget);
                        familyBudget.setText( String.format(Locale.US, "%.2f", Budget) );
                        updateTime.setText(dataSnapshot.child("time").getValue().toString());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//TODO Logout btn fails
        // Logout from family database
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), FamilyLoginActivity.class);
                startActivity(intent);
            }


    });

        // Refresh to logged in user
        // TODO needs testing
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }


        });

        //TODO it should be automated, but at the moment it is easier to handle
        // Send data to Firebase database
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseUser user = mAuth.getCurrentUser();
                String userId = user.getUid();

                    //TODO peaks tegema lollikindlaks niiet teist korda vajutades enam ei liidaks summat
                    calculateSum();
                    familys = String.format(Locale.US,"%.2f",sum);
                    String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
                    Message message = new Message(familys, time);
                    mMessageReference.child(userId).setValue(message);


            }

        });

        // Back to main page
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

    }

    public void calculateSum(){

        if((familyBudget.getText().toString()).matches("") ){
            sum = Double.parseDouble(view_sum());
        }else{
            Family = Double.parseDouble(familyBudget.getText().toString());
            exp = Double.parseDouble(view_sum());
            sum = Family + exp;
        }
    }

    // Method to show current date
    public void setDate(TextView view) {
        String date = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());
        view.setText(date);
    }

    // Method for refresh Firebase user
    private void signIn() {

        String emailRefresh = email.getText().toString();
        String passwordRefresh = password.getText().toString();

        mAuth.signInWithEmailAndPassword(emailRefresh, passwordRefresh)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                           task.getResult().getUser();
                        }
                    }
                });
    }
    public String view_sum() {

        Cursor res = budgetDB.AllSum(ID);
        if (res.getCount() == 0) {

            String calculatedSum = "0";
            return calculatedSum;
        }else{

            String calculatedSum = budgetDB.Sum(ID);
            return calculatedSum;
        }
    }
}
