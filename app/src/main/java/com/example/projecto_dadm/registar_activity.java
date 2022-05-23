package com.example.projecto_dadm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.HashMap;
import java.util.Map;

public class registar_activity extends AppCompatActivity {

    private EditText nome;
    private EditText email;
    private EditText pass;
    private EditText pass2;
    private CheckBox check;
    private String passRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registar);
        nome = findViewById(R.id.registar_input_user);
        email = findViewById(R.id.registar_input_email);
        pass = findViewById(R.id.registar_input_pass);
        pass2 = findViewById(R.id.registar_input_pass2);
        check = findViewById(R.id.registar_checkBox);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onBackPressed() {
        Intent switchActivityIntent = new Intent(this, splash_activity.class);
        startActivity(switchActivityIntent);
        finish();
    }

    public void registarUtilizador(View view) {

        if (email.getText().toString().equals("") || pass.getText().toString().equals("") || pass2.getText().toString().equals("") || nome.getText().toString().equals("")) {
            DynamicToast.makeError(registar_activity.this, "Por favor preencha todos os campos.").show();
        } else if (!check.isChecked()) {
            DynamicToast.makeError(registar_activity.this, "É necessário aceitar os termos e condições de serviço para prosseguir.").show();
        } else if (!pass.getText().toString().equals(pass2.getText().toString())) {
            DynamicToast.makeError(registar_activity.this, "As passwords introduzidas não sao iguais.").show();
        } else if (!pass.getText().toString().matches(passRegex)) {
            DynamicToast.makeError(registar_activity.this, "A Password é fraca.").show();
            pass.setError("A password deve conter pelo menos 8 carácteres, uma letra maiúscula, uma letra minúscula e um número.");
            pass.requestFocus();
            pass.setText("");
            pass2.setText("");
        } else {
            mAuth.createUserWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                Map<String, User> Users = new HashMap<>();
                                Users.put(user.getUid(), new User(email.getText().toString(), nome.getText().toString()));
                                mDatabase.child("Users").setValue(Users);

                                DynamicToast.makeSuccess(registar_activity.this, "Conta criada com sucesso.").show();
                                updateUI(user);
                            } else {
                                String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                switch (errorCode) {
                                    case "ERROR_INVALID_EMAIL":
                                        DynamicToast.makeError(registar_activity.this, "O email introduzido é inválido.").show();
                                        email.setError("The email address is badly formatted.");
                                        email.requestFocus();
                                        break;

                                    case "ERROR_EMAIL_ALREADY_IN_USE":
                                        DynamicToast.makeError(registar_activity.this, "Já existe uma conta associada a este email.").show();
                                        email.setError("The email address is already in use by another account.");
                                        email.requestFocus();
                                        break;

                                    default:
                                        DynamicToast.makeError(registar_activity.this, "Ocorreu um erro. Por favor tente mais tarde..").show();
                                }
                            }
                        }
                    });
        }
    }

    private void updateUI(FirebaseUser user) {

    }

}
