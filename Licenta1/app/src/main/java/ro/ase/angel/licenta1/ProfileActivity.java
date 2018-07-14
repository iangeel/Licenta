package ro.ase.angel.licenta1;

import android.content.Intent;
import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ro.ase.angel.licenta1.Database.FirebaseController;
import ro.ase.angel.licenta1.Utils.Constants;
import ro.ase.angel.licenta1.Utils.Records;
import ro.ase.angel.licenta1.Utils.RecordsAdapter;
import ro.ase.angel.licenta1.Utils.Users;
import ro.ase.angel.licenta1.Utils.ValuesOfInterest;

public class ProfileActivity extends AppCompatActivity {

    private List<Records> recordsList = new ArrayList<>();
    private ListView lvRecords;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseController firebaseController;
    RecordsAdapter adapter;
    private int lowestValues, highestValue, mediumValue;
    private float lowestSpeedV, highestSpeedV, mediumSpeedV;
    private List<Integer> pulseList = new ArrayList<>();
    private List<Float> speedList = new ArrayList<>();

    ImageView ivNoRecords;
    ValuesOfInterest valuesOfInterest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.darktheme);
        }
        else setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        componentsInitialization();
        getRecordsListFromDatabase();


        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if(adapter.getCount() != 0) {
                    ivNoRecords.setVisibility(View.GONE);
                }
            }
        });

        lvRecords.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
                Records myRecord = (Records) parent.getItemAtPosition(position);

                //////////// PULSE VALUES ///////////////////////
                for(int pulseValue : myRecord.getPulse()) {
                    pulseList.add(pulseValue);
                }
                calculatePulseValuesOfInterest();



                //////////// SPEED VALUES //////////////////////
                if(myRecord.getSpeed() != null && myRecord.getSpeed().size() > 0) {
                    for (float speedValue : myRecord.getSpeed()) {
                        speedList.add(speedValue);
                    }
                    calculateSpeedValuesOfInterest();
                } else {
                    lowestSpeedV = 99.9f;
                    mediumSpeedV = 99.9f;
                    highestSpeedV = 99.9f;
                }

                int[] pulseValueOfInterest = new int[] {lowestValues, mediumValue, highestValue};
                float[] speedValuesOfInterest = new float[] {lowestSpeedV, mediumSpeedV, highestSpeedV};

                valuesOfInterest = new ValuesOfInterest(pulseValueOfInterest, speedValuesOfInterest,
                        myRecord.getLatitudes(), myRecord.getLongitudes());

                intent.putExtra(Constants.VALUES_OF_INTEREST, valuesOfInterest);
                startActivity(intent);
            }
        });
    }



    public void componentsInitialization() {
        mAuth = FirebaseAuth.getInstance();
        firebaseController = FirebaseController.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        lvRecords = (ListView) findViewById(R.id.lvRecords);

        adapter = new RecordsAdapter(getApplicationContext(), R.layout.profile_list_layout,
                recordsList, getLayoutInflater());

        lvRecords.setAdapter(adapter);

        ivNoRecords = findViewById(R.id.ivNoRecords);



    }

    public void getRecordsListFromDatabase() {
        FirebaseUser user = mAuth.getCurrentUser();

        firebaseController.findAllRecords(uploadRecordsFromDatabaseGlobal(), user.getUid());

        adapter.notifyDataSetChanged();

    }

    private ValueEventListener uploadRecordsFromDatabaseGlobal() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Records record = data.getValue(Records.class);
                    if (record != null) {
                        recordsList.add(record);
                        Log.i("ProfileActivity", "Selected record: " + record.toString());
                    } else {
                        Log.i("ProfileActivity", "Selected record is null");
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("ProfileActivity", "Data is not available");
            }
        };
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    private void calculatePulseValuesOfInterest() {
        Collections.sort(pulseList);

        for(int pulseV : pulseList) {
            if(pulseV > 50) {
                lowestValues = pulseV;
                break;
            }
        }

        highestValue = pulseList.get(pulseList.size() - 1);

        int sum = 0;
        int underFifty = 0;
        for(int pulseV : pulseList) {
            if(pulseV > 50) {
                sum += pulseV;
            } else underFifty++;
        }
        mediumValue = sum / (pulseList.size() - underFifty);
    }

    private void calculateSpeedValuesOfInterest() {
        Collections.sort(speedList);

        for(float speedV : speedList) {
            lowestSpeedV = speedV;
            break;
        }

        highestSpeedV = speedList.get(speedList.size() - 1);

        int sum = 0;
        for(float speedV : speedList) {
            sum += speedV;
        }

        mediumSpeedV = sum / speedList.size();
    }

}
