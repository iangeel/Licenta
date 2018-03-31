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

    public boolean addAllUsers(List<Users> users) {

        if (users == null || users.size() == 0)
            return false;

        for (Users user : users) {
            addUser(user);
        }

        return true;
    }

    public boolean addUser(Users user) {
        String userGlobalId = null;
        responseInsert = false;
        if (user == null)
            return responseInsert;

        database = fcontroller.getReference(TABLE_NAME_USERS);
        //genereaza un id pentru inregistrarea ta.
        if (user.getGlobalId() == null || user.getGlobalId().trim().isEmpty()) {
            userGlobalId = database.push().getKey();
            user.setGlobalId(userGlobalId);
        }
        if (user.getRecords() == null || user.getRecords().toArray().length == 0) {
            (database.child(userGlobalId)).push().setValue("records");
        }
        database.child(user.getGlobalId()).setValue(user);
        addChangeEventListenerForEachUser(user);

        return responseInsert;
    }

    public boolean addRecord(Records record) {

        FirebaseAuth mAtuh = FirebaseAuth.getInstance();

        responseInsert = false;
        if (record == null)
            return responseInsert;

        database = fcontroller.getReference(mAtuh.getCurrentUser().getUid());

        //genereaza un id pentru inregistrarea ta.
        if (record.getGlobalId() == null || record.getGlobalId().trim().isEmpty()) {
            record.setGlobalId(database.push().getKey());
        }
        database.child(record.getGlobalId()).setValue(record);
        addChangeEventListenerForEachRecord(record);

        return responseInsert;
    }


    private void addChangeEventListenerForEachUser(Users user) {

        //acest eveniment se declanseaza pentru orice modificare adusa fiecarui user din firebase
        database.child(user.getGlobalId().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Users temp = dataSnapshot.getValue(Users.class);
                if (temp != null) {
                    responseInsert = true;
                    Log.i("FireBaseController", "Updated User: " + temp.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("FirebaseController", "Insert is not working");
            }
        });
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

    public void findAllUsers(ValueEventListener eventListener) {
        if (eventListener != null)
            database = fcontroller.getReference(TABLE_NAME_USERS);
        database.addValueEventListener(eventListener);
    }

    public Query removePlayer(Users player){
        //aceasta metoda face stergerea unui jucator, primit ca parametru
        //prima data verificam daca jucatorul este nenull si are un globalId. altfel nu putem face delete
        if(player == null || player.getGlobalId() == null || player.getGlobalId().trim().isEmpty()){
            return null;
        }
        // accesam tabela players, deoarece vrem sa stergem un element din ea.
        //accesul se face prin metoda getReference
        database = fcontroller.getReference(TABLE_NAME_USERS);
        //din toate elementele tabelei players, vrem sa stergem doar inregistrarea care are id-ul egal cu globalId.
        //child(id) va ofera inregistrarea cautata.
        //metoda removevalue() realizeaza stergerea din baza de date.
        database.child(player.getGlobalId()).removeValue();
        //returnam Query-ul care contine intr-un obiect Datasnapshot obiectul sters.
        return database.child(player.getGlobalId());
    }


}
