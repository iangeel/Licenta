package ro.ase.angel.licenta1;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import ro.ase.angel.licenta1.Utils.Constants;

public class SplashScreenActivity extends AppCompatActivity {

    ImageView ivLogo;
    Animation animation, textAnimation;
    TextView tvSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.AppThemeSplash);
        }
        else setTheme(R.style.AppThemeSplash);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ivLogo = (ImageView) findViewById(R.id.ivLogo);
        tvSplash = (TextView) findViewById(R.id.tvSplashActivity);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.transition);
        textAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.text_transition);
        ivLogo.startAnimation(animation);
        tvSplash.startAnimation(textAnimation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, Constants.SPLASH_DISPLAY_LENGTH);
    }
}
