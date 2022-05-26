package com.example.projecto_dadm;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class items_perto_activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_perto);
    }

    @Override
    public void onBackPressed() {
        Intent switchActivityIntent = new Intent(this, menu_activity.class);
        startActivity(switchActivityIntent);
        finish();
    }

}
