package ro.ase.angel.licenta1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import ro.ase.angel.licenta1.Utils.Constants;
import ro.ase.angel.licenta1.Utils.ValuesOfInterest;

public class InfoActivity extends AppCompatActivity {


    private int lowestValue, mediumValue, highestValue;
    private float lowestSpeedV, highestSpeedV, mediumSpeedV;
    private ValuesOfInterest valuesOfInterest;

    private TextView lowestValueTV, mediumValueTV, highestValueTV, lowestSpeedTv, mediumSpeedTv, highestSpeedTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        componentsInitialization();
        setValuesOfInterest();
    }

    public void componentsInitialization() {

        lowestValueTV = findViewById(R.id.lowestValueTV);
        mediumValueTV = findViewById(R.id.mediumValueTV);
        highestValueTV = findViewById(R.id.highestValuesTV);

        lowestSpeedTv = findViewById(R.id.lowestSpeedValue);
        mediumSpeedTv = findViewById(R.id.mediumSpeedValue);
        highestSpeedTv = findViewById(R.id.highestSpeedValue);

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(intent);
    }
}
