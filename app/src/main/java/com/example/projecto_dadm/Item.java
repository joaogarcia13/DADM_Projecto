package com.example.projecto_dadm;

import android.text.Editable;

public class Item {
    public String nome;
    public String descricao;
    public double latitude;
    public double longitude;
    public String foto;
    public boolean ativo;
    public String userId;

    public Item(String nome, String desc, double lat, double longi, String foto, String id){
        this.nome = nome;
        this.descricao = desc;
        this.latitude = lat;
        this.longitude = longi;
        this.foto = foto;
        this.userId = id;
        ativo = true;
    }
}
