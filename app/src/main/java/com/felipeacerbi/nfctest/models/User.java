package com.felipeacerbi.nfctest.models;

import com.felipeacerbi.nfctest.firebasemodels.UserDB;

/**
 * Created by felipe.acerbi on 06/10/2016.
 */

public class User {

    private String username;
    private UserDB userDB;

    public User() {
    }

    public User(String username, UserDB userDB) {
        this.username = username;
        this.userDB = userDB;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserDB getUserDB() {
        return userDB;
    }

    public void setUserDB(UserDB userDB) {
        this.userDB = userDB;
    }
}
