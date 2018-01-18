
// I can't change root module name, I am looking for a way to change MyApp name
// Same thing is with package name. The name is such because I created the project on Janari's computer.

package com.example.janari.SimpleDailyBudgetApp;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class EnterIncomeAndExpensesActivity extends AppCompatActivity {

    DatePickerDialog datePickerDialog;
    Boolean EmptyField;
    DBHelper budgetDB;
    DataHelper InputDB;
    String dailySum = "", ID, mIncome, mExpenses, mStart, mEnd;

    //TODO going back to daily view activity ID is wrong
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_income_and_expenses);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        budgetDB = new DBHelper(this);
        InputDB = new DataHelper(this);
        ID = getIntent().getStringExtra("id");

        //TODO temporary. For check user ID
        TextView start = (TextView) findViewById(R.id.o);
        start.setText(ID);


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
                AddDaily();

                //Method that adds data to input database and refresh it
                AddData();

            }
        });

        //TODO Temporary. Checking input database
        Button i = (Button) findViewById(R.id.i);
        i.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //InputDB.delete();
                viewData();
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

        // Methods for ask data from input database and show them in fields
        //TODO Dates are shown in ints. Need to be corrected.
        viewIncome();
        viewExpenses();
        viewStart();
        viewEnd();
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
    // TODO Math should be corrected - same day for start and end collapse the app
    public void CalculateDataFunction() {

        if (EmptyField) {
            // Here I get income and fixed expenses data and calculate it for one day
            EditText income = (EditText) findViewById(R.id.incomes);
            String stringIncome = income.getText().toString();
            mIncome = stringIncome;
            double incomeValue = Double.parseDouble(stringIncome);

            EditText fixedExpenses = (EditText) findViewById(R.id.fixed_expenses);
            String stringExpences = fixedExpenses.getText().toString();
            mExpenses = stringExpences;
            double fixedExpensesValue = Double.parseDouble(stringExpences);

            // Get dates from EditText fields and use Daybetween() method to set days for value calculation process
            EditText start = (EditText) findViewById(R.id.start_date);
            String stringStart = start.getText().toString();
            mStart = stringStart;
            EditText end = (EditText) findViewById(R.id.end_date);
            String stringEnd = end.getText().toString();
            mEnd = stringEnd;

            // TODO I don't know if it is better when period 1.11-30.11 makes 30days or period 1.11-1.12. (How to count days.)
            double value = (incomeValue - fixedExpensesValue) / Daybetween(stringStart, stringEnd, "dd.MM.yyyy");
            double rounded = Math.round(value);
            String calculated = String.valueOf(rounded);

            // I use Indent to send calculated value to NavigationDrawerActivity - because this is at the moment my main activity
            Intent intent = new Intent(EnterIncomeAndExpensesActivity.this, NavigationDrawerActivity.class);
            // Here I get calculated dailySum from this method to use this value to addData() method
            dailySum = calculated;
            intent.putExtra("dailySum", dailySum);
            startActivity(intent);


        }else {
            Toast.makeText(EnterIncomeAndExpensesActivity.this,"Please fill all fields",Toast.LENGTH_LONG).show();

        }
    }

    // Method for adding data to database. Start date, end date and Daily sum.
    public  void AddDaily() {

        boolean isInserted = budgetDB.insertDaily(ID, dailySum.toString());
        UpdateData();

    }
    // Refreshing data in database
    public void UpdateData() {

        boolean isUpdate = budgetDB.updateSum(ID,
                dailySum.toString());
    }

    // TODO Temporary for checking
    public void viewData() {

        Cursor res = InputDB.getAllData();
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
            buffer.append("Password :" + res.getString(4) + "\n\n");

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

    // Add and update data for user entered period, income and expenses
    public  void AddData() {

        boolean isInserted = InputDB.insertData(ID, mIncome.toString(), mExpenses.toString(), mStart.toString(), mEnd.toString());
        UpdateInput();

    }
    public void UpdateInput() {

            boolean isUpdate = InputDB.updateData(ID,
                    mIncome.toString(), mExpenses.toString(), mStart.toString(), mEnd.toString());

        }

    public void viewIncome() {

        Cursor res = InputDB.AllIncome(ID);
        if (res.getCount() == 0) {

            TextView textValue = (TextView) findViewById(R.id.incomes);
            textValue.setText(null);
            return;
        }else{

            long input = InputDB.Income(ID);
            String inputToString = String.valueOf(input);
            TextView textValue = (TextView) findViewById(R.id.incomes);
            textValue.setText(inputToString);
        }
    }
    public void viewStart() {

        Cursor res = InputDB.AllStart(ID);
        if (res.getCount() == 0) {

            TextView textValue = (TextView) findViewById(R.id.start_date);
            textValue.setText(null);
            return;
        }else{

            long start_date = InputDB.Start(ID);
            String start_dateToString = String.valueOf(start_date);
            TextView textValue = (TextView) findViewById(R.id.start_date);
            textValue.setText(start_dateToString);
        }
    }
    public void viewExpenses() {

        Cursor res = InputDB.AllExpenses(ID);
        if (res.getCount() == 0) {

            TextView textValue = (TextView) findViewById(R.id.fixed_expenses);
            textValue.setText(null);
            return;
        }else{

            long expenses = InputDB.Expenses(ID);
            String expensesToString = String.valueOf(expenses);
            TextView textValue = (TextView) findViewById(R.id.fixed_expenses);
            textValue.setText(expensesToString);
        }
    }
    public void viewEnd() {

        Cursor res = InputDB.AllEnd(ID);
        if (res.getCount() == 0) {

            TextView textValue = (TextView) findViewById(R.id.end_date);
            textValue.setText(null);
            return;
        }else{

            long end_date = InputDB.End(ID);
            String end_dateToString = String.valueOf(end_date);
            TextView textValue = (TextView) findViewById(R.id.end_date);
            textValue.setText(end_dateToString);
        }
    }
}