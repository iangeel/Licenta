package ro.ase.angel.licenta1;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
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
    private ProgressBar progressBarPulse, progressBarSpeed;
    private int progressStatus = 0;
    private Handler handler = new Handler();
    private ImageView ivRecord, ivPause, ivStop;
    private boolean isPressed = false;


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

        ivRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressStatus = 0;
                isPressed = true;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressBarPulse.setProgress(progressStatus);

                                }
                            });
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            progressStatus++;
                        }
                    }
                }).start();


            }
        });

        ivPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPressed = false;

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


        progressBarPulse = findViewById(R.id.progressBarPulse);
        ivRecord = findViewById(R.id.ivStartRecord);
        ivPause = findViewById(R.id.ivPauseRecord);
        ivStop = findViewById(R.id.ivStopRecord);

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
            LoginManager.getInstance().logOut();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
        else if(sessionManagement.isLoggedIn()) {
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            sessionManagement.logoutUser();

        }
        else {
            Toast.makeText(getApplicationContext(), R.string.not_logged_in, Toast.LENGTH_SHORT).show();
        }
    }

    private void adaugaRecordTest() {

            Records records = new Records(80, 30f, 500000L, userGlobalId);
            firebaseController.addRecord(records);

    }
}
