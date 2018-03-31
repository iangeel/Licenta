package ro.ase.angel.licenta1;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;

import ro.ase.angel.licenta1.Database.FirebaseController;
import ro.ase.angel.licenta1.Utils.Records;
import ro.ase.angel.licenta1.Utils.SessionManagement;
import ro.ase.angel.licenta1.Utils.Users;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    private TextView tvBPM, tvSpeed, tvUser;
    private SessionManagement sessionManagement;
    private FirebaseController firebaseController;
    private String userGlobalId, username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        componentsInitialization();
        checkUserConnection();
        adaugaRecordTest();

        Toast.makeText(getApplicationContext(), FirebaseAuth.getInstance().getCurrentUser().getEmail().toString(),
                Toast.LENGTH_LONG).show();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;
                switch (item.getItemId()) {
//                    case R.id.nav_login:
//                        if(!sessionManagement.isLoggedIn()) {
//                            intent = new Intent(getApplicationContext(), LoginActivity.class);
//                            startActivity(intent);
//                        }
//                        else {
//                            Toast.makeText(getApplicationContext(), R.string.already_logged_in, Toast.LENGTH_SHORT).show();
//                        }
//                        break;
                    case R.id.nav_profile:
                        intent = new Intent(getApplicationContext(), ProfileActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_settings:
                        intent = new Intent(getApplicationContext(), SettingsActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_help:
                        intent = new Intent(getApplicationContext(), HelpActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_logout:
                        logoutMethoud();
                        break;

                }


                return true;
            }
        });
    }

    private void componentsInitialization() {
        navigationView = (NavigationView) findViewById(R.id.navId);
        mToolbar = (Toolbar) findViewById(R.id.navigation_action_id);
        setSupportActionBar(mToolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.mainDrawerLayoutId);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open,
                R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        firebaseController = FirebaseController.getInstance();

        tvBPM = (TextView) findViewById(R.id.tvPulseContor);
        tvSpeed = (TextView) findViewById(R.id.tvKmContor);
        tvUser = (TextView) findViewById(R.id.tvUserMainActivity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManagement = new SessionManagement(getApplicationContext());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mToggle.onOptionsItemSelected(item)) {
            return true;

        }


        return super.onOptionsItemSelected(item);
    }

    private void checkUserConnection() {
//        if(sessionManagement.isLoggedIn()) {
//            HashMap<String, String> userDetails = sessionManagement.getUserDetails();
//            userGlobalId = userDetails.get(sessionManagement.getKeyUserId());
//            username = userDetails.get(sessionManagement.getKeyUsername());
//
//            tvUser.setText("You are logged in as: " + username.toString());
//
//
//        }
//        else
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            username = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
            tvUser.setText("You are logged in as: " + username.toString());
        }
    }

    private void logoutMethoud() {
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
        else if(sessionManagement.isLoggedIn()) {
            FirebaseAuth.getInstance().signOut();
            sessionManagement.logoutUser();

        }
        else {
            Toast.makeText(getApplicationContext(), R.string.not_logged_in, Toast.LENGTH_SHORT).show();
        }
    }

    private void adaugaRecordTest() {

            Records records = new Records(60, 20f, 300000L, userGlobalId);
            firebaseController.addRecord(records);

    }
}
