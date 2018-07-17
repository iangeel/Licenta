package ro.ase.angel.licenta1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ro.ase.angel.licenta1.Utils.Constants;
import ro.ase.angel.licenta1.Utils.ValuesOfInterest;

public class InfoActivity extends AppCompatActivity {


    private int lowestValue, mediumValue, highestValue;
    private float lowestSpeedV, highestSpeedV, mediumSpeedV;
    private ValuesOfInterest valuesOfInterest;

    private TextView lowestValueTV, mediumValueTV, highestValueTV, lowestSpeedTv, mediumSpeedTv, highestSpeedTv;
    private Button btnMap;
    private double[] myCoordinates;

    private SharedPreferences mPrefs;
    private ImageView field;
    private TextView fieldText;
    final double pi = 3.14159265359;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.AppThemeInfo);
        }
        else setTheme(R.style.AppThemeInfo);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        componentsInitialization();
        setValuesOfInterest();
        getCoordinates();

        setFieldLocation();

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra(Constants.MAP_ARRAY, myCoordinates);
                startActivity(intent);
            }
        });
    }

    public void componentsInitialization() {

        lowestValueTV = findViewById(R.id.lowestValueTV);
        mediumValueTV = findViewById(R.id.mediumValueTV);
        highestValueTV = findViewById(R.id.highestValuesTV);

        lowestSpeedTv = findViewById(R.id.lowestSpeedValue);
        mediumSpeedTv = findViewById(R.id.mediumSpeedValue);
        highestSpeedTv = findViewById(R.id.highestSpeedValue);

        btnMap = findViewById(R.id.btnMap);

        mPrefs = getSharedPreferences(Constants.COORDINATES_PREFFS, MODE_PRIVATE);


        field = findViewById(R.id.footbalCourt);
        fieldText = findViewById(R.id.tvPlayerType);

    }


    public void setValuesOfInterest() {
        valuesOfInterest = getIntent().getParcelableExtra(Constants.VALUES_OF_INTEREST);

        int[] pulseValueOfInterest = valuesOfInterest.getPulseValuesOfInterest();
        float[] speedValueOfInterest = valuesOfInterest.getSpeedValuesOfInterest();

        lowestValue = pulseValueOfInterest[0];
        mediumValue = pulseValueOfInterest[1];
        highestValue = pulseValueOfInterest[2];

        lowestSpeedV = speedValueOfInterest[0];
        mediumSpeedV = speedValueOfInterest[1];
        highestSpeedV = speedValueOfInterest[2];


        lowestValueTV.setText(Integer.toString(lowestValue));
        mediumValueTV.setText(Integer.toString(mediumValue));
        highestValueTV.setText(Integer.toString(highestValue));

        lowestSpeedTv.setText(Float.toString(lowestSpeedV));
        mediumSpeedTv.setText(Float.toString(mediumSpeedV));
        highestSpeedTv.setText(Float.toString(highestSpeedV));
    }

    private void getCoordinates() {
        if(valuesOfInterest.getLatitudes() != null && !(valuesOfInterest.getLatitudes().isEmpty()) &&
                valuesOfInterest.getLongitudes() != null && !(valuesOfInterest.getLongitudes().isEmpty())) {

            int size = valuesOfInterest.getLatitudes().size() + valuesOfInterest.getLongitudes().size();
            myCoordinates = new double[size];

            int k = 0;
            int z = 0;

            for (int i = 0; i < size; i++) {
                if (i % 2 == 0) {
                    myCoordinates[i] = valuesOfInterest.getLatitudes().get(k);
                    k++;
                } else {
                    myCoordinates[i] = valuesOfInterest.getLongitudes().get(z);
                    z++;
                }
            }
        }
    }

    private void setFieldLocation() {
        double Ax = Double.parseDouble(mPrefs.getString(Constants.FIELD_AX, "0"));
        double Ay = Double.parseDouble(mPrefs.getString(Constants.FIELD_AY, "0"));
        double Bx = Double.parseDouble(mPrefs.getString(Constants.FIELD_BX, "0"));
        double By = Double.parseDouble(mPrefs.getString(Constants.FIELD_BY,"0"));
        double Cx = Double.parseDouble(mPrefs.getString(Constants.FIELD_CX, "0"));
        double Cy = Double.parseDouble(mPrefs.getString(Constants.FIELD_CY,"0"));
        double Dx = Double.parseDouble(mPrefs.getString(Constants.FIELD_DX, "0"));
        double Dy = Double.parseDouble(mPrefs.getString(Constants.FIELD_DY,"0"));

        Log.i("COORDS", String.valueOf(Ax));


        if(Ax != 0 && Ay != 0 &&
                Bx != 0 && By != 0 &&
                Cx != 0 && Cy != 0 &&
                Dx != 0 && Dy != 0) {

            SharedPreferences prefs = getSharedPreferences(Constants.FIELD_SETTINGS, MODE_PRIVATE);
            double fieldWidth = prefs.getInt(Constants.FIELD_WIDTH, 25);

            double fieldWidthKm = fieldWidth / 1000;
            double fieldWidthDegree = (fieldWidthKm / 40000) * 360;

            Log.i("COORDS", "OK");
            double halfY = Ay - (fieldWidthDegree * Math.sin(pi / 2));
            double latsSum = 0;

            Log.i("COORDS", String.valueOf(halfY));

            for(double lats : valuesOfInterest.getLatitudes()) {
                latsSum += lats;
            }

            double latsMean = latsSum / valuesOfInterest.getLatitudes().size();

            if(latsMean > halfY) {
                field.setImageResource(R.drawable.football_field_offensive);
                fieldText.setText("You have played most of the time in offensive.");
            } else {
                field.setImageResource(R.drawable.football_field_defensive);
                fieldText.setText("You have played most of the time in defensive.");
            }

        } else {
            field.setImageResource(R.drawable.football_field);

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(intent);
    }
}
