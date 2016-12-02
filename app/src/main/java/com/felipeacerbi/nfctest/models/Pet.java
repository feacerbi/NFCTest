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
    private String animal;
    private String breed;
    private String description;
    private int gender;
    private Map<String, Boolean> owners;

    public Pet() {
        owners = new HashMap<>();
    }

    public Pet(String id) {
        this.id = id;
        owners = new HashMap<>();
    }

    public Pet(DataSnapshot dataSnapshot) {
        fromMap(dataSnapshot);
    }

    // Test Constructor
    public Pet(String id, String tag, String name, String age, String profileImage, String animal, String breed, String description, int gender) {
        this.id = id;
        this.tag = tag;
        this.name = name;
        this.age = age;
        this.profileImage = profileImage;
        this.animal = animal;
        this.breed = breed;
        this.description = description;
        this.gender = gender;
        owners = new HashMap<>();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("tag", tag);
        result.put("name", name);
        result.put("age", age);
        result.put("profileImage", profileImage);
        result.put("animal", animal);
        result.put("breed", breed);
        result.put("description", description);
        result.put("gender", gender);
        result.put("owners", owners);

        return result;
    }

    public void fromMap(DataSnapshot dataSnapshot) {
        id = dataSnapshot.getKey();
        tag = dataSnapshot.child("tag").getValue(String.class);
        name = dataSnapshot.child("name").getValue(String.class);
        age = dataSnapshot.child("age").getValue(String.class);
        profileImage = dataSnapshot.child("profileImage").getValue(String.class);
        animal = dataSnapshot.child("animal").getValue(String.class);
        breed = dataSnapshot.child("breed").getValue(String.class);
        description = dataSnapshot.child("description").getValue(String.class);
        gender = dataSnapshot.child("gender").getValue(Integer.class);
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

    public String getAnimal() {
        return animal;
    }

    public void setAnimal(String animal) {
        this.animal = animal;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
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
