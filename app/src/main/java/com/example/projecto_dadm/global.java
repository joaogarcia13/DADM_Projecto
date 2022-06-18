package com.example.projecto_dadm;
import android.app.Application;
import com.google.firebase.auth.FirebaseUser;

public class global extends Application {
    private static  FirebaseUser user;
    public static FirebaseUser getUser() {
        return user;
    }
    public void setUser(FirebaseUser user) {
        global.user = user;
    }

}
