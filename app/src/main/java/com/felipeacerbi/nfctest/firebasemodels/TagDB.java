package com.felipeacerbi.nfctest.firebasemodels;

import java.io.Serializable;
import java.util.List;

public class TagDB implements Serializable {

    private List<Boolean> users;

    public TagDB() {
    }

    public TagDB(List<Boolean> users) {
        this.users = users;
    }

    public List<Boolean> getUsers() {
        return users;
    }

    public void setUsers(List<Boolean> users) {
        this.users = users;
    }
}
