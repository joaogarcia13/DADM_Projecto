package com.example.projecto_dadm;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class registar_item_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registar_item);
    }
    @Override
    public void onBackPressed() {
        Intent switchActivityIntent = new Intent(this, splash_activity.class);
        startActivity(switchActivityIntent);
        finish();
    }

}
