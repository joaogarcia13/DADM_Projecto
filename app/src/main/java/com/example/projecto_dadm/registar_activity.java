package com.example.projecto_dadm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.pranavpandey.android.dynamic.toasts.DynamicToast;

public class registar_activity extends AppCompatActivity {

    private EditText user;
    private EditText email;
    private EditText pass;
    private EditText pass2;
    private CheckBox check;
    private String emailRegex = "^[a-zA-Z]{1,}@[a-zA-z]{1,}.[a-zA-z]{1,}$";
    private String passRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registar);
        user = findViewById(R.id.registar_input_user);
        email = findViewById(R.id.registar_input_email);
        pass = findViewById(R.id.registar_input_pass);
        pass2 = findViewById(R.id.registar_input_pass2);
        check = findViewById(R.id.registar_checkBox);
    }

    @Override
    public void onBackPressed() {
        Intent switchActivityIntent = new Intent(this, splash_activity.class);
        startActivity(switchActivityIntent);
        finish();
    }

    public void registarUtilizador(View view){
        if(email.getText().toString().equals("") || pass.getText().toString().equals("") || pass2.getText().toString().equals("") || user.getText().toString().equals("")) {
            DynamicToast.makeError(registar_activity.this, "Por favor preencha todos os campos.").show();
        }else if(!check.isChecked()){
            DynamicToast.makeError(registar_activity.this, "É necessário aceitar os termos e condições de serviço para prosseguir.").show();
        }else if(!email.getText().toString().matches(emailRegex)){
            DynamicToast.makeError(registar_activity.this, "O Email introduzido não é valido.").show();
        }else if(!pass.getText().toString().matches(passRegex)){
            DynamicToast.makeError(registar_activity.this, "A Password tem de ter pelo menos 8 caracteres e conter uma Letra maiúscula, uma letra minúscula e um número.").show();
        }else if(!pass.getText().toString().equals(pass2.getText().toString())){
            DynamicToast.makeError(registar_activity.this, "As passwords introduzidas não sao iguais.").show();
        }else{
            
        }
    }

}
