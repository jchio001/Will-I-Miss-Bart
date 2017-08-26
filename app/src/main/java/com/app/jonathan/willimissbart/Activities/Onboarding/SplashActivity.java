package com.app.jonathan.willimissbart.Activities.Onboarding;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.app.jonathan.willimissbart.Activities.AppActivities.MainActivity;
import com.app.jonathan.willimissbart.Persistence.SPSingleton;
import com.app.jonathan.willimissbart.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (SPSingleton.getInstance(getApplicationContext()).getUserData().isEmpty()) {
            Intent intent = new Intent(this, StationInputActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        finish();
    }
}