package com.app.jonathan.willimissbart.activity.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.app.jonathan.willimissbart.activity.core.MainActivity;
import com.app.jonathan.willimissbart.persistence.SPManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this,
            !new SPManager(this).containsUserData()
                ? OnboardingActivity.class : MainActivity.class);
        startActivity(intent);
        finish();
    }
}
