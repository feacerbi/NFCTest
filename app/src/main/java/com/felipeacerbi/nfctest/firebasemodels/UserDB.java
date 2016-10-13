package com.felipeacerbi.nfctest.firebasemodels;

import java.util.ArrayList;
import java.util.List;

public class UserDB {

    private String name;
    private String email;
    private List<NFCTagDB> tags;
    private List<String> requests;
    private boolean online;
    private boolean playing;

    public UserDB() {
    }

    public UserDB(String name, String email, List<NFCTagDB> tags, List<String> requests) {
        this.name = name;
        this.email = email;
        this.tags = tags;
        this.requests = requests;
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

    public List<String> getRequests() {
        return (requests == null) ? new ArrayList<String>() : requests;
    }

    public void setRequests(List<String> requests) {
        this.requests = requests;
    }
}
