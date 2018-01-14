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

    //TODO Kõik lahtrid peavad olema täidetud

    DatabaseHelper myDb;
    EditText Name, Email, Password;
    Button btnAddData;
    String EmailHolder, PasswordHolder, NameHolder, id;
    Boolean EditTextEmptyHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        myDb = new DatabaseHelper(this);


        //TODO Muuda ära nimetused
        Name = (EditText) findViewById(R.id.editText_name);
        Email = (EditText) findViewById(R.id.editText_surname);
        Password = (EditText) findViewById(R.id.editText_Marks);

        btnAddData = (Button) findViewById(R.id.button_add);
        btnAddData.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                CheckEditTextStatus();
                if(EditTextEmptyHolder) {

                    // Add new user to database
                    UpdateData();
                    //DeleteData();

                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);

                }else{
                    Toast.makeText(getApplicationContext(), "Enter email and password", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    // May be useful method, but not in use at the moment
    public void DeleteData() {

        Integer deletedRows = myDb.deleteData(id);
        if(deletedRows > 0)
            Toast.makeText(RegisterActivity.this,"Data Deleted",Toast.LENGTH_LONG).show();
        else
            Toast.makeText(RegisterActivity.this,"Data not Deleted",Toast.LENGTH_LONG).show();
    }

    // Updates data in user info database
    public void UpdateData() {

        long i = myDb.insertData(Name.getText().toString(),
                Email.getText().toString(),
                Password.getText().toString());
        id = String.valueOf(i);
        boolean isUpdate = myDb.updateData(id,
                Name.getText().toString(),
                Email.getText().toString(),Password.getText().toString());
        if(isUpdate == true)
            Toast.makeText(RegisterActivity.this,"Data Update",Toast.LENGTH_LONG).show();
        else
            Toast.makeText(RegisterActivity.this,"Data not Updated",Toast.LENGTH_LONG).show();
                   }

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

        //TODO Menu is missing some functions...
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}