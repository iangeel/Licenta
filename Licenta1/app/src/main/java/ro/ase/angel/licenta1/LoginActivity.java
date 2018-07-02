package ro.ase.angel.licenta1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ro.ase.angel.licenta1.Database.FirebaseController;
import ro.ase.angel.licenta1.Utils.Constants;
import ro.ase.angel.licenta1.Utils.SessionManagement;
import ro.ase.angel.licenta1.Utils.Users;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "FBLOG";
    private static final String RESET_TAG = "PASS_RESET_TAG";
    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;
    private LoginButton btnFbLogin;
    private Button btnLogin, btnFacebook;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private CheckBox cbRememberMe;
    private EditText etUsername, etPassword;
    private List<Users> usersList = new ArrayList<>();
    private Users loggedUser;
    private FirebaseController firebaseController;
    private SessionManagement sessionManagement;
    private FirebaseUser currentUser = null;
    private String email, password;
    private TextView tvError, tvForgotPassword, btnRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.AppThemeLogin);
        }
        else setTheme(R.style.AppThemeLogin);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        componentsInitialization();


        cbRememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                rememberMeMethod();
            }
        });


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivityForResult(intent, Constants.REGISTER_USER_CODE);

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if(mAuth.getCurrentUser() != null) {
                        mAuth.signOut();
                    }
                    rememberMeMethod();
                    signInWithEmailAndPassword();

            }
        });



        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnFacebook.setEnabled(false);

                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this,
                        Arrays.asList("email", "public_profile"));
                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, "facebook:onSuccess:" + loginResult);
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "facebook:onCancel");
                        // ...
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(TAG, "facebook:onError", error);
                        // ...
                    }
                });
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPasswordResetEmail();
            }
        });

    }


    private void componentsInitialization() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        mAuth = FirebaseAuth.getInstance();
        firebaseController = FirebaseController.getInstance();

        mCallbackManager = CallbackManager.Factory.create();
//        btnFbLogin = findViewById(R.id.btnFbLogin);
//        btnFbLogin.setReadPermissions("email", "public_profile");

        btnFacebook = (Button) findViewById(R.id.btnFacebook);

        tvForgotPassword = (TextView) findViewById(R.id.tvForgotPassword);
        tvError = (TextView) findViewById(R.id.tvError);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (TextView) findViewById(R.id.btnRegister_login_activity);
        cbRememberMe = (CheckBox) findViewById(R.id.cbLoginActivity);

        sessionManagement = new SessionManagement(getApplicationContext());

        sharedPreferences = getSharedPreferences(Constants.LOGIN_SHARED_PREF, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        checkRememberMeOnStartApplication();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        if(requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);

        }
        else if (requestCode == Constants.REGISTER_USER_CODE) {
            if (resultCode == RESULT_OK) {
                String[] loginExtra = data.getStringArrayExtra(Constants.LOGIN_EXTRA);
                if(data != null) {
                    email = loginExtra[0];
                    password = loginExtra[1];
                    if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                        etUsername.setText(email);
                        etPassword.setText(password);
                    }
                } else {
                    Toast.makeText(this,"Data null", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this,"Result code error", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this,"Request code error", Toast.LENGTH_SHORT).show();
        }
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        currentUser = mAuth.getCurrentUser();
//
//        if(currentUser != null) {
//           updateUI();
//        }
//    }

    private void checkRememberMeOnStartApplication() {
        if (sharedPreferences.getBoolean(Constants.KEY_REMEMBER, false))
            cbRememberMe.setChecked(true);
        else
            cbRememberMe.setChecked(false);

        etUsername.setText(sharedPreferences.getString(Constants.USERNAME_KEY,""));
        etPassword.setText(sharedPreferences.getString(Constants.PASSWORD_KEY,""));
    }

    private void rememberMeMethod() {
        if (cbRememberMe.isChecked()) {
            editor.putString(Constants.USERNAME_KEY, etUsername.getText().toString().trim());
            editor.putString(Constants.PASSWORD_KEY, etPassword.getText().toString().trim());
            editor.putBoolean(Constants.KEY_REMEMBER, true);
            editor.commit();
        } else {
            editor.putBoolean(Constants.KEY_REMEMBER, false);
            editor.remove(Constants.PASSWORD_KEY);
            editor.remove(Constants.USERNAME_KEY);
            editor.commit();
        }
    }

    private boolean userCredentialsValidation() {
        boolean check = true;

        if(TextUtils.isEmpty(etUsername.getText().toString())){
           etUsername.setError("E-mail is mandatory...");
           check = false;
        }
        else if(TextUtils.isEmpty(etPassword.getText().toString())) {
            etPassword.setError("Password is mandatory...");
            check = false;
        }

        return check;
    }


    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            sessionManagement.createLoginSession(user.getUid(), user.getEmail());

                            btnFacebook.setEnabled(true);

                            updateUI();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                            btnFacebook.setEnabled(true);

                            Toast.makeText(LoginActivity.this, "Auth fail...",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }

    private void signInWithEmailAndPassword() {
            if(userCredentialsValidation()) {
                mAuth.signInWithEmailAndPassword(etUsername.getText().toString(), etPassword.getText().toString())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    checkEmailVerification();

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    tvError.setText("Incorrect e-mail or password");
                                    tvError.setTextColor(getResources().getColor(R.color.red));
                                    tvError.setError("Incorrect e-mail or password");

                                }

                                // ...
                            }
                        });
            }

    }


    private void checkEmailVerification() {
        FirebaseUser user = mAuth.getCurrentUser();

        if(user.isEmailVerified()) {
            updateUI();
        }
        else {
            tvError.setText("Please verify your email before signing in...");
            tvError.setTextColor(getResources().getColor(R.color.red));
            tvError.setError("Please verify your email before signing in...");
            mAuth.signOut();
        }
    }

    private void updateUI() {

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendPasswordResetEmail() {
        if(TextUtils.isEmpty(etUsername.getText().toString())) {
            tvError.setText("Enter your e-mail");
            tvError.setTextColor(getResources().getColor(R.color.orange));
            tvError.setError("Enter your e-mail");
        }
        tvError.setError(null);

        mAuth.sendPasswordResetEmail(etUsername.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            tvError.setText("Check your e-mail");
                            tvError.setTextColor(getResources().getColor(R.color.green));
                            tvError.setError("Check your e-mail");
                            try {
                                Thread.sleep(5000);
                                tvError.setError(null);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            Log.w(RESET_TAG, "passwordResetEmail:failure");
                        }
                    }
                });
    }
}

