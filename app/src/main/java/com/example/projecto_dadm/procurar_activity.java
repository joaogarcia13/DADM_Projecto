package com.example.projecto_dadm;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class procurar_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_procurar);
    }

    @Override
    public void onBackPressed() {
        Intent switchActivityIntent = new Intent(this, splash_activity.class);
        startActivity(switchActivityIntent);
        finish();
    }
}
