package com.example.janari.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static java.lang.Math.round;


public class EnterIncomeAndExpenses extends AppCompatActivity {

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_income_and_expenses);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

                double value = (incomeValue - fixedExpensesValue)/30;
                double rounded = Math.round(value);
                String calculated = String.valueOf(rounded);

                // I use Indent to send calculated value to MainActivity
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.putExtra("key", calculated);
                startActivity(intent);

        }
    });
            }

}
