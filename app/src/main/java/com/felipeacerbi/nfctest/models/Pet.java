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
    private String age;
    private String profileImage;
    private String breed;
    private Map<String, Boolean> owners;

    public Pet() {
    }

    public Pet(String id) {
        this.id = id;
    }

    public Pet(DataSnapshot dataSnapshot) {
        fromMap(dataSnapshot);
    }

    // Test Constructor
    public Pet(String id, String tag, String name, String age, String breed, String profileImage) {
        this.id = id;
        this.tag = tag;
        this.name = name;
        this.age = age;
        this.breed = breed;
        this.profileImage = profileImage;
        owners = new HashMap<>();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("tag", tag);
        result.put("name", name);
        result.put("age", age);
        result.put("profileImage", profileImage);
        result.put("breed", breed);
        result.put("profileImage", profileImage);
        result.put("owners", owners);

        return result;
    }

    public void fromMap(DataSnapshot dataSnapshot) {
        id = dataSnapshot.getKey();
        tag = dataSnapshot.child("tag").getValue(String.class);
        name = dataSnapshot.child("name").getValue(String.class);
        age = dataSnapshot.child("age").getValue(String.class);
        breed = dataSnapshot.child("breed").getValue(String.class);
        profileImage = dataSnapshot.child("profileImage").getValue(String.class);
        GenericTypeIndicator<Map<String, Boolean>> t = new GenericTypeIndicator<Map<String, Boolean>>() {};
        owners = dataSnapshot.child("owners").getValue(t);
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

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public Map<String, Boolean> getOwners() {
        return owners;
    }

    public void setOwners(Map<String, Boolean> owners) {
        this.owners = owners;
    }

    public String getInfos() {
        return getBreed() + " | " + getAge() + " years";
    }

    @Override
    public String toString() {
        return getName();
    }
}
