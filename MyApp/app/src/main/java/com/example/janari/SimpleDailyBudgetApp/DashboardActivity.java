package com.example.janari.SimpleDailyBudgetApp;

import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

//TODO Helper Activity to see all data in database
public class DashboardActivity extends AppCompatActivity {

    DatabaseHelper myDb;
    Button btnviewAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        myDb = new DatabaseHelper(this);

        btnviewAll = (Button) findViewById(R.id.btnviewAll);

        btnviewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                viewAll();

                Toast.makeText(DashboardActivity.this, "Log Out Successfull", Toast.LENGTH_LONG).show();
            }
        });
    }

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
}
