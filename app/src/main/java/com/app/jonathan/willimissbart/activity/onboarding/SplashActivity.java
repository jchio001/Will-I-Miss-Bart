package com.app.jonathan.willimissbart.activity.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.app.jonathan.willimissbart.activity.core.MainActivity;
import com.app.jonathan.willimissbart.persistence.SPManager;
import com.app.jonathan.willimissbart.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Intent intent = new Intent(this,
            !SPManager.containsUserData(this)
                ? OnboardingActivity.class : MainActivity.class);
        startActivity(intent);
        finish();
    }
}
