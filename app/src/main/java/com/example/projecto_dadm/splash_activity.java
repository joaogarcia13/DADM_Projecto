package com.example.projecto_dadm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class splash_activity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
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

    //redericiona para o menu se o user estiver logged in
    private void reload() {

    }
}