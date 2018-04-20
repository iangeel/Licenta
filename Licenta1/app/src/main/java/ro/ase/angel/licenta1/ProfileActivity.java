package ro.ase.angel.licenta1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ro.ase.angel.licenta1.Database.FirebaseController;
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
                        Log.i("LoginActivity", "Selected User: " + record.toString());
                    } else {
                        Log.i("LoginActivity", "Selected User is null");
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("LoginActivity", "Data is not available");
            }
        };
    }


}
