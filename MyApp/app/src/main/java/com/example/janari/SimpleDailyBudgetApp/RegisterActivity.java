package com.example.janari.SimpleDailyBudgetApp;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    DatabaseHelper myDb;
    EditText Name, Email, Password, ID;
    Button btnAddData, nextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        myDb = new DatabaseHelper(this);

        Name = (EditText)findViewById(R.id.editText_name);
        Email = (EditText)findViewById(R.id.editText_surname);
        Password = (EditText)findViewById(R.id.editText_Marks);
        ID = (EditText)findViewById(R.id.editText_id);
        btnAddData = (Button)findViewById(R.id.button_add);

        AddData();

        nextView = (Button) findViewById(R.id.next_view);
        nextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });
    }


    public  void AddData() {
        btnAddData.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isInserted = myDb.insertData(Name.getText().toString(),
                                Email.getText().toString(),
                                Password.getText().toString() );
                                UpdateData();

                        if(isInserted == true)
                            Toast.makeText(RegisterActivity.this,"Data Inserted",Toast.LENGTH_LONG).show();

                        else
                            Toast.makeText(RegisterActivity.this,"Data not Inserted",Toast.LENGTH_LONG).show();
                    }

                }

        );

    }

    public void DeleteData() {

        Integer deletedRows = myDb.deleteData(ID.getText().toString());
        if(deletedRows > 0)
            Toast.makeText(RegisterActivity.this,"Data Deleted",Toast.LENGTH_LONG).show();
        else
            Toast.makeText(RegisterActivity.this,"Data not Deleted",Toast.LENGTH_LONG).show();
                    }

    public void UpdateData() {
        boolean isUpdate = myDb.updateData(ID.getText().toString(),
                Name.getText().toString(),
                Email.getText().toString(),Password.getText().toString());
        if(isUpdate == true)
            Toast.makeText(RegisterActivity.this,"Data Update",Toast.LENGTH_LONG).show();
        else
            Toast.makeText(RegisterActivity.this,"Data not Updated",Toast.LENGTH_LONG).show();
                   }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}