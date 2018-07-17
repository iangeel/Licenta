package ro.ase.angel.licenta1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;

import ro.ase.angel.licenta1.MqttConnection.MqttHelper;
import ro.ase.angel.licenta1.Utils.Constants;

public class SettingsActivity extends AppCompatActivity {

    SeekBar widthSeekbar, heightSeekbar;
    Button btnSave, btnCalculate;
    SharedPreferences mPrefs;
    SharedPreferences.Editor mEdtior;
    TextView tvWidth, tvHeight;

    private MqttHelper mqttHelper;
    Timer timer;
    TimerTask doThis;

    double fieldLatitude, fieldLongitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.AppThemeInfo);
        }
        else setTheme(R.style.AppThemeInfo);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        componentsInitialization();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                mEdtior.putInt(Constants.FIELD_HEIGHT, Integer.parseInt(tvHeight.getText().toString()));
                mEdtior.putInt(Constants.FIELD_WIDTH, Integer.parseInt(tvWidth.getText().toString()));

                mEdtior.commit();
                btnSave.setText("Saved");
            }
        });

        widthSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int current = progress + 25;
                tvWidth.setText(String.valueOf(current));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        heightSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int current = progress + 15;
                tvHeight.setText(String.valueOf(current));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int delay = 0;
                int period = 5000;
                doThis = new TimerTask() {
                    @Override
                    public void run() {
                        mqttHelper.publishMqttFieldTopic();
                //        Toast.makeText(getApplicationContext(), "...", Toast.LENGTH_SHORT).show();
                    }
                };
                timer = new Timer();
                timer.scheduleAtFixedRate(doThis, delay , period);
            }
        });
    }

    private void componentsInitialization() {
        widthSeekbar = findViewById(R.id.seekWidthId);
        heightSeekbar = findViewById(R.id.seekHeightId);
        btnSave = findViewById(R.id.btnSave);
        btnCalculate = findViewById(R.id.btnCalculateSize);
        tvWidth = findViewById(R.id.widthValue);
        tvHeight = findViewById(R.id.heightValue);

        mPrefs = getSharedPreferences(Constants.FIELD_SETTINGS, MODE_PRIVATE);
        mEdtior = mPrefs.edit();

        widthSeekbar.setMax(42 - 25);
        heightSeekbar.setMax(25 - 15);

        String defaultHeight = String.valueOf(mPrefs.getInt(Constants.FIELD_HEIGHT, 15));
        String defaultWidth = String.valueOf(mPrefs.getInt(Constants.FIELD_WIDTH, 25));
        tvHeight.setText(defaultHeight);
        tvWidth.setText(defaultWidth);

        widthSeekbar.setProgress(Integer.parseInt((String) tvWidth.getText()) - 25);
        heightSeekbar.setProgress(Integer.parseInt((String) tvHeight.getText()) - 15);


        startMqtt();
        timer = new Timer();
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
                if(topic.equals("/tests/fieldCoordinates/latitude")) {
                    if(!(message.toString().isEmpty()) && !(message.toString().equals(null)) &&
                            (Double.parseDouble(message.toString()) > 0)) {
                        fieldLatitude = Double.parseDouble(message.toString());
                        timer.cancel();
                        Log.i("Field_LATITUDE", String.valueOf(fieldLatitude));
                        btnCalculate.setBackground(getResources().getDrawable(R.drawable.buttonshape_calculate_done));
                        btnCalculate.setText("Done");
                    }
                } else if (topic.equals("/tests/fieldCoordinates/longitude")) {
                    if(!(message.toString().isEmpty()) && !(message.toString().equals(null)) &&
                            (Double.parseDouble(message.toString()) > 0)) {
                        fieldLongitude = Double.parseDouble(message.toString());
                        timer.cancel();
                        Log.i("Field_LONGITUDE", String.valueOf(fieldLongitude));
                        btnCalculate.setBackground(getResources().getDrawable(R.drawable.buttonshape_calculate_done));
                        btnCalculate.setText("Done");
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        timer.cancel();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        double[] coordinates = new double[]{fieldLatitude, fieldLongitude};
        if(fieldLongitude != 0 && fieldLatitude != 0) {
            intent.putExtra(Constants.COORDINATES_ARRAY, coordinates);
        }
        startActivity(intent);
    }
}
