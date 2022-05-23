package com.example.projecto_dadm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class splash_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    public void MudarLayoutSignIn(View view) {
        Intent switchActivityIntent = new Intent(this, login_activity.class);
        startActivity(switchActivityIntent);
        finish();

    }
    public void MudarLayoutRegistar(View view) {
        Intent switchActivityIntent = new Intent(this, registar_activity.class);
        startActivity(switchActivityIntent);
        finish();
    }
}
