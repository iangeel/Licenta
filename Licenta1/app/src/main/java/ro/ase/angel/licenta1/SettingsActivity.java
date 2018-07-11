package ro.ase.angel.licenta1;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import ro.ase.angel.licenta1.Utils.Constants;

public class SettingsActivity extends AppCompatActivity {

    SeekBar widthSeekbar, heightSeekbar;
    Button btnSave;
    SharedPreferences mPrefs;
    SharedPreferences.Editor mEdtior;
    TextView tvWidth, tvHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.darktheme);
        }
        else setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        componentsInitialization();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                mEdtior.putInt(Constants.FIELD_HEIGHT, Integer.parseInt(tvHeight.getText().toString()));
                Toast.makeText(getApplicationContext(), tvHeight.getText().toString(), Toast.LENGTH_SHORT).show();
                mEdtior.putInt(Constants.FIELD_WIDTH, Integer.parseInt(tvWidth.getText().toString()));

                mEdtior.commit();
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
    }

    private void componentsInitialization() {
        widthSeekbar = findViewById(R.id.seekWidthId);
        heightSeekbar = findViewById(R.id.seekHeightId);
        btnSave = findViewById(R.id.btnSave);
        tvWidth = findViewById(R.id.widthValue);
        tvHeight = findViewById(R.id.heightValue);
        mPrefs = getPreferences(MODE_PRIVATE);
        mEdtior = mPrefs.edit();

        widthSeekbar.setMax(42 - 25);
        heightSeekbar.setMax(25 - 15);

        String defaultHeight = String.valueOf(mPrefs.getInt(Constants.FIELD_HEIGHT, 15));
        String defaultWidth = String.valueOf(mPrefs.getInt(Constants.FIELD_WIDTH, 25));
        tvHeight.setText(defaultHeight);
        tvWidth.setText(defaultWidth);

        widthSeekbar.setProgress(Integer.parseInt((String) tvWidth.getText()) - 25);
        heightSeekbar.setProgress(Integer.parseInt((String) tvHeight.getText()) - 15);


    }
}
