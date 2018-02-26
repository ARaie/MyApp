package com.example.janari.SimpleDailyBudgetApp;

import android.content.Intent;
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
    private TextView updateTime, email, password, familyBudget, tt, ttt;
    private DatabaseReference mDatabase;
    private DatabaseReference mMessageReference;
    private FirebaseAuth mAuth;
    DBHelper budgetDB;
    DataHelper InputDB;
    String ID, originalBudget, userExpenses, emailCopy, passwordCopy, familyB, family;

// TODO
    //TODO muidu on timmu aga kui uus family member tahab liituda pärast seda kui ta on juba natuke toimetanud siis on jama majas
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

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
        originalBudget = extras.getString("original");
        userExpenses = extras.getString("exe");
        emailCopy = extras.getString("email");
        passwordCopy = extras.getString("password");
        family = extras.getString("family");

        // Set email and password to fields for refreshing user
        email.setText(emailCopy);
        password.setText(passwordCopy);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mMessageReference = FirebaseDatabase.getInstance().getReference("user");
        mAuth = FirebaseAuth.getInstance();

        budgetDB = new DBHelper(this);
        InputDB = new DataHelper(this);
        tt = (TextView) findViewById(R.id.tt);
        tt.setText(originalBudget);
        ttt = (TextView) findViewById(R.id.ttt);
        ttt.setText(family);

        // Set current date
        TextView dateView = (TextView) findViewById(R.id.date);
        setDate(dateView);

        // TODO when some familymember period is over then it is shown. Needs thinking
        String timesUp = dateView.getText().toString();

        // Get data from Firebase DB and display it
        mDatabase.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                        String budget = dataSnapshot.child("userBudget").getValue().toString();
                        double Budget = Double.parseDouble(budget);
                        familyBudget.setText( String.format( "%.2f", Budget) );
                        updateTime.setText(dataSnapshot.child("time").getValue().toString());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//TODO miks logout äpi kokku jooksuyab?
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


                //TextView dateView = (TextView) findViewById(R.id.date);
               //String timesUp = dateView.getText().toString();

                FirebaseUser user = mAuth.getCurrentUser();
                String userId = user.getUid();
                double fam = Double.parseDouble(family);
                String Family = String.format( "%.2f", fam);

                String w = expences();

                /*// When one familymember's period is over then others can get some notice
                if (timesUp.matches(viewEnd())) {
                    if (originalBudget.matches(budget)) {

                        String family = String.valueOf(Double.parseDouble(budget) + Double.parseDouble(w));
                        String time = "One of Your family member has time period over";
                        Message message = new Message(family, time);
                        mMessageReference.child(userId).setValue(message);
                    } else {
                        String expenses = String.valueOf(Double.parseDouble(w) - Double.parseDouble(userExpenses));
                        String time = "One of Your family member has time period over";
                        Message message = new Message(expenses, time);
                        mMessageReference.child(userId).setValue(message);
                        userExpenses = "0";
                    }*/

                    // TODO saving needs more thinking and testing
                    // Normal workflow to save data to Firebase database

                    if (originalBudget.matches(Family)) {

                        String familys = String.valueOf(fam + Double.parseDouble(w));
                        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
                        Message message = new Message(familys, time);
                        mMessageReference.child(userId).setValue(message);
                        originalBudget = "0";
                    } else {

                        String expenses = String.valueOf(Double.parseDouble(w) - Double.parseDouble(userExpenses));
                        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
                        Message message = new Message(expenses, time);
                        mMessageReference.child(userId).setValue(message);
                        userExpenses = "0";
                   }

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

    // Method for check when Family database is empty
    public String expences(){

        familyB = familyBudget.getText().toString();

        if (familyB.matches("")) {

            return "0";
        }else{
            return familyB;
        }
    }

    // Method to show current date
    public void setDate(TextView view) {
        String date = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());
        view.setText(date);
    }

    // Method that gives period end date from local database
    public String viewEnd() {

        Cursor res = InputDB.AllEnd(ID);
        if (res.getCount() == 0) {
            return "23.02.2800";
        }else{

            String end_date = InputDB.End(ID);
            return end_date;
        }
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
}
