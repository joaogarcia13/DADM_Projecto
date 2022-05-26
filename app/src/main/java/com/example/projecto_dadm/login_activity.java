package com.example.projecto_dadm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
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
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                switch (errorCode) {
                                    case "ERROR_INVALID_CREDENTIAL":
                                        DynamicToast.makeError(login_activity.this, "As credenciais introduzidas ou são inválidas ou expiraram.").show();
                                        break;

                                    case "ERROR_INVALID_EMAIL":
                                        DynamicToast.makeError(login_activity.this, "O Email introduzido é inválido.").show();
                                        email.setError("The email address is badly formatted.");
                                        email.requestFocus();
                                        break;

                                    case "ERROR_WRONG_PASSWORD":
                                        DynamicToast.makeError(login_activity.this, "Password incorreta.").show();
                                        password.setError("A Password está incorreta.");
                                        password.requestFocus();
                                        password.setText("");
                                        break;

                                    case "ERROR_USER_DISABLED":
                                        DynamicToast.makeError(login_activity.this, "A sua conta foi desativada.").show();
                                        break;

                                    case "ERROR_USER_NOT_FOUND":
                                        DynamicToast.makeError(login_activity.this, "Não existe um conta associada a este email.").show();
                                        break;

                                    default:
                                        DynamicToast.makeError(login_activity.this, "Ocorreu um erro. Por favor tente mais tarde..").show();
                                }
                            }
                        }
                    });
        }
    }

    //TODO redirecionar para o menu
    private void updateUI(FirebaseUser user) {

    }
}
