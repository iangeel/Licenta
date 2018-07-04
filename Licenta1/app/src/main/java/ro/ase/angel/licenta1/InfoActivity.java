package ro.ase.angel.licenta1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import ro.ase.angel.licenta1.Utils.Constants;

public class InfoActivity extends AppCompatActivity {


    private int lowestValue, mediumValue, highestValue;

    private TextView lowestValueTV, mediumValueTV, highestValueTV;

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
    }


    public void setValuesOfInterest() {
        int[] valuesOfInterest = getIntent().getIntArrayExtra(Constants.VALUES_OF_INTEREST);

        lowestValue = valuesOfInterest[0];
        mediumValue = valuesOfInterest[1];
        highestValue = valuesOfInterest[2];

        lowestValueTV.setText(Integer.toString(lowestValue));
        mediumValueTV.setText(Integer.toString(mediumValue));
        highestValueTV.setText(Integer.toString(highestValue));
    }
}
