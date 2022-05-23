package com.example.projecto_dadm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

public class login_activity extends AppCompatActivity {
    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;
    private EditText email;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.password);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }

    @Override
    public void onBackPressed() {
        Intent switchActivityIntent = new Intent(this, splash_activity.class);
        startActivity(switchActivityIntent);
        finish();
    }

    public void RecuperarConta(View view){

    }
    public void Autenticar(View view){
        if(email.getText().toString().equals("") || password.getText().toString().equals("")) {
            DynamicToast.makeError(login_activity.this, "Por favor preencha todos os campos.").show();
        }else {
            mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                //updateUI(user);
                            } else {
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                //verificar se e so a password que esta mal ou se nao existe uma conta con este email
                                DynamicToast.makeError(login_activity.this, "Authentication failed.").show();
                            }
                        }
                    });
        }
    }

    //redericiona para o menu se o user estiver loged in
    private void reload() {

    }

    private void UpdateUI(FirebaseUser user){
        //mudar para  o menu como utilizador user

    }
}
