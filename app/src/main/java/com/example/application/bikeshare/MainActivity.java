package com.example.application.bikeshare;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private TextView side_Nav_logged_in_User;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavigationView navigationView = findViewById(R.id.nvDrawer);
        View headerView = navigationView.getHeaderView(0);
        side_Nav_logged_in_User = headerView.findViewById(R.id.sideNavLoggedInUser);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String sideNavUser = sharedPreferences.getString("student_id", "0");

        side_Nav_logged_in_User.setText(sideNavUser);




        /*
        //receive data from login activity
        Bundle bundle = getIntent().getExtras();
        String student_id = bundle.getString("student_id");
        String first_name = bundle.getString("first_name");

        //pack data into bundle
        Bundle bundle1 = new Bundle();
        bundle1.putString("student_id", student_id);
        bundle1.putString("first_name",first_name);

        //send data to dashboard fragment
        Dashboard dashboard_obj = new Dashboard();
        dashboard_obj.setArguments(bundle1);
*/
        //launch the dashboard fragment
        Dashboard fragment = new Dashboard();
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.flcontent, fragment);
        transaction.commit();

        mDrawerLayout = findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        NavigationView nvDrawer = findViewById(R.id.nvDrawer);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupDrawerContent(nvDrawer);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void selectItemDrawer(MenuItem menuItem) {
        Fragment myFragment = null;
        Class fragmentClass;

        switch (menuItem.getItemId()) {
            case R.id.db:
                fragmentClass = Dashboard.class;
                break;

            case R.id.bikes:
                fragmentClass = Bikes.class;
                break;

            case R.id.my_account:
                fragmentClass = Myaccount.class;
                break;

            case R.id.contact:
                fragmentClass = Contact.class;
                break;

            case R.id.logout:
                fragmentClass = Logout.class;
                break;

            default:
                fragmentClass = Dashboard.class;

        }

        try {
            myFragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flcontent, myFragment).commit();
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());

        mDrawerLayout.closeDrawers();
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectItemDrawer(item);
                return true;
            }
        });
    }
}
