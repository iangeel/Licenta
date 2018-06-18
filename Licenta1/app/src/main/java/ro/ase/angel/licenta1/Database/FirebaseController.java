package ro.ase.angel.licenta1.Database;

import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ro.ase.angel.licenta1.Utils.Records;
import ro.ase.angel.licenta1.Utils.Users;

/**
 * Created by angel on 20.03.2018.
 */

public class FirebaseController implements FirebaseConstants {
    private DatabaseReference database;
    private FirebaseDatabase fcontroller;
    private static FirebaseController firebaseController;

    private boolean responseInsert;

    private FirebaseController() {
        fcontroller = FirebaseDatabase.getInstance();
    }

    public static FirebaseController getInstance() {

        synchronized (FirebaseController.class) {
            if (firebaseController == null) {
                firebaseController = new FirebaseController();
            }
        }

        return firebaseController;
    }


    public boolean addRecord(Records record) {

        FirebaseAuth mAtuh = FirebaseAuth.getInstance();

        responseInsert = false;
        if (record == null)
            return responseInsert;

        database = fcontroller.getReference(mAtuh.getCurrentUser().getUid());


        if (record.getGlobalId() == null || record.getGlobalId().trim().isEmpty()) {
            record.setGlobalId(database.push().getKey());
        }

        database.child(record.getGlobalId()).setValue(record);
        addChangeEventListenerForEachRecord(record);

        return responseInsert;
    }


    private void addChangeEventListenerForEachRecord(Records record) {

        //acest eveniment se declanseaza pentru orice modificare adusa fiecarui record din firebase
        database.child(record.getGlobalId().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Records temp = dataSnapshot.getValue(Records.class);
                if (temp != null) {
                    responseInsert = true;
                    Log.i("FireBaseController", "Updated Record: " + temp.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("FirebaseController", "Insert is not working");
            }
        });
    }

    public void findAllRecords(ValueEventListener eventListener, String userId) {
        if (eventListener != null)
            database = fcontroller.getReference(userId);
        database.addValueEventListener(eventListener);
    }



}
