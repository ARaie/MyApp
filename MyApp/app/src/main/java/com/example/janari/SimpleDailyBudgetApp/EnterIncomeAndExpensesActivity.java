
// I can't change root module name, I am looking for a way to change MyApp name
// Same thing is with package name. The name is such because I created the project on Janari's computer.

package com.example.janari.SimpleDailyBudgetApp;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.content.Intent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class EnterIncomeAndExpensesActivity extends AppCompatActivity {

    DatePickerDialog datePickerDialog;
    EditText startDate;
    EditText endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_income_and_expenses);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // TODO majority of picking date code should move to CalendarAcrivity class
        final EditText startDate = (EditText)findViewById(R.id.start_date);
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

        final EditText endDate = (EditText)findViewById(R.id.end_date);
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
                String stringEnd= end.getText().toString();

                // TODO I don't know if it is better when period 1.11-30.11 makes 30days or period 1.11-1.12. (How to count days.)
                double value = (incomeValue - fixedExpensesValue) /Daybetween(stringStart, stringEnd, "dd.MM.yyyy");
                double rounded = Math.round(value);
                String calculated = String.valueOf(rounded);

                // I use Indent to send calculated value to MainActivity
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.putExtra("key", calculated);
                startActivity(intent);

            }
        });

    }
    public double Daybetween(String date1,String date2,String pattern)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date Date1 = null,Date2 = null;
        try {
            Date1 = sdf.parse(date1);
            Date2 = sdf.parse(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (double) (Date2.getTime() - Date1.getTime())/(24*60*60*1000);
    }
}