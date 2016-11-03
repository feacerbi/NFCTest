package com.felipeacerbi.nfctest.firebasemodels;

import java.io.Serializable;

public class TagDB implements Serializable {

    private String user;

    public TagDB() {
    }

    public TagDB(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }
}
