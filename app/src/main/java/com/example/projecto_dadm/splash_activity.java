package com.example.projecto_dadm;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

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
        if(isInternetAvailable()) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if(currentUser != null){
                ((global) this.getApplication()).setUser(currentUser);
                reload();
            }
        }else{
            DynamicToast.makeError(this,"Esta aplicação precisa de Ligação á internet para iniciar.", 100000000).show();
        }
    }

    public void MudarLayoutSignIn(View view) {
        if(isInternetAvailable()) {
            Intent switchActivityIntent = new Intent(this, login_activity.class);
            startActivity(switchActivityIntent);
            finish();
        }else{
            DynamicToast.makeError(this,"Esta aplicação precisa de Ligação á internet para iniciar.", 100000000).show();
        }
    }
    public void MudarLayoutRegistar(View view) {
        if(isInternetAvailable()) {
            Intent switchActivityIntent = new Intent(this, registar_activity.class);
            startActivity(switchActivityIntent);
            finish();
        }else{
            DynamicToast.makeError(this,"Esta aplicação precisa de Ligação á internet para iniciar.", 100000000).show();
        }
    }

    private void reload() {
        Intent switchActivityIntent = new Intent(this, menu_activity.class);
        startActivity(switchActivityIntent);
        finish();
    }

    public boolean isInternetAvailable() {
        final ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connMgr != null) {
            NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();
            if (activeNetworkInfo != null) { // connected to the internet
                // connected to the mobile provider's data plan
                if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    // connected to wifi
                    return true;
                } else return activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            }
        }
        return false;
    }
}
