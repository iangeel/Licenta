package ro.ase.angel.licenta1;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import ro.ase.angel.licenta1.Utils.Constants;
import ro.ase.angel.licenta1.Utils.Records;
import ro.ase.angel.licenta1.Utils.Users;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterUserLOG";

    private EditText etUsername, etPassword, etRePassword;
    private Button btnRegister;
    private FirebaseAuth mAuth;
    private String email, password;
    public final static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        componentsInitialization();


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validations()) {
                    //creez user si trimit catre Login

                    createUserWithEmailAndPasswordViaFirebase();

                }
            }
        });
    }

    private void componentsInitialization() {
        mAuth = FirebaseAuth.getInstance();
        etUsername = (EditText) findViewById(R.id.etUsername_register_activity);
        etPassword = (EditText) findViewById(R.id.etPassword_register_activity);
        etRePassword = (EditText) findViewById(R.id.etRePassword_register_activity);
        btnRegister = (Button) findViewById(R.id.btnRegister_register_activity);

    }


    public boolean validations() {
        boolean check = true;

        if(TextUtils.isEmpty(etUsername.getText().toString()) || !isValidEmail(etUsername.getText().toString())) {
            etUsername.setError("E-mail is mandatory...");
            check = false;
        }
        else if (TextUtils.isEmpty(etPassword.getText().toString())) {
            etPassword.setError("Password is mandatory...");
            check = false;
        }
        else if (TextUtils.isEmpty(etRePassword.getText().toString()) || !TextUtils.equals(etPassword.getText().toString(), etRePassword.getText().toString())) {
            etRePassword.setError("Passwords not match...");
            check = false;
        }

        return check;
    }

//    public Users createUserFromData() {
//        String email = etUsername.getText().toString();
//        String pass = etPassword.getText().toString();
//
//        return new Users(email, pass);
//    }

    private void createUserWithEmailAndPasswordViaFirebase() {
        email = etUsername.getText().toString();
        password = etPassword.getText().toString();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            sendVerificationEmail();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            String[] loginExtra = {email, password};
                            intent.putExtra(Constants.LOGIN_EXTRA, loginExtra);
                            setResult(RESULT_OK, intent);
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
        

    }

    private void sendVerificationEmail() {
        FirebaseUser user = mAuth.getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            mAuth.signOut();
                        }
                        else {
                            Log.w(TAG, "sendVerificationEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Failed sending verification email...",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
