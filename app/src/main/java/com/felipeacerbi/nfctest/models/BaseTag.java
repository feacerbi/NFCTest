package com.felipeacerbi.nfctest.models;

import com.felipeacerbi.nfctest.firebasemodels.TagDB;

public class BaseTag {

    String id;
    TagDB tagDB;

    public BaseTag() {
    }

    public BaseTag(String id, String user) {
        this.id = id;
        this.tagDB = new TagDB(user);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TagDB getTagDB() {
        return tagDB;
    }

    public void setTagDB(TagDB tagDB) {
        this.tagDB = tagDB;
    }
}
