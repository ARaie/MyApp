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

    //TODO Kõik lahtrid peavad olema täidetud

    DatabaseHelper myDb;
    EditText Name, Email, Password;
    Button btnAddData;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        myDb = new DatabaseHelper(this);


        //TODO Muuda ära nimetused
        Name = (EditText)findViewById(R.id.editText_name);
        Email = (EditText)findViewById(R.id.editText_surname);
        Password = (EditText)findViewById(R.id.editText_Marks);

       id = myDb.insertData(Name.getText().toString(),
               Email.getText().toString(),
               Password.getText().toString());

        Intent intent = new Intent(getApplicationContext(), EnterIncomeAndExpensesActivity.class);
        intent.putExtra("id", id);

        btnAddData = (Button)findViewById(R.id.button_add);

        // calling method for add data to user info database
        AddData();

    }

    // Adding data to user info database
    public  void AddData() {
        btnAddData.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDb.insertData(Name.getText().toString(),
                                Email.getText().toString(),
                                Password.getText().toString() );
                                UpdateData();
                                //DeleteData();

                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);

                    }

                }

        );

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
        boolean isUpdate = myDb.updateData(id,
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