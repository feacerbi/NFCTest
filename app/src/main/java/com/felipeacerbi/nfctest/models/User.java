package com.felipeacerbi.nfctest.models;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {

    private String username;
    private String idToken;
    private String name;
    private String email;
    private boolean online = false;
    private boolean playing = false;
    private Map<String, Boolean> pets = new HashMap<>();
    private Map<String, Boolean> added = new HashMap<>();

    public User() {
    }

    public User(String username) {
        this.username = username;
    }

    public User(DataSnapshot dataSnapshot) {
        fromMap(dataSnapshot);
    }

    public User(String username, String idToken, String name, String email) {
        this.username = username;
        this.idToken = idToken;
        this.name = name;
        this.email = email;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("idToken", idToken);
        result.put("name", name);
        result.put("email", email);
        result.put("online", online);
        result.put("playing", playing);

        return result;
    }

    public void fromMap(DataSnapshot dataSnapshot) {
        username = dataSnapshot.getKey();
        idToken = dataSnapshot.child("idToken").getValue(String.class);
        name = dataSnapshot.child("name").getValue(String.class);
        email = dataSnapshot.child("email").getValue(String.class);
        online = dataSnapshot.child("online").getValue(Boolean.class);
        playing = dataSnapshot.child("playing").getValue(Boolean.class);
        GenericTypeIndicator<Map<String, Boolean>> t = new GenericTypeIndicator<Map<String, Boolean>>() {};
        pets = dataSnapshot.child("pets").getValue(t);
        added = dataSnapshot.child("added").getValue(t);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Map<String, Boolean> getPets() {
        return pets;
    }

    public void setPets(Map<String, Boolean> pets) {
        this.pets = pets;
    }

    public Map<String, Boolean> getAdded() {
        return added;
    }

    public void setAdded(Map<String, Boolean> added) {
        this.added = added;
    }
}
