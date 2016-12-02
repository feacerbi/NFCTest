package com.felipeacerbi.nfctest.models.tags;

import com.google.firebase.database.DataSnapshot;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class BaseTag implements Serializable {

    private String id;
    private String pet;
    private String petName;

    public BaseTag() {
    }

    public BaseTag(String id) {
        this.id = id;
    }

    public BaseTag(String id, String pet, String petName) {
        this.id = id;
        this.pet = pet;
        this.petName = petName;
    }

    public BaseTag(DataSnapshot dataSnapshot) {
        fromMap(dataSnapshot);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("pet", pet);
        result.put("petName", petName);

        return result;
    }

    public void fromMap(Map<String, Object> map) {
        pet = (String) map.get("pet");
    }

    public void fromMap(DataSnapshot dataSnapshot) {
        id = dataSnapshot.getKey();
        pet = dataSnapshot.child("pet").getValue(String.class);
        petName = dataSnapshot.child("petName").getValue(String.class);
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

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }
}
