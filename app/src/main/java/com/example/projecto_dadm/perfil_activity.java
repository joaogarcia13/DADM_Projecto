package com.example.projecto_dadm;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.Objects;

public class perfil_activity extends AppCompatActivity {

    private TextView nomeTexto;
    private TextView emailTexto;
    private FirebaseAuth FireUser;
    private DatabaseReference mDatabase;
    private String passRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        FireUser = FirebaseAuth.getInstance();
        nomeTexto = findViewById(R.id.perfil_user);
        emailTexto = findViewById(R.id.perfil_email);
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        getUser();
    }

    @Override
    public void onBackPressed() {
        Intent switchActivityIntent = new Intent(this, menu_activity.class);
        startActivity(switchActivityIntent);
        finish();
    }
    public void perfilMenu(View v){
        updateUI();
    }

    public void logout(View v){
       FireUser.signOut();
        ((global) this.getApplication()).setUser(null);
        Intent switchActivityIntent = new Intent(this, splash_activity.class);
        startActivity(switchActivityIntent);
        finish();
    }

    public void MudarPass(View v){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Mudar Password");
        final EditText oldPass = new EditText(this);
        final EditText newPass = new EditText(this);
        final EditText confirmarPass = new EditText(this);
        oldPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        newPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        confirmarPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        oldPass.setPadding(10,100,0,25);
        oldPass.setHint("Password Atual:");
        newPass.setHint("Nova Password:");
        confirmarPass.setHint("Confirmar Nova Password");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(50,5,50,30);
        LinearLayout ll = new LinearLayout(this);
        oldPass.setLayoutParams(lp);
        newPass.setLayoutParams(lp);
        confirmarPass.setLayoutParams(lp);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.addView(oldPass);
        ll.addView(newPass);
        ll.addView(confirmarPass);
        alertDialog.setView(ll);
        alertDialog.setPositiveButton("Confirmar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(Objects.equals(oldPass.getText().toString(), "") || Objects.equals(newPass.getText().toString(), "")
                                || Objects.equals(confirmarPass.getText().toString(), "")){
                            DynamicToast.makeError(perfil_activity.this, "Os campos não podem estar vazios..").show();
                        }else if(!newPass.getText().toString().equals(confirmarPass.getText().toString())){
                            DynamicToast.makeError(perfil_activity.this, "As novas passwords introduzidas não coincidem").show();
                        }else if (!newPass.getText().toString().matches(passRegex)){
                            DynamicToast.makeError(perfil_activity.this, "A password deve conter pelo menos 8 carácteres, uma letra maiúscula, uma letra minúscula e um número.",1999999999).show();
                        }else {
                            final String email = FireUser.getCurrentUser().getEmail();
                            AuthCredential credential = EmailAuthProvider.getCredential(email, oldPass.getText().toString());

                            FireUser.getCurrentUser().reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        FireUser.getCurrentUser().updatePassword(newPass.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (!task.isSuccessful()) {
                                                    DynamicToast.makeError(perfil_activity.this, "Ocurreu um erro. Por favor tente mais tarde.").show();
                                                } else {
                                                    DynamicToast.makeSuccess(perfil_activity.this, "Password atualizada com sucesso.").show();
                                                }
                                            }
                                        });
                                    } else {
                                        DynamicToast.makeError(perfil_activity.this, "Ocorreu um erro ao validar o seu pedido. Certifique-se que a Password atual introduzida está correcta.", 1999999999).show();
                                    }
                                }
                            });
                        }
                    }
                });
        alertDialog.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    public void MudarUser(View v){
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
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot nomeSnapshot : dataSnapshot.getChildren()) {
                            for (DataSnapshot nomeSnapshot2 : nomeSnapshot.getChildren()) {
                                if(Objects.equals(nomeSnapshot2.getKey(), FireUser.getUid())) {
                                    DatabaseReference ref = nomeSnapshot2.child("nome").getRef();
                                    ref.setValue(input.getText().toString());
                                    System.out.println("eeeee");
                                    return;
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                nomeTexto.setText(input.getText().toString());
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

    private void updateUI() {
        Intent switchActivityIntent = new Intent(this, menu_activity.class);
        startActivity(switchActivityIntent);
        finish();
    }

    private void getUser(){
        mDatabase.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot nomeSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot nomeSnapshot2 : nomeSnapshot.getChildren()) {
                        if(Objects.equals(nomeSnapshot2.getKey(), FireUser.getUid())) {
                            nomeTexto.setText(nomeSnapshot2.child("nome").getValue(String.class));
                            emailTexto.setText(nomeSnapshot2.child("email").getValue(String.class));
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
