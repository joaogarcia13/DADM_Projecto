package com.example.projecto_dadm;

import com.google.firebase.database.DatabaseReference;

public class User {

    public String nome;
    public String email;

    public User(String email, String nome) {
        this.nome = nome;
        this.email = email;
    }
}


