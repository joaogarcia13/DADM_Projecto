package com.example.projecto_dadm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class menu_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    //TODO fazer os eventos onclick dos botoes que ligam para as restantes paginas
    public void MudarLayoutRegistar_Item(View v){
        Intent switchActivityIntent = new Intent(this, registar_item_activity.class);
        startActivity(switchActivityIntent);
        finish();
    }

    public void MudarLayoutProcurar(View v){
        Intent switchActivityIntent = new Intent(this, procurar_activity.class);
        startActivity(switchActivityIntent);
        finish();
    }

    public void MudarLayoutPertoMim(View v){
        Intent switchActivityIntent = new Intent(this, items_perto_activity.class);
        startActivity(switchActivityIntent);
        finish();
    }
}
