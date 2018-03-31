package ro.ase.angel.licenta1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ro.ase.angel.licenta1.Utils.Records;
import ro.ase.angel.licenta1.Utils.RecordsAdapter;

public class ProfileActivity extends AppCompatActivity {

    List<Records> recordsList = new ArrayList<>();
    ListView lvRecords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        lvRecords = (ListView) findViewById(R.id.lvRecords);

        RecordsAdapter adapter = new RecordsAdapter(getApplicationContext(), R.layout.profile_list_layout,
                recordsList, getLayoutInflater());

        lvRecords.setAdapter(adapter);
    }


}
