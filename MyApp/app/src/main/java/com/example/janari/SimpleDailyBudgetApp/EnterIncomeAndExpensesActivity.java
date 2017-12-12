
// I can't change root module name, I am looking for a way to change MyApp name
// Same thing is with package name. The name is such because I created the project on Janari's computer.

package com.example.janari.SimpleDailyBudgetApp;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class EnterIncomeAndExpensesActivity extends AppCompatActivity {

    DatePickerDialog datePickerDialog;
    Boolean EmptyField;
    DBHelper budgetDB;
    String dailySum = "", ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_income_and_expenses);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        budgetDB = new DBHelper(this);


        // TODO majority of picking date code should move to CalendarActivity class
        final EditText startDate = (EditText) findViewById(R.id.start_date);
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the values for day of month , month and year from a date picker
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                // date picker dialog
                datePickerDialog = new DatePickerDialog(EnterIncomeAndExpensesActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                startDate.setText(dayOfMonth + "."
                                        + (monthOfYear + 1) + "." + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        final EditText endDate = (EditText) findViewById(R.id.end_date);
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the values for day of month , month and year from a date picker
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                // date picker dialog
                datePickerDialog = new DatePickerDialog(EnterIncomeAndExpensesActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                endDate.setText(dayOfMonth + "."
                                        + (monthOfYear + 1) + "." + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        // This button helps to send calculated sum to Daily sum field
        Button calculate = (Button) findViewById(R.id.calculate);
        calculate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // Check that all fields are filled
                CheckFieldsAreFilled();

                // Daily sum calculating method
                CalculateDataFunction();

                // Method that adds data do user budget database
                AddData();
            }
        });

        // TODO this button and viewAll() method is for checking the user budget database. Temporary function.
        Button all = (Button) findViewById(R.id.all);
        all.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                viewAll();

            }
        });
        // Back "button"
        ImageView back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(EnterIncomeAndExpensesActivity.this, NavigationDrawerActivity.class);
                startActivity(intent);
            }
        });
    }

// Method for get the days between user selected period
    public double Daybetween(String date1, String date2, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date Date1 = null, Date2 = null;
        try {
            Date1 = sdf.parse(date1);
            Date2 = sdf.parse(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (double) (Date2.getTime() - Date1.getTime()) / (24 * 60 * 60 * 1000);
    }

    // Checking EditText is empty or not.
    public void CheckFieldsAreFilled() {

        EditText start = (EditText) findViewById(R.id.start_date);
        String startDate = start.getText().toString();
        EditText end = (EditText) findViewById(R.id.end_date);
        String stringEnd = end.getText().toString();
        EditText income = (EditText) findViewById(R.id.incomes);
        String stringIncome = income.getText().toString();
        EditText fixedExpenses = (EditText) findViewById(R.id.fixed_expenses);
        String stringExpences = fixedExpenses.getText().toString();


        // Checking EditText is empty or no using TextUtils.
        if (TextUtils.isEmpty(startDate) || TextUtils.isEmpty(stringEnd) || TextUtils.isEmpty(stringIncome) || TextUtils.isEmpty(stringExpences)) {

            EmptyField = false ;

        }
        else {

            EmptyField = true ;
        }
    }

    // Calculating daily sum logic is in this method
    public void CalculateDataFunction() {

        if (EmptyField) {
            // Here I get income and fixed expenses data and calculate it for one day
            EditText income = (EditText) findViewById(R.id.incomes);
            String stringIncome = income.getText().toString();
            double incomeValue = Double.parseDouble(stringIncome);

            EditText fixedExpenses = (EditText) findViewById(R.id.fixed_expenses);
            String stringExpences = fixedExpenses.getText().toString();
            double fixedExpensesValue = Double.parseDouble(stringExpences);

            // Get dates from EditText fields and use Daybetween() method to set days for value calculation process
            EditText start = (EditText) findViewById(R.id.start_date);
            String stringStart = start.getText().toString();
            EditText end = (EditText) findViewById(R.id.end_date);
            String stringEnd = end.getText().toString();

            // TODO I don't know if it is better when period 1.11-30.11 makes 30days or period 1.11-1.12. (How to count days.)
            double value = (incomeValue - fixedExpensesValue) / Daybetween(stringStart, stringEnd, "dd.MM.yyyy");
            double rounded = Math.round(value);
            String calculated = String.valueOf(rounded);

            // I use Indent to send calculated value to NavigationDrawerActivity - because this is at the moment my main activity
            Intent intent = new Intent(getBaseContext(), NavigationDrawerActivity.class);
            // Here I get calculated dailySum from this method to use this value to addData() method
            dailySum = calculated;
            startActivity(intent);

        }else {
            Toast.makeText(EnterIncomeAndExpensesActivity.this,"Please fill all fields",Toast.LENGTH_LONG).show();

        }
    }

    // Method for adding data to database. Start date, end date and Daily sum.
    public  void AddData() {

        EditText start = (EditText) findViewById(R.id.start_date);
        EditText end = (EditText) findViewById(R.id.end_date);

        boolean isInserted = budgetDB.insertData(dailySum.toString(),
                start.getText().toString(),
                end.getText().toString() );
        UpdateData();

        if(isInserted == true)
            Toast.makeText(EnterIncomeAndExpensesActivity.this,"Data Inserted",Toast.LENGTH_LONG).show();

        else
            Toast.makeText(EnterIncomeAndExpensesActivity.this,"Data not Inserted",Toast.LENGTH_LONG).show();
    }

    // Useful method, but become to use maybe later.
    public void DeleteData() {

        ID = "";

        Integer deletedRows = budgetDB.deleteData(ID);
        if(deletedRows > 0)
            Toast.makeText(EnterIncomeAndExpensesActivity.this,"Data Deleted",Toast.LENGTH_LONG).show();
        else
            Toast.makeText(EnterIncomeAndExpensesActivity.this,"Data not Deleted",Toast.LENGTH_LONG).show();
    }

    // It should be for changing data in database
    public void UpdateData() {

       ID = "";
        EditText start = (EditText) findViewById(R.id.start_date);
        EditText end = (EditText) findViewById(R.id.end_date);

        boolean isUpdate = budgetDB.updateData(ID,
                dailySum.toString(),
                start.getText().toString(),end.getText().toString());
        if(isUpdate == true)
            Toast.makeText(EnterIncomeAndExpensesActivity.this,"Data Update",Toast.LENGTH_LONG).show();
        else
            Toast.makeText(EnterIncomeAndExpensesActivity.this,"Data not Updated",Toast.LENGTH_LONG).show();
    }

    // Two methods for showing all data in user budget database. Temporary. Only for checking.
    public void viewAll() {

        Cursor res = budgetDB.getAllData();
        if (res.getCount() == 0) {
            // show message
            showMessage("Error", "Nothing found");
            return;
        }

        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()) {
            buffer.append("Id :" + res.getString(0) + "\n");
            buffer.append("daily :" + res.getString(1) + "\n");
            buffer.append("start :" + res.getString(2) + "\n");
            buffer.append("end :" + res.getString(3) + "\n\n");
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