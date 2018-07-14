package ro.ase.angel.licenta1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.RotateDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.github.mikephil.charting.charts.LineChart;
import com.google.firebase.auth.FirebaseAuth;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ro.ase.angel.licenta1.Chart.ChartHelper;
import ro.ase.angel.licenta1.Database.FirebaseController;
import ro.ase.angel.licenta1.MqttConnection.MqttHelper;
import ro.ase.angel.licenta1.Utils.Constants;
import ro.ase.angel.licenta1.Utils.Records;
import ro.ase.angel.licenta1.Utils.SessionManagement;

public class MainActivity extends AppCompatActivity   {

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    private TextView tvBPM, tvSpeed, tvUser, header_email, header_welcome;
    private SessionManagement sessionManagement;
    private FirebaseController firebaseController;
    private String userGlobalId, username;
    private ProgressBar progressBarPulse, progressBarSpeed;
    private ImageView ivRecord, ivPause, ivStop, ivDayNightTheme;
    private MqttHelper mqttHelper;
    private ScheduledExecutorService executorService;
    Timer timer;
    TimerTask doThis;
    public static List<Integer> pulseValuesRetrivedFromServer = new ArrayList<>();
    public static List<Float> speedValuesRetrivedFromServer = new ArrayList<>();
    public static List<Double> latitudeValuesRetrivedFromServer = new ArrayList<>();
    public static List<Double> longitudeValuesRetrivedFromServer = new ArrayList<>();
    ChartHelper chartHelper;
    LineChart lineChart;
    private boolean stopMqttClinet = false;

    double[] coordinatesArray;
    public static double Ax, Ay, Bx, By, Cx, Cy, Dx, Dy;
    SharedPreferences prefs;
    final double r_earth = 6378000;
    final double pi = 3.14;

    long clock = 0L;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.darktheme);
        }
        else setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        componentsInitialization();
        checkUserConnection();


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
                if(progressBarPulse != null && progressBarSpeed != null) {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        RotateDrawable rotateDrawable = (RotateDrawable) progressBarPulse.getIndeterminateDrawable();
                        rotateDrawable.setToDegrees(270);
                        RotateDrawable rotateDrawable2 = (RotateDrawable) progressBarSpeed.getIndeterminateDrawable();
                        rotateDrawable2.setToDegrees(270);

                    }

                }


                    int delay = 0;
                    int period = 5000;
                    doThis = new TimerTask() {
                        @Override
                        public void run() {
                            mqttHelper.publishMqttPulseTopic();
                            clock += 5000;
                        }
                    };
                    timer = new Timer();
                    timer.scheduleAtFixedRate(doThis, delay , period);




            }
        });

        ivPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(progressBarPulse != null && progressBarSpeed != null) {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        RotateDrawable rotateDrawable = (RotateDrawable) progressBarPulse.getIndeterminateDrawable();
                        rotateDrawable.setToDegrees(-90);
                        RotateDrawable rotateDrawable2 = (RotateDrawable) progressBarSpeed.getIndeterminateDrawable();
                        rotateDrawable2.setToDegrees(-90);
                    }
                }

                timer.cancel();


            }
        });

        ivStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(progressBarPulse != null && progressBarSpeed != null) {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        RotateDrawable rotateDrawable = (RotateDrawable) progressBarPulse.getIndeterminateDrawable();
                        rotateDrawable.setToDegrees(-90);
                        RotateDrawable rotateDrawable2 = (RotateDrawable) progressBarSpeed.getIndeterminateDrawable();
                        rotateDrawable2.setToDegrees(-90);
                    }
                }

                timer.cancel();
                addRecords();
                pulseValuesRetrivedFromServer.clear();
                speedValuesRetrivedFromServer.clear();
                latitudeValuesRetrivedFromServer.clear();
                longitudeValuesRetrivedFromServer.clear();
                tvBPM.setText("?");
                tvSpeed.setText("?");

            }
        });

        ivDayNightTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                restartApp();
            }
        });


    }

    private void componentsInitialization() {


        navigationView = (NavigationView) findViewById(R.id.navId);
        View headerLayout = navigationView.inflateHeaderView(R.layout.navigation_header);
        mToolbar = (Toolbar) findViewById(R.id.navigation_action_id);
        setSupportActionBar(mToolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.mainDrawerLayoutId);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open,
                R.string.close);
        //View headerLayout = navigationView.getHeaderView(0);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        ivDayNightTheme = headerLayout.findViewById(R.id.ivDayNightTheme);
        header_email = headerLayout.findViewById(R.id.header_email);
        header_welcome = headerLayout.findViewById(R.id.header_welcome);

        firebaseController = FirebaseController.getInstance();

        //mqtt start
        startMqtt();
        executorService = Executors.newSingleThreadScheduledExecutor();
        timer = new Timer();

        //chart initialization
        lineChart = findViewById(R.id.chart);
        chartHelper = new ChartHelper(lineChart);


        progressBarSpeed = findViewById(R.id.progressBarSpeed);
        progressBarPulse = findViewById(R.id.progressBarPulse);
        ivRecord = findViewById(R.id.ivStartRecord);
        ivPause = findViewById(R.id.ivPauseRecord);
        ivStop = findViewById(R.id.ivStopRecord);

        tvBPM = (TextView) findViewById(R.id.tvPulseContor);
        tvSpeed = (TextView) findViewById(R.id.tvKmContor);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManagement = new SessionManagement(getApplicationContext());

        if(progressBarPulse != null && progressBarSpeed != null) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                RotateDrawable rotateDrawable = (RotateDrawable) progressBarPulse.getIndeterminateDrawable();
                rotateDrawable.setToDegrees(-90);
                RotateDrawable rotateDrawable2 = (RotateDrawable) progressBarSpeed.getIndeterminateDrawable();
                rotateDrawable2.setToDegrees(-90);
            }
        }


        prefs = getPreferences(MODE_PRIVATE);

        if(getIntent().getDoubleArrayExtra(Constants.COORDINATES_ARRAY) != null) {
            coordinatesArray = getIntent().getDoubleArrayExtra(Constants.COORDINATES_ARRAY);
            Bx = coordinatesArray[1];
            By = coordinatesArray[0];

            if (Bx != 0 && By != 0) {
                calculateFieldCoordinates();
                Toast.makeText(getApplicationContext(), "A(" + Ax + ", " + Ay + ")", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "B(" + Bx + ", " + By + ")", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "C(" + Cx + ", " + Cx + ")", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "D(" + Dx + ", " + Dy + ")", Toast.LENGTH_LONG).show();
            }
        }

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
            header_welcome.setText("Welcome");
            header_email.setText(username);
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

    private void restartApp() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);

    }


    private void startMqtt() {
        mqttHelper = new MqttHelper(getApplicationContext());
        mqttHelper.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("MQTT_message",message.toString());
                if(topic.equals("/tests/confirm")) {
                    if(!(message.toString().isEmpty()) && !(message.toString().equals(null)) &&
                            (Integer.parseInt(message.toString()) > 0)) {
                        tvBPM.setText(message.toString());
                        pulseValuesRetrivedFromServer.add(Integer.parseInt(message.toString()));
                        chartHelper.addEntry(Float.valueOf(message.toString()));
                    }
                } else if(topic.equals("/tests/speed")) {
                    if(!(message.toString().isEmpty()) && !(message.toString().equals(null)) &&
                            (Float.parseFloat(message.toString()) > 0)) {
                        tvSpeed.setText(message.toString());
                        speedValuesRetrivedFromServer.add(Float.parseFloat(message.toString()));
                    }
                } else if(topic.equals("/tests/coordinates/latitude")) {
                    if(!(message.toString().isEmpty()) && !(message.toString().equals(null)) &&
                            (Double.parseDouble(message.toString()) > 0)) {
                        latitudeValuesRetrivedFromServer.add(Double.parseDouble(message.toString()));
                    }
                } else if(topic.equals("/tests/coordinates/longitude")) {
                    if(!(message.toString().isEmpty()) && !(message.toString().equals(null)) &&
                            (Double.parseDouble(message.toString()) > 0)) {
                        longitudeValuesRetrivedFromServer.add(Double.parseDouble(message.toString()));
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }


    private void addRecords() {

        if(pulseValuesRetrivedFromServer != null && !(pulseValuesRetrivedFromServer.isEmpty())) {
            Records record = new Records(pulseValuesRetrivedFromServer, speedValuesRetrivedFromServer, clock,
                    latitudeValuesRetrivedFromServer, longitudeValuesRetrivedFromServer, userGlobalId);
            firebaseController.addRecord(record);
        }
    }

    private void calculateFieldCoordinates() {

        /*
        new_latitude  = latitude  + (dy / r_earth) * (180 / pi);
        new_longitude = longitude + (dx / r_earth) * (180 / pi) / cos(latitude * pi/180);
         */

        int fieldHeight = prefs.getInt(Constants.FIELD_HEIGHT, 15);
        int fieldWidth = prefs.getInt(Constants.FIELD_WIDTH, 25);

        int fieldHeightKm = fieldHeight / 1000;
        int fieldWidthKm = fieldWidth / 1000;

        double fieldHeightDegree = (fieldHeightKm / 40000) * 360;
        double fieldWidthDegree = (fieldWidthKm / 40000) * 360;

        Ay  = By;
        //Ax = Bx + (fieldWidth / r_earth) * (180 / pi) / Math.cos(By * pi/180);
        Ax = Bx - ( fieldWidthDegree * Math.cos(2 * pi));

        Cx = Bx;
        Cy = By - (fieldHeightDegree * Math.sin(pi / 2));

        Dy = Cy;
        Dx = Ax;
    }

    private void debugging() {
        Log.d("DEBUG", "debugging");
    }
}
