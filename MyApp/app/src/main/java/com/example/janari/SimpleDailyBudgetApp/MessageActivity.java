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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        btnSend = (Button) findViewById(R.id.btn_send);
        btnLogout = (Button) findViewById(R.id.log_out);
        btnBack = (Button) findViewById(R.id.btn_back);
        tvAuthor = (TextView) findViewById(R.id.tv_author);
        tvAuthor.setText("0");
        tvTime = (TextView) findViewById(R.id.tv_time);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        ID = extras.getString("id");
        originalBudget = extras.getString("original");
        exp = extras.getString("exe");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mMessageReference = FirebaseDatabase.getInstance().getReference("user");
        budgetDB = new DBHelper(this);
        mAuth = FirebaseAuth.getInstance();

        TextView dateView = (TextView) findViewById(R.id.date);
        setDate(dateView);

        viewAll();
        expences();

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

                FirebaseUser user = mAuth.getCurrentUser();
                String userId = user.getUid();
                TextView e = (TextView) findViewById(R.id.edt_sent_text);
                String s = e.getText().toString();
                String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
                Message message = new Message(s, time);
                mMessageReference.child(userId).setValue(message);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    public void expences(){

        tvAuthor = (TextView) findViewById(R.id.tv_author);
        double familyBudget = Double.parseDouble(tvAuthor.getText().toString());
        double budget = viewAll();
        sum = (TextView) findViewById(R.id.tv_body);

        if (originalBudget == (String.valueOf(viewAll()))){

            //String family = String.valueOf(budget + familyBudget);
            //sum.setText(String.valueOf(viewAll()));
        }else{

           // String expenses = String.valueOf(familyBudget - Double.parseDouble(exp));
            sum.setText(exp);
        }
    }

    public double viewAll() {

        TextView e = (TextView) findViewById(R.id.edt_sent_text);

        Cursor res = budgetDB.budget(ID);
        if (res.getCount() == 0) {

            double budget = 0;
            String a = String.valueOf(budget);
            e.setText(a);
            return budget;
        } else {

            double budget = budgetDB.bud(ID);
            String a = String.valueOf(budget);
            e.setText(a);
            return budget;
        }
    }
    public void setDate(TextView view) {
        String date = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());
        view.setText(date);
    }
}
