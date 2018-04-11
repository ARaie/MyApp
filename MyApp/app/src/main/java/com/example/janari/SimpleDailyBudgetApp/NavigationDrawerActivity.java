package com.example.janari.SimpleDailyBudgetApp;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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
import com.example.janari.SimpleDailyBudgetApp.Models.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NavigationDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    DBHelper budgetDB;
    DatabaseHelper myDb;
    DataHelper daysDB;
    DataHelper InputDB;
    ExpenseHelper expenseDB;
    String dailySum = "", id, sum, familys;
    EmailHelper emailDB;
    private TextView familyBudget, time;
    private DatabaseReference mDatabase;
    private DatabaseReference mMessageReference;
    private FirebaseAuth mAuth;
    double Family, sumExpenses, Fam, expencesValue, doubleDays;
    boolean flag = false;
    public static final String PREFS_NAME = "MyPrefsFile";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Locale locale = Locale.US;
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        this. setContentView(R.layout.activity_navigation_drawer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Local databases
        budgetDB = new DBHelper(this);
        myDb = new DatabaseHelper(this);
        InputDB = new DataHelper(this);
        emailDB = new EmailHelper(this);
        daysDB = new DataHelper(this);
        expenseDB = new ExpenseHelper(this);

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


        mDatabase = FirebaseDatabase.getInstance().getReference();
        mMessageReference = FirebaseDatabase.getInstance().getReference("user");
        mAuth = FirebaseAuth.getInstance();

        familyBudget = (TextView) findViewById(R.id.original);
        time = (TextView) findViewById(R.id.exp);

        if (mAuth.getCurrentUser() != null) {
            // Get data from Firebase DB
            mDatabase.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        familyBudget.setText(dataSnapshot.child("userBudget").getValue().toString());
                        time.setText(dataSnapshot.child("time").getValue().toString());
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        // Save user ID
            viewID();

        // View all user money that is left for selected period
        String all = view_sum();
        double leftSum = Double.parseDouble(all);
        TextView allSum = (TextView) findViewById(R.id.all);
        allSum.setText( String.format( "%.2f", leftSum) );


        // Shows user daily budget
            String dailyBudget =  viewAll();
            if (dailyBudget.matches("Enter your data first")){
                TextView setUserName = (TextView) findViewById(R.id.daily_sum);
                setUserName.setText("Enter your data first");
                TextView start = (TextView) findViewById(R.id.oo);
                String string = start.getText().toString();
                Intent intent = new Intent(getApplicationContext(), EnterIncomeAndExpensesActivity.class);
                intent.putExtra("id", string);
                startActivity(intent);
                finish();
            }else{
                double budget = Double.parseDouble(dailyBudget);
                TextView setUserName = (TextView) findViewById(R.id.daily_sum);
                setUserName.setText( String.format( "%.2f", budget) );
            }

            // When selected period is over then system clears period data
            end();

            // This is the "-" button, that calculates daily expenses
            final Button button = (Button) findViewById(R.id.button);
            button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    // Here I take data from fields and parse them to doubles and then use the
                    // CalculateDailySumClass class to do the simple math and then display value back to Daily sum field.
                    EditText expences = (EditText) findViewById(R.id.expences);
                    String stringValue2 = expences.getText().toString();
                    EditText notes = (EditText) findViewById(R.id.note);

                    if (stringValue2.matches("")) {

                        Toast.makeText(getApplicationContext(), "Enter expenses", Toast.LENGTH_LONG).show();
                    }else{
                        dailySum = getIntent().getStringExtra("dailySum");

                        doubleDays  = viewDays();
                        periodDays();
                        String roundedDays = String.format( "%.0f", doubleDays);
                        double day = Double.parseDouble(roundedDays);
                        String originalValue = view_sum();
                        double originalValueDouble = Double.parseDouble(originalValue);
                        TextView textValue = (TextView) findViewById(R.id.daily_sum);
                        String check = textValue.getText().toString();
                        if (check.matches("Enter your data first")){
                            Toast.makeText(getApplicationContext(), "Enter your calculation period first", Toast.LENGTH_LONG).show();
                        }else if(check.matches("Period is over")){

                            Toast.makeText(getApplicationContext(), "Make new calculation period", Toast.LENGTH_LONG).show();
                        }else{
                            expencesValue = Double.parseDouble(stringValue2);
                            double newValue = CalculateDailySumClass.calculateSum(originalValueDouble, expencesValue, day);
                            //textValue.setText(Double.toString(newValue));
                            textValue.setText( String.format( "%.2f", newValue) );
                            dailySum = Double.toString(newValue);
                            double sumDouble = originalValueDouble - expencesValue;
                            sum = Double.toString(sumDouble);
                            // Adds calculated daily sum back to budget database
                            AddData();

                            AddExpenses();
                            // Empty the EditText field
                            expences.setText(null);
                            notes.setText(null);
                            // Method that updates widget view
                            updateWidget();



                            // Family budget refreshing
                            if (mAuth.getCurrentUser() != null) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                String userId = user.getUid();

                                if (check.matches("Period is over")) {

                                Family = Double.parseDouble(familyBudget.getText().toString());
                                double fam = Family + 0.00;
                                familys = String.format(Locale.US, "%.2f", fam);
                                String time = "Family member period is over";
                                Message message = new Message(familys, time);
                                mMessageReference.child(userId).setValue(message);
                            }else{
                                calculateExpenses();
                                String expenses = String.format(Locale.US, "%.2f", sumExpenses);
                                String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
                                Message message = new Message(expenses, time);
                                mMessageReference.child(userId).setValue(message);
                            }
                            }
                        }
                    }

                    String all = view_sum();
                    double leftSum = Double.parseDouble(all);
                    TextView allSum = (TextView) findViewById(R.id.all);
                    allSum.setText( String.format( "%.2f", leftSum) );
                }
            });

            // Button to show expenses
            Button be = (Button) findViewById(R.id.showExpenses);
            be.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    viewData();
                }
            });

            // Method that updates widget view
            updateWidget();
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
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Navigation drawer menu selection
    @SuppressWarnings("StatementWithEmptyBody")
    @Override

    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Bundle bundle = new Bundle();

        if (id == R.id.nav_main) {
            Intent anIntent = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
            startActivity(anIntent);
            finish();
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
            Intent anIntent = new Intent(getApplicationContext(), FamilyLoginActivity.class);
            Bundle extras = new Bundle();
            extras.putString("id", string);
            anIntent.putExtras(extras);
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
    public String viewAll() {

        TextView id = (TextView) findViewById(R.id.oo);
        String ID = id.getText().toString();

        Cursor res = budgetDB.budget(ID);
        if (res.getCount() == 0) {

            String calculatedSum = "Enter your data first";
            return calculatedSum;
        } else {

            String budgetToString = budgetDB.bud(ID);
            return budgetToString;
        }
    }

    // Two methods for after every "-" button click add new daily sum in the budget database
    public void RefreshData() {

        TextView id = (TextView) findViewById(R.id.oo);
        String ID = id.getText().toString();
        budgetDB.updateSum(ID,
                dailySum.toString(), sum);

    }
    public void AddData() {

        TextView id = (TextView) findViewById(R.id.oo);
        String ID = id.getText().toString();
        budgetDB.insertDaily(ID, dailySum.toString(), sum);
        RefreshData();
    }

    // Method for passing daily sum to widget view
    private void updateWidget() {

        TextView budget = (TextView) findViewById(R.id.daily_sum);

        Intent i = new Intent(this, Widget.class);
        i.setAction("yourpackage.TEXT_CHANGED");
        //Toast.makeText(getApplicationContext(),budget.getText().toString()+"from the activity",
               // Toast.LENGTH_SHORT).show();
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

    // Takes user username from user database and show it in navigation drawer menu field. Based on email database.
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
    public double viewDays() {

        TextView id = (TextView) findViewById(R.id.oo);
        String ID = id.getText().toString();

        Cursor res = InputDB.AllEnd(ID);
        if (res.getCount() == 0) {

            double days = 0;
            return days;
        }else{

            String today = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());
            String end = InputDB.End(ID);
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            Date Today = null;
            Date End = null;
            try {
                Today = sdf.parse(today);
                End = sdf.parse(end);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return (double) (End.getTime() - Today.getTime()) / (24 * 60 * 60 * 1000) + 1;
        }
    }
    public void periodDays() {

        String periodStartDate = viewStart();
        String today = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date Today = null;
        Date Tomorrow = null;
        try {
            Today = sdf.parse(today);
            Tomorrow = sdf.parse(periodStartDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        double startDate = (Tomorrow.getTime() - Today.getTime()) / (24 * 60 * 60 * 1000);
        if (startDate >= 0) {

            String startD = viewStart();
            String endD= viewEnd();
            Date start = null;
            Date end = null;
            try {
                start = sdf.parse(startD);
                end = sdf.parse(endD);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            doubleDays = (end.getTime() - start.getTime()) / (24 * 60 * 60 * 1000) + 1;
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

    // Method to handle the end of user selected period
    public void end(){

        String periodEndDate = viewEnd();
        String today = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date Today = null;
        Date Tomorrow = null;
        try {
            Today = sdf.parse(today);
            Tomorrow = sdf.parse(periodEndDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        double endDate = (Tomorrow.getTime() - Today.getTime()) / (24 * 60 * 60 * 1000);
        if (endDate < 0) {

            // TODO databases could be emptied to start new period
            //budgetDB.delete();
            //InputDB.delete();
            TextView over = (TextView) findViewById(R.id.daily_sum);
            over.setText("Period is over");
            expenseDB.delete();
        }
    }

    // Method to get period end date from local database
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
    // Method to get period end date from local database
    public String viewStart() {

        TextView id = (TextView) findViewById(R.id.oo);
        String ID = id.getText().toString();

        Cursor res = InputDB.AllStart(ID);
        if (res.getCount() == 0) {

            String date = "01.01.2800";
            return date;
        }else{

            String date = InputDB.Start(ID);
            return date;
        }
    }
    public void calculateExpenses() {

        String family = familyBudget.getText().toString();
        if (family.matches("")) {
            sumExpenses = 0;
        } else {
            Fam = Double.parseDouble(family);
            sumExpenses = Fam - expencesValue;
        }
    }
    // Add and update data for user entered period, income and expenses
    public  void AddExpenses() {

        EditText exps = (EditText) findViewById(R.id.expences);
        String expenses = exps.getText().toString();
        EditText notes = (EditText) findViewById(R.id.note);
        String note = notes.getText().toString();

        expenseDB.insertExpenses(expenses, note);
        //UpdateExpenses();

    }
    // Refreshing data in database
    public void UpdateExpenses() {

        TextView id = (TextView) findViewById(R.id.oo);
        String ID = id.getText().toString();
        EditText exps = (EditText) findViewById(R.id.expences);
        String expenses = exps.getText().toString();
        EditText notes = (EditText) findViewById(R.id.note);
        String note = notes.getText().toString();

        expenseDB.updateExpenses(ID,
                expenses, note);

    }

    public void viewData() {

        Cursor res = expenseDB.getAllData();
        if (res.getCount() == 0) {
            // show message
            showMessage("Error", "Nothing found");
            return;
        }

        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()) {
            buffer.append("Sum :" + res.getString(1) + "\n");
            buffer.append("Note :" + res.getString(2) + "\n");
        }

        // Show all data
        showMessage("Expenses", buffer.toString());
    }

    public void showMessage(String title, String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }
}
