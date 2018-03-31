package ro.ase.angel.licenta1.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

import ro.ase.angel.licenta1.LoginActivity;
import ro.ase.angel.licenta1.MainActivity;

/**
 * Created by angel on 22.03.2018.
 */

public class SessionManagement implements Constants {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    public static String getKeyUserId() { return  KEY_USER_ID; }

    public static String getKeyUsername() {
        return KEY_USERNAME;
    }

//    public static String getKeyName() {
//        return KEY_NAME;
//    }

    public SessionManagement(Context context){
        this._context = context;
        sharedPreferences = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void createLoginSession(String globalId, String username){

        editor.putBoolean(IS_LOGIN, true);
        editor.putString(Constants.KEY_USER_ID, globalId);
        editor.putString(KEY_USERNAME, username);
//        editor.putString(KEY_NAME, name);

        editor.commit();
    }

    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // daca userul nu este logat, va fi redirectionat catre activitatea de login
            Intent i = new Intent(_context, LoginActivity.class);
            // Se inchid toate activitatile
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Se adauga un FLAG nou pt a porni Activitatea
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


            _context.startActivity(i);
        }
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<>();
        //user id
        user.put(KEY_USER_ID, sharedPreferences.getString(KEY_USER_ID, ""));
        // username
        user.put(KEY_USERNAME, sharedPreferences.getString(KEY_USERNAME, ""));

        // user name
//        user.put(KEY_NAME, sharedPreferences.getString(KEY_NAME, ""));

        // return user
        return user;
    }

    public void logoutUser(){
        editor.clear();
        editor.commit();

        // Dupa LogOut userul va fi redirectionat catre activitatea principala
        Intent i = new Intent(_context, LoginActivity.class);
        // Se inchid toate activitatile
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Se adauga un FLAG nou pt a porni Activitatea
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    public boolean isLoggedIn(){
        return sharedPreferences.getBoolean(IS_LOGIN, false);
    }
}
