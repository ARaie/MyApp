package com.example.janari.SimpleDailyBudgetApp;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// TODO Should be deleted activity. NavigationDrawerActivity in now my main activity.
public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Display the current date
        TextView dateView = (TextView)findViewById(R.id.date_today);
        setDate(dateView);

        // Get calculated daily sum and displays it in Daily sum field
        String value = getIntent().getStringExtra("key");
        TextView textValue = (TextView) findViewById(R.id.daily_sum);
        textValue.setText(value);

        // This is the "-" button, that calculates daily expenses
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // Here I take data from fields and parse them to doubles and then use the
                // CalculateDailySumClass class to do the simple math and then display value back to Daily sum field.
                TextView textValue = (TextView) findViewById(R.id.daily_sum);
                String stringValue = textValue.getText().toString();
                double originalValue = Double.parseDouble(stringValue);
                EditText expences = (EditText) findViewById(R.id.expences);
                String stringValue2 = expences.getText().toString();
                double expencesValue = Double.parseDouble(stringValue2);
                double newValue = CalculateDailySumClass.calculateSum(originalValue, expencesValue);
                textValue.setText(Double.toString(newValue));

                Snackbar.make(view, "Calculate your daily sum ", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
    // This method is for get current date
    public void setDate (TextView view){
        String date = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());
        view.setText(date);
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
