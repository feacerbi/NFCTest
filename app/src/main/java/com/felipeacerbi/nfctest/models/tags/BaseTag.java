package com.felipeacerbi.nfctest.models.tags;

import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;
import java.util.Map;

public class BaseTag {

    private String id;
    private String pet;

    public BaseTag() {
    }

    public BaseTag(String id) {
        this.id = id;
    }

    public BaseTag(DataSnapshot dataSnapshot) {
        fromMap(dataSnapshot);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("pet", pet);

        return result;
    }

    public void fromMap(Map<String, Object> map) {
        pet = (String) map.get("pet");
    }

    public void fromMap(DataSnapshot dataSnapshot) {
        id = dataSnapshot.getKey();
        pet = dataSnapshot.child("pet").getValue(String.class);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPet() {
        return pet;
    }

    public void setPet(String pet) {
        this.pet = pet;
    }
}
