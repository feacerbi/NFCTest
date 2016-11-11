package com.felipeacerbi.nfctest.models;

import com.felipeacerbi.nfctest.firebasemodels.BaseTagDB;

public class BaseTag {

    String id;
    BaseTagDB baseTagDB;

    public BaseTag() {
    }

    public BaseTag(String id, BaseTagDB baseTagDB) {
        this.id = id;
        this.baseTagDB = baseTagDB;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BaseTagDB getBaseTagDB() {
        return baseTagDB;
    }

    public void setBaseTagDB(BaseTagDB baseTagDB) {
        this.baseTagDB = baseTagDB;
    }
}
