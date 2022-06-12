package com.example.projecto_dadm;

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

    public Item(){};

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
