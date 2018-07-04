package ro.ase.angel.licenta1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

public class ProfileActivity extends AppCompatActivity {

    private List<Records> recordsList = new ArrayList<>();
    private ListView lvRecords;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseController firebaseController;
    RecordsAdapter adapter;
    private int lowestValues, highestValue, mediumValue;
    private List<Integer> pulseList = new ArrayList<>();


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

        lvRecords.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
                Records myRecord = (Records) parent.getItemAtPosition(position);

                for(int pulseValue : myRecord.getPulse()) {
                    pulseList.add(pulseValue);
                }

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

                int[] valuesOfInterest = new int[] {lowestValues, mediumValue, highestValue};

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


}
