package com.felipeacerbi.nfctest.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by felipe.acerbi on 06/10/2016.
 */

public class UserDB {

    private String name;
    private String email;
    private List<NFCTagDB> nfcTagDBs;
    private List<String> playRequests;
    private boolean isOnline;

    public UserDB() {
    }

    public UserDB(String name, String email, List<NFCTagDB> nfcTagDBs, List<String> playRequests, boolean isOnline) {
        this.name = name;
        this.email = email;
        this.nfcTagDBs = nfcTagDBs;
        this.playRequests = playRequests;
        this.isOnline = isOnline;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public List<NFCTagDB> getNfcTagDBs() {

        return (nfcTagDBs == null) ? new ArrayList<NFCTagDB>() : nfcTagDBs;
    }

    public void setNfcTagDBs(List<NFCTagDB> nfcTagDBs) {
        this.nfcTagDBs = nfcTagDBs;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getPlayRequests() {
        return (playRequests == null) ? new ArrayList<String>() : playRequests;
    }

    public void setPlayRequests(List<String> playRequests) {
        this.playRequests = playRequests;
    }
}
