package com.example.janari.SimpleDailyBudgetApp;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.janari.SimpleDailyBudgetApp.Models.Message;
import com.example.janari.SimpleDailyBudgetApp.Models.User;
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

public class MessageActivity extends AppCompatActivity {

    private Button btnBack;
    private Button btnSend, btnLogout;
    private TextView tvAuthor;
    private TextView tvTime, sum;
    private DatabaseReference mDatabase;
    private DatabaseReference mMessageReference;
    DBHelper budgetDB;
    String ID, originalBudget, exp;
    private FirebaseAuth mAuth;
    DataHelper InputDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        btnSend = (Button) findViewById(R.id.btn_send);
        btnLogout = (Button) findViewById(R.id.log_out);
        btnBack = (Button) findViewById(R.id.btn_back);
        tvAuthor = (TextView) findViewById(R.id.tv_author);
        tvTime = (TextView) findViewById(R.id.tv_time);
        sum = (TextView) findViewById(R.id.tv_body);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        ID = extras.getString("id");
        originalBudget = extras.getString("original");
        exp = extras.getString("exe");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mMessageReference = FirebaseDatabase.getInstance().getReference("user");
        budgetDB = new DBHelper(this);
        InputDB = new DataHelper(this);
        mAuth = FirebaseAuth.getInstance();

        TextView dateView = (TextView) findViewById(R.id.date);
        setDate(dateView);
        String timesUp = dateView.getText().toString();

        viewAll();

        mDatabase.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                        tvAuthor.setText(dataSnapshot.child("userBudget").getValue().toString());
                        tvTime.setText(dataSnapshot.child("time").getValue().toString());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MessageActivity.this, FamilyLoginActivity.class));
                finish();
            }


    });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView dateView = (TextView) findViewById(R.id.date);
                String timesUp = dateView.getText().toString();
                String periodsEnd = viewEnd();

                FirebaseUser user = mAuth.getCurrentUser();
                String userId = user.getUid();
                TextView e = (TextView) findViewById(R.id.edt_sent_text);
                String budget = e.getText().toString();
                String w = expences();
                sum = (TextView) findViewById(R.id.tv_body);

                //TODO sellepärast ei mätsi et 22.02.2018 ja 22.2.2018
                if (timesUp.matches(periodsEnd)){
                    if (originalBudget.matches(budget)){

                        String family = String.valueOf(Double.parseDouble(budget) + Double.parseDouble(w));
                        String time = "One of Your family member has time period over";
                        Message message = new Message(family, time);
                        mMessageReference.child(userId).setValue(message);
                    }else{

                        String expenses = String.valueOf(Double.parseDouble(w) - Double.parseDouble(exp));
                        String time = "One of Your family member has time period over";
                        Message message = new Message(expenses, time);
                        mMessageReference.child(userId).setValue(message);
                        exp = "0";
                    }
                }else{
                    if (originalBudget.matches(budget)){

                        String family = String.valueOf(Double.parseDouble(budget) + Double.parseDouble(w));
                        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
                        Message message = new Message(family, time);
                        mMessageReference.child(userId).setValue(message);
                        originalBudget = "0";
                    }else{

                        String expenses = String.valueOf(Double.parseDouble(w) - Double.parseDouble(exp));
                        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
                        Message message = new Message(expenses, time);
                        mMessageReference.child(userId).setValue(message);
                        exp = "0";
                    }
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

    }
    public String expences(){

        String familyBudget = tvAuthor.getText().toString();

        if (familyBudget.matches("")) {

            return "0";
        }else{
            return familyBudget;
        }
    }

    public double viewAll() {

        TextView e = (TextView) findViewById(R.id.edt_sent_text);

        Cursor res = budgetDB.AllSum(ID);
        if (res.getCount() == 0) {

            double budget = 0;
            String a = String.valueOf(budget);
            e.setText(a);
            return budget;
        } else {

            String budget = budgetDB.Sum(ID);
            double a = Double.parseDouble(budget);
            e.setText(budget);
            return a;
        }
    }
    public void setDate(TextView view) {
        String date = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());
        view.setText(date);
    }
    public String viewEnd() {

        Cursor res = InputDB.AllEnd(ID);
        if (res.getCount() == 0) {

            return "28.02.2800";
        }else{

            String end_date = InputDB.End(ID);
            return end_date;
        }
    }
}
