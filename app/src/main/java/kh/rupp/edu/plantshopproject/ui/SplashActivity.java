package kh.rupp.edu.plantshopproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import kh.rupp.edu.plantshopproject.R;
import kh.rupp.edu.plantshopproject.session.SessionManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        setContentView(R.layout.activity_splash);

        // If already logged in go straight to Main
        SessionManager session = new SessionManager(this);
        if (session.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        Button btnGetStarted = findViewById(R.id.btn_get_started);
        btnGetStarted.setOnClickListener(v -> {
            startActivity(new Intent(SplashActivity.this, OnboardingActivity.class));
            finish();
        });
    }
}
