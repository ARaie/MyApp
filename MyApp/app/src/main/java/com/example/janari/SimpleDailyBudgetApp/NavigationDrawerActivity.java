package com.example.janari.SimpleDailyBudgetApp;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class NavigationDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    DBHelper budgetDB;
    DatabaseHelper myDb;
    String dailySum = "", id, days, sum;
    EmailHelper emailDB;
    DataHelper daysDB;
    DataHelper InputDB;
    public static final String PREFS_NAME = "MyPrefsFile";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        budgetDB = new DBHelper(this);
        myDb = new DatabaseHelper(this);
        InputDB = new DataHelper(this);
        emailDB = new EmailHelper(this);
        daysDB = new DataHelper(this);

        // Get user email and name and save them to navigation drawer header view
        view_email();
        viewName();

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

            // TODO temporary fields. To view user ID
            viewID();

            // Shows user daily budget
            viewAll();

            // When selected period is over then system clears period data
            end();

            // This is the "-" button, that calculates daily expenses
            final Button button = (Button) findViewById(R.id.button);
            button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    // Here I take data from fields and parse them to doubles and then use the
                    // CalculateDailySumClass class to do the simple math and then display value back to Daily sum field.

                    dailySum = getIntent().getStringExtra("dailySum");

                    TextView originalBudget = (TextView) findViewById(R.id.original);
                    originalBudget.setText(dailySum);

                    String getDays = viewDays();
                    double doubleDays = Double.parseDouble(getDays);
                    String originalValue = view_sum();
                    double originalValueDouble = Double.parseDouble(originalValue);
                    TextView textValue = (TextView) findViewById(R.id.daily_sum);
                    EditText expences = (EditText) findViewById(R.id.expences);
                    String stringValue2 = expences.getText().toString();
                    double expencesValue = Double.parseDouble(stringValue2);
                    double newValue = CalculateDailySumClass.calculateSum(originalValueDouble, expencesValue, doubleDays);
                    textValue.setText(Double.toString(newValue));
                    dailySum = Double.toString(newValue);
                    double sumDouble = originalValueDouble - expencesValue;
                    sum = Double.toString(sumDouble);
                    // Adds calculated daily sum back to budget database
                    AddData();

                    TextView exp = (TextView) findViewById(R.id.exp);
                    exp.setText(stringValue2);
                    // Empty the EditText field
                    expences.setText(null);
                    // Method that updates widget view
                    updateWidget();

                }
            });

            // TODO Temporary. Calling method for get daily sum data from user budget database

            Button be = (Button) findViewById(R.id.ooo);
            be.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    //budgetDB.delete();
                    //emailDB.delete();
                    viewData();
                }
            });
            // Method that updates widget view
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
            buffer.append("Daily :" + res.getString(1) + "\n");
            buffer.append("CalculatedSum :" + res.getString(2) + "\n\n");
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
//TODO Logout is OK. Only widget holds data. And when you logout and click widget button
// TODO it opens last users budget activity. It doesn' t care that user was already logged out.
            Intent intent = new Intent(NavigationDrawerActivity.this, LoginActivity.class);
            // Empties SharedPreferences
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.remove("key");
            editor.commit();
            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(intent);
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
            // Starts the input entering activity and also gives logged in user ID
            TextView start = (TextView) findViewById(R.id.oo);
            String string = start.getText().toString();
            Intent intent = new Intent(getApplicationContext(), EnterIncomeAndExpensesActivity.class);
            intent.putExtra("id", string);
            startActivity(intent);
        } else if (id == R.id.nav_family) {
            TextView start = (TextView) findViewById(R.id.oo);
            String string = start.getText().toString();
            TextView originalBudget = (TextView) findViewById(R.id.original);
            String original = originalBudget.getText().toString();
            TextView exp = (TextView) findViewById(R.id.exp);
            String exe = exp.getText().toString();
            Intent anIntent = new Intent(getApplicationContext(), FamilyLoginActivity.class);
            Bundle extras = new Bundle();
            extras.putString("id", string);
            extras.putString("original", original);
            extras.putString("exe", exe);
            anIntent.putExtras(extras);
            startActivity(anIntent);
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

        TextView id = (TextView) findViewById(R.id.oo);
        String ID = id.getText().toString();

        Cursor res = budgetDB.budget(ID);
        if (res.getCount() == 0) {

            TextView textValue = (TextView) findViewById(R.id.daily_sum);
            textValue.setText(null);
            return;
        } else {

            double budget = budgetDB.bud(ID);
            String budgetToString = String.valueOf(budget);
            TextView textValue = (TextView) findViewById(R.id.daily_sum);
            textValue.setText(budgetToString);
        }
    }

    // Two methods for after every "-" button click add new daily sum in the budget database
    public void RefreshData() {

        TextView id = (TextView) findViewById(R.id.oo);
        String ID = id.getText().toString();
        boolean isUpdate = budgetDB.updateSum(ID,
                dailySum.toString(), sum);

    }
    public void AddData() {

        TextView id = (TextView) findViewById(R.id.oo);
        String ID = id.getText().toString();
        boolean isInserted = budgetDB.insertDaily(ID, dailySum.toString(), sum);
        RefreshData();
    }

    // Method for passing daily sum to widget view
    private void updateWidget() {

        TextView budget = (TextView) findViewById(R.id.daily_sum);

        Intent i = new Intent(this, Widget.class);
        i.setAction("yourpackage.TEXT_CHANGED");
        Toast.makeText(getApplicationContext(),budget.getText().toString()+"from the activity",
                Toast.LENGTH_SHORT).show();
        int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), Widget.class));
        i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        i.putExtra("title", budget.getText().toString());
        sendBroadcast(i);
    }

    // Takes user id from user database and show it in activity field. Based on logged in user email
    public void viewID() {

        NavigationView navigation = (NavigationView) findViewById(R.id.nav_view);
        View hView = navigation.getHeaderView(0);
        TextView setUserEmail = (TextView) hView.findViewById(R.id.user_email);
        String e = setUserEmail.getText().toString();

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

    // Takes user name from user database and show it in navigation drawer menu field. Based on logged in user email
    public void viewName() {

        NavigationView navigation = (NavigationView) findViewById(R.id.nav_view);
        View hView = navigation.getHeaderView(0);
        TextView setUserEmail = (TextView) hView.findViewById(R.id.user_email);
        String e = setUserEmail.getText().toString();

        Cursor res = myDb.AllName(e);
        if (res.getCount() == 0) {

            NavigationView navigation1 = (NavigationView) findViewById(R.id.nav_view);
            View hView1 = navigation1.getHeaderView(0);
            TextView setUserName = (TextView) hView1.findViewById(R.id.user_name);
            setUserName.setText(null);
            return;
        }else{

            String name = myDb.Name(e);
            NavigationView navigation2 = (NavigationView) findViewById(R.id.nav_view);
            View hView2 = navigation2.getHeaderView(0);
            TextView setUserName = (TextView) hView2.findViewById(R.id.user_name);
            setUserName.setText(name);
        }
    }

    // Takes user email from user database and show it in navigation drawer menu field. Based on email database.
    public void view_email() {

        Cursor res = emailDB.viewEmail();
        if (res.getCount() == 0) {

            NavigationView navigation = (NavigationView) findViewById(R.id.nav_view);
            View hView = navigation.getHeaderView(0);
            TextView setUserEmail = (TextView) hView.findViewById(R.id.user_email);
            setUserEmail.setText(null);
            return;
        }else{

            String email = emailDB.email("1");
            NavigationView navigation = (NavigationView) findViewById(R.id.nav_view);
            View hView = navigation.getHeaderView(0);
            TextView setUserEmail = (TextView) hView.findViewById(R.id.user_email);
            setUserEmail.setText(email);
        }
    }

    // Takes days between user entered start and end dates from input database. Based on user ID.
    // Method is for calculating new daily sum when new expenses are entered.
    public String viewDays() {

        TextView id = (TextView) findViewById(R.id.oo);
        String ID = id.getText().toString();

        Cursor res = daysDB.AllDays(ID);
        if (res.getCount() == 0) {

            days = "0";
            return days;
        }else{

            days = daysDB.Days(ID);
            return days;
        }
    }

    // Takes sum from input database that is calculated based on user incomes and fixed expenses. Based on user ID.
    // Method is for calculating new daily sum when new expenses are entered.
    public String view_sum() {

        TextView id = (TextView) findViewById(R.id.oo);
        String ID = id.getText().toString();

        Cursor res = budgetDB.AllSum(ID);
        if (res.getCount() == 0) {

            String calculatedSum = "0";
            return calculatedSum;
        }else{

            String calculatedSum = budgetDB.Sum(ID);
            return calculatedSum;
        }
    }
    public void end(){

        String periodEndDate = viewEnd();
        TextView textValue = (TextView) findViewById(R.id.date_today);
        String today = textValue.getText().toString();
        if (periodEndDate == today){

            budgetDB.delete();
            InputDB.delete();
        }
    }
    public String viewEnd() {

        TextView id = (TextView) findViewById(R.id.oo);
        String ID = id.getText().toString();

        Cursor res = InputDB.AllEnd(ID);
        if (res.getCount() == 0) {

            String date = "01.01.2800";
            return date;
        }else{

            String date = InputDB.End(ID);
            return date;
        }
    }
}
