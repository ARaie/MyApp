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
    private TextView tvTime;
    private DatabaseReference mDatabase;
    private DatabaseReference mMessageReference;
    DBHelper budgetDB;
    String ID;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        btnSend = (Button) findViewById(R.id.btn_send);
        btnLogout = (Button) findViewById(R.id.log_out);
        btnBack = (Button) findViewById(R.id.btn_back);
        tvAuthor = (TextView) findViewById(R.id.tv_author);
        tvTime = (TextView) findViewById(R.id.tv_time);
        ID = getIntent().getStringExtra("id");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mMessageReference = FirebaseDatabase.getInstance().getReference("users");
        budgetDB = new DBHelper(this);
        mAuth = FirebaseAuth.getInstance();

        TextView dateView = (TextView) findViewById(R.id.date);
        setDate(dateView);

        viewAll();

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

                FirebaseUser user = mAuth.getCurrentUser();
                String userId = user.getUid();
                TextView e = (TextView) findViewById(R.id.edt_sent_text);
                String s = e.getText().toString();
                String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
                Message message = new Message(s, time);
                mMessageReference.child("users").child(userId).setValue(message);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ValueEventListener messageListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Message message = dataSnapshot.getValue(Message.class);
                    tvAuthor.setText(message.userBudget);
                    tvTime.setText(message.time);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
            }
        };

        mMessageReference.addValueEventListener(messageListener);
    }

    public void viewAll() {

        Cursor res = budgetDB.budget(ID);
        if (res.getCount() == 0) {

            TextView textValue = (TextView) findViewById(R.id.edt_sent_text);
            textValue.setText("0");
            return;
        } else {

            double budget = budgetDB.bud(ID);
            double rounded = Math.round(budget);
            String budgetToString = String.valueOf(rounded);
            TextView textValue = (TextView) findViewById(R.id.edt_sent_text);
            textValue.setText(budgetToString);
        }
    }
    public void setDate(TextView view) {
        String date = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());
        view.setText(date);
    }
}
