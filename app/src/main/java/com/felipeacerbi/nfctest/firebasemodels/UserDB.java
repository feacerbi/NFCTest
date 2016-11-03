package com.felipeacerbi.nfctest.firebasemodels;

import com.felipeacerbi.nfctest.models.BaseTag;

import java.util.ArrayList;
import java.util.List;

public class UserDB {

    private String idToken;
    private String name;
    private String email;
    private boolean online;
    private boolean playing;

    public UserDB() {
    }

    public UserDB(String idToken, String name, String email) {
        this.idToken = idToken;
        this.name = name;
        this.email = email;
        online = false;
        playing = false;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
