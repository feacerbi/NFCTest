package com.felipeacerbi.nfctest.firebasemodels;

import java.util.ArrayList;
import java.util.List;

public class UserDB {

    private String idToken;
    private String name;
    private String email;
    private List<NFCTagDB> tags;
    private boolean online;
    private boolean playing;

    public UserDB() {
    }

    public UserDB(String idToken, String name, String email, List<NFCTagDB> tags) {
        this.idToken = idToken;
        this.name = name;
        this.email = email;
        this.tags = tags;
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

    public List<NFCTagDB> getTags() {

        return (tags == null) ? new ArrayList<NFCTagDB>() : tags;
    }

    public void setTags(List<NFCTagDB> tags) {
        this.tags = tags;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
