package com.example.projecto_dadm;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Introduza o novo username:");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setPadding(5,80,5,30);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(50,3,50,3);
        input.setLayoutParams(lp);
        RelativeLayout container = new RelativeLayout(this);
        RelativeLayout.LayoutParams rlParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        container.setLayoutParams(rlParams);
        container.addView(input);
        builder.setView(container);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(input.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    DynamicToast.makeSuccess(login_activity.this, "Por favor siga as instruções enviadas para o seu email",1999999999).show();
                                    Log.d("Sucesso", "Email sent.");
                                }else{
                                    DynamicToast.makeError(login_activity.this, "Não existe uma conta associada a este email.",1999999999).show();
                                }
                            }
                        });
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }
    public void Autenticar(View view){
        if(email.getText().toString().equals("") || password.getText().toString().equals("")) {
            DynamicToast.makeError(login_activity.this, "Por favor preencha todos os campos.").show();
        }else {
            mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(mAuth.getCurrentUser().isEmailVerified() || email.getText().toString().equals("teste@teste.pt")) { //TODO este equals e para tirar, esta aqui so para a conta de teste passar a verificaçao pois e um email falso
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
                            }else{
                                DynamicToast.makeError(login_activity.this, "Por favor confirme o seu email antes de fazer login.",1999999999).show();
                            }
                        }
                    });
        }
    }

    private void updateUI(FirebaseUser user) {
        ((global) this.getApplication()).setUser(user);
        Intent switchActivityIntent = new Intent(this, menu_activity.class);
        startActivity(switchActivityIntent);
        finish();
    }
}
