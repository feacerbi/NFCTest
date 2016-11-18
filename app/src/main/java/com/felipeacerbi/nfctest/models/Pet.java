package com.felipeacerbi.nfctest.models;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.GenericTypeIndicator;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Pet implements Serializable {

    private String id;
    private String tag;
    private String name;
    private int type;
    private Map<String, Boolean> users;

    public Pet() {
    }

    public Pet(String id) {
        this.id = id;
    }

    public Pet(DataSnapshot dataSnapshot) {
        fromMap(dataSnapshot);
    }

    // Test Constructor
    public Pet(String id, String tag, String name, int type) {
        this.id = id;
        this.tag = tag;
        this.name = name;
        this.type = type;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("tag", tag);
        result.put("name", name);
        result.put("type", type);
        result.put("users", users);

        return result;
    }

    public void fromMap(DataSnapshot dataSnapshot) {
        id = dataSnapshot.getKey();
        tag = dataSnapshot.child("tag").getValue(String.class);
        name = dataSnapshot.child("name").getValue(String.class);
        type = dataSnapshot.child("type").getValue(Integer.class);
        GenericTypeIndicator<Map<String, Boolean>> t = new GenericTypeIndicator<Map<String, Boolean>>() {};
        users = dataSnapshot.child("users").getValue(t);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Map<String, Boolean> getUsers() {
        return users;
    }

    public void setUsers(Map<String, Boolean> users) {
        this.users = users;
    }
}
