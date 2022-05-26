package com.example.projecto_dadm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class menu_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    //TODO fazer os eventos onclick dos botoes que ligam para as restantes paginas
    //TODO refazer todas as ligaçoes de botoes (tirar o onclick do xml e fazer programaticamente)
    public void MudarLayoutRegistarItem(){
        Intent switchActivityIntent = new Intent(this, registar_item_activity.class);
        startActivity(switchActivityIntent);
        finish();
    }

    public void MudarLayoutProcurar(){
        Intent switchActivityIntent = new Intent(this, procurar_activity.class);
        startActivity(switchActivityIntent);
        finish();
    }

    public void MudarLayoutPertoMim(){
        Intent switchActivityIntent = new Intent(this, items_perto_activity.class);
        startActivity(switchActivityIntent);
        finish();
    }
}
