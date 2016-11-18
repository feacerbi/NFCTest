package com.felipeacerbi.nfctest.models;

import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;
import java.util.Map;

public class Comment {

    private String id;
    private String user;
    private String comment;

    public Comment() {
    }

    public Comment(String id) {
        this.id = id;
    }

    public Comment(DataSnapshot dataSnapshot) {
        fromMap(dataSnapshot);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("comment", comment);

        return result;
    }

    public void fromMap(DataSnapshot dataSnapshot) {
        id = dataSnapshot.getKey();
        user = dataSnapshot.child("user").getValue(String.class);
        comment = dataSnapshot.child("comment").getValue(String.class);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
