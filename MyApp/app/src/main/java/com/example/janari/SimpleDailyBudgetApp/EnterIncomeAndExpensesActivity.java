
// I can't change root module name, I am looking for a way to change MyApp name
// Same thing is with package name. The name is such because I created the project on Janari's computer.

package com.example.janari.SimpleDailyBudgetApp;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
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
import java.util.Locale;


public class EnterIncomeAndExpensesActivity extends AppCompatActivity {

    DatePickerDialog datePickerDialog;
    Boolean EmptyField;
    DBHelper budgetDB;
    DataHelper InputDB;
    String dailySum = "", ID, mIncome, mExpenses, mStart, mEnd, Days, sum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Locale locale = Locale.US;
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        this.setContentView(R.layout.activity_enter_income_and_expenses);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        budgetDB = new DBHelper(this);
        InputDB = new DataHelper(this);
        ID = getIntent().getStringExtra("id");
        TextView start = (TextView) findViewById(R.id.o);
        start.setText(ID);


        // View all user money that is left for selected period
        String all = view_sum();
        double leftSum = Double.parseDouble(all);
        TextView setUserName = (TextView) findViewById(R.id.all);
        setUserName.setText( String.format( "%.2f", leftSum) );


        // Calendar activity code
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
                                if (dayOfMonth < 10 && monthOfYear < 10){
                                    startDate.setText("0" + dayOfMonth + "." + "0" + (monthOfYear + 1) + "." + year);
                                }else if (monthOfYear < 9){
                                    startDate.setText(dayOfMonth + "." + "0"
                                            + (monthOfYear + 1) + "." + year);
                                }else if (dayOfMonth < 10) {
                                    startDate.setText("0" + dayOfMonth + "." + (monthOfYear + 1) + "." + year);
                                }else{
                                    startDate.setText(dayOfMonth + "."
                                            + (monthOfYear + 1) + "." + year);
                                }

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

                            if (dayOfMonth < 10 && monthOfYear < 10){
                                endDate.setText("0" + dayOfMonth + "." + "0" + (monthOfYear + 1) + "." + year);
                            }else if (monthOfYear < 9){
                                    endDate.setText(dayOfMonth + "." + "0"
                                            + (monthOfYear + 1) + "." + year);
                                }else if (dayOfMonth < 10) {
                                    endDate.setText("0" + dayOfMonth + "." + (monthOfYear + 1) + "." + year);
                                }else{
                                    endDate.setText(dayOfMonth + "."
                                            + (monthOfYear + 1) + "." + year);
                                }
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        // This button helps to send calculated sum to main activity
        Button calculate = (Button) findViewById(R.id.calculate);
        calculate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // Dialog window to remind user to not recalculate existing data.
                AlertDialog.Builder dialog = new AlertDialog.Builder(EnterIncomeAndExpensesActivity.this);
                dialog.setCancelable(false);
                dialog.setTitle("Calculating daily sum");
                dialog.setMessage("When daily sum is already calculated then recalculating deletes your existing data" );
                dialog.setPositiveButton("Calculate", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Check that all fields are filled
                        CheckFieldsAreFilled();

                        // Daily sum calculating method
                        CalculateDataFunction();

                        if (EmptyField == true){

                            // Method that adds data do user budget database
                            AddDaily();

                            //Method that adds data to input database and refresh it
                            AddData();
                        }
                    }
                })
                        .setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Action for "Cancel".
                            }
                        });

                final AlertDialog alert = dialog.create();
                alert.show();


            }
        });

        //TODO Temporary. Checking input database
        /*Button i = (Button) findViewById(R.id.i);
        i.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //InputDB.delete();
                viewData();
        }
    });*/


        // Back "button", may be same like in familyActivity page
        ImageView back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(EnterIncomeAndExpensesActivity.this, NavigationDrawerActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Methods for ask data from input database and show them in fields
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
            String today = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());
            EditText end = (EditText) findViewById(R.id.end_date);
            String stringEnd = end.getText().toString();
            mEnd = stringEnd;

            // Days and sum are saved also separately to database for using them in further calculations
            double days = Daybetween(today, stringEnd, "dd.MM.yyyy") + 1;
            Days = String.valueOf(days);
            double sumDouble = incomeValue - fixedExpensesValue;
            sum = String.valueOf(sumDouble);
            double value = (incomeValue - fixedExpensesValue) / days;
            String calculated = String.valueOf(value);

            // I use Indent to send calculated value to NavigationDrawerActivity
            Intent intent = new Intent(EnterIncomeAndExpensesActivity.this, NavigationDrawerActivity.class);

            // Here I get calculated dailySum from this method to use this value to addData() method
            dailySum = calculated;
            intent.putExtra("dailySum", dailySum);
            startActivity(intent);
            finish();


        }else {
            Toast.makeText(EnterIncomeAndExpensesActivity.this,"Please fill all fields",Toast.LENGTH_LONG).show();

        }
    }

    // Method for adding data to budget database. ID and Daily sum and all sum.
    public  void AddDaily() {

        budgetDB.insertDaily(ID, dailySum, sum);
        UpdateData();

    }
    // Refreshing data in database
    public void UpdateData() {

        budgetDB.updateSum(ID,
                dailySum, sum);
    }

    // TODO Temporary for checking input database
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
            buffer.append("Password :" + res.getString(3) + "\n");
            buffer.append("Password :" + res.getString(4) + "\n");
            buffer.append("Password :" + res.getString(5) + "\n");

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

        InputDB.insertData(ID, mIncome.toString(), mExpenses.toString(), mStart.toString(), mEnd.toString(), Days);
        UpdateInput();

    }
    // Refreshing data in database
    public void UpdateInput() {

        InputDB.updateData(ID,
                    mIncome.toString(), mExpenses.toString(), mStart.toString(), mEnd.toString(), Days);

        }

// Four methods for check if there is data in database and if there is then save it in activity fields
    public void viewIncome() {

        Cursor res = InputDB.AllIncome(ID);
        if (res.getCount() == 0) {

            TextView textValue = (TextView) findViewById(R.id.incomes);
            textValue.setText(null);
            return;
        }else{

            String input = InputDB.Income(ID);
            TextView textValue = (TextView) findViewById(R.id.incomes);
            textValue.setText(input);
        }
    }
    public void viewStart() {

        Cursor res = InputDB.AllStart(ID);
        if (res.getCount() == 0) {

            TextView textValue = (TextView) findViewById(R.id.start_date);
            textValue.setText(null);
            return;
        }else{

            String start_date = InputDB.Start(ID);
            TextView textValue = (TextView) findViewById(R.id.start_date);
            textValue.setText(start_date);
        }
    }
    public void viewExpenses() {

        Cursor res = InputDB.AllExpenses(ID);
        if (res.getCount() == 0) {

            TextView textValue = (TextView) findViewById(R.id.fixed_expenses);
            textValue.setText(null);
            return;
        }else{

            String expenses = InputDB.Expenses(ID);
            TextView textValue = (TextView) findViewById(R.id.fixed_expenses);
            textValue.setText(expenses);
        }
    }
    public void viewEnd() {

        Cursor res = InputDB.AllEnd(ID);
        if (res.getCount() == 0) {

            TextView textValue = (TextView) findViewById(R.id.end_date);
            textValue.setText(null);
            return;
        }else{

            String end_date = InputDB.End(ID);
            TextView textValue = (TextView) findViewById(R.id.end_date);
            textValue.setText(end_date);
        }
    }

    // Method to show user all money that is left for selected period
    public String view_sum() {

        Cursor res = budgetDB.AllSum(ID);
        if (res.getCount() == 0) {

            String calculatedSum = "0";
            return calculatedSum;
        }else{

            String calculatedSum = budgetDB.Sum(ID);
            return calculatedSum;
        }
    }
}