package com.example.janari.SimpleDailyBudgetApp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class NavigationDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    DBHelper budgetDB;
    DatabaseHelper myDb;
    String dailySum = "", email, id, Name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        budgetDB = new DBHelper(this);
        myDb = new DatabaseHelper(this);


        // TODO Method for add yesturdays left sum when time is 00:00:00. need little bit more thinking...
        // yesturdaysLeft ();

        // TODO ta ei saa enam kätte emaili kui ma olen sisse logitud. Nimi ka nüüd puudu
        // Get user email and name and save them to navigation drawer header view
        NavigationView navigation = (NavigationView) findViewById(R.id.nav_view);
        View hView = navigation.getHeaderView(0);
        email = getIntent().getStringExtra("userEmail");
        TextView setUserEmail = (TextView) hView.findViewById(R.id.user_email);
        setUserEmail.setText(email);
        TextView ee = (TextView) findViewById(R.id.e);
        ee.setText(email);


            // Navigation drawer code
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            navigationView.setCheckedItem(R.id.nav_main);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            // Main activity code
            // Display the current date
            TextView dateView = (TextView) findViewById(R.id.date_today);
            setDate(dateView);

            // TODO see koodike ei lase teda andmete sisetusest tagasi ja muidu on kenasti igal kärsal om andmebaas ja uue kasutajaga on ka timmu
        //TODO Nüüd viimase meetodiga kokku ei jookse enam. vaheta andmete pärimise meetodid ära. Tagasi minnes ta ainult kaotab ära oma IDd ja email'id

            viewID();

            // Shows user daily budget
            //viewAll();

            // This is the "-" button, that calculates daily expenses
            final Button button = (Button) findViewById(R.id.button);
            button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    // Here I take data from fields and parse them to doubles and then use the
                    // CalculateDailySumClass class to do the simple math and then display value back to Daily sum field.
                    dailySum = getIntent().getStringExtra("dailySum");
                    TextView textValue = (TextView) findViewById(R.id.daily_sum);
                    String stringValue = textValue.getText().toString();
                    double originalValue = Double.parseDouble(stringValue);
                    EditText expences = (EditText) findViewById(R.id.expences);
                    String stringValue2 = expences.getText().toString();
                    double expencesValue = Double.parseDouble(stringValue2);
                    double newValue = CalculateDailySumClass.calculateSum(originalValue, expencesValue);
                    textValue.setText(Double.toString(newValue));
                    dailySum = Double.toString(newValue);
                    AddData();
                    expences.setText(null);


                    // TODO not very useful code I think
                    Snackbar.make(view, "Calculate your daily sum ", Snackbar.LENGTH_LONG)
                            .setAction("Actaion", null).show();
                }
            });

            // Calling method for get daily sum data from user budget database and show it to main page

            Button be = (Button) findViewById(R.id.ooo);
            be.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    //budgetDB.delete();
                    viewData();
                }
            });
            updateWidget();
        }

    //TODO Temporary. For checking budget database
    public void viewData() {

        Cursor res = budgetDB.getAllData();
        if (res.getCount() == 0) {
            // show message
            showMessage("Error", "Nothing found");
            return;
        }

        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()) {
            buffer.append("Id :" + res.getString(0) + "\n");
            buffer.append("Name :" + res.getString(1) + "\n");
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
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

            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        if (id == R.id.action_logout) {

            Intent intent = new Intent(NavigationDrawerActivity.this, LoginActivity.class);
            TextView start = (TextView) findViewById(R.id.oo);
            start.setText(null);
            SharedPreferences preferences = getSharedPreferences("key", StartActivity.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.commit();
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // TODO Navigation drawer menu items. The Share part is not connected to right pages yet.
    @SuppressWarnings("StatementWithEmptyBody")
    @Override

    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Bundle bundle = new Bundle();

        if (id == R.id.nav_main) {
            Intent anIntent = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
            startActivity(anIntent);
        } else if (id == R.id.nav_data) {
            TextView start = (TextView) findViewById(R.id.oo);
            String string = start.getText().toString();
            Intent intent = new Intent(getApplicationContext(), EnterIncomeAndExpensesActivity.class);
            intent.putExtra("id", string);
            startActivity(intent);
        } else if (id == R.id.nav_fb) {
            Intent anIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(anIntent);
        } else if (id == R.id.nav_send) {
            Intent anIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(anIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // This method is for get current date
    public void setDate(TextView view) {
        String date = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());
        view.setText(date);
    }

    // Method for get data from user budget database
    public void viewAll() {

        Cursor res = budgetDB.budget(id);
        if (res.getCount() == 0) {

            TextView textValue = (TextView) findViewById(R.id.daily_sum);
            textValue.setText(null);
            return;
        } else {

            long budget = budgetDB.bud(id);
            String budgetToString = String.valueOf(budget);
            TextView textValue = (TextView) findViewById(R.id.daily_sum);
            textValue.setText(budgetToString);
        }
    }

    // TODO Add left money. Needs thinking
    public void yesturdaysLeft() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String strDate = sdf.format(c.getTime());
        if (strDate == "00:00:00") {
            Cursor res = budgetDB.getAllData();
            if (res.getCount() == 0) {
                TextView value = (TextView) findViewById(R.id.daily_sum);
                value.setText(null);
                return;
            }
            String value = "";
            while (res.moveToNext()) {
                value = res.getString(1);
            }
            TextView textValue = (TextView) findViewById(R.id.daily_sum);
            String stringValue = textValue.getText().toString();
            double originalValue = Double.parseDouble(stringValue);
            originalValue = originalValue + Double.parseDouble(value);
            textValue.setText(Double.toString(originalValue));
        }
    }

    // Two methods for after every "-" button click add new daily sum in the budget database
    public void RefreshData() {

        boolean isUpdate = budgetDB.updateSum(id,
                dailySum.toString());

    }

    public void AddData() {

        boolean isInserted = budgetDB.insertDaily(id, dailySum.toString());
        RefreshData();
    }

    private void updateWidget() {

        TextView textValue = (TextView) findViewById(R.id.daily_sum);
        String stringValue = textValue.getText().toString();
        if(stringValue == ""){
            sendBroadcast(new Intent(Widget.ACTION_UPDATE).putExtra("budget", "0"));
        }else{
            sendBroadcast(new Intent(Widget.ACTION_UPDATE).putExtra("budget", stringValue));
        }
    }

    public void viewID() {

        TextView ee = (TextView) findViewById(R.id.e);
        String e = ee.getText().toString();

        Cursor res = myDb.AllID(e);
        if (res.getCount() == 0) {

            TextView textValue = (TextView) findViewById(R.id.oo);
            textValue.setText(null);
            return;
        }else{

            long idee = myDb.ID(e);
            String stringID = String.valueOf(idee);
            TextView textValue = (TextView) findViewById(R.id.oo);
            textValue.setText(stringID);
        }
    }

}
